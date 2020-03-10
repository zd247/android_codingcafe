package rattclub.c.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    var fullName: String? = null
    var username: String? = null
    var email: String? = null
    var password: String? = null
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    var loadingBar : ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        loadingBar = ProgressDialog(this@RegisterActivity)

        register_btn.setOnClickListener(View.OnClickListener {
            registerUser()
        })
    }

    private fun registerUser() {
        fullName = full_name_register.text.toString()
        username = username_register.text.toString()
        email = email_register.text.toString()
        password = password_register.text.toString()

        if (TextUtils.isEmpty(fullName) or TextUtils.isEmpty(username) or
            TextUtils.isEmpty(email) or TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill in required field", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar!!.setTitle("Registering")
            loadingBar!!.setMessage("Please wait...")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()
            //create a loading bar
            mAuth.createUserWithEmailAndPassword(email!!.trim(), password!!)
                .addOnCompleteListener(OnCompleteListener {task ->
                      if (task.isSuccessful){
                          val currentUserID = mAuth.currentUser?.uid.toString()
                          saveUserInfo(currentUserID)
                      }else {
                          loadingBar!!.dismiss()
                          Toast.makeText(this, "Error ${task.exception.toString()}", Toast.LENGTH_SHORT).show();
                      }
                })
        }
    }

    private fun saveUserInfo(currentUserID: String?) {
        var userMap = HashMap<String, Any?>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName
        userMap["username"] = username
        userMap["email"] = email
        userMap["image"] = ""
        userMap["bio"] = ""

        usersRef.child("Users").child(currentUserID!!)
            .updateChildren(userMap)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful){
                    loadingBar!!.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }else {
                    loadingBar!!.dismiss()
                    Toast.makeText(this, "Error ${task.exception.toString()}", Toast.LENGTH_SHORT).show();
                }
            })
    }
}
