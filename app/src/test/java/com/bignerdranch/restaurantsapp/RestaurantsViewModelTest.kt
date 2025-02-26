package com.bignerdranch.restaurantsapp

import com.bignerdranch.restaurantsapp.restaurants.DummyContent
import com.bignerdranch.restaurantsapp.restaurants.data.RestaurantsRepository
import com.bignerdranch.restaurantsapp.restaurants.domain.GetInitialRestaurantsUseCase
import com.bignerdranch.restaurantsapp.restaurants.domain.GetSortedRestaurantsUseCase
import com.bignerdranch.restaurantsapp.restaurants.domain.ToggleRestaurantUseCase
import com.bignerdranch.restaurantsapp.restaurants.presentation.list.RestaurantsScreenState
import com.bignerdranch.restaurantsapp.restaurants.presentation.list.RestaurantsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class RestaurantsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun initialState_isProduced() = scope.runTest {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value
        assert(
            initialState == RestaurantsScreenState(
                restaurants = emptyList(),
                isLoading = true,
                error = null
            )
        )
    }

    @Test
    fun stateWithContent_isProduced() = scope.runTest {
        val testVM = getViewModel()
        advanceUntilIdle()
        val currentState = testVM.state.value
        assert(
            currentState == RestaurantsScreenState(
                restaurants =
                DummyContent.getDomainRestaurants(),
                isLoading = false,
                error = null
            )
        )
    }

    @Test
    fun toggleRestaurant_IsUpdatingFavoriteField() =
        scope.runTest {
            // Setup useCase
            val restaurantsRepository = RestaurantsRepository(
                FakeApiService(),
                FakeRoomDao(),
                dispatcher
            )
            val getSortedRestaurantsUseCase =
                GetSortedRestaurantsUseCase(restaurantsRepository)
            val useCase = ToggleRestaurantUseCase(
                restaurantsRepository,
                getSortedRestaurantsUseCase
            )
            // Preload data
            restaurantsRepository.loadRestaurants()
            advanceUntilIdle()
            // Execute useCase
            val restaurants = DummyContent.getDomainRestaurants()
            val targetItem = restaurants[0]
            val isFavorite = targetItem.isFavorite
            val updatedRestaurants = useCase(
                targetItem.id,
                isFavorite
            )
            advanceUntilIdle()
            // Assertion
            restaurants[0] = targetItem.copy(
                isFavorite =
                !isFavorite
            )
            assert(updatedRestaurants == restaurants)
        }

    private fun getViewModel(): RestaurantsViewModel {
        val restaurantsRepository = RestaurantsRepository(
            FakeApiService(), FakeRoomDao(), dispatcher
        )
        val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase(restaurantsRepository)
        val getInitialRestaurantsUseCase = GetInitialRestaurantsUseCase(
            restaurantsRepository, getSortedRestaurantsUseCase
        )
        val toggleRestaurantUseCase =
            ToggleRestaurantUseCase(
                restaurantsRepository,
                getSortedRestaurantsUseCase
            )
        return RestaurantsViewModel(
            getInitialRestaurantsUseCase,
            toggleRestaurantUseCase,
            dispatcher
        )
    }
}