package bootcamp.kotlin.udemy.com.kotlinmessage.views

import bootcamp.kotlin.udemy.com.kotlinmessage.R
import bootcamp.kotlin.udemy.com.kotlinmessage.models.User
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChaFromItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_from_row.text = text

        //load our user image
        val uri = user?.profileImageUrl
        val target = viewHolder.itemView.imageview_from_row_item
        Glide.with(viewHolder.itemView.context).load(uri).centerCrop().into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_to_row.text = text

        //load our user image
        val uri = user?.profileImageUrl
        val target = viewHolder.itemView.imageview_to_row_item

        Glide.with(viewHolder.itemView.context).load(uri).centerCrop().into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
