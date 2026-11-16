package com.habitatplus.app.features.reportes.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ReportPhoto(
    photoUri: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Fotografía de evidencia"
) {
    val context = LocalContext.current

    val bitmapState = produceState<Bitmap?>(
        initialValue = null,
        key1 = photoUri
    ) {
        value = withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(
                    Uri.parse(photoUri)
                )?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            } catch (exception: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        bitmapState.value?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
