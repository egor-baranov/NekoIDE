import java.awt.Color
import java.awt.EventQueue
import java.awt.Font
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
val badCharacterRed = Color(255, 0, 0)

class JTextPaneTest : JFrame("Neko IDE") {

    var textEditor: JTextPane? = null

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

    for (i in split(cleared(textEditor.text), setOf(' ', '\n', '\r'))) {
        if (Keywords.contains(i.source) or Constants.contains(i.source))
            paintWord(i, darculaOrange)
        if (isNumber(i.source)) paintWord(i, numberBlue)
    }

    println(textEditor.text)
}

class EditorListener(t: JTextPane) : DocumentListener {

    var textEditor: JTextPane = t

    override fun insertUpdate(e: DocumentEvent) {
        paintEditor(textEditor)
    }

    override fun removeUpdate(e: DocumentEvent) {
        paintEditor(textEditor)
    }

    override fun changedUpdate(e: DocumentEvent?) { //Plain text components do not fire these events
        // paintEditor(textEditor)
    }
}

private fun createAndShowGUI() {
    val frame = JTextPaneTest()

    frame.textEditor!!.font = Font("Fira Code", Font.PLAIN, 14)

    frame.textEditor!!.background = darculaBackgroundColor
    frame.textEditor!!.caretColor = Color.white
    frame.textEditor!!.selectionColor = textSelectionColor
    frame.textEditor!!.document.addDocumentListener(EditorListener(frame.textEditor!!))
    paintEditor(frame.textEditor!!)
}

fun main(args: Array<String>) {
    EventQueue.invokeLater(::createAndShowGUI)
}