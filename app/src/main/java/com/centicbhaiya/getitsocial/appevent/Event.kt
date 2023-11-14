package com.centicbhaiya.getitsocial.appevent

open class Event(type: EventType)

class ConnectionAvailable() : Event(EventType.CONNECTION_AVAILABLE)
class ConnectionLost() : Event(EventType.CONNECTION_LOST)


