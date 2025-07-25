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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Place



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
                },
                onOpenMap = {
                    navController.navigate("mapa")
                }
            )
        }
        composable("checkout") {
            CheckoutScreen(
                cartItems = cartItems,
                onBack = { navController.popBackStack() }
            )
        }
        composable("mapa") {
            MapaComFarmaciasScreen(navController)
        }
    }
}


data class Product(val name: String
                    , val imageRes: Int
                    ,  val pharmacy: String )// novo campo)

val productList = listOf(
    Product("Paracetamol 500mg", R.drawable.baseline_medication_24, "Droga Raia"),
    Product("Ibuprofeno 400mg", R.drawable.baseline_medication_24, "Drogaven"),
    Product("Dipirona 1g", R.drawable.baseline_medication_24, "Farmacia 24h"),
    Product("Vitamina C", R.drawable.baseline_medication_24, "Pharmacia"),
    Product("Antisséptico Bucal", R.drawable.baseline_medication_24, "Droga Raia")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddToCart: (Product) -> Unit,
    onGoToCart: () -> Unit,
    onOpenMap: () -> Unit
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmarket") },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onOpenMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Ver Mapa de Farmácias")
                }
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
            Text(text = product.pharmacy, fontSize = 14.sp, color = Color.Gray) // novo texto
        }
        Button(onClick = { onAddToCart(product) }) {
            Text("Adicionar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(cartItems: List<Product>, onBack: () -> Unit) {
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var orderPlaced by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voltar para as compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (orderPlaced) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pedido realizado com sucesso!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Itens no carrinho:", fontWeight = FontWeight.Bold)
                cartItems.forEach { item ->
                    Text("- ${item.name}")
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        if (errorMessage.isNotEmpty()) errorMessage = "" // limpa erro se usuário digitar
                    },
                    label = { Text("Endereço de entrega") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = {
                        paymentMethod = it
                        if (errorMessage.isNotEmpty()) errorMessage = ""
                    },
                    label = { Text("Forma de pagamento (Dinheiro, Cartão, Pix)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (address.isBlank() || paymentMethod.isBlank()) {
                            errorMessage = "Por favor, preencha todos os campos."
                        } else {
                            orderPlaced = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar Pedido")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaComFarmaciasScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Farmácias") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(R.drawable.mapa_farmacias),
                contentDescription = "Mapa de farmácias",
                modifier = Modifier.fillMaxSize()
            )

            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Farmácia A",
                tint = Color.Red,
                modifier = Modifier
                    .offset(x = 100.dp, y = 200.dp)
                    .size(32.dp)
            )
        }
    }
}


