package com.centicbhaiya.getitsocial.appevent

import java.util.concurrent.Executors

class EventManager
{
    companion object
    {
        private var instance: EventManager? = null

        fun getInstance(): EventManager
        {
            if (instance == null)
            {
                instance = EventManager()
            }
            return instance!!
        }
    }


    private val eventHandlers: MutableList<EventHandler> = mutableListOf()
    private val executor = Executors.newSingleThreadExecutor()

    fun fire(event: Event)
    {
        eventHandlers.forEach { eventHandler ->
            executor.submit {
                eventHandler.handleEvent(event)
            }
        }
    }


    @Synchronized
    fun addHandler(handler: EventHandler)
    {
        eventHandlers.add(handler)
    }

    @Synchronized
    fun removeHandler(handler: EventHandler)
    {
        eventHandlers.remove(handler)
    }
}