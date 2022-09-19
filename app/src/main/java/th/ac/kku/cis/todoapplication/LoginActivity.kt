package th.ac.kku.cis.todoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    var TAG = "TODOAPPLICATION"
    var GOOGLE_SIGNIN_CODE = 11234

    val i = 10
    val j = 5

    lateinit var k:Int

    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        var btnLogin: Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            Log.d(TAG, "Logging in")
            //google login
            var signInIntent: Intent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGNIN_CODE)
            //pass -> start activity MainActivity
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGNIN_CODE){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "onActivityResult, Logged in with " + account.email)
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
                Log.e(TAG, "onActivityResult, Google sign-in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String){
        var credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){
                task -> if(task.isSuccessful){
                    val user = auth.currentUser
                    updateUI(user)
                }else{
                    updateUI(null)
                }
            }
    }
    fun updateUI(user: FirebaseUser?){
        if(user != null){
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            Log.d(TAG, "None user sign-in")
        }
    }

    override fun onStart() {
        super.onStart()
        val current = auth.currentUser
        Log.d(TAG, "onStart, get current user")
        updateUI(current)
    }
}