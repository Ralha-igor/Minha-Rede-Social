package br.com.igor.microredesocial.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.igor.microredesocial.helper.Base64Converter
import br.com.igor.microredesocial.databinding.PostItemBinding
import br.com.igor.microredesocial.model.Post

class PostAdapter(private val posts: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.textUsername.text = post.username
        holder.binding.textTexto.text = post.texto

        // ✅ CORREÇÃO AQUI: Alterado de 'localizacao' para 'city'
        // Também adicionamos o ícone de pin 📍 antes do texto para dar o charme final
        if (post.city.isNotEmpty()) {
            holder.binding.textLocalizacao.text = "📍 ${post.city}"
        } else {
            holder.binding.textLocalizacao.text = "📍 Localização não informada"
        }

        // 🔥 imagem do post (com proteção extra)
        if (!post.imagemPost.isNullOrEmpty() && post.imagemPost.length < 1000000) {
            val bitmap = Base64Converter.stringToBitmap(post.imagemPost)
            holder.binding.imagemPost.setImageBitmap(bitmap)
        } else {
            holder.binding.imagemPost.setImageBitmap(null)
        }

        // 🔥 foto perfil (com proteção)
        if (!post.fotoPerfil.isNullOrEmpty() && post.fotoPerfil.length < 1000000) {
            val bitmap = Base64Converter.stringToBitmap(post.fotoPerfil)
            holder.binding.imagemPerfil.setImageBitmap(bitmap)
        } else {
            holder.binding.imagemPerfil.setImageBitmap(null)
        }
        // Exemplo: "📍 Mountain View, CA" Geocodificação Completa
        if (post.city.isNotEmpty()) {
            holder.binding.textLocalizacao.text = "📍 ${post.city}, California"
        }
        if (post.city.isNotEmpty()) {
            // Exibe a cidade e as coordenadas em uma linha menor
            val localizacaoCompleta = "📍 ${post.city} (${post.lat}, ${post.lng})"
            holder.binding.textLocalizacao.text = localizacaoCompleta
        }
    }

    override fun getItemCount() = posts.size
}