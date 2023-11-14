package com.gd.infinitelist


class LinkedList<T>(
    val value: T, val pos: Int
) {
    var next: LinkedList<T>? = null
    var prev: LinkedList<T>? = null

    companion object {
        fun <T> with(initialValue: T): LinkedList<T> {
            return LinkedList(initialValue, 0).apply {
                info = Info(this, this)
            }
        }

        fun <T> fromList(items : List<T>): LinkedList<T> {
            val list = LinkedList(items[0], 0).apply { info = Info(this, this) }
            for (i in 1 .. items.lastIndex) {
                list.add(items[i])
            }
            return list
        }
    }

    private lateinit var info: Info<T>
    val size: Int get() = info.size
    val head: LinkedList<T> get() = info.head
    val tail: LinkedList<T> get() = info.tail

    internal class Info<T>(
        var head: LinkedList<T>, var tail: LinkedList<T>, var size: Int = 1
    )

    fun add(value: T) {
        val end = info.tail
        val new = LinkedList(value, this.tail.pos + 1)
        new.info = info
        end.next = new
        new.prev = end
        info.tail = new
        info.size++
    }

    fun addAll(items: List<T>) {
        for (item in items) {
            add(item)
        }
    }


    fun addFront(value: T) {
        val front = info.head
        val new = LinkedList(value, this.head.pos - 1)
        new.info = info
        new.next = front
        front.prev = new
        info.head = new
        info.size++
    }

    fun addAllFront(items: List<T>) {
        for (item in items.asReversed()) {
            addFront(item)
        }
    }

    fun remove(): T {
        if (this === info.tail) {
            throw RuntimeException("Cannot remove because this is the last element you are calling remove. To avoid this you can check by calling isEnd() before remove.")
        }
        val end = info.tail
        val p = end.prev!!
        p.next = null
        end.prev = null
        info.tail = p
        info.size--
        return end.value
    }


    fun removeFront(): T {
        if (this === info.head) {
            throw RuntimeException("Cannot remove because this is the head element you are trying to remove. To avoid this you can check by calling isHead() before remove.")
        }
        val f = info.head
        val n = f.next!!
        f.next = null
        n.prev = null
        info.head = n
        info.size--
        return f.value
    }

    fun format(): String {
        var f = info.head
        val stringBuilder = StringBuilder()
        stringBuilder.append("(${f.pos})")
        while (!f.isEnd()) {
            f = f.next!!
            stringBuilder.append(" => (${f.pos})")
        }
        return stringBuilder.toString()
    }

    fun formatReverse(): String {
        var e = info.tail
        val stringBuilder = StringBuilder()
        stringBuilder.append("(${e.pos})")
        while (!e.isFront()) {
            e = e.prev!!
            stringBuilder.append(" => (${e.pos})")
        }
        return stringBuilder.toString()
    }

    fun isEnd(): Boolean {
        return this === info.tail
    }

    fun isFront(): Boolean {
        return this === info.head
    }

    inline fun moveNext(block: (LinkedList<T>) -> Unit): Boolean{
        if (!this.isEnd()) {
            block(next!!)
            return true
        }
        return false
    }


    fun movePrev(block: (LinkedList<T>) -> Unit): Boolean {
        if (!this.isFront()) {
            block(prev!!)
            return true
        }
        return false
    }
}


fun <T> LinkedList<T>.isCloseToEnd(closeDis: Int = 5): Boolean = tail.pos - pos <= closeDis

fun <T> LinkedList<T>.isCloseToHead(closeDis: Int = 5): Boolean = pos - head.pos <= closeDis