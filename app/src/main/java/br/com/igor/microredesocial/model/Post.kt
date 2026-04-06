package br.com.igor.microredesocial.model

data class Post(
    val userId: String = "",
    val username: String = "",
    val texto: String = "",
    val fotoPerfil: String? = null,
    val imagemPost: String? = null,
    val timestamp: Long = 0L,
    val likesCount: Int = 0,
    val comentarioCount: Int = 0,

    // Sprint 6 (localização)
    val city: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)