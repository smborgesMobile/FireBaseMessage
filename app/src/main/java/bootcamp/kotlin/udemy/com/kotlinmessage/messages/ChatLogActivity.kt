package bootcamp.kotlin.udemy.com.kotlinmessage.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import bootcamp.kotlin.udemy.com.kotlinmessage.R
import bootcamp.kotlin.udemy.com.kotlinmessage.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Chat Log"

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.userName

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChaFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChaFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChaFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChaFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChaFromItem())
        adapter.add(ChatToItem())

        recyclerview_chat_log.adapter = adapter
    }
}

class ChaFromItem : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}