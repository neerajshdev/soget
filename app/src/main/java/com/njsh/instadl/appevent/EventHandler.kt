package com.njsh.instadl.appevent

import com.njsh.instadl.appevent.Event

interface EventHandler
{
    fun handleEvent(event: Event)
}