package br.com.igor.microredesocial.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.controller.AuthController
import br.com.igor.microredesocial.databinding.ActivityMainBinding

class   MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authController = AuthController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {

        binding.btnLogin.setOnClickListener {
            autenticarUsuario()
        }

        binding.btnGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun autenticarUsuario() {

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        authController.login(email, password) { sucesso, erro ->
            if (sucesso) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, erro ?: "Erro no login", Toast.LENGTH_SHORT).show()
            }
        }
    }
}