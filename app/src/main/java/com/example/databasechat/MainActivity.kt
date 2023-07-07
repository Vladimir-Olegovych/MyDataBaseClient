package com.example.databasechat

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databasechat.adapter.MainAdapter
import com.example.databasechat.client.Client
import com.example.databasechat.constants.Constants
import com.example.databasechat.databinding.ActivityMainBinding
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private lateinit var sPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var binding: ActivityMainBinding

    private val mainExecutor = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val executor2 = Executors.newSingleThreadExecutor()

    private val adapter = MainAdapter()
    private val client = Client()

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
        executor2.shutdown()
    }

    override fun onPause() {
        super.onPause()
        editor = sPreferences.edit()
        editor.putString(Constants.APP_PREFERENCES_NAME, binding.edTextName.text.toString())
        editor.apply()
    }

    private fun toastErrors(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sPreferences = getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)

        if(sPreferences.contains(Constants.APP_PREFERENCES_NAME)) {
            binding.edTextName.setText(sPreferences.getString(Constants.APP_PREFERENCES_NAME, ""))
        }

        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter

        client.registerInfo()
        list()
        repeat()

        binding.button.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edTextName.text) && !TextUtils.isEmpty(binding.edTextInfo.text)){
                send()
            } else {
                toastErrors("empty text [err 2]")
            }
        }
    }

    private fun send(){
        executor.execute {
            if (client.getBoolean()) {
                val info = client.sendMessage(
                    "178.163.60.33", 27015,
                    binding.edTextName.text.toString(),
                    binding.edTextInfo.text.toString()
                )
                if (info.info != "null") {
                    mainExecutor.post {
                        binding.edTextInfo.text.clear()
                        adapter.add(info)
                        binding.rcView.smoothScrollToPosition(adapter.getItemCount() + 1)
                    }
                }
                else{
                    mainExecutor.post {
                        toastErrors("no connect [err 1]")
                    }
                }
            }
        }
    }

    private fun repeat(){
        executor2.execute {
            var process = true
            while (process) {
                Thread.sleep(1_000/3)
                if (client.getBoolean()){
                    val info = client.getMessage("178.163.60.33", 27015)
                    if (info.info != "null") {
                        mainExecutor.post {
                            adapter.addRepeat(info)
                            if (adapter.getBool()){
                                binding.rcView.smoothScrollToPosition(adapter.getItemCount() + 1)
                                adapter.setBool()
                            }
                        }
                    }
                    else{
                        process = false
                        mainExecutor.post {
                            adapter.add(info)
                            if (adapter.getBool()){
                                binding.rcView.smoothScrollToPosition(adapter.getItemCount() + 1)
                                adapter.setBool()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun list(){
        executor.execute {
            if (client.getBoolean()) {

                val list = client.getList("178.163.60.33", 27015)

                if (list[0].info != "null") {
                    mainExecutor.post {
                        list.forEach { adapter.add(it) }
                    }
                }
            }
        }
    }
}