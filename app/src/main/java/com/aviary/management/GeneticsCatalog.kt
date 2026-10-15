package com.aviary.management

import androidx.annotation.DrawableRes

object GeneticsCatalog {
    val species = listOf(
        "Fischer's Lovebird",
        "Peach-faced Lovebird",
        "Masked Lovebird",
        "Nyasa Lovebird",
        "Black-cheeked Lovebird",
        "Red-faced Lovebird"
    )

    private val mutations = mapOf(
        "Fischer's Lovebird" to listOf("Normal", "Pastel", "Cinnamon", "Lutino", "Pied", "Violet", "Dark Factor"),
        "Peach-faced Lovebird" to listOf("Normal", "Blue", "Pied", "Pastel", "Cinnamon", "Sapphire", "Violet"),
        "Masked Lovebird" to listOf("Normal", "Blue", "Pied", "Pastel", "Cinnamon", "Lutino", "Violet"),
        "Nyasa Lovebird" to listOf("Normal", "Pastel", "Blue", "Cinnamon", "Silver", "Pied"),
        "Black-cheeked Lovebird" to listOf("Normal", "Blue", "Pied", "Pastel", "Cinnamon", "White", "Lutino"),
        "Red-faced Lovebird" to listOf("Normal", "Cinnamon", "Pied", "Pastel", "Blue", "Lutino", "Violet")
    )

    fun mutationsFor(speciesName: String): List<String> = mutations[speciesName] ?: listOf("Normal")
}
