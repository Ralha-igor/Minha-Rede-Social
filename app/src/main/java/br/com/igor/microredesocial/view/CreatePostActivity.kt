package br.com.igor.microredesocial.view

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import br.com.igor.microredesocial.helper.LocationController
import br.com.igor.microredesocial.controller.PostController
import br.com.igor.microredesocial.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val postController = PostController()
    private lateinit var locationController: LocationController

    private var imagemSelecionada: Uri? = null
    private var cidadeAtual: String = ""
    private var latAtual: Double = 0.0
    private var lngAtual: Double = 0.0

    // Launcher para abrir a galeria
    private val galeriaLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imagemSelecionada = uri
                binding.imagePreview.setImageURI(uri)
                binding.imagePreview.visibility = android.view.View.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationController = LocationController(this)

        binding.buttonSelecionarImagem.setOnClickListener {
            galeriaLauncher.launch("image/*")
        }

        binding.buttonLocalizacao.setOnClickListener {
            solicitarLocalizacao()
        }

        binding.buttonPostar.setOnClickListener {
            criarPost()
        }
    }

    private fun solicitarLocalizacao() {
        val permissao = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permissao), 1001)
            return
        }

        binding.textCidade.text = "Detectando localização..."

        locationController.obterLocalizacao { dados ->
            runOnUiThread {
                if (dados != null) {
                    cidadeAtual = dados.city
                    latAtual = dados.lat
                    lngAtual = dados.lng
                    binding.textCidade.text = "Cidade: ${dados.city}"
                } else {
                    binding.textCidade.text = "Não foi possível detectar"
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            solicitarLocalizacao()
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun criarPost() {
        val texto = binding.editPost.text.toString()

        if (texto.isEmpty()) {
            Toast.makeText(this, "Escreva algo para postar", Toast.LENGTH_SHORT).show()
            return
        }

        binding.buttonPostar.isEnabled = false

        postController.criarPost(
            texto = texto,
            imageView = binding.imagePreview,  // <- passa o ImageView
            city = cidadeAtual,
            lat = latAtual,
            lng = lngAtual
        ) { sucesso, erro ->
            runOnUiThread {
                binding.buttonPostar.isEnabled = true
                if (sucesso) {
                    finish()
                } else {
                    Toast.makeText(this, erro ?: "Erro ao criar post", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}