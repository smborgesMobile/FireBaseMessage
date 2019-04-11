package bootcamp.kotlin.udemy.com.kotlinmessage

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_text_view.setOnClickListener {
            // Launch the login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d("sm.borges", "Email is: $email ")
        Log.d("sm.borges", "Password is: $password")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).
                addOnCompleteListener {
            Log.d("sm.borges", "isSuccessful: ${it.isSuccessful}")
            if (!it.isSuccessful) return@addOnCompleteListener
            Log.d("sm.borges", "User is created: ${it.result.user.uid}")
        }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create user ${it.message}",
                            Toast.LENGTH_SHORT).show()
                }
    }
}
