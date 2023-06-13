package com.anmol.a7minutesworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.FrameLayout
import android.widget.Toast
import com.anmol.a7minutesworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // The binding is name just like the name of the layout with Binding attached
    // We create a variable for it and assign to null

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // We inflate the late file by calling inflate on the Binding name
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Then replace the setContentView parameter with binding?.root
        setContentView(binding?.root)
       // val flStartButton: FrameLayout = findViewById(R.id.flStart)
        binding?.flStart?.setOnClickListener {

            // When click to start then open new page.
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // TO avoid memory leak we unassign the binding once the activity is destroyed
        binding = null
    }
}
