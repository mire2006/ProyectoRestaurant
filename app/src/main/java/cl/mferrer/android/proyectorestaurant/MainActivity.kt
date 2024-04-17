package cl.mferrer.android.proyectorestaurant

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import cl.mferrer.android.proyectorestaurant.modelo.CuentaMesa
import cl.mferrer.android.proyectorestaurant.modelo.ItemMenu
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val menuCazuela = ItemMenu("Cazuela", 12000)
    private val menuChoclo = ItemMenu("Pastel de Choclo", 10000)
    private lateinit var cuentaMesa: CuentaMesa

    private lateinit var noCazuela: EditText
    private lateinit var noChoclo: EditText
    private lateinit var subtotalCazuela: EditText
    private lateinit var subtotalChoclo: EditText
    private lateinit var totalComida: EditText
    private lateinit var propinaSwitch: Switch
    private lateinit var totalPropina: EditText
    private lateinit var totalRestaurant: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noCazuela = findViewById(R.id.noCazuela)
        noChoclo = findViewById(R.id.noChoclo)
        subtotalCazuela = findViewById(R.id.subtotalCazuela)
        subtotalChoclo = findViewById(R.id.SubtotalChoclo)
        totalComida = findViewById(R.id.subtotalComida)
        propinaSwitch = findViewById(R.id.propina)
        totalPropina = findViewById(R.id.totalPropina)
        totalRestaurant = findViewById(R.id.totalRestaurant)

        // Deshabilitamos la edición en los EditText de subtotales y totales
        subtotalCazuela.isFocusable = false
        subtotalCazuela.isClickable = false
        subtotalChoclo.isFocusable = false
        subtotalChoclo.isClickable = false
        totalComida.isFocusable = false
        totalComida.isClickable = false
        totalPropina.isFocusable = false
        totalPropina.isClickable = false
        totalRestaurant.isFocusable = false
        totalRestaurant.isClickable = false

        cuentaMesa = CuentaMesa()

        // Configurar listeners para los EditText de cantidad
        setupCantidadListeners()

        // Configurar listener para el switch de propina
        setupPropinaListener()

        // Calcular el subtotal y actualizar la interfaz
        calcularSubtotal()
    }

    // Configurar listeners para los EditText de cantidad
    private fun setupCantidadListeners() {
        noCazuela.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularSubtotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        noChoclo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularSubtotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Agregar un TextWatcher adicional para limpiar los campos de subtotal y total de comida
        noCazuela.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    subtotalCazuela.setText("") // Limpiar subtotal si el texto se borra
                    totalComida.setText("") // Limpiar total de comida si el texto se borra
                } else {
                    calcularTotalRestaurant() // Recalcular el total del restaurante
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        noChoclo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    subtotalChoclo.setText("") // Limpiar subtotal si el texto se borra
                    totalComida.setText("") // Limpiar total de comida si el texto se borra
                } else {
                    calcularTotalRestaurant() // Recalcular el total del restaurante
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Configurar listener para el switch de propina
    private fun setupPropinaListener() {
        propinaSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                calcularPropina()
            } else {
                totalPropina.setText("")
            }
            calcularTotalRestaurant() // Calcular el total del restaurante al cambiar el estado del switch
        }
    }

    // Calcular el subtotal y actualizar la interfaz
    private fun calcularSubtotal() {
        // Obtener cantidad de platos
        val cantidadCazuela = noCazuela.text.toString().toIntOrNull() ?: 0
        val cantidadChoclo = noChoclo.text.toString().toIntOrNull() ?: 0

        // Agregar los items al pedido
        cuentaMesa.agregarItem(menuCazuela, cantidadCazuela)
        cuentaMesa.agregarItem(menuChoclo, cantidadChoclo)

        // Mostrar subtotales en los EditText correspondientes
        subtotalCazuela.setText(formatoMoneda(menuCazuela.precio * cantidadCazuela))
        subtotalChoclo.setText(formatoMoneda(menuChoclo.precio * cantidadChoclo))

        // Calcular y mostrar total de la comida
        val totalCalculadoComida = cuentaMesa.calcularTotalSinPropina() // Cambiado de totalComida a totalCalculadoComida
        totalComida.setText(formatoMoneda(totalCalculadoComida)) // Cambiado de totalComida a totalCalculadoComida

        // Si ambos campos de cantidad están vacíos, limpiar los campos de subtotal y total de comida
        if (noCazuela.text.isNullOrBlank() && noChoclo.text.isNullOrBlank()) {
            subtotalCazuela.setText("")
            subtotalChoclo.setText("")
            totalComida.setText("")
        }

        // Si el switch de propina está activado, se calcula la propina
        if (propinaSwitch.isChecked) {
            calcularPropina()
        } else {
            totalPropina.setText("") // Limpiar el EditText de propina si el switch está desactivado
        }

        calcularTotalRestaurant() // Calcular el total del restaurante al cambiar la cantidad del pedido
    }

    // Calcular y mostrar el total de propina
    private fun calcularPropina() {
        val propina = cuentaMesa.calcularPropina()
        totalPropina.setText(formatoMoneda(propina))
    }

    // Calcular y mostrar el total del restaurante (comida + propina)
    private fun calcularTotalRestaurant() {
        val totalRestaurantValue = if (propinaSwitch.isChecked) {
            cuentaMesa.calcularTotalConPropina()
        } else {
            cuentaMesa.calcularTotalSinPropina()
        }
        totalRestaurant.setText(formatoMoneda(totalRestaurantValue))
    }

    // Formatear la moneda a pesos
    private fun formatoMoneda(valor: Int): String {
        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        return formato.format(valor)
    }
}
