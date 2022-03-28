package com.example.lab3

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat.startActivity
import com.example.lab3.GameCore.MainActivity2
import kotlinx.android.synthetic.main.song_ticket.view.*

var CurrentSongPosition:Int = 0

class MySongAdapter(var context: Context) : BaseAdapter()
{
    override fun getView(postion: Int, p1: View?, p2: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myView= inflater.inflate(R.layout.song_ticket, p2, false)
        val Song= myListSong[postion]
        myView.tvSongName.text = Song.Title
        myView.tvSongName.isSelected = true
        myView.tvAuthor.text = Song.AuthorName
        myView.tvAuthor.isSelected = true
        myView.buPlay.setOnClickListener{
            CurrentSongPosition = postion
            val intent = Intent(context, MainActivity2::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK
            startActivity(context,intent,null)
        }
        return  myView
    }

    override fun getItem(item: Int): Any {
        return myListSong[item]
    }

    override fun getItemId(p0: Int): Long {
        return  p0.toLong()
    }

    override fun getCount(): Int {
        return myListSong.size
    }
}
