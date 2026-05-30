package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.db.AppDatabase
import com.example.data.repository.CastingRepository
import com.example.ui.CastingViewModel
import com.example.ui.CastingViewModelFactory
import com.example.ui.DashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge layout activation
        enableEdgeToEdge()
        
        // App Database initialization
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CastingRepository(database.castingPredictionDao())
        
        // Android standard ViewModel instantiation
        val viewModelFactory = CastingViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[CastingViewModel::class.java]
        
        setContent {
            MyApplicationTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
