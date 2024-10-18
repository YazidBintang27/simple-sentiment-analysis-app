package com.latihan.sentimenanalysisapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.utilities.Score.score
import com.google.mediapipe.tasks.components.containers.Classifications
import com.latihan.sentimenanalysisapp.databinding.ActivityMainBinding
import com.latihan.sentimenanalysisapp.helper.TextClassifierHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

   private lateinit var binding: ActivityMainBinding

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)
      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
         v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
         insets
      }
      classify()
   }

   private fun classify() {
      val textClassfierHelper = TextClassifierHelper(
         context = this,
         classifierListener = object: TextClassifierHelper.ClassifierListener {
            override fun onError(error: String) {
               Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: MutableList<Classifications>?, inferenceTime: Long) {
               lifecycleScope.launch(Dispatchers.Main) {
                  results?.let { result ->
                     if (result.isNotEmpty() && result[0].categories().isNotEmpty()) {
                        println(result)

                        val sortedCategories = result[0].categories().sortedByDescending { it?.score() }

                        val displayResult =
                           sortedCategories.joinToString("\n") {
                              "${it.categoryName()} " + NumberFormat.getPercentInstance()
                                 .format(it.score()).trim()
                           }
                        binding.tvResult.text = displayResult
                     } else {
                        binding.tvResult.text = ""
                     }
                  }
               }
            }
         }
      )
      binding.btnClassify.setOnClickListener {
         val inputText = binding.edInput.text.toString()
         textClassfierHelper.classify(inputText)
      }
   }
}