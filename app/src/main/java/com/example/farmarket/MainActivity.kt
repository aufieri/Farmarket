package com.example.farmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarmarketApp()
        }
    }
}

@Composable
fun FarmarketApp() {
    val navController = rememberNavController()
    var cartItems by remember { mutableStateOf(listOf<Product>()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onAddToCart = { product ->
                    cartItems = cartItems + product
                },
                onGoToCart = {
                    navController.navigate("checkout")
                }
            )
        }
        composable("checkout") {
            CheckoutScreen(cartItems)
        }
    }
}

data class Product(val name: String, val imageRes: Int)

val productList = listOf(
    Product("Paracetamol 500mg", android.R.drawable.ic_menu_info_details),
    Product("Ibuprofeno 400mg", android.R.drawable.ic_menu_info_details),
    Product("Dipirona 1g", android.R.drawable.ic_menu_info_details),
    Product("Vitamina C", android.R.drawable.ic_menu_info_details),
    Product("Antisséptico Bucal", android.R.drawable.ic_menu_info_details)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onAddToCart: (Product) -> Unit, onGoToCart: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmarket") },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_view),
                            contentDescription = "Carrinho"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(productList) { product ->
                ProductItem(product, onAddToCart)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onAddToCart: (Product) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(product.imageRes),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Button(onClick = { onAddToCart(product) }) {
            Text("Adicionar")
        }
    }
}

@Composable
fun CheckoutScreen(cartItems: List<Product>) {
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var orderPlaced by remember { mutableStateOf(false) }

    if (orderPlaced) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Pedido realizado com sucesso!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Itens no carrinho:", fontWeight = FontWeight.Bold)
                cartItems.forEach { item ->
                    Text("- ${item.name}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Endereço de entrega") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = { paymentMethod = it },
                    label = { Text("Forma de pagamento (Dinheiro, Cartão, Pix)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { orderPlaced = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar Pedido")
                }
            }
        }
    }
}
