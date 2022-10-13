package com.njsh.instadl.util

import android.content.Context
import com.njsh.instadl.Application


/**
 * A Singleton object to create unique sequential ids
 */
class UniqueId
{
    private var stateValue: Long

    companion object
    {
        private val uniqueId by lazy { UniqueId() }

        fun getUniqueId(): Long
        {
            return uniqueId.getId()
        }

        fun saveState()
        {
            Application.getAppContext().getSharedPreferences("counter-state", Context.MODE_PRIVATE)
                .edit().putLong("state-value", uniqueId.stateValue)
                .apply()
        }
    }


    init
    {
        val ctx = Application.getAppContext()
        stateValue = ctx.getSharedPreferences("counter-state", Context.MODE_PRIVATE).getLong("state-value", 0)
    }

    fun getId() : Long
    {
        return stateValue++
    }
}