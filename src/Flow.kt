import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

fun main() {
    flatMapLatest()
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun flatMapConcat() {
    runBlocking {
        DataSource.getColorsFlow()
            .flatMapConcat { color -> DataSource.getDetailColor(color) }
            .collect { detail ->
                println(detail)
            }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
private fun flatMapMerge() {
    runBlocking {
        DataSource.getColorsFlow()
            .flatMapMerge { color -> DataSource.getDetailColor(color) }
            .collect { detail ->
                println(detail)
            }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun flatMapLatest() {
    runBlocking {
        DataSource.getColorsFlow()
            .flatMapLatest { color -> DataSource.getDetailColor(color) }
            .collect { detail ->
                println(detail)
            }
    }
}


/**
 * The `reduce` operator combines all the elements emitted by the flow into a single value
 * using a specified accumulator function.
 */
private fun reduceOperator() {
    runBlocking {
        println("---------------  REDUCE  -----------------")
        val colors = DataSource.getColorsFlow()
            .map { it.name }
            .reduce { acc, color ->
            "$acc + $color"
        }
        println(colors)
    }
}

/**
 * The `fold` operator combines all the elements emitted by the flow into a single value
 * using a specified accumulator function but with an initial values.
 */
private fun foldOperator() {
    runBlocking {
        println("---------------  FOLD  -----------------")
        val colors = DataSource.getColorsFlow()
            .map { it.name }
            .fold("Valor inicial: ") { acc, color ->
                "$acc + $color"
            }
        println(colors)
    }
}

private fun singleOperator() {
    runBlocking {
        println("---------------  SINGLE  -----------------")
        val color = DataSource.getColorsFlow()
            .filter { it.name.contains("Blue") }
            .single()
        println(color)
    }
}

private fun mapOperator() {
    runBlocking {
        println("---------------  MAP  -----------------")
        DataSource.getColorsFlow().map {
            it.name.capitalize()
        }.collect { color ->
            println(color)
        }
    }
}

private fun filterOperator() {
    runBlocking {
        println("---------------  FILTER  -----------------")
        DataSource.getColorsFlow().filter {
            it.isPrimary
        }.collect { color ->
            println(color)
        }
    }
}

private fun filterNotOperator() {
    runBlocking {
        println("---------------  FILTER NOT  -----------------")
        DataSource.getColorsFlow().filterNot {
            it.isPrimary
        }.collect { color ->
            println(color)
        }
    }
}

/**
 * Tiene un metodo emit, para enviar el objeto transformado,
 * la combinacion entre el map y el filter
 */
private fun transformOperator() {
    runBlocking {
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
            delay(200)
            emit(color)
        }
    }

    fun getDetailColor(color: Color): Flow<String> = flow {
        emit("--------------------------------")
        emit("ID: ${color.id}")
        delay(200)
        emit("Name: ${color.name}")
        delay(200)
        emit("Hex: ${color.hexadecimal}")
        delay(200)
        emit("Is primary: ${color.isPrimary}")
        delay(200)
    }
}

data class Color(
    val id: Int,
    val name: String,
    val hexadecimal: String,
    val isPrimary: Boolean,
    val rgb: String
)
