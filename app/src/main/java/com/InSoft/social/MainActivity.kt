package com.InSoft.social
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.InSoft.social.daos.PostDao
import com.InSoft.social.login.LoginActivity
import com.InSoft.social.messages.LatestMessagesActivity
import com.InSoft.social.messages.NewMessageActivity
import com.InSoft.social.modals.Post
import com.InSoft.social.register.RegisterActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), IpostAdapter{
    lateinit var postDao: PostDao
    lateinit var adapter: PostAdapter
    lateinit var appUpdateManager:AppUpdateManager
    val updateCode = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inappUp()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            println("your registration token is $token")
            Log.d("check", token)

        })

        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
//        toolbar.overflowIcon!!.setTint(Color.WHITE)



        fab.setOnClickListener {
            val postActivityIntent = Intent(this, CreatePostActivity::class.java)
            startActivity(postActivityIntent)
        }
        setupRecyclerView()



        Firebase.messaging.subscribeToTopic("Posts")
            .addOnCompleteListener { task ->
                var msg ="msg_subscribed"
                if (!task.isSuccessful) {
                    msg = "msg_subscribe_failed"
                }
                Log.d("topic", msg)
           /*     Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()  */
            }



        val nv = findViewById<BubbleNavigationConstraintView>(R.id.bottombar)
        nv.setNavigationChangeListener { view , position ->
            val postActivityIntent = Intent(this, CreatePostActivity::class.java)
            val chatbot = Intent(this, chatbot::class.java)


            when (position) {

                1 -> startActivity(chatbot)
                2 -> startActivity(postActivityIntent)
                3 -> showDialog()
            }

        }
    }
    private fun showDialog() {
        val logoutDialog = LogoutDialog()
        logoutDialog.show(supportFragmentManager, "logout_dialog")
    }
     fun inappUp() {
         appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    updateCode)
            }
        }

    appUpdateManager.registerListener(inListner)
    }
    val inListner = InstallStateUpdatedListener {
        if(it.installStatus() == InstallStatus.DOWNLOADED){}
        popUp()
    }

     fun popUp() {

        val snackbar = Snackbar.make(findViewById(android.R.id.content),"App update almost done",LENGTH_INDEFINITE)
        snackbar.setAction("Reload",View.OnClickListener {
        appUpdateManager.completeUpdate()
        })
         snackbar.show()
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        updateCode
                    );
                }
            }
    }
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == updateCode){
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d("TAG", "" + "Result Ok")
                    //  handle user's approval }
                }
                Activity.RESULT_CANCELED -> {
                    {
//if you want to request the update again just call checkUpdate()
                    }
                    Log.d("TAG", "" + "Result Cancelled")
                    // handle user's rejection  }
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //if you want to request the update again just call checkUpdate()
                    Log.d("TAG", "" + "Update Failure")
                    //  handle update failure
                }
            }
        }
    }

    private fun setupRecyclerView() {
        postDao = PostDao()
        val postCollection = postDao.postCollection
        val query = postCollection.orderBy("createdat",Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        adapter = PostAdapter(recyclerViewOptions,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikedClicked(postId: String) {
        postDao.updatelikes(postId)
    }

    override fun ondeleteClicked(id: String) {
        postDao.deletePost(id)

    }

    override fun onCommentClicked() {
        val cmtActivityIntent = Intent(this, commentActivity::class.java)
        startActivity(cmtActivityIntent)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu, menu)
        return true
    }

    fun openchat(view: android.view.View) {
        val ActivityIntent = Intent(this, LatestMessagesActivity::class.java)
            startActivity(ActivityIntent)
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
//        val id: Int = item.getItemId()
//        return if (id == R.id.signout) {
//        Firebase.auth.signOut()
//            GoogleSignIn.getClient(
//                this,
//                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
//            ).signOut()
//            val signinActivityIntent = Intent(this, SignInActivity::class.java)
//            startActivity(signinActivityIntent)
//            true
//        } else super.onOptionsItemSelected(item)
//    }


}