package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.R

class SlabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_slab)
        val btn = findViewById<Button>(R.id.btn_Continue)

        val hideButton = intent.getBooleanExtra("hideButton", false)
        // If the extra is true, hide the button
        if (hideButton) {
            btn.visibility = Button.INVISIBLE  // Hide the button
        }
        btn.setOnClickListener(){
            startActivity(Intent(this, TermsAndConditionActivity::class.java))
        }



    }
}