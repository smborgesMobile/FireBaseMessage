package bootcamp.kotlin.udemy.com.kotlinmessage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

     val TAG: String = "RegisterActivity"

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

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Photo button is pressed")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    var selectPhotoURI: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "The new photo is selected")

            selectPhotoURI = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoURI)
            select_photo_image_view.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
        }
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d(TAG, "Email is: $email ")
        Log.d(TAG, "Password is: $password")

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            uploadImageToFireBaseStorage()
        }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create user ${it.message}",
                            Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFireBaseStorage() {
        if (selectPhotoURI == null) return
        //create a random ID
        val filename = UUID.randomUUID().toString()
        val cloudReference = FirebaseStorage.getInstance().getReference("/images/$filename")

        cloudReference.putFile(selectPhotoURI!!).addOnSuccessListener {
            Log.d(TAG, "Successfully uploaded image ${it.metadata?.path}")
            cloudReference.downloadUrl.addOnSuccessListener {
                Log.d(TAG, "Download is OK")
                saveUserToFirebaseDatabase(it.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "Fail to download photo: ${it.message}", Toast.LENGTH_SHORT)
            }

        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Finally we saved the user to Firebase Database")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to set value to database: ${it.message}")
                }
    }
}

class User(val uid: String, val userName: String, val profileImageUrl: String)