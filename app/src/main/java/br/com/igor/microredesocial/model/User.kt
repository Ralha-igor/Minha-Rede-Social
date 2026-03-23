package br.com.igor.microredesocial.model

data class User(
    val email: String = "",
    val username: String = "",
    val nomecompleto: String = "",
    val fotoPerfil: String? = null,
    val dataCriacao: Long = 0L
)