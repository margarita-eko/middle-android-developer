package ru.skillbranch.gameofthrones.ui.houses.characterlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.main.MainViewModel
import java.lang.IllegalArgumentException

class CharacterListViewModel(application: Application): AndroidViewModel(application) {

    fun getCharacters(): LiveData<List<CharacterItem>> {
        TODO("Not yet implemented")
    }

    private var repository = RootRepository

    class CharacterListViewModelFactory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CharacterListViewModel::class.java)){
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown class")
        }

    }
}