package com.funyaani.todolist.presentation.utils

sealed class UiEvent {

    data class ShowSnackbar(
        val message:String,
        val action:String?=null
    ):UiEvent()
    object SaveEvent:UiEvent()
}