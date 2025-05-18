package com.example.practica04_tsp.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica04_tsp.model.Product
import com.example.practica04_tsp.network.Api
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.UUID

class ApiViewModel : ViewModel() {
    var apiUiState: ApiUiState by mutableStateOf(ApiUiState.Loading)
    private val products = mutableStateListOf<Product>()

    private val apiKey = "AIzaSyA9WJ3eBBq5Uo94ZT_Y66DtrIQGAMAgiPg"
    private val cx = "068de69f3c5a743b0"

    private val db = Firebase.firestore
    private val productsCollection = db.collection("products")

    private val pendingProducts = mutableStateListOf<Product>()
    private val pendingDeletes = mutableStateListOf<String>()

    var hasPendingChanges by mutableStateOf(false)
        private set

    init {
        productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                apiUiState = ApiUiState.Error("Error cargando datos")
                return@addSnapshotListener
            }
            if (!hasPendingChanges) {
                val firestoreProducts = snapshot?.documents?.mapNotNull {
                    it.toObject(Product::class.java)
                } ?: emptyList()
                products.clear()
                products.addAll(firestoreProducts)
                apiUiState = ApiUiState.Success(products.toList())
            }
        }
    }

    fun addProduct(name: String, budget: Double, priority: Int) {
        viewModelScope.launch {
            apiUiState = ApiUiState.Loading
            try {
                val imageUrl = getProductImage(name)
                val newProduct = Product(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    budget = budget,
                    priority = priority,
                    imageUrl = imageUrl
                )

                pendingProducts.add(newProduct)
                hasPendingChanges = true
                updateLocalState()

            } catch (e: Exception) {
                apiUiState = ApiUiState.Error(e.message ?: "Error al agregar producto")
            }
        }
    }

    fun deleteProduct(productId: String) {
        pendingDeletes.add(productId)
        products.removeAll { it.id == productId }
        hasPendingChanges = true
        updateLocalState()
    }

    fun saveChanges() {
        viewModelScope.launch {
            apiUiState = ApiUiState.Loading
            try {
                val savedProducts = pendingProducts.toList()
                val savedDeletes = pendingDeletes.toList()

                products.removeAll { savedDeletes.contains(it.id) }
                products.addAll(savedProducts)
                apiUiState = ApiUiState.Success(products.toList())

                val batch = db.batch()
                savedProducts.forEach { product ->
                    batch.set(productsCollection.document(product.id), product)
                }
                savedDeletes.forEach { productId ->
                    batch.delete(productsCollection.document(productId))
                }
                batch.commit().await()

                pendingProducts.clear()
                pendingDeletes.clear()
                hasPendingChanges = false

            } catch (e: Exception) {
                apiUiState = ApiUiState.Error("Error al guardar: ${e.message}")
                hasPendingChanges = true
            }
        }
    }

    private fun updateLocalState() {
        val visibleProducts = (products + pendingProducts)
            .filterNot { pendingDeletes.contains(it.id) }
            .distinctBy { it.id }

        apiUiState = ApiUiState.Success(visibleProducts)
    }

    private suspend fun getProductImage(query: String): String {
        return try {
            val encodedQuery = withContext(Dispatchers.IO) {
                URLEncoder.encode(query, "UTF-8")
            }

            val result = Api.retrofitService.searchImages(
                apiKey = apiKey,
                cx = cx,
                query = encodedQuery, // Usar la versión codificada
            )

            result.items?.firstOrNull()?.link ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}

sealed interface ApiUiState {
    data object Loading : ApiUiState
    data class Success(val products: List<Product>) : ApiUiState
    data class Error(val message: String) : ApiUiState
}