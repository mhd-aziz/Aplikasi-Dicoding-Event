package com.dicoding.aplikasidicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.viewmodel.EventAdapter
import com.dicoding.aplikasidicodingevent.databinding.FragmentHomeBinding
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var activeEventAdapter: EventAdapter
    private lateinit var finishedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Setup RecyclerView
        setupRecyclerViews()

        // Setup SearchView
        setupSearchView()

        // Observe data from ViewModel
        mainViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            activeEventAdapter.submitList(events)
        }

        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedEventAdapter.submitList(events)
        }

        mainViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            // Tampilkan hasil pencarian
            activeEventAdapter.submitList(searchResults)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        // Fetch initial data
        mainViewModel.fetchEvents(1)
        mainViewModel.fetchEvents(0)
    }

    private fun setupRecyclerViews() {
        // Inisialisasi adapter untuk event aktif (horizontal)
        activeEventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recyclerViewActiveEvents.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActiveEvents.adapter = activeEventAdapter

        // Inisialisasi adapter untuk event selesai (vertikal)
        finishedEventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFinishedEvents.adapter = finishedEventAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mainViewModel.searchEvents(it) // Panggil searchEvents dengan query
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // Kita tidak melakukan pencarian di sini
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



