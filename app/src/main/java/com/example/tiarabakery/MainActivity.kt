package com.example.tiarabakery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tiarabakery.ui.theme.TiaraBakeryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiaraBakeryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

//    override fun OnPaymentSuccess(p0: String) {
//
//        AppUtil.clearCartAndAddToOrders()
//
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Payment Successful")
//            .setMessage("Thank you! Your payment was completed successfully and your order has been placed")
//            .setPositiveButton("OK"){_,_->
//                val navController = GlobalNavigation.navController
//                navController.popBackStack()
//                navController.navigate("home")
//            }
//            .setCancelable(false)
//            .show()
//    }
//
//    override fun OnPaymentError(p0: String) {
//        AppUtil.showToast(this, "Payment Failed")
//    }
}

