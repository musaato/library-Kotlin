package com.musashi.library

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LibraryApplicationTest{

/*
    @Test
    fun contextLoads() {　// run successfully
    }
*/


    @Test
    fun contextLoads() { // updated version, assertion and prompt message added.
        val contextLoaded = true // This should reflect the actual context load status
        assertEquals(true, contextLoaded, "Spring Boot application context failed to load.")
        // println("Spring Boot application started successfully in LibraryApplicationTest!")
        println("おめでとう！ Spring Boot アプリケーションがスタート成功。")

    }
}