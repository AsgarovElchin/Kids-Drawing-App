package com.elchinasgarov.kidsdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.elchinasgarov.DrawingView
import com.elchinasgarov.kidsdrawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageBackGround: ImageView = findViewById(R.id.iv_background)
                imageBackGround.setImageURI(result.data?.data)
            }
        }
    val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult((ActivityResultContracts.RequestMultiplePermissions())) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    Toast.makeText(
                        this,
                        "Permission granted now you can read the storage files",
                        Toast.LENGTH_SHORT
                    ).show()

                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)

                } else {
                    if (permissionName == android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        Toast.makeText(
                            this,
                            "Oops you just denied the permission",
                            Toast.LENGTH_SHORT
                        ).show()

                }
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawingView = findViewById(R.id.drawing_view)

        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout_colors)
        mImageButtonCurrentPaint = linearLayout[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.selected_colors)
        )
        val brush: ImageButton = findViewById(R.id.brush_btn)
        brush.setOnClickListener {
            showBrushChooserDialog()
        }

        val importButton: ImageButton = findViewById(R.id.import_btn)
        importButton.setOnClickListener {
            requestStoragePermission()
        }
        val undo: ImageButton = findViewById(R.id.undo_btn)
        undo.setOnClickListener {
            drawingView?.onClickUndo()
        }
        val save: ImageButton = findViewById(R.id.save_btn)
        save.setOnClickListener {
            if(isReadStorageAllowed()){
                lifecycleScope.launch {
                    val flDrawingView : FrameLayout = findViewById(R.id.fl_drawing_view_container)
                    saveBitmapFile(getBitmapFromView(flDrawingView))

                } }
            }

        }




    private fun showBrushChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size :")
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.small_brush)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()

        }
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.medium_brush)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()

        }
        val largeBtn: ImageButton = brushDialog.findViewById(R.id.large_brush)
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()

        }

        brushDialog.show()
    }

    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,

            )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationableDialog(
                "Kids Drawing App",
                "Kids Drawing App" + "needs to access your External Storage"
            )

        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }


    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)

        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)

                    val file = File(
                        externalCacheDir?.absoluteFile.toString() +
                                File.separator + "KidsDrawingApp_" + System.currentTimeMillis() / 1000 + ".png"
                    )
                    val fileOutput = FileOutputStream(file)
                    fileOutput.write(bytes.toByteArray())
                    fileOutput.close()
                    result = file.absolutePath

                    runOnUiThread {
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File saved successfully : $result",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Something went wrong while saving the file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }

        }
        return result
    }


    private fun showRationableDialog(title: String, messag: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(messag)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    fun paintClicked(view: View) {
        if (view != mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.selected_colors)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pallet_normal)
            )
            mImageButtonCurrentPaint = view
        }
    }
}