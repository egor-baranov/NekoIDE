import java.awt.Color
import java.awt.EventQueue
import javax.swing.*
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.Style
import javax.swing.text.StyleConstants

val darculaBackground = Color(43, 43, 43)
val darculaOrange = Color(203, 96, 45)

class JTextPaneTest : JFrame("Neko IDE") {

    var textEditor: JTextPane? = null

    private var heading: Style? = null
    private var normal: Style? = null
    private val STYLE_heading = "heading"
    private val STYLE_normal = "normal"
    private val FONT_style = "Fira Code"
    private val TEXT = arrayOf(
        arrayOf("Компонент JTextPane \r\n", "heading"),
        arrayOf("\r\n", "normal"),
        arrayOf("JTextPane незаменим при создании в приложении  \r\n", "normal"),
        arrayOf("многофункционального текстового редактора.\r\n", "normal"),
        arrayOf("\r\n", "normal"),
        arrayOf("Он позволяет вставлять в документ визуальные \r\n", "normal"),
        arrayOf("компоненты типа JCheckBox и JRadioButton.\r\n ", "normal")
    )

    private fun createStyles(editor: JTextPane) { // Создание стилей
        normal = editor.addStyle(STYLE_normal, null)
        StyleConstants.setFontFamily(normal, FONT_style)
        StyleConstants.setFontSize(normal, 12)
        // Наследуем свойстdо FontFamily
        heading = editor.addStyle(STYLE_heading, normal)
        StyleConstants.setFontSize(heading, 24)
        StyleConstants.setBold(heading, true)
        StyleConstants.setForeground(normal, Color.white)
    }

    private fun loadText(editor: JTextPane) { // Загружаем в документ содержимое
        for (i in TEXT.indices) {
            val style = if (TEXT[i][1] == STYLE_heading) heading else normal
            insertText(editor, TEXT[i][0], style)
        }
        // Размещение компонента в конце текста
        val doc = editor.styledDocument
        editor.caretPosition = doc.length
    }

    private fun changeDocumentStyle(editor: JTextPane) { // Изменение стиля части текста
        val orange = SimpleAttributeSet()
        StyleConstants.setForeground(orange, darculaOrange)
        val doc = editor.styledDocument
        doc.setCharacterAttributes(10, 9, orange, false)
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
        changeDocumentStyle(textEditor!!)
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

private fun createAndShowGUI() {
    val frame = JTextPaneTest()
    frame.textEditor!!.background = darculaBackground
    frame.textEditor!!.caretColor = Color.white
}

fun main(args: Array<String>) {
    EventQueue.invokeLater(::createAndShowGUI)
}