import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue

fun main() {
    val lockAndUnlock = LockAndUnlock()
    lockAndUnlock.startProducerAndConsumer()
}

class LockAndUnlock {

    private val queue = ConcurrentLinkedQueue<Notes>() // Cola que esta optimizada para trabajar con coroutinas
    private var produced = 0
    private var consumed = 0
    private var isFinished = false
    private val batch = IntArray(5) // Lotes para correr las coroutinas

    private val mutex = Mutex()

    fun startProducerAndConsumer() {
        runBlocking {
            // Producers
            launch(Dispatchers.Default) {
                val producers = List(100_000) {
                    launch {
                        val note = NotesUtils.getNote()
                        if (queue.offer(note)) {
                            mutex.withLock {
                                produced ++
                            }
                        }
                    }
                }
                producers.joinAll()
                isFinished = true
                println("Producers Done")
            }

            // Consumers
            launch(Dispatchers.Default) {
                val consumers = List(batch.size) {
                    launch {
                        while (!isFinished || queue.isNotEmpty()) {
                            val note = queue.poll()
                            if (note != null) {
                                mutex.withLock {
                                    consumed ++
                                    batch[it] ++
                                }
                            }
                        }
                    }
                }
                consumers.joinAll()
                println("Consumers Done")
            }
        }

        // Verificando la cantidad de notas producidas y consumidas
        println("Queue size: ${queue.size}")
        println("Produced: $produced")
        println("Consumed: $consumed")

        var totalBatch = 0
        batch.forEachIndexed { index, amount ->
            totalBatch += amount
            println("Batch $index: $amount")
        }
        println("Total Batch: $totalBatch")
    }

}

object NotesUtils {
    private val notes = Notes.entries.toTypedArray()

    fun getNote(): Notes {
        val index = (0..<notes.size).random()
        return notes[index]
    }
}

enum class Notes {
    DO, RE, MI, FA, SOL, LA, SI
}