package pe.edu.upeu.proyecto

import java.math.BigDecimal

data class Muebles(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val precio: BigDecimal,
    val stock: Int,
    val dimensiones: String,
    val color: String,
    val material: String,
)
