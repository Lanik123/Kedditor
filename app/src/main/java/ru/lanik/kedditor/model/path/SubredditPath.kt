package ru.lanik.kedditor.model.path

import ru.lanik.network.constants.DefaultSubredditSource

data class SubredditPath(
    val mainSrc: String,
) {
    companion object {
        fun fromEnum(src: DefaultSubredditSource): SubredditPath {
            return SubredditPath(src.name.lowercase())
        }
    }
}