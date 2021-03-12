package ru.skillbranch.gameofthrones.ui.houses

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.skillbranch.gameofthrones.ui.main.MainActivity
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.databinding.FragmentHousesBinding
import kotlin.math.hypot
import kotlin.math.max

class HousesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var colors: Array<Int>
    private lateinit var charactersPagerAdapter: HousesPageAdapter

    private var _binding: FragmentHousesBinding? = null
    private val binding get() = _binding!!

    @ColorInt
    private var currentColor: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        charactersPagerAdapter = HousesPageAdapter(requireActivity())
        colors = requireContext().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martel_primary),
                getColor(R.color.tyrel_primary)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //with(menu.findItem())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHousesBinding.inflate(inflater,container,false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_houses, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        (requireActivity() as MainActivity).setSupportActionBar(binding.toolbar)
        if (currentColor != -1) binding.appbar.setBackgroundColor(currentColor)

        binding.viewPager.adapter = charactersPagerAdapter
        TabLayoutMediator(binding.tabs,binding.viewPager) { tab, position ->
            tab.text = HouseType.values() [position].title
        }.attach()

        with(binding.tabs){
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab!!.position

                    //if color different, do animate

                    if ((binding.appbar.background!! as ColorDrawable).color != colors[position]){
                        val rect = Rect()
                        val tabView = tab.view as View

                        tabView.postDelayed(
                            {
                                tabView.getGlobalVisibleRect(rect)
                                animateAppBarReval(position, rect.centerX(), rect.centerY())
                            },
                            300
                        )

                        tabView.getGlobalVisibleRect(rect)
                        animateAppBarReval(position, rect.centerX(), rect.centerY())
                    }
                }



            })
        }
    }

    private fun animateAppBarReval(position: Int, centerX: Int, centerY: Int) {
        val endRadius = max(
            hypot(centerX.toDouble(),centerY.toDouble()),
            hypot(binding.appbar.width.toDouble() - centerX.toDouble(),centerY.toDouble())
        )

        with(binding.revealView){
            visibility = View.VISIBLE
            setBackgroundColor(colors[position])
        }

        ViewAnimationUtils.createCircularReveal(
            binding.revealView,
            centerX,
            centerY,
            0f,
            endRadius.toFloat()
        ).apply {
            doOnEnd {
                binding.appbar?.setBackgroundColor(colors[position])
                binding.revealView.visibility = View.INVISIBLE
            }
            start()
        }
        currentColor = colors[position]
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HousesFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}