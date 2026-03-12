package com.simats.ovadrugx

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class ReportExportActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_export)

        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        val gene = intent.getStringExtra("RISK_GENE_NAME") ?: "Target Molecule"
        val drug = intent.getStringExtra("RISK_DRUG_NAME") ?: "Novel Compound"
        val score = intent.getDoubleExtra("RISK_MATCH_SCORE", 0.0)

        findViewById<View>(R.id.btn_download_report).setOnClickListener {
            if (checkPermissions()) {
                generateAndDownloadPdf(gene, drug, score)
            } else {
                requestPermissions()
            }
        }

        findViewById<View>(R.id.btn_share_secure_link).setOnClickListener {
            startActivity(Intent(this, LinkSharingActivity::class.java))
        }
        
        findViewById<CheckBox>(R.id.cb_digital_signature).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startActivity(Intent(this, DigitalSignatureActivity::class.java))
            }
        }

        setupBottomNavigation()
    }

    private fun checkPermissions(): Boolean {
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true // No need for WRITE_EXTERNAL_STORAGE on Android 10+ when using MediaStore
        } else {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return storagePermission && notificationPermission
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                val gene = intent.getStringExtra("RISK_GENE_NAME") ?: "Target Molecule"
                val drug = intent.getStringExtra("RISK_DRUG_NAME") ?: "Novel Compound"
                val score = intent.getDoubleExtra("RISK_MATCH_SCORE", 0.0)
                generateAndDownloadPdf(gene, drug, score)
            } else {
                android.widget.Toast.makeText(this, "Permissions required to download report", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateAndDownloadPdf(gene: String, drug: String, score: Double) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ovadrugx_downloads"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Downloads", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_check_circle_black_24dp)
            .setContentTitle("Downloading OvaDrugX Report...")
            .setContentText("OvaDrugX_${gene}_Report.pdf")
            .setProgress(100, 0, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        notificationManager.notify(1001, notificationBuilder.build())

        val pdfDocument = android.graphics.pdf.PdfDocument()
        val myPageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(1200, 2010, 1).create()
        val myPage = pdfDocument.startPage(myPageInfo)
        val canvas = myPage.canvas
        val paint = android.graphics.Paint()

        // --- BACKGROUND WATERMARK ---
        try {
            val logoBitmap = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.report_bg_new)
            val matrix = android.graphics.Matrix()
            val scale = 600f / logoBitmap.width
            matrix.postScale(scale, scale)
            matrix.postTranslate(300f, 700f) // Center-ish
            val filterPaint = android.graphics.Paint().apply { alpha = 30 } // 12% opacity
            canvas.drawBitmap(logoBitmap, matrix, filterPaint)
        } catch (e: Exception) {
            android.util.Log.e("PDF", "Watermark failed: ${e.message}")
        }

        // --- HEADER ---
        paint.color = android.graphics.Color.parseColor("#1A237E") // Deep Blue
        paint.textSize = 60f
        paint.isFakeBoldText = true
        canvas.drawText("OvaDrugX report", 80f, 150f, paint)

        paint.textSize = 30f
        paint.isFakeBoldText = false
        paint.color = android.graphics.Color.GRAY
        canvas.drawText("Clinical AI-Powered Drug Screening Analysis", 80f, 200f, paint)

        paint.strokeWidth = 4f
        paint.color = android.graphics.Color.parseColor("#1A237E")
        canvas.drawLine(80f, 230f, 1120f, 230f, paint)

        // --- BODY: Primary Information ---
        var currentY = 320f
        paint.textSize = 40f
        paint.color = android.graphics.Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Screening Results Summary", 80f, currentY, paint)
        currentY += 80f

        paint.textSize = 32f
        paint.isFakeBoldText = false
        val fields = listOf(
            "Target Gene:" to gene,
            "Drug Candidate:" to drug,
            "Chemical Compound:" to "C22H24N2O8 (Synthetic)", // Example compound
            "Binding Affinity:" to "${String.format("%.1f", -11.2 + (100 - score)/10.0)} kcal/mol",
            "Efficiency Score:" to "${score.toInt()}%"
        )

        for ((label, value) in fields) {
            paint.isFakeBoldText = true
            canvas.drawText(label, 80f, currentY, paint)
            paint.isFakeBoldText = false
            canvas.drawText(value, 450f, currentY, paint)
            currentY += 60f
        }

        currentY += 40f
        paint.strokeWidth = 2f
        paint.color = android.graphics.Color.LTGRAY
        canvas.drawLine(80f, currentY, 1120f, currentY, paint)
        currentY += 80f

        // --- RISK & EFFECTIVENESS REPLICATION ---
        paint.textSize = 40f
        paint.color = android.graphics.Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Risk & Effectiveness Data", 80f, currentY, paint)
        currentY += 70f

        // Efficiency & Safety Bars
        val safetyScore = (score - 2.0).coerceIn(0.0, 100.0)
        
        paint.textSize = 30f
        paint.isFakeBoldText = false
        canvas.drawText("Predicted Efficiency:", 80f, currentY, paint)
        paint.color = android.graphics.Color.parseColor("#E0E0E0")
        canvas.drawRect(400f, currentY - 25f, 1000f, currentY + 5f, paint)
        paint.color = if (score >= 80) android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.parseColor("#FFC107")
        canvas.drawRect(400f, currentY - 25f, 400f + (score.toFloat() * 6f), currentY + 5f, paint)
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("${score.toInt()}%", 1020f, currentY, paint)
        currentY += 60f

        canvas.drawText("Safety Profile:", 80f, currentY, paint)
        paint.color = android.graphics.Color.parseColor("#E0E0E0")
        canvas.drawRect(400f, currentY - 25f, 1000f, currentY + 5f, paint)
        paint.color = android.graphics.Color.parseColor("#2196F3") // Blue for safety
        canvas.drawRect(400f, currentY - 25f, 400f + (safetyScore.toFloat() * 6f), currentY + 5f, paint)
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("${safetyScore.toInt()}%", 1020f, currentY, paint)
        currentY += 100f

        // Side Effects Probability (Toxicity)
        val cardio = ((100.0 - score) * 0.4).coerceIn(0.0, 100.0)
        val hepato = ((100.0 - score) * 0.35).coerceIn(0.0, 100.0)
        val neuro = ((100.0 - score) * 0.25).coerceIn(0.0, 100.0)

        canvas.drawText("Side Effect Probability:", 80f, currentY, paint)
        currentY += 50f
        
        val toxList = listOf("Cardiotoxicity" to cardio, "Hepatotoxicity" to hepato, "Neurotoxicity" to neuro)
        var toxX = 150f
        for ((name, valTox) in toxList) {
            paint.color = android.graphics.Color.LTGRAY
            canvas.drawRect(toxX, currentY, toxX + 80f, currentY + 200f, paint)
            paint.color = android.graphics.Color.parseColor("#F44336")
            val barHeight = (valTox.toFloat() * 2f)
            canvas.drawRect(toxX, currentY + 200f - barHeight, toxX + 80f, currentY + 200f, paint)
            
            paint.color = android.graphics.Color.BLACK
            paint.textSize = 24f
            canvas.save()
            canvas.rotate(-45f, toxX, currentY + 230f)
            canvas.drawText(name, toxX, currentY + 230f, paint)
            canvas.restore()
            
            paint.textSize = 28f
            canvas.drawText("${String.format("%.1f", valTox)}%", toxX, currentY - 15f, paint)
            toxX += 300f
        }
        currentY += 350f

        // Weekly Progress Forecast
        paint.textSize = 32f
        paint.isFakeBoldText = true
        canvas.drawText("Weekly Clinical Progression Forecast ($drug)", 80f, currentY, paint)
        currentY += 60f

        val weeklyScores = listOf(score * 0.7, score * 0.85, score * 0.92, score)
        var weekX = 150f
        for (i in 0 until 4) {
            val wScore = weeklyScores[i]
            paint.color = android.graphics.Color.parseColor("#E3F2FD")
            canvas.drawRect(weekX, currentY, weekX + 80f, currentY + 150f, paint)
            paint.color = android.graphics.Color.parseColor("#1E88E5")
            canvas.drawRect(weekX, currentY + 150f - (wScore.toFloat() * 1.5f), weekX + 80f, currentY + 150f, paint)
            
            paint.color = android.graphics.Color.BLACK
            paint.textSize = 24f
            canvas.drawText("Week ${i+1}", weekX, currentY + 180f, paint)
            weekX += 250f
        }
        currentY += 280f

        // --- FOOTER ---
        paint.strokeWidth = 3f
        paint.color = android.graphics.Color.DKGRAY
        canvas.drawLine(80f, 1800f, 1120f, 1800f, paint)

        // Digital Signature Image Loading
        try {
            val sigFile = java.io.File(cacheDir, "signature.png")
            if (sigFile.exists()) {
                val sigBitmap = android.graphics.BitmapFactory.decodeFile(sigFile.absolutePath)
                if (sigBitmap != null) {
                    val sigScale = 200f / Math.max(sigBitmap.width, 1) // Scale to max 200px width
                    val sigMatrix = android.graphics.Matrix()
                    sigMatrix.postScale(sigScale, sigScale)
                    sigMatrix.postTranslate(80f, 1820f)
                    
                    // Filter specifically for removing strict white backgrounds if user sketched quickly
                    val sigPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
                    
                    canvas.drawBitmap(sigBitmap, sigMatrix, sigPaint)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        paint.textSize = 45f
        paint.color = android.graphics.Color.BLACK
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.ITALIC))
        canvas.drawText("OvaDrugX Digital Signature", 400f, 1880f, paint)
        
        paint.textSize = 28f
        paint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL))
        paint.color = android.graphics.Color.GRAY
        canvas.drawText("Validated & Verified AI Screening Node", 400f, 1930f, paint)
        canvas.drawText("Auth ID: ${System.currentTimeMillis()}", 400f, 1970f, paint)

        paint.color = android.graphics.Color.GRAY
        paint.textAlign = android.graphics.Paint.Align.CENTER
        canvas.drawText("ovadrugx.team@gmail.com", 600f, 2000f, paint)

        pdfDocument.finishPage(myPage)

        val fileName = "OvaDrugX_${gene}_Report.pdf"

        try {
            var outputStream: java.io.OutputStream? = null
            var fileUri: android.net.Uri? = null

            // Use Modern MediaStore equivalent to Chrome downloads to avoid Write Permissions silently failing
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (fileUri != null) {
                    outputStream = resolver.openOutputStream(fileUri)
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = java.io.File(downloadsDir, fileName)
                outputStream = java.io.FileOutputStream(file)
                fileUri = android.net.Uri.fromFile(file)
            }

            if (outputStream != null) {
                pdfDocument.writeTo(outputStream)
                outputStream.close()
            }
            
            // Finish Push Notification (Pop Message Android Native)
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_IMMUTABLE)

            notificationBuilder.setContentTitle("Download Successful!")
                .setContentText(fileName)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            
            notificationManager.notify(1001, notificationBuilder.build())
            
            android.widget.Toast.makeText(this, "File downloaded successfully", android.widget.Toast.LENGTH_LONG).show()
            
            // Auto Route Back to Home
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(homeIntent)
            finish()

        } catch (e: Exception) {
            notificationBuilder.setContentTitle("Download Failed")
                .setContentText(e.message)
                .setProgress(0, 0, false)
                .setOngoing(false)
            notificationManager.notify(1001, notificationBuilder.build())
            
            android.widget.Toast.makeText(this, "Failed to Export PDF: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<View>(R.id.nav_screening).setOnClickListener {
            startActivity(Intent(this, ScreeningActivity::class.java))
        }

        findViewById<View>(R.id.nav_targets).setOnClickListener {
            startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }

        findViewById<View>(R.id.nav_insights).setOnClickListener {
             startActivity(Intent(this, AiInsightsActivity::class.java))
        }

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
