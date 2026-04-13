package br.com.igor.microredesocial.controller

import br.com.igor.microredesocial.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserController {

    private val db = FirebaseFirestore.getInstance()

    fun buscarPerfil(email: String, onResult: (User?) -> Unit) {

        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) { // <- adiciona .exists()
                    val user = User(
                        email = document.getString("email") ?: "",
                        username = document.getString("username") ?: "",
                        nomecompleto = document.getString("nomeCompleto") ?: "",
                        fotoPerfil = document.getString("fotoPerfil"),
                        dataCriacao = document.getLong("dataCriacao") ?: 0L
                    )
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    fun salvarPerfil(user: User, onResult: (Boolean, String?) -> Unit) {

        val dados = hashMapOf(
            "email" to user.email,
            "username" to user.username,
            "nomeCompleto" to user.nomecompleto,
            "fotoPerfil" to user.fotoPerfil,
            "dataCriacao" to user.dataCriacao
        )

        db.collection("usuarios")
            .document(user.email)
            .set(dados)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}