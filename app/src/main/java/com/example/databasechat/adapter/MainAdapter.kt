package com.example.databasechat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databasechat.R
import com.example.databasechat.client.models.Info
import com.example.databasechat.holder.MainHolder

class MainAdapter: RecyclerView.Adapter<MainHolder>() {
    private val listInfo = ArrayList<Info>()
    private var scroll = false

    fun getBool(): Boolean{
        return scroll
    }

    fun setBool(){
        scroll = false
    }

    fun add(info: Info){
        listInfo.add(info)
        notifyDataSetChanged()
    }
    fun addRepeat(info: Info){
        if (listInfo.size != 0) {
            if (info.info != listInfo.get(listInfo.size - 1).info) {
                listInfo.add(info)
                notifyDataSetChanged()
                scroll = true
            }
        } else {
            listInfo.add(info)
            notifyDataSetChanged()
            scroll = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return MainHolder(view)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(listInfo[position])
    }

    override fun getItemCount(): Int {
        return listInfo.size
    }
}