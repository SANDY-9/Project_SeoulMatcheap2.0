package com.sandy.matcheap.domain.model.store

data class StoreItem(
    val id: String = "",
    val code: String = "",
    val	name: String = "",
    val	address: String = "",
    val	photo: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val curLat: Double = 0.0,
    val curLng: Double = 0.0,
    val d: Float = 0.0f
)