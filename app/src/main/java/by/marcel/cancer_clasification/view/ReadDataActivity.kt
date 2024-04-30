package by.marcel.cancer_clasification.view

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import by.marcel.cancer_clasification.R
import by.marcel.cancer_clasification.adapter.HistoryAdapter
import by.marcel.cancer_clasification.databinding.ActivityReadDataBinding
import by.marcel.cancer_clasification.helper.SQLiteHelper

class ReadDataActivity : AppCompatActivity() {
    private lateinit var Readbinding: ActivityReadDataBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var sqliteHelper: SQLiteHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Readbinding = ActivityReadDataBinding.inflate(layoutInflater)
        setContentView(Readbinding.root)

        sqliteHelper = SQLiteHelper(this)

        setupRecyclerView()
        loadData()
    }
    private fun setupRecyclerView() {
        val cursor: Cursor? = sqliteHelper.getAllPredictions()
        historyAdapter = HistoryAdapter(sqliteHelper, cursor)
        Readbinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReadDataActivity)
            adapter = historyAdapter
        }
    }

    private fun loadData() {
        val cursor: Cursor? = sqliteHelper.getAllPredictions()
        historyAdapter.swapCursor(cursor)
    }
}