package com.example.apphw02

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // 獲取從 MainActivity 傳遞過來的 Intent
        val intent = intent
        // 從 Intent 中提取編號
        val itemPosition = intent.getIntExtra("earthquakePosition", -1)

        val client = OkHttpClient()
        //[GET]地震內容URL
        val url = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/E-A0015-001?AuthorizationKey=CWB-45EACE07-4E21-42EF-8A4A-92C944365F79"
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    println(responseBody)

                    val gson = Gson()
                    val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
                    //地震編號----------------------------------------------
                    val earthquakeNo = jsonObject
                        .getAsJsonObject("records")
                        .getAsJsonArray("Earthquake")[itemPosition]
                        .asJsonObject
                        .get("EarthquakeNo")
                        .asInt
                    println("編號---------${earthquakeNo}")
                    //地震時間日期------------------------------------------
                    val earthquakeDataTask = jsonObject
                        .getAsJsonObject("records")
                        .getAsJsonArray("Earthquake")[itemPosition]
                        .asJsonObject
                        .getAsJsonObject("EarthquakeInfo")
                        .get("OriginTime")
                        .asString
                    println("地震時間---------${earthquakeDataTask}")
                    //地震規模------------------------------------------
                    val magnitudeValue = jsonObject
                        .getAsJsonObject("records")
                        .getAsJsonArray("Earthquake")[itemPosition]
                        .asJsonObject
                        .getAsJsonObject("EarthquakeInfo")
                        .getAsJsonObject("EarthquakeMagnitude")
                        .get("MagnitudeValue")
                        .asString
                    //地震位置------------------------------------------
                    val location = jsonObject
                        .getAsJsonObject("records")
                        .getAsJsonArray("Earthquake")[itemPosition]
                        .asJsonObject
                        .getAsJsonObject("EarthquakeInfo")
                        .getAsJsonObject("Epicenter")
                        .get("Location")
                        .asString
                    //地震詳細資訊------------------------------------------
                    val earthquakeURL = jsonObject
                        .getAsJsonObject("records")
                        .getAsJsonArray("Earthquake")[itemPosition]
                        .asJsonObject
                        .get("Web")
                        .asString
                    //地震圖片------------------------------------------
                    val reportImageURI = jsonObject
                        .getAsJsonObject("records")
                        .getAsJsonArray("Earthquake")[itemPosition]
                        .asJsonObject
                        .get("ReportImageURI")
                        .asString
                    println("ReportImageURI---------${reportImageURI}")
                    val imageView = findViewById<ImageView>(R.id.imageView)

                    runOnUiThread {
                        //直接將內容回傳給id名稱為TextView
                        findViewById<TextView>(R.id.TextView1).text = earthquakeNo.toString()
                        findViewById<TextView>(R.id.TextView2).text = earthquakeDataTask.toString()
                        findViewById<TextView>(R.id.TextView3).text = magnitudeValue.toString()
                        findViewById<TextView>(R.id.TextView4).text = location.toString()
                        findViewById<TextView>(R.id.TextView5).text = earthquakeURL.toString()
                        Picasso.get().load(reportImageURI).into(imageView)
                    }
                } else {
                    println("Request failed")
                    runOnUiThread {
                        findViewById<TextView>(R.id.textViewData).text = "資料錯誤"
                    }
                }
            }
        })

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
