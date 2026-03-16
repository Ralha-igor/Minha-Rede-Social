package br.com.igor.microredesocial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.databinding.ActivityCreatePostBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPostar.setOnClickListener {
            criarPost()
        }
    }

    private fun criarPost(){

        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser!!.email

        val texto = binding.editPost.text.toString()

        val db = Firebase.firestore

        val post = hashMapOf(
            "userId" to email,
            "texto" to texto,
            "timestamp" to System.currentTimeMillis(),
            "likesCount" to 0,
            "comentariosCount" to 0
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                finish()
            }

    }
}