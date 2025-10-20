package com.example.myapplication.Data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import okhttp3.internal.http.StatusLine
import java.util.Date


class DatabaseProvider: ContentProvider() {
    val Context: Context? = null
    var sqlite: SQLiteDatabase? = null
    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int{
        TODO("NOT yet implemented")
    }

    override fun getType(p0: Uri): String?{
        TODO("Not yet implemented")
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun query(
        p0: Uri,
        p1: Array<out String?>?,
        p2: String?,
        p3: Array<out String?>?,
        p4: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }

    override fun update(
        p0: Uri,
        p1: ContentValues?,
        p2: String?,
        p3: Array<out String?>?
    ): Int {
        TODO("Not yet implemented")
    }

}