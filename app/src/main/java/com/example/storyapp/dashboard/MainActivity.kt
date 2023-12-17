package com.example.storyapp.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.storyapp.R
import com.example.storyapp.addstory.CameraActivity
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.location.MapsActivity

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragmentHome = HomeFragment()
    private val fragmentSettings = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNav()
        switchNav(fragmentHome)
    }

    private fun switchNav(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun bottomNav() {
        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> switchNav(fragmentHome)
                R.id.nav_settings -> switchNav(fragmentSettings)
                R.id.nav_add -> directToCam()
                R.id.nav_loc -> directToLoc()
            }
            true
        }
    }

    private fun directToCam() {
        if (!allPermissionsGranted()) {
            reqPermission()
        } else {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    private fun directToLoc() {
        if (!allPermissionsGranted()) {
            reqPermission()
        } else {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun reqPermission() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}