package br.com.igor.microredesocial.controller

import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import br.com.igor.microredesocial.helper.Base64Converter
import br.com.igor.microredesocial.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostController {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun criarPost(
        texto: String,
        imageView: ImageView? = null,
        city: String = "",
        lat: Double = 0.0,
        lng: Double = 0.0,
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(false, "Usuário não autenticado")
            return
        }

        val imagemBase64 = if (imageView != null) {
            val drawable = imageView.drawable
            if (drawable != null && drawable is BitmapDrawable) {
                Base64Converter.drawableToString(drawable)
            } else ""
        } else ""

        val userController = UserController()
        userController.buscarPerfil(uid) { user ->
            if (user == null) {
                onResult(false, "Usuário não encontrado")
                return@buscarPerfil
            }

            val post = hashMapOf(
                "userId" to uid,
                "username" to user.username,
                "fotoPerfil" to (user.fotoPerfil ?: ""),
                "texto" to texto,
                "timestamp" to System.currentTimeMillis(),
                "likesCount" to 0,
                "comentarioCount" to 0,
                "imagemPost" to imagemBase64,
                "city" to city,
                "lat" to lat,
                "lng" to lng
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { e -> onResult(false, e.message) }
        }
    }

    private var ultimoDocumento: com.google.firebase.firestore.DocumentSnapshot? = null

    fun buscarPosts(carrregarMais: Boolean = false, onResult: (ArrayList<Post>, temMais: Boolean) -> Unit) {
        var query = db.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)

        if (carrregarMais && ultimoDocumento != null) {
            query = query.startAfter(ultimoDocumento!!)
        } else {
            ultimoDocumento = null
        }

        query.get()
            .addOnSuccessListener { documents ->
                val posts = ArrayList<Post>()
                for (document in documents) {
                    val post = Post(
                        postId = document.id,
                        userId = document.getString("userId") ?: "",
                        username = document.getString("username") ?: "",
                        texto = document.getString("texto") ?: "",
                        fotoPerfil = document.getString("fotoPerfil"),
                        imagemPost = document.getString("imagemPost"),
                        timestamp = document.getLong("timestamp") ?: 0L,
                        likesCount = document.getLong("likesCount")?.toInt() ?: 0,
                        comentarioCount = document.getLong("comentarioCount")?.toInt() ?: 0,
                        city = document.getString("city") ?: "",
                        lat = document.getDouble("lat") ?: 0.0,
                        lng = document.getDouble("lng") ?: 0.0
                    )
                    posts.add(post)
                }
                if (documents.size() > 0) {
                    ultimoDocumento = documents.documents.last()
                }
                onResult(posts, documents.size() >= 5)
            }
            .addOnFailureListener { onResult(ArrayList(), false) }
    }

    fun buscarPostsPorCidade(city: String, onResult: (ArrayList<Post>) -> Unit) {
        db.collection("posts")
            .whereEqualTo("city", city)
            .get()
            .addOnSuccessListener { documents ->
                val posts = ArrayList<Post>()
                for (document in documents) {
                    try {
                        val post = Post(
                            postId = document.id,
                            userId = document.getString("userId") ?: "",
                            username = document.getString("username") ?: "",
                            texto = document.getString("texto") ?: "",
                            fotoPerfil = document.getString("fotoPerfil"),
                            imagemPost = document.getString("imagemPost"),
                            timestamp = document.getLong("timestamp") ?: 0L,
                            likesCount = document.getLong("likesCount")?.toInt() ?: 0,
                            comentarioCount = document.getLong("comentarioCount")?.toInt() ?: 0,
                            city = document.getString("city") ?: "",
                            lat = document.getDouble("lat") ?: 0.0,
                            lng = document.getDouble("lng") ?: 0.0
                        )
                        posts.add(post)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                onResult(posts)
            }
            .addOnFailureListener { onResult(ArrayList()) }
    }

    fun deletarPost(postId: String, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        val email = auth.currentUser?.email
        if (uid == null) {
            onResult(false, "Usuário não autenticado")
            return
        }

        db.collection("posts").document(postId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    onResult(false, "Post não encontrado")
                    return@addOnSuccessListener
                }

                val userId = document.getString("userId")
                // Aceita tanto UID quanto email (compatibilidade com posts antigos)
                if (userId != uid && userId != email) {
                    onResult(false, "Você não pode deletar este post")
                    return@addOnSuccessListener
                }

                db.collection("posts").document(postId)
                    .delete()
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    fun atualizarPost(postId: String, novoTexto: String, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(false, "Usuário não autenticado")
            return
        }

        db.collection("posts").document(postId)
            .update("texto", novoTexto)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}