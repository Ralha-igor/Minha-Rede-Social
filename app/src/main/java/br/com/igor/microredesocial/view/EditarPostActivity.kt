package br.com.igor.microredesocial.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.igor.microredesocial.controller.PostController
import br.com.igor.microredesocial.databinding.ActivityEditarPostBinding

class EditarPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPostBinding
    private val postController = PostController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o View Binding apontando para o layout correto
        binding = ActivityEditarPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Resgata os dados enviados pela tela anterior
        val postId = intent.getStringExtra("postId") ?: return
        val textoAtual = intent.getStringExtra("textoAtual") ?: ""

        // Insere o texto antigo no campo de edição
        binding.editTextoPost.setText(textoAtual)

        // Salvar alterações
        binding.btnSalvarEdicao.setOnClickListener {
            salvarEdicao(postId)
        }

        // Cancelar e voltar
        binding.btnCancelarEdicao.setOnClickListener {
            finish()
        }
    }

    private fun salvarEdicao(postId: String) {
        val novoTexto = binding.editTextoPost.text.toString().trim()

        if (novoTexto.isEmpty()) {
            Toast.makeText(this, "O texto não pode estar vazio", Toast.LENGTH_SHORT).show()
            return
        }

        postController.atualizarPost(postId, novoTexto) { sucesso, erro ->
            runOnUiThread {
                if (sucesso) {
                    Toast.makeText(this, "Post atualizado!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, erro ?: "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}