package com.example.lab7tasks

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import kotlin.random.Random

class MainActivity : Activity() {
    private val fileRequestCode = 100
    private val months = arrayOf(
        "Січень", "Лютий", "Березень", "Квітень", "Травень", "Червень",
        "Липень", "Серпень", "Вересень", "Жовтень", "Листопад", "Грудень"
    )

    private val holidays = arrayOf(
        arrayOf("1 січня - Новий рік", "7 січня - Різдво Христове"),
        arrayOf("14 лютого - День святого Валентина"),
        arrayOf("8 березня - Міжнародний жіночий день"),
        arrayOf("1 квітня - День сміху"),
        arrayOf("1 травня - День праці", "8 травня - День пам'яті та перемоги"),
        arrayOf("28 червня - День Конституції України"),
        arrayOf("15 липня - День Української Державності"),
        arrayOf("24 серпня - День Незалежності України"),
        arrayOf("1 вересня - День знань"),
        arrayOf("1 жовтня - День захисників і захисниць України"),
        arrayOf("21 листопада - День Гідності та Свободи"),
        arrayOf("6 грудня - День Збройних Сил України", "25 грудня - Різдво Христове")
    )

    private lateinit var holidayResult: TextView
    private lateinit var gameResult: TextView
    private lateinit var cardButtons: Array<Button>
    private lateinit var selectedFileText: TextView
    private lateinit var fileResult: TextView

    private var winningCard = 1
    private var gameFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(dp(16), dp(16), dp(16), dp(16))
        scrollView.addView(root)

        root.addView(createTitle("Лабораторна робота 7"))
        addHolidayTask(root)
        addCardGameTask(root)
        addFileCountTask(root)

        setContentView(scrollView)
        showHolidays(0)
        startNewGame()
    }

    private fun addHolidayTask(root: LinearLayout) {
        root.addView(createSectionTitle("1. Список свят за місяцем"))

        val monthSpinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter

        val showButton = createButton("Показати свята")
        holidayResult = createText("")

        showButton.setOnClickListener {
            showHolidays(monthSpinner.selectedItemPosition)
        }

        root.addView(monthSpinner)
        root.addView(showButton)
        root.addView(holidayResult)
    }

    private fun showHolidays(monthIndex: Int) {
        var text = ""

        for (holiday in holidays[monthIndex]) {
            text += holiday + "\n"
        }

        holidayResult.text = text
    }

    private fun addCardGameTask(root: LinearLayout) {
        root.addView(createSectionTitle("2. Гра «Вгадай 1 з 3 карт»"))
        root.addView(createText("Натисніть одну із закритих карт. Якщо обрали виграшну - перемога."))

        val cardsLayout = LinearLayout(this)
        cardsLayout.orientation = LinearLayout.HORIZONTAL
        cardsLayout.gravity = Gravity.CENTER

        val firstCard = createCardButton(1)
        val secondCard = createCardButton(2)
        val thirdCard = createCardButton(3)
        cardButtons = arrayOf(firstCard, secondCard, thirdCard)

        cardsLayout.addView(firstCard)
        cardsLayout.addView(secondCard)
        cardsLayout.addView(thirdCard)

        val newGameButton = createButton("Нова гра")
        gameResult = createText("")

        newGameButton.setOnClickListener {
            startNewGame()
        }

        root.addView(cardsLayout)
        root.addView(newGameButton)
        root.addView(gameResult)
    }

    private fun createCardButton(cardNumber: Int): Button {
        val button = Button(this)
        button.text = "?"
        button.textSize = 26f

        val params = LinearLayout.LayoutParams(dp(90), dp(110))
        params.setMargins(dp(4), dp(8), dp(4), dp(8))
        button.layoutParams = params

        button.setOnClickListener {
            openCard(cardNumber)
        }

        return button
    }

    private fun startNewGame() {
        winningCard = Random.nextInt(1, 4)
        gameFinished = false
        gameResult.text = "Оберіть карту."

        for (button in cardButtons) {
            button.text = "?"
            button.isEnabled = true
        }
    }

    private fun openCard(selectedCard: Int) {
        if (gameFinished) {
            return
        }

        gameFinished = true

        for (i in cardButtons.indices) {
            val cardNumber = i + 1

            if (cardNumber == winningCard) {
                cardButtons[i].text = "WIN"
            } else {
                cardButtons[i].text = "X"
            }

            cardButtons[i].isEnabled = false
        }

        if (selectedCard == winningCard) {
            gameResult.text = "Ви вгадали карту."
        } else {
            gameResult.text = "Ви не вгадали. Спробуйте ще раз."
        }
    }

    private fun addFileCountTask(root: LinearLayout) {
        root.addView(createSectionTitle("3. Підрахунок символів у файлі"))
        root.addView(createText("Натисніть кнопку та оберіть текстовий або Word-файл на пристрої чи емуляторі."))

        selectedFileText = createText("Файл ще не обрано.")
        val selectFileButton = createButton("Обрати файл")

        fileResult = createText("")

        selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, fileRequestCode)
        }

        root.addView(selectedFileText)
        root.addView(selectFileButton)
        root.addView(fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == fileRequestCode && resultCode == RESULT_OK && data != null) {
            val fileUri = data.data

            if (fileUri != null) {
                selectedFileText.text = "Обрано файл: $fileUri"
                countSymbolsInFile(fileUri)
            }
        }
    }

    private fun countSymbolsInFile(fileUri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(fileUri)
            val bytes = inputStream?.readBytes() ?: ByteArray(0)
            val text = getTextFromFileBytes(bytes)

            var letters = 0
            var spaces = 0
            var signs = 0

            for (symbol in text) {
                if (symbol.isLetter()) {
                    letters++
                } else if (symbol == ' ') {
                    spaces++
                } else {
                    signs++
                }
            }

            fileResult.text = "Літер: $letters\nПробілів: $spaces\nЗнаків: $signs"
        } catch (error: Exception) {
            fileResult.text = "Не вдалося прочитати файл."
        }
    }

    private fun getTextFromFileBytes(bytes: ByteArray): String {
        if (bytes.size > 2 && bytes[0] == 'P'.code.toByte() && bytes[1] == 'K'.code.toByte()) {
            return getTextFromDocx(bytes)
        }

        return bytes.toString(Charsets.UTF_8)
    }

    private fun getTextFromDocx(bytes: ByteArray): String {
        val zipInputStream = ZipInputStream(bytes.inputStream())
        var entry = zipInputStream.nextEntry

        while (entry != null) {
            if (entry.name == "word/document.xml") {
                val output = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var count = zipInputStream.read(buffer)

                while (count > 0) {
                    output.write(buffer, 0, count)
                    count = zipInputStream.read(buffer)
                }

                val xmlText = output.toString("UTF-8")
                return xmlText
                    .replace(Regex("<w:tab[^>]*/>"), " ")
                    .replace(Regex("<w:br[^>]*/>"), "\n")
                    .replace(Regex("</w:p>"), "\n")
                    .replace(Regex("<[^>]+>"), "")
            }

            entry = zipInputStream.nextEntry
        }

        return ""
    }

    private fun createTitle(text: String): TextView {
        val view = createText(text)
        view.textSize = 26f
        return view
    }

    private fun createSectionTitle(text: String): TextView {
        val view = createText(text)
        view.textSize = 21f
        view.setPadding(0, dp(18), 0, dp(8))
        return view
    }

    private fun createText(text: String): TextView {
        val view = TextView(this)
        view.text = text
        view.textSize = 16f
        view.setPadding(0, dp(6), 0, dp(6))
        return view
    }

    private fun createButton(text: String): Button {
        val button = Button(this)
        button.text = text
        return button
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
