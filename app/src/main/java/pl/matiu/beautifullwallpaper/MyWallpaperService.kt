package pl.matiu.beautifullwallpaper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.matiu.beautifullwallpaper.tetris.Figure
import pl.matiu.beautifullwallpaper.tetris.MyTetrisGame

class MyWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }

    inner class MyWallpaperEngine : Engine() {
        private var isVisible: Boolean = false
        private var width: Int = 0
        private var height: Int = 0
        private val scope = CoroutineScope(Dispatchers.Main)
        private val myTetrisGame: MyTetrisGame = MyTetrisGame()

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            Log.d("MyWallpaperService", "onSurfaceCreated called")
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder, format: Int,
            newWidth: Int, newHeight: Int
        ) {
            super.onSurfaceChanged(holder, format, newWidth, newHeight)
            width = newWidth
            height = newHeight
            myTetrisGame.createMap(width, height)
//            Log.d("map",  myTetrisGame.listOfPositions.toString())
//            Log.d("MyWallpaperService", "Surface changed: $width x $height")
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            Log.d("MyWallpaperService", "Visibility changed: $visible")
            if (visible) {
                scope.coroutineContext.cancelChildren()
                scope.launch {
                    myTetrisGame.tetrisGame(
                        isVisible = isVisible,
                        surfaceHolder = surfaceHolder
                    )
                }
            } else {
                scope.coroutineContext.cancelChildren()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
//            Log.d("MyWallpaperService", "Surface destroyed")
            scope.cancel()
            isVisible = false
        }
    }
}