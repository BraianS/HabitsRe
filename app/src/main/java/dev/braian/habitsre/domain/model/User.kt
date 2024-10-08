package dev.braian.habitsre.domain.model

data class User (
    val userId: String,
    val username: String,
    val profilePictureUrl: String?
)