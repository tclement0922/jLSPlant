package dev.tclement.jlsplant.demo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.tclement.jlsplant.LSPlant

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resources.getColor(R.color.purple_500)
        findViewById<TextView>(R.id.status).text =
            "LSPlant initialization state : ${if (LSPlant.isInitialized()) "initialized" else "not initialized"}"


        val button = findViewById<Button>(R.id.hook_button)
        button.text = "Hook"
        button.setOnClickListener {
            it as Button
            if (DemoHook.state) {
                DemoHook.unhook()
                it.text = "Hook"
            } else {
                DemoHook.hook()
                it.text = "Unhook"
            }
            DemoHook.state = !DemoHook.state
        }
        val colorView = findViewById<View>(R.id.color_view)
        colorView.setBackgroundColor(resources.getColor(R.color.lsplant_demo_color))
        colorView.setOnClickListener {
            it.setBackgroundColor(resources.getColor(R.color.lsplant_demo_color))
        }
        val textView = findViewById<TextView>(R.id.text_view)
        textView.text = DemoHook.helloWorld()
        textView.setOnClickListener {
            textView.text = DemoHook.helloWorld()
        }
    }
}