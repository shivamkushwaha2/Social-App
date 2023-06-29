package com.InSoft.social

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogoutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        Firebase.auth.signOut()
                        GoogleSignIn.getClient(
                            this.requireActivity(),
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        ).signOut()
                        val signinActivityIntent = Intent(this.context, SignInActivity::class.java)
                        startActivity(signinActivityIntent)

                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Dismiss the dialog
                        dialog.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
