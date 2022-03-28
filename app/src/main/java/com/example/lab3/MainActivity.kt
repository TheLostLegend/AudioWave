package com.example.lab3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mtechviral.mplaylib.MusicFinder

var db: SQLiteDatabase?= null

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = baseContext.openOrCreateDatabase("app.db" ,
            MODE_PRIVATE , null)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

            //Ask for permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),0)
        }
        val viewPager: ViewPager2 = findViewById(R.id.viewpager)

        val adapter = ViewPagerAdapter(this)

        val audioFragment = AudioFragment()

        adapter.addNewFragment(audioFragment)

        viewPager.adapter = adapter

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            val text = "Permission not granted. Shutting down."
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.show()
            finish()
        }
    }

    class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

        var fragments: MutableList<Fragment> = ArrayList()

        fun addNewFragment(newFragment: Fragment) {
            fragments.add(newFragment)
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments.get(position)
        }
    }

}