package ru.netology.nmedia.activity.functions

//преобразование формата представления кол-ва
fun formatAmount(amount: Int): String {
    return when {
        amount < 1_000 -> amount.toString()
        amount < 10_000 -> "${amount / 1_000}.${(amount % 1_000) / 100}K"
        amount < 1_000_000 -> "${amount / 1_000}K"
        else -> "${amount / 1_000_000}.${(amount % 1_000_000) / 100_000}M"
    }
}