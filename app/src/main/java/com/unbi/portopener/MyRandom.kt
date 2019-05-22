package com.unbi.portopener

import java.util.concurrent.atomic.AtomicInteger

//generating notification ID
private val c = AtomicInteger(0)
fun getID(): Int {
    return c.incrementAndGet()
}