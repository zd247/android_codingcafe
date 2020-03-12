@file:Suppress("DEPRECATION")

package rattclub.c.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rattclub.c.instagramclone.Fragments.ProfileFragment

class AccountSettingsActivity : AppCompatActivity() {
    private val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
    private val mAuth = FirebaseAuth.getInstance()
    var imageUri: Uri? = null
    var checker: Boolean = false
    var loadingBar : ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        loadingBar = ProgressDialog(this@AccountSettingsActivity)

        displayUserInfo()

        /**======================[OnClicks]====================**/

        profile_settings_logout_btn.setOnClickListener(View.OnClickListener {
            mAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })

        save_profile_btn.setOnClickListener(View.OnClickListener {
            uploadUserInfo()
        })

        change_profile_image_settings_text_link.setOnClickListener(View.OnClickListener {
            checker = true

            // if imageUri is null, start cropper activity
            CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .start(this)
        })
    }

    private fun displayUserInfo() {
        loadingBar?.setTitle("Displaying Profile")
        loadingBar?.setMessage("Please wait...")
        loadingBar?.setCanceledOnTouchOutside(false)
        loadingBar?.show()
        usersRef.child(mAuth.currentUser!!.uid)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()){
                        val image : String? = dataSnapshot.child("image").value.toString()
                        if (image != ""){
                            Picasso.get().load(image)
                                .into(profile_settings_image_view)
                        }
                        if (dataSnapshot.child("bio").value.toString() != "") {
                            profile_settings_bio_edit_txt
                                .setText(dataSnapshot.child("bio").value.toString())
                        }

                        profile_settings_full_name_edit_txt
                            .setText(dataSnapshot.child("fullname").value.toString())
                        profile_settings_username_edit_txt
                            .setText(dataSnapshot.child("username").value.toString())

                    }
                }

                override fun onCancelled(p0: DatabaseError) {}

            })
        GlobalScope.launch(context = Dispatchers.Main) {
            delay(868)
            loadingBar?.dismiss()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null){
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_settings_image_view.setImageURI(imageUri)
        }else {
            startActivity(Intent(this, this.javaClass))
            finish()
        }
    }

    private fun uploadUserInfo() {
        loadingBar?.setTitle("Updating Profile")
        loadingBar?.setMessage("Please wait...")
        loadingBar?.setCanceledOnTouchOutside(false)
        loadingBar?.show()
        if (checker && imageUri != null){
            uploadImage()
        }

        val fullName: String = profile_settings_full_name_edit_txt.text.toString()
        val username: String = profile_settings_username_edit_txt.text.toString()
        val bio: String? = profile_settings_bio_edit_txt.text.toString()

        if (fullName.isEmpty() or username.isEmpty()){
            Toast.makeText(this, "Please fill in the required fields", Toast.LENGTH_SHORT).show()
        }else {
            var profileSettingMap = HashMap<String, Any?>()
            profileSettingMap.put("fullname",fullName)
            profileSettingMap.put("username",username)
            if (!bio.equals("")) {
                profileSettingMap.put("bio", bio)
            }
            usersRef.child(mAuth.currentUser!!.uid).updateChildren(profileSettingMap)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        loadingBar?.dismiss()
                        Toast.makeText(this, "User profile updated", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                        finish()
                    }else {
                        loadingBar?.dismiss()
                        Toast.makeText(this, "Error + ${task.exception.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }

    private fun uploadImage() {
        val storageProfilePictureRef : StorageReference =
            FirebaseStorage.getInstance().reference.child("Profile Pictures")
        val fileRef : StorageReference = storageProfilePictureRef
            .child(imageUri?.lastPathSegment.toString())

        // upload image to firebase storage
        var uploadTask= fileRef.putFile(imageUri!!)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Fail to upload image", Toast.LENGTH_SHORT).show()
            loadingBar?.dismiss()
        }

        // save download url of the image to firebase database
        uploadTask.continueWithTask {task->
            if (!task.isSuccessful){
                task.exception?.let { throw it }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful){
                val myUrl : String = task.result.toString()
                usersRef.child(mAuth.currentUser!!.uid).child("image")
                    .setValue(myUrl)
            }else {
                Toast.makeText(this,
                    "Fail to retrieve image download URL",
                    Toast.LENGTH_SHORT).show()
                loadingBar?.dismiss()
            }
        })


    }
}
