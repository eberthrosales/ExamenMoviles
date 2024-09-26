package pe.edu.upeu.proyecto

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.edu.upeu.proyecto.navigation.NavGraph
import pe.edu.upeu.proyecto.ui.theme.ProyectoTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun MuebleListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    val coroutineScope = rememberCoroutineScope()
    var muebles by remember { mutableStateOf(listOf<Muebles>()) }

    // Lanzamos una coroutine para cargar los datos de la API
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.getAllMuebles().execute()
            }
            if (response.isSuccessful) {
                muebles = response.body() ?: listOf()
            } else {
                Toast.makeText(context, "Error al cargar los muebles: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error de red: ${e.message ?: "desconocido"}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Lista de Muebles",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = {
            navController.navigate("form_screen")
        }) {
            Text("Añadir Mueble")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        // Listado
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(muebles) { mueble ->
                MuebleItem(
                    mueble = mueble,
                    onEditClick = {
                        navController.navigate("form_screen/${mueble.id}")
                    },
                    onDeleteClick = {
                        coroutineScope.launch {
                            eliminarMueble(mueble.id, apiService, context) {
                                muebles = muebles.filter { it.id != mueble.id }
                            }
                        }
                    }
                )
                Divider()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

suspend fun eliminarMueble(id: Long, apiService: ApiService, context: Context, onSuccess: () -> Unit) {
    try {
        val response = withContext(Dispatchers.IO) {
            apiService.deleteMuebles(id).execute()
        }
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Mueble eliminado con éxito", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al eliminar el mueble", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}

@Composable
fun MuebleItem(mueble: Muebles, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = mueble.nombre, style = MaterialTheme.typography.titleLarge)
        Text(text = mueble.descripcion)
        Text(text = "Precio: ${mueble.precio}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onEditClick) {
                Text("Editar")
            }
            Button(onClick = onDeleteClick) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
fun FormScreen(navController: NavHostController, muebleId: Long? = null) {
    // Variables de estado para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var dimensiones by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }

    val context = LocalContext.current
    val apiService = RetrofitClient.apiService
    val coroutineScope = rememberCoroutineScope()

    // Cargar los datos del mueble si se está editando
    LaunchedEffect(muebleId) {
        if (muebleId != null) {
            val response = withContext(Dispatchers.IO) {
                apiService.getMueblesById(muebleId).execute()
            }
            if (response.isSuccessful) {
                response.body()?.let { mueble ->
                    nombre = mueble.nombre
                    descripcion = mueble.descripcion
                    tipo = mueble.tipo
                    precio = mueble.precio.toString()
                    stock = mueble.stock.toString()
                    dimensiones = mueble.dimensiones
                    color = mueble.color
                    material = mueble.material
                }
            } else {
                Toast.makeText(context, "Error al cargar el mueble", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (muebleId == null) "Formulario para Nuevo Mueble" else "Editar Mueble",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo de Mueble") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (en USD)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = dimensiones,
            onValueChange = { dimensiones = it },
            label = { Text("Dimensiones") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = material,
            onValueChange = { material = it },
            label = { Text("Material") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botones en una fila (Row)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                coroutineScope.launch {
                    val nuevoMueble = Muebles(
                        id = muebleId ?: 0L, // Si es null, se crea un nuevo mueble
                        nombre = nombre,
                        descripcion = descripcion,
                        tipo = tipo,
                        precio = BigDecimal(precio.toDoubleOrNull() ?: 0.0),
                        stock = stock.toIntOrNull() ?: 1,
                        dimensiones = dimensiones,
                        color = color,
                        material = material
                    )

                    if (muebleId != null) {
                        // Actualizar mueble existente
                        apiService.updateMuebles(muebleId, nuevoMueble).enqueue(object : Callback<Muebles> {
                            override fun onResponse(call: Call<Muebles>, response: Response<Muebles>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Mueble actualizado con éxito", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack() // Regresa a la pantalla anterior
                                } else {
                                    Toast.makeText(context, "Error al actualizar el mueble", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Muebles>, t: Throwable) {
                                Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        // Crear nuevo mueble
                        apiService.createMuebles(nuevoMueble).enqueue(object : Callback<Muebles> {
                            override fun onResponse(call: Call<Muebles>, response: Response<Muebles>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Mueble guardado con éxito", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack() // Regresa a la pantalla anterior
                                } else {
                                    Toast.makeText(context, "Error al guardar el mueble", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Muebles>, t: Throwable) {
                                Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }) {
                Text("Guardar Mueble")
            }

            Button(onClick = {
                navController.navigate("list_screen")
            }) {
                Text("Salir")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProyectoTheme {
        val navController = rememberNavController()
        NavGraph(navController = navController)
    }
}