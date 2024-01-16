package ru.netology.nmedia.api

import com.google.firebase.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository

//private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"


interface PostApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likePost(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun unlikePost(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

//    fun getAllAsync(callback: PostRepository.RepositoryCallback<List<Post>>)
//    fun getByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Post>)
//    fun likePostAsync(id: Long, callback: PostRepository.RepositoryCallback<Boolean>)
//    fun unlikePostAsync(id: Long, callback: PostRepository.RepositoryCallback<Boolean>)
//    fun removeByIdAsync(id: Long, callback: PostRepository.RepositoryCallback<Boolean>)
//    fun saveAsync(post: Post, callback: PostRepository.RepositoryCallback<Post>)
//    fun share(id: Long)

}

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object PostsApi {
    val retrofitService: PostApiService by lazy {
        retrofit.create(PostApiService::class.java)
    }
}