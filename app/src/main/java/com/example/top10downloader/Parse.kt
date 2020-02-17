package com.example.top10downloader

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.*
import kotlin.collections.ArrayList

class Parse {
    val application = ArrayList<FeedData>()

    fun parse(xmlData: String):Boolean{

        var status = true
        var insideEntry =false
        var gotImage =false
        var textValue = ""

        try{
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware= true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord= FeedData()

            while(eventType!= XmlPullParser.END_DOCUMENT ){
                val tagName= xpp.name?.toLowerCase(Locale.ENGLISH)

                when(eventType){
                    XmlPullParser.START_TAG -> {
                        if( tagName== "entry" ){
                            insideEntry=true
                        }else if(tagName=="image" && insideEntry ){
                            val imageResolution = xpp.getAttributeValue(null,"height")
                            if( imageResolution.isNotEmpty() && ( imageResolution=="100"  || imageResolution=="170" )  ) gotImage =true
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG ->{
                        when(tagName){
                            "entry" -> {
                                application.add(currentRecord)
                                currentRecord = FeedData()
                                insideEntry = false
                            }
                            "name" -> currentRecord.name= textValue
                            "artist"-> currentRecord.artist= textValue
                            "releasedate" -> currentRecord.releaseDate= textValue
                            "summary" -> currentRecord.summary = textValue
                            "image" -> if(gotImage){
                                currentRecord.imageUrl= textValue
                                gotImage=false
                            }
                        }
                    }

                }
                eventType= xpp.next()
            }

        }catch (e:Exception){
            e.printStackTrace()
            status=false
        }

        return status
    }

}
