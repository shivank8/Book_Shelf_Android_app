package com.example.bookshelf.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshelf.R
import com.example.bookshelf.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, private val itemList: ArrayList<Book>):RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single,parent,false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
      val book = itemList[position]
      holder.txtBookName.text=book.bookName
      holder.txtBookAuthor.text=book.bookAuthor
      holder.txtBookPrice.text=book.bookPrice
      holder.txtBookRating.text=book.bookRating
      //holder.txtBookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.txtBookImage)

        holder.Lldashboard.setOnClickListener{
            Toast.makeText(context,"Clicked on ${holder.txtBookName.text}",Toast.LENGTH_SHORT).show()
        }

    }
    class DashboardViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtBookName:TextView= view.findViewById(R.id.txtBookName)
        val txtBookAuthor:TextView= view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice:TextView= view.findViewById(R.id.txtBookPrice)
        val txtBookRating:TextView= view.findViewById(R.id.txtBookRating)
        val txtBookImage:ImageView= view.findViewById(R.id.imgBookImage)
        val Lldashboard:LinearLayout =view.findViewById(R.id.Lldashboard)

    }
}

