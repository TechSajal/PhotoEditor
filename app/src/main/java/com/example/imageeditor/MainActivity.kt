package com.example.imageeditor

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.imageeditor.EditImageActivity
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var imageView: ImageView? = null
    var cameraImage: File? = null
    var textView: TextView? = null
    var galleryButton: Button? = null
    var textviewtwo:TextView? = null
    var editedimage:TextView? = null
    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                photo

            }
        if ( requestCode== 2 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2)
        }

    }

    var currentPhotoPath: String? = null
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        currentPhotoPath = image.absolutePath
        println(currentPhotoPath)
        return image
    }

    private val photo: Unit
        @SuppressLint("QueryPermissionsNeeded") get() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                var photoFile: File?
                photoFile = createImageFile()
                val photoURI:Uri = FileProvider.getUriForFile(this, "com.example.imageeditor.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                startActivityForResult(takePictureIntent, 1)
            }
        }

    private fun makeBitmapNull() {
        EditImageActivity.croppedBitmap = null
        EditImageActivity.rotateBitmap = null
        EditImageActivity.cropThenRotateBitmap = null
        EditImageActivity.rotateThenCropBitmap = null
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        textviewtwo = findViewById(R.id.textView2)
        textView = findViewById(R.id.textView)
        galleryButton = findViewById(R.id.galleryButton)
        editedimage = findViewById(R.id.editedimage)
        if (EditImageActivity.cropThenRotateBitmap != null) {
            imageView!!.setImageBitmap(EditImageActivity.cropThenRotateBitmap)
            textView!!.visibility = View.GONE
            textviewtwo!!.visibility =View.GONE
            editedimage!!.visibility =View.VISIBLE
            makeBitmapNull()
        } else if (EditImageActivity.rotateThenCropBitmap != null) {
            imageView!!.setImageBitmap(EditImageActivity.rotateThenCropBitmap)
            textView!!.visibility = View.GONE
            textviewtwo!!.visibility =View.GONE
            editedimage!!.visibility =View.VISIBLE
            makeBitmapNull()
        } else if (EditImageActivity.rotateBitmap != null) {
            imageView!!.setImageBitmap(EditImageActivity.rotateBitmap)
            textView!!.visibility = View.GONE
            textviewtwo!!.visibility =View.GONE
            editedimage!!.visibility =View.VISIBLE
            makeBitmapNull()
        } else if (EditImageActivity.croppedBitmap != null) {
            imageView!!.setImageBitmap(EditImageActivity.croppedBitmap)
            textView!!.visibility = View.GONE
            textviewtwo!!.visibility =View.GONE
            editedimage!!.visibility =View.VISIBLE
            makeBitmapNull()
        } else if (bitmap != null) {
            imageView!!.setImageBitmap(bitmap)
            textView!!.visibility = View.GONE
            textviewtwo!!.visibility =View.GONE
            editedimage!!.visibility =View.VISIBLE
            makeBitmapNull()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Throws(IOException::class)
    fun clickSelfie(view: View?) {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            photo
        }

    }

    fun clickGallery(view: View?) {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2)
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
                bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                 val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$imageFileName.jpg")
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                 uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                var imageOutStream: OutputStream? = null
                try {
                    imageOutStream = contentResolver.openOutputStream(uri!!)
                    Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
                } finally {
                    imageOutStream?.close()
                    val intent = Intent(this, EditImageActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                }

        }
        else{
             if(requestCode == 2 && resultCode == RESULT_OK) {
            uri1 = data!!.data
               bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri1)
            val intent = Intent(this, EditImageActivity::class.java)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            startActivity(intent)
        } }
    }

    companion object {
        var bitmap: Bitmap? = null
        var imageFileName: String? = null
        var uri: Uri? = null
        var uri1: Uri? = null
    }
}