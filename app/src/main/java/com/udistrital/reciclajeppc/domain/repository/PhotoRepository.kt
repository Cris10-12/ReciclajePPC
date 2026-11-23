package com.udistrital.reciclajeppc.domain.repository

import com.udistrital.reciclajeppc.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    /** Fotos del usuario, de la más reciente a la más antigua. */
    fun observePhotos(ownerId: String): Flow<List<Photo>>

    suspend fun addPhoto(ownerId: String, uri: String, caption: String): Result<Photo>

    suspend fun deletePhoto(photoId: String): Result<Unit>
}