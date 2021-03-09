package ru.skillbranch.gameofthrones.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.ActivityMainBinding
import ru.skillbranch.gameofthrones.ui.SplashScreenFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        savedInstanceState ?: loadData()
        navController = Navigation.findNavController(this, R.id.fragment_home)
    }


    private fun loadData() {
        viewModel.syncData().observe(this, Observer {
            when (it) {
                is MainViewModel.LoadResult.Loading -> {
                    navController.navigate(R.id.splashScreenFragment)
                    binding.swipe.isRefreshing = true
                    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            , WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
                is MainViewModel.LoadResult.Success -> {
                    navController.navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToCharacterListScreenFragment())
                }
                is MainViewModel.LoadResult.Error -> {
                    Snackbar.make(binding.root,it.errorMessage.toString(),Snackbar.LENGTH_INDEFINITE).show()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    binding.swipe.isRefreshing = false
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel  = ViewModelProvider(this, MainViewModel.MainViewModelFactory(application)).get(
            MainViewModel::class.java)
    }
}