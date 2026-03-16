package br.com.igor.microredesocial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarPerfil()
    }

    private fun carregarPerfil() {

        val firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email

        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .document(email!!)
            .get()
            .addOnSuccessListener { document ->

                if (document != null) {

                    val username = document.getString("username")
                    val fotoPerfil = document.getString("fotoPerfil")

                    binding.textUsername.text = username

                    if (fotoPerfil != null) {

                        val bitmap = Base64Converter.stringToBitmap(fotoPerfil)
                        binding.imageProfile.setImageBitmap(bitmap)

                    }

                }

            }
    }
}