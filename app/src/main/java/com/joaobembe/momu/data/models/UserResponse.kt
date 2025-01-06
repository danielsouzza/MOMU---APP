package com.joaobembe.momu.data.models

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val roles: List<Role>
)