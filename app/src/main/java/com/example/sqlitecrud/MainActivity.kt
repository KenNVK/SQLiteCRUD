package com.example.sqlitecrud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var editName:EditText
    private lateinit var editEmail:EditText
    private lateinit var btnAdd:Button
    private lateinit var btnView:Button
    private lateinit var sqliteHelper:SQLiteHelper
    private lateinit var recyclerView:RecyclerView
    private lateinit var btnUpdate:Button
    private var adapter: StudentAdapter? = null
    private var std: StudentModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        initRecylerView()
        sqliteHelper = SQLiteHelper(this)

        btnAdd.setOnClickListener{ addStudent() }
        btnView.setOnClickListener{ getStudents() }
        btnUpdate.setOnClickListener{ updateStudent() }
        adapter?.setOnClickItem {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            // update record
            editName.setText(it.name)
            editEmail.setText(it.email)
            std = it
        }

        adapter?.setOnClickDeleteItem {
            deleteStudent(it.id)
        }
    }

    private fun addStudent() {
        val name = editName.text.toString()
        val email = editEmail.text.toString()
        if (name.isEmpty() || email.isEmpty()){
            Toast.makeText(this, "必須フィールドに入力してください。", Toast.LENGTH_SHORT).show()
        } else {
            val std = StudentModel(name = name, email = email)
            val status = sqliteHelper.insertStudent(std)
            //挿入が成功したかどうかを確認します
            if(status > -1) {
                Toast.makeText(this, "追加できました。", Toast.LENGTH_SHORT).show()
                clearEditText()
                getStudents()
            } else {
                Toast.makeText(this, "追加できなかった。", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private  fun getStudents() {
        val stdList = sqliteHelper.getAllStudent()
        Log.e("pppp","${stdList.size}")

        //Display data in RecyclerView
        adapter?.addItems(stdList)
    }

    private fun updateStudent() {
        val name = editName.text.toString()
        val email = editEmail.text.toString()

        //Check record not change
        if(name == std?.name && email == std?.email) {
            Toast.makeText(this, "更新できました。", Toast.LENGTH_SHORT).show()
            return
        }
        if(std == null) return

        val std = StudentModel(id = std!!.id, name = name, email = email )
        val status = sqliteHelper.updateStudent(std)
        if(status > -1) {
            clearEditText()
            getStudents()
        } else {
            Toast.makeText(this, "更新できなかった", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(id:Int) {
        if(id == null) return

        val builder = AlertDialog.Builder(this)
        builder.setMessage("削除しますか？")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes") {dialog, _ ->
            sqliteHelper.deleteStudentById(id)
            getStudents()
            clearEditText()
            dialog.dismiss()}
        builder.setNegativeButton("No") {dialog, _ -> dialog.dismiss()}

        val alert = builder.create()
        alert.show()
    }

    private fun clearEditText() {
        editEmail.setText("")
        editName.setText("")
        editName.requestFocus()
    }

    private fun initRecylerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentAdapter()
        recyclerView.adapter = adapter
    }

    private fun initView() {
        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        btnAdd = findViewById(R.id.btnAdd)
        btnView = findViewById(R.id.btnView)
        recyclerView = findViewById(R.id.recyclerView)
        btnUpdate = findViewById(R.id.btnUpdate)

    }


}