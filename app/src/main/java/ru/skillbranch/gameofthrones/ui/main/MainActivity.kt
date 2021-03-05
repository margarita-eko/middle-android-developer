package ru.skillbranch.gameofthrones.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.SplashScreenFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        savedInstanceState ?: loadData()
        navController = Navigation.findNavController(this, R.id.navigation_graph)
    }

    private fun loadData() {
        viewModel.syncData().observe(this, Observer {
            when (it) {
                is MainViewModel.LoadResult.Loading -> {
                    navController.navigate(R.id.splashScreenFragment)
                }
                is MainViewModel.LoadResult.Success -> {
                    navController.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToCharacterListScreenFragment())
                }
                is MainViewModel.LoadResult.Error -> {
                    //Snackbar.make(,it.errorMessage.toString(),Snackbar.LENGTH_INDEFINITE).show()
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel  = ViewModelProvider(this, MainViewModel.MainViewModelFactory(application)).get(
            MainViewModel::class.java)
    }
}