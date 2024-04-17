package cl.mferrer.android.proyectorestaurant.modelo

class CuentaMesa() {
    private val _items: MutableList<ItemMesa> = mutableListOf()

    fun agregarItem(itemMenu: ItemMenu, cantidad: Int) {
        val itemExistente = _items.find { it.itemMenu.nombre == itemMenu.nombre }
        if (itemExistente != null) {
            itemExistente.actualizarCantidad(cantidad)
        } else {
            _items.add(ItemMesa(itemMenu, cantidad))
        }
    }

    fun calcularTotalSinPropina(): Int {
        var total = 0
        _items.forEach { itemMesa ->
            total += itemMesa.calcularSubtotal()
        }
        return total
    }

    fun calcularPropina(): Int {
        return (calcularTotalSinPropina() * 0.1).toInt()
    }

    fun calcularTotalConPropina(): Int {
        val totalSinPropina = calcularTotalSinPropina()
        val propina = calcularPropina()
        return totalSinPropina + propina
    }
}
