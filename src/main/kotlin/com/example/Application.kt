package com.example

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.tls.certificates.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.io.use

fun main() = runBlocking {
    startSocketServer()
}


fun startSocketServer() = runBlocking{

    /*val keyStoreFile = File("com/example/smart-linkpos-ssl-cert/keystore.jks")
    val keyStorePassword = "smartlinkpos!!@@"

    val keyStore = buildKeyStore {
        certificate("smart-linkpos") {
            password = keyStorePassword
            domains = listOf("192.168.101.149", "192.168.101.51")
        }
    }
    keyStore.saveToFile(keyStoreFile, keyStorePassword)*/

    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).tcp().bind("192.168.101.51", 4001)

    println("Server is running on ${serverSocket.localAddress}")



    serverSocket.use {
        println("Server :: Listening at ${serverSocket.localAddress}")

        while (true) {
            val socket = serverSocket.accept()

            println("Status :: Accepted ${socket.remoteAddress}")

            val receiveChannel = socket.openReadChannel()
            val sendChannel = socket.openWriteChannel(autoFlush = true)
            try {

                val message: String = receiveChannel.readUTF8Line().toString()
                println("Message :: $message")
                sendChannel.writeStringUtf8("[Receive] your message is \"$message\"")
            } catch (e: Throwable) {
                println("Error : $e")
            } finally {
                socket.close()
            }

        }
    }
}
