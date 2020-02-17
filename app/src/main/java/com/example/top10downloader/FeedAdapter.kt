package com.example.top10downloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class ViewHolder(v: View){
    val tvName = v.findViewById(R.id.tvName) as TextView
    val tvArtist = v.findViewById(R.id.tvArtist) as TextView
    val tvSummary = v.findViewById(R.id.tvSummary) as TextView
    val myImage = v.findViewById(R.id.myIconicImage) as ImageView
}

class FeedAdapter(context : Context,private val resource :Int,private val application : ArrayList<FeedData>) : ArrayAdapter<FeedData>(context, resource) {

    private val inflater= LayoutInflater.from(context)

    override fun getCount(): Int {
        return application.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var viewHolder : ViewHolder
        lateinit var view : View

        if( convertView == null ){
            view = inflater.inflate(resource, parent, false)
            viewHolder= ViewHolder(view)
            view.tag = viewHolder
        }else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.tvName.text = application[position].name
        viewHolder.tvArtist.text = application[position].artist
   //     viewHolder.tvSummary.text = application[position].summary
      //  viewHolder.myImage.setImageBitmap( getBitmapFromURL(application[position].imageUrl) )
        ImageLoadTask(application[position].imageUrl,viewHolder.myImage).execute()

        return view
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            myBitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    companion object{
        private  class ImageLoadTask(private val url: String, private val imageView: ImageView) :  AsyncTask<Void?, Void?, Bitmap?>() {
            override fun doInBackground(vararg params: Void?): Bitmap? {
                try {
                    val urlConnection = URL(url)
                    val connection = urlConnection.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    return BitmapFactory.decodeStream(input)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                imageView.setImageBitmap(result)
            }

        }
    }

}


