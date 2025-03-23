package com.musashi.library.common

data class PageBean<T>(
    val total: Long,
    val items: List<T> = emptyList()
)
