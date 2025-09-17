package com.dicoding.asclepius.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.room.PredictionDatabase
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.room.HistoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchHistoryData()
    }

    private fun fetchHistoryData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val historyList = PredictionDatabase.getDatabase(applicationContext)
                .predictionHistoryDao()
                .getAllHistory()
            withContext(Dispatchers.Main) {
                binding.historyRecyclerView.adapter = HistoryAdapter(historyList) { id ->
                    deleteHistoryItem(id)
                }
            }
        }
    }

    private fun deleteHistoryItem(id: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            PredictionDatabase.getDatabase(applicationContext)
                .predictionHistoryDao()
                .deleteHistoryById(id)

            withContext(Dispatchers.Main) {
                fetchHistoryData()
                Toast.makeText(this@HistoryActivity, "History deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
