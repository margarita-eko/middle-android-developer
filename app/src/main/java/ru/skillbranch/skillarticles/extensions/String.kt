package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(
    substr: String,
    ignoreCase: Boolean = true
): List<Int> {
    if (this == null || substr.isBlank()) return emptyList()
    var startIndex = 0
    val result = mutableListOf<Int>()
    var currentSearchResult: Int
    do {
        currentSearchResult = this.indexOf(substr, startIndex, ignoreCase)
        if (currentSearchResult != -1) {
            result.add(currentSearchResult)
        }
        startIndex = currentSearchResult + 1
    } while (currentSearchResult != -1)
    return result
}