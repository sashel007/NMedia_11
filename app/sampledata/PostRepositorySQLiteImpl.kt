package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(private val dao: PostDao) : PostRepository {
    private var posts = emptyList<Post>()
    private var data = MutableLiveData(posts)

//    init {
//        posts = dao.getAll()
//        data.value = posts
//    }

    override fun get(): LiveData<List<Post>> = data

    override fun getById(id: Long): Post? = posts.find { it.id == id }


    override fun like(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                val updatedLikeStatus = !it.likedByMe
                val updatedPost = it.copy(
                    likedByMe = updatedLikeStatus,
                    likes = if (updatedLikeStatus) it.likes + 1 else it.likes - 1
                )
                dao.like(updatedPost.id)
                updatedPost
            } else {
                it
            }
        }
        data.value = posts
    }

    override fun share(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                val updatedPost = post.copy(sharings = post.sharings + 1)
                dao.share(updatedPost.id)
                updatedPost
            } else {
                post
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
//        val id = post.id
//        val saved = dao.save(post)
//        posts = if (id == 0L) {
//            listOf(saved) + posts
//        } else {
//            posts.map {
//                if (it.id != id) it else saved
//            }
//        }
//        data.value = posts
    }
}