package com.example.instagramphotovideodownloader.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.instagramphotovideodownloader.MySingleton
import com.example.instagramphotovideodownloader.databinding.FragmentPhotoBinding
import org.json.JSONObject
import java.io.File

class PhotoFragment : Fragment() {
    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    private lateinit var binding: FragmentPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(inflater, container, false)

        binding.btnPhotoLoad.setOnClickListener {
            binding.ivPhoto.setImageResource(0)
            try {
                if (binding.etPhoto.text.isNotEmpty()) {
                    loadPhoto()
                    closeKeyBoard()
                }

            } catch (s: Exception) {
                Toast.makeText(context, "Enter valid Link !!", Toast.LENGTH_SHORT).show()
            }

        }
        binding.btnPhotoPasteLink.setOnClickListener {
            binding.ivPhoto.setImageResource(0)
            try {
                binding.ivCancelPhoto.visibility = View.VISIBLE
                pasteLink()
                loadPhoto()
                closeKeyBoard()
            } catch (e: Exception) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        showIconWhenEdittextNotEmpty(binding.etPhoto, binding.ivCancelPhoto)

        binding.ivCancelPhoto.setOnClickListener {
            binding.etPhoto.text.clear()
        }

        binding.btnPhotoDownload.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    askPermissions()
                } else {
                    if (binding.etPhoto.text.isNotEmpty())
                        loadPhoto()
                    downloadImage()
                }
            } catch (s: Exception) {
                Toast.makeText(context, "Enter valid Link !!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun loadPhoto() {
        binding.pb.visibility = View.VISIBLE

        val imageUrl = binding.etPhoto.text
        val abc = imageUrl.substring(0, imageUrl.indexOf("?")) + "?__a=1&__d=dis"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, abc, null,
            { response ->
                val graphqlObject: JSONObject = response.getJSONObject("graphql")
                val shortcodeMediaObject: JSONObject =
                    graphqlObject.getJSONObject("shortcode_media")
                val nameValue = shortcodeMediaObject.getString("display_url")

                Glide.with(this).load(nameValue).listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pb.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.pb.visibility = View.GONE
                        return false
                    }

                }).into(binding.ivPhoto)
            },
            {
            })
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

    private fun pasteLink() {
        val clipboard: ClipboardManager? =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard?.hasPrimaryClip() == true) {
            binding.etPhoto.setText(clipboard.primaryClip?.getItemAt(0)?.text.toString())
        }
    }

    private fun downloadImage() {
        val fileName = "IG_photo_${System.currentTimeMillis()}"
        val imageUrl = binding.etPhoto.text
        val abc = imageUrl.substring(0, imageUrl.indexOf("?")) + "?__a=1&__d=dis"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, abc, null,
            { response ->
                val graphqlObject: JSONObject = response.getJSONObject("graphql")
                val shortcodeMediaObject: JSONObject =
                    graphqlObject.getJSONObject("shortcode_media")
                val nameValue = shortcodeMediaObject.getString("display_url")

                try {
                    val downloadManager: DownloadManager?
                    downloadManager = context?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadUri = Uri.parse(nameValue)
                    val request = DownloadManager.Request(downloadUri).setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    ).setAllowedOverRoaming(false).setTitle(fileName).setMimeType("image/jpeg")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_PICTURES,
                            File.separator + fileName + ".jpg"
                        )

                    downloadManager.enqueue(request)
                    Toast.makeText(context, "Download Done", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(context, "Download Fail", Toast.LENGTH_SHORT).show()
                }
            },
            {
            })
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions() {

        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

                AlertDialog.Builder(context)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Allow") { _, _ ->
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                        (context as Activity).finish()
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            downloadImage()
        }
    }


    private fun showIconWhenEdittextNotEmpty(et: EditText, iv: ImageView) {
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

            }
        })
    }

    private fun closeKeyBoard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}