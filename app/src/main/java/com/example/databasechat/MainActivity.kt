package com.example.databasechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databasechat.adapter.MainAdapter
import com.example.databasechat.client.Client
import com.example.databasechat.client.models.Info
import com.example.databasechat.constants.Constants.GET_JSON_ARRAY
import com.example.databasechat.constants.Constants.GET_MESSAGE
import com.example.databasechat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = MainAdapter()
    private val client = Client(this)

    fun getInfo(info: Info){
        adapter.add(info)
    }

    fun repeat(info: Info){
        adapter.repeat(info)
    }

    fun getList(list: ArrayList<Info>){
        adapter.clear()
        list.forEach{ adapter.add(it)}
    }

    override fun onDestroy() {
        super.onDestroy()
        client.destroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

        client.start("admin", GET_JSON_ARRAY)

        binding.button.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edTextName.text) && !TextUtils.isEmpty(binding.edTextInfo.text)){
                client.start(binding.edTextName.text.toString(), binding.edTextInfo.text.toString())
                binding.edTextInfo.text.clear()
            } else {
                Toast.makeText(this, "empty text [err 2]", Toast.LENGTH_SHORT).show()
            }
        }
        binding.button2.setOnClickListener {
            client.start("admin", GET_MESSAGE)
        }
    }
}