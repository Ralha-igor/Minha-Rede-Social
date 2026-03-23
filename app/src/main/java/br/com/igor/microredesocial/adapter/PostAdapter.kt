package br.com.igor.microredesocial.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.igor.microredesocial.controller.Base64Converter
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

        if (post.imagemPost != null) {
            val bitmap = Base64Converter.stringToBitmap(post.imagemPost)
            holder.binding.imagemPost.setImageBitmap(bitmap)
        }

        if (post.fotoPerfil != null) {
            val bitmap = Base64Converter.stringToBitmap(post.fotoPerfil)
            holder.binding.imagemPerfil.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount() = posts.size
}