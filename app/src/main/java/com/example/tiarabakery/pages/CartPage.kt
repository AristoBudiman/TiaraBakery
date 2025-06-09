package com.example.tiarabakery.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiarabakery.AppUtil
import com.example.tiarabakery.GlobalNavigation
import com.example.tiarabakery.R
import com.example.tiarabakery.components.CartItemView
import com.example.tiarabakery.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun CartPage(modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    DisposableEffect (key1 = Unit){
        var listener = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener {it, _ ->
                if(it!=null){
                    val result = it.toObject(UserModel::class.java)
                    if(result!= null){
                        userModel.value = result
                    }
                }
            }
        onDispose {
            listener.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cream))
            .padding(16.dp),
    ) {
        Text(
            text = "Your Cart",
            style = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                fontWeight = FontWeight.Bold,
            )
        )

        LazyColumn (
//            modifier = Modifier.weight(1f)
        ){
            items(userModel.value.cartItems.toList(), key = {it.first}){(productId,qty)->
                CartItemView(productId = productId, qty = qty)
            }
        }

        Button(
            onClick = {
                GlobalNavigation.navController.navigate("checkout")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(text = "Checkout")
        }
    }
}