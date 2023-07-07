package com.example.databasechat.client

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.example.databasechat.client.models.Info
import com.example.databasechat.constants.Constants
import org.apache.commons.io.IOUtils
import java.net.Socket

class Client {
    private val kryo = Kryo()

    private var socket: Socket? = null
    private var connect = true

    private fun closeSocket(){
        try {
            IOUtils.closeQuietly(socket)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun registerInfo(){
        kryo.register(Info::class.java)
        kryo.register(ArrayList<Info>()::class.java)
    }

    fun getBoolean(): Boolean{
        return connect
    }

    fun getList(address: String, port: Int): ArrayList<Info> {
        connect = false
        val info = ArrayList<Info>()
        info.add(Info())

        return try {
            socket = Socket(address, port)
            val input = Input(socket?.getInputStream())
            val output = Output(socket?.getOutputStream())

            kryo.writeObject(output, Info("admin", Constants.GET_JSON_ARRAY))
            output.flush()

            val get = kryo.readObject(input, ArrayList<Info>()::class.java)
            get
        } catch (e: Throwable) {
            info
        }finally {
            closeSocket()
            connect = true
        }
    }
    fun getMessage(address: String, port: Int): Info {
        connect = false

        return try {
            socket = Socket(address, port)
            val input = Input(socket?.getInputStream())
            val output = Output(socket?.getOutputStream())

            kryo.writeObject(output, Info("admin", Constants.GET_MESSAGE))
            output.flush()

            val get = kryo.readObject(input, Info::class.java)
            get
        } catch (e: Throwable) {
            Info()
        }finally {
            closeSocket()
            connect = true
        }
    }
    fun sendMessage(address: String, port: Int, name: String, info: String): Info {
        connect = false

        return try {
            socket = Socket(address, port)
            val output = Output(socket?.getOutputStream())

            kryo.writeObject(output, Info(name, info))
            output.flush()

            Info(name, info)
        } catch (e: Throwable) {
            Info()
        }finally {
            closeSocket()
            connect = true
        }
    }
}