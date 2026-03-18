package com.dream.homeset.core.model

import com.google.gson.annotations.SerializedName

data class UnsplashUser(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String
)
