package com.example.databasechat.client

import android.util.Log
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.example.databasechat.client.models.Info
import com.example.databasechat.constants.Constants
import org.apache.commons.io.IOUtils
import java.net.Socket

class Client {

    private var socket: Socket? = null
    private lateinit var input: Input
    private lateinit var output: Output

    private val kryo = Kryo()

    private fun closeSocket(){
        try {
            IOUtils.closeQuietly(socket)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        Log.d("errorsSocket", "socket close")
    }

    fun connect(address: String, port: Int) {
        try {
            socket = Socket(address, port)
            input = Input(socket?.getInputStream())
            output = Output(socket?.getOutputStream())

            kryo.register(Info::class.java)
            kryo.register(ArrayList<Info>()::class.java)

            Log.d("errorsSocket", "socket connect")


        } catch (e: Throwable) {
            e.printStackTrace()
            Log.d("errorsSocket", e.toString())
        }
    }

    fun getList(): ArrayList<Info>{
        if (socket != null) {
            kryo.writeObject(output, Info("admin", Constants.GET_JSON_ARRAY))
            output.flush()

            Log.d("errorsSocket", "socket getList")

            val get = kryo.readObject(input, ArrayList<Info>()::class.java)
            closeSocket()
            return get
        }

        val list = ArrayList<Info>()
        list.add(Info())

        return list
    }

    fun getMessage(): Info{
        if (socket != null) {
            kryo.writeObject(output, Info("admin", Constants.GET_MESSAGE))
            output.flush()

            Log.d("errorsSocket", "socket getMassage")

            val get = kryo.readObject(input, Info::class.java)
            closeSocket()
            return get
        }
        return Info()
    }

    fun sendMessage(name: String, info: String): Info{
        if (socket != null) {
            kryo.writeObject(output, Info(name, info))
            output.flush()

            Log.d("errorsSocket", "socket sendMessage")

            val get = kryo.readObject(input, Info::class.java)
            closeSocket()
            return get
        }
        return Info()
    }
}