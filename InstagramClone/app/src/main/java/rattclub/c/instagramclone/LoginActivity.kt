@file:Suppress("DEPRECATION")

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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var email: String? = null
    var password: String? = null
    val mAuth = FirebaseAuth.getInstance()
    var loadingBar : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadingBar = ProgressDialog(this@LoginActivity)

        login_btn.setOnClickListener(View.OnClickListener {
            loginUser()
        })

        register_txt_link.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        })
    }

    private fun loginUser() {
        email = email_login.text.toString()
        password = password_login.text.toString()

        if (TextUtils.isEmpty(email) or TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill in required field", Toast.LENGTH_SHORT).show();
        }else {

            loadingBar!!.setTitle("Logging in")
            loadingBar!!.setMessage("Please wait...")
            loadingBar!!.setCanceledOnTouchOutside(false)
            loadingBar!!.show()

            mAuth.signInWithEmailAndPassword(email!!.trim(), password!!)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful){
                        loadingBar!!.dismiss()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }else {
                        loadingBar!!.dismiss()
                        Toast.makeText(this, "Error + ${task.exception.toString()}", Toast.LENGTH_SHORT).show();
                    }
                })
        }
    }
}
