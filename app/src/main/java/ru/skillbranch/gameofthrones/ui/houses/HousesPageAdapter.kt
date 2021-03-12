package ru.skillbranch.gameofthrones.ui.houses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.houses.characterlist.CharacterListFragment

class HousesPageAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm){

    override fun getItemCount(): Int {
        return HouseType.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return CharacterListFragment.newInstance(HouseType.values() [position].title)
    }

}