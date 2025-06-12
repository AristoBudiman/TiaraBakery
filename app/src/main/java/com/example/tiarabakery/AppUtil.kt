package com.example.tiarabakery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.tiarabakery.model.OrderModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.midtrans.sdk.corekit.core.MidtransSDK
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

object AppUtil {

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    fun addToCart(context: Context, productId: String){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
        userDoc.get().addOnCompleteListener {
            if(it.isSuccessful){
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity + 1;

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            showToast(context, "Item added to the cart")
                        } else {
                            showToast(context, "Failed adding item to the cart")
                        }
                    }
            }
        }
    }
    fun removeFromCart(context: Context, productId: String, removeAll: Boolean = false){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
        userDoc.get().addOnCompleteListener {
            if(it.isSuccessful){
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity - 1;

                val updatedCart =
                    if (updatedQuantity<=0 || removeAll){
                        mapOf("cartItems.$productId" to FieldValue.delete())
                    }else{
                        mapOf("cartItems.$productId" to updatedQuantity)
                    }

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if(it.isSuccessful){
                            showToast(context, "Item removed from the cart")
                        } else {
                            showToast(context, "Failed removing item from the cart")
                        }
                    }
            }
        }
    }


    fun getDiscountPercentage() : Float{
        return 10.0f
    }

    fun getTaxPercentage() : Float{
        return 11.0f
    }

//  Non-fungsional
//    fun razorpaiApiKey() : String{
//        return ""
//    }

//    fun startPayment(amount : Float){
//        val checkout = Checkout()
//        checkout.setKeyID(razorpaiApiKey())
//
//        val options = JSONObject()
//        options.put("name", "Tiara Bakery")
//        options.put("description","")
//        options.put("amount",amount*100)
//        options.put("currency", "USD")
//
//        checkout.open(GlobalNavigation.navController.context as Activity,options)
//    }

    fun startPayment(amount: Float, email: String, activity: Activity) {
        val client = OkHttpClient()

        val jsonObject = JSONObject().apply {
            put("total", amount.toInt())
            put("email", email)
        }

        val requestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.2.2:3001/create-transaction") // URL backend kamu
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Gagal membuat transaksi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val snapUrl = try {
                    JSONObject(responseBody ?: "").getString("redirect_url")
                } catch (e: Exception) {
                    null
                }

                if (snapUrl != null) {
                    activity.runOnUiThread {
                        val intent = Intent(activity, MidtransWebViewActivity::class.java)
                        intent.putExtra("snap_url", snapUrl)
                        activity.startActivity(intent)
                    }
                } else {
                    activity.runOnUiThread {
                        Toast.makeText(activity, "Gagal mendapatkan Snap URL", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    fun clearCartAndAddToOrders(total: Long){
        val userDoc = Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userData = task.result

                val currentCart = userData.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val address = userData.getString("address") ?: ""
                val phone = userData.getString("phone") ?: ""
                val userEmail = userData.getString("email") ?: ""

                    val order = OrderModel(
                        id = "ORD_" + UUID.randomUUID().toString().replace("-", "").take(10).uppercase(),
                        userId = FirebaseAuth.getInstance().currentUser?.uid!!,
                        userEmail = userEmail,
                        phone = phone,
                        createdAt = Timestamp.now(),
                        updatedAt = Timestamp.now(),
                        items = currentCart,
                        status = "sedang diproses",
                        address = address,
                        total = total
                    )

                    Firebase.firestore.collection("orders")
                        .document(order.id).set(order)
                        .addOnCompleteListener { orderTask ->
                            if (orderTask.isSuccessful) {
                                userDoc.update("cartItems", FieldValue.delete())
                            }
                        }
            }
        }
    }
}
