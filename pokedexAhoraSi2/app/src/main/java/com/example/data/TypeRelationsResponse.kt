package com.example.data

class TypeRelationsResponse(
    val damage_relations: DamageRelations,
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