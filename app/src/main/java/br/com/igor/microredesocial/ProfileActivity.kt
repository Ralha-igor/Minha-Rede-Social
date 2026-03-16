package br.com.igor.microredesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    // ActivityResult para abrir a galeria
    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->

        if (uri != null) {

            binding.imageProfile.setImageURI(uri)

        } else {

            Toast.makeText(
                this,
                "Nenhuma foto selecionada",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão para abrir a galeria
        binding.buttonAlterarFoto.setOnClickListener {

            galeria.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.buttonSalvar.setOnClickListener {

            val firebaseAuth = FirebaseAuth.getInstance()

            if (firebaseAuth.currentUser != null){

                val email = firebaseAuth.currentUser!!.email.toString()
                val username = binding.editUsername.text.toString()
                val nomeCompleto = binding.editNomeCompleto.text.toString()

                val fotoPerfilString = Base64Converter.drawableToString(binding.imageProfile.drawable)

                val db = Firebase.firestore

                val dados = hashMapOf(
                    "username" to username,
                    "nomeCompleto" to nomeCompleto,
                    "email" to email,
                    "fotoPerfil" to fotoPerfilString,
                    "dataCriacao" to System.currentTimeMillis()
                )

                db.collection("usuarios")
                    .document(email)
                    .set(dados)
                    .addOnSuccessListener {

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()

                    }
            }
        }
    }
}