package com.example.tweety.ui.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.tweety.NavigationUtil

import com.example.tweety.R
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterCore
import kotlinx.android.synthetic.main.fragment_twitter_blank.*

class TwitterLoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_twitter_blank, container, false)
        setUpTwitterLoginButton(view)
        return view
    }

    private fun setUpTwitterLoginButton(view: View) {
        val loginButton =
            view.findViewById<com.twitter.sdk.android.core.identity.TwitterLoginButton>(R.id.login_button)
        loginButton.callback =
            (object : Callback<TwitterSession>() {
                override fun success(result: com.twitter.sdk.android.core.Result<TwitterSession>?) {
                    val session = TwitterCore.getInstance().sessionManager.activeSession
                    val authToken = session.authToken
                    val token = authToken.token
                    val secret = authToken.secret
                    val bundle = login(session)
                    val dataVisualiserFragment = DataVisualiserFragment.newInstance()
                    dataVisualiserFragment.arguments = bundle
                    NavigationUtil.pushFragment(
                        fragmentManager,
                        R.id.container,
                        dataVisualiserFragment,
                        "data_visualiser_fragment"
                        )
                }

                override fun failure(exception: TwitterException) {
                    // Do something on failure
                }
            })
    }

    private fun login(twitterSession: TwitterSession): Bundle {
        val userName = twitterSession.userName
        val authToken = twitterSession.authToken
        Toast.makeText(this.requireContext(), userName, Toast.LENGTH_LONG).show()
        val sessionInfo = Bundle()
        sessionInfo.putString("userName",userName)
        sessionInfo.putString("authToken", authToken.toString())
        sessionInfo.putString("secret", authToken.secret.toString())
        return sessionInfo
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        login_button.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        @JvmStatic
        fun newInstance(): TwitterLoginFragment {
            return TwitterLoginFragment()
        }
    }
}
