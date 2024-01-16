package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl(private val context: Context) : PostRepository {

    // Создание экземпляра Gson для сериализации/десериализации данных
    private val gson = Gson()
    // Инициализация переменных для хранения следующего идентификатора и списка постов
    private var nextId = 0L
    private var posts = emptyList<Post>()
    // Имена файлов для сохранения данных на диске
    private val postsFileName = "posts.json"
    private val nextIdFileName = "next_id.json"
    // Тип, который будет использоваться для десериализации списка постов
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    // LiveData для хранения постов, которые будут обновляться
    private val data = MutableLiveData(posts)

    // Инициализация блока для загрузки данных из файлов при создании экземпляра репозитория
    init {
        // Попытка прочитать файл с постами
        val postsFile = context.filesDir.resolve(postsFileName)
        // Если файл существует, считываем и десериализуем данные, иначе используем пустой список
        posts = if (postsFile.exists()) {
            postsFile.reader().buffered().use {// здесь происходит десериализация
                gson.fromJson(it, type)
            }
        } else {
            emptyList()
        }
        // По аналогии считываем следующий идентификатор из файла
        val nextIdFile = context.filesDir.resolve(nextIdFileName)
        nextId = if (nextIdFile.exists()) {
            nextIdFile.reader().buffered().use {
                gson.fromJson(it, Long::class.java)
            }
        } else {
            nextId
        }
        // Обновляем LiveData новыми данными
        data.value = posts
    }

    override fun get(): LiveData<List<Post>> = data

    override fun getById(id: Long): Post? = posts.find { it.id == id }
    override fun like(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                it.copy(
                    likedByMe = !it.likedByMe,
                    likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
                )
            } else {
                it
            }
        }
        data.value = posts
        sync()
    }

    override fun share(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(sharings = post.sharings + 1)
            } else {
                post
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(post.copy(id = nextId++, author = "Me", published = "Now")) + posts
        } else {
            posts.map { if (it.id != post.id) it else it.copy(content = post.content) }
        }
        data.value = posts
        sync()
    }

    // Метод для синхронизации данных с файловой системой
    private fun sync() {
        // 1. Определение пути к файлу posts.json
        context.filesDir.resolve(postsFileName).writer().buffered().use {
            // 2. Сериализация списка постов в формат JSON и запись в файл posts.json
            it.write(gson.toJson(posts))
        }
        // 3. Определение пути к файлу next_id.json
        context.filesDir.resolve(nextIdFileName).writer().buffered().use {
            // 4. Сериализация значения nextId в формат JSON и запись в файл next_id.json
            it.write(gson.toJson(nextId))
        }
    }
}