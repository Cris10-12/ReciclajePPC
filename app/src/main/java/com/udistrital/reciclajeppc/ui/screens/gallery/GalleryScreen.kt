package com.udistrital.reciclajeppc.ui.screens.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.constants.Dimens
import com.udistrital.reciclajeppc.core.util.pluralize
import com.udistrital.reciclajeppc.core.util.screenPadding
import com.udistrital.reciclajeppc.domain.model.Photo
import com.udistrital.reciclajeppc.ui.components.AppTopBar
import com.udistrital.reciclajeppc.ui.components.EmptyState
import com.udistrital.reciclajeppc.ui.components.PrimaryButton

@Composable
fun GalleryScreen(
    state: GalleryUiState,
    onAddPhotos: (List<String>) -> Unit,
    onDeletePhoto: (String) -> Unit,
    onBack: () -> Unit
) {
    var viewerIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    var photoToDelete by rememberSaveable { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(GalleryViewModel.MAX_PER_PICK)
    ) { uris ->
        if (uris.isNotEmpty()) onAddPhotos(uris.map { it.toString() })
    }

    val title = when {
        state.isOwner -> AppTexts.GALLERY_TITLE_OWN
        state.ownerName.isBlank() -> AppTexts.GALLERY_TITLE_FALLBACK
        else -> AppTexts.GALLERY_TITLE_OTHER.format(state.ownerName.substringBefore(" "))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = title, onBack = onBack)

        if (state.isOwner) {
            Column(
                modifier = Modifier.screenPadding(),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSm)
            ) {
                PrimaryButton(
                    text = AppTexts.ADD_PHOTOS,
                    onClick = {
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    icon = Icons.Filled.AddPhotoAlternate,
                    enabled = state.photos.size < GalleryViewModel.MAX_PHOTOS
                )
                Text(
                    text = pluralize(state.photos.size, "foto", "fotos") + " · " +
                            AppTexts.GALLERY_LIMIT.format(GalleryViewModel.MAX_PHOTOS),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (state.limitReached) {
                    Text(
                        text = AppTexts.GALLERY_LIMIT_REACHED,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (!state.isLoading && state.photos.isEmpty()) {
            if (state.isOwner) {
                EmptyState(
                    message = AppTexts.GALLERY_EMPTY_OWN,
                    hint = AppTexts.GALLERY_EMPTY_OWN_HINT
                )
            } else {
                EmptyState(
                    message = AppTexts.GALLERY_EMPTY_OTHER,
                    hint = AppTexts.GALLERY_EMPTY_OTHER_HINT
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(Dimens.spaceXs),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceXs),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceXs)
            ) {
                items(state.photos, key = { it.id }) { photo ->
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = photo.caption.ifBlank { null },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewerIndex = state.photos.indexOf(photo) }
                    )
                }
            }
        }
    }

    // ---- Visor a pantalla completa ----
    val startIndex = viewerIndex?.takeIf { it in state.photos.indices }
    if (startIndex != null) {
        PhotoViewer(
            photos = state.photos,
            startIndex = startIndex,
            isOwner = state.isOwner,
            onClose = { viewerIndex = null },
            onDeleteRequest = { photoToDelete = it.id }
        )
    }

    // ---- Confirmación de eliminación ----
    photoToDelete?.let { id ->
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text(AppTexts.DELETE_PHOTO_TITLE) },
            text = { Text(AppTexts.DELETE_PHOTO_MESSAGE) },
            confirmButton = {
                TextButton(onClick = {
                    photoToDelete = null
                    viewerIndex = null
                    onDeletePhoto(id)
                }) { Text(AppTexts.DELETE_PHOTO_CONFIRM) }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text(AppTexts.DELETE_PHOTO_DISMISS)
                }
            }
        )
    }
}

@Composable
private fun PhotoViewer(
    photos: List<Photo>,
    startIndex: Int,
    isOwner: Boolean,
    onClose: () -> Unit,
    onDeleteRequest: (Photo) -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val pagerState = rememberPagerState(initialPage = startIndex) { photos.size }
        val current = photos.getOrNull(pagerState.currentPage)

        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val photo = photos.getOrNull(page)
                if (photo != null) {
                    AsyncImage(
                        model = photo.uri,
                        contentDescription = photo.caption.ifBlank { null },
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Dimens.spaceXs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = AppTexts.CLOSE, tint = Color.White)
                }
                Text(
                    text = "${pagerState.currentPage + 1}/${photos.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(Modifier.weight(1f))
                if (isOwner && current != null) {
                    IconButton(onClick = { onDeleteRequest(current) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = AppTexts.DELETE_PHOTO,
                            tint = Color.White
                        )
                    }
                }
            }

            if (current != null && current.caption.isNotBlank()) {
                Text(
                    text = current.caption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(Dimens.spaceMd)
                )
            }
        }
    }
}