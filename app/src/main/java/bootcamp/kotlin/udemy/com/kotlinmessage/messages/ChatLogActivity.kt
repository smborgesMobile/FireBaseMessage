package bootcamp.kotlin.udemy.com.kotlinmessage.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import bootcamp.kotlin.udemy.com.kotlinmessage.R
import bootcamp.kotlin.udemy.com.kotlinmessage.R.id.imageview_to_row_item
import bootcamp.kotlin.udemy.com.kotlinmessage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
        var currentUser: User? = null
    }
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        fetchCurrentUser()
        recyclerview_chat_log.adapter = adapter

        supportActionBar?.title = "Chat Log"

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user.userName

        listenerForMessages()

        send_button.setOnClickListener {
            Log.d(TAG, "Attempt to send message ...")
            performSendMessage()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(TAG, "CurrentUser: ${currentUser?.userName}")
            }

        })
    }

    private fun listenerForMessages() {
        val reference = FirebaseDatabase.getInstance().getReference("/message")

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage?.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChaFromItem(chatMessage?.text, currentUser as User))
                    } else {
                        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(ChatToItem(chatMessage.text, toUser))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timeStamp: Long) {
        constructor() : this("", "", "", "", -1)
    }

    private fun performSendMessage() {

        val text = editText_enter_message.text.toString()
        //Create field inside the data base
        val reference = FirebaseDatabase.getInstance().getReference("/message").push()

        // get who is the current user
        val fromId = FirebaseAuth.getInstance().uid

        // get who is the end user
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY).uid

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId,
                (System.currentTimeMillis() / 1000))

        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Save our chat message: ${reference.key}")
                }
    }
}

class ChaFromItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_from_row.text = text

        //load our user image
        val uri = user?.profileImageUrl
        val target = viewHolder.itemView.imageview_from_row_item
        Picasso.get().load(uri).into(target)
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
        Picasso.get().load(uri).into(target)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}