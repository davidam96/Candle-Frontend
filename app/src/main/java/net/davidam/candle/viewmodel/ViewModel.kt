package net.davidam.candle.viewmodel

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import net.davidam.candle.model.Response
import net.davidam.candle.model.User
import net.davidam.candle.model.WordDocument
import java.lang.Exception
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

class ViewModel(app: FirebaseApp) {

    companion object {
        const val TAG = "dabudin"
    }
    private val db = FirebaseFirestore.getInstance(app)

    //  These class methods communicate with the Firestore database
    //  and then return personalised ViewModel objects

    //  (POR HACER): Remember to later make Firestore's security rules in order to restrict the
    //  format of all future database requests, having into account each method written here.

    fun checkUser(authUser: FirebaseUser): User {
        var user = User(authUser.uid, "${authUser.email}",
            "", "", "${authUser.photoUrl}", setDate())

        //  (POR HACER): Este metodo retorna "user" antes de que se complete el listener, lo cual es
        //  un problema porque entonces se cierra antes de que el listener pueda terminar su trabajo
        //  Posible solución: implementar corrutinas?
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { userDoc ->
                Log.d(TAG, userDoc.toString())
                if (!userDoc.exists()) {
                    db.collection("users").document(authUser.uid).set(user)
                        .continueWith { task ->
                            if (task.exception !== null) {
                                user = User("", "", "", "",
                                    "", "", "")
                            }
                        }
                }
            }
        return user
    }

    private fun setDate(): String {
        val date = ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu 'T'HH:mm:ss '['O VV']'")

        //  Retrieve Test
/*        Log.d("dabudin", date.format(formatter).toString())
        val date2 = ZonedDateTime.parse(date.format(formatter).toString(), formatter)
        Log.d("dabudin", date2.format(formatter).toString())*/

        return date.format(formatter).toString()
    }

}