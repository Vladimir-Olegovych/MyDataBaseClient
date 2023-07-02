package com.example.databasechat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databasechat.R
import com.example.databasechat.client.models.Info
import com.example.databasechat.holder.MainHolder

class MainAdapter: RecyclerView.Adapter<MainHolder>() {
    private val listInfo = ArrayList<Info>()

    fun add(info: Info){
        listInfo.add(info)
        notifyDataSetChanged()
    }

    fun repeat(info: Info){
        if (listInfo.get(listInfo.size-1).info != info.info) {
            listInfo.add(info)
            notifyDataSetChanged()
        }
    }

    fun clear(){
        listInfo.clear()
        notifyDataSetChanged()
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