package ru.skillbranch.gameofthrones.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import java.lang.IllegalArgumentException

class CharacterScreenViewModel(private val characterId: String) : ViewModel() {
    fun getCharacter() : LiveData<CharacterFull> {
        TODO("Not yet implemented")
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