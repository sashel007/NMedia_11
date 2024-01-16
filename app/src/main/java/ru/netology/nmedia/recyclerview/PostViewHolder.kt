package ru.netology.nmedia.recyclerview

import android.util.Log
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.functions.formatAmount
import ru.netology.nmedia.databinding.PostCardBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: PostCardBinding, private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
//        val videoCLickListener = View.OnClickListener {
//            onInteractionListener.playVideo(post.video)
//        }
        Log.d("POST_AVATAR", post.authorAvatar)
        binding.apply {
            author.text = post.author
//            published.text = post.published
            content.text = post.content
            likeIcon.isChecked = post.likedByMe
            likeIcon.text = formatAmount(post.likes)
            likeIcon.setOnClickListener {
                onInteractionListener.like(post)
            }
//            sharingIcon.setOnClickListener {
//                onInteractionListener.share(post)
//            }
//            sharingIcon.text = formatAmount(post.sharings)
//            if (post.video.isNullOrEmpty()) {
//                videoGroupViews.visibility = View.GONE
//            } else {
//                videoGroupViews.visibility = View.VISIBLE
//                playButton.setOnClickListener(videoCLickListener)
//                videoImage.setOnClickListener(videoCLickListener)
//            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.remove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.edit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
            clickableView.setOnClickListener {
                onInteractionListener.onPostClicked(post)
            }

        }
        getImageWithGlide(post.authorAvatar, binding.avatar)
        Log.d("POST_AVATAR", "${getImageWithGlide(post.authorAvatar, binding.avatar)}")
    }

    private fun getImageWithGlide(imageName: String, imageView: ImageView) {
        
        val url = "http://10.0.2.2:9999/avatars/$imageName"

        Glide
            .with(imageView)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .error(R.drawable.image_loading_error)
            .into(imageView)
    }
}