package com.shamim.frremoteattendence.face_detection
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import com.shamim.frremoteattendence.camerax.GraphicOverlay

class FaceContourGraphic(
    overlay: GraphicOverlay,
    private val face: Face,
    private val imageRect: Rect
) : GraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint
    private val contourPaint: Paint

    init {
        val selectedColor = Color.GREEN

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH

        contourPaint = Paint()
        contourPaint.color = selectedColor
        contourPaint.style = Paint.Style.STROKE
        contourPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    override fun draw(canvas: Canvas?) {
        val rect = calculateRect(
            imageRect.height().toFloat(),
            imageRect.width().toFloat(),
            face.boundingBox
        )

        // Draw the bounding box
        //canvas?.drawRect(rect, boxPaint)

        // Calculate the center of the bounding box
        val centerX = rect.centerX()
        val centerY = rect.centerY()

        // Calculate the radius as a fraction of the box's width or height
        val radius = (rect.width() + rect.height()) / 3.5

        // Draw a circle at the center of the bounding box
        canvas?.drawCircle(centerX, centerY, radius.toFloat(), contourPaint)
    }

    companion object {
        private const val BOX_STROKE_WIDTH = 5.0f
    }
}
