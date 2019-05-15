package bootcamp.kotlin.udemy.com.kotlinmessage.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import bootcamp.kotlin.udemy.com.kotlinmessage.R
import bootcamp.kotlin.udemy.com.kotlinmessage.models.User
import bootcamp.kotlin.udemy.com.kotlinmessage.views.ChaFromItem
import bootcamp.kotlin.udemy.com.kotlinmessage.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat_log.adapter = adapter
        recyclerview_chat_log.hasFixedSize()

        supportActionBar?.title = "Chat Log"

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.userName

        listenerForMessages()
        send_button.setOnClickListener {
            Log.d(TAG, "Attempt to send message ...")
            if (!editText_enter_message.text.isEmpty())
                performSendMessage()
        }
    }

    private fun listenerForMessages() {
        val fromId = LatestMessageActivity.currentUser?.uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId")

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
                        adapter.add(ChaFromItem(chatMessage?.text, LatestMessageActivity.currentUser as User))
                    } else {
                        val toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(ChatToItem(chatMessage.text, toUser))
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
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
//        val reference = FirebaseDatabase.getInstance().getReference("/message").push()

        // get who is the current user
        val fromId = FirebaseAuth.getInstance().uid
        // get who is the end user
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY).uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-message/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-message/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId,
                (System.currentTimeMillis() / 1000))

        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Save our chat message: ${reference.key}")
                    editText_enter_message.text.clear()
                    //Scroll to position
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                }

        toReference.setValue(chatMessage).addOnSuccessListener {
            //Scroll to position
            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
        }

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}