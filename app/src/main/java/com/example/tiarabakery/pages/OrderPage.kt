package com.example.tiarabakery.pages

import OrderViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tiarabakery.R
import com.example.tiarabakery.model.OrderModel
import com.example.tiarabakery.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderPage(
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel = viewModel()
) {
    val orders by orderViewModel.orders.collectAsState()
    val productsMap by orderViewModel.productsMap.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.cream))
            .padding(horizontal = 16.dp)  // Hanya padding horizontal saja
            .padding(bottom = 120.dp)
    ) {
        Text(
            text = "Your Order",
            style = TextStyle(
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.catamaran_medium)),
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(top = 16.dp, start = 8.dp, bottom = 8.dp)  // Mengurangi bottom padding
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)  // Padding horizontal konsisten
        ) {
            items(orders) { order ->
                val itemList = order.items.mapNotNull { (productId, quantity) ->
                    val product = productsMap[productId]
                    product?.let { it to quantity }
                }

                OrderCard(order = order, items = itemList)
                Spacer(modifier = Modifier.height(8.dp))  // Jarak antar card
            }
        }
    }
}



@Composable
fun OrderCard(order: OrderModel, items: List<Pair<ProductModel, Long>>) {
    val dateFormat = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(order.createdAt.toDate())

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorResource(id = R.color.light_brown)) // Changed to cream background
            .fillMaxWidth()
    ) {
        // Bagian atas: metadata order - now in vertical layout
        Column(
            Modifier
                .background(colorResource(id = R.color.brown)) // Changed to brown
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            // Order ID - full version
            Column(Modifier.padding(bottom = 8.dp)) {
                Text(
                    "Order ID",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    order.id,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Date
            Column(Modifier.padding(bottom = 8.dp)) {
                Text(
                    "Date",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    formattedDate,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Total
            Column(Modifier.padding(bottom = 8.dp)) {
                Text(
                    "Total",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    "Rp ${order.total}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            // Status
            Column {
                Text(
                    "Status",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    order.status,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        // List item dalam order
        items.forEach { (product, qty) ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(product.title, fontWeight = FontWeight.Bold)
                    Text("x$qty", style = MaterialTheme.typography.bodySmall)
                }

                val priceLong = product.actualPrice.toLongOrNull() ?: 0L
                val totalPrice = priceLong * qty
                Text("Rp $totalPrice")
            }
        }
    }
}