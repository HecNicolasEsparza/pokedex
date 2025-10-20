package com.example.data

import com.google.gson.annotations.SerializedName

class sprites(
    @SerializedName("front_default")
    var frontDefault: String?,
){
    override fun equals(other: Any?): Boolean{
        return super.equals(other)
    }
    override fun hashCode(): Int{
        return super.hashCode()
    }
    override fun toString(): String{
        return super.toString()
    }
}