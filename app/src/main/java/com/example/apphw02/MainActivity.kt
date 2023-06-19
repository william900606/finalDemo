@file:Suppress("DEPRECATION")

package com.example.apphw02

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.apphw02.databinding.ActivityMainBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var earthquakeList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        earthquakeList = ArrayList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            earthquakeList
        )
        binding.listView.adapter = adapter

        binding.listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedEarthquake = earthquakeList[position]
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            intent.putExtra("earthquakePosition", position)
            startActivity(intent)
        }


        // 下拉式選單選項
        val options = arrayOf("10 筆", "15 筆", "20 筆","25 筆")

        // 選項適配器
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter

        // 選項選擇監聽器
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = options[position]
                val numRecords = selectedOption.split(" ")[0].toInt()

                FetchEarthquakeDataTask().execute(numRecords)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 不執行任何操作
            }
        }
    }

    inner class FetchEarthquakeDataTask : AsyncTask<Int, Void, List<String>>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Int?): List<String> {
            val numRecords = params[0] ?: 5

            val url = URL("https://opendata.cwb.gov.tw/api/v1/rest/datastore/E-A0015-001?AuthorizationKey=CWB-45EACE07-4E21-42EF-8A4A-92C944365F79&limit=$numRecords")
            val connection = url.openConnection() as HttpURLConnection

            var result = ""
            try {
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                result = stringBuilder.toString()
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                connection.disconnect()
            }

            val earthquakeData = ArrayList<String>()

            try {
                val jsonObject = JSONObject(result)
                val recordsObject = jsonObject.getJSONObject("records")
                val earthquakeArray = recordsObject.getJSONArray("Earthquake")

                for (i in 0 until earthquakeArray.length()) {
                    val earthquakeObject = earthquakeArray.getJSONObject(i)
                    val reportContent = earthquakeObject.getString("ReportContent")
                    earthquakeData.add(reportContent)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return earthquakeData
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: List<String>) {
            earthquakeList.clear()
            earthquakeList.addAll(result)
            (binding.listView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
        }
    }
}