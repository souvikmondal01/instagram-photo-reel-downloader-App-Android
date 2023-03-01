package com.example.instagramphotovideodownloader.utilities

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

object Utility {
    fun pasteLink(context: Context, et: EditText) {
        val clipboard: ClipboardManager? =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard?.hasPrimaryClip() == true) {
            val text = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            if (text.isNotBlank()) {
                et.setText(text)
            } else {
                et.setText("")
            }

        }
    }

    fun closeKeyBoard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    //show edittext clear icon when edittext is not empty
    fun edittextClear(et: EditText, iv: ImageView) {
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et.text.toString().isEmpty()) {
                    iv.visibility = View.GONE
                } else {
                    iv.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                iv.setOnClickListener {
                    et.text.clear()
                }
            }
        })
    }

}