package com.example.tweety.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tweety.R
import com.example.tweety.ui.fragments.TwitterLoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TwitterLoginFragment.newInstance(), "R.id.twitter_login_fragment")
            .addToBackStack("R.id.twitter_login_fragment")
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the fragment, which will then pass the result to the login
        // button.
        val fragment = supportFragmentManager.findFragmentByTag("R.id.twitter_login_fragment")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}
