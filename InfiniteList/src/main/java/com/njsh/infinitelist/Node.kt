package com.njsh.infinitelist

data class Node<T>(
    val index: Int,
    val data: T,
    var next: Node<T>? = null,
    var prev: Node<T>? = null
) {
    fun addNext(index: Int, data: T): Node<T> {
        val newNode = Node(index, data, next, this)
        this.next = newNode
        return newNode
    }

    fun addPrev(index: Int, data: T): Node<T> {
        val newNode = Node(index, data, this, prev)
        this.prev = newNode
        return newNode
    }

    fun leaveLeft(): Node<T> {
        var node = this
        if (node.next != null) {
            node = node.next!!
            node.prev = null
        }
        return node
    }

    fun leaveRight(): Node<T>? {
        var node = this
        if (node.prev != null) {
            node = node.prev!!
            node.next = null
        }
        return node
    }
}
