package com.example.evetbuspatt

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evetbuspatt.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Nuevo
        binding = ActivityMainBinding.inflate(layoutInflater)

        //Esto viene por defecto y es mejor colocarlo dado que sin esto traspasa los bordes normales
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(binding.root)

        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()
        setupSubscribers()
        /*Solo para pruebas antes de eventbus
        getResultEventsInRealtime().forEach {
            if(it is SportEvent.ResultSuccess){
                adapter.add(it)
            }
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

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            adapter.clear()
            getEvents()
            binding.btnAd.visibility = View.VISIBLE
        }
    }

    private fun setupSubscribers() {
        lifecycleScope.launch {
            EventBus.instance().subscribe<SportEvent> { event ->
                binding.srlResults.isRefreshing = false
                when (event) {
                    is SportEvent.ResultSuccess ->
                        adapter.add(event)

                    is SportEvent.AdEvent ->
                        Toast.makeText(
                            this@MainActivity,
                            "Ad click. Send data to server---",
                            Toast.LENGTH_SHORT
                        ).show()

                    is SportEvent.ClosedAdEvent ->
                        binding.btnAd.visibility = View.GONE
                    else -> {}
                }
            }
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    val events = getAdEventsInRealtime()
                    EventBus.instance().publish(events.first())
                }
            }
            setOnLongClickListener { view ->
                lifecycleScope.launch {
                    binding.srlResults.isRefreshing = true
                    EventBus.instance().publish(SportEvent.ClosedAdEvent)

                }
                true
            }

        }
    }

    private fun getEvents() {
        lifecycleScope.launch {
            val events = getResultEventsInRealtime()
            events.forEach { event ->
                delay(someTime())
                EventBus.instance().publish(event)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.srlResults.isRefreshing = true
        getEvents()
    }

    //OnClickListener interface
    override fun onClick(result: SportEvent.ResultSuccess) {

    }
}