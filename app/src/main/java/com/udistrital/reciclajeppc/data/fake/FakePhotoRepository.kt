package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.core.util.newId
import com.udistrital.reciclajeppc.domain.model.Photo
import com.udistrital.reciclajeppc.domain.repository.PhotoRepository
import com.udistrital.reciclajeppc.domain.repository.SocialError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePhotoRepository : PhotoRepository {

    private val photos = MutableStateFlow(FakeData.photos)

    override fun observePhotos(ownerId: String): Flow<List<Photo>> =
        photos.map { list ->
            list.filter { it.ownerId == ownerId }.sortedByDescending { it.createdAt }
        }

    override suspend fun addPhoto(ownerId: String, uri: String, caption: String): Result<Photo> {
        val photo = Photo(
            id = newId("ph"),
            ownerId = ownerId,
            uri = uri,
            caption = caption.trim(),
            createdAt = System.currentTimeMillis()
        )
        photos.value = photos.value + photo
        return Result.success(photo)
    }

    override suspend fun deletePhoto(photoId: String): Result<Unit> {
        val current = photos.value
        if (current.none { it.id == photoId }) return Result.failure(SocialError.NotFound)
        photos.value = current.filterNot { it.id == photoId }
        return Result.success(Unit)
    }
}