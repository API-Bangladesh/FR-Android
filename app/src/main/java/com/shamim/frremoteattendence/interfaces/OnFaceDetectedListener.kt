package com.shamim.frremoteattendence.interfaces

interface OnFaceDetectedListener
{

    fun onFaceDetected(isDetected: Boolean?)

    fun onMultipleFaceDetected()
}