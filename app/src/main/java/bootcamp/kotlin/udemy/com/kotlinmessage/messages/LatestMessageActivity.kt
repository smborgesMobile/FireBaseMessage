package bootcamp.kotlin.udemy.com.kotlinmessage.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import bootcamp.kotlin.udemy.com.kotlinmessage.R
import bootcamp.kotlin.udemy.com.kotlinmessage.models.User
import bootcamp.kotlin.udemy.com.kotlinmessage.registerlogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    companion object {
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        recycler_view_latest_message.adapter = adapter
//        setupDummyRows()
        listenForLatestMessages()
        verifyUserIsLoggedIn()
        fetchCurrentUser()
    }

    val latestNessagesMap = HashMap<String, ChatLogActivity.ChatMessage>()
    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatLogActivity.ChatMessage::class.java) ?: return
                latestNessagesMap[p0.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatLogActivity.ChatMessage::class.java) ?: return
                latestNessagesMap[p0.key!!] = chatMessage
                refreshRecyclerView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun refreshRecyclerView() {
        adapter.clear()
        latestNessagesMap.values.forEach {
            adapter.add(LatestMessage(it))
        }
    }

    class LatestMessage(val chatMessage: ChatLogActivity.ChatMessage) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textView_latest_messages.text = chatMessage.text
        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            //Avoid the user use back key to user it without login
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d(ChatLogActivity.TAG, "CurrentUser: ${currentUser?.userName}")
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                //Avoid the user use back key to user it without login
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
