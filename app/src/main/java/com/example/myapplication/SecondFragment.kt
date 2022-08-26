package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.annotation.DBField
import com.example.myapplication.annotation.DBTable
import com.example.myapplication.database.MusicTable
import com.example.myapplication.database.UserDB
import com.example.myapplication.databinding.FragmentSecondBinding
import com.example.myapplication.ui.TextViewHolder

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var dbFields : List<DBField> = ArrayList()
    private val tableClasses = listOf(MusicTable::class.java, UserDB::class.java)
    private var curTableIdx = 0

    private val adapter = object: RecyclerView.Adapter<TextViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
            return TextViewHolder(TextView(context))
        }

        override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
            (holder.itemView as TextView).text = "${dbFields[position].fieldName}-${dbFields[position].fieldType}"
        }

        override fun getItemCount(): Int {
            return dbFields.size
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        DBService.getInstance()?.getTableManager("user")
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.dataItemList.layoutManager = linearLayoutManager

        binding.dataItemList.adapter = adapter

        binding.nextBtn.setOnClickListener {
            curTableIdx = (curTableIdx - 1 + tableClasses.size) % tableClasses.size
            dbUpdated()
        }

        binding.prevBtn.setOnClickListener {
            curTableIdx = (curTableIdx + 1) % tableClasses.size
            dbUpdated()
        }

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @UiThread
    fun dbUpdated() {
        val clazz = tableClasses[curTableIdx]
        clazz.getAnnotation(DBTable::class.java)?.tableName.let {
            binding.curTable.text = it
        }
        dbFields = clazz.declaredFields.mapNotNull {
            it.getAnnotation(DBField::class.java)
        }
        adapter.notifyDataSetChanged()
    }
}