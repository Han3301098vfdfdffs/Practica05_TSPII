package com.example.practica04_tsp.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practica04_tsp.R
import com.example.practica04_tsp.model.Product

@Composable
fun AddProductDialog(
    viewModel: ApiViewModel,
    onDismiss: () -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var priority by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Producto") },
        text = {
            Column {
                // Campo para nombre del producto
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para presupuesto
                OutlinedTextField(
                    value = budget,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) { // Solo números y decimales
                            budget = it
                        }
                    },
                    label = { Text("Presupuesto estimado") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    prefix = { Text("$") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de prioridad
                Text("Prioridad del producto:", style = MaterialTheme.typography.bodyMedium)
                StarRating(
                    rating = priority,
                    onRatingChanged = { priority = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budgetValue = budget.toDoubleOrNull() ?: 0.0
                    if (productName.isNotBlank() && budgetValue > 0) {
                        viewModel.addProduct(
                            name = productName,
                            budget = budgetValue,
                            priority = priority
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun StarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        for (i in 1..5) {
            IconButton(
                onClick = { onRatingChanged(i) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Estrella $i",
                    tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                    )
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Presupuesto con formato monetario
                Text(
                    text = "Presupuesto: $${"%.2f".format(product.budget)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Prioridad con etiqueta descriptiva
                Column {
                    Text(
                        text = "Prioridad:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    StarRating(
                        rating = product.priority,
                        onRatingChanged = {} // No editable aquí
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Eliminar producto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = products,
            key = { it.id }
        ) { product ->
            ProductCard(
                product = product,
                onDelete = { onDelete(product.id) }
            )
        }
    }
}