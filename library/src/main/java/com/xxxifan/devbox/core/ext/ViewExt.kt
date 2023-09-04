package com.xxxifan.devbox.core.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.xxxifan.devbox.core.R

//
// Created by xxxifan on 2022/1/13.
//

/**
 * prevent click button multiple times, use view's tag to store last click time.
 * @param resetTime true - reset last click time every click. false - use a single duration to prevent clicks.
 */
fun View.throttleClick(resetTime: Boolean = true, interval: Long = 300L): Boolean {
  val checkTime = System.currentTimeMillis()
  return if (getTag(R.id.viewClickTag) == null) {
    setTag(R.id.viewClickTag, checkTime)
    false
  } else {
    if (checkTime - (getTag(R.id.viewClickTag) as Long) < interval) {
      if (resetTime) {
        setTag(R.id.viewClickTag, checkTime)
      }
      true
    } else {
      setTag(R.id.viewClickTag, checkTime)
      false
    }
  }
}

/**
 * Link editTexts input states to `this`. If the state matches the maxState, then `TextView` will be enabled.
 */
fun TextView.linkStateTo(vararg editTexts: EditText) {
  isEnabled = false
  val state = State(editTexts.size, this)
  editTexts.forEach {
    it.addTextChangedListener(object : TextWatcher {
      private var innerState = 0

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      }

      override fun afterTextChanged(s: Editable?) {
        s ?: return
        val newState = if (s.isNotEmpty()) 1 else 0
        if (newState != innerState) {
          innerState = newState
          if (newState == 1) state.currentState++ else state.currentState--
        }
      }
    })
  }
}

private class State(private val maxState: Int, val view: TextView) {
  var currentState: Int = 0
    set(value) {
      field = value
      view.isEnabled = currentState == maxState
    }

}
