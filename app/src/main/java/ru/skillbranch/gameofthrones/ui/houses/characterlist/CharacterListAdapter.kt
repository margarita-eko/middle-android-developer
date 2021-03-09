package ru.skillbranch.gameofthrones.ui.houses.characterlist

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.databinding.ItemCharacterlistBinding

class CharacterListAdapter(val listener: (CharacterItem) -> Unit) : RecyclerView.Adapter<CharacterListAdapter.CharacterItemHolder>() {

    var items: List<CharacterItem> = listOf()

    class CharacterItemHolder(val containerView: View, val binding: ItemCharacterlistBinding): RecyclerView.ViewHolder(containerView) {

        fun bind(
            item: CharacterItem,
            listener: (CharacterItem) -> Unit
        ){
            item.name.also {
                binding.tvName.text = if (it.isBlank()) "Information is unknown" else it
            }

            item.titles
                .plus(item.aliases)
                .filter { it.isNotBlank() }
                .also {
                    binding.tvAliases.text = if (it.isEmpty()) "Information is unknown"
                    else it.joinToString { " * " }
                }

            binding.ivAvatar.setImageResource(item.house.icon)

            itemView.setOnClickListener{
                listener(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterItemHolder {
        val binding = ItemCharacterlistBinding.inflate(from(parent.context),parent,false)
        //val containerView: View = from(parent.context).inflate(R.layout.item_characterlist,parent,false)
        return CharacterItemHolder(binding.root,binding)
    }

    override fun onBindViewHolder(holder: CharacterItemHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    fun updateItems(data: List<CharacterItem>) {

        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return items.size
            }

            override fun getNewListSize(): Int {
                return data.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].id == data[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition].hashCode() == data[newItemPosition].hashCode()
            }

        }

    }


}