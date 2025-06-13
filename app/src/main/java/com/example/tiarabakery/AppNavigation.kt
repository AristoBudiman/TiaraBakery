package com.example.tiarabakery

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tiarabakery.pages.CategoryProductPage
import com.example.tiarabakery.pages.Checkoutpage
import com.example.tiarabakery.pages.ProductDetailsPage
import com.example.tiarabakery.pages.ProfilePage
import com.example.tiarabakery.screen.AuthScreen
import com.example.tiarabakery.screen.ForgotPasswordScreen
import com.example.tiarabakery.screen.HomeScreen
import com.example.tiarabakery.screen.LoginScreen
import com.example.tiarabakery.screen.SignupScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstPage = if(isLoggedIn) "home" else "auth"

    NavHost(navController= navController, startDestination= firstPage){
        composable("auth"){
            AuthScreen(modifier, navController)
        }
        composable("login"){
            LoginScreen(modifier, navController)
        }
        composable("signup"){
            SignupScreen(modifier, navController)
        }
        composable("home"){
            HomeScreen(modifier, navController)
        }
        composable("category-product/{categoryId}"){
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductPage(modifier,categoryId?:"")
        }
        composable("product-details/{productId}"){
            var productId = it.arguments?.getString("productId")
            ProductDetailsPage(modifier,productId?:"")
        }
        //tambahan
        composable("profilpage"){
            ProfilePage(modifier, navController)
        }
        composable("checkout"){
            Checkoutpage(modifier)
        }

        composable("forgot-password") {
            ForgotPasswordScreen(navController = navController)
        }
    }
}

object GlobalNavigation{
    lateinit var navController: NavController
}