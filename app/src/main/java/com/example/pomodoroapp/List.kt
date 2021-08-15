package com.example.pomodoroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import java.util.ArrayList

class List : AppCompatActivity() {

    private var editTxt: EditText? = null
    private var btn: Button? = null
    private var list: ListView? = null
    private var adapter: ArrayAdapter<String>? = null
    private var arrayList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        editTxt = findViewById(R.id.editText) as EditText?
        btn = findViewById(R.id.button) as Button?
        list = findViewById(R.id.listView) as ListView?
        arrayList = ArrayList()

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = ArrayAdapter(
            getApplicationContext(), android.R.layout.simple_spinner_item,
            arrayList!!
        )

        // Here, you set the data in your ListView
        list?.setAdapter(adapter)
        btn?.setOnClickListener {

            // this line adds the data of your EditText and puts in your array
            arrayList!!.add(editTxt!!.text.toString())
            // next thing you have to do is check if your adapter has changed
            adapter!!.notifyDataSetChanged()
        }
    }
}