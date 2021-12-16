package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.view.forEach
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.viewmodels.*

class RootActivity : AppCompatActivity() {

    private val viewModel: RootViewModel by viewModels()
    lateinit var viewBinding: ActivityRootBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("RootActivity", "OnCreate root activity")
        viewBinding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setSupportActionBar(viewBinding.toolbar)

        //set nav controller
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        //setup action bar navigation
        val appbarConfig = AppBarConfiguration(
            setOf(
                R.id.nav_articles,
                R.id.nav_bookmarks,
                R.id.nav_profile
            )
        )

        setupActionBarWithNavController(navController, appbarConfig)
        viewBinding.navView.setOnItemSelectedListener {
            viewModel.topLevelNavigate(it.itemId)
            true
        }

        viewModel.observeNavigation(this, ::handleNavigation)

        navController.addOnDestinationChangedListener{_, destination, args ->
            Log.d("RootActivity", "change destination $destination")
            viewBinding.navView.menu.forEach {  item ->
                if (destination.matchDestination(item.itemId)){
                    item.isChecked = true
                }

                //implement select profile icon if auth flow is open
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(viewBinding.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)

        when(notify){
            is Notify.TextMessage -> {/* nothing */}
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel){
                    notify.actionHandler?.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar){
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel){
                        notify.errHandler?.invoke()
                    }
                }
            }
        }
        snackbar.show()
    }

    fun handleNavigation(cmd: NavCommand){
        when(cmd) {
            is NavCommand.Action -> navController.navigate(cmd.action)
            is NavCommand.Builder -> navController.navigate(cmd.destination, cmd.args, cmd.options, cmd.extras)
            is NavCommand.TopLevel -> {
                val popBackstack = navController.popBackStack(cmd.destination,false)
                if (!popBackstack) navController.navigate(cmd.destination, null, cmd.options)
            }
        }
    }

    private fun NavDestination.matchDestination(@IdRes resId: Int) = hierarchy.any{it.id == resId}


}