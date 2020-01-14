package com.example.weatherapp.view

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.R

class MainActivity : AppCompatActivity() {

    private val fragment1: Fragment = FragmentOne()
    private val fragment2: Fragment = FragmentTwo()
    private val fragment3: Fragment = FragmentThree()
    private val fm: FragmentManager = supportFragmentManager
    private var active = fragment1

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                print("1")
                changeFragment(fragment1)
//                changeFragment(FragmentOne())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                print("2")
                changeFragment(fragment2)
//                changeFragment(FragmentTwo())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                Log.e("...", "3")
                changeFragment(fragment3)
//                changeFragment(FragmentThree())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun changeFragment(fragment : Fragment){
        fm.beginTransaction().hide(active).show(fragment).commit()
        active = fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        fm.beginTransaction().add(R.id.fragmentContainer, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.fragmentContainer, fragment1, "3").commit()

//        changeFragment(FragmentOne())
    }
}
