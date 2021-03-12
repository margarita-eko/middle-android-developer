package ru.skillbranch.gameofthrones.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.lang.IllegalArgumentException

class CharacterScreenViewModel(private val characterId: String) : ViewModel() {

    private var repository = RootRepository

    fun getCharacter() : LiveData<CharacterFull> {
        val character: MutableLiveData<CharacterFull> = MutableLiveData()
        repository.findCharacterFullById(characterId){
            character.postValue(it)
        }
        return character
    }

    class CharacterScreenViewModelFactory(private val characterId: String): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(CharacterScreenViewModel::class.java)){
                return CharacterScreenViewModel(characterId) as T
            }
            throw IllegalArgumentException("Unknown class View Model")
        }

    }

}