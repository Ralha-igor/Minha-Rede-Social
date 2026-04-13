package br.com.igor.microredesocial.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.helper.Base64Converter
import br.com.igor.microredesocial.controller.UserController
import br.com.igor.microredesocial.databinding.ActivityProfileBinding
import br.com.igor.microredesocial.model.User
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val userController = UserController()

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.imageProfile.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAlterarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.buttonSalvar.setOnClickListener {
            salvarPerfil()
        }
    }

    private fun salvarPerfil() {

        val email = FirebaseAuth.getInstance().currentUser?.email

        if (email == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val username = binding.editUsername.text.toString()
        val nomeCompleto = binding.editNomeCompleto.text.toString()

        if (username.isEmpty() || nomeCompleto.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        val drawable = binding.imageProfile.drawable
        val fotoPerfilString = if (drawable != null && drawable is android.graphics.drawable.BitmapDrawable) {
            Base64Converter.drawableToString(drawable)
        } else {
            ""
        }

        val user = User(
            email = email,
            username = username,
            nomecompleto = nomeCompleto,
            fotoPerfil = fotoPerfilString,
            dataCriacao = System.currentTimeMillis()
        )

        userController.salvarPerfil(user) { sucesso, erro ->
            if (sucesso) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, erro ?: "Erro ao salvar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}