package ru.netology.nmedia.activity

class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled) {
            return null
        } else {
            hasBeenHandled = true
            return content
        }
    }
}
