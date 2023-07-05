package com.example.databasechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databasechat.adapter.MainAdapter
import com.example.databasechat.client.Client
import com.example.databasechat.databinding.ActivityMainBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainExecutor = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private val adapter = MainAdapter()
    private val client = Client()

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun toastErrors(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

        executor.execute {
            client.connect("178.163.60.33", 27015)
            val list = client.getList()
            mainExecutor.post {
                list.forEach { adapter.add(it) }
            }
        }

        binding.button.setOnClickListener {
            send()
        }

        binding.button2.setOnClickListener {
            get()
        }
    }
    private fun send(){
        if (!TextUtils.isEmpty(binding.edTextName.text) && !TextUtils.isEmpty(binding.edTextInfo.text)){
            executor.execute {
                client.connect("178.163.60.33", 27015)
                val info = client.sendMessage(
                    binding.edTextName.text.toString(),
                    binding.edTextInfo.text.toString()
                )
                mainExecutor.post {
                    adapter.add(info)
                    binding.edTextInfo.text.clear()
                }
            }
        } else {
            toastErrors("empty text [err 2]")
        }
    }

    private fun get(){
        executor.execute {
            client.connect("178.163.60.33", 27015)
            val info = client.getMessage()
            mainExecutor.post { adapter.add(info) }
        }
    }
}