package com.example.lab3.GameCore

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.lab3.R
import java.util.*

class ChampionDashboard : AppCompatActivity() {
    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_champion_dashboard)
        val players: ArrayList<String?> = ArrayList<String?>()
        val db=baseContext.openOrCreateDatabase("app.db" , MODE_PRIVATE , null)
        val query=db.rawQuery("SELECT * FROM players ORDER BY result DESC;" , null)
        var i=0
        while (query.moveToNext()) {
            val name=query.getString(0)
            val result=query.getString(1)
            i++
            players.add("$i) $result\t,by\t:$name")
        }
        val listView=findViewById<ListView>(R.id.list)
        val adapter= ArrayAdapter(this , android.R.layout.simple_list_item_1,
            players as List<*>
        )
        listView.adapter=adapter
    }

    fun onDashboardClosed(view: View?) {
        val intent=Intent(this , MainActivity2::class.java)
        startActivity(intent)
    }
}