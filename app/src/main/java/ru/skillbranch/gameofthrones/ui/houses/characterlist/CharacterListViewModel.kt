package ru.skillbranch.gameofthrones.ui.houses.characterlist

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.circularreveal.CircularRevealHelper
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import ru.skillbranch.gameofthrones.ui.main.MainViewModel
import java.lang.IllegalArgumentException

class CharacterListViewModel(private val houseName: String): ViewModel() {

    fun getCharacters(): LiveData<List<CharacterItem>> {
        val characters: MutableLiveData<List<CharacterItem>> = MutableLiveData()
        repository.findCharactersByHouseName(houseName){
            characters.postValue(it)
        }
        return characters
    }

    private var repository = RootRepository

    class CharacterListViewModelFactory(private val houseName: String): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CharacterListViewModel::class.java)){
                return CharacterListViewModel(houseName) as T
            }
            throw IllegalArgumentException("Unknown class View Model")
        }

    }
}