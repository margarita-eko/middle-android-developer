package ru.skillbranch.gameofthrones.ui.character

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterScreenBinding
import ru.skillbranch.gameofthrones.ui.main.MainActivity

class CharacterScreenFragment : Fragment() {

    private var _binding: FragmentCharacterScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CharacterScreenViewModel
    private val args: CharacterScreenFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        initViewModel(args.characterid)
        setHasOptionsMenu(true)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterScreenBinding.inflate(inflater,container,false)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_character_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val houseType = HouseType.fromString(args.house)
        val arms = houseType.coastOfArms
        val scrim = houseType.primaryColor
        val scrimDark = houseType.darkColor

        val mainActivity = requireActivity() as MainActivity
        //TODO set action bar
        //mainActivity.setSupportActionBar()
        mainActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = args.title
        }
        binding.ivArms.setImageResource(arms)
        with(binding.collapsingLayout){
            setBackgroundResource(scrim)
            setContentScrimResource(scrim)
            setStatusBarScrimResource(scrimDark)
        }

        //bug fix?
        //binding.collapsingLayout.post { binding.collapsingLayout.requestLayout() }

        viewModel.getCharacter().observe(viewLifecycleOwner, Observer { character ->
            if (character==null) return@Observer

            val iconColor = requireContext().getColor(houseType.accentColor)

            //set decor
            listOf(binding.tvWordsLable,binding.tvAliasesLable,binding.tvBornLable, binding.tvTitlesLable).forEach{
                it.compoundDrawables.first().setTint(iconColor)
            }

            binding.tvWords.text = character.words
            binding.tvBorn.text = character.born
            binding.tvTitles.text = character.titles
                    .filter { it.isNotEmpty() }
                    .joinToString { "\n" }
            binding.tvAliases.text = character.aliases
                    .filter { it.isNotEmpty() }
                    .joinToString { "\n" }

            character.father?.let {
                binding.groupFather.visibility = View.VISIBLE
                binding.btnFather.text = it.name
                val action = CharacterScreenFragmentDirections.actionCharacterScreenFragmentSelf(it.id, it.house.title,it.name)
                binding.btnFather.setOnClickListener {
                    findNavController().navigate(action)
                }
            }

            character.mother?.let {
                binding.groupMother.visibility = View.VISIBLE
                binding.btnMother.text = it.name
                val action = CharacterScreenFragmentDirections.actionCharacterScreenFragmentSelf(it.id, it.house.title,it.name)
                binding.btnMother.setOnClickListener {
                    findNavController().navigate(action)
                }
            }

            if (character.died.isNotBlank()) {
                Snackbar.make(binding.coordinator, "Died in: ${character.died}", Snackbar.LENGTH_INDEFINITE).show()
            }

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CharacterScreenFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    fun initViewModel(characterId: String){
        viewModel  = ViewModelProvider(this, CharacterScreenViewModel.CharacterScreenViewModelFactory(characterId)).get(
            CharacterScreenViewModel::class.java)
    }
}