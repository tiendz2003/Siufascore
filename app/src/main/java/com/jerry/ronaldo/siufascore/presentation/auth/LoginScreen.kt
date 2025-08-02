package com.jerry.ronaldo.siufascore.presentation.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun LoginScreen(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSuccess: () -> Unit,
    loginViewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val activity = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        loginViewModel.singleEvent.collectLatest { event ->
            when (event) {
                is AuthUiEvent.ShowError -> {

                }

                is AuthUiEvent.ShowSuccess -> {

                }

                AuthUiEvent.NavigateToHome -> {
                    onSuccess()
                }

                AuthUiEvent.ShowEmailVerificationDialog -> {

                }

                AuthUiEvent.NavigateToSignIn -> {

                }

                AuthUiEvent.NavigateToSignUp -> {
                    onSignUpClick()
                }

                is AuthUiEvent.ShowSnackbar -> {

                }
            }
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = PremierPurpleDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Icon(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "Premier League Logo",
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)  // Nếu logo tròn, иначе remove
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Đăng nhập với Siufascore",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremierWhite
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
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
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = PremierGray) },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,  // Giả định icons
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

            Spacer(modifier = Modifier.height(24.dp))

            // Sign In Button
            Button(
                onClick = { /* Handle sign in */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremierWhite,
                    contentColor = PremierPurpleDark
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = PremierPurpleDark
                    )
                } else {
                    Text(
                        "Đăng nhập",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 16.sp,
                            color = PremierPurpleDark
                        ),
                        fontWeight = FontWeight.Bold
                    )
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

            // Social Login Buttons
            SocialLoginButton(
                iconRes = R.drawable.ic_gg,  // Giả định icons
                text = "Đăng nhập với Google",
                onClick = {
                    loginViewModel.sendIntent(
                        AuthIntent.SignInWithGoogle(
                            context = activity
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SocialLoginButton(
                iconRes = R.drawable.ic_fb,
                text = "Đăng nhập với Facebook",
                onClick = { /* Handle FB */ }
            )

            Spacer(modifier = Modifier.height(8.dp))


            Spacer(modifier = Modifier.height(8.dp))


            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "Không có tài khoản? Đăng ký ngay",
                color = PremierWhite,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        loginViewModel.sendIntent(AuthIntent.NavigateToSignUp)
                    }
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PremierPurpleLight,
            contentColor = PremierWhite
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text, style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                ), fontWeight = FontWeight.Medium
            )
        }
    }
}
