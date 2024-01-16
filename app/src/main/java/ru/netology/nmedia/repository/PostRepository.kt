package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getAllAsync(callback: RepositoryCallback<List<Post>>)
//    fun getByIdAsync(id: Long, callback: RepositoryCallback<Post>)
//    fun likePostAsync(id: Long, callback: RepositoryCallback<Boolean>)
//    fun unlikePostAsync(id: Long, callback: RepositoryCallback<Boolean>)
//    fun removeByIdAsync(id: Long, callback: RepositoryCallback<Boolean>)
//    fun saveAsync(post: Post, callback: RepositoryCallback<Post>)
//    fun share(id: Long)

    val data: LiveData<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun getById(id: Long): Post
    suspend fun likePost(id: Long): Post
    suspend fun unlikePost(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: Post): Post
//    suspend fun share(id: Long): Unit

//    interface RepositoryCallback<T> {
//        fun onSuccess(result: T)
//        fun onError(e: Exception)
//    }
}
