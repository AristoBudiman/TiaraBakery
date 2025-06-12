import androidx.lifecycle.ViewModel
import com.example.tiarabakery.model.OrderModel
import com.example.tiarabakery.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OrderViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _orders = MutableStateFlow<List<OrderModel>>(emptyList())
    val orders: StateFlow<List<OrderModel>> = _orders

    private val _productsMap = MutableStateFlow<Map<String, ProductModel>>(emptyMap())
    val productsMap: StateFlow<Map<String, ProductModel>> = _productsMap

    init {
        loadOrders()
        loadProducts()
    }

    private fun loadOrders() {
        db.collection("orders")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val orderList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(OrderModel::class.java)?.copy(id = doc.id)
                }

                _orders.value = orderList
            }
    }

    private fun loadProducts() {
        Firebase.firestore
            .collection("data")
            .document("stock")
            .collection("products")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                val productList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
                }

                _productsMap.value = productList.associateBy { it.id }
            }
    }
}



