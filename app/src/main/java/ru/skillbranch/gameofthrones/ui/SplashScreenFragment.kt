package ru.skillbranch.gameofthrones.ui

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentSplashScreenBinding
import ru.skillbranch.gameofthrones.ui.main.MainActivity
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater,container,false)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.hide()

        val animation = ValueAnimator.ofObject(ArgbEvaluator(),Color.WHITE,Color.RED)
        animation.repeatMode = ObjectAnimator.REVERSE
        animation.repeatCount = ObjectAnimator.INFINITE
        animation.duration = 2000
        animation.addUpdateListener {
            //FIXME ??? using findViewById because of binding is null after onDestroyView
            val bgView = view.findViewById<ImageView>(R.id.iv_splash_bg)
            bgView.setColorFilter(animation.animatedValue as Int,PorterDuff.Mode.MULTIPLY)
        }
        animation.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SplashScreenFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}