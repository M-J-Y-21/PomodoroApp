package com.example.pomodoroapp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.pomodoroapp.MainActivity
import com.example.pomodoroapp.R
import com.example.pomodoroapp.SharedViewModel
import com.example.pomodoroapp.databinding.ActivityMainBinding
import com.example.pomodoroapp.databinding.DialogTagBinding
import java.lang.IllegalStateException
import java.util.ArrayList

class WorkTagDialogFragment : DialogFragment() {


    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val tagList = resources.getStringArray(R.array.work_tag_array)
        return activity?.let {
            var checkedTag = -1
            // use builder to construct dialog
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.tag_dialog)
                .setSingleChoiceItems(
                    R.array.work_tag_array, checkedTag,
                    DialogInterface.OnClickListener { dialog, which ->
                        checkedTag = which
                    })
                .setPositiveButton(
                    R.string.set_work_tag,
                    DialogInterface.OnClickListener { dialog, id ->
                        when (checkedTag) {
                            0 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to set a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag(tagList[checkedTag].toString())
//                                val intent = Intent(context, MainActivity::class.java)
//
//                                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
//                                intent.putExtra("tag", "Study")
//                                startActivity(intent)
                            }
                            1 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to set a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag(tagList[checkedTag].toString())

                            }
                            2 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to set a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag(tagList[checkedTag].toString())
                            }
                            3 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to set a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag(tagList[checkedTag].toString())
                            }
                        }
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        when (checkedTag) {
                            0 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to cancel a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag("")
                            }
                            1 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to cancel a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag("")
                            }
                            2 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to cancel a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag("")
                            }
                            3 -> {
                                Toast.makeText(
                                    context,
                                    "I see you're trying to cancel a ${tagList[checkedTag]} tag",
                                    Toast.LENGTH_LONG
                                ).show()
                                sharedViewModel.saveTag("")
                            }
                        }

                    })

            // create the dialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


}


//    arrayList = ArrayList()
//
//    // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
//    // and the array that contains the data
//    adapter = context?.let {
//        ArrayAdapter(
//            it, android.R.layout.simple_spinner_item,
//            arrayList!!
//        )
//    }
//
//    // Here, you set the data in your ListView
//    list?.setAdapter(adapter)
//    btn?.setOnClickListener {
//
//        // this line adds the data of your EditText and puts in your array
//        arrayList!!.add(editTxt!!.text.toString())
//        // next thing you have to do is check if your adapter has changed
//        adapter!!.notifyDataSetChanged()
//    }