package com.example.tiarabakery

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.tiarabakery.model.OrderModel
import com.example.tiarabakery.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
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

    fun getUserData(userId: String, onSuccess: (UserModel) -> Unit, onFailure: (Exception) -> Unit) {
        Firebase.firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    user?.let { onSuccess(it) }
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateUserData(userId: String, updatedData: Map<String, Any>,
                       onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Firebase.firestore.collection("users").document(userId)
            .update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
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

    fun updatePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = Firebase.auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", currentPassword)

        user?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener {
                        onFailure("Gagal mengupdate password: ${it.message}")
                    }
            } else {
                onFailure("Password saat ini salah")
            }
        }
    }

    fun getDiscountPercentage() : Float{
        return 10.0f
    }

    fun getTaxPercentage() : Float{
        return 11.0f
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