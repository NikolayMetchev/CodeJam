package com.metchevn.codejam

import java.io.FileInputStream
import java.io.FileOutputStream

fun solve(args: Array<String>, solver: Solver.(caseNumber: Long)-> String) {
    val start = System.currentTimeMillis()
    (if (args.isNotEmpty()) FileInputStream(args[0]) else System.`in`).use { inputStream ->
        (if (args.size == 2) FileOutputStream(args[1]) else System.out).use { out ->
            object: Solver(inputStream, out) {
                override fun solve(caseNumber: Long) = solver(caseNumber)
            }.use(Solver::solve)
        }
    }
    println("Done in " + (System.currentTimeMillis() - start) + " milliseconds")
}
