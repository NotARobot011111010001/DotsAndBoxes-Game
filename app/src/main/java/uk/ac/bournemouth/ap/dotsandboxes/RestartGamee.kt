package uk.ac.bournemouth.ap.dotsandboxes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.TextView

class RestartGamee : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restart_gamee)
/*
        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.RestartButton).apply {
            text = message
        }*/
    }
}