package com.jerry.ronaldo.siufascore.presentation.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.designsystem.component.PermissionToggleCard
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleDark
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleLight
import com.jerry.ronaldo.siufascore.presentation.ui.rememberPermissionManager
import com.jerry.ronaldo.siufascore.utils.PermissionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val permissionManager = rememberPermissionManager()
    LaunchedEffect(viewModel.singleEvent) {
        viewModel.singleEvent.collect { event ->
            when (event) {
                is SettingEvent.SignoutSuccess -> {
                    onSignOut()
                }

                is SettingEvent.ShowError -> {

                }

                SettingEvent.UserInfoLoaded -> {

                }

                SettingEvent.UserInfoUpdated -> {

                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremierPurpleDark)
    ) {
        // Top Bar
        TopAppBar(
            title = {
                Text(
                    text = "Cài đặt",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackClick()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back",
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Handle help */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        tint = Color.White,
                        contentDescription = "Help",
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PremierPurpleDark
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Section
            item {
                UserProfileSection(
                    name = uiState.userInfo?.name ?: "Không rõ",
                    email = uiState.userInfo?.email ?: "",
                    profileImageUrl = uiState.userInfo?.profilePictureUrl
                        ?: "" // Replace with actual image URL
                )
            }

            // Menu Items
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MenuItemCard(
                        icon = Icons.Default.Person,
                        iconColor = Color(0xFFFF9500),
                        title = "Thông tin cá nhân",
                        onClick = { /* Handle click */ }
                    )


                    MenuItemCard(
                        icon = Icons.Default.Apps,
                        iconColor = Color(0xFFFF1493),
                        title = "Chung",
                        onClick = { /* Handle click */ }
                    )

                    MenuItemCard(
                        icon = Icons.Default.Security,
                        iconColor = Color(0xFF00CED1),
                        title = "Bảo mật",
                        onClick = { /* Handle click */ }
                    )

                    // Dark Mode Toggle
                    PermissionToggleCard(
                        permissionType = PermissionType.Notification,
                        permissionManager = permissionManager,
                        icon = Icons.Default.Notifications,
                        iconColor = Color(0xFF7B68EE),
                        onPermissionChanged = { isGranted ->
                            // Handle permission change in ViewModel
                            // viewModel.updateNotificationPermission(isGranted)
                        }
                    )

                    MenuItemCard(
                        icon = Icons.AutoMirrored.Filled.Help,
                        iconColor = Color(0xFFFF8C00),
                        title = "Trung tâm hỗ trợ ",
                        onClick = { /* Handle click */ }
                    )

                    MenuItemCard(
                        icon = Icons.Default.Info,
                        iconColor = Color(0xFF9370DB),
                        title = "Về Siufascore",
                        onClick = { /* Handle click */ }
                    )

                    // Logout Item
                    MenuItemCard(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        iconColor = Color(0xFFFF6B6B),
                        title = "Đăng xuất",
                        onClick = {
                            showLogoutDialog = true
                        }
                    )
                }
            }
        }
    }
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                viewModel.sendIntent(SettingIntent.Signout)
            },
        )
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(300, easing = EaseOutCubic)
        ),
        exit = fadeOut(
            animationSpec = tween(200, easing = EaseInCubic)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.5f)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    visible = false
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            val scale by animateFloatAsState(
                targetValue = if (visible) 1f else 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "scale"
            )

            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .scale(scale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Prevent dismiss when clicking on dialog */ },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = PremierPurpleDark,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Warning Icon with Animation
                    val iconScale by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium,
                        ),
                        label = "iconScale"
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(iconScale)
                            .background(
                                Color(0xFFFF6B6B).copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Red,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title with Animation
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(400, delayMillis = 200, easing = EaseOutCubic)
                        ) + fadeIn(
                            animationSpec = tween(400, delayMillis = 200)
                        )
                    ) {
                        Text(
                            text = "Đăng xuất",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                fontSize = 20.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Message with Animation
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 4 },
                            animationSpec = tween(400, delayMillis = 300, easing = EaseOutCubic)
                        ) + fadeIn(
                            animationSpec = tween(400, delayMillis = 300)
                        )
                    ) {
                        Text(
                            text = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản của mình không?",
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons with Animation
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(400, delayMillis = 400, easing = EaseOutCubic)
                        ) + fadeIn(
                            animationSpec = tween(400, delayMillis = 400)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cancel Button
                            OutlinedButton(
                                onClick = {
                                    visible = false
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(
                                        Color.White.copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Hủy",
                                    fontSize = 16.sp,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White
                                    ),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Confirm Button
                            Button(
                                onClick = {
                                    visible = false
                                    onConfirm()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Đăng xuất",
                                    fontSize = 16.sp,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White
                                    ),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserProfileSection(
    name: String,
    email: String,
    profileImageUrl: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PremierPurpleLight)
        ) {
            AsyncImage(
                model = profileImageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (profileImageUrl.isEmpty()) {
                Text(
                    text = "AA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 14.sp,
                    color = Color.Gray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Edit icon
        IconButton(
            onClick = { /* Handle edit profile */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MenuItemCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NotificationToggleCard(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                contentDescription = "Dark Mode",
                tint = Color(0xFF4285F4),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Thông báo",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PremierPurpleLight,
                    checkedTrackColor = Color.LightGray,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCCCCCC)
                )
            )
        }
    }
}
