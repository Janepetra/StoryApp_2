package com.storyapp.viewmodel

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.util.Log.isLoggable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.espresso.core.internal.deps.guava.base.Joiner.on
import com.example.storyapp.adapter.ListStoryAdapter
import com.example.storyapp.db.local.entity.Story
import com.example.storyapp.viewmodel.DetailStoryViewModel
import com.storyapp.utils.DummyData
import com.storyapp.utils.DummyData.generateDummyListStory
import com.storyapp.utils.DummyData.generateDummyNewStories
import com.storyapp.utils.DummyData.generateDummyToken
import com.storyapp.utils.MainDispatcherRule
import com.storyapp.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var viewModel : DetailStoryViewModel

    private val dummyToken = generateDummyToken()

    @Test
    fun `Get stories successfully`() = runTest {
        val dummyStories = generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(viewModel.getPagingStory(dummyToken)).thenReturn(stories)

        val actualStories = viewModel.getPagingStory(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.ListStoryDiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        verify(viewModel).getPagingStory(dummyToken)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class PagedTestDataSource :
        PagingSource<Int, LiveData<List<Story>>>() {

        companion object {
            fun snapshot(items: List<Story>): PagingData<Story> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }

    }

    @ExperimentalCoroutinesApi
    class CoroutinesTestRule(
        val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {

        override fun starting(description: Description?) {
            super.starting(description)
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description?) {
            super.finished(description)
            Dispatchers.resetMain()
        }
    }

    object Log {
        fun d(tag: String, msg: String): Int {
            println("DEBUG: $tag: $msg")
            return 0
        }

        fun i(tag: String, msg: String): Int {
            println("INFO: $tag: $msg")
            return 0
        }

        fun w(tag: String, msg: String): Int {
            println("WARN: $tag: $msg")
            return 0
        }

        fun e(tag: String, msg: String): Int {
            println("ERROR: $tag: $msg")
            return 0
        }
    }
}

//@RunWith(MockitoJUnitRunner::class)
//class MainViewModelTest {
//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//
//    @ExperimentalCoroutinesApi
//    @get:Rule
//    var mainDispatcherRule = MainDispatcherRule()
//
//    private lateinit var viewModel: DetailStoryViewModel
//
////    @Mock
////    private var mockFile = File("fileName")
//
//    @Before
//    fun setUp() {
//        viewModel = mock(DetailStoryViewModel::class.java)
//    }
//
//    // get story
//    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
//    @Test
//    fun `verify getStory is working and Should Not Return Null`() = runTest {
//        val noopListUpdateCallback = NoopListCallback()
//        val dummyStory = generateDummyNewStories()
//        val data = PagedTestDataSources.snapshot(dummyStory)
//        val story = MutableLiveData<PagingData<Story>>()
//        val token = generateDummyToken()
//        story.value = data
//        `when`(viewModel.getPagingStory(token)).thenReturn(story)
//        val realData = viewModel.getPagingStory(token).getOrAwaitValue()
//
//        val differ = AsyncPagingDataDiffer(
//            diffCallback = ListStoryAdapter.ListStoryDiffCallback(),
//            updateCallback = noopListUpdateCallback,
//            mainDispatcher = Dispatchers.Unconfined,
//            workerDispatcher = Dispatchers.Unconfined,
//        )
//        differ.submitData(realData)
//
//        advanceUntilIdle()
//        Mockito.verify(viewModel).getPagingStory(token)
//        Assert.assertNotNull(differ.snapshot())
//        assertEquals(dummyStory.size, differ.snapshot().size)
//        assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
//    }
//
//    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
//    @Test
//    fun `when GetStory is Empty Should Not return Null`() = runTest {
//        val noopListUpdateCallback = NoopListCallback()
//        val data = PagedTestDataSources.snapshot(listOf())
//        val story = MutableLiveData<PagingData<Story>>()
//        val token = generateDummyToken()
//        story.value = data
//        `when`(viewModel.getPagingStory(token)).thenReturn(story)
//        val actualData = viewModel.getPagingStory(token).getOrAwaitValue()
//
//        val differ = AsyncPagingDataDiffer(
//            diffCallback = ListStoryAdapter.ListStoryDiffCallback(),
//            updateCallback = noopListUpdateCallback,
//            mainDispatcher = Dispatchers.Unconfined,
//            workerDispatcher = Dispatchers.Unconfined,
//        )
//        differ.submitData(actualData)
//
//        advanceUntilIdle()
//        Mockito.verify(viewModel).getPagingStory(token)
//        Assert.assertNotNull(differ.snapshot())
//        Assert.assertTrue(differ.snapshot().isEmpty())
//        print(differ.snapshot().size)
//    }
//
//    class NoopListCallback : ListUpdateCallback {
//        override fun onChanged(position: Int, count: Int, payload: Any?) {}
//        override fun onMoved(fromPosition: Int, toPosition: Int) {}
//        override fun onInserted(position: Int, count: Int) {}
//        override fun onRemoved(position: Int, count: Int) {}
//    }
//
//    class PagedTestDataSources private constructor() :
//        PagingSource<Int, LiveData<List<Story>>>() {
//        companion object {
//            fun snapshot(items: List<Story>): PagingData<Story> {
//                return PagingData.from(items)
//            }
//        }
//
//        override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
//            return 0
//        }
//
//        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
//            return LoadResult.Page(emptyList(), 0, 1)
//        }
//    }
//}