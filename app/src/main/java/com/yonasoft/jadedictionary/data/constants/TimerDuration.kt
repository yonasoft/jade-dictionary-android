package com.yonasoft.jadedictionary.data.constants

sealed class TimerDuration(val time: String, val durationInMillis: Long?) {
    data object None: TimerDuration(time = "None", durationInMillis = null)
    data object ThreeSeconds: TimerDuration(time ="3 seconds", durationInMillis = 3000L)
    data object FiveSeconds: TimerDuration(time = "5 seconds", durationInMillis = 5000L)
    data object TenSeconds: TimerDuration(time = "10 seconds", durationInMillis = 10000L)
    data object FifteenSeconds: TimerDuration(time = "15 seconds", durationInMillis = 15000L)
    data object ThirtySeconds: TimerDuration(time = "30 seconds", durationInMillis = 30000L)
    data object OneMinute: TimerDuration(time = "1 minute", durationInMillis = 60000L)
}