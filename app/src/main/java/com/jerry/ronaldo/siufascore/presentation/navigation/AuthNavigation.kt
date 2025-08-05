package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.auth.LoginScreen
import com.jerry.ronaldo.siufascore.presentation.auth.SignUpScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoute:AppRoute

fun NavController.navigateToLogin(navOptions: NavOptions) = navigate(route = AuthRoute, navOptions)
fun NavGraphBuilder.loginScreen(
    transitionResolver: NavigationTransitionResolver,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSuccess:()->Unit
) {
    animComposable<AuthRoute>(transitionResolver) {
        LoginScreen(
            onSignInClick = onSignInClick,
            onSignUpClick = onSignUpClick,
            onSuccess = onSuccess
        )
    }

}
@Serializable
data object SignUpRoute:AppRoute

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) = navigate(route = SignUpRoute, navOptions)
fun NavGraphBuilder.signUpScreen(
    transitionResolver: NavigationTransitionResolver,
    onSignUpClick: () -> Unit,
    onSuccess:()->Unit
) {
    animComposable<SignUpRoute>(transitionResolver) {
        SignUpScreen(
            onSignInClick = onSignUpClick,
            onSuccess = onSuccess
        )
    }

}