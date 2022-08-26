package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.database.MusicTable
import com.example.myapplication.databinding.FragmentFirstBinding
import com.example.myapplication.ui.TextViewHolder
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var musicTableManager: TableManager<*>
    private val musics = ArrayList<MusicTable>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initDataFromDatabase() {
        if (!musicTableManager.success()) {
            return
        }
        val query = musicTableManager.query("")
        while (query.moveToNext()) {
            val item = MusicTable()
            item.a = query.getInt(query.getColumnIndex("a"))
            musics.add(item)
        }
        query.close()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        musicTableManager = DBService.getInstance("db")?.getTableManager("music")!!
        initDataFromDatabase()
        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        val adapter = object: RecyclerView.Adapter<TextViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
                return TextViewHolder(TextView(context))
            }

            override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
                (holder.itemView as TextView).text = musics[position].a.toString()
            }

            override fun getItemCount(): Int {
                return musics.size
            }
        }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.dataItemList.layoutManager = linearLayoutManager

        binding.dataItemList.adapter = adapter
        binding.createBtn.setOnClickListener {
            val data = MusicTable()
            data.a = Random(System.currentTimeMillis()).nextInt()
            musics.add(data)
            adapter.notifyItemInserted(musics.size)
            if (musicTableManager.success()) {
                musicTableManager.insert(data.intoContentValues())
            }
        }

        binding.delBtn.setOnClickListener {
            val music = musics.last()
            val idx = musics.lastIndex
            musics.removeLast()
            adapter.notifyItemRemoved(idx)
            if (musicTableManager.success()) {
                musicTableManager.delete("a = ?", arrayOf(music.a.toString()))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}