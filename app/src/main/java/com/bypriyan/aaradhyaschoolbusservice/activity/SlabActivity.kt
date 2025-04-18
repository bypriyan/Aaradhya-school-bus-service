package com.bypriyan.aaradhyaschoolbusservice.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bypriyan.aaradhyaschoolbusservice.R

class SlabActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private val backPressThreshold: Long = 2000 // 2 seconds

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



            setContentView(R.layout.activity_slab)
        val btn = findViewById<Button>(R.id.btn_Continue)

        val hideButton = intent.getBooleanExtra("hideButton", false)
        // If the extra is true, hide the button
        if (hideButton) {
            btn.visibility = Button.INVISIBLE
        // Hide the button
        }
        btn.setOnClickListener(){
            startActivity(Intent(this, TermsAndConditionActivity::class.java))
            finish()

        }



    }
    override fun onBackPressed() {
        if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}