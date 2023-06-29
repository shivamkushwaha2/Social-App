import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.InSoft.social.R
import com.InSoft.social.modals.comment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.commentitem.view.*
import org.w3c.dom.Comment

class CommentAdapter(options: FirebaseRecyclerOptions<comment>) :
    FirebaseRecyclerAdapter<comment, CommentAdapter.CommentViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.commentitem, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: comment) {
        holder.bind(model)
        Log.d("model","$model")
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val authorTextView: TextView = itemView.findViewById(R.id.cmtuserName)
        private val commentTextView: TextView = itemView.findViewById(R.id.commentdes)
        private val time: TextView = itemView.findViewById(R.id.cmtcreatedAt)
        private val dp: ImageView = itemView.findViewById(R.id.cmtuserImage)


        fun bind(comment1: comment) {
            authorTextView.text = comment1.author
            commentTextView.text = comment1.comment


        }
    }
}
