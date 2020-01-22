import java.awt.Color
import java.awt.EventQueue
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.Style
import javax.swing.text.StyleConstants


val darculaBackgroundColor = Color(43, 43, 43)
val darculaOrange = Color(204, 120, 50)
val textSelectionColor = Color(42, 80, 142)
val darculaPurple = Color(152, 118, 170)
val numberBlue = Color(104, 151, 187)
val commentGrey = Color(128, 128, 128)
val textColor = Color(169, 183, 198)
val stringGreen = Color(106, 135, 89)
val functionNameYellow = Color(255, 198, 109)
val badCharacterRed = Color(255, 0, 0)

class JTextPaneTest : JFrame("Neko IDE") {

    var textEditor: JTextPane? = null

    var prevStateStack = Stack<String>()
    var nextStateStack = Stack<String>()

    private var normal: Style? = null
    private val STYLE_normal = "normal"
    private val FONT_style = "Fira Code"
    private val TEXT = arrayOf(
        arrayOf("Компонент or ", "normal"),
        arrayOf("", "normal"),
        arrayOf("JTextPane незаменим при создании в приложении ", "normal"),
        arrayOf("многофункционального public редактора.", "normal"),
        arrayOf("", "normal"),
        arrayOf("Он and вставлять в документ визуальные ", "normal"),
        arrayOf("компоненты типа fun и JRadioButton. ", "normal")
    )

    private fun createStyles(editor: JTextPane) { // Создание стилей
//        normal = editor.addStyle(STYLE_normal, null)
//        StyleConstants.setFontFamily(normal, FONT_style)
//        StyleConstants.setFontSize(normal, 14)
//        // Наследуем свойство FontFamily
//        StyleConstants.setForeground(normal, textColor)
    }

    private fun loadText(editor: JTextPane) { // Загружаем в документ содержимое
        for (i in TEXT.indices)
            insertText(editor, TEXT[i][0], normal)
        // Размещение компонента в конце текста
        val doc = editor.styledDocument
        editor.caretPosition = doc.length
    }

    private fun insertText(editor: JTextPane, string: String, style: Style?) {
        try {
            val doc = editor.document
            doc.insertString(doc.length, string, style)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        // Создание редактора
        textEditor = JTextPane()
        // Определение стилей редактора
        createStyles(textEditor!!)
        // Загрузка документа
        loadText(textEditor!!)
        // changeDocumentStyle(textEditor!!)
        // Размещение редактора в панели содержимого
        contentPane.add(JScrollPane(textEditor))
        // Открытие окна
        setSize(380, 240)
        isVisible = true
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
        }
    }

    public fun saveState() {
        prevStateStack.push(textEditor!!.text)
        nextStateStack.clear()
        println("save")
    }

    public fun prevState() {
        if (prevStateStack.isEmpty()) return
        println("prev")
    }

    public fun nextState() {
        if (nextStateStack.isEmpty()) return
        println("next")
    }
}

fun paintEditor(textEditor: JTextPane) {

    val editorSD = textEditor.styledDocument

    fun paintWord(word: Word, clr: Color) {
        val tmpAS = SimpleAttributeSet()
        StyleConstants.setForeground(tmpAS, clr)
        val paintRunnable = Runnable {
            editorSD.setCharacterAttributes(word.index, word.source.length, tmpAS, false)
        }
        SwingUtilities.invokeLater(paintRunnable)
    }

    paintWord(Word(textEditor.text, 0), textColor)

    val formattedText = formatted(textEditor.text)

    val words = separate(formattedText, unite(Punctuations, Operators))
    for (i in 0 until words.size) {
        val word = words[i]
        if (word.source == "." && i > 0 && i < words.size - 1) {
            if (isNumber(words[i - 1].source) && isNumber(words[i + 1].source))
                paintWord(word, numberBlue)
            continue
        }
        if (i >= 1 && (word.source == "\"" || word.source == "\'")) {
            var prevIndex = -1
            for (index in i - 1 downTo 0) {
                if (words[index].source == "\n")
                    break
                if (words[index].source == word.source) {
                    prevIndex = index
                    break
                }
            }
            if (prevIndex != -1) {
                for (j in prevIndex..i)
                    paintWord(words[j], stringGreen)
                continue
            }
        }
        if (Keywords.contains(word.source) || Constants.contains(word.source) || word.source == ",")
            paintWord(word, darculaOrange)
        else if (isNumber(word.source)) paintWord(word, numberBlue)
        else {
            if (i > 0)
                if (words[i - 1].source == "fun")
                    paintWord(word, functionNameYellow)
        }
    }
    println(toString(separate(formattedText, unite(Punctuations, Operators))))
}

class EditorListener(f: JTextPaneTest) : DocumentListener {

    private var frame: JTextPaneTest = f
    private var textEditor: JTextPane = f.textEditor!!

    override fun insertUpdate(e: DocumentEvent) {
        paintEditor(textEditor)
        frame.saveState()
    }

    override fun removeUpdate(e: DocumentEvent) {
        paintEditor(textEditor)
        frame.saveState()
    }

    override fun changedUpdate(e: DocumentEvent?) { //Plain text components do not fire these events
        // paintEditor(textEditor)
    }
}

class KeywordListener(f: JTextPaneTest) : KeyListener {

    private var frame: JTextPaneTest = f

    override fun keyTyped(event: KeyEvent?) {
        println("key typed")
//        if (event?.keyCode == KeyEvent.VK_Z)
//            if (event.isControlDown) {
//                if (event.isShiftDown)
//                    frame.prevState()
//                else {
//                    frame.nextState()
//                }
//            }
    }

    override fun keyPressed(p0: KeyEvent?) {
        println("key pressed")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun keyReleased(p0: KeyEvent?) {
        println("key released")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

private fun createAndShowGUI() {
    val frame = JTextPaneTest()
    val textEditor = frame.textEditor!!

    textEditor.font = Font("Fira Code", Font.PLAIN, 24)
    textEditor.background = darculaBackgroundColor
    textEditor.caretColor = Color.white
    textEditor.selectionColor = textSelectionColor

    textEditor.document.addDocumentListener(EditorListener(frame))
    frame.addKeyListener(KeywordListener(frame))
    paintEditor(textEditor)
}

fun main(args: Array<String>) {
    EventQueue.invokeLater(::createAndShowGUI)
    // val frame = GUIFrame()
}