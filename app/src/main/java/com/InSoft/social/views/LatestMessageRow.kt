package com.InSoft.social.views

import android.app.Activity
import android.content.Context
import com.InSoft.social.R
import com.InSoft.social.models.ChatMessage
import com.InSoft.social.models.Users
import com.InSoft.social.utils.DateUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*


/**
 * Created by ansh on 04/09/18.
 */
class LatestMessageRow(val chatMessage: ChatMessage, val context: Context) : Item<GroupieViewHolder>() {

    var chatPartnerUser: Users? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(Users::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.name
                viewHolder.itemView.latest_msg_time.text = DateUtils.getFormattedTime(chatMessage.timestamp)

                if (!chatPartnerUser?.profileImageUrl?.isEmpty()!!) {
                    val requestOptions = RequestOptions().placeholder(R.drawable.no_image2)

                    Glide.with(context.applicationContext)
                            .load(chatPartnerUser?.profileImageUrl)
                            .apply(requestOptions)
                            .into(viewHolder.itemView.imageview_latest_message)

                    viewHolder.itemView.imageview_latest_message.setOnClickListener {
                        BigImageDialog.newInstance(chatPartnerUser?.profileImageUrl!!).show((context as Activity).fragmentManager
                                , "")
                    }

                }
            }

        })


    }

}