package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL


class FeedData {
    var name = ""
    var artist = ""
    var summary = ""
    var releaseDate = ""
    var imageUrl = ""
    override fun toString(): String {
        return """
            name : $name
            artist : $artist
            releaseDate : $releaseDate
            image Url : $imageUrl
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    private val STATUS_URL = "status_url"
    private val STATUS_FEED = "status_feed"
    private val STATUS_TITLE= "status_title"
    private var downloadData : DownloadData?  = null
    private var myUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
    private var feedLimit = 10
    private var Title = "Top %d Paid Apps"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadURL()
    }

    private fun downloadURL(){
        findViewById<TextView>(R.id.currentFeed).text = Title.format(feedLimit)
        downloadData = DownloadData(this,xmlListView)
        downloadData?.execute(myUrl.format(feedLimit))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feed_menu,menu)

        if( feedLimit==10 )  menu?.findItem(R.id.top10)?.isChecked = true
        else  menu?.findItem(R.id.top25)?.isChecked = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var newUrl = myUrl
        var newFeedLimit= feedLimit
        var refresh = false
        when(item.itemId){
            R.id.freeAppsMenu ->{
                newUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
                Title = "Top %d free Apps"
            }
            R.id.paidAppsMenu ->{
                newUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
                Title = "Top %d Paid Apps"
            }
            R.id.songsMenu -> {
                newUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
                Title = "Top %d Songs"
            }
            R.id.top10, R.id.top25 -> {
                item.isChecked=true
                newFeedLimit = 35-feedLimit
            }
            R.id.refresh -> refresh= true
            else -> return super.onOptionsItemSelected(item)
        }

        if( newFeedLimit != feedLimit || newUrl != myUrl || refresh ) {
            feedLimit = newFeedLimit
            myUrl = newUrl
            downloadURL()
        }else findViewById<TextView>(R.id.currentFeed).text = Title.format(feedLimit)

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATUS_FEED, feedLimit)
        outState.putString(STATUS_URL, myUrl )
        outState.putSerializable(STATUS_TITLE, Title)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        feedLimit = savedInstanceState.getInt(STATUS_FEED)
        myUrl = savedInstanceState.getString(STATUS_URL,null)
        Title = savedInstanceState.getString(STATUS_TITLE,null)
        downloadURL()
    }

    companion object {

        private class DownloadData(val context: Context, val listView: ListView) : AsyncTask<String, Void, String>() {

            val tag2 = "DownloadData"
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.d(tag2, "OnPostExecute called")
                val parseApplication = Parse()
                if (!parseApplication.parse(result)) {
                    Log.d(tag2, "Parsing failed !!")
                }


                val feedAdapter = FeedAdapter(context,R.layout.list_record,parseApplication.application)
                listView.adapter = feedAdapter

            }

            override fun doInBackground(vararg params: String?): String {
                Log.d(tag2, "doInBackground called")

                val data = downloadXml(params[0])
                if (data.isEmpty()) Log.e( tag2, "doingBackground : Something went wrong while downloading" )

                return data
            }

            private fun downloadXml(urlPath: String?): String {
                return URL(urlPath).readText()
                /*
                val xmlData= StringBuilder()

                try{
                    val url = URL(urlPath)
                    val connection= url.openConnection() as HttpURLConnection
                    val response = connection.responseCode
                    Log.d(tag2,"Response code is $response ")

                    connection.inputStream.buffered().reader().use { xmlData.append(it.readText()) }

                    return xmlData.toString()
                }catch(e:Exception){
                    val message = when(e){
                        is MalformedURLException -> "downloadXml : Url Malformed Exception : ${e.message}"
                        is IOException -> "downloadXml : IO exception :${e.message} "
                        else -> "downloadXml : Some kind of error occurred ${e.message}"
                    }
                    Log.e(tag2,message)
                }

                return ""
                */
            }

        }
    }

}