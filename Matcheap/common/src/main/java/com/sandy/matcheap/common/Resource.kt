package com.sandy.matcheap.common

sealed class Resource<T> (
    val data: T? = null,
    val message: String? = null
) {

    class Success<T>(data: T?): Resource<T>(data = data)
    class Error<T>(message: String? = null): Resource<T>(message = message)
    class Loading<T>(data: T? = null): Resource<T>(data = data)

}