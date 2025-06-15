package com.example.tiarabakery.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.tiarabakery.R
import com.example.tiarabakery.components.ProductItemView
import com.example.tiarabakery.model.CategoryModel
import com.example.tiarabakery.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoryProductPage(modifier: Modifier = Modifier, categoryId:String) {
    val productsList = remember {
        mutableStateOf<List<ProductModel>>(emptyList())
    }

    DisposableEffect(Unit) {
        val listenerRegistration = Firebase.firestore.collection("data")
            .document("stock")
            .collection("products")
            .whereEqualTo("category", categoryId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val filteredList = snapshot.documents.mapNotNull { doc ->
                        if (!doc.contains("status")) {
                            doc.toObject(ProductModel::class.java)
                        } else {
                            null
                        }
                    }
                    productsList.value = filteredList
                } else {
                    productsList.value = emptyList()
                }
            }
        onDispose {
            listenerRegistration.remove()
        }
    }


    Box (
        modifier = Modifier.fillMaxSize()
            .background(colorResource(id = R.color.cream))
            .padding(top = 48.dp)
    ){
        LazyColumn (
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
        ){
            items(productsList.value.chunked(2)){rowItem->
                Row {
                    rowItem.forEach{
                        ProductItemView(product = it, modifier = Modifier.weight(1f))
                    }
                    if(rowItem.size==1){
                        Spacer(modifier =Modifier.weight(1f))
                    }
                }
            }
        }
    }
}