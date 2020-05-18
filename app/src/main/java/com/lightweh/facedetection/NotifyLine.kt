package com.lightweh.facedetection

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.*
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


open class NotifyLine {
    companion object {
        var token2 = "m0L2CqcTfznaEVldjUArL6LSM6EJvJBZn8448gU4gxm"//old token Android+PHP group
        var token = "cRUP9bf99gQZE6456geLkZFyDFmH0DDe5lSTyl8Fetq"//new token

        fun sendNotifyImage(msg: String, image: File) {
            AndroidNetworking
                .post("https://notify-api.line.me/api/notify")
                .addHeaders("Authorization", "Bearer "+token)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addBodyParameter("message", msg)
//                .addBodyParameter("imageThumbnail", msg)
//                .addBodyParameter("imageFullsize", msg)
//                .addBodyParameter("imageFile", image)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.e("panya", "onResponse : $response")
                    }

                    override fun onError(error: ANError) {
                        Log.e("panya", "onError : " + error.errorBody)
                    }
                })
        }

        fun sendImageNotifyImage(msg: String, image: File) {
            AndroidNetworking
                .upload("https://notify-api.line.me/api/notify")
                .addHeaders("Authorization", "Bearer "+token)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addMultipartFile("imageFile", image)
                .addMultipartParameter("message", "เครื่อง "+PreferencesManager.getInstance().deviceName +" : "+msg)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Log.e("panya", "onResponse : $response")
                    }

                    override fun onError(error: ANError) {
                        Log.e("panya", "onError : " + error.errorBody)
                    }
                })
        }

        fun bitmapToFile(context: Context, bitmap: Bitmap, msg: String, color: Int): File {
            // Get the context wrapper
            var newBitmap = drawTextToBitmap(context, bitmap, msg, color)
            val wrapper = ContextWrapper(context)

            // Initialize a new file instance to save bitmap object
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file,"test.jpg")

            try{
                // Compress the bitmap and save in jpg format
                val stream = FileOutputStream(file)
                newBitmap?.compress(Bitmap.CompressFormat.JPEG,100, stream)
                stream.flush()
                stream.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
            // Return the saved bitmap uri
            return file
        }

        fun drawTextToBitmap(gContext: Context, bitmap: Bitmap, gText: String, color: Int): Bitmap? {
            val c: Calendar = Calendar.getInstance()
            val df = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val formattedDate: String = df.format(c.getTime())
            var bitmapConfig = bitmap.config
            val resources: Resources = gContext.resources
            val scale: Float = resources.getDisplayMetrics().density
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888
            }
            Log.e("panya", "size "+ bitmap.width +"X"+bitmap.height)
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            var bitmap = bitmap.copy(bitmapConfig, true)
            val canvas = Canvas(bitmap)
            val bitmapLogoTop = BitmapFactory.decodeResource(resources, R.drawable.logo_top)

            canvas.drawBitmap(bitmap, Matrix(), null)
            canvas.drawBitmap(Bitmap.createScaledBitmap(bitmapLogoTop, bitmap.width, 130, false),  Matrix(), null)
            // new antialised Paint
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // text color - #3D3D3D
            paint.color = Color.rgb(255,255, 255 )
            // text size in pixels
            paint.setTextSize((40 * scale).toFloat())
            // draw text to the Canvas center
            val bounds = Rect()
            paint.getTextBounds(gText, 0, gText.length, bounds)
            val x = (bitmap.width - bounds.width()) / 2
            val y = (bitmap.height + bounds.height()) / 1.1


            paint.color = color
            canvas.drawRect(0f, (bitmap.height-(bounds.height()*2)).toFloat(),bitmap.width.toFloat(),bitmap.height.toFloat(), paint)
            paint.color = Color.rgb(255,255, 255)
            canvas.drawText(gText, x.toFloat(), y.toFloat(), paint)


            ///////////
            val paint1 = Paint(Paint.ANTI_ALIAS_FLAG)

            // text color - #3D3D3D
            paint1.color = Color.rgb(255,255, 255 )
            // text size in pixels
            paint1.setTextSize((25 * scale).toFloat())
            // draw text to the Canvas center
            val bounds1 = Rect()
            val detail = PreferencesManager.getInstance().deviceName
            paint1.getTextBounds(detail, 0, detail.length, bounds1)
            val x1 = (bitmap.width - bounds1.width()) / 2
            val y1 = ((bitmap.height-(bounds1.height()+ bounds.height())).toFloat())/1.05

            paint1.color = Color.rgb(255,255, 255)
            canvas.drawRect(0f, (bitmap.height-((bounds1.height()+ bounds.height())*2)).toFloat(),bitmap.width.toFloat(),(bitmap.height-(bounds.height()*2)).toFloat(), paint1)
            paint1.color = Color.rgb(0,0, 0)
            canvas.drawText(detail, 10f, y1.toFloat(), paint1)


            paint1.color = Color.rgb(0,0, 0)
            canvas.drawText(formattedDate, (bitmap.width/2)+ 10f, y1.toFloat(), paint1)

            return bitmap
        }

    }
}