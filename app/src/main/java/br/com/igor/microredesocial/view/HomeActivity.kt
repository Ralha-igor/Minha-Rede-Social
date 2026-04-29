package br.com.igor.microredesocial.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.igor.microredesocial.R
import br.com.igor.microredesocial.adapter.PostAdapter
import br.com.igor.microredesocial.controller.AuthController
import br.com.igor.microredesocial.helper.Base64Converter
import br.com.igor.microredesocial.controller.PostController
import br.com.igor.microredesocial.controller.UserController
import br.com.igor.microredesocial.databinding.ActivityHomeBinding
import br.com.igor.microredesocial.model.Post
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val postController = PostController()
    private val userController = UserController()
    private val authController = AuthController()
    private val listaPosts = ArrayList<Post>()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PostAdapter(listaPosts) { postId, position ->
            postController.deletarPost(postId) { sucesso, erro ->
                runOnUiThread {
                    if (sucesso) {
                        adapter.removerPost(position)
                    } else {
                        Toast.makeText(this, erro ?: "Erro ao deletar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        carregarPerfil()
        carregarFeed()
        setupListeners()
    }

    private fun carregarPerfil() {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return
        userController.buscarPerfil(email) { user ->
            runOnUiThread {
                if (user != null) {
                    binding.textUsername.text = user.username
                    if (!user.fotoPerfil.isNullOrEmpty()) {
                        val bitmap = Base64Converter.stringToBitmap(user.fotoPerfil)
                        binding.imageProfile.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }

    private fun carregarFeed(carregarMais: Boolean = false) {
        postController.buscarPosts(carregarMais) { novos, temMais ->
            runOnUiThread {
                if (!carregarMais) listaPosts.clear()
                listaPosts.addAll(novos)
                adapter.notifyDataSetChanged()
                binding.btnCarregarFeed.text = if (temMais) "Carregar mais" else "Atualizar feed"
            }
        }
    }

    private fun buscarPorCidade(cidade: String) {
        val regex = Regex("^([A-ZÀ-Ÿ][a-zà-ÿ]*(\\s[A-ZÀ-Ÿ][a-zà-ÿ]*)*)$")

        if (!cidade.matches(regex)) {
            exibirAvisoFormato()
            return
        }

        postController.buscarPostsPorCidade(cidade) { posts ->
            runOnUiThread {
                listaPosts.clear()
                listaPosts.addAll(posts)
                adapter.notifyDataSetChanged()

                if (posts.isEmpty()) {
                    Toast.makeText(this, "Nenhum post encontrado em $cidade", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun exibirAvisoFormato() {
        val view = layoutInflater.inflate(R.layout.dialog_custom_aviso, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btnEntendi).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupListeners() {
        binding.btnCarregarFeed.setOnClickListener {
            val cidade = binding.editBuscarCidade.text.toString().trim()
            if (cidade.isEmpty()) carregarFeed(carregarMais = true)
            else buscarPorCidade(cidade)
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
            if (cidade.isEmpty()) carregarFeed()
            else buscarPorCidade(cidade)
        }
    }

    override fun onResume() {
        super.onResume()
        carregarFeed()
    }
}