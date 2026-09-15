package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.AccentRed
import com.example.ui.theme.TextSecondary

@Composable
fun AuthScreen(
  isDarkMode: Boolean,
  onLogin: (email: String, password: String) -> Unit,
  onSignUp: (name: String, email: String, password: String) -> Unit,
  onDemoLogin: () -> Unit
) {
  var isLoginMode by remember { mutableStateOf(true) }
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var rememberMe by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val backgroundColor = if (isDarkMode) Color(0xFF141416) else Color(0xFFF4F4F8)
  val cardBackground = if (isDarkMode) Color(0xFF1E1E22) else Color.White
  val borderColor = if (isDarkMode) Color(0x1FFFFFFF) else Color(0x12000000)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(backgroundColor)
      .testTag("auth_screen")
  ) {
    // Subtle ambient gradient background orb
    Box(
      modifier = Modifier
        .size(320.dp)
        .align(Alignment.TopCenter)
        .background(
          Brush.radialGradient(
            colors = listOf(
              AccentPurple.copy(alpha = if (isDarkMode) 0.18f else 0.12f),
              Color.Transparent
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. App Brand Logo & Name
      Box(
        modifier = Modifier
          .size(72.dp)
          .shadow(16.dp, RoundedCornerShape(22.dp), spotColor = AccentPurple.copy(alpha = 0.5f))
          .clip(RoundedCornerShape(22.dp))
          .background(
            Brush.linearGradient(
              listOf(Color(0xFF6C63FF), Color(0xFF4834D4))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Wallet,
          contentDescription = "FinanceFlow Logo",
          tint = Color.White,
          modifier = Modifier.size(38.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "FinanceFlow",
        fontSize = 30.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.5).sp,
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = if (isLoginMode) "Sign in to manage your budget & goals" else "Create your secure local wallet",
        fontSize = 14.sp,
        color = TextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(28.dp))

      // 2. Tab Switcher (Sign In vs Create Account)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(if (isDarkMode) Color(0xFF28282D) else Color(0xFFEAEAEE))
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isLoginMode) cardBackground else Color.Transparent)
            .clickable {
              isLoginMode = true
              errorMessage = null
            }
            .padding(vertical = 10.dp)
            .testTag("auth_tab_signin"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Sign In",
            fontSize = 14.sp,
            fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Medium,
            color = if (isLoginMode) MaterialTheme.colorScheme.onBackground else TextSecondary
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (!isLoginMode) cardBackground else Color.Transparent)
            .clickable {
              isLoginMode = false
              errorMessage = null
            }
            .padding(vertical = 10.dp)
            .testTag("auth_tab_signup"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Create Account",
            fontSize = 14.sp,
            fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Medium,
            color = if (!isLoginMode) MaterialTheme.colorScheme.onBackground else TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 3. Credentials Input Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Error notification banner
          AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            errorMessage?.let { error ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(AccentRed.copy(alpha = 0.12f))
                  .border(BorderStroke(1.dp, AccentRed.copy(alpha = 0.3f)), RoundedCornerShape(12.dp))
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = AccentRed,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = error,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium,
                  color = AccentRed
                )
              }
            }
          }

          // Full Name Field (Sign Up mode only)
          if (!isLoginMode) {
            Column {
              Text(
                text = "Full Name",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                placeholder = { Text("Alex Morgan", color = TextSecondary) },
                leadingIcon = {
                  Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary)
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = AccentPurple,
                  unfocusedBorderColor = borderColor,
                  focusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB),
                  unfocusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_name_input")
              )
            }
          }

          // Email Field
          Column {
            Text(
              text = "Email Address",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = email,
              onValueChange = { email = it; errorMessage = null },
              placeholder = { Text("alex.morgan@example.com", color = TextSecondary) },
              leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary)
              },
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
              ),
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB),
                unfocusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_email_input")
            )
          }

          // Password Field
          Column {
            Text(
              text = "Password",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = password,
              onValueChange = { password = it; errorMessage = null },
              placeholder = { Text("••••••••", color = TextSecondary) },
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
              },
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                    tint = TextSecondary
                  )
                }
              },
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next
              ),
              keyboardActions = KeyboardActions(
                onDone = {
                  performAuthSubmit(
                    isLoginMode = isLoginMode,
                    name = name,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    onError = { errorMessage = it },
                    onLogin = onLogin,
                    onSignUp = onSignUp
                  )
                }
              ),
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB),
                unfocusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_password_input")
            )
          }

          // Confirm Password Field (Sign Up mode only)
          if (!isLoginMode) {
            Column {
              Text(
                text = "Confirm Password",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))
              OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorMessage = null },
                placeholder = { Text("••••••••", color = TextSecondary) },
                leadingIcon = {
                  Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Password,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    performAuthSubmit(
                      isLoginMode = isLoginMode,
                      name = name,
                      email = email,
                      password = password,
                      confirmPassword = confirmPassword,
                      onError = { errorMessage = it },
                      onLogin = onLogin,
                      onSignUp = onSignUp
                    )
                  }
                ),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = AccentPurple,
                  unfocusedBorderColor = borderColor,
                  focusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB),
                  unfocusedContainerColor = if (isDarkMode) Color(0xFF28282D) else Color(0xFFF9F9FB)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_confirm_password_input")
              )
            }
          }

          // Remember Me & Forgot Password
          if (isLoginMode) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Switch(
                  checked = rememberMe,
                  onCheckedChange = { rememberMe = it },
                  colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentPurple
                  ),
                  modifier = Modifier.size(24.dp)
                )
                Text(
                  text = "Remember me",
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Text(
                text = "Forgot password?",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = AccentPurple,
                modifier = Modifier.clickable {
                  errorMessage = "Password reset instructions sent to your email."
                }
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Submit Primary Button
          Button(
            onClick = {
              performAuthSubmit(
                isLoginMode = isLoginMode,
                name = name,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                onError = { errorMessage = it },
                onLogin = onLogin,
                onSignUp = onSignUp
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("auth_submit_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
          ) {
            Text(
              text = if (isLoginMode) "Sign In to Wallet" else "Create Free Account",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 4. Quick Demo Login Button (One-tap for reviewer convenience)
      OutlinedButton(
        onClick = onDemoLogin,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("auth_demo_button"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
          containerColor = AccentPurple.copy(alpha = if (isDarkMode) 0.12f else 0.08f)
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("✨", fontSize = 16.sp)
          Text(
            text = "Quick Demo Access (Alex Morgan)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AccentPurple
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 5. Encrypted offline badge
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = null,
          tint = AccentGreen,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "Offline SQLite Room Database • Private & Encrypted",
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = TextSecondary
        )
      }
    }
  }
}

private fun performAuthSubmit(
  isLoginMode: Boolean,
  name: String,
  email: String,
  password: String,
  confirmPassword: String,
  onError: (String) -> Unit,
  onLogin: (String, String) -> Unit,
  onSignUp: (String, String, String) -> Unit
) {
  val cleanEmail = email.trim()
  if (cleanEmail.isBlank()) {
    onError("Please enter your email address.")
    return
  }
  if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
    onError("Please enter a valid email format (e.g. name@domain.com).")
    return
  }
  if (password.length < 4) {
    onError("Password must be at least 4 characters long.")
    return
  }

  if (isLoginMode) {
    onLogin(cleanEmail, password)
  } else {
    val cleanName = name.trim()
    if (cleanName.isBlank()) {
      onError("Please enter your full name.")
      return
    }
    if (password != confirmPassword) {
      onError("Passwords do not match. Please verify.")
      return
    }
    onSignUp(cleanName, cleanEmail, password)
  }
}
