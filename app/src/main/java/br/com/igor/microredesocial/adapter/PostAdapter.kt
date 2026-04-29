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
    private val onDeletar: (postId: String, position: Int) -> Unit
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

        holder.binding.textUsername.text = post.username
        holder.binding.textTexto.text = post.texto
        holder.binding.textLocalizacao.text = if (post.city.isNotEmpty()) "📍 ${post.city}" else ""
        holder.binding.textCoordenadas.text = if (post.lat != 0.0) "Lat: ${post.lat}, Lng: ${post.lng}" else ""

        if (!post.imagemPost.isNullOrEmpty()) {
            try {
                val bitmap = Base64Converter.stringToBitmap(post.imagemPost)
                holder.binding.imagemPost.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.binding.imagemPost.setImageBitmap(null)
            }
        } else {
            holder.binding.imagemPost.setImageBitmap(null)
        }

        if (!post.fotoPerfil.isNullOrEmpty()) {
            try {
                val bitmap = Base64Converter.stringToBitmap(post.fotoPerfil)
                holder.binding.imagemPerfil.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.binding.imagemPerfil.setImageBitmap(null)
            }
        } else {
            holder.binding.imagemPerfil.setImageBitmap(null)
        }

        // Mostra botão deletar só para o dono do post
        if (post.userId == emailAtual) {
            holder.binding.btnDeletar.visibility = View.VISIBLE
            holder.binding.btnDeletar.setOnClickListener {
                onDeletar(post.postId, position)
            }
        } else {
            holder.binding.btnDeletar.visibility = View.GONE
        }
    }

    override fun getItemCount() = posts.size

    fun removerPost(position: Int) {
        posts.removeAt(position)
        notifyItemRemoved(position)
    }
}