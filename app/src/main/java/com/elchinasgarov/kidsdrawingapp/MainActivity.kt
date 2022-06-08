package com.elchinasgarov.kidsdrawingapp

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.elchinasgarov.DrawingView
import com.elchinasgarov.kidsdrawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPath : ImageButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawingView = findViewById(R.id.drawing_view)

        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout_colors)
        mImageButtonCurrentPath = linearLayout[1] as ImageButton
        mImageButtonCurrentPath!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.selected_colors)
        )
        val brush: ImageButton = findViewById(R.id.brush_btn)
        brush.setOnClickListener {
            showBrushChooserDialog()
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
}