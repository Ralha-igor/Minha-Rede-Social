package br.com.igor.microredesocial.controller

import br.com.igor.microredesocial.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostController {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun criarPost(texto: String, onResult: (Boolean, String?) -> Unit) {

        val email = auth.currentUser?.email

        if (email == null) {
            onResult(false, "Usuário não autenticado")
            return
        }

        val post = hashMapOf(
            "userId" to email,
            "texto" to texto,
            "timestamp" to System.currentTimeMillis(),
            "likesCount" to 0,
            "comentarioCount" to 0
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun buscarPosts(onResult: (ArrayList<Post>) -> Unit) {

        db.collection("posts")
            .get()
            .addOnSuccessListener { documents ->

                val posts = ArrayList<Post>()

                for (document in documents) {
                    val post = Post(
                        userId = document.getString("userId") ?: "",
                        username = document.getString("username") ?: "",
                        texto = document.getString("texto") ?: "",
                        fotoPerfil = document.getString("fotoPerfil"),
                        imagemPost = document.getString("imagemPost"),
                        timestamp = document.getLong("timestamp") ?: 0L,
                        likesCount = document.getLong("likesCount")?.toInt() ?: 0,
                        comentarioCount = document.getLong("comentarioCount")?.toInt() ?: 0
                    )
                    posts.add(post)
                }

                onResult(posts)
            }
            .addOnFailureListener {
                onResult(ArrayList())
            }
    }
}