package es.tipolisto.player.Activities


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar


import es.tipolisto.player.R

import android.view.Menu


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        toolbar.setOnMenuItemClickListener { false }

    }


    /**
     * Menú
     */



}
