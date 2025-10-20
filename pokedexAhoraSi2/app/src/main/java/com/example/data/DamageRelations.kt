package com.example.data

class DamageRelations(
    val double_damage_from: List<DamageType>,
    val half_damage_from: List<DamageType>,
    val no_damage_from: List<DamageType>
) {
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