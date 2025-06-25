package com.jerry.ronaldo.siufascore.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.presentation.ui.PremierGray
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleDark
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleLight
import com.jerry.ronaldo.siufascore.presentation.ui.PremierWhite
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onSuccess: () -> Unit,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by signUpViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current  // Nếu cần cho social sign up

    LaunchedEffect(Unit) {
        signUpViewModel.singleEvent.collectLatest { event ->
            when (event) {
                is AuthUiEvent.ShowError -> {
                    // Handle show error (e.g., Snackbar)
                }
                is AuthUiEvent.ShowSuccess -> {
                    onSuccess()
                }
                AuthUiEvent.ShowEmailVerificationDialog -> {
                    // Handle email verification dialog
                }
                else -> {}
            }
        }
    }

    // Surface for full screen with background
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PremierPurpleDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding()
                .pointerInput(Unit) { /* Handle taps if needed */ },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.premier_league),
                contentDescription = "Premier League Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Sign up for myPremier League",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremierWhite
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { signUpViewModel.sendIntent(AuthIntent.UpdateEmail(it)) },
                label = { Text("Email", color = PremierGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremierWhite,
                    unfocusedBorderColor = PremierGray,
                    focusedLabelColor = PremierWhite,
                    unfocusedLabelColor = PremierGray,
                    cursorColor = PremierWhite,
                    focusedContainerColor = PremierPurpleLight,
                    unfocusedContainerColor = PremierPurpleLight
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                supportingText = {
                    if (!uiState.emailValidation.isValid) {
                        Text(
                            uiState.emailValidation.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { signUpViewModel.sendIntent(AuthIntent.UpdatePassword(it)) },
                label = { Text("Password", color = PremierGray) },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                trailingIcon = {
                    IconButton(onClick = { signUpViewModel.sendIntent(AuthIntent.TogglePasswordVisibility) }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = PremierGray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremierWhite,
                    unfocusedBorderColor = PremierGray,
                    focusedLabelColor = PremierWhite,
                    unfocusedLabelColor = PremierGray,
                    cursorColor = PremierWhite,
                    focusedContainerColor = PremierPurpleLight,
                    unfocusedContainerColor = PremierPurpleLight
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                supportingText = {
                    if (!uiState.passwordValidation.isValid) {
                        Text(
                            uiState.passwordValidation.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { signUpViewModel.sendIntent(AuthIntent.UpdateConfirmPassword(it)) },
                label = { Text("Confirm Password", color = PremierGray) },
                singleLine = true,
                visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { signUpViewModel.sendIntent(AuthIntent.ToggleConfirmPasswordVisibility) }) {
                        Icon(
                            imageVector = if (uiState.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle confirm password visibility",
                            tint = PremierGray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PremierWhite,
                    unfocusedBorderColor = PremierGray,
                    focusedLabelColor = PremierWhite,
                    unfocusedLabelColor = PremierGray,
                    cursorColor = PremierWhite,
                    focusedContainerColor = PremierPurpleLight,
                    unfocusedContainerColor = PremierPurpleLight
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                supportingText = {
                    if (!uiState.confirmPasswordValidation.isValid) {
                        Text(
                            uiState.confirmPasswordValidation.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = { signUpViewModel.sendIntent(AuthIntent.SignUpWithEmail) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremierWhite,
                    contentColor = PremierPurpleDark
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PremierPurpleDark
                    )
                } else {
                    Text("Đăng ký", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Or divider
            Text(
                text = "hoặc",
                color = PremierGray,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Social Sign Up Buttons (giữ tương tự login, adjust nếu cần)
            SocialLoginButton(
                iconRes = R.drawable.premier_league,  // Giả định icons cho Google
                text = "Đăng ký với Google",
                onClick = {
                    // Handle Google sign up (nếu hỗ trợ, gọi tương ứng trong ViewModel)
                    // signUpViewModel.onAction(AuthUiAction.SignUpWithGoogle(context = activity))
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SocialLoginButton(
                iconRes = R.drawable.premier_league,  // Giả định icons cho Facebook
                text = "Đăng ký với Facebook",
                onClick = { /* Handle FB sign up */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign In Link
            Text(
                text = "Đã có tài khoản? Đăng nhập ngay",
                color = PremierWhite,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}