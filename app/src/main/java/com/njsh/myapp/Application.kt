package com.njsh.myapp

import android.annotation.SuppressLint
import android.content.Context


class Application : android.app.Application()
{
    companion object
    {
        @SuppressLint("StaticFieldLeak")
        private lateinit var ctx: Context
        fun getAppContext(): Context
        {
            return ctx;
        }
    }

    override fun onCreate()
    {
        super.onCreate()
        ctx = this
    }
}