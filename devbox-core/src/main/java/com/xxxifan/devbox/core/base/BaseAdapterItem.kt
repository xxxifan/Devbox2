package com.xxxifan.devbox.core.base

import android.support.annotation.CallSuper
import android.view.View
import kale.adapter.item.AdapterItem

/**
 * Created by xifan on 17-5-9.
 */
abstract class BaseAdapterItem<T> : AdapterItem<T> {

    protected var data: T? = null
    protected var view: View? = null
    protected var position: Int = 0

    private var mItemClickListener: ItemClickListener<T>? = null
    private var mItemLongClickListener: ItemLongClickListener<T>? = null

    override fun bindViews(view: View?) {
        this.view = view

        // bindViews may happens after set listeners
        this.view?.post {
            setOnItemClickListener(mItemClickListener)
            setOnItemLongClickListener(mItemLongClickListener)
        }
    }

    @CallSuper override fun handleData(data: T?, position: Int) {
        this.data = data
        this.position = position
    }

    fun setOnItemClickListener(listener: ItemClickListener<T>?) {
        mItemClickListener = listener
        if (listener != null) {
            view?.setOnClickListener { mItemClickListener?.onItemClick(it, data, position) }
        }
    }

    fun setOnItemLongClickListener(listener: ItemLongClickListener<T>?) {
        mItemLongClickListener = listener
        if (listener != null) {
            view?.setOnLongClickListener {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener!!.onItemLongClick(it, data, position)
                    return@setOnLongClickListener true
                }
                false
            }
        }
    }

    interface ItemClickListener<in T> {
        fun onItemClick(v: View, data: T?, index: Int)
    }

    interface ItemLongClickListener<in T> {
        fun onItemLongClick(v: View, data: T?, index: Int)
    }
}