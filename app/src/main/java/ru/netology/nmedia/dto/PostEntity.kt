package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
//    var sharings: Int,
//    var video: String?
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likedByMe, likes)

    fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
    fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::toEntity)

    companion object {
        fun toEntity(post: Post) = PostEntity(
            post.id,
            post.author,
            post.authorAvatar,
            post.content,
            post.published,
            post.likedByMe,
            post.likes
        )
    }
}