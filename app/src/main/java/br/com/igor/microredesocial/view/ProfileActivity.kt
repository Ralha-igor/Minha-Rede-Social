package br.com.igor.microredesocial.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.igor.microredesocial.R
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProfileActivity : AppCompatActivity() {

    private lateinit var editUsername: TextInputEditText
    private lateinit var editNomeCompleto: TextInputEditText
    private lateinit var editSenha: TextInputEditText
    private lateinit var imageProfile: ImageView
    private lateinit var buttonSalvar: Button
    private lateinit var buttonAlterarFoto: Button

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadFotoParaFirebase(it) }
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pickImage.launch("image/*")
        else Toast.makeText(this, "Permissão necessária para acessar a galeria", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        inicializarComponentes()
        carregarDadosIniciais()

        buttonSalvar.setOnClickListener { executarEdicaoPerfil() }
        buttonAlterarFoto.setOnClickListener { abrirGaleria() }
    }

    private fun inicializarComponentes() {
        editUsername = findViewById(R.id.editUsername)
        editNomeCompleto = findViewById(R.id.editNomeCompleto)
        editSenha = findViewById(R.id.edtPassword)
        imageProfile = findViewById(R.id.imageProfile)
        buttonSalvar = findViewById(R.id.buttonSalvar)
        buttonAlterarFoto = findViewById(R.id.buttonAlterarFoto)
    }

    private fun carregarDadosIniciais() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("usuarios").document(uid).get()
            .addOnSuccessListener { doc ->
                editUsername.setText(doc.getString("username") ?: "")
                editNomeCompleto.setText(doc.getString("nomeCompleto") ?: "")

                val fotoBase64 = doc.getString("fotoPerfil")
                if (!fotoBase64.isNullOrEmpty()) {
                    val bytes = android.util.Base64.decode(
                        fotoBase64.removePrefix("data:image/jpeg;base64,"),
                        android.util.Base64.DEFAULT
                    )
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageProfile.setImageBitmap(bitmap)
                }
            }
    }

    private fun abrirGaleria() {
        val permissao = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permissao) == PackageManager.PERMISSION_GRANTED) {
            pickImage.launch("image/*")
        } else {
            requestPermission.launch(permissao)
        }
    }

    private fun uploadFotoParaFirebase(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val bytes = inputStream.readBytes()

            val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val output = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 50, output)
            val base64 = android.util.Base64.encodeToString(output.toByteArray(), android.util.Base64.DEFAULT)

            Toast.makeText(this, "Salvando foto...", Toast.LENGTH_SHORT).show()

            firestore.collection("usuarios").document(uid)
                .set(mapOf("fotoPerfil" to "data:image/jpeg;base64,$base64"), SetOptions.merge())
                .addOnSuccessListener {
                    Glide.with(this).load(uri).circleCrop().into(imageProfile)
                    Toast.makeText(this, "Foto atualizada!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao processar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executarEdicaoPerfil() {
        val nome = editNomeCompleto.text.toString()
        val username = editUsername.text.toString()
        val senha = editSenha.text.toString()

        if (nome.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Preencha o nome e o usuário!", Toast.LENGTH_SHORT).show()
            return
        }

        if (senha.isNotEmpty() && senha.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = auth.currentUser?.uid ?: return
        val dados = mapOf("username" to username, "nomeCompleto" to nome)

        firestore.collection("usuarios").document(uid)
            .set(dados, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}