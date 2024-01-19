package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException

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
        dao.like(id)
        return try {
            val response: Response<Post> = PostsApi.retrofitService.likePost(id)
            if (!response.isSuccessful) {
                // В случае неудачи откатываем изменения в базе данных
                dao.like(id)
                throw ApiError(response.code(), response.message())
            }
            val likedPost: Post =
                response.body() ?: throw ApiError(response.code(), response.message())
            likedPost
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unlikePost(id: Long): Post {
        dao.like(id)
        return try {
            val response: Response<Post> = PostsApi.retrofitService.likePost(id)
            if (!response.isSuccessful) {
                // В случае неудачи откатываем изменения в базе данных
                dao.like(id)
                throw ApiError(response.code(), response.message())
            }
            val likedPost: Post =
                response.body() ?: throw ApiError(response.code(), response.message())
            likedPost
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        // Получаем копию поста перед удалением
        val postToDelete = dao.getByIdSync(id)
        dao.removeById(id)
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                // Если запрос на сервере не удался, восстанавливаем пост в базе данных
                postToDelete?.let { dao.insert(it) }
                throw ApiError(response.code(), response.message())
            }
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
