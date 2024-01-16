package ru.netology.nmedia.activity.functions

fun truncateText(text: String, maxLength: Int): String {
    if (text.length <= maxLength) return text
    return text.substring(0, maxLength) + "..."
}
