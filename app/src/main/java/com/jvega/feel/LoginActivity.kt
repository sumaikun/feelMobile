package com.jvega.feel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jvega.feel.models.LoginResponse
import com.jvega.feel.network.RetrofitClient
import com.jvega.feel.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var backgroundImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.initialize(this)
        if(TokenManager.getToken()?.isNotEmpty() == true ){
            navigateToMainActivity()
        }
        setContentView(R.layout.activity_login)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        backgroundImage = findViewById(R.id.backgroundImage)

        Glide.with(this)
            .load("https://source.unsplash.com/1600x900/?psychology")
            .centerCrop()
            .into(backgroundImage)

        supportActionBar?.hide() // Hide the app bar

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            Log.d("login", username)
            Log.d("Password", password)

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill in your information to continue",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val apiService = RetrofitClient.apiService
                val call = apiService.login(username, password)

                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            Log.d("loginResponse", loginResponse?.token.toString())
                            if (loginResponse?.token?.isNotEmpty() == true) {
                                TokenManager.saveToken(loginResponse.token)

                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "There is an error in the server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            // Handle login failure
                            Toast.makeText(
                                applicationContext,
                                "Please check your credentials",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        // Handle network or request failure
                        Toast.makeText(
                            applicationContext,
                            "Can't connect to server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Finish the login activity
    }
}
