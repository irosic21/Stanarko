package hr.foi.rampu.stanarko.F01_Registracija

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
import hr.foi.rampu.stanarko.F02_Prijava.Prijava
import hr.foi.rampu.stanarko.R
import hr.foi.rampu.stanarko.TenantActivity
import hr.foi.rampu.stanarko.database.TenantsDAO
import hr.foi.rampu.stanarko.entities.Tenant

class Registracija : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registracija)
        val signUp = findViewById<Button>(R.id.btn_register)
        signUp.setOnClickListener{
            registerUser()
        }
        spannableString()
    }

    private fun registerUser(){
        val name = findViewById<EditText>(R.id.et_name).text.toString()
        val surname = findViewById<EditText>(R.id.et_surname).text.toString()
        val phoneNumber = findViewById<EditText>(R.id.et_phone_number).text.toString()
        val mail = findViewById<EditText>(R.id.et_mail).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()
        val confirmPassword = findViewById<EditText>(R.id.et_confirm_password).text.toString()

        if (!ProvjereUnosaRegistracije().blankCheck(baseContext, name, surname, phoneNumber, mail, password, confirmPassword)) return
        if(!ProvjereUnosaRegistracije().phoneNumberCheck(baseContext, phoneNumber)) return
        if(!ProvjereUnosaRegistracije().mailCheck(baseContext, mail)) return
        if(!ProvjereUnosaRegistracije().passwordCheck(baseContext, password,confirmPassword)) return

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful
                    val tenant = Tenant(
                        name = name,
                        surname = surname,
                        phoneNumber = phoneNumber,
                        mail = mail,
                        flat = null,
                        dateOfMovingIn = null,
                        dateOfMovingOut = null
                    )
                    TenantsDAO().createTenant(tenant,this)
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val intent = Intent(this, TenantActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("userId",userId)
                    intent.putExtra("mail",mail)
                    startActivity(intent)
                    finish()
                } else {
                    // Registration failed
                    Toast.makeText(this,getString(R.string.failed_registration_message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun spannableString(){
        val spannable = SpannableString(getString(R.string.already_have_an_account_login)+" "+getString(R.string.login_prompt))
        val clickableString = getString(R.string.login_prompt)
        val start = spannable.indexOf(clickableString)
        val end = start + clickableString.length
        if (start != -1 && end != -1){
            val span = object : ClickableSpan(){
                override fun onClick(widget: View) {
                    val intent = Intent(this@Registracija, Prijava::class.java)
                    startActivity(intent)
                }
            }
            spannable.setSpan(span,start,end,0)
            val register = findViewById<TextView>(R.id.login_prompt)
            register.text = spannable
            register.movementMethod = LinkMovementMethod.getInstance()
        }else{
            Log.e("ERROR", "Clickable string not found in spannable string.")
        }
    }
}