package com.example.tiarabakery.pages

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tiarabakery.AppUtil
import com.example.tiarabakery.AppUtil.clearCartAndAddToOrders
import com.example.tiarabakery.GlobalNavigation
import com.example.tiarabakery.MidtransWebView
import com.example.tiarabakery.R
import com.example.tiarabakery.model.ProductModel
import com.example.tiarabakery.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


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

    val total = remember {
        mutableStateOf(0f)
    }

    val showWebView = remember { mutableStateOf(false) }
    val paymentUrl = remember { mutableStateOf("") }
    val orderId = remember { mutableStateOf("") }
    val context = LocalContext.current


    fun calculateAndAssign() {
        subTotal.value = 0f // Reset before calculation

        productList.forEach { product ->
            if (product.actualPrice.isNotEmpty()) {
                val normalizedPrice = product.actualPrice
                    .replace(",", ".")
                    .replace("[^\\d.]".toRegex(), "")
                val price = normalizedPrice.toFloatOrNull() ?: 0f
                val qty = userModel.value.cartItems[product.id] ?: 0
                subTotal.value += price * qty
            }
        }

        discount.value = subTotal.value * (AppUtil.getDiscountPercentage() / 100)
        tax.value = subTotal.value * (AppUtil.getTaxPercentage() / 100)
        total.value = subTotal.value - discount.value + tax.value
    }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect

        Firebase.firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val name = snapshot.getString("name") ?: ""
                        val email = snapshot.getString("email") ?: ""
                        val uid = snapshot.getString("uid") ?: ""
                        val phone = snapshot.getString("phone") ?: ""
                        val address = snapshot.getString("address") ?: ""
                        val role = snapshot.getString("role") ?: "user"
                        val cartMap = snapshot.get("cartItems") as? Map<String, Long> ?: emptyMap()

                        val user = UserModel(
                            name = name,
                            email = email,
                            uid = uid,
                            phone = phone,
                            address = address,
                            role = role,
                            cartItems = cartMap
                        )

                        userModel.value = user

                        Firebase.firestore.collection("data")
                            .document("stock").collection("products")
                            .whereIn("id", user.cartItems.keys.toList())
                            .get()
                            .addOnSuccessListener { task ->
                                productList.clear()
                                productList.addAll(task.toObjects(ProductModel::class.java))
                                calculateAndAssign()
                            }

                    } catch (e: Exception) {
                        Log.e("SnapshotParsing", "Error mapping snapshot to UserModel", e)
                    }
                }
            }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cream)),
        color = Color.Transparent // Pastikan Surface tidak menimpa background
    ){
        Column(
            modifier = modifier
                .background(colorResource(id = R.color.cream))
                .fillMaxSize()
                .padding(16.dp)
        ){
            Text(text = "Checkout", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Deliver to : ", fontWeight = FontWeight.SemiBold)
            Text(text = userModel.value.name)
            Text(text = userModel.value.address)
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            RowCheckoutItems(title = "Subtotal", value = subTotal.value.toString())
            Spacer(modifier = Modifier.height(16.dp))
            RowCheckoutItems(title = "Discount (-)", value = discount.value.toString())
            Spacer(modifier = Modifier.height(16.dp))
            RowCheckoutItems(title = "Tax (+)", value = tax.value.toString())
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "To Pay",
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Rp"+total.value.toString(),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            val isPayButtonEnabled = remember {
                derivedStateOf {
                    userModel.value.address.isNotEmpty() &&
                            userModel.value.phone.isNotEmpty()
                }
            }

            Button(
                onClick = {
                    val data = mapOf(
                        "total" to total.value.toLong(),
                        "email" to userModel.value.email
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val url = URL("https://midtrans-server-ujicoba-production.up.railway.app/create-transaction")
                            val connection = (url.openConnection() as HttpURLConnection).apply {
                                requestMethod = "POST"
                                doOutput = true
                                setRequestProperty("Content-Type", "application/json")
                            }

                            val body = JSONObject(data).toString()
                            connection.outputStream.use { os ->
                                os.write(body.toByteArray())
                                os.flush()
                            }

                            val responseText = connection.inputStream.bufferedReader().readText()
                            val json = JSONObject(responseText)

                            // Ambil token
                            val token = json.getString("token")
                            val order = json.getString("order_id")

                            // Bentuk redirect_url secara manual
                            val redirectUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/$token"

                            withContext(Dispatchers.Main) {
                                paymentUrl.value = redirectUrl
                                orderId.value = order
                                showWebView.value = true
                            }


                        } catch (e: Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                AppUtil.showToast(context, "Failed to start payment: ${e.message}")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.brown)
                ),
                enabled = isPayButtonEnabled.value
            ) {
                Text(text = "Pay Now")
            }


            if (!isPayButtonEnabled.value) {
                Text(
                    text = if (userModel.value.address.isEmpty() && userModel.value.phone.isEmpty()) {
                        "Please fill your address and phone number before payment"
                    } else if (userModel.value.address.isEmpty()) {
                        "Please fill your address before payment"
                    } else {
                        "Please fill your phone number before payment"
                    },
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if (showWebView.value) {
                Dialog(onDismissRequest = {
                    showWebView.value = false
                }) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        MidtransWebView(
                            url = paymentUrl.value,
                            onFinish = {
                                showWebView.value = false
                                AppUtil.clearCartAndAddToOrders(total.value.toLong())
                                GlobalNavigation.navController.navigate("home")
                            }
                        )
                    }
                }
            }
            LaunchedEffect(showWebView.value, orderId.value) {
                if (showWebView.value && orderId.value.isNotEmpty()) {
                    while (true) {
                        delay(3000) // setiap 3 detik
                        try {
                            val client = HttpClient(CIO)
                            val response: io.ktor.client.statement.HttpResponse = client.get("https://midtrans-server-ujicoba-production.up.railway.app/check-transaction-status/${orderId.value}")
                            val json = JSONObject(response.bodyAsText())
                            val status = json.getString("status")
                            if (status == "settlement" || status == "capture") {
                                showWebView.value = false
                                AppUtil.clearCartAndAddToOrders(total.value.toLong())
                                GlobalNavigation.navController.navigate("home")
                                break
                            }
                        } catch (e: Exception) {
                            Log.e("Polling", "Status check error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RowCheckoutItems(title: String,value: String){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = "Rp"+value, fontSize = 18.sp)
    }
}