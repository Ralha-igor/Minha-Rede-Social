package br.com.igor.microredesocial.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.igor.microredesocial.adapter.PostAdapter
import br.com.igor.microredesocial.controller.AuthController
import br.com.igor.microredesocial.controller.Base64Converter
import br.com.igor.microredesocial.controller.PostController
import br.com.igor.microredesocial.controller.UserController
import br.com.igor.microredesocial.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val postController = PostController()
    private val userController = UserController()
    private val authController = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarPerfil()
        setupListeners()
    }

    private fun carregarPerfil() {

        val email = FirebaseAuth.getInstance().currentUser?.email ?: return

        userController.buscarPerfil(email) { user ->
            if (user != null) {
                binding.textUsername.text = user.username

                if (user.fotoPerfil != null) {
                    val bitmap = Base64Converter.stringToBitmap(user.fotoPerfil)
                    binding.imageProfile.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun buscarPorCidade(cidade: String) {
        postController.buscarPostsPorCidade(cidade) { posts ->
            val adapter = PostAdapter(posts)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun setupListeners() {

        binding.btnCarregarFeed.setOnClickListener {
            carregarFeed()
        }

        binding.btnNovoPost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            authController.logout()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnBuscarCidade.setOnClickListener {
            val cidade = binding.editBuscarCidade.text.toString().trim()
            if (cidade.isEmpty()) {
                carregarFeed()
            } else {
                buscarPorCidade(cidade)
            }
        }
    }

    private fun carregarFeed() {

        postController.buscarPosts { posts ->
            val adapter = PostAdapter(posts)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }
    }

}