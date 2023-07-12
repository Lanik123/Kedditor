package ru.lanik.kedditor.model.path

import ru.lanik.network.constants.DefaultPostSort

data class PostPath(
    val mainSrc: String,
    val sortType: DefaultPostSort,
) {
    fun toPathStr(): String {
        return "$mainSrc/${sortToStr()}"
    }

    fun sortToStr(): String {
        return sortType.name.lowercase()
    }
}