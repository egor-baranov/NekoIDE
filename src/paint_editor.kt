import java.awt.Color
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

val darculaBackgroundColor = Color(43, 43, 43)
val darculaForegroundColor = Color(60, 63, 65)
val darculaOrange = Color(204, 120, 50)
val textSelectionColor = Color(42, 80, 162)
val darculaPurple = Color(152, 118, 170)
val numberBlue = Color(104, 151, 187)
val commentGrey = Color(128, 128, 128)
val textColor = Color(169, 183, 198)
val stringGreen = Color(106, 135, 89)
val functionNameYellow = Color(255, 198, 109)
val badCharacterRed = Color(255, 0, 0)

fun paintEditor(textEditor: CodeEditor) {

    val editorSD = textEditor.styledDocument

    fun paintWord(word: Word, clr: Color) {
        val tmpAS = SimpleAttributeSet()
        StyleConstants.setForeground(tmpAS, clr)
        val paintRunnable = Runnable {
            editorSD.setCharacterAttributes(word.index, word.source.length, tmpAS, false)
        }
        SwingUtilities.invokeLater(paintRunnable)
    }

    fun paintWords(words: ArrayList<Word>, range: IntRange, clr: Color) {
        for (i in range) paintWord(words[i], clr)
    }

    paintWord(Word(textEditor.text, 0), textColor)

    val formattedText = formatted(textEditor.text)

    val words = separate(formattedText, unite(Punctuations, Operators))

    var oneLineCommented = false
    var multilineCommented = false
    var isString = false
    var isChar = false

    for (i in 0 until words.size) {
        val word = words[i]

        // комментарии
        if (words[i].source == "\n" && oneLineCommented) {
            oneLineCommented = false
        }
        if (i > 0) {
            if (words[i - 1].source == "/" && word.source == "/" && !multilineCommented) {
                oneLineCommented = true
                paintWord(words[i - 1], commentGrey)
                paintWord(words[i], commentGrey)
                continue
            }
            if (words[i - 1].source == "/" && word.source == "*" && !oneLineCommented) {
                multilineCommented = true
                paintWord(words[i - 1], commentGrey)
                paintWord(word, commentGrey)
                continue
            }
            if (words[i - 1].source == "*" && word.source == "/" && multilineCommented) {
                multilineCommented = false
                paintWord(word, commentGrey)
                continue
            }
        }
        if (oneLineCommented || multilineCommented) {
            paintWord(word, commentGrey)
            continue
        }
        // строки
        if (word.source == "\n") {
            isString = false
            isChar = false
            continue
        }
        if (word.source == "\"" || word.source == "\'") {
            paintWord(word, stringGreen)
        }
        if (word.source == "\"" && !isChar) {
            isString = !isString
            continue
        }
        if (word.source == "\'" && !isString) {
            isChar = !isChar
            continue
        }
        if (isChar || isString) {
            paintWord(word, stringGreen)
            continue
        }
        // числа с плавающей точкой
        if (word.source == "." && i > 0 && i < words.size - 1) {
            if (isNumber(words[i - 1].source) && isNumber(words[i + 1].source)) {
                paintWord(word, numberBlue)
            }
            continue
        }
        // ключевые слова, константы и запятая
        if (Keywords.contains(word.source) || ObjectConstants.contains(word.source) || word.source == ",") {
            paintWord(word, darculaOrange)
        }
        // целые числа
        else if (isNumber(word.source)) {
            paintWord(word, numberBlue)
        } else {
            if (getToken(word.source).type == TokenType.Name && !isCorrectName(word.source)) {
                paintWord(word, badCharacterRed)
                continue
            }
            if (i > 0)
            // имена функций
                if (words[i - 1].source == "fun") {
                    paintWord(word, functionNameYellow)
                    continue
                }
        }
    }
    println(toString(separate(formattedText, unite(Punctuations, Operators))))
}