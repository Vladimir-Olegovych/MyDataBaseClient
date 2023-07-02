package com.example.databasechat.client

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.example.databasechat.MainActivity
import com.example.databasechat.client.models.Info
import com.example.databasechat.constants.Constants.GET_JSON_ARRAY
import com.example.databasechat.constants.Constants.GET_MESSAGE
import org.apache.commons.io.IOUtils
import java.net.Socket
import java.util.concurrent.Executors

class Client(private val activity: MainActivity) {

    private val mainExecutor = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var socket: Socket

    fun start(name: String, info: String) {
        executor.execute {
            val kryo = Kryo()
            try {
                socket = Socket("178.163.60.33", 27015)

                val input = Input(socket.getInputStream())
                val output = Output(socket.getOutputStream())

                kryo.register(Info::class.java)
                kryo.register(ArrayList<Info>()::class.java)

                kryo.writeObject(output, Info(name, info))
                output.flush()

                when (info){
                    GET_JSON_ARRAY -> {
                        val get = kryo.readObject(input, ArrayList<Info>()::class.java)
                        mainExecutor.post { activity.getList(get) }
                    }
                    GET_MESSAGE -> {
                        val get = kryo.readObject(input, Info::class.java)
                        mainExecutor.post { activity.repeat(get) }
                    }
                    else -> {
                        val get = kryo.readObject(input, Info::class.java)
                        mainExecutor.post { activity.getInfo(get) }
                    }
                }
            } catch (e: Throwable) {
                mainExecutor.post { Toast.makeText(activity, "no connect [err 1]", Toast.LENGTH_LONG).show() }
                println(e)
            } finally {
                try {
                    IOUtils.closeQuietly(socket)
                }catch (e: Throwable){
                    e.printStackTrace()
                }
            }
        }
    }
    fun destroy(){
        executor.shutdown()
    }
}