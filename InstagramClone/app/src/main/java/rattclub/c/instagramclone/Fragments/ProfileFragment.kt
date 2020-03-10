package rattclub.c.instagramclone.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.view.*
import rattclub.c.instagramclone.AccountSettingsActivity

import rattclub.c.instagramclone.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        view.edit_account_btn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(view.context,AccountSettingsActivity().javaClass))
        })

        return view
    }

}
