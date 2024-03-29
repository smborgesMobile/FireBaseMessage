package bootcamp.kotlin.udemy.com.kotlinmessage.registerlogin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import bootcamp.kotlin.udemy.com.kotlinmessage.R
import bootcamp.kotlin.udemy.com.kotlinmessage.messages.LatestMessageActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val TAG: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button?.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = name_login_edit_text.text.toString()
        val password = name_password_edit_text.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    val intent = Intent(this, LatestMessageActivity::class.java)
                    //Avoid the user use back key to user it without login
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Login ${it.message}",
                            Toast.LENGTH_SHORT).show()
                }
    }
}