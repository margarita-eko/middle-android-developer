package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBar()
        setupBottombar()
        setupSubmenu()

        viewModel = ViewModelProvider(this, ViewModelFactory("0")).get(ArticleViewModel::class.java)
        viewModel.observeState(this){
            renderUi(it)
            setupToolBar()
        }

        viewModel.observeNotifications(this){
            renderNotification(it)
        }

    }

    private fun renderUi(data: ArticleState) {
        with(binding.bottombar){
            //bind submenu state
            btnSettings.isChecked = data.isShowMenu
            if (data.isShowMenu) binding.submenu.open() else binding.submenu.close()

            //bind article person data
            btnLike.isChecked = data.isLike
            btnBookmark.isChecked = data.isBookmark

        }

        //bind submenu views
        with(binding.submenu){
            switchMode.isChecked = data.isDarkMode
            delegate.localNightMode = if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            if (data.isBigText){
                binding.tvTextContent.textSize = 18f
                btnTextUp.isChecked = true
                btnTextDown.isChecked = false
            }else{
                binding.tvTextContent.textSize = 14f
                btnTextUp.isChecked = false
                btnTextDown.isChecked = true
            }
        }

        //bind content
        binding.tvTextContent.text = if (data.isLoadingContent) "loading" else data.content.first() as String

        //bind toolbar
        with(binding.toolbar){
            title = data.title ?: "Skill Articles"
            subtitle = data.category ?: "loading..."
            if (data.categoryIcon != null) logo = getDrawable(data.categoryIcon as Int)
        }
    }

    private fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(binding.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)

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
    private fun setupSubmenu() {
        with(binding.submenu){
            btnTextUp.setOnClickListener { viewModel.handleUpText() }
            btnTextDown.setOnClickListener { viewModel.handleDownText() }
            switchMode.setOnClickListener { viewModel.handleNightMode() }
        }
    }

    private fun setupBottombar() {
        with(binding.bottombar){
            btnLike.setOnClickListener{
                viewModel.handleLike()
            }
            btnBookmark.setOnClickListener{
                viewModel.handleBookmark()
            }
            btnShare.setOnClickListener {
                viewModel.handleShare()
            }
            btnSettings.setOnClickListener { viewModel.handleToggleMenu() }
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (binding.toolbar.childCount>2) binding.toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = dpToIntPx(40)
            it.height = dpToIntPx(40)
            it.marginEnd =dpToIntPx(16)
            logo.layoutParams = it
        }

    }
}