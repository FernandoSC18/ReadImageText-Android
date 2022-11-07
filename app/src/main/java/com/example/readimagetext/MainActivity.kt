package com.example.readimagetext

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private lateinit var btnAddImage : Button
    private lateinit var btnProcessImage : Button
    private lateinit var ivImage : ImageView
    private lateinit var tvImageText : TextView

    private lateinit var readImageText : ReadImageText

    private val imageChose = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK)
            ivImage.setImageURI(it.data?.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddImage = findViewById(R.id.btnAdd)
        btnProcessImage = findViewById(R.id.btnProc)
        ivImage = findViewById(R.id.ivSource)
        tvImageText = findViewById(R.id.tvResult)

        btnAddImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT

            imageChose.launch (intent)
        }

        btnProcessImage.setOnClickListener {
            if (ivImage.drawable != null){
                lifecycleScope.launch {
                    val bitmapDrawable : BitmapDrawable = ivImage.drawable as BitmapDrawable
                    tvImageText.text = readImageText.processImage(bitmapDrawable.bitmap, "spa")
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        val permissionCheck = ContextCompat.checkSelfPermission(
            applicationContext, STORAGE_PERMISSION
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            //Not allow permission Storage
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, STORAGE_PERMISSION)){
                ActivityCompat.requestPermissions(this, arrayOf(STORAGE_PERMISSION), 0)
            }else{
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts( "package", this.packageName, null )
                this.startActivity(intent)
            }
        }else {
            //Storage permissions are allow
            readImageText = ReadImageText(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        readImageText.recycle()
    }

}