package com.githukudenis.feature_weather_info.common

data class UserMessage(
    val id: Int? = null,
    val description: String? = null,
    val messageType: MessageType
)

enum class MessageType {
    STANDARD,
    ERROR
}