package ru.skillbranch.gameofthrones.ui.main

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.AppClass
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.lang.IllegalArgumentException

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var isNeedUpdate = MutableLiveData(false)
    var loadFinished = MutableLiveData(false)
    private var repository = RootRepository

    class MainViewModelFactory(private val application: Application):ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown class")
        }

    }

    fun syncData(): MutableLiveData<LoadResult<Boolean>> {
        val result: MutableLiveData<LoadResult<Boolean>> =
            MutableLiveData(LoadResult.Loading(false))
        repository.isNeedUpdate {
            val isNeeded = it
            if (isNeeded) {
                if (!getApplication<AppClass>().isNetworkAvailable) {
                    result.postValue(LoadResult.Error("Интернет недоступен, приложение может работать некорректно"))
                    return@isNeedUpdate
                } else {
                    repository.sync {
                        result.postValue(LoadResult.Success(true))
                    }
                }
            } else {
                viewModelScope.launch {
                    delay(5000)
                    result.postValue(LoadResult.Success(true))
                }
            }
        }
        return result
    }

    sealed class LoadResult<T>(
        val data: T?,
        val errorMessage: String?=null
    ){
        class Success<T>(data:T): LoadResult<T>(data)
        class Loading<T>(data:T?=null): LoadResult<T>(data)
        class Error<T>(message:String, data: T? = null): LoadResult<T>(data)
    }

}