package ru.netology.nmedia.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.databinding.PostCardBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun like(post: Post)
    fun remove(post: Post)
    fun edit(post: Post)
    fun share(post: Post)
    fun playVideo(videoUrl: String?)
    fun onPostClicked(post: Post)
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

