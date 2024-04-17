package cl.mferrer.android.proyectorestaurant.modelo

class ItemMesa(val itemMenu: ItemMenu, var cantidad: Int) {
    fun calcularSubtotal(): Int {
        return itemMenu.precio * cantidad
    }

    fun actualizarCantidad(cantidad: Int) {
        this.cantidad = cantidad
    }
}
