package com.example.tweety

import android.app.Application;

import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import android.util.Log

class TweetyApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        val config = TwitterConfig.Builder(this.applicationContext)
            .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
            .twitterAuthConfig(
                TwitterAuthConfig(
                   resources.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                    resources.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
            //pass the created app Consumer KEY and Secret also called API Key and Secret
            .debug(true)//enable debug mode
            .build()

        //finally initialize twitter with created configs
        Twitter.initialize(config)
    }
}