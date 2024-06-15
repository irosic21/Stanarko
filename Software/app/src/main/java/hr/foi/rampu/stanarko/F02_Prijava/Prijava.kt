package hr.foi.rampu.stanarko.F02_Prijava

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import hr.foi.rampu.stanarko.F01_Registracija.Registracija
import hr.foi.rampu.stanarko.MainActivity
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.TenantActivity
import hr.foi.rampu.stanarko.database.OwnersDAO

class Prijava : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionCheck()
        setContentView(R.layout.activity_prijava)
        val loginButton = findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            loginUser()
        }
        spannableString()
    }

    private fun sessionCheck() {
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val userMail = FirebaseAuth.getInstance().currentUser!!.email
            val ownersCollection = FirebaseFirestore.getInstance().collection("owners")
            ownersCollection.whereEqualTo("mail",userMail).get().addOnSuccessListener { document ->
                if(!document.isEmpty){
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("Email",userMail)
                    startActivity(intent)
                }
                val tenantsCollection = FirebaseFirestore.getInstance().collection("tenants")
                tenantsCollection.whereEqualTo("mail",userMail).get().addOnSuccessListener { document ->
                    if(!document.isEmpty){
                        val intent = Intent(this,TenantActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("Email",userMail)
                        startActivity(intent)
                    }
                }
            }
        } else {
            return
        }
    }

    private fun loginUser() {
        val mail = findViewById<EditText>(R.id.et_mail).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        if (!blankCheck(baseContext, mail, password)) return

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail,password).addOnSuccessListener {
            val userMail = FirebaseAuth.getInstance().currentUser!!.email
            val ownersCollection = FirebaseFirestore.getInstance().collection("owners")
            ownersCollection.whereEqualTo("mail",userMail).get().addOnSuccessListener { document ->
                if(!document.isEmpty){
                    val intent = Intent(this, MainActivity::class.java)
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                        val ownersDAO = OwnersDAO()
                        ownersDAO.updateOwnerToken(userMail!!, token )
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("Email",mail)
                    startActivity(intent)

                }
                val tenantsCollection = FirebaseFirestore.getInstance().collection("tenants")
                tenantsCollection.whereEqualTo("mail",userMail).get().addOnSuccessListener { document ->
                    if(!document.isEmpty){
                        val intent = Intent(this,TenantActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("Email",mail)
                        startActivity(intent)
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this,getString(R.string.invalid_login_message),Toast.LENGTH_SHORT).show()
        }
    }

    private fun blankCheck(context: Context, mail: String, password: String): Boolean {
        if (mail.isBlank()||password.isBlank()){
            Toast.makeText(context,context.getString(R.string.blank_register_fields),Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun spannableString(){
        val spannable = SpannableString(getString(R.string.don_t_have_an_account_register)+" "+getString(R.string.register_prompt))
        val clickableString = getString(R.string.register_prompt)
        val start = spannable.indexOf(clickableString)
        val end = start + clickableString.length
        if (start != -1 && end != -1){
            val span = object : ClickableSpan(){
                override fun onClick(widget: View) {
                    val intent = Intent(this@Prijava, Registracija::class.java)
                    startActivity(intent)
                }
            }
            spannable.setSpan(span,start,end,0)
            val register = findViewById<TextView>(R.id.register_prompt)
            register.text = spannable
            register.movementMethod = LinkMovementMethod.getInstance()
        }else{
            Log.e("ERROR", "Clickable string not found in spannable string.")
        }
    }
}