package com.InSoft.social

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.InSoft.social.daos.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    lateinit var postDao: PostDao
        var a: Uri? = null
    private val PICK_IMAGE_REQUEST = 1 // a constant to identify the request code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        pickimg.setOnClickListener {


            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)

        }


        post.setOnClickListener {
            postDao = PostDao()
            val input = posttext.text.toString().trim()
            if (input.isNotEmpty()){
            postDao.addPost(input,a)
            finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data // the uri of the selected image
            // now you can upload the image to Firebase Storage or display it in an ImageView
       a = imageUri

            if (imageUri != null) {
                contentResolver.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

        }
    }

}