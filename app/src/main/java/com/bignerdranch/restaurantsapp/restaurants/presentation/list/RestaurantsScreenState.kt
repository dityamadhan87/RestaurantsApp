package com.bignerdranch.restaurantsapp.restaurants.presentation.list

import com.bignerdranch.restaurantsapp.restaurants.domain.Restaurant

data class RestaurantsScreenState(
    val restaurants: List<Restaurant>,
    val isLoading: Boolean,
    val error: String? = null
)