package com.metchevn.codejam.panckakeflipper

import com.metchevn.codejam.solve

fun main(args: Array<String>) {
    solve@ solve(args) {
        println("Solving case " + it)
        var charOrNumber = parseCharOrNumber()
        val pancakes = ArrayList<Boolean>()
        while(charOrNumber.n == null) {
            when(charOrNumber.c) {
                '+' -> pancakes.add(true)
                '-' -> pancakes.add(false)
            }
            charOrNumber = parseCharOrNumber()
        }

        val n = charOrNumber.n ?: throw RuntimeException("Shouldn't happen")
        val flipperSize = n.toInt()
        var index = 0
        var flips = 0
        while (true) {
            if (index >= pancakes.size) {
                return@solve flips.toString()
            }
            if (!pancakes[index]) {
                if (index > pancakes.size - flipperSize) {
                    return@solve "IMPOSSIBLE"
                } else {
                    for (i in 0 until flipperSize)
                    pancakes[index + i] = !pancakes[index + i]
                    flips++
                }
            }
            index++
        }
        throw RuntimeException("shouldn't happen")
    }
}