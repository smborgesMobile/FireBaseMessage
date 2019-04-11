package bootcamp.kotlin.udemy.com.kotlinmessage

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
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
                    Log.d("sm.borges", "Login is successful")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Login ${it.message}",
                            Toast.LENGTH_SHORT).show()
                }
    }
}