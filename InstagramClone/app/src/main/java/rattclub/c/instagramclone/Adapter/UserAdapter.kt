package rattclub.c.instagramclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import rattclub.c.instagramclone.Model.User
import rattclub.c.instagramclone.R

class UserAdapter (private var mContext:Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

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
//        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.userProfileImage)
//        holder.userFollowButton.setOnClickListener(View.OnClickListener {
//
//        })
    }

    class ViewHolder(itemView: View):  RecyclerView.ViewHolder(itemView){
        var userNameTextView = itemView.findViewById<TextView>(R.id.user_name_search)
        var userFullNameTextView = itemView.findViewById<TextView>(R.id.user_fullname_search)
        var userProfileImage = itemView.findViewById<CircleImageView>(R.id.user_profile_image_search)
        var userFollowButton = itemView.findViewById<Button>(R.id.user_follow_btn)
    }


}