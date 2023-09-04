package com.xxxifan.devbox.core.ext

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Process
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xxxifan.devbox.core.Devbox
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.roundToInt

const val PERMISSION_REQUEST_CODE = 9999

fun Application.initDevbox(debug: Boolean = true) {
  Devbox.appRef = this
  Devbox.debug = debug
}

fun Application.install(vararg blocks: () -> Unit) {
  blocks.forEach { it() }
}

fun Application.isMainProcess(): Boolean {
  val am = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return false
  val processes = am.runningAppProcesses
  val mainProcessName = packageName
  val myPid = Process.myPid()
  return processes.any { it.pid == myPid && mainProcessName == it.processName }
}

inline fun debug(e: Throwable? = null, block: (() -> Unit)) {
  if (Devbox.debug) {
    e?.printStackTrace()
    block.invoke()
  }
}

/**
 * 时间戳转换成字符窜
 * @param pattern 时间样式 yyyy-MM-dd HH:mm:ss
 * @return [String] 时间字符串
 */
@SuppressLint("SimpleDateFormat")
fun Long.toDateStr(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
  val date = Date(this)
  val format = SimpleDateFormat(pattern)
  return format.format(date)
}

/**
 * convert px float to dp
 */
fun Float.asDp() = this / (Devbox.appRef.resources?.displayMetrics?.density ?: 1F)

/**
 * convert px int to dp
 */
fun Int.asDp() = toFloat().asDp()

/**
 * convert dp to px
 */
fun Int.asPx() = this * (Devbox.appRef.resources?.displayMetrics?.density ?: 1F)

/**
 * convert dp to px int
 */
fun Int.asPxInt() = (this * (Devbox.appRef.resources?.displayMetrics?.density ?: 1F)).roundToInt()

fun String.md5(): String {
  try {
    val hash = MessageDigest.getInstance("MD5").digest(toByteArray(charset("UTF-8")))
    val hex = StringBuilder(hash.size * 2)
    for (b in hash) {
      val result = b.toInt() and 0xFF
      if (result < 0x10) hex.append("0")
      hex.append(Integer.toHexString(result))
    }
    return hex.toString()
  } catch (e: Exception) {
    e.printStackTrace()
  }

  return this
}

fun Location.getAddress(): Address? {
  return if (longitude > 0.0 || latitude > 0.0) {
    val geoCoder = Geocoder(Devbox.appRef)
    try {
      geoCoder.getFromLocation(latitude, longitude, 1)?.lastOrNull()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  } else {
    null
  }
}

fun String?.joinString(separator: String, vararg strings: String?): String {
  val start = if (this.isNullOrEmpty()) "" else this + separator
  val buffer = StringBuilder(start)
  for (str in strings) {
    if (str.isNullOrEmpty()) continue
    buffer.append(str)
    buffer.append(separator)
  }
  return if (buffer.isNotEmpty()) {
    buffer.substring(0, buffer.length - separator.length)
  } else {
    buffer.toString()
  }
}

fun getLetters(vararg str: String?): String {
  var newStr = ""
  str.forEach {
    if (it.isNullOrBlank()) return@forEach
    newStr += it[0].uppercaseChar()
  }
  return newStr.trim()
}

fun Location?.isLocated(): Boolean {
  if (this == null || latitude == 0.0 || longitude == 0.0) {
    return false
  }
  return true
}

fun CoroutineScope.launchCatching(
  context: CoroutineContext = EmptyCoroutineContext,
  start: CoroutineStart = CoroutineStart.DEFAULT,
  errorHandler: ((e: Throwable) -> Unit)? = null,
  block: suspend CoroutineScope.() -> Unit
): Job {
  return launch(context, start) {
    try {
      block()
    } catch (e: Throwable) {
      if (e !is CancellationException) {
//        debug(e) { e.printStackTrace() }
        errorHandler?.invoke(e)
      }
    }
  }
}

fun String.fillTo(size: Int, fill: String): String {
  return if (length <= size) {
    (this + fill).substring(0, size)
  } else {
    substring(0, size)
  }
}

fun String.copyToClipboard(context: Context): Boolean {
  return try {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val mClipData = ClipData.newPlainText("Tradove", this)
    cm.setPrimaryClip(mClipData)
    true
  } catch (e: Exception) {
    false
  }
}

fun RecyclerView.useDefaultLayoutManager(
  context: Context,
  stackFromEnd: Boolean = false,
  fixSize: Boolean = true
) {
  layoutManager = LinearLayoutManager(context).apply {
    setStackFromEnd(stackFromEnd)
  }
  setHasFixedSize(fixSize)
}

inline fun String?.isNullOrEmptyOrElse(block: () -> String): String =
  if (isNullOrEmpty()) block() else this


/**
 * post object itself to event bus.
 */
fun Any.postEvent() {
  EventBus.getDefault().post(this)
}

/**
 * post object itself to event bus with sticky mode.
 */
fun Any.postStickyEvent() {
  EventBus.getDefault().postSticky(this)
}

fun Any.removeFromStickyEvents() {
  EventBus.getDefault().removeStickyEvent(this)
}

fun TextView.setKeywordText(@ColorInt color: Int, vararg strs: String) {
  val allText = text
  if (allText.isEmpty() || strs.isEmpty()) return

  val span = SpannableStringBuilder(allText)
  strs.forEach {
    val p = Pattern.compile(it.lowercase())
    val m = p.matcher(allText.toString().lowercase())
    while (m.find()) {
      span.setSpan(
        ForegroundColorSpan(color),
        m.start(),
        m.end(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
  }

  text = span
}

fun TextView.setBoldText(vararg strs: String) {
  val allText = text
  if (allText.isEmpty() || strs.isEmpty()) return

  val span = SpannableStringBuilder(allText)
  strs.forEach {
    val p = Pattern.compile(it.lowercase())
    val m = p.matcher(allText.toString().lowercase())
    while (m.find()) {
      span.setSpan(
        StyleSpan(Typeface.BOLD),
        m.start(),
        m.end(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
  }

  text = span
}