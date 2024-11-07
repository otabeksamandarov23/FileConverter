package com.example.fileconverter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arthenica.ffmpegkit.FFmpegKit
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var selectedFileUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Проверка и запрос разрешений на чтение/запись файлов
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

        // Кнопка для выбора файла
        val selectFileButton = findViewById<Button>(R.id.selectFileButton)
        selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"  // Выбор всех типов файлов
            startActivityForResult(intent, 100)
        }

        // Список форматов
        val formatSpinner = findViewById<Spinner>(R.id.formatSpinner)
        val formats = arrayOf("DOCX", "JPG", "MP4")  // Пример форматов
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, formats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        formatSpinner.adapter = adapter

        // Кнопка для конвертации
        val convertButton = findViewById<Button>(R.id.convertButton)
        convertButton.setOnClickListener {
            val selectedFormat = formatSpinner.selectedItem.toString()
            Toast.makeText(this, "Конвертирование в формат $selectedFormat", Toast.LENGTH_SHORT).show()
            convertFile(selectedFormat)
        }

        // Прогрессбар
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE  // Скрыть прогрессбар по умолчанию
    }

    // Обработка результата выбора файла
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedFileUri = data?.data!!
            Toast.makeText(this, "Выбран файл: $selectedFileUri", Toast.LENGTH_SHORT).show()

            // Включаем кнопку конвертации, когда файл выбран
            val convertButton = findViewById<Button>(R.id.convertButton)
            convertButton.isEnabled = true
        }
    }

    // Функция конвертации файла
    private fun convertFile(format: String) {
        try {
            when (format) {
                "DOCX" -> {
                    convertToDocx()
                }
                "JPG" -> {
                    convertToJpg()
                }
                "MP4" -> {
                    convertToMp4()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при конвертации в $format", Toast.LENGTH_SHORT).show()
        }
    }

    // Конвертация в DOCX с использованием Apache POI
    private fun convertToDocx() {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(selectedFileUri)
            val document = XWPFDocument(inputStream)
            val outputDir = File(cacheDir, "converted_files")
            if (!outputDir.exists()) outputDir.mkdirs()
            val outputFile = File(outputDir, "converted_document.docx")
            val fos = FileOutputStream(outputFile)
            document.write(fos)
            fos.close()
            Toast.makeText(this, "Файл сохранен как DOCX: ${outputFile.path}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Конвертация в JPG
    private fun convertToJpg() {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(selectedFileUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputDir = File(cacheDir, "converted_files")
            if (!outputDir.exists()) outputDir.mkdirs()
            val outputFile = File(outputDir, "converted_image.jpg")
            val fos = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            Toast.makeText(this, "Файл сохранен как JPG: ${outputFile.path}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Конвертация в MP4 с использованием FFmpegKit
    private fun convertToMp4() {
        try {
            val outputDir = File(cacheDir, "converted_files")
            if (!outputDir.exists()) outputDir.mkdirs()
            val outputFile = File(outputDir, "converted_video.mp4")
            val command = "-i ${selectedFileUri.path} -c:v libx264 -preset ultrafast ${outputFile.absolutePath}"

            FFmpegKit.execute(command) { session ->
                val returnCode = session.returnCode
                if (returnCode.isValueSuccess) {
                    Toast.makeText(this, "Файл сохранен как MP4: ${outputFile.path}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Ошибка конвертации видео", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Обработка разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Разрешение получено!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
