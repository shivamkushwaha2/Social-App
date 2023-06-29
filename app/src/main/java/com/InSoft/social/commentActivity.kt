package com.InSoft.social

import CommentAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.InSoft.social.modals.comment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Comment

private lateinit var database: FirebaseDatabase
private lateinit var commentsRef: DatabaseReference
private lateinit var submit: Button
class commentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)


        FirebaseApp.initializeApp(this)
        commentsRef = FirebaseDatabase.getInstance().getReference("comments")
        val query = FirebaseDatabase.getInstance().getReference("comments")
        val options = FirebaseRecyclerOptions.Builder<comment>().setQuery(query, comment::class.java).build()

        val adapter = CommentAdapter(options)
        cmtRecyclerView.adapter = adapter
        cmtRecyclerView.layoutManager = LinearLayoutManager(this)


        submit = findViewById(R.id.cmtpost)
        database = FirebaseDatabase.getInstance()
        commentsRef = database.getReference("comments")
        val editText = findViewById<EditText>(R.id.cmtposttext)
        submit.setOnClickListener {

            val comment = editText.text.toString().trim()
            val user = FirebaseAuth.getInstance().currentUser

            if (comment.isNotEmpty() && user != null) {
                val commentMap = hashMapOf(
                    "comment" to comment,
                    "userId" to user.uid,
                    "timestamp" to ServerValue.TIMESTAMP
                )

                commentsRef.push().setValue(commentMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
                            editText.setText("")
                        } else {
                            Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }
}