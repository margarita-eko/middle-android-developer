package ru.skillbranch.gameofthrones.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.gameofthrones.repositories.RootRepository

class SplashScreenViewModel() : ViewModel() {

    var isNeedUpdate = MutableLiveData(false)
    private var repository = RootRepository

    init {
        repository.isNeedUpdate {
            isNeedUpdate.value = it
            if (isNeedUpdate.value!!) {
                // load data from network
                repository.sync {
                    //load finished go to characters screen
                }
            }
        }
    }


}