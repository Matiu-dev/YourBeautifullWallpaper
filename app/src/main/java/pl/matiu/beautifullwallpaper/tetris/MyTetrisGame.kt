package pl.matiu.beautifullwallpaper.tetris

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.delay
import kotlin.random.Random

class MyTetrisGame {

    private val COLUMN_SIZE = 6
    private val ROW_SIZE = 3

    private val listOfPositions: MutableList<MutableList<Position>> = MutableList(COLUMN_SIZE) {
        MutableList(ROW_SIZE) {
            Position(
                0,
                0,
                0,
                0
            )
        }
    }

    private var listOfFigures = MutableList(COLUMN_SIZE) {
        MutableList<Figure>(ROW_SIZE) {
            Figure(Rect(0, 0, 0, 0), Color.Black.toArgb())
        }
    }

    //sprawdzenie czy moze opasc jakis klocek
    private fun canUpdatePosition(): Boolean {
        var isUpdatedPosition = false

        for (column in listOfFigures.size - 2 downTo 0) {
            for (row in listOfFigures[column].size - 1 downTo 0) {

                if (listOfFigures[column][row].color != Color.Black.toArgb()
                    && listOfFigures[column + 1][row].color == Color.Black.toArgb()
                ) {
                    Log.d("przesuniecie", "tak")

                    listOfFigures[column + 1][row].color = listOfFigures[column][row].color

                    listOfFigures[column][row].color = Color.Black.toArgb()

                    listOfFigures[column + 1][row].rect = Rect(
                        listOfPositions[column + 1][row].left,
                        listOfPositions[column + 1][row].top,
                        listOfPositions[column + 1][row].right,
                        listOfPositions[column + 1][row].bottom
                    )
                    isUpdatedPosition = true
                }
            }
        }

        return isUpdatedPosition
    }

    private fun generateNewRectangle() {
        //jesli zaden nie moze, to stworzenie nowego klocka na srodku najwyzszego wiersza

        val randomValues = Random.nextInt(0, 3)

        if (listOfFigures[0][randomValues].color == Color.Black.toArgb()) {
            val rect = Rect(
                listOfPositions[0][randomValues].left,
                listOfPositions[0][randomValues].top,
                listOfPositions[0][randomValues].right,
                listOfPositions[0][randomValues].bottom
            )


            listOfFigures[0][randomValues] = Figure(rect, Color.Yellow.toArgb())
        }
    }

    private fun refreshCanvas(canvas: Canvas) {

        val cornerRadiusX = 80f
        val cornerRadiusY = 80f

        for (column in 0 until listOfFigures.size) {
            for (row in 0 until listOfFigures[column].size) {
                if (listOfFigures[column][row].color != Color.Black.toArgb()) {

                    val rect = RectF(
                        listOfPositions[column][row].left.toFloat(),
                        listOfPositions[column][row].top.toFloat(),
                        listOfPositions[column][row].right.toFloat(),
                        listOfPositions[column][row].bottom.toFloat()
                    )

                    val centerX = rect.left + rect.width() / 2
                    val centerY = rect.top + rect.height() / 2
                    val radius = Math.max(rect.width(), rect.height()) / 2

                    val paint = Paint().apply {
                        shader =  RadialGradient(
                            centerX, centerY, radius,
                            Color(0xFFADD8E6).toArgb(),
                            Color(0xFF000080).toArgb(),
                            Shader.TileMode.CLAMP
                        )
                        style = Paint.Style.FILL
                    }

                    val borderPaint = Paint().apply {
                        color = Color.Black.toArgb() // Border color
                        strokeWidth = 5f // Border thickness
                        style = Paint.Style.STROKE // Only draws the border
                    }

                    canvas.drawRoundRect(
                        rect,
                        cornerRadiusX,
                        cornerRadiusY,
                        paint
                    )

                    canvas.drawRoundRect(
                        rect,
                        cornerRadiusX,
                        cornerRadiusY,
                        borderPaint
                    )
                }
            }
        }
    }

    private fun canDeleteRow(): Boolean {
        var hasFullRow = false

        for (i in 0 until COLUMN_SIZE) { //kolumny

            if (!(listOfFigures[i][0].color == Color.Black.toArgb())
                && !(listOfFigures[i][1].color == Color.Black.toArgb())
                && !(listOfFigures[i][2].color == Color.Black.toArgb())
            ) {
                listOfFigures[i][0].color = Color.Black.toArgb()
                listOfFigures[i][1].color = Color.Black.toArgb()
                listOfFigures[i][2].color = Color.Black.toArgb()
                hasFullRow = true
            }
        }

        return hasFullRow
    }

    private fun draw(surfaceHolder: SurfaceHolder) {


        val holder = surfaceHolder
        var canvas: Canvas? = null

        try {
            canvas = holder.lockCanvas()
            if (canvas != null) {

                canvas.drawColor(Color.Black.toArgb())

                if (!canUpdatePosition()) {
                    if (!canDeleteRow()) {
                        generateNewRectangle()
                    }
                }

                refreshCanvas(canvas)
            }
        } catch (e: Exception) {
            Log.e("MyWallpaperService", "Error during drawing", e)
        } finally {
            if (canvas != null) {
                try {
                    holder.unlockCanvasAndPost(canvas)
                } catch (e: Exception) {
                    Log.e("MyWallpaperService", "Error posting canvas", e)
                }
            }
        }
    }

    suspend fun tetrisGame(isVisible: Boolean, surfaceHolder: SurfaceHolder) {
        while (isVisible) {
            draw(surfaceHolder = surfaceHolder)//lista figur
            delay(1000)
        }
    }

    fun createMap(width: Int, height: Int) {

        val rectangleHeight = height / COLUMN_SIZE
        val rectangleWidth = width / ROW_SIZE

        //standardowo 20 wierszy i 10 kolumn
        for (i in 0 until COLUMN_SIZE) { //kolumny
            for (j in 0 until ROW_SIZE) {
                listOfPositions[i][j] = Position(
                    i * rectangleHeight, j * rectangleWidth,
                    i * rectangleHeight + rectangleHeight, j * rectangleWidth + rectangleWidth
                )
            }
        }
    }
}


