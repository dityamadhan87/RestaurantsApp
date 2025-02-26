package com.bignerdranch.restaurantsapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.bignerdranch.restaurantsapp.restaurants.DummyContent
import com.bignerdranch.restaurantsapp.restaurants.presentation.Description
import com.bignerdranch.restaurantsapp.restaurants.presentation.list.RestaurantsScreen
import com.bignerdranch.restaurantsapp.restaurants.presentation.list.RestaurantsScreenState
import com.bignerdranch.restaurantsapp.ui.theme.RestaurantsAppTheme
import org.junit.Rule
import org.junit.Test

class RestaurantsScreenTest {
    @get:Rule
    val testRule: ComposeContentTestRule = createComposeRule()

    @Test
    fun initialState_isRendered() {
        testRule.setContent {
            RestaurantsAppTheme {
                RestaurantsScreen(
                    state = RestaurantsScreenState(
                        restaurants = emptyList(),
                        isLoading = true
                    ),
                    onFavoriteClick =
                    { _: Int, _: Boolean -> },
                    onItemClick = { }
                )
            }
        }
        testRule.onNodeWithContentDescription(
            Description.RESTAURANTS_LOADING
        ).assertIsDisplayed()
    }

    @Test
    fun stateWithContent_isRendered() {
        val restaurants = DummyContent.getDomainRestaurants()
        testRule.setContent {
            RestaurantsAppTheme {
                RestaurantsScreen(
                    state = RestaurantsScreenState(
                        restaurants = restaurants,
                        isLoading = false
                    ),
                    onItemClick = {},
                    onFavoriteClick = { _: Int, _: Boolean -> }
                )
            }
        }
        testRule.onNodeWithText(restaurants[0].title)
            .assertIsDisplayed()
        testRule.onNodeWithText(restaurants[0].description)
            .assertIsDisplayed()
        testRule.onNodeWithContentDescription(
            Description.RESTAURANTS_LOADING
        ).assertDoesNotExist()
    }

    @Test
    fun stateWithContent_ClickOnItem_isRegistered() {
        val restaurants = DummyContent.getDomainRestaurants()
        val targetRestaurant = restaurants[0]
        testRule.setContent {
            RestaurantsAppTheme {
                RestaurantsScreen(
                    state = RestaurantsScreenState(
                        restaurants = restaurants,
                        isLoading = false
                    ),
                    onFavoriteClick = { _, _ -> },
                    onItemClick = { id ->
                        assert(id == targetRestaurant.id)
                    }
                )
            }
        }
        testRule.onNodeWithText(targetRestaurant.title)
            .performClick()
    }
}