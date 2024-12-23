import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        println("---------------  MAP  -----------------")
        DataSource.getColorsFlow().map {
            it.name.capitalize()
        }.collect { color ->
            println(color)
        }

        println("---------------  FILTER  -----------------")
        DataSource.getColorsFlow().filter {
            it.isPrimary
        }.collect { color ->
            println(color)
        }

        println("---------------  FILTER NOT  -----------------")
        DataSource.getColorsFlow().filterNot {
            it.isPrimary
        }.collect { color ->
            println(color)
        }

        // Tiene un metodo emit, para enviar el objeto transformado
        // al flujo original, sin cambiarlo.
        // Este metodo puede ser util cuando se quiere mantener el estado
        // de los objetos en el flujo original.
        // Combinacion del map y el filter
        println("---------------  TRANSFORM  -----------------")
        DataSource.getColorsFlow().transform {
            if (it.isPrimary) {
                emit("${it.name} - No es primario")
            }
        }.collect { color ->
            println(color)
        }
    }
}

object DataSource {
    private fun getListColors(): List<Color> {
        return listOf(
            Color(1, "Blue", "#0000FF", true, "0.0.255"),
            Color(2, "green", "#00FF00", true, "0.255.0"),
            Color(3, "red", "#FF0000", true, "255.0.0"),
            Color(4, "yellow", "#FFFF00", false, "255.255.0"),
            Color(5, "purple", "#00FFFF", false, "0.255.255"),
            Color(6, "orange", "#FF00FF", false, "255.0.255")
        )
    }

    fun getColorsFlow(): Flow<Color> = flow {
        getListColors().forEach { color ->
            delay(600)
            emit(color)
        }
    }
}

data class Color(
    val id: Int,
    val name: String,
    val hexadecimal: String,
    val isPrimary: Boolean,
    val rgb: String
)
