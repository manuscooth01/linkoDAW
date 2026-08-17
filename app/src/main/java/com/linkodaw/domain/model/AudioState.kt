package com.linkodaw.domain.model

sealed interface AudioState {
    data class Idle(val message: String = "Listo") : AudioState
    data class Recording(val currentDuration: Long, val amplitude: Float) : AudioState
    data class Playing(val currentPosition: Long, val totalDuration: Long) : AudioState
    data class Paused(val currentPosition: Long, val totalDuration: Long) : AudioState
    data class Error(val message: String, val throwable: Throwable? = null) : AudioState
    data class PermissionRequired(val permission: String) : AudioState
}