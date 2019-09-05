package com.microdevrj.superellipseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.microdevrj.superellipse.performance.SuperellipseLogic

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onDestroy() {
        super.onDestroy()
        SuperellipseLogic.release()
    }
}
