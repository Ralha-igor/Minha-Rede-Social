package br.com.igor.microredesocial.controller

import com.google.firebase.auth.FirebaseAuth

class AuthController {
    private val auth = FirebaseAuth.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.message)
            }
    }
    fun logout() {
        auth.signOut()
    }
}