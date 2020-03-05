import java.awt.Color
import java.util.*
import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import kotlin.collections.ArrayList

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
val highlightYellowColor = Color(187, 181, 41)
val indexBracketColor = Color(95, 140, 138)
val unusedColor = commentGrey

// TODO: repaint на подотрезке текста (находим общий префикс, постфикс и вызываем repaint)
// TODO: добавить подсветку TODO-блоков

// отрисовка элементов по типу слов, чисел и тд
fun paintEditor(textEditor: CodeEditor) {

    val editorSD = textEditor.styledDocument

    fun paintWord(word: Word, clr: Color, len: Int = -1) {
        val tmpAS = SimpleAttributeSet()
        StyleConstants.setForeground(tmpAS, clr)
        val paintRunnable = Runnable {
            editorSD.setCharacterAttributes(
                word.index,
                if (len == -1) word.source.length else len, tmpAS, false
            )
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

    // обработка скобочных последовательностей
    val bracketStack = Stack<Word>()
    textEditor.pairedBracket.clear()

    for (i in 0 until words.size) {
        val word = words[i]

        // комментарии
        if (words[i].source == "\n" && oneLineCommented) {
            oneLineCommented = false
        }
        if (i > 0) {
            if (words[i - 1].source == "/" && word.source == "/" && !multilineCommented &&
                word.index - words[i - 1].index == 1 && !isString && !isChar
            ) {
                oneLineCommented = true
                paintWord(words[i - 1], commentGrey)
                paintWord(words[i], commentGrey)
                continue
            }
            if (words[i - 1].source == "/" && word.source == "*" && !oneLineCommented &&
                word.index - words[i - 1].index == 1 && !isString && !isChar
            ) {
                multilineCommented = true
                paintWord(words[i - 1], commentGrey)
                paintWord(word, commentGrey)
                continue
            }
            if (words[i - 1].source == "*" && word.source == "/" && multilineCommented &&
                word.index - words[i - 1].index == 1
            ) {
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
            if (isString) {
                var t = i
                while (words[t].source != "\"") {
                    --t
                    paintWord(words[t], badCharacterRed)
                }
                paintWord(words[t], badCharacterRed)
            }
            if (isChar) {
                var t = i
                while (words[t].source != "\'") {
                    --t
                    paintWord(words[t], badCharacterRed)
                }
                paintWord(words[t], badCharacterRed)
            }
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
            if (isString && word.source == "$") {
                paintWord(word, darculaOrange)
            } else if (isString && words[i - 1].source == "$" && getToken(word.source).type == TokenType.Name) {
                paintWord(word, textColor)
            } else if (isString && words[i - 1].source == "\\" && words[i - 1].index + 1 == word.index) {
                var slashCountMod2 = 1
                var t = i - 2
                while (words[t].source == "\\") {
                    slashCountMod2 = (slashCountMod2 + 1) % 2
                    --t
                }
                paintWord(word, stringGreen)
                if (slashCountMod2 == 1) {
                    paintWord(word, darculaOrange, 1)
                }
            } else {
                paintWord(word, if (word.source == "\\") darculaOrange else stringGreen)
            }
            continue
        }

        if (word.source == ";") {
            if (i != words.size - 1 && i != 0) {
                if (words[i - 1].source != "\n" && words[i + 1].source != "\n") {
                    paintWord(word, darculaOrange)
                } else paintWord(word, unusedColor)
            }
            continue
        }

        if (word.isBracket()) {
            if (word.isLeftBracket()) {
                bracketStack.push(word)
            } else {
                if (bracketStack.isEmpty()) {
                    paintWord(word, badCharacterRed)
                    continue
                }
                if (!isBracketPair(bracketStack.peek(), word)) {
                    paintWord(word, badCharacterRed)
                    continue
                }
                textEditor.pairedBracket[bracketStack.peek().toPair()] = word.toPair()
                textEditor.pairedBracket[word.toPair()] = bracketStack.peek().toPair()
                bracketStack.pop()
            }
        }

        // числа с плавающей точкой
        if (word.source == "." && i > 0 && i < words.size - 1) {
            if (isNumber(words[i - 1].source) && isNumber(words[i + 1].source)) {
                paintWord(word, numberBlue)
            }
            continue
        }

        // ключевые слова, константы и запятая
        if (Keywords.contains(word.source) || ObjectConstants.contains(word.source) || word.source == "," ||
            setOf("and", "or", "not").contains(word.source)
        ) {
            if (setOf("until", "downTo").contains(word.source)) {
                paintWord(word, functionNameYellow)
            } else paintWord(word, darculaOrange)
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

    while (!bracketStack.isEmpty()) {
        paintWord(bracketStack.peek(), badCharacterRed)
        bracketStack.pop()
    }

    // println(toString(separate(formattedText, unite(Punctuations, Operators))))
}

// подсветка скобочек и тд = caret position changed
fun highlightEditor(textEditor: CodeEditor) {
    val editorSD = textEditor.styledDocument

    fun paintWord(word: Word, clr: Color) {
        val tmpAS = SimpleAttributeSet()
        StyleConstants.setForeground(tmpAS, clr)
        val paintRunnable = Runnable {
            editorSD.setCharacterAttributes(word.index, word.source.length, tmpAS, false)
        }
        SwingUtilities.invokeLater(paintRunnable)
    }

    val formattedText = formatted(textEditor.text)

    val words = separate(formattedText, unite(Punctuations, Operators))

    var oneLineCommented = false
    var multilineCommented = false
    var isString = false
    var isChar = false

    val notRepaint: MutableSet<Int> = mutableSetOf()

    for (i in 0 until words.size) {
        val word = words[i]

        // комментарии
        if (words[i].source == "\n" && oneLineCommented) {
            oneLineCommented = false
        }

        if (i > 0) {
            if (words[i - 1].source == "/" && word.source == "/" && !multilineCommented &&
                word.index - words[i - 1].index == 1
            ) {
                oneLineCommented = true
                continue
            }
            if (words[i - 1].source == "/" && word.source == "*" && !oneLineCommented &&
                word.index - words[i - 1].index == 1
            ) {
                multilineCommented = true
                continue
            }
            if (words[i - 1].source == "*" && word.source == "/" && multilineCommented &&
                word.index - words[i - 1].index == 1
            ) {
                multilineCommented = false
                continue
            }
        }
        if (oneLineCommented || multilineCommented) {
            continue
        }
        // строки
        if (word.source == "\n") {
            isString = false
            isChar = false
            continue
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
            continue
        }

        // числа с плавающей точкой
        if (word.source == "." && i > 0 && i < words.size - 1) {
            continue
        }

        // TODO: 2a[i]
        if (word.isBracket()) {
            if (textEditor.caretPosition in word.index..word.index + 1) {
                if (textEditor.pairedBracket.containsKey(word.toPair())) {
                    paintWord(word, highlightYellowColor)
                    paintWord(toWord(textEditor.pairedBracket[word.toPair()]!!), highlightYellowColor)
                }
            } else {
                if (textEditor.pairedBracket.containsKey(word.toPair())) {
                    val other = toWord(textEditor.pairedBracket[word.toPair()]!!)
                    if (textEditor.caretPosition in other.index..other.index + 1) {
                        paintWord(word, highlightYellowColor)
                    } else {
                        if (word.source == "[" && i != 0) {
                            if (getToken(words[i - 1].source).type == TokenType.Name) {
                                paintWord(word, indexBracketColor)
                                notRepaint.add(word.index)
                                paintWord(other, indexBracketColor)
                                notRepaint.add(other.index)
                                continue
                            }
                        }
                        if (!notRepaint.contains(word.index)) {
                            paintWord(word, textColor)
                        } else notRepaint.remove(word.index)
                    }
                } else paintWord(word, badCharacterRed)
            }
            continue
        }
    }
}