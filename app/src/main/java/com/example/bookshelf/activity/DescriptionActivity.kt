package com.example.bookshelf.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookshelf.R
import com.example.bookshelf.database.BookDatabase
import com.example.bookshelf.database.BookEntity
import com.example.bookshelf.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class DescriptionActivity : AppCompatActivity() {

    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFav: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar

    var bookid: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookRating = findViewById(R.id.txtBookRating)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        imgBookImage = findViewById(R.id.imgBookImage)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookid = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(this, "Some unexpected error found", Toast.LENGTH_SHORT).show()
        }
        if (bookid == "100") {
            finish()
            Toast.makeText(this, "Some unexpected error found", Toast.LENGTH_SHORT).show()

        }
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookid)
        if (ConnectionManager().checkConnectivity(this)) {
            val jsonObject =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            //Toast.makeText(this,"Everything working Fine",Toast.LENGTH_SHORT).show()

                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            val bookImageUrl=bookJsonObject.getString("image")
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity=BookEntity(
                            bookid?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDesc.text.toString(),
                            //Picasso.get().load(bookImageUrl).error(R.drawable.default_book_cover).into(imgBookImage)
                            bookImageUrl
                            )
                            val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav=checkFav.get()

                            if(isFav){
                                btnAddToFav.text="Remove from Favourites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.favouriteChanging)
                                btnAddToFav.setBackgroundColor(favColor)
                            }else{
                                btnAddToFav.text = "Add to Favourites"
                                val favColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(favColor)
                            }
                            btnAddToFav.setOnClickListener{
                                if(DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                    val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                    val result=async.get()
                                    if(result){
                                        Toast.makeText(this, "Book added to favourites", Toast.LENGTH_SHORT)
                                            .show()
                                        btnAddToFav.text="Remove from favourites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.favouriteChanging)
                                        btnAddToFav.setBackgroundColor(favColor)

                                    }else{
                                        if(result){
                                            Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT)
                                                .show()


                                    }}
                                }else{
                                    val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result=async.get()
                                    if(result){
                                        Toast.makeText(this, "Book Removed from favourites", Toast.LENGTH_SHORT)
                                            .show()
                                        btnAddToFav.text="Add to favourites"
                                        val favColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                        btnAddToFav.setBackgroundColor(favColor)

                                    }else{
                                        if(result){
                                            Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT)
                                                .show()


                                        }}
                                }
                            }

                        } else {
                            Toast.makeText(this, "Some unexpected error found", Toast.LENGTH_SHORT)
                                .show()

                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Some unexpected error found", Toast.LENGTH_SHORT)
                            .show()

                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley Error is $it", Toast.LENGTH_SHORT).show()

                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "e621bdb7e2f1c1"
                        return headers

                    }

                }
            queue.add(jsonObject)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection Found")
            dialog.setPositiveButton("Open Setting") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }
    class  DBAsyncTask(val context:Context,val bookEntity: BookEntity, val mode:Int): AsyncTask<Void, Void, Boolean>() {

        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()


        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode) {
                1 -> {
                    val book:BookEntity?=db.bookDao().getBookByid(bookEntity.book_id.toString())
                    db.close()
                    return book !=null
                }

                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true

                }
                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }
            return false
        }

    }
    }
