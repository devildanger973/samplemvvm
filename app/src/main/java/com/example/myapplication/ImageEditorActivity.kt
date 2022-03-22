package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class ImageEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        selectImage()

    }
    private fun selectImage(){
        val filePath = intent.getStringExtra("FILE_PATH")
        var mPhotograp: ImageView = findViewById(R.id.image_view)
        val uri: Uri
        uri = Uri.parse(filePath)
        mPhotograp.setImageURI(uri)
    }
}