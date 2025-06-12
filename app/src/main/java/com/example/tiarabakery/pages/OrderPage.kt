package com.example.tiarabakery.pages

import OrderViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(orders) { order ->
            val itemList = order.items.mapNotNull { (productId, quantity) ->
                val product = productsMap[productId]
                product?.let { it to quantity }
            }

            OrderCard(order = order, items = itemList)
        }
    }
}



@Composable
fun OrderCard(order: OrderModel, items: List<Pair<ProductModel, Long>>) {
    val dateFormat = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val formattedDate = dateFormat.format(order.createdAt.toDate())
    val shortId = if (order.id.length >= 4) order.id.substring(0, 4) else order.id

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF0D5))
            .fillMaxWidth()
    ) {
        // Bagian atas: metadata order
        Row(
            Modifier
                .background(Color(0xFF6A4000))
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Order id: ", fontWeight = FontWeight.Bold, color = Color.White)
                Text(shortId, color = Color.White)
            }
            Column { Text("Date", fontWeight = FontWeight.Bold, color = Color.White); Text(formattedDate, color = Color.White) }
            Column { Text("Total", fontWeight = FontWeight.Bold, color = Color.White); Text("Rp ${order.total}", color = Color.White) }
            Column { Text("Status", fontWeight = FontWeight.Bold, color = Color.White); Text(order.status, color = Color.White) }
        }

        // List item dalam order
        items.forEach { (product, qty) ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
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
                Text("Rp ${product.price}")
            }
        }
    }
}

