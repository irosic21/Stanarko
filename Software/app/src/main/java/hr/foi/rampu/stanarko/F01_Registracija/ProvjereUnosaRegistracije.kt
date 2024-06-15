package hr.foi.rampu.stanarko.F01_Registracija

import android.content.Context
import android.widget.Toast
import hr.foi.rampu.stanarko.R

 class ProvjereUnosaRegistracije {
      fun blankCheck(context: Context, name: String, surname: String, phoneNumber: String, mail: String, password: String, confirmPassword: String): Boolean {
         if (name.isBlank()||surname.isBlank()||phoneNumber.isBlank()||mail.isBlank()||password.isBlank()||confirmPassword.isBlank()){
             Toast.makeText(context,context.getString(R.string.blank_register_fields),Toast.LENGTH_SHORT).show()
             return false
         }
         return true
     }

      fun phoneNumberCheck(context: Context, phoneNumber: String): Boolean{
         val pattern = Regex("^([\\d]{10,10})\$")
         if (pattern.matches(phoneNumber))  return true
         Toast.makeText(context,context.getString(R.string.incorrect_phone_number), Toast.LENGTH_SHORT).show()
         return false
     }

      fun mailCheck(context: Context, mail: String): Boolean {
         val pattern = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
         if (pattern.matches(mail)) return true
         Toast.makeText(context,context.getString(R.string.incorrect_mail_register), Toast.LENGTH_SHORT).show()
         return false
     }

      fun passwordCheck(context: Context, password : String, confirmPassword : String): Boolean {
         if(password==confirmPassword)
             return true
         Toast.makeText(context,context.getString(R.string.incorrect_register_password), Toast.LENGTH_SHORT).show()
         return false
     }
}