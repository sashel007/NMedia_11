package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.functions.formatAmount
import ru.netology.nmedia.databinding.TargetPostLayoutBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class TargetPostFragment : Fragment() {
    private val sharedViewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = TargetPostLayoutBinding.inflate(inflater, container, false)
        val interactionListener = sharedViewModel.getInteractionListener()
        val args: NewPostFragmentArgs by navArgs()
        val postId = args.postId

        // Получаем данные из репозитория по postId
//        sharedViewModel.getPostById(postId).observe(viewLifecycleOwner, Observer { post ->
//                binding.apply {
//                    includedPostCard.author.text = post.author
//                    includedPostCard.published.text = post.published
//                    includedPostCard.content.text = post.content
//                    includedPostCard.likeIcon.isChecked = post.likedByMe
//                    includedPostCard.likeIcon.text = formatAmount(post.likes)
//                    includedPostCard.likeIcon.setOnClickListener {
//                        interactionListener?.like(post)
//                    }
//                    includedPostCard.sharingIcon.setOnClickListener {
//                        interactionListener?.share(post)
//                    }
//                    includedPostCard.sharingIcon.text = formatAmount(post.sharings)
//
//                    if (post.video.isNullOrEmpty()) {
//                        includedPostCard.videoGroupViews.visibility = View.GONE
//                    } else {
//                        includedPostCard.videoGroupViews.visibility = View.VISIBLE
//                        includedPostCard.playButton.setOnClickListener {
//                            interactionListener?.playVideo(post.video)
//                        }
//                        includedPostCard.videoImage.setOnClickListener {
//                            interactionListener?.playVideo(post.video)
//                        }
//                    }
//                    includedPostCard.menu.setOnClickListener {
//                        PopupMenu(it.context, it).apply {
//                            inflate(R.menu.menu_options)
//                            setOnMenuItemClickListener { item ->
//                                when (item.itemId) {
//                                    R.id.remove -> {
//                                        interactionListener?.remove(post)
//                                        findNavController().navigateUp()
//                                        true
//                                    }
//                                    R.id.edit -> {
//                                        val action = R.id.action_targetPostFragment_to_newPostFragment
//                                        val bundle = bundleOf(
//                                            "postId" to post.id,
//                                            "postContent" to post.content
//                                        )
//                                        findNavController().navigate(action, bundle)
//                                        true
//                                    }
//                                    else -> false
//                                }
//                            }
//                        }.show()
//
//                    }
//                }
//            })
        return binding.root
    }
}
