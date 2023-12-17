package com.storyapp.utils

import com.example.storyapp.db.local.entity.Story
import com.example.storyapp.response.AllStories
import com.example.storyapp.response.LoginResult

object DummyData {

    val dummyLogin = LoginResult(
        userId = "123",
        name = "john_doe",
        token = "abcd1234"
    )

    fun generateDummyNewsEntity(): List<Story> {
        val newsList = ArrayList<Story>()
        for (i in 0..5) {
            val stories = Story(
                "Title $i",
                "BAMBANG SUGENI",
                "This is description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2023-14-20T02:04:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyNewStories(): List<Story> {
        val newsList = ArrayList<Story>()
        for (i in 0..5) {
            val stories = Story(
                "Title $i",
                "Jane Doe",
                "Hope u're doing well",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            newsList.add(stories)
        }
        return newsList
    }

    fun generateDummyStoriesResponse(): AllStories {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )

            listStory.add(story)
        }

        return AllStories(listStory, error, message)
    }

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Jane",
                description = "have a great day, mate!",
                lon = -16.002,
                lat = -10.212
            )

            items.add(story)
        }

        return items
    }

    fun generateDummyToken(): String {
        return "ini token"
    }
}