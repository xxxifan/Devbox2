package com.xxxifan.devbox.core.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import kotlin.LazyThreadSafetyMode.NONE

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
  crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(NONE) { bindingInflater(layoutInflater) }

/**
 * startActivity with FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK flags.
 */
inline fun <reified T : Activity> Context.newTask(bundle: Bundle? = null) {
  applicationContext.startActivity(Intent(this, T::class.java).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    if (bundle != null) {
      putExtras(bundle)
    }
  })
}

/**
 * startActivity with bundle data using context.
 */
inline fun <reified T : Activity> Context.start(bundle: Bundle? = null) {
  startActivity(Intent(this, T::class.java).apply {
    if (bundle != null) {
      putExtras(bundle)
    }
  })
}

/**
 * startActivity with bundle data using fragment.
 */
inline fun <reified T : Activity> Fragment.start(bundle: Bundle? = null) {
  context?.start<T>(bundle)
}

/**
 * convenient register activity result
 */
fun ComponentActivity.forResult(callback: ActivityResultCallback<ActivityResult>): ActivityResultLauncher<Intent> {
  return registerForActivityResult(ActivityResultContracts.StartActivityForResult(), callback)
}

/**
 * convenient launch ActivityResultLauncher with Intent
 */
inline fun <reified T : Activity> ActivityResultLauncher<Intent>.start(
  activity: Activity,
  bundle: Bundle? = null
) {
  this.launch(Intent(activity, T::class.java).apply {
    if (bundle != null) {
      putExtras(bundle)
    }
  })
}

/**
 * @param sdkAtLeast permissions will not be requested if sdk less than it.
 * @param sdkAtMost permissions will not be requested if sdk over than it.
 */
fun Context.request(
  vararg permissions: String,
  sdkAtLeast: Int = 0,
  sdkAtMost: Int = 0,
  denied: ((permissions: List<String>) -> Unit)? = null,
  callback: (granted: Boolean) -> Unit
) {
  if (Build.VERSION.SDK_INT < sdkAtLeast) {
    callback(true)
    return
  }
  if (sdkAtMost != 0 && Build.VERSION.SDK_INT > sdkAtMost) {
    callback(true)
    return
  }

  XXPermissions.with(this)
    .permission(permissions)
    .request(object : OnPermissionCallback {
      override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
        callback(all)
      }

      override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
        // 这里使用一个Dialog展示没有这些权限应用程序无法继续运行，询问用户是否去设置中授权。
        denied?.invoke(permissions ?: emptyList())
      }
    })
}

fun Context.toast(str: String) {
  Toast.makeText(this.applicationContext, "", Toast.LENGTH_SHORT).apply {
    setText(str) //修复小米MAX2会在所有toast 文本前加上 app label,fk
    show()
  }
}

fun Context.longToast(str: String) {
  Toast.makeText(this.applicationContext, "", Toast.LENGTH_LONG).apply {
    setText(str)
    show()
  }
}

fun Fragment.toast(str: String) {
  Toast.makeText(context?.applicationContext, "", Toast.LENGTH_SHORT).apply {
    setText(str)
    show()
  }
}

fun Fragment.longToast(str: String) {
  Toast.makeText(context?.applicationContext, "", Toast.LENGTH_LONG).apply {
    setText(str)
    show()
  }
}

fun Context.startAppSettings() {
  // 跳转到设置页面再返回到app时重启
//  val context = Devbox.appRef
//  AppManager.getInstance(context).appExit()
//  context.startActivity<LaunchActivity>()

  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
  intent.data = Uri.parse("package:$packageName")
  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
  startActivity(intent)
}

inline fun Fragment.isAdded(block: () -> Unit) {
  if (isAdded) {
    block()
  }
}

fun Activity.getCompatColor(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, theme)

fun Activity.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Fragment.getCompatColor(@ColorRes id: Int) =
  ResourcesCompat.getColor(resources, id, context?.theme)

fun Fragment.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(context!!, id)

fun Context.getCompatColor(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, theme)

fun Context.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Dialog?.safeDismiss() {
  if (this != null && isShowing) {
    dismiss()
  }
}

fun DialogFragment.bindToLifecycle(owner: LifecycleOwner? = null): DialogFragment {
  val observer = DialogLifecycleObserver(::dismiss)
  val lifecycleOwner = owner ?: (context as? LifecycleOwner
    ?: throw IllegalStateException(
      "$context is not a LifecycleOwner."
    ))
  lifecycleOwner.lifecycle.addObserver(observer)
  return this
}

/** @author @jordyamc */
internal class DialogLifecycleObserver(private val dismiss: () -> Unit) : LifecycleObserver {
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  fun onDestroy() = dismiss()
}