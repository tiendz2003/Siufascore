package com.jerry.ronaldo.siufascore.data.source


import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jerry.ronaldo.siufascore.data.mapper.FacebookAuthMapper
import com.jerry.ronaldo.siufascore.utils.AuthException
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FacebookAuthDataSource @Inject constructor() {

    private var callbackManager: CallbackManager? = null

    fun initialize(): CallbackManager {
        callbackManager = CallbackManager.Factory.create()
        return callbackManager!!
    }

    suspend fun signInWithFacebook(activity: androidx.fragment.app.FragmentActivity): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            val loginManager = LoginManager.getInstance()

            loginManager.registerCallback(
                callbackManager!!,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        val accessToken = result.accessToken.token
                        continuation.resume(Result.success(accessToken))
                    }

                    override fun onCancel() {
                        continuation.resume(
                            Result.failure(AuthException.CancelledByUser)
                        )
                    }

                    override fun onError(error: FacebookException) {
                        continuation.resume(
                            Result.failure(FacebookAuthMapper.mapFacebookExceptionToDomain(error))
                        )
                    }
                }
            )

            loginManager.logInWithReadPermissions(
                activity,
                listOf("email", "public_profile")
            )

            continuation.invokeOnCancellation {
                loginManager.unregisterCallback(callbackManager!!)
            }
        }
    }

    fun handleActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: android.content.Intent?
    ) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            LoginManager.getInstance().logOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}