package com.example.evetbuspatt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evetbuspatt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Nuevo
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapter()
        setupRecyclerView()

        /*Solo para pruebas antes de eventbus
        getResultEventsInRealtime().forEach {
            if(it is SportEvent.ResultSuccess){
                adapter.add(it)
            }
        }
         */
        /* Nuevo
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */
    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }


    //OnClickListener interface
    override fun onClick(result: SportEvent.ResultSuccess) {

    }
}