package com.dicoding.aplikasidicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.viewmodel.EventAdapter
import com.dicoding.aplikasidicodingevent.databinding.FragmentFinishedBinding
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.google.android.material.snackbar.Snackbar

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup ViewModel
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // Observe LiveData for the list of events
        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        // Observe loading state
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Observasi pesan error
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                // Menggunakan Snackbar untuk menampilkan pesan error
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(requireContext()) { event ->
            // Handle event click, navigate to DetailActivity
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event) // Pastikan 'event' sesuai dengan model data yang dikirim
            }
            startActivity(intent)
        }
        binding.recycleApiFinish.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recycleApiFinish.adapter = eventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
