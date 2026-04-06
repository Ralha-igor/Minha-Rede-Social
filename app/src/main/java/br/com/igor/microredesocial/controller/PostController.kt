    package br.com.igor.microredesocial.controller

    import br.com.igor.microredesocial.model.Post
    import android.net.Uri
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.storage.FirebaseStorage
    import java.util.UUID

    class PostController {

        private val db = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()
        private val storage = FirebaseStorage.getInstance()

        // Agora recebe imageUri e dados de localização opcionais
        fun criarPost(
            texto: String,
            imageUri: Uri? = null,
            city: String = "",
            lat: Double = 0.0,
            lng: Double = 0.0,
            onResult: (Boolean, String?) -> Unit
        ) {
            val email = auth.currentUser?.email
            if (email == null) {
                onResult(false, "Usuário não autenticado")
                return
            }

            if (imageUri != null) {
                // Se tem imagem, faz upload primeiro, depois salva o post
                uploadImagem(imageUri) { urlImagem, erro ->
                    if (erro != null) {
                        onResult(false, erro)
                        return@uploadImagem
                    }
                    salvarPost(email, texto, urlImagem, city, lat, lng, onResult)
                }
            } else {
                // Sem imagem, salva direto
                salvarPost(email, texto, null, city, lat, lng, onResult)
            }
        }

        private fun uploadImagem(uri: Uri, onResult: (String?, String?) -> Unit) {
            val nomeArquivo = UUID.randomUUID().toString()
            val ref = storage.reference.child("posts/$nomeArquivo.jpg")

            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { url ->
                        onResult(url.toString(), null)
                    }
                }
                .addOnFailureListener { e ->
                    onResult(null, e.message)
                }
        }

        private fun salvarPost(
            email: String,
            texto: String,
            imagemUrl: String?,
            city: String,
            lat: Double,
            lng: Double,
            onResult: (Boolean, String?) -> Unit
        ) {
            val post = hashMapOf(
                "userId" to email,
                "texto" to texto,
                "timestamp" to System.currentTimeMillis(),
                "likesCount" to 0,
                "comentarioCount" to 0,
                "imagemPost" to (imagemUrl ?: ""),
                "city" to city,
                "lat" to lat,
                "lng" to lng
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
                            comentarioCount = document.getLong("comentarioCount")?.toInt() ?: 0,
                            city = document.getString("city") ?: "",
                            lat = document.getDouble("lat") ?: 0.0,
                            lng = document.getDouble("lng") ?: 0.0
                        )
                        posts.add(post)
                    }
                    onResult(posts)
                }
                .addOnFailureListener {
                    onResult(ArrayList())
                }
        }

        // Sprint 6 — busca por cidade
        fun buscarPostsPorCidade(city: String, onResult: (ArrayList<Post>) -> Unit) {
            db.collection("posts")
                .whereEqualTo("city", city)
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
                            comentarioCount = document.getLong("comentarioCount")?.toInt() ?: 0,
                            city = document.getString("city") ?: "",
                            lat = document.getDouble("lat") ?: 0.0,
                            lng = document.getDouble("lng") ?: 0.0
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