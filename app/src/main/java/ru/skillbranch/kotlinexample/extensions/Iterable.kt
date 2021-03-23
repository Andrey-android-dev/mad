package ru.skillbranch.kotlinexample.extensions

/**
 * Type description here....
 *
 * Created by Andrey on 21.03.2021
 */
fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    if (!isEmpty()) {
        val iterator = listIterator(size)
        while (iterator.hasPrevious()) {
            if (predicate(iterator.previous())) {
                return take(iterator.nextIndex())
            }
        }
    }
    return emptyList()
}





