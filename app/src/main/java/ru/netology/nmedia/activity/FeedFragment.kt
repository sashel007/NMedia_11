package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.recyclerview.OnInteractionListener
import ru.netology.nmedia.recyclerview.PostAdapter
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val viewModel: PostViewModel by activityViewModels()

        val interactionListener = object : OnInteractionListener {
            override fun like(post: Post) {
                viewModel.also {
                    it.like(post.id)
                    it.errorMessages.observe(viewLifecycleOwner) { errorMessage ->
                        if (errorMessage != null) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                .show()
                            // Сброс значения на null во избежание срабатывания при любых событиях
                            it.clearErrorMessage()
                        }
                    }
                }

            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                val action = R.id.action_feedFragment_to_newPostFragment
                val bundle = bundleOf(
                    "postId" to post.id,
                    "postContent" to post.content
                )
                findNavController().navigate(action, bundle)
            }

            override fun share(post: Post) {
                viewModel.share(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun playVideo(videoUrl: String?) {
                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(videoUrl)
                }
                val shareIntent = Intent.createChooser(intent, "Выберите приложение")
                startActivity(shareIntent)
            }

            override fun onPostClicked(post: Post) {
//                val action = R.id.action_feedFragment_to_targetPostFragment
//                val bundle = bundleOf("postId" to post.id)
//                findNavController().navigate(action, bundle)
            }

        }

        viewModel.setInteractionListener(interactionListener)

        val adapter = PostAdapter(interactionListener)
        findNavController().navigateUp()

        binding.postList?.layoutManager = LinearLayoutManager(requireContext())
        binding.postList?.adapter = adapter
        //  аргумент state - экземпляр FeedModelState()
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress?.isVisible = state.loading
            binding.swipeContainer?.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT)
                    .setAction("Повторите снова") { viewModel.loadPosts() }
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { feedModel ->
            adapter.submitList(feedModel.posts)
            binding.emptyText?.isVisible = feedModel.empty
        }

        binding.swipeContainer?.setOnRefreshListener {
            viewModel.loadPosts()
        }

//        viewModel.data.observe(viewLifecycleOwner) { state ->
//            val newPost = state.posts.size > adapter.currentList.size
//            adapter.submitList(state.posts) {
//                if (newPost) binding.postList?.smoothScrollToPosition(0)
//            }
//            binding.progress?.isVisible = state.loading
//            binding.errorGroup?.isVisible = state.error
//            binding.emptyText?.isVisible = state.empty
//        }

//        binding.retryButton?.setOnClickListener {
//            viewModel.loadPosts()
//        }

        binding.addButton?.setOnClickListener {
            val action = R.id.action_feedFragment_to_newPostFragment
            val bundle = bundleOf(
                "postId" to 0L,
                "postContent" to ""
            )
            findNavController().navigate(action, bundle)
        }

        return binding.root
    }

}