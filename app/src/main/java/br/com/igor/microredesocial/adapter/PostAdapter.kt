package br.com.igor.microredesocial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.igor.microredesocial.databinding.PostItemBinding
import br.com.igor.microredesocial.helper.Base64Converter
import br.com.igor.microredesocial.model.Post
import com.google.firebase.auth.FirebaseAuth

class PostAdapter(
    private val posts: ArrayList<Post>,
    private val onDeletar: (postId: String, position: Int) -> Unit,
    private val onEditar: (post: Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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
        val emailAtual = FirebaseAuth.getInstance().currentUser?.email

        bindTextos(holder, post)
        bindImagemPost(holder, post)
        bindImagemPerfil(holder, post)
        bindBotoesAcao(holder, post, position, emailAtual)
    }

    private fun bindTextos(holder: PostViewHolder, post: Post) {
        holder.binding.textUsername.text = post.username
        holder.binding.textTexto.text = post.texto
        holder.binding.textLocalizacao.text =
            if (post.city.isNotEmpty()) "📍 ${post.city}" else ""
        holder.binding.textCoordenadas.text =
            if (post.lat != 0.0) "Lat: ${post.lat}, Lng: ${post.lng}" else ""
    }

    private fun bindImagemPost(holder: PostViewHolder, post: Post) {
        val bitmap = post.imagemPost
            ?.takeIf { it.isNotEmpty() }
            ?.let { runCatching { Base64Converter.stringToBitmap(it) }.getOrNull() }
        holder.binding.imagemPost.setImageBitmap(bitmap)
    }

    private fun bindImagemPerfil(holder: PostViewHolder, post: Post) {
        val bitmap = post.fotoPerfil
            ?.takeIf { it.isNotEmpty() }
            ?.let { runCatching { Base64Converter.stringToBitmap(it) }.getOrNull() }
        holder.binding.imagemPerfil.setImageBitmap(bitmap)
    }

    private fun bindBotoesAcao(
        holder: PostViewHolder,
        post: Post,
        position: Int,
        emailAtual: String?
    ) {
        val isDono = post.userId == emailAtual

        holder.binding.btnDeletar.visibility = if (isDono) View.VISIBLE else View.GONE
        holder.binding.btnEditar.visibility = if (isDono) View.VISIBLE else View.GONE

        if (isDono) {
            holder.binding.btnDeletar.setOnClickListener {
                onDeletar(post.postId, position)
            }
            holder.binding.btnEditar.setOnClickListener {
                onEditar(post)
            }
        }
    }

    override fun getItemCount() = posts.size

    fun removerPost(position: Int) {
        posts.removeAt(position)
        notifyItemRemoved(position)
    }
}