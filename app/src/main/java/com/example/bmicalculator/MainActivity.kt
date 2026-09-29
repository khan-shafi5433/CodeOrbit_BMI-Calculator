package com.example.bmicalculator

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText

    private lateinit var etFeet: EditText
    private lateinit var etInches: EditText

    private lateinit var spHeightUnit: Spinner
    private lateinit var spWeightUnit: Spinner

    private lateinit var heightFeetInchesLayout: LinearLayout

    private lateinit var btnCalculate: Button
    private lateinit var btnReset: Button
    private lateinit var btnTheme: ImageButton
    private lateinit var btnClearHistory: Button

    private lateinit var tvBMI: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvBMIMessage: TextView
    private lateinit var tvHealthyRange: TextView

    private lateinit var resultLayout: LinearLayout

    private lateinit var bmiIndicator: View
    private lateinit var tvRangeMin: TextView

    private lateinit var historyContainer: LinearLayout
    private lateinit var tvNoHistory: TextView

    private val preferencesName = "BMI_Settings"

    private val themeKey = "dark_mode"
    private val heightUnitKey = "height_unit"
    private val weightUnitKey = "weight_unit"

    // History storage
    private val historyKey = "bmi_history"

    override fun onCreate(savedInstanceState: Bundle?) {

        val preferences =
            getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

        val darkMode =
            preferences.getBoolean(themeKey, false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // ---------------------------------------------------------
        // FIND VIEWS
        // ---------------------------------------------------------

        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)

        etFeet = findViewById(R.id.etFeet)
        etInches = findViewById(R.id.etInches)

        spHeightUnit = findViewById(R.id.spHeightUnit)
        spWeightUnit = findViewById(R.id.spWeightUnit)

        heightFeetInchesLayout =
            findViewById(R.id.heightFeetInchesLayout)

        btnCalculate = findViewById(R.id.btnCalculate)
        btnReset = findViewById(R.id.btnReset)
        btnTheme = findViewById(R.id.btnTheme)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        tvBMI = findViewById(R.id.tvBMI)
        tvCategory = findViewById(R.id.tvCategory)
        tvBMIMessage = findViewById(R.id.tvBMIMessage)
        tvHealthyRange = findViewById(R.id.tvHealthyRange)

        resultLayout = findViewById(R.id.resultLayout)

        bmiIndicator = findViewById(R.id.bmiIndicator)
        tvRangeMin = findViewById(R.id.tvRangeMin)

        // History views
        historyContainer =
            findViewById(R.id.historyContainer)

        tvNoHistory =
            findViewById(R.id.tvNoHistory)

        // ---------------------------------------------------------
        // HEIGHT SPINNER
        // ---------------------------------------------------------

        val heightUnits = arrayOf(
            "cm",
            "m",
            "ft/in"
        )

        val heightAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            heightUnits
        )

        heightAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spHeightUnit.adapter = heightAdapter

        // ---------------------------------------------------------
        // WEIGHT SPINNER
        // ---------------------------------------------------------

        val weightUnits = arrayOf(
            "kg",
            "lb"
        )

        val weightAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            weightUnits
        )

        weightAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spWeightUnit.adapter = weightAdapter

        // ---------------------------------------------------------
        // RESTORE SAVED UNITS
        // ---------------------------------------------------------

        val savedHeightUnit =
            preferences.getInt(heightUnitKey, 0)

        val savedWeightUnit =
            preferences.getInt(weightUnitKey, 0)

        spHeightUnit.setSelection(savedHeightUnit)
        spWeightUnit.setSelection(savedWeightUnit)

        // ---------------------------------------------------------
        // HEIGHT UNIT SELECTION
        // ---------------------------------------------------------

        spHeightUnit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    preferences.edit()
                        .putInt(heightUnitKey, position)
                        .apply()

                    if (position == 2) {

                        etHeight.visibility =
                            View.GONE

                        heightFeetInchesLayout.visibility =
                            View.VISIBLE

                    } else {

                        etHeight.visibility =
                            View.VISIBLE

                        heightFeetInchesLayout.visibility =
                            View.GONE
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // ---------------------------------------------------------
        // WEIGHT UNIT SELECTION
        // ---------------------------------------------------------

        spWeightUnit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    preferences.edit()
                        .putInt(weightUnitKey, position)
                        .apply()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // ---------------------------------------------------------
        // THEME
        // ---------------------------------------------------------

        updateThemeIcon(darkMode)

        btnTheme.setOnClickListener {

            val currentDarkMode =
                preferences.getBoolean(themeKey, false)

            val newDarkMode =
                !currentDarkMode

            preferences.edit()
                .putBoolean(themeKey, newDarkMode)
                .apply()

            AppCompatDelegate.setDefaultNightMode(
                if (newDarkMode) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        // ---------------------------------------------------------
        // CALCULATE
        // ---------------------------------------------------------

        btnCalculate.setOnClickListener {
            calculateBMI()
        }

        // ---------------------------------------------------------
        // RESET
        // ---------------------------------------------------------

        btnReset.setOnClickListener {
            resetCalculator()
        }

        // ---------------------------------------------------------
        // CLEAR HISTORY
        // ---------------------------------------------------------

        btnClearHistory.setOnClickListener {

            clearHistory()
        }

        // ---------------------------------------------------------
        // LOAD HISTORY
        // ---------------------------------------------------------

        loadHistory()
    }

    // =============================================================
    // THEME ICON
    // =============================================================

    private fun updateThemeIcon(isDarkMode: Boolean) {

        if (isDarkMode) {

            btnTheme.setImageResource(
                R.drawable.ic_sun
            )

            btnTheme.contentDescription =
                "Switch to light theme"

        } else {

            btnTheme.setImageResource(
                R.drawable.ic_moon
            )

            btnTheme.contentDescription =
                "Switch to dark theme"
        }
    }

    // =============================================================
    // BMI CALCULATION
    // =============================================================

    private fun calculateBMI() {

        try {

            val heightUnit =
                spHeightUnit.selectedItem.toString()

            val heightMeters: Double

            // -----------------------------------------------------
            // CM
            // -----------------------------------------------------

            if (heightUnit == "cm") {

                val heightText =
                    etHeight.text.toString().trim()

                if (heightText.isEmpty()) {

                    etHeight.error =
                        "Please enter your height"

                    etHeight.requestFocus()

                    return
                }

                val heightCm =
                    heightText.toDouble()

                if (heightCm < 50 || heightCm > 300) {

                    etHeight.error =
                        "Height must be between 50 and 300 cm"

                    etHeight.requestFocus()

                    return
                }

                heightMeters =
                    heightCm / 100.0
            }

            // -----------------------------------------------------
            // METERS
            // -----------------------------------------------------

            else if (heightUnit == "m") {

                val heightText =
                    etHeight.text.toString().trim()

                if (heightText.isEmpty()) {

                    etHeight.error =
                        "Please enter your height"

                    etHeight.requestFocus()

                    return
                }

                val heightM =
                    heightText.toDouble()

                if (heightM < 0.5 || heightM > 3.0) {

                    etHeight.error =
                        "Height must be between 0.5 and 3.0 m"

                    etHeight.requestFocus()

                    return
                }

                heightMeters =
                    heightM
            }

            // -----------------------------------------------------
            // FEET / INCHES
            // -----------------------------------------------------

            else {

                val feetText =
                    etFeet.text.toString().trim()

                val inchesText =
                    etInches.text.toString().trim()

                if (feetText.isEmpty()) {

                    etFeet.error =
                        "Please enter feet"

                    etFeet.requestFocus()

                    return
                }

                val feet =
                    feetText.toDouble()

                val inches =
                    if (inchesText.isEmpty()) {
                        0.0
                    } else {
                        inchesText.toDouble()
                    }

                if (feet < 1 || feet > 9) {

                    etFeet.error =
                        "Feet must be between 1 and 9"

                    etFeet.requestFocus()

                    return
                }

                if (inches < 0 || inches >= 12) {

                    etInches.error =
                        "Inches must be between 0 and 11.99"

                    etInches.requestFocus()

                    return
                }

                val totalInches =
                    (feet * 12) + inches

                heightMeters =
                    totalInches * 0.0254

                if (heightMeters < 0.5 || heightMeters > 3.0) {

                    Toast.makeText(
                        this,
                        "Height must be between 0.5 and 3.0 m",
                        Toast.LENGTH_SHORT
                    ).show()

                    return
                }
            }

            // -----------------------------------------------------
            // WEIGHT
            // -----------------------------------------------------

            val weightText =
                etWeight.text.toString().trim()

            if (weightText.isEmpty()) {

                etWeight.error =
                    "Please enter your weight"

                etWeight.requestFocus()

                return
            }

            val weightValue =
                weightText.toDouble()

            val weightUnit =
                spWeightUnit.selectedItem.toString()

            if (weightUnit == "kg") {

                if (weightValue < 10 || weightValue > 500) {

                    etWeight.error =
                        "Weight must be between 10 and 500 kg"

                    etWeight.requestFocus()

                    return
                }

            } else {

                if (weightValue < 22 || weightValue > 1100) {

                    etWeight.error =
                        "Weight must be between 22 and 1100 lb"

                    etWeight.requestFocus()

                    return
                }
            }

            // -----------------------------------------------------
            // CONVERT TO KG
            // -----------------------------------------------------

            val weightKg =
                if (weightUnit == "kg") {
                    weightValue
                } else {
                    weightValue * 0.45359237
                }

            // -----------------------------------------------------
            // CALCULATE BMI
            // -----------------------------------------------------

            val bmi =
                weightKg / (heightMeters * heightMeters)

            // -----------------------------------------------------
            // CATEGORY
            // -----------------------------------------------------

            val category = when {

                bmi < 18.5 ->
                    "Underweight"

                bmi < 25.0 ->
                    "Normal"

                bmi < 30.0 ->
                    "Overweight"

                else ->
                    "Obese"
            }

            // -----------------------------------------------------
            // DISPLAY RESULT
            // -----------------------------------------------------

            tvBMI.text =
                String.format(
                    Locale.getDefault(),
                    "%.2f",
                    bmi
                )

            tvCategory.text =
                category

            tvBMIMessage.text =
                when {

                    bmi < 18.5 ->
                        "Your BMI is below the normal range."

                    bmi < 25.0 ->
                        "Your BMI is within the normal range."

                    bmi < 30.0 ->
                        "Your BMI is above the normal range."

                    else ->
                        "Your BMI is in the obese range."
                }

            tvHealthyRange.text =
                "18.5 – 24.9"

            updateBMIIndicator(bmi)

            resultLayout.visibility =
                View.VISIBLE

            // -----------------------------------------------------
            // SAVE TO HISTORY
            // -----------------------------------------------------

            saveHistory(
                bmi = bmi,
                category = category,
                height = getHeightForHistory(),
                heightUnit = heightUnit,
                weight = weightValue,
                weightUnit = weightUnit
            )

        } catch (e: NumberFormatException) {

            Toast.makeText(
                this,
                "Please enter valid numbers",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // =============================================================
    // GET HEIGHT FOR HISTORY
    // =============================================================

    private fun getHeightForHistory(): Double {

        return when (
            spHeightUnit.selectedItem.toString()
        ) {

            "cm" -> {

                etHeight.text
                    .toString()
                    .trim()
                    .toDouble()
            }

            "m" -> {

                etHeight.text
                    .toString()
                    .trim()
                    .toDouble()
            }

            else -> {

                val feet =
                    etFeet.text
                        .toString()
                        .trim()
                        .toDouble()

                val inches =
                    if (
                        etInches.text
                            .toString()
                            .trim()
                            .isEmpty()
                    ) {
                        0.0
                    } else {
                        etInches.text
                            .toString()
                            .trim()
                            .toDouble()
                    }

                feet + (inches / 12.0)
            }
        }
    }

    // =============================================================
    // SAVE HISTORY
    // =============================================================

    private fun saveHistory(
        bmi: Double,
        category: String,
        height: Double,
        heightUnit: String,
        weight: Double,
        weightUnit: String
    ) {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        val existingHistory =
            preferences.getString(
                historyKey,
                "[]"
            )

        val historyArray =
            JSONArray(existingHistory)

        val entry =
            JSONObject()

        entry.put(
            "bmi",
            bmi
        )

        entry.put(
            "category",
            category
        )

        entry.put(
            "height",
            height
        )

        entry.put(
            "heightUnit",
            heightUnit
        )

        entry.put(
            "weight",
            weight
        )

        entry.put(
            "weightUnit",
            weightUnit
        )

        val dateFormat =
            SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
            )

        entry.put(
            "date",
            dateFormat.format(Date())
        )

        // Add newest result at the beginning
        historyArray.put(entry)

        // Keep latest 20 calculations
        val maxHistory = 20

        val trimmedArray =
            JSONArray()

        val startIndex =
            maxOf(
                0,
                historyArray.length() - maxHistory
            )

        for (i in startIndex until historyArray.length()) {

            trimmedArray.put(
                historyArray.getJSONObject(i)
            )
        }

        preferences.edit()
            .putString(
                historyKey,
                trimmedArray.toString()
            )
            .apply()

        loadHistory()
    }

    // =============================================================
    // LOAD HISTORY
    // =============================================================

    private fun loadHistory() {

        historyContainer.removeAllViews()

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        val savedHistory =
            preferences.getString(
                historyKey,
                "[]"
            )

        try {

            val historyArray =
                JSONArray(savedHistory)

            if (historyArray.length() == 0) {

                tvNoHistory.visibility =
                    View.VISIBLE

                return
            }

            tvNoHistory.visibility =
                View.GONE

            // Newest first
            for (
            i in historyArray.length() - 1 downTo 0
            ) {

                val entry =
                    historyArray.getJSONObject(i)

                addHistoryItem(entry)
            }

        } catch (e: Exception) {

            tvNoHistory.visibility =
                View.VISIBLE
        }
    }

    // =============================================================
    // CREATE HISTORY ITEM
    // =============================================================

    private fun addHistoryItem(
        entry: JSONObject
    ) {

        val item =
            LinearLayout(this)

        item.orientation =
            LinearLayout.VERTICAL

        item.setPadding(
            16,
            14,
            16,
            14
        )

        item.setBackgroundResource(
            R.drawable.bg_input
        )

        val itemParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        itemParams.setMargins(
            0,
            0,
            0,
            10
        )

        item.layoutParams =
            itemParams

        // ---------------------------------------------------------
        // TOP ROW
        // ---------------------------------------------------------

        val topRow =
            LinearLayout(this)

        topRow.orientation =
            LinearLayout.HORIZONTAL

        topRow.gravity =
            android.view.Gravity.CENTER_VERTICAL

        // BMI value
        val bmiText =
            TextView(this)

        bmiText.text =
            String.format(
                Locale.getDefault(),
                "%.2f",
                entry.getDouble("bmi")
            )

        bmiText.textSize =
            24f

        bmiText.setTextColor(
            getColor(R.color.primary)
        )

        bmiText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        val bmiParams =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

        bmiText.layoutParams =
            bmiParams

        topRow.addView(
            bmiText
        )

        // Category
        val categoryText =
            TextView(this)

        categoryText.text =
            entry.getString("category")

        categoryText.textSize =
            13f

        categoryText.setTextColor(
            getColor(R.color.primary)
        )

        categoryText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        topRow.addView(
            categoryText
        )

        item.addView(
            topRow
        )

        // ---------------------------------------------------------
        // DETAILS
        // ---------------------------------------------------------

        val detailsText =
            TextView(this)

        val height =
            entry.getDouble("height")

        val weight =
            entry.getDouble("weight")

        val heightUnit =
            entry.getString("heightUnit")

        val weightUnit =
            entry.getString("weightUnit")

        detailsText.text =
            String.format(
                Locale.getDefault(),
                "Height: %.1f %s   •   Weight: %.1f %s",
                height,
                heightUnit,
                weight,
                weightUnit
            )

        detailsText.textSize =
            12f

        detailsText.setTextColor(
            getColor(R.color.text_secondary)
        )

        val detailsParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        detailsParams.topMargin =
            5

        detailsText.layoutParams =
            detailsParams

        item.addView(
            detailsText
        )

        // ---------------------------------------------------------
        // DATE
        // ---------------------------------------------------------

        val dateText =
            TextView(this)

        dateText.text =
            entry.getString("date")

        dateText.textSize =
            11f

        dateText.setTextColor(
            getColor(R.color.text_secondary)
        )

        val dateParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        dateParams.topMargin =
            4

        dateText.layoutParams =
            dateParams

        item.addView(
            dateText
        )

        historyContainer.addView(
            item
        )
    }

    // =============================================================
    // CLEAR HISTORY
    // =============================================================

    private fun clearHistory() {

        val preferences =
            getSharedPreferences(
                preferencesName,
                Context.MODE_PRIVATE
            )

        preferences.edit()
            .remove(historyKey)
            .apply()

        loadHistory()

        Toast.makeText(
            this,
            "BMI history cleared",
            Toast.LENGTH_SHORT
        ).show()
    }

    // =============================================================
    // BMI INDICATOR
    // =============================================================

    private fun updateBMIIndicator(
        bmi: Double
    ) {

        val minimumBMI =
            10.0

        val maximumBMI =
            40.0

        val clampedBMI =
            bmi.coerceIn(
                minimumBMI,
                maximumBMI
            )

        val percentage =
            (
                    (clampedBMI - minimumBMI) /
                            (maximumBMI - minimumBMI)
                    ) * 100.0

        bmiIndicator.post {

            val parent =
                bmiIndicator.parent as View

            val parentWidth =
                parent.width

            if (parentWidth <= 0) {
                return@post
            }

            val indicatorWidth =
                bmiIndicator.width

            val availableWidth =
                parentWidth - indicatorWidth

            val newX =
                (
                        availableWidth *
                                (percentage / 100.0)
                        ).toFloat()

            bmiIndicator.translationX =
                newX
        }

        tvRangeMin.text =
            "10"
    }

    // =============================================================
    // RESET
    // =============================================================

    private fun resetCalculator() {

        etHeight.text.clear()
        etWeight.text.clear()

        etFeet.text.clear()
        etInches.text.clear()

        etHeight.error = null
        etWeight.error = null
        etFeet.error = null
        etInches.error = null

        // Keep user's preferred units
        resultLayout.visibility =
            View.GONE

        bmiIndicator.translationX =
            0f

        tvBMI.text =
            "--"

        tvCategory.text =
            ""

        tvBMIMessage.text =
            ""

        tvHealthyRange.text =
            "18.5 – 24.9"
    }
}