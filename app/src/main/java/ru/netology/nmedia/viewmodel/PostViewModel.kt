package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.SingleLiveEvent
import ru.netology.nmedia.recyclerview.OnInteractionListener
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

private val empty = Post(
    id = 0L,
    author = "Евгений",
    authorAvatar = "",
    content = "",
    published = "Now",
    likedByMe = false,
    likes = 0
//    sharings = 0,
//    video = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

//    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)

    private val _data = MutableLiveData<FeedModel>()
    val data: LiveData<FeedModel> = _data

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState


    private var interactionListener: OnInteractionListener? = null

    // Обработка ошибок на сервере, механизм Event
    private val _errorMessages = MutableLiveData<String?>()
    val errorMessages: LiveData<String?>
        get() = _errorMessages

    // Статус загрузки в ожидании ответа от сервера
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadPosts()

        // Наблюдаем за изменениями в LiveData из репозитория
        repository.data.observeForever { posts ->
            // Преобразуем List<Post> в FeedModel и обновляем _data
            _data.postValue(FeedModel(posts, posts.isEmpty()))
        }

    }

    // Функции для установки обработчика взаимодействий и переменная для хранения
    fun setInteractionListener(listener: OnInteractionListener) {
        this.interactionListener = listener
    }

    fun getInteractionListener(): OnInteractionListener? {
        return interactionListener
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun like(id: Long) = viewModelScope.launch {
        // Получаем текущий список постово
        val currentPosts = data.value?.posts.orEmpty()

        // Получаем текущий пост
        val currentPost = currentPosts.find { it.id == id }

        currentPost?.let { post ->
            // Асинхронное обновление на сервере
            try {
                if (post.likedByMe) {
                    repository.unlikePost(id)
                } else {
                    repository.likePost(id)
                }
            } catch (e: Throwable) {
                // Если запрос не удался, откатываем изменения в UI
                val errorMessage = "Ошибка при нажатии кнопки \"Лайк\". Повторите снова"
                _errorMessages.postValue(errorMessage)
            }
        }
    }

    fun share(id: Long) {}

    fun removeById(id: Long) = viewModelScope.launch {
        // Оптимистичная модель: предполагаем, что пост удален
        val oldPosts = data.value?.posts.orEmpty()
        val updatedPosts = oldPosts.filter { it.id != id }

        updatedPosts.let { posts ->
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                val errorMessage = "Ошибка при удалении. Повторите снова"
                _errorMessages.postValue(errorMessage)
            }
        }
    }

    private fun resetEditingState() {
        edited.postValue(empty)
    }

    fun addNewPost(content: String) = viewModelScope.launch {
        // Показываем индикатор загрузки
        _isLoading.value = true

        val newPost = empty.copy(content = content.trim(), id = 0L)

        // Оптимистичное обновление: добавляем новый пост в UI
        val currentPosts = _data.value?.posts.orEmpty()

        // Ассинхронное сохранение поста
        try {
            repository.save(newPost)
            val updatedPosts = listOf(newPost) + currentPosts.filter { it.id != 0L }

            _data.value?.posts?.map {
                updatedPosts
            }.orEmpty()
            // оповещение об успешном создании
            _postCreated.postValue(Unit)
            resetEditingState()

            // Скрываем индикатор загрузки
            _isLoading.postValue(false)
        } catch (e: Exception) {
            // Обработка ошибок при обновлении
            _errorMessages.postValue("Повторите попытку")
        } finally {
            // Скрываем индикатор ожидания ответа от сервера
            _isLoading.postValue(false)
        }
    }

    fun updatePost(postId: Long, content: String) = viewModelScope.launch {
        // Показываем индикатор ожидания ответа от сервера
        _isLoading.value = true

        try {
            // Получаем текущий пост и обновляем его содержимое
            val originalPost = repository.getById(postId)
            val updatedPost = originalPost.copy(content = content.trim())

            // Асинхронно сохраняем обновленный пост
            repository.save(updatedPost)

            // Обновляем список постов в LiveData
            _data.value?.posts?.map { post ->
                if (post.id == updatedPost.id) updatedPost else post
            }.orEmpty()

            // Оповещаем о завершении обновления
            _postCreated.postValue(Unit)
            _isLoading.postValue(false)
        } catch (e: Exception) {
            // Обработка ошибок при обновлении
            _errorMessages.postValue("Повторите попытку")
        } finally {
            // Скрываем индикатор ожидания ответа от сервера
            _isLoading.postValue(false)
        }

        // Сбрасываем состояние редактирования
        resetEditingState()
    }

    // Метод для сбрасывания значения об ошибке при прослушивании событий нажатия лайка
    fun clearErrorMessage() {
        _errorMessages.value = null
    }
}



