package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val viewModel: TotalViewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    private lateinit var db: TotalDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = prepareDatabase()
        initializeValueFromDatabase()

        prepareViewModel()
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()

            val currentValue = viewModel.total.value ?: 0
            updateTotalValueInDb(currentValue)
        }
    }

    // ========== ROOM DATABASE ==========

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    private fun initializeValueFromDatabase() {
        val list = db.totalDao().getTotal(ID)
        if (list.isEmpty()) {
            val now = Date().toString()
            db.totalDao().insert(
                Total(
                    id = ID,
                    total = TotalObject(value = 0, date = now)
                )
            )
            viewModel.setTotal(0)
        } else {
            val saved = list.first()
            viewModel.setTotal(saved.total.value)
        }
    }

    private fun updateTotalValueInDb(newValue: Int) {
        val current = db.totalDao().getTotal(ID).firstOrNull()
        val currentDate = current?.total?.date ?: ""
        db.totalDao().update(
            Total(
                id = ID,
                total = TotalObject(value = newValue, date = currentDate)
            )
        )
    }

    // ========== LIFECYCLE: DATE & TOAST ==========

    override fun onPause() {
        super.onPause()
        val currentValue = viewModel.total.value ?: 0
        val now = Date().toString()
        db.totalDao().update(
            Total(
                id = ID,
                total = TotalObject(value = currentValue, date = now)
            )
        )
    }

    override fun onStart() {
        super.onStart()
        val current = db.totalDao().getTotal(ID).firstOrNull()
        val dateString = current?.total?.date ?: return
        Toast.makeText(this, dateString, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val ID: Long = 1L
    }
}