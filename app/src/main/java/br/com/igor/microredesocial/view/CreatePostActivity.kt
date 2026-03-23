package br.com.igor.microredesocial.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.controller.PostController
import br.com.igor.microredesocial.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val postController = PostController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPostar.setOnClickListener {
            criarPost()
        }
    }

    private fun criarPost() {

        val texto = binding.editPost.text.toString()

        if (texto.isEmpty()) {
            Toast.makeText(this, "Escreva algo para postar", Toast.LENGTH_SHORT).show()
            return
        }

        postController.criarPost(texto) { sucesso, erro ->
            if (sucesso) {
                finish()
            } else {
                Toast.makeText(this, erro ?: "Erro ao criar post", Toast.LENGTH_SHORT).show()
            }
        }
    }
}