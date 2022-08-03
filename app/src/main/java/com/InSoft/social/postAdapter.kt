package com.InSoft.social

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.InSoft.social.modals.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listner:IpostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(options) {
    class PostViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deletepost)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewholder =  PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.itempost,parent,false))
       viewholder.likeButton.setOnClickListener{
           listner.onLikedClicked(snapshots.getSnapshot(viewholder.adapterPosition).id)
       }
        viewholder.deleteButton.setOnClickListener{
            listner.ondeleteClicked(snapshots.getSnapshot(viewholder.adapterPosition).id)
        }
        return viewholder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
    holder.postText.text = model.text
        holder.userText.text = model.createdby.displayName?.uppercase()
        Glide.with(holder.userImage.context).load(model.createdby.photoUrl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdat)
        val auth = Firebase.auth
        val currentuserId = auth.currentUser!!.uid
        val isLiked =  model.likedBy.contains(currentuserId)
        val postby = model.createdby.uid
        if (isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
        }
        else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked))
        }
        if (currentuserId==postby){
            holder.deleteButton.visibility = View.VISIBLE
        }else
        {
            holder.deleteButton.visibility = View.GONE

        }

    }
}
interface IpostAdapter{
    fun onLikedClicked(postId:String)
    fun ondeleteClicked(id: String)
}