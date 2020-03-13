package rattclub.c.instagramclone.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.edit_account_btn
import rattclub.c.instagramclone.AccountSettingsActivity
import rattclub.c.instagramclone.Model.User

import rattclub.c.instagramclone.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private lateinit var profileID: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        profileID = firebaseUser.uid

        // Get onCliked userID from search bar list
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileID = pref.getString("profileID", "none").toString()
        }

        if (profileID == firebaseUser.uid){
            view.edit_account_btn.text = "Edit Profile"
        }else if (profileID != firebaseUser.uid){
            changeEditButtonText()
        }

        view.edit_account_btn.setOnClickListener(View.OnClickListener {
            val getButtonText = view.edit_account_btn.text.toString()

            when {
                getButtonText == "Edit Profile" ->{
                    startActivity(Intent(view.context,AccountSettingsActivity().javaClass))
                }

                getButtonText == "Follow" -> {
                    firebaseUser?.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it)
                            .child("Following").child(profileID)
                            .setValue(true)
                    }

                    firebaseUser?.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileID)
                            .child("Followers").child(it)
                            .setValue(true)
                    }
                }

                getButtonText == "Following" -> {
                    firebaseUser?.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it)
                            .child("Following").child(profileID)
                            .removeValue()
                    }

                    firebaseUser?.uid.let {it ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileID)
                            .child("Followers").child(it)
                            .removeValue()
                    }
                }


            }

        })

        getDisplayFollowers()
        getDisplayFollowing()
        getDisplayUserInfo()

        return view
    }

    private fun changeEditButtonText() {
        FirebaseDatabase.getInstance()
            .reference.child("Follow")
            .child(firebaseUser.uid)
            .child("Following")?.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(profileID).exists()) {
                        edit_account_btn?.text = "Following"
                    }else {
                        edit_account_btn?.text = "Follow"
                    }
             }
        })
    }

    private fun getDisplayFollowers(){
        val followersRef: DatabaseReference = FirebaseDatabase.getInstance()
            .reference.child("Follow")
            .child(profileID)
            .child("Followers")

        followersRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    view?.total_followers?.text = dataSnapshot.childrenCount.toString()
                }
            }

        })
    }

    private fun getDisplayFollowing(){
        val followingRef: DatabaseReference = FirebaseDatabase.getInstance()
            .reference.child("Follow")
            .child(profileID)
            .child("Following")

        followingRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    view?.total_following?.text = dataSnapshot.childrenCount.toString()
                }
            }

        })
    }

    private fun getDisplayUserInfo(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(profileID)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (context != null) {
//                    return
//                }

                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)

                    if (user!!.image != ""){
                        Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profile_image_profile)
                    }
                    full_name_profile.text = user!!.fullname
                    bio_profile.text = user!!.bio
                }
            }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileID", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileID", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref= context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileID", firebaseUser.uid)
        pref?.apply()
    }

}
