package ru.skillbranch.gameofthrones.ui.houses.characterlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.GeneratedAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.databinding.FragmentCharacterListBinding
import ru.skillbranch.gameofthrones.databinding.FragmentHousesBinding
import ru.skillbranch.gameofthrones.ui.houses.HousesFragment
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections
import ru.skillbranch.gameofthrones.ui.main.MainViewModel

class CharacterListFragment : Fragment() {

    private var _binding: FragmentCharacterListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CharacterListViewModel
    private lateinit var characterAdapter: CharacterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        initViewModel()
        characterAdapter = CharacterListAdapter {
           //todo change params for character screen
            val action = HousesFragmentDirections.actionCharacterListFragmentToCharacterScreenFragment(it.id,it.house,it.house)
            findNavController().navigate(action)
        }
        viewModel.getCharacters().observe(this, Observer {
            //TODO updateItems
            characterAdapter.updateItems(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharacterListBinding.inflate(inflater,container,false)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_character_list, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CharacterListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    fun initViewModel(){
        viewModel  = ViewModelProvider(this, CharacterListViewModel.CharacterListViewModelFactory(requireActivity().application)).get(
            CharacterListViewModel::class.java)
    }
}