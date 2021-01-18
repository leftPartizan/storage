package com.example.storage

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.storage.databinding.ActivityMainBinding
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY = "KEY"
    }

    private val fromDbValue: MutableLiveData<String> = MutableLiveData()

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val editText by lazy {
        binding.editText
    }

    private val file by lazy { File(filesDir, "dataFile.txt") }
    private val db by lazy {
        Room.databaseBuilder(this,MyRoom.StorageDataBase::class.java, "myStorage")
            .fallbackToDestructiveMigration()
            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db.getDataStorage().getData().observe(this, {fromDbValue.value = it} )
        createListeners()
    }

    private fun createListeners() {
        saveToPrefs()
        loadFromPrefs()
        saveToFile()
        loadFromFile()
        saveToDb()
        loadFromDb()
        saveToExternal()
        loadFromExternal()
    }

    private fun saveToPrefs() {
        binding.saveToPrefs.setOnClickListener{
            getPreferences(MODE_PRIVATE).
            edit().
            putString(KEY, "${editText.text}").
            apply()
            editText.text.clear()
        }
    }

    private fun loadFromPrefs() {
        binding.loadFromPrefs.setOnClickListener{
            editText.append(getPreferences(MODE_PRIVATE).getString(KEY, "null"))
        }
    }

    private fun saveToFile() {
        binding.saveToInternal.setOnClickListener {
            try {
                val output = file.outputStream()
                output.write(editText.text.toString().toByteArray())
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            editText.text.clear()
        }
    }

    private fun loadFromFile() {
        binding.loadFromInternal.setOnClickListener {
            try {
                val input = file.inputStream()
                editText.append(input.readBytes().decodeToString())
                input.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveToDb() {
        binding.saveToDB.setOnClickListener {
            thread {
                db.getDataStorage().saveData("${editText.text}")
                editText.text.clear()
            }
        }
    }

    private fun loadFromDb() {
        binding.loadFromDB.setOnClickListener {
            editText.append(fromDbValue.value)
        }
    }

    private fun saveToExternal() {
        binding.saveToExternal.setOnClickListener{
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                try {
                    val file = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "dataStorage")
                            .outputStream()
                    file.write(editText.text.toString().toByteArray())
                    editText.text.clear()
                    file.close()
                }
                catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadFromExternal() {
        binding.loadFromExternal.setOnClickListener{
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                Log.i("www", "yes")
                try {
                    val file = File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "dataStorage").inputStream()
                    editText.append(file.readBytes().decodeToString())
                    file.close()
                }
                catch (e : Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}