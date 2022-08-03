package com.InSoft.social.daos

import com.InSoft.social.modals.Post
import com.InSoft.social.modals.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class PostDao {
    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("Posts")
    val auth = Firebase.auth
    fun addPost(text:String){
        val currentUserid = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = userDao()
            val user = userDao.getUserbyId(currentUserid).await().toObject(User::class.java)!!
            val time = System.currentTimeMillis()
            val post = Post(text, user,time)
            postCollection.document().set(post)
        }

    }
    fun getPostbyId(postId: String):Task<DocumentSnapshot>{
        return postCollection.document(postId).get()
    }
   fun updatelikes(postId:String){
       val currentuserid = auth.currentUser!!.uid
       GlobalScope.launch {
           val post = getPostbyId(postId).await().toObject(Post::class.java)
           val isLiked =  post!!.likedBy.contains(currentuserid)
           if (isLiked){
               post.likedBy.remove(currentuserid)
           }
           else{
               post.likedBy.add(currentuserid)
           }
           postCollection.document(postId).set(post)
       }
   }
    fun deletePost(postId:String){
        GlobalScope.launch {
            postCollection.document(postId).delete()
        }
    }


}