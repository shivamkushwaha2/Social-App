package com.InSoft.social

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.InSoft.social.daos.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        post.setOnClickListener {
            postDao = PostDao()
            val input = posttext.text.toString().trim()
            if (input.isNotEmpty()){
            postDao.addPost(input)
            finish()
            }
        }
    }
}