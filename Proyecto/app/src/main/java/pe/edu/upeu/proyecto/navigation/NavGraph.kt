package pe.edu.upeu.proyecto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pe.edu.upeu.proyecto.FormScreen
import pe.edu.upeu.proyecto.MuebleListScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "list_screen") {
        composable("list_screen") {
            MuebleListScreen(navController)
        }
        composable("form_screen/{ofertaId}") { backStackEntry ->
            val muebleId = backStackEntry.arguments?.getString("muebleId")?.toLongOrNull()
            FormScreen(navController = navController, muebleId = muebleId)
        }
        composable("form_screen") {
            FormScreen(navController)
        }
    }
}
