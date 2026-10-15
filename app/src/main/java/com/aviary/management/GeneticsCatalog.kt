package com.aviary.management

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
        "Fischer's Lovebird" to listOf(
            "Normal",
            "Pastel",
            "Cinnamon",
            "Lutino",
            "Pied",
            "Violet",
            "Dark Factor",
            "Split Pastel",
            "Split Cinnamon"
        ),
        "Peach-faced Lovebird" to listOf(
            "Normal",
            "Blue",
            "Cinnamon",
            "Pied",
            "Violet",
            "Pale",
            "Pastel",
            "Sapphire",
            "Dominant Silver"
        ),
        "Masked Lovebird" to listOf(
            "Normal",
            "Blue",
            "Cinnamon",
            "Pied",
            "Pastel",
            "Lutino",
            "Dark Factor",
            "Violet",
            "Golden"
        ),
        "Nyasa Lovebird" to listOf(
            "Normal",
            "Pastel",
            "Silver",
            "Pied",
            "Cinnamon",
            "Blue",
            "Dark Factor",
            "Violet"
        ),
        "Black-cheeked Lovebird" to listOf(
            "Normal",
            "Blue",
            "Pied",
            "Pastel",
            "Cinnamon",
            "White",
            "Lutino",
            "Violet"
        ),
        "Red-faced Lovebird" to listOf(
            "Normal",
            "Cinnamon",
            "Pied",
            "Pastel",
            "Blue",
            "Lutino",
            "Violet",
            "Dark Factor"
        )
    )

    fun mutationsFor(species: String): List<String> = mutations[species] ?: listOf("Normal")
}
