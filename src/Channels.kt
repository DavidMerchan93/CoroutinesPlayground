import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.measureTimeMillis

fun main() {
    val lockAndUnlock = LockAndUnlock()
    lockAndUnlock.startProducerAndConsumer()
}

class LockAndUnlock {

    //private val queue = ConcurrentLinkedQueue<Notes>() // Cola java thread tipo safe thread
    private val channel = Channel<Notes>() // Cola optimizada para trabajar con coroutinas
    private var produced = 0
    private var consumed = 0
    private var isFinished = false
    private val batch = IntArray(5) // Lotes para correr las coroutinas

    private val mutex = Mutex()

    fun startProducerAndConsumer() {
        val duration = measureTimeMillis {
            runBlocking {
                // Producers
                launch(Dispatchers.Default) {
                    val producers = List(100_000) {
                        launch {
                            val note = NotesUtils.getNote()
                            channel.send(note)
                            mutex.withLock {
                                produced ++
                            }
//                            if (queue.offer(note)) {
//                                mutex.withLock {
//                                    produced ++
//                                }
//                            }
                        }
                    }
                    producers.joinAll()
                    channel.close()
                    isFinished = true
                    println("Producers Done")
                }

                // Consumers
                launch(Dispatchers.Default) {
                    val consumers = List(batch.size) {
                        launch {
                            for (note in channel) {
                                mutex.withLock {
                                    consumed ++
                                    batch[it] ++
                                }
                            }
//                            while (!isFinished || queue.isNotEmpty()) {
//                                val note = queue.poll()
//                                if (note != null) {
//                                    mutex.withLock {
//                                        consumed ++
//                                        batch[it] ++
//                                    }
//                                }
//                            }
                        }
                    }
                    consumers.joinAll()
                    println("Consumers Done")
                }
            }
        }

        // Verificando la cantidad de notas producidas y consumidas
        println("Time: $duration") // 559 ms | 442 ms
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