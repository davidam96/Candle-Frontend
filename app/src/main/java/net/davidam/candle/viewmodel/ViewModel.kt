package net.davidam.candle.viewmodel

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import net.davidam.candle.model.User
import net.davidam.candle.model.UserResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ViewModel(app: FirebaseApp) {

    companion object {
        const val TAG = "dabudin"
    }
    private val db = FirebaseFirestore.getInstance(app)

    //  These class methods communicate with the Firestore database
    //  and then return personalised ViewModel objects

    //  (POR HACER): Remember to later make Firestore's security rules in order to restrict the
    //  format of all future database requests, having into account each method written here.

    fun checkUser(authUser: FirebaseUser): Task<UserResponse> {
        val user = User(authUser.uid, "${authUser.email}",
            "", "", "${authUser.photoUrl}", setDate())
        var response = UserResponse(user)

        //  (POR HACER): probar si este codigo funciona correctamente
        val userDocRef = db.collection("users").document(user.uid)
        return db.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (!userDoc.exists()) {
                db.collection("users").document(authUser.uid).set(user)
            }
        }
            .continueWith { task ->
                if (task.exception != null) {
                    response = UserResponse(null, task.exception.toString(), 10)
                }
                response
            }
    }

    private fun setDate(): String {
        val date = ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu 'T'HH:mm:ss '['O VV']'")
        return date.format(formatter).toString()
    }

}