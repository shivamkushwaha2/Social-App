package com.InSoft.social.modals

data class Post (val text:String = "",
                 val imageUrl: String = "", // New field to store the image URl
                 val createdby:User = User(),
                 val createdat: Long = 0L,
                 val likedBy:ArrayList<String> = ArrayList())