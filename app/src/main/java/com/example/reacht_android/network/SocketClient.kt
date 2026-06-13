package com.example.reacht_android.network

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

object SocketClient {

    private const val PORT = 4444

    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: PrintWriter? = null

    // Cola donde el hilo lector mete las respuestas normales del servidor
    // para que send() las pueda recoger
    private val responseQueue = LinkedBlockingQueue<String>()

    // Cola donde el hilo lector mete los NEW_CHAT_MESSAGE del servidor
    // para que enterChat() los recoja en tiempo real
    val newChatMessageQueue = LinkedBlockingQueue<String>()

    fun connect(serverIp: String) {
        socket = Socket(serverIp, PORT)
        reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
        writer = PrintWriter(socket!!.getOutputStream(), true)
        startReaderThread()
    }

    fun disconnect() {
        socket?.close()
        socket = null
        reader = null
        writer = null
        responseQueue.clear()
        newChatMessageQueue.clear()
    }

    fun isConnected(): Boolean {
        return socket != null && socket!!.isConnected && !socket!!.isClosed
    }

    // Envía un JSON al servidor y espera la respuesta (máximo 10 segundos)
    fun send(json: String): String {
        if (writer != null) {
            writer!!.println(json)
        }
        // No espera la respuesta directa del servidor, la respuesta la espera y recibe el hilo de escucha,
        // la mete en la cola y este método lo que espera es que haya algo en la cola para cogerlo
        // Y no puede haber dos seguidas muy rápido, porque hasta que no se recibe la respuesta de una y acaba el método,
        // no se puede hacer otra.
        // Sólo se hace para que el hilo de escucha meta las respuestas que no son mensajes de chat
        // Podría ser una variable simple, pero esta clase nos da thread-safe y la función de espera
        val response = responseQueue.poll(5, TimeUnit.SECONDS)
        if (response != null) {
            return response
        }
        return ""
    }

    // Hilo de fondo que lee continuamente todo lo que manda el servidor
    private fun startReaderThread() {
        val listeningThread = Thread {
            try {
                while (socket != null && !socket!!.isClosed) {
                    val line = reader!!.readLine() ?: break
                    if (line.contains("\"NEW_CHAT_MESSAGE\"")) {
                        newChatMessageQueue.put(line)
                    } else {
                        responseQueue.put(line)
                    }
                }
            } catch (e: IOException) {
                println("SocketClient reader thread connection lost — ${e.message}")
            }
        }
        listeningThread.isDaemon = true
        listeningThread.start()
    }
}
