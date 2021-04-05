package com.example.movieapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.movieapp.R
import com.example.movieapp.service.ServiceBuilder
import com.example.movieapp.ui.fragments.DiscoverFragment
import com.example.movieapp.ui.fragments.HomeFragment
import com.example.movieapp.ui.fragments.ProfileFragment
import com.google.gson.GsonBuilder
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //set navigator
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fragment = HomeFragment.newInstance()
        addFragment(fragment)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.navigation_home -> {
                val fragment : Fragment =
                    HomeFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_discover -> {
                val fragment : Fragment =
                    DiscoverFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_profile -> {
                val fragment : Fragment =
                    ProfileFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun addFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.layout_content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}
