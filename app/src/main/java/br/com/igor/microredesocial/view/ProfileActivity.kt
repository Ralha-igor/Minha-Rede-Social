package br.com.igor.microredesocial.view


import android.os.Bundle

import android.widget.Button

import android.widget.ImageView

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import br.com.igor.microredesocial.R

import com.google.android.material.textfield.TextInputEditText


class ProfileActivity : AppCompatActivity() {


// Declarando as variáveis que representam os componentes do XML

    private lateinit var editUsername: TextInputEditText

    private lateinit var editNomeCompleto: TextInputEditText

    private lateinit var editSenha: TextInputEditText // O erro estava aqui!

    private lateinit var imageProfile: ImageView

    private lateinit var buttonSalvar: Button

    private lateinit var buttonAlterarFoto: Button


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)


// 1. Inicializar os componentes ligando-os ao XML

        inicializarComponentes()


// 2. Carregar os dados atuais do usuário (Simulação)

        carregarDadosIniciais()


// 3. Configurar o clique do botão Salvar (Requisito RF3-3)

        buttonSalvar.setOnClickListener {

            executarEdicaoPerfil()

        }


// 4. Configurar clique para alterar foto

        buttonAlterarFoto.setOnClickListener {

// Lógica para abrir galeria será implementada a seguir

            Toast.makeText(this, "Abrir galeria de fotos", Toast.LENGTH_SHORT).show()

        }

    }


    private fun inicializarComponentes() {

// Certifique-se de que esses IDs são os mesmos do seu arquivo XML

        editUsername = findViewById(R.id.editUsername)

        editNomeCompleto = findViewById(R.id.editNomeCompleto)

        editSenha = findViewById(R.id.edtPassword) // Resolvendo a referência não resolvida

        imageProfile = findViewById(R.id.imageProfile)

        buttonSalvar = findViewById(R.id.buttonSalvar)

        buttonAlterarFoto = findViewById(R.id.buttonAlterarFoto)

    }


    private fun carregarDadosIniciais() {

// Aqui você buscaria do seu banco de dados

// Exemplo: editNomeCompleto.setText(usuarioLogado.nome)

    }


    private fun executarEdicaoPerfil() {

        val nome = editNomeCompleto.text.toString()

        val username = editUsername.text.toString()

        val senha = editSenha.text.toString()


// Validação básica

        if (nome.isEmpty() || username.isEmpty()) {

            Toast.makeText(this, "Preencha o nome e o usuário!", Toast.LENGTH_SHORT).show()

            return

        }


// Lógica de Salvamento (Aqui entraria seu Firebase ou SQLite)

// Se a senha não estiver vazia, atualizamos ela também

        if (senha.isNotEmpty() && senha.length < 6) {

            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()

            return

        }


// Simulação de sucesso

        Toast.makeText(this, "Perfil de $username atualizado com sucesso!", Toast.LENGTH_SHORT).show()

        finish() // Fecha a tela de edição e volta

    }

} 