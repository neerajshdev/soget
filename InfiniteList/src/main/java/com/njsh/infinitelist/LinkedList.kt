package com.njsh.infinitelist


class LinkedList<T : Any> {
    class Node<T>(
        val value: T,
        var next: Node<T>? = null,
        var prev: Node<T>? = null,
    )

    class Anchor<T>(var pointer: Node<T>) {
        private var t: Node<T> = pointer

        fun get(): T {
            return pointer.value
        }

        fun next(): Boolean {
            t = t.next ?: return false
            return true
        }

        fun reset() {
            t = pointer
        }

        fun new(): Anchor<T> {
            val a = Anchor(pointer)
            a.t = t
            return a
        }
    }

    var head: Node<T>? = null
    var tail: Node<T>? = null
    var size: Int = 0

    fun getAnchor(): Anchor<T> {
        return if (size > 0) Anchor(head!!) else throw java.lang.RuntimeException("list is empty")
    }

    fun add(value: T) {
        val node = Node(value)
        if (size == 0) {
            head = node
            tail = node
        } else {
            tail?.next = node
            node.prev = tail
            tail = node
        }
        size++
    }

    fun addFront(value: T) {
        val node = Node(value)
        if (size == 0) {
            head = node
            tail = node
        } else {
            head?.prev = node
            node.next = head
            head = node
        }
        size++
    }

    fun remove(): T {
        if (size > 0) {
            val removedElemt = head
            head = head?.next
            head?.prev = null
            size--
            return removedElemt!!.value
        }
        throw RuntimeException("No elements to remove")
    }


    fun removeFront(): T {
        if (size > 0) {
            val removedElemt = tail
            tail = tail?.prev
            tail?.next = null
            size--
            return removedElemt!!.value
        }
        throw RuntimeException("No elements to remove")
    }


    fun print() {
        var p = head
        while (p != null) {

            print("=>(${p.value})")
            p = p.next
        }
        println()
        p = tail
        while (p != null) {
            print("=>(${p.value})")
            p = p.prev
        }
    }


    fun main(args: Array<String>) {
        val list = LinkedList<Int>()
        list.add(10)
        list.addFront(9)
        list.print()
    }
}