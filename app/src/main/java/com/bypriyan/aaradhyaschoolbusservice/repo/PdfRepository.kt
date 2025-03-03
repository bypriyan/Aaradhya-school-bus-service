package com.bypriyan.aaradhyaschoolbusservice.repo

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class PdfRepository @Inject constructor(private val context: Context) {
    suspend fun createPdf(
        receiptNo: String,
        date: String,
        studentName: String,
        address: String,
        mobileNo: String,
        amount: String,
        std: String,
        totalFees: String,
        monthFrom: String,
        monthTo: String
    ): String {
        return withContext(Dispatchers.IO) {
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val borderPaint = Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
            }
            val pageInfo = PdfDocument.PageInfo.Builder(600, 800, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("Aaradhya School Bus Services", 120f, 50f, paint)

            paint.textSize = 16f
            paint.isFakeBoldText = true
            canvas.drawText("BUS RECEIPT", 250f, 80f, paint)

            paint.textSize = 12f
            paint.isFakeBoldText = false

            // Receipt No. and Date with separate outlines
            canvas.drawRect(40f, 100f, 280f, 130f, borderPaint)
            canvas.drawRect(320f, 100f, 560f, 130f, borderPaint)
            canvas.drawText("Receipt No: $receiptNo", 50f, 120f, paint)
            canvas.drawText("Date: $date", 330f, 120f, paint)

            // Student details with outline
            canvas.drawRect(40f, 140f, 560f, 210f, borderPaint)
            canvas.drawText("Student Name: $studentName", 50f, 160f, paint)
            canvas.drawText("Address: $address", 50f, 180f, paint)
            canvas.drawText("Mobile No: $mobileNo", 50f, 200f, paint)

            // Amount section
            canvas.drawRect(40f, 220f, 560f, 250f, borderPaint)
            canvas.drawText("Rupees in Words: ${convertNumberToWords(amount)}", 50f, 240f, paint)

            // Standard and Total Fees with outline
            canvas.drawRect(40f, 260f, 560f, 290f, borderPaint)
            canvas.drawText("Std: $std", 50f, 280f, paint)
            canvas.drawText("Total Fees Year: $totalFees", 400f, 280f, paint)

            // Months section
            canvas.drawRect(40f, 300f, 560f, 330f, borderPaint)
            canvas.drawText("Month of: $monthFrom to $monthTo", 50f, 320f, paint)

            // Fees Received (No outline)
            paint.isFakeBoldText = true
            canvas.drawText("Fees Received. Thank you!", 200f, 370f, paint)

            // Amount section
            canvas.drawRect(40f, 390f, 560f, 420f, borderPaint)
            paint.isFakeBoldText = false
            canvas.drawText("Rupees: $amount", 50f, 410f, paint)

            // Receiver Signature (No outline, with margin)
            canvas.drawText("Receiver Signature", 400f, 470f, paint)

            pdfDocument.finishPage(page)
            val filePath = savePdf(pdfDocument)
            pdfDocument.close()
            return@withContext filePath
        }
    }

    fun convertNumberToWords(number: String): String {
        val num = number.toLongOrNull() ?: return "Invalid number"

        if (num == 0L) return "zero"

        val units = arrayOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
        val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")
        val scales = arrayOf("", "thousand", "lakh", "crore")

        fun convertChunk(n: Int): String {
            return when {
                n < 20 -> units[n]
                n < 100 -> tens[n / 10] + if (n % 10 != 0) " " + units[n % 10] else ""
                else -> units[n / 100] + " hundred" + if (n % 100 != 0) " " + convertChunk(n % 100) else ""
            }
        }

        val parts = arrayOf(num % 1000, (num / 1000) % 100, (num / 100000) % 100, (num / 10000000) % 100)  // Supports up to crores
        val wordParts = mutableListOf<String>()

        for (i in parts.indices.reversed()) {
            if (parts[i] > 0) {
                wordParts.add(convertChunk(parts[i].toInt()) + " " + scales[i])
            }
        }

        return wordParts.joinToString(" ").trim()
    }

    private fun savePdf(pdfDocument: PdfDocument): String {
        val fileName = "BusReceipt_${System.currentTimeMillis()}.pdf"
        val outputStream: OutputStream?
        val filePath: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val contentResolver = context.contentResolver
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = uri?.let { contentResolver.openOutputStream(it) }
            filePath = uri.toString()
        } else {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
            outputStream = FileOutputStream(file)
            filePath = file.absolutePath
        }
        outputStream?.use { pdfDocument.writeTo(it) }
        return filePath
    }
}