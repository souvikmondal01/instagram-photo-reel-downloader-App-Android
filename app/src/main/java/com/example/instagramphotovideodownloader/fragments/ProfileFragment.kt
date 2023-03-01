package com.example.instagramphotovideodownloader.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.instagramphotovideodownloader.MySingleton
import com.example.instagramphotovideodownloader.databinding.FragmentProfileBinding
import org.json.JSONObject
import java.io.File


class ProfileFragment : Fragment() {
    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnProfileLoad.setOnClickListener {
            binding.ivProfile.setImageResource(0)
            try {
                if (binding.etProfile.text?.isNotEmpty() == true) {
                    loadProfile()
                    closeKeyBoard()
                }
            } catch (s: Exception) {
                Toast.makeText(context, "Enter valid Link !!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnProfilePasteLink.setOnClickListener {
            try {
                binding.ivCancelProfile.visibility = View.VISIBLE
                pasteLink()
                loadProfile()
                closeKeyBoard()
            } catch (e: Exception) {

            }

        }

        binding.btnProfileDownload.setOnClickListener {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    askPermissions()
                } else {
                    if (binding.etProfile.text.isNotEmpty())
                        loadProfile()
                    downloadProfile()
                }
            } catch (s: Exception) {
                Toast.makeText(context, "Enter valid Username !!", Toast.LENGTH_SHORT).show()
            }
        }
        showIconWhenEdittextNotEmpty(binding.etProfile, binding.ivCancelProfile)
        binding.ivCancelProfile.setOnClickListener {
            binding.etProfile.text.clear()

        }

        return binding.root
    }

    private fun loadProfile() {
        binding.pbProfile.visibility = View.VISIBLE
        val username = binding.etProfile.text.trim()
//        val abc = "https://www.instagram.com/" + imageUrl.toString() + "/channel/?__a=1"
        val url = "https://www.instagram.com/" + username.toString() + "/?__a=1&__d=dis"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                binding.tvFollowerStill.visibility = View.VISIBLE
                binding.tvFollowingStill.visibility = View.VISIBLE
                val graphqlObject: JSONObject = response.getJSONObject("graphql")
                val shortcodeMediaObject: JSONObject =
                    graphqlObject.getJSONObject("user")
                val edgeFollowedByObject = shortcodeMediaObject.getJSONObject("edge_followed_by")
                val edgeFollowObject = shortcodeMediaObject.getJSONObject("edge_follow")
                val follower = edgeFollowedByObject.getInt("count")
                val following = edgeFollowObject.getInt("count")

                binding.tvFollowerCount.text = follower.toString()
                binding.tvFollowingCount.text = following.toString()
                val profilePhoto = shortcodeMediaObject.getString("profile_pic_url_hd")

                Glide.with(this).load(profilePhoto)
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.pbProfile.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.pbProfile.visibility = View.GONE
                            return false
                        }

                    }).into(binding.ivProfile)

            },
            {
                binding.pbProfile.visibility = View.GONE
            })
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }


    }

    private fun pasteLink() {
        val clipboard: ClipboardManager? =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        if (clipboard?.hasPrimaryClip() == true) {
            binding.etProfile.setText(clipboard.primaryClip?.getItemAt(0)?.text.toString())
        }
    }

    private fun downloadProfile() {
        val fileName = "IG_profile_${System.currentTimeMillis()}"
        val username = binding.etProfile.text
//        val abc = "https://www.instagram.com/" + username.toString() + "/channel/?__a=1"
        val url = "https://www.instagram.com/" + username.toString() + "/?__a=1&__d=dis"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val graphqlObject: JSONObject = response.getJSONObject("graphql")
                val shortcodeMediaObject: JSONObject =
                    graphqlObject.getJSONObject("user")

                val nameValue = shortcodeMediaObject.getString("profile_pic_url_hd")

                try {
                    val downloadManager: DownloadManager?
                    downloadManager =
                        context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
            downloadProfile()
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