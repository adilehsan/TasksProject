package com.example.app.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app.state.NetworkResponseStates
import com.example.app.utils.ProgressDialog
import com.example.app.viewmodel.MainViewModel
import com.example.mynewprojecttest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val homeViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchDataFromServer()
    }

    private fun fetchDataFromServer() {
        inItProgressDialog()
        //fetch data call from viewModel using coroutine
        lifecycleScope.launch {
            homeViewModel.fetchData()
        }
        fetchCategories()
    }

    private fun fetchCategories() {
        //map response with success response and error response
        homeViewModel.responseLiveData.observe(this) { response ->
            when (response) {
                is NetworkResponseStates.Success -> {
                    progressDialog?.cancel()
                    Log.d("MainActivity", "Data fetched successfully: ${response.data}")
                    Toast.makeText(this,"Data fetched successfully: ${response.data}",Toast.LENGTH_LONG).show()
                }

                is NetworkResponseStates.Error -> {
                    progressDialog?.cancel()
                    Toast.makeText(this,"Data fetched successfully: $response",Toast.LENGTH_LONG).show()
                    Log.d("MainActivity", "error in fetching data: $response")
                }

                is NetworkResponseStates.Loading -> {
                    progressDialog?.show()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.responseLiveData.removeObservers(this)
    }

    private fun inItProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Loading...")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.setCancelable(false)
    }

}