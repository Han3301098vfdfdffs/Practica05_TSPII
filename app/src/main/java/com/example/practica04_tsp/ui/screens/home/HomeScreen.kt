package com.example.practica04_tsp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practica04_tsp.R

@Composable
fun HomeScreen() {
    val viewModel: ApiViewModel = viewModel()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón de guardado (aparece arriba del Add cuando hay cambios)
                if (viewModel.hasPendingChanges) {
                    FloatingActionButton(
                        onClick = { viewModel.saveChanges() },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Guardar cambios",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar producto",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = viewModel.apiUiState) {
                ApiUiState.Loading -> LoadingScreen()
                is ApiUiState.Error -> ErrorScreen(message = state.message)
                else -> {
                    // Verificar si hay productos
                    val products = when (state) {
                        is ApiUiState.Success -> state.products
                        else -> emptyList()
                    }

                    if (products.isEmpty()) {
                        EmptyScreen()
                    } else {
                        ProductList(
                            products = products,
                            onDelete = { productId ->
                                viewModel.deleteProduct(productId)
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AddProductDialog(
                viewModel = viewModel,
                onDismiss = { showDialog = false }
            )
        }
    }
}
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.loading),
                contentDescription = "Error",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.empty_box),
                contentDescription = "Despensa vacía",
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "Tu despensa está vacía",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Presiona el botón + para agregar productos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorScreen(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.error),
                contentDescription = "Error",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
