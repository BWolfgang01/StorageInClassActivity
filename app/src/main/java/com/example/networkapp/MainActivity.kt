package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    private lateinit var file : File
    private val fileName = "comic.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

        file = File(getExternalFilesDir(null), fileName)
        loadSavedComic()
    }

    // Fetches comic from web as JSONObject
    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        Volley.newRequestQueue(this).add (
            JsonObjectRequest(url
                , { response ->
                    showComic(response)
                    saveComic(response)
                }
                , {}
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    private fun loadSavedComic() {
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                val comicObject = JSONObject(jsonString)
                showComic(comicObject)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load saved comic: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No saved comic found in external storage", Toast.LENGTH_SHORT).show()
        }
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        try {
            val file = File(getExternalFilesDir(null), fileName)
            file.writeText(comicObject.toString())
            Toast.makeText(this, "Comic saved successfully to external storage", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save comic: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


}