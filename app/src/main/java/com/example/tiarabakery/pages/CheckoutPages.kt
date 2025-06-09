package com.example.tiarabakery.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tiarabakery.AppUtil
import com.example.tiarabakery.model.ProductModel
import com.example.tiarabakery.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

@Composable
fun Checkoutpage(modifier: Modifier = Modifier){

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    val productList = remember {
        mutableStateListOf(ProductModel())
    }

    val subTotal = remember {
        mutableStateOf(0f)
    }

    val discount = remember {
        mutableStateOf(0f)
    }

    val tax = remember {
        mutableStateOf(0f)
    }

    fun calculateAndAssign(){
        productList.forEach {
            if (it.actualPrice.isNotEmpty()) {
                val qty = userModel.value.cartItems[it.id] ?: 0
                subTotal.value += it.actualPrice.toFloat() * qty
            }
        }

        discount.value = subTotal.value * (AppUtil.getDiscountPercentage())/100;
        tax.value = subTotal.value * (AppUtil.getTaxPercentage())/100
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get().addOnCompleteListener{
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if(result!=null){
                        userModel.value = result

                        Firebase.firestore.collection("data")
                            .document("stock").collection("products")
                            .whereIn("id",userModel.value.cartItems.keys.toList() )
                            .get().addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    val resultProducts = task.result.toObjects(ProductModel::class.java)
                                    productList.addAll(resultProducts)
                                    calculateAndAssign()
                                }
                            }
                    }
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text(text = "Checkout", fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }

}