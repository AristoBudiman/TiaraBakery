package com.example.tiarabakery.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tiarabakery.AppUtil
import com.example.tiarabakery.R
import com.example.tiarabakery.viewmodel.AuthViewModel

@Composable
fun SignupScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel = viewModel()) {

    var email by remember {
        mutableStateOf("")
    }
    var name by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var isLoading by remember {
        mutableStateOf(false)
    }

    var context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cream))
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.tiara_vector),
            contentDescription = "LogoTiara",
            modifier = Modifier.fillMaxWidth()
                .height(300.dp)
        )

        Text(
            text = "Create an account",
            style = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full name") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password ...") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                isLoading = true
                authViewModel.signup(name, email, password){success, errorMessage->
                    if(success){
                        isLoading = false
                        navController.navigate("home"){
                            popUpTo("auth"){inclusive = true}
                        }
                    } else {
                        isLoading = false
                        AppUtil.showToast(context, errorMessage?:"Something went wrong")
                    }

                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.brown)
            )
        ) {
            Text(
                text = if(isLoading)"Create an account" else "Signup",
                fontSize = 22.sp,
                fontFamily = FontFamily(Font(R.font.catamaran_medium))
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(
                text = "Already have an account? Login",
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                fontSize = 16.sp,
                color = colorResource(id = R.color.brown)
            )
        }



    }
}