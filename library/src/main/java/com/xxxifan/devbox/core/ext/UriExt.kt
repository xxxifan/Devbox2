package com.xxxifan.devbox.core.ext

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.database.getLongOrNull
import androidx.core.net.toFile
import com.xxxifan.devbox.core.Devbox
import com.xxxifan.devbox.core.util.MediaFileInfo
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.io.FileNotFoundException

//
// Created by xxxifan on 2020/10/26.
//


fun File.toCompatUri(): Uri {
  return FileProvider.getUriForFile(Devbox.appRef, Devbox.appRef.packageName + ".provider", this)
}

fun Uri.getMimeType(contentResolver: ContentResolver): String? {
  return contentResolver.getType(this)
}

@RequiresApi(Build.VERSION_CODES.N) fun Uri.isVirtualUri(context: Context): Boolean {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    if (!DocumentsContract.isDocumentUri(context, this)) {
      return false
    }
    context.contentResolver.query(
      this, arrayOf(DocumentsContract.Document.COLUMN_FLAGS),
      null, null, null
    )?.use { cursor ->
      var flags = 0
      if (cursor.moveToFirst()) {
        flags = cursor.getInt(0)
      }
      return flags and DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT != 0
    }
    return false
  } else {
    return false
  }
}

@Deprecated("use queryFileInfo() instead")
fun Uri.queryFileName(contentResolver: ContentResolver): String? {
  contentResolver.openFileDescriptor(this, "r").use {
    var result: String? = null
    if (scheme == "content") {
      contentResolver.query(this, null, null, null, null).use {
        if (it?.moveToFirst() == true) {
          result = try {
            it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
          } catch (e: Exception) {
            null
          }
        }
      }
    }
    if (result == null) {
      result = path
      val cut = result!!.lastIndexOf('/')
      if (cut != -1) {
        result = result!!.substring(cut + 1)
      }
    }
    return result
  }
}

fun Uri.queryFileInfo(contentResolver: ContentResolver): MediaFileInfo? {
  if (scheme == "content") {
    contentResolver.openFileDescriptor(this, "r")?.use { pfd ->
      if (!pfd.fileDescriptor.valid()) return null

      contentResolver.query(this, null, null, null, null).use {
        if (it?.moveToFirst() == true) {
          try {
            return MediaFileInfo().apply {
              val idIndex = it.getColumnIndex(MediaStore.MediaColumns._ID)
              val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
              val sizeIndex = it.getColumnIndex(MediaStore.MediaColumns.SIZE)
              val dateIndex = it.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)

              id = if (idIndex > -1) it.getString(idIndex) else null
              displayName = if (nameIndex > -1) it.getString(nameIndex) else null
              size = if (sizeIndex > -1) it.getLongOrNull(sizeIndex) ?: 0 else 0
              dateModified = if (dateIndex > -1) it.getLongOrNull(dateIndex) ?: 0 else 0
            }
          } catch (e: Exception) {
            debug { e.printStackTrace() }
          }
        }
      }
    }
  } else if (scheme == "file" || toString().startsWith("/")) {
    val file = toFile()
    return MediaFileInfo().apply {
      id = ""
      displayName = file.name
      size = file.length()
      dateModified = file.lastModified()
    }
  }
  return null
}

fun Uri.isLoadable(contentResolver: ContentResolver): Boolean {
  val validUris = contentResolver.persistedUriPermissions.map { it.uri }
  return when (this.scheme) {
    "content" -> {
      if (DocumentsContract.isDocumentUri(Devbox.appRef, this))
        documentUriExists(contentResolver, this) && validUris.contains(this)
      else contentUriExists(contentResolver, this)
    }
    "file" -> File(this.path!!).exists()
    else -> { // http, https, etc. No inexpensive way to test existence.
      Log.e("xifan", "Can't resolve uri: $this")
      false
    }
  }
}


fun CoroutineScope.createDownloadUri(
  fileName: String,
  use: suspend (uri: Uri?, uriExist: Boolean) -> Unit
) {
// content://media/external/downloads/317917
  //content://com.dovecard.app.provider/external_download/Screenshot_20210606-225053.jpg
  launchCatching {
    val contentResolver = Devbox.appRef.contentResolver
    val volume = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
      "external" else "internal"

    val mimeType = MimeTypeMap.getSingleton()
      .getMimeTypeFromExtension(fileName.substring(fileName.lastIndexOf(".") + 1)) ?: "file/*"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      val contentUri = MediaStore.Downloads.getContentUri(volume)
      val existUri = getFileUri(contentUri, fileName)
      if (existUri != null) {
        use(existUri, true)
      } else {
        val contentValues = ContentValues().apply {
          put(MediaStore.Downloads.DISPLAY_NAME, fileName)
          put(MediaStore.Downloads.MIME_TYPE, mimeType)
          put(MediaStore.Downloads.DATE_MODIFIED, System.currentTimeMillis())
          put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = contentResolver.insert(MediaStore.Downloads.getContentUri(volume), contentValues)
        use(uri, false)
        if (uri != null) {
          contentValues.clear()
          contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
          contentResolver.update(uri, contentValues, null, null)
        } else return@launchCatching
      }
    } else {
      val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      if (!file.exists()) file.mkdirs()
      val path = File(file, fileName)
      val uri = path.toCompatUri()

      use(uri, path.exists())

      // scan file after use.
      MediaScannerConnection.scanFile(
        Devbox.appRef,
        arrayOf(path.absolutePath), arrayOf(mimeType), null
      )
    }
  }
}

fun getFileUri(contentUri: Uri, fileName: String): Uri? {
  return Devbox.appRef.contentResolver.query(
    contentUri,
    arrayOf(MediaStore.Downloads._ID),
    MediaStore.Downloads.DISPLAY_NAME + " = ?",
    arrayOf(fileName),
    null
  )?.use { cursor ->
    return if (cursor.count > 0) {
      cursor.moveToFirst()
      ContentUris.withAppendedId(contentUri, cursor.getLong(0))
    } else null
  }
}

fun Uri.fileExists(
  context: Context
): Boolean {
  return try {
    context.contentResolver.openFileDescriptor(this, "r")?.use {
      it.fileDescriptor.valid()
    } ?: false
  } catch (e: FileNotFoundException) {
    false
  }
}

fun File.subFile(fileName: String): File {
  return File(this, fileName)
}

// All DocumentProviders should support the COLUMN_DOCUMENT_ID column
private fun documentUriExists(contentResolver: ContentResolver, uri: Uri): Boolean =
  resolveUri(contentResolver, uri, DocumentsContract.Document.COLUMN_DOCUMENT_ID)

// All ContentProviders should support the BaseColumns._ID column
private fun contentUriExists(contentResolver: ContentResolver, uri: Uri): Boolean =
  resolveUri(contentResolver, uri, BaseColumns._ID)

private fun resolveUri(contentResolver: ContentResolver, uri: Uri, column: String): Boolean {
  return contentResolver.query(
    uri,
    arrayOf(column), // Empty projections are bad for performance
    null,
    null,
    null
  )?.use {
    it.moveToFirst()
  } ?: false
}