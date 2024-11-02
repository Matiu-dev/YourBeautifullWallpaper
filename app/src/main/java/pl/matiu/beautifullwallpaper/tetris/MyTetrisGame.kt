package pl.matiu.beautifullwallpaper.tetris

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.random.Random

class MyTetrisGame {

    private val COLUMN_SIZE = 6
    private val ROW_SIZE = 3

    val listOfPositions: MutableList<MutableList<Position>> = MutableList(COLUMN_SIZE) {
        MutableList(ROW_SIZE) {
            Position(
                0,
                0,
                0,
                0
            )
        }
    }

    var listOfFigures = MutableList(COLUMN_SIZE) {
        MutableList<Figure>(ROW_SIZE) {
            Figure(Rect(0, 0, 0, 0), Color.Black.toArgb())
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

    //sprawdzenie czy moze opasc jakis klocek
    fun canUpdatePosition(): Boolean {
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

    fun generateNewRectangle() {
        //jesli zaden nie moze, to stworzenie nowego klocka na srodku najwyzszego wiersza

        val randomValues = Random.nextInt(0, 3)

        if (listOfFigures[0][randomValues].color == Color.Black.toArgb()) {
            val rect = Rect(
                listOfPositions[0][randomValues].left,
                listOfPositions[0][randomValues].top,
                listOfPositions[0][randomValues].right,
                listOfPositions[0][randomValues].bottom
            )


            listOfFigures[0][randomValues] = Figure(rect, randomColor())
        }
    }

    fun randomColor(): Int {
        val randomValues = Random.nextInt(0, 10)
        return when (randomValues) {
            0 -> Color.Blue.toArgb()
            1 -> Color.Green.toArgb()
            2 -> Color.Red.toArgb()
            3 -> Color.Cyan.toArgb()
            4 -> Color.LightGray.toArgb()
            5 -> Color.Magenta.toArgb()
            6 -> Color.White.toArgb()
            7 -> Color.DarkGray.toArgb()
            8 -> Color.Gray.toArgb()
            9 -> Color.Yellow.toArgb()
            else -> Color.Yellow.toArgb()
        }
    }

    fun refreshCanvas(canvas: Canvas) {
        for (column in 0 until listOfFigures.size) {
            for (row in 0 until listOfFigures[column].size) {
                if (!listOfFigures[column][row].color.equals(Color.Black)) {
                    canvas.drawRect(Rect(
                        listOfPositions[column][row].left,
                        listOfPositions[column][row].top,
                        listOfPositions[column][row].right,
                        listOfPositions[column][row].bottom
                    ),
                        Paint().apply { color = listOfFigures[column][row].color })
                }
            }
        }
    }

    fun canDeleteRow(): Boolean {
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

}


