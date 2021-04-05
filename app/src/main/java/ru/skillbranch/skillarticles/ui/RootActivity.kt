package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.extensions.dpToIntPx

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBar()

        with (binding.bottombar) {
            btnLike.setOnClickListener {
                Snackbar.make(binding.coordinatorContainer, "test", Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.bottombar)
                    .show()
            }

        }

        val switchMode = binding.submenu.switchMode
        switchMode.setOnClickListener {
            delegate.localNightMode =
                if (switchMode.isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        }



    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (binding.toolbar.childCount>2) binding.toolbar.getChildAt(2) as ImageView else null
        (logo?.layoutParams as? Toolbar.LayoutParams)?.let {
            it.width = dpToIntPx(40)
            it.height = dpToIntPx(40)
            it.marginEnd =dpToIntPx(16)
            logo.layoutParams = it
        }
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
    }
}