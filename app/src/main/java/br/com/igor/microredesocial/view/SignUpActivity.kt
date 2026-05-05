package br.com.igor.microredesocial.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.controller.UserController
import br.com.igor.microredesocial.databinding.ActivitySignUpBinding
import br.com.igor.microredesocial.model.User
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnCreateAccount.setOnClickListener {

            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                        val userController = UserController()
                        val novoUsuario = User(
                            email = email,
                            username = "",
                            nomecompleto = "",
                            fotoPerfil = null,
                            dataCriacao = System.currentTimeMillis()
                        )

                        userController.salvarPerfil(uid, novoUsuario) { sucesso, erro ->
                            if (sucesso) {
                                startActivity(Intent(this, ProfileActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, erro ?: "Erro ao salvar perfil", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }
    }
}