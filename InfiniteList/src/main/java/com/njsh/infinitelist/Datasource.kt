package com.njsh.infinitelist

import android.util.Log

private const val TAG = "Datasource"
abstract class Datasource<T> {
    private var maxSize: Int = 20
    private var size: Int = 0

    var rightNode: Node<T>? = null
    var left: Node<T>? = null
    var headNode: Node<T>? = null
    var tailNode: Node<T>? = null


    fun setMaxCacheSize(size: Int) {
        assert(size > 0)
        this.size = size
    }


    fun createFirst(): Node<T> {
        val data: T = onCreate(0)!!
        headNode = Node(0, data)
        tailNode = headNode
        left = headNode
        rightNode = headNode
        size++
        return headNode!!
    }

    fun createNext(node: Node<T>): Node<T>? {
        val nextIndex = node.index.inc()
        val data = onCreate(nextIndex)
        var newNode: Node<T>? = null
        if (data != null) {
            newNode = node.addNext(nextIndex, data)
            tailNode = newNode
            size++

            // drop extra node
            if (size > maxSize) {
                if (headNode != left) {
                    headNode = headNode?.leaveLeft()
                    size--
                }
            }
        }

        return newNode
    }


    fun createPrev(node: Node<T>): Node<T>? {
        val prevIndex = node.index - 1
        var newNode: Node<T>? = null
        if (!(prevIndex < 0)) {
            val data = onCreate(prevIndex)
            if (data != null) {
                newNode = node.addPrev(prevIndex, data)
                headNode = newNode
                size++

                if (size > maxSize) {
                    if (rightNode != tailNode) {
                        tailNode = tailNode?.leaveRight()
                        size--
                    }
                }
            }
        }

        return newNode
    }

    /**
     * Return data at index n.
     * You should return null if you want to end this list.
     * This function will be called every time when user scroll through the list.
     * n can be between 0 to last possible number.
     *
     * it is required to not give a null for index 0
     */
    abstract fun onCreate(index: Int): T?

    abstract fun nextOf(from: T): List<T>
    abstract fun prevOf(from: T): List<T>

    fun printNodes() {
        printNodes(headNode!!, tailNode!!)
        printNodes(left!!, rightNode!!)
    }

    private fun printNodes(from: Node<T>, to: Node<T>) {
        val stringBuilder = StringBuilder()
        var current = from

        while (current != to) {
            stringBuilder.append("node(${current.index}) \u2190 ")
            current = current.next!!
        }
        stringBuilder.append("$to")
        Log.d(TAG, "printNodes: (${from.index} to ${to.index}): $stringBuilder")
    }
}