package com.example.databasechat.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.databasechat.client.models.Info
import com.example.databasechat.databinding.CardViewBinding

class MainHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = CardViewBinding.bind(item)
        fun bind(new: Info) = with(binding){
            tvName.text = new.name
            tvInfo.text = new.info
    }
}