package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(
    substr: String,
    ignoreCase: Boolean = true
): List<Int> {
    if (isNullOrEmpty() || substr.isEmpty())
        return emptyList()

    var startIdx = 0
    var findResult = -1
    val result = mutableListOf<Int>()
    do {
        findResult = indexOf(substr, startIdx, ignoreCase)
        if (findResult != -1) {
            result.add(findResult)
            startIdx = findResult + 1
        }
    } while (findResult != -1)
    return result
}