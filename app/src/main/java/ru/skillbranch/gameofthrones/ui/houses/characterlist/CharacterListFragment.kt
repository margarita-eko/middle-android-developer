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
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
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
        val houseName = arguments?.getString("HOUSE_NAME", HouseType.STARK.title) ?: HouseType.STARK.title
        initViewModel(houseName)
        characterAdapter = CharacterListAdapter {
           //todo change params for character screen
            val action = HousesFragmentDirections.actionCharacterListFragmentToCharacterScreenFragment(it.id,it.house.title,it.name)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO create layout
        /*with(_binding.rvCharacterList) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            addItemDecoration(ItemDivider())
            adapter = characterAdapter
        }*/
    }

    companion object {

        @JvmStatic
        fun newInstance(houseName: String) =
            CharacterListFragment().apply {
                arguments = Bundle().apply {
                    putString("HOUSE_NAME", houseName)
                }
            }
    }

    fun initViewModel(houseName: String){
        viewModel  = ViewModelProvider(this, CharacterListViewModel.CharacterListViewModelFactory(houseName)).get(
            CharacterListViewModel::class.java)
    }
}