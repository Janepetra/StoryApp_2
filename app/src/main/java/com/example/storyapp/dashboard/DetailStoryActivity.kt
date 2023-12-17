package com.example.storyapp.dashboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.db.local.entity.Story
import com.example.storyapp.location.LocationConverter
import com.example.storyapp.viewmodel.DetailStoryViewModel

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val storyViewModel: DetailStoryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<Story>(EXTRA_DATA) as Story
        getData(story)
        showLoading()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    //get data
    private fun getData(story: Story) {
        with(binding) {
            Glide.with(this.root.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.ivDetPhoto)
            tvDetName.text = story.name
            tvDetDesc.text = story.description
            tvDetLoc.text = LocationConverter.getStringAddress(
                LocationConverter.toLatlng(story.lat, story.lon),
                this@DetailStoryActivity
            )
        }
    }

    private fun showLoading() {
        storyViewModel?.isLoading?.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}