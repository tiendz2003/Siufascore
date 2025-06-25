package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jerry.ronaldo.siufascore.presentation.auth.LoginScreen
import com.jerry.ronaldo.siufascore.presentation.auth.SignUpScreen
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute

fun NavController.navigateToLogin(navOptions: NavOptions) = navigate(route = AuthRoute, navOptions)
fun NavGraphBuilder.loginScreen(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSuccess:()->Unit
) {
    composable<AuthRoute> {
        LoginScreen(
            onSignInClick = onSignInClick,
            onSignUpClick = onSignUpClick,
            onSuccess = onSuccess
        )
    }

}
@Serializable
data object SignUpRoute

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) = navigate(route = SignUpRoute, navOptions)
fun NavGraphBuilder.signUpScreen(
    onSignUpClick: () -> Unit,
    onSuccess:()->Unit
) {
    composable<SignUpRoute> {
        SignUpScreen(
            onSignInClick = onSignUpClick,
            onSuccess = onSuccess
        )
    }

}