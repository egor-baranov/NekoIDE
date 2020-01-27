import java.util.*
import javax.swing.*
import javax.swing.plaf.ScrollPaneUI
import javax.swing.text.Style

class CodeField : JFrame("Neko IDE") {

    var textEditor: JTextPane? = null
    var shouldSave: Boolean = true
    private var prevStateStack = Stack<String>()
    private var nextStateStack = Stack<String>()

    private var normal: Style? = null
    private val STYLE_normal = "normal"
    private val FONT_style = "Fira Code"
    private val TEXT = arrayOf<String>()

    private fun createStyles(editor: JTextPane) { // Создание стилей
//        normal = editor.addStyle(STYLE_normal, null)
//        StyleConstants.setFontFamily(normal, FONT_style)
//        StyleConstants.setFontSize(normal, 14)
//        // Наследуем свойство FontFamily
//        StyleConstants.setForeground(normal, textColor)
    }

    private fun loadText(editor: JTextPane) { // Загружаем в документ содержимое
//        for (i in TEXT.indices)
//            insertText(editor, TEXT[i][0], normal)
//        // Размещение компонента в конце текста
//        val doc = editor.styledDocument
//        editor.caretPosition = doc.length
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
        prevStateStack.push("")
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        // Создание редактора
        textEditor = JTextPane()
        // Определение стилей редактора
        createStyles(textEditor!!)
        // Загрузка документа
        loadText(textEditor!!)
        // changeDocumentStyle(textEditor!!)
        // Размещение редактора в панели содержимого
        val scrollPane = JScrollPane(textEditor)
        contentPane.add(scrollPane)
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
        if (!shouldSave) return
        prevStateStack.push(textEditor!!.text)
        nextStateStack.clear()
    }

    fun prevState() {
        if (prevStateStack.size <= 1) return
        shouldSave = false
        nextStateStack.push(prevStateStack.pop())
        textEditor!!.text = prevStateStack.peek()
        shouldSave = true
    }

    fun nextState() {
        if (nextStateStack.empty()) return
        shouldSave = false
        prevStateStack.push(nextStateStack.pop())
        textEditor!!.text = prevStateStack.peek()
        shouldSave = true
    }
}
