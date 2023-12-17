package com.example.storyapp.addstory

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.customview.Helper
import com.example.storyapp.dashboard.MainActivity
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.location.ChooseLocationActivity
import com.example.storyapp.viewmodel.DetailStoryViewModel
import com.example.storyapp.viewmodel.SettingModelFactory
import com.example.storyapp.viewmodel.SettingPreferences
import com.example.storyapp.viewmodel.SettingViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import com.example.storyapp.viewmodel.dataStore
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var token: String? = null
    private var getFile: File? = null
    private var storyViewModel: DetailStoryViewModel? = null
    private var latlon: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getToken()
        getPhoto()
        uploadStory()

        binding.btnAddLoc.setOnClickListener {
            val intent = Intent(this, ChooseLocationActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    private fun getToken() {
        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(this, SettingModelFactory(pref))[SettingViewModel::class.java]
        storyViewModel = ViewModelProvider(this, ViewModelFactory(application))[DetailStoryViewModel::class.java]

        settingViewModel.getUserTokens().observe(this) {
            token = StringBuilder("Bearer ").append(it).toString()
        }
    }

    // Define an ActivityResultLauncher with StartActivityForResult contract
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // If the result is OK, extract data from the intent
                result.data?.let { data ->
                    val address = data.getStringExtra("address")
                    val lat = data.getDoubleExtra("lat", 0.0)
                    val lon = data.getDoubleExtra("lon", 0.0)
                    latlon = LatLng(lat, lon)

                    binding.detailLocation.text = address
                }
            }
        }

    private fun getPhoto() {
        getFile = intent?.getSerializableExtra(EXTRA_CAMERAX_IMAGE) as File
        val isFromBackCamera = intent?.getBooleanExtra(EXTRA_CAMERAX_MODE, true) as Boolean
        val resultBitmap = Helper.rotateBitmap(
            BitmapFactory.decodeFile(getFile!!.path),
            isFromBackCamera
        )

        binding.imgAddStory.setImageBitmap(resultBitmap)
        storyViewModel?.let {
            it.addStory.observe(this) {
                if(true) {
                    Toast.makeText(this, getString(R.string.selected_photo), Toast.LENGTH_SHORT).show()
                }
            }
            it.isLoading.observe(this) {isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun uploadStory() {
        binding.btnAddStory.setOnClickListener {

            val desc = binding.descAddStory.text.toString().trim()
            if (desc.isEmpty()) {
                binding.descAddStory.error = resources.getString(R.string.please_filled_story_description)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val file = getFile as File
                val requestImageFile =
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                val descPart = desc.toRequestBody("text/plain".toMediaType())

                storyViewModel?.addStory(
                    token!!,
                    imageMultipart,
                    descPart,
                    latlon?.latitude,
                    latlon?.longitude)
                //if upload success go to dashboard
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val EXTRA_CAMERAX_IMAGE = "CameraX Image"
        const val EXTRA_CAMERAX_MODE = "CameraX_Mode"
    }
}