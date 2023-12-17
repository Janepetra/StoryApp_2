package com.storyapp.db.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.db.local.entity.Story
import com.example.storyapp.db.remote.StoryRepository
import com.storyapp.utils.DummyData.generateDummyNewsEntity
import com.storyapp.utils.MainDispatcherRule
import com.storyapp.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.io.File

class StoryRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Mock
    private var mockFile = File("fileName")

    private lateinit var repository: StoryRepository

    @Before
    fun setUp() {

        repository = Mockito.mock(StoryRepository::class.java)
    }

    @Test
    fun `verify getStories function is working`() {
        val dummyStories = generateDummyNewsEntity()
        val expectedStories = MutableLiveData<List<Story>>()
        expectedStories.value = dummyStories

        val token = "ini token"
        repository.getListStory(token)
        Mockito.verify(repository).getListStory(token)

        Mockito.`when`(repository.listStory).thenReturn(expectedStories)

        val actualStories = repository.listStory.getOrAwaitValue()

        Mockito.verify(repository).listStory

        Assert.assertNotNull(actualStories)
        Assert.assertEquals(expectedStories.value, actualStories)
        assertEquals(dummyStories.size, actualStories.size)
    }
}
