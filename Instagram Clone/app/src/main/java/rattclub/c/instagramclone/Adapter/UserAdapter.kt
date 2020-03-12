package rattclub.c.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import rattclub.c.instagramclone.Fragments.ProfileFragment
import rattclub.c.instagramclone.Model.User
import rattclub.c.instagramclone.R

class UserAdapter (private var mContext:Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.username
        holder.userFullNameTextView.text = user.fullname
        if (user.image != "") {
            Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.userProfileImage)
        }

        if (mAuth.currentUser!!.uid == user.uid){
            holder.userFollowButton.visibility = View.INVISIBLE
        }

        checkFollowingStatus(user.uid, holder.userFollowButton)

        holder.userFollowButton.setOnClickListener(View.OnClickListener {
            val followRef: DatabaseReference = FirebaseDatabase.getInstance()
                .reference.child("Follow")

            if (holder.userFollowButton.text.toString() == "Following") {
                followRef.child(user.uid)
                    .child("Followers")
                    .child(mAuth.currentUser!!.uid)
                    .removeValue()
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            followRef.child(mAuth.currentUser!!.uid)
                                .child("Following")
                                .child(user.uid)
                                .removeValue()
                                .addOnCompleteListener {task ->
                                    if (task.isSuccessful){
                                        holder.userFollowButton.text = "Follow"
                                    }
                                }
                        }
                    }
            }else {
                followRef.child(user.uid)
                    .child("Followers")
                    .child(mAuth.currentUser!!.uid)
                    .setValue(true)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            followRef.child(mAuth.currentUser!!.uid)
                                .child("Following")
                                .child(user.uid)
                                .setValue(true)
                                .addOnCompleteListener {task ->
                                    if (task.isSuccessful){
                                        holder.userFollowButton.text = "Following"
                                    }
                                }
                        }
                    }
            }
        })


        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref= mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileID", user.uid)
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        })
    }

    class ViewHolder(itemView: View):  RecyclerView.ViewHolder(itemView){
        var userNameTextView = itemView.findViewById<TextView>(R.id.user_name_search)
        var userFullNameTextView = itemView.findViewById<TextView>(R.id.user_fullname_search)
        var userProfileImage = itemView.findViewById<CircleImageView>(R.id.user_profile_image_search)
        var userFollowButton = itemView.findViewById<Button>(R.id.user_follow_btn)
    }

    private fun checkFollowingStatus(uid: String, followButton: Button) {
        val followRef: DatabaseReference = FirebaseDatabase.getInstance()
            .reference.child("Follow")

        followRef.child(mAuth.currentUser!!.uid).child("Following")
            .addValueEventListener(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(uid).exists()){
                        followButton.text = "Following"
                    }else {
                        followButton.text = "Follow"
                    }
                }

            })
    }


}