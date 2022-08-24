package com.udacity


sealed class ButtonState(var customTextButton: Int) {
    object Clicked : ButtonState(R.string.btn_download)
    object Loading : ButtonState(R.string.btn_loading)
    object Completed : ButtonState(R.string.btn_complete)
}