package com.simats.ovadrugx

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.app.Activity
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log
import android.widget.Toast
import com.simats.ovadrugx.api.RetrofitClient
import com.simats.ovadrugx.model.PredictTargetsRequest
import com.simats.ovadrugx.model.PredictTargetsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadReportActivity : AppCompatActivity() {

    private var selectedFileType: String = ""

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileName(uri)
                updateFileText(fileName)
                
                // Set the preview image
                val imageViewId = when (selectedFileType) {
                    "mirna" -> R.id.iv_preview_mirna
                    "proteins" -> R.id.iv_preview_proteins
                    "compounds" -> R.id.iv_preview_compounds
                    else -> null
                }
                
                imageViewId?.let { id ->
                    val iv = findViewById<android.widget.ImageView>(id)
                    var previewSuccess = false

                    try {
                        val mimeType = contentResolver.getType(uri)
                        if (mimeType?.startsWith("image/") == true) {
                            val bitmap = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            iv.setImageBitmap(bitmap)
                            previewSuccess = true
                        } else {
                            val bitmap = renderPdfPreview(uri)
                            if (bitmap != null) {
                                iv.setImageBitmap(bitmap)
                                previewSuccess = true
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("UploadReportActivity", "Error loading preview: ${e.message}")
                    }

                    if (previewSuccess) {
                        iv.visibility = android.view.View.VISIBLE
                    } else {
                        Toast.makeText(this@UploadReportActivity, "Could not generate preview", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_report)
        
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<android.view.View>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
        
        setupBottomNavigation()
        setupUploadButtons()
        setupSearch()
    }

    private fun setupSearch() {
        val etSearch = findViewById<android.widget.EditText>(R.id.et_gene_search)
        findViewById<android.view.View>(R.id.btn_submit_report).setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchGenePredictions(query)
            } else {
                Toast.makeText(this, "Please enter a gene name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchGenePredictions(geneName: String) {
        val request = PredictTargetsRequest(geneName)
        RetrofitClient.instance.predictTargets(request).enqueue(object : Callback<PredictTargetsResponse> {
            override fun onResponse(call: Call<PredictTargetsResponse>, response: Response<PredictTargetsResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    if (body.status == "success" && body.data != null) {
                        launchTargetDiscoveryWithData(body.data)
                    } else {
                        Toast.makeText(this@UploadReportActivity, body.message ?: "Gene not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UploadReportActivity, "Failed to analyze gene", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PredictTargetsResponse>, t: Throwable) {
                Toast.makeText(this@UploadReportActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun launchTargetDiscoveryWithData(data: com.simats.ovadrugx.model.PredictTargetsData) {
        // Phase 4 Preparation: Commit the top ML data to Global Memory so the Home Page graph updates!
        val sharedPrefs = getSharedPreferences("ML_GLOBAL_STATE", android.content.Context.MODE_PRIVATE)
        val currentGenes = sharedPrefs.getString("HISTORY_GENES", "") ?: ""
        val currentScores = sharedPrefs.getString("HISTORY_SCORES", "") ?: ""
        val currentDates = sharedPrefs.getString("HISTORY_DATES", "") ?: ""
        
        val newGene = data.inputGene ?: data.matchedGene ?: "Unknown Gene"
        val newScore = (data.confidenceScore ?: 0.0).toFloat()
        val newDate = System.currentTimeMillis().toString()
        
        val geneList = currentGenes.split(",").filter { it.isNotEmpty() }.toMutableList()
        val scoreList = currentScores.split(",").filter { it.isNotEmpty() }.toMutableList()
        val dateList = currentDates.split(",").filter { it.isNotEmpty() }.toMutableList()
        
        val existingIndex = geneList.indexOf(newGene)
        if (existingIndex != -1) {
            val existingScore = scoreList.getOrNull(existingIndex)?.toFloatOrNull() ?: 0f
            if (newScore > existingScore) {
                geneList.removeAt(existingIndex)
                if (existingIndex < scoreList.size) scoreList.removeAt(existingIndex)
                if (existingIndex < dateList.size) dateList.removeAt(existingIndex)

                geneList.add(0, newGene)
                scoreList.add(0, newScore.toString())
                dateList.add(0, newDate)
            }
        } else {
            geneList.add(0, newGene)
            scoreList.add(0, newScore.toString())
            dateList.add(0, newDate)
        }
        
        while (geneList.size > 5) {
            geneList.removeAt(geneList.size - 1)
            if (scoreList.size > 5) scoreList.removeAt(scoreList.size - 1)
            if (dateList.size > 5) dateList.removeAt(dateList.size - 1)
        }
        
        sharedPrefs.edit().apply {
            putString("HISTORY_GENES", geneList.joinToString(","))
            putString("HISTORY_SCORES", scoreList.joinToString(","))
            putString("HISTORY_DATES", dateList.joinToString(","))
            
            // Legacy fallbacks
            putString("LATEST_GENE", newGene)
            putFloat("LATEST_CONFIDENCE", newScore)
            apply()
        }

        val intent = Intent(this, TargetDiscoveryActivity::class.java)
        intent.putExtra("ML_INPUT_GENE", data.inputGene)
        intent.putExtra("ML_MATCHED_GENE", data.matchedGene)
        intent.putExtra("ML_ORGANISM", data.organism)
        intent.putExtra("ML_CONFIDENCE_SCORE", data.confidenceScore ?: 0.0)
        
        val targetsList = ArrayList(data.highConfidenceTargets ?: emptyList())
        intent.putStringArrayListExtra("ML_TARGETS_LIST", targetsList)
        
        startActivity(intent)
    }

    private fun setupUploadButtons() {
        findViewById<android.view.View>(R.id.btn_upload_mirna).setOnClickListener {
            selectedFileType = "mirna"
            openFilePicker()
        }

        findViewById<android.view.View>(R.id.btn_upload_proteins).setOnClickListener {
            selectedFileType = "proteins"
            openFilePicker()
        }

        findViewById<android.view.View>(R.id.btn_upload_compounds).setOnClickListener {
            selectedFileType = "compounds"
            openFilePicker()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            val mimeTypes = arrayOf("application/pdf", "image/*")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private fun updateFileText(fileName: String) {
        val textViewId = when (selectedFileType) {
            "mirna" -> R.id.tv_file_mirna
            "proteins" -> R.id.tv_file_proteins
            "compounds" -> R.id.tv_file_compounds
            else -> return
        }
        findViewById<TextView>(textViewId).text = fileName
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it != null && it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "file"
    }

    private fun setupBottomNavigation() {
        findViewById<android.view.View>(R.id.nav_home).setOnClickListener {
             startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_screening).setOnClickListener {
             startActivity(Intent(this, ScreeningActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_targets).setOnClickListener {
             startActivity(Intent(this, TargetDiscoveryActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_insights).setOnClickListener {
             startActivity(Intent(this, AiInsightsActivity::class.java))
        }
        findViewById<android.view.View>(R.id.nav_profile).setOnClickListener {
             startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun renderPdfPreview(uri: Uri): android.graphics.Bitmap? {
        return try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            parcelFileDescriptor?.let { pfd ->
                val pdfRenderer = android.graphics.pdf.PdfRenderer(pfd)
                if (pdfRenderer.pageCount > 0) {
                    val page = pdfRenderer.openPage(0)
                    val bitmap = android.graphics.Bitmap.createBitmap(
                        page.width,
                        page.height,
                        android.graphics.Bitmap.Config.ARGB_8888
                    )
                    // Render on a white background (PDFs often have transparent backgrounds)
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    
                    page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    pdfRenderer.close()
                    pfd.close()
                    bitmap
                } else null
            }
        } catch (e: Exception) {
            Log.e("UploadReportActivity", "Error rendering PDF preview: ${e.message}")
            null
        }
    }
}
