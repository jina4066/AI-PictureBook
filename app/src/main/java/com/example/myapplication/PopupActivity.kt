package com.example.myapplication

import MyDatabase
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityPopupBinding

class PopupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPopupBinding
    private lateinit var bookId: String
    private lateinit var title: String
    private lateinit var myDatabase: MyDatabase
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 상태바 제거
        setContentView(binding.root)
        bookId = intent.getStringExtra("bookId") ?: ""
        title = intent.getStringExtra("title") ?: ""

        myDatabase = MyDatabase.getInstance(this)

        if (!isFinishing) {
            showPopupDialog(bookId, title)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 다이얼로그를 닫기 위해 onDestroy에서 호출합니다.
        alertDialog?.dismiss()
    }

    private fun showPopupDialog(bookId: String, title: String) {
        Log.d("PopupActivity", "BookId: $bookId")
        Log.d("PopupActivity", "Title: $title")

        // 팝업창 생성
        val builder = AlertDialog.Builder(this)

        //버튼 외 화면, backpress 눌러도 화면 꺼지지않음
        builder.setCancelable(false)

        // LinearLayout을 생성하고 binding의 루트 뷰를 추가합니다.
        val container = LinearLayout(this)

        // Remove the view from its current parent
        val parent = binding.root.parent as? ViewGroup
        parent?.removeView(binding.root)

        // Disable clicks and focus for the root view
        binding.root.isClickable = false
        binding.root.isFocusable = false
        val image=myDatabase.getImageForPage(bookId, 0)
        binding.cover.setImageBitmap(image)
        binding.titleText.text = "『$title』의\n 마지막 페이지입니다."

        // Add the view to the new parent
        container.addView(binding.root)

        binding.saveBtn.setOnClickListener {
            // 저장 버튼 클릭 시 동작을 구현
            // TODO: 저장 버튼 동작 구현

            // 팝업창 닫기
            finish()

            // Toast 메시지 표시
            Toast.makeText(this, "동화가 생성되었습니다.", Toast.LENGTH_SHORT).show()

            // 동화읽는 화면으로 가기
            val intent = Intent(this, ReadActivity::class.java)
            intent.putExtra("bookId", bookId) // Pass the bookId as an extra
            startActivity(intent)
        }

        binding.deleteBtn.setOnClickListener {
            // DB 삭제
            myDatabase.deleteBook(bookId)

            // 팝업창 닫기
            finish()
            // Toast 메시지 표시
            Toast.makeText(this, "동화가 저장되지 않았습니다.\n메인화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show()

            // 메인 화면으로 돌아가기
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 팝업창 생성 및 표시
        if (!isFinishing) {
            alertDialog = builder.create()
            alertDialog?.setView(container)
            alertDialog?.show()
        }
    }
}
