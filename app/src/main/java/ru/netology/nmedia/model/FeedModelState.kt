package ru.netology.nmedia.model

data class FeedModelState(
    val error: Boolean = false,
    val loading: Boolean = false,
    val refreshing: Boolean = false
)