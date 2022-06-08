package com.elchinasgarov.kidsdrawingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elchinasgarov.DrawingView
import com.elchinasgarov.kidsdrawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.setSizeForBrush(20.toFloat())


    }
}