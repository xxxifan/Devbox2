package com.xxxifan.devbox.core.base

import android.app.Activity
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import com.xxxifan.devbox.core.R

/**
 * toolbar module
 */
class ToolbarModule {

  lateinit var toolbarView: View

  fun isAttached() = this::toolbarView.isInitialized && toolbarView.parent != null

  fun attach(activity: Activity, layoutId: Int, isTransparentToolbar: Boolean = false): View {
    val windowView = activity.window.decorView as ViewGroup
    findChild(windowView) {
      if (it.fitsSystemWindows) {
        it.fitsSystemWindows = false
      }
    }

    val statusBarHeight = getStatusBarHeight(activity.resources)
    val root = LayoutInflater.from(windowView.context).inflate(layoutId, windowView)
    val toolbarView: View = root.findViewById(R.id.toolbar)
      ?: throw IllegalArgumentException("please set @+/toolbar to toolbar's root view.")
    this.toolbarView = toolbarView
    toolbarView.setPadding(
      toolbarView.paddingLeft,
      toolbarView.paddingTop + statusBarHeight,
      toolbarView.paddingRight,
      toolbarView.bottom
    )

    if (!isTransparentToolbar) {
      // adjust window size
      val toolbarHeight = activity.resources.getDimensionPixelSize(R.dimen.toolbar_height)
      val contentLayout = windowView.findViewById<ViewGroup>(android.R.id.content)
      val contentPadding = toolbarHeight + statusBarHeight
      contentLayout.setPadding(0, contentPadding, 0, 0)
    }
    return toolbarView
  }

  fun getStatusBarHeight(resources: Resources): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
      result = resources.getDimensionPixelSize(resourceId)
    }
    return result
  }

  fun setTitle(title: CharSequence?) {
    if (isAttached()) {
      // toolbarView.findViewById<TextView>(R.id.toolbarTitle)?.text = title
    }
  }

  private fun findChild(viewGroup: ViewGroup, function: (View) -> Unit) {
    if (viewGroup.childCount > 0) {
      viewGroup.forEach {
        if (it.id == android.R.id.content) return@forEach

        function(it)
        if (it is ViewGroup) {
          findChild(it, function)
        }
      }
    }
  }
}

//fun ToolbarModule.attachDefaultToolbar(
//  activity: Activity, transparent:Boolean = false,
//  apply: (toolbar: View, title: TextView) -> Unit
//) {
//  val view = attach(activity, R.layout.common_toolbar, transparent)
//  val back = view.findViewById<View>(R.id.toolbarBack)
//  back.setOnClickListener { activity.finish() }
//  apply(view, view.findViewById(R.id.toolbarTitle))
//}