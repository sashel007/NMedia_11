package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll().map { postEntityList ->
        postEntityList.map(PostEntity::toDto)
    }

    override suspend fun getAll(): List<Post> {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            // Преобразование List<Post> в List<PostEntity>
            val postListEntity = body.map { post ->
                PostEntity.toEntity(post)
            }
            // Вставка списка объектов сущности в базу данных
            dao.insert(postListEntity)

            return body
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long): Post {
        try {
            val response = PostsApi.retrofitService.getById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post: Post = response.body() ?: throw ApiError(response.code(), response.message())
            val postEntity: PostEntity = PostEntity.toEntity(post)
            dao.insert(postEntity)
            return post
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likePost(id: Long): Post {
        try {
            val response: Response<Post> = PostsApi.retrofitService.likePost(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val likedPost: Post =
                response.body() ?: throw ApiError(response.code(), response.message())
            val likedPostEntity = PostEntity.toEntity(likedPost)
            dao.like(likedPostEntity.id)
            return likedPost
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unlikePost(id: Long): Post {
        try {
            val response: Response<Post> = PostsApi.retrofitService.likePost(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val likedPost: Post =
                response.body() ?: throw ApiError(response.code(), response.message())
            val likedPostEntity = PostEntity.toEntity(likedPost)
            dao.like(likedPostEntity.id)
            return likedPost
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun save(post: Post): Post {
        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val post = response.body() ?: throw ApiError(response.code(), response.message())
            val postEntity = PostEntity.toEntity(post)
            dao.save(postEntity)
            return post
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}

sealed class AppError(var code: String) : java.lang.RuntimeException()
class ApiError(val status: Int, code: String) : AppError(code)
data object NetworkError : AppError(code = "error_network")
data object UnknownError : AppError(code = "error_unknown")
