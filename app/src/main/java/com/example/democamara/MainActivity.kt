package com.example.democamara

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.democamara.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var outputDirectory: File
    private var imagenCaptura:ImageCapture?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        outputDirectory = getOutputDirectory()
        requestPermissions()
        binding.btnTomarFoto.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera(){
        val camerProviderFeature = ProcessCameraProvider.getInstance(this)
        camerProviderFeature.addListener({
            val cameraProvider = camerProviderFeature.get()
            val preview = Preview.Builder().build().also {
                    mPreview -> mPreview.setSurfaceProvider( binding.viewFinder.surfaceProvider
            )
            }
            imagenCaptura = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imagenCaptura
                )
            }catch (exc:Exception){
                Log.d(Constants.TAG, exc.message!!)
            }
        },ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto(){
        val imageCaptura = imagenCaptura?:return
        val photoFile=File(outputDirectory, SimpleDateFormat(Constants.FILE_NAME_FORMAT,Locale.getDefault())
            .format(System.currentTimeMillis())+".jpg")

        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCaptura.takePicture(
                outputOption, ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback{

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Foto guardada"
                    Log.i(Constants.TAG, "Foto: $msg, $savedUri")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG, "onError: ${exception.message}", exception )
                }

            }
                )

    }

    private  fun  getOutputDirectory():File{
        val mediaDir = externalMediaDirs.firstOrNull()?.let {

                it -> File(it,"demoCamera").apply {
            mkdirs()
        }
        }

        return if( mediaDir != null && mediaDir.exists() ) mediaDir else filesDir
    }

    private fun requestPermissions(){
        if(allPermissionsGranted()){
            // Iniciar la camara
            startCamera()
        }else{
            ActivityCompat.requestPermissions(this, Constants.REQUIERED_PERMISSIONS,Constants.REQUEST_CODE_PERMISSIONS)
        }
    }
    private fun allPermissionsGranted() = Constants.REQUIERED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                //Iniciar Camara
                startCamera()
            }else finish()
        }
    }
}