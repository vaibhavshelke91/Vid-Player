package com.vaibhav.vplayer.model

data class FolderModel(
    val id : Long =0,
    val name : String="",
    var count : Int = 0,
    val created : Long=0,
    val size : Long=0
){
    override fun equals(other: Any?): Boolean {
        if (other is FolderModel){
           return other.id==this.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return this.id.toInt()
    }

    override fun toString(): String {
        return "{Id : ${id}, Name : ${name}, Count : ${count}}"
    }
}
