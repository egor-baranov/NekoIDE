import java.awt.Color
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.io.FileWriter
import java.util.*
import javax.swing.JTextPane
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.collections.ArrayList


class EditorListener(f: CodeEditor) : DocumentListener {

    private var textEditor: CodeEditor = f

    override fun insertUpdate(e: DocumentEvent) {
        paintEditor(textEditor)
        textEditor.saveState()
    }

    override fun removeUpdate(e: DocumentEvent) {
        paintEditor(textEditor)
        textEditor.saveState()
    }

    override fun changedUpdate(e: DocumentEvent?) { //Plain text components do not fire these events
        // paintEditor(textEditor)
    }
}

class CodeEditor(sourcePath: String, _ide: IDE, _text: String = "") : JTextPane(), KeyListener {
    private var shouldSave = true
    private var ide: IDE = _ide
    private val prevStateStack = Stack<String>()
    private var nextStateStack = Stack<String>()
    val path = File(sourcePath)

    init {
        text = _text
        prevStateStack.push(text)
        font = Font("Fira Code", Font.PLAIN, 18)
        background = darculaBackgroundColor
        caretColor = Color.white
        selectionColor = textSelectionColor
        document.addDocumentListener(EditorListener(this))
        addKeyListener(this)
        paintEditor(this)
    }

    public fun substring(left: Int, right: Int): String {
        return formatted(text).substring(left until right)
    }

    fun setContent(content: String) {
        shouldSave = false
        val caretPosition = this.caretPosition
        text = content
        this.caretPosition = caretPosition
        shouldSave = true
        saveState()
    }

    public fun saveState() {
        if (!shouldSave) return
        prevStateStack.push(this.text)
        nextStateStack.clear()
        val fileWriter = FileWriter(path)
        this.write(fileWriter)
        fileWriter.close()
    }

    fun prevState() {
        if (prevStateStack.size <= 1) return
        val caretPosition = this.caretPosition
        shouldSave = false
        nextStateStack.push(prevStateStack.pop())
        this.text = prevStateStack.peek()
        this.caretPosition = caretPosition
        shouldSave = true
    }

    fun nextState() {
        if (nextStateStack.isEmpty()) return
        val caretPosition = this.caretPosition
        shouldSave = false
        prevStateStack.push(nextStateStack.pop())
        this.text = prevStateStack.peek()
        this.caretPosition = caretPosition
        shouldSave = true
    }

    fun getLineIndex(index: Int): Int {
        return formatted(this.text).substring(0 until index).count { "\n".contains(it) }
    }

    fun isLineCommented(index: Int): Boolean = isTextCommented(index..index)

    fun isTextCommented(range: IntRange): Boolean {
        val formattedText = formatted(text).split("\n") as ArrayList
        var ret = true
        for (line in range) {
            ret = ret && isLineCommented(formattedText[line])
        }
        return ret
    }

    fun comment(index: Int) = comment(index..index)

    fun uncomment(index: Int) = uncomment(index..index)

    fun comment(range: IntRange) {
        val caretPosition = this.caretPosition
        val separatedText = text.split("\n") as ArrayList
        for (line in range) {
            separatedText[line] = commented(separatedText[line])
        }
        setContent(separatedText.joinToString(separator = "\n"))
        this.caretPosition = caretPosition
    }

    fun uncomment(range: IntRange) {
        val caretPosition = this.caretPosition
        val separatedText = text.split("\n") as ArrayList
        for (line in range) {
            separatedText[line] = uncommented(separatedText[line])
        }
        setContent(separatedText.joinToString(separator = "\n"))
        this.caretPosition = caretPosition
    }

    override fun keyTyped(p0: KeyEvent?) {
        return
    }

    override fun keyPressed(event: KeyEvent?) {
        if (event!!.keyCode == KeyEvent.VK_Z) {
            if (event.isControlDown) {
                if (event.isShiftDown) {
                    nextState()
                } else {
                    prevState()
                }
            }
        }
        if (event.keyCode == KeyEvent.VK_SLASH) {
            if (event.isControlDown) {
                val left = getLineIndex(selectionStart)
                val right = getLineIndex(selectionEnd)
                println("$left : $right, isLineCommented = ${isLineCommented(left)}")
                if (isTextCommented(left..right)) {
                    uncomment(left..right)
                } else comment(left..right)
            }
        }
        // сочетания клавиш
        // сочетания клавиш
        if (event.isControlDown) { // создание нового файла
            if (event.keyCode == KeyEvent.VK_N) {
                ide.showNewFileDialog()
            }
            // закрытие файла
            if (event.keyCode == KeyEvent.VK_W) {
                ide.tabbedPane.remove(ide.tabbedPane.selectedComponent)
            }
            // открытие файла
            if (event.keyCode == KeyEvent.VK_O) {
                ide.showOpenFileDialog()
            }
            // открытие меню настроек
            if (event.keyCode == KeyEvent.VK_S) {
                ide.showSettingsDialog()
            }
        }
        return
    }
    
    override fun keyReleased(p0: KeyEvent?) {
        return
    }
}