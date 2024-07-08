package com.example.week2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kakao.sdk.user.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendAddActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_add)

        apiService = RetrofitClient.getInstance().create(ApiService::class.java)

        val toolbar: Toolbar = findViewById(R.id.friend_search_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val searchEditText: EditText = findViewById(R.id.friend_search_EditText)
        val searchButton: Button = findViewById(R.id.friend_search_Button)
        val searchResult: LinearLayout = findViewById(R.id.friend_search_result)
        val resultId: TextView = findViewById(R.id.friend_search_result_id)
        val resultNickname: TextView = findViewById(R.id.friend_search_result_nickname)
        val resultButton: Button = findViewById(R.id.friend_search_result_button)

        searchButton.setOnClickListener {
            val userId = searchEditText.text.toString()
            if (userId.isNotEmpty()) {
                searchUser(userId, searchResult, resultId, resultNickname, resultButton)
            } else {
                Toast.makeText(this, "사용자 ID를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun searchUser(userId: String, searchResult: LinearLayout, resultId: TextView, resultNickname: TextView, resultButton: Button) {
        apiService.searchUser(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data
                    searchResult.visibility = View.VISIBLE
                    resultId.text = "아이디: ${user?.login_id}"
                    resultNickname.text = "닉네임: ${user?.nickname}"
                    resultButton.setOnClickListener{
                        Toast.makeText(this@FriendAddActivity, "${user?.nickname}에게 친구 신청을 보냈습니다.",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    searchResult.visibility = View.GONE
                    Toast.makeText(this@FriendAddActivity, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                searchResult.visibility = View.GONE
                Toast.makeText(this@FriendAddActivity, "검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
