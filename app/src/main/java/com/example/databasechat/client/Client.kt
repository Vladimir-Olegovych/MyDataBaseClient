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
    private lateinit var input: Input
    private lateinit var output: Output

    private fun closeSocket(){
        try {
            input.close()
            output.close()
            IOUtils.closeQuietly(socket)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun connect(address: String, port: Int): Boolean {
        try {
            socket = Socket(address, port)
            input = Input(socket?.getInputStream())
            output = Output(socket?.getOutputStream())

            kryo.register(Info::class.java)

        } catch (e: Throwable) {
            return false
        }
        return true
    }

    fun getList(): ArrayList<Info>{
        return try {
            kryo.writeObject(output, Info("admin", Constants.GET_JSON_ARRAY))
            output.flush()

            kryo.register(ArrayList<Info>()::class.java)
            val get = kryo.readObject(input, ArrayList<Info>()::class.java)
            get
        }catch (e: Throwable){
            ArrayList()
        }finally {
            closeSocket()
        }
    }

    fun getMessage(): Info{
        return try {
            kryo.writeObject(output, Info("admin", Constants.GET_MESSAGE))
            output.flush()
            
            val get = kryo.readObject(input, Info::class.java)
            get
        }catch (e: Throwable){
            Info()
        }finally {
            closeSocket()
        }
    }

    fun sendMessage(name: String, info: String): Info{
        return try {
            kryo.writeObject(output, Info(name, info))
            output.flush()

            closeSocket()
            Info(name, info)
        }catch (e: Throwable){
            Info()
        }finally {
            closeSocket()
        }
    }
}