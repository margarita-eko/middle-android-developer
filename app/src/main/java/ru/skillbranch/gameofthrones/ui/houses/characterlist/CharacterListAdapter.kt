package ru.skillbranch.gameofthrones.ui.houses.characterlist

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem

class CharacterListAdapter(val listener: (CharacterItem) -> Unit) : RecyclerView.Adapter<CharacterListAdapter.CharacterItemHolder>() {

    inner class CharacterItemHolder(convertView: View): RecyclerView.ViewHolder(convertView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterItemHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CharacterItemHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    fun updateItems(it: List<CharacterItem>?) {

    }


}