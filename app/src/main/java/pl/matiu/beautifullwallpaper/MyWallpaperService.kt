package pl.matiu.beautifullwallpaper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

class MyWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return MyWallpaperEngine()
    }

    inner class MyWallpaperEngine : Engine() {
        private var isVisible: Boolean = false
        private var width: Int = 0
        private var height: Int = 0
        private val paint = Paint().apply {
            color = Color.White.toArgb()
            textSize = 60f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            Log.d("MyWallpaperService", "onSurfaceCreated called")
            draw()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder, format: Int,
            newWidth: Int, newHeight: Int
        ) {
            super.onSurfaceChanged(holder, format, newWidth, newHeight)
            width = newWidth
            height = newHeight
            Log.d("MyWallpaperService", "Surface changed: $width x $height")
            draw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            Log.d("MyWallpaperService", "Visibility changed: $visible")
            if (visible) {
                draw()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            Log.d("MyWallpaperService", "Surface destroyed")
            isVisible = false
        }

        private fun draw() {
            if (!isVisible) {
                Log.d("MyWallpaperService", "Not visible, skipping draw")
                return
            }

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    Log.d("MyWallpaperService", "Drawing on canvas")
                    // Rysuj białe tło
                    canvas.drawColor(Color.Black.toArgb())

                    // Rysuj tekst na środku
                    val text = "Hello World!"
                    val x = width / 2f
                    val y = height - 500.dp.value

                    // Rysuj tekst
                    canvas.drawText(text, x, y, paint)

                    Log.d("MyWallpaperService", "Drawing completed")
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
    }
}