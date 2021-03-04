package ru.skillbranch.gameofthrones.repositories

import android.util.Log
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import ru.skillbranch.gameofthrones.AppClass
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.database.AppDatabase
import ru.skillbranch.gameofthrones.database.DaoInterface
import ru.skillbranch.gameofthrones.network.RetrofitClient

object RootRepository {

    private fun <T>isResponseOk(response: Response<T>, methodName: String): Boolean {
        return try {
            if (response.isSuccessful && response.body()!=null) {
                true
            } else {
                Log.e("RootRepository", "$methodName Error: ${response.code()}")
                false
            }
        } catch (e: HttpException) {
            Log.e("RootRepository", "$methodName Exception ${e.message}")
            false
        } catch (e: Throwable) {
            Log.e("RootRepository", "$methodName Unknown exception")
            false
        }
    }

    private val dbDao: DaoInterface = AppClass.getDatabase()!!.dbDao()

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        CoroutineScope(IO).launch {
            val response = RetrofitClient.getJSONApi().getHousesList()
            withContext(Dispatchers.Main) {
                if (isResponseOk(response,this::class.java.name)){
                    result.invoke(response.body()!!)
                }
            }
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети 
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {
        CoroutineScope(IO).launch{
            var neededHouseRes: MutableList<HouseRes> = mutableListOf()
            houseNames.forEach {
                val response = RetrofitClient.getJSONApi().getHouseByName(it)
                if (isResponseOk(response,this::class.java.name) && response.body()!!.isNotEmpty() ){
                    neededHouseRes.add(response.body()!!.first())
                }
            }
            result.invoke(neededHouseRes.toList())
        }

    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        CoroutineScope(IO).launch {
            var houses: MutableList<Pair<HouseRes, List<CharacterRes>>> = mutableListOf()
            houseNames.forEach { houseName ->
                val response = RetrofitClient.getJSONApi().getHouseByName(houseName)
                if (isResponseOk(response, this::class.java.name) && response.body()!!.isNotEmpty()) {
                    val houseRes = response.body()!!.first()
                    val characterList: MutableList<CharacterRes> = mutableListOf()
                    houseRes.swornMembers.forEach {
                        val response = RetrofitClient.getJSONApi().getCharacterByUrl(it.replace(AppConfig.BASE_URL,""))
                        if (isResponseOk(response, this::class.java.name)) {
                            characterList.add(response.body()!!.apply { houseId = houseName })
                        }
                    }
                    houses.add(houseRes to characterList)
                }
            }
            result.invoke(houses.toList())
        }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {
        CoroutineScope(IO).launch {
            val houseList: List<House> = houses.map { it.toHouse() }
            dbDao.insertHouses(houseList)
            complete.invoke()
        }
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    //fun insertCharacters(apiData : List<Pair<HouseRes, List<CharacterRes>>>, complete: () -> Unit) {
    fun insertCharacters(Characters : List<CharacterRes>, complete: () -> Unit) {
        CoroutineScope(IO).launch {
            val characterList = Characters.map{it.toCharacter()}
            dbDao.insertCharacters(characterList)
            complete.invoke()
        }

    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        CoroutineScope(IO).launch {
            dbDao.dropDB()
            complete()
        }
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String, result: (characters : List<CharacterItem>) -> Unit) {
        CoroutineScope(IO).launch {
            val characterList = dbDao.findCharactersByHouseName(name)
            result(characterList)
        }
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id : String, result: (character : CharacterFull) -> Unit) {
        CoroutineScope(IO).launch {
            val characterList = dbDao.findCharacterFullById(id)
            result(characterList.first())
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed : Boolean) -> Unit){
        CoroutineScope(IO).launch {
            val isNeed = dbDao.isDbEmpty()
            result(isNeed)
        }
    }

    fun sync(complete: () -> Unit){
        getNeedHouseWithCharacters(*AppConfig.NEED_HOUSES) {
            val housesList = mutableListOf<HouseRes>()
            val charactersList = mutableListOf<CharacterRes>()
            it.forEach {
                housesList.add(it.first)
                charactersList.addAll(it.second)
            }
             insertHouses(housesList){
                insertCharacters(charactersList){
                    complete()
                }
             }
        }
    }

}