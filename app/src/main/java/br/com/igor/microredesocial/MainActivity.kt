package br.com.igor.microredesocial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebase()
        setupListeners()
    }

    private fun setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupListeners() {

        binding.btnLogin.setOnClickListener {
            autenticarUsuario()
        }

        binding.btnGoToSignUp.setOnClickListener {

            startActivity(
                Intent(this, SignUpActivity::class.java)
            )

        }
    }

    private fun autenticarUsuario() {

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                this,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    startActivity(
                        Intent(this, HomeActivity::class.java)
                    )

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Erro no login",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
    }
}