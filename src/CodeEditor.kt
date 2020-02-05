import java.awt.Color
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.FileWriter
import java.util.*
import javax.swing.JTextPane
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


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

class KeywordListener(f: CodeEditor) : KeyListener {

    private var frame: CodeEditor = f


    override fun keyTyped(event: KeyEvent?) {
        return
    }

    override fun keyPressed(event: KeyEvent?) {
        if (event!!.keyCode == KeyEvent.VK_Z) {
            if (event.isControlDown) {
                if (event.isShiftDown) {
                    frame.nextState()
                } else {
                    frame.prevState()
                }
            }
        }
        return
    }

    override fun keyReleased(p0: KeyEvent?) {
        return
    }

}

class CodeEditor(sourcePath: String, text: String = "") : JTextPane() {
    private var shouldSave = true
    private val prevStateStack = Stack<String>()
    private var nextStateStack = Stack<String>()
    val path = sourcePath

    init {
        this.prevStateStack.push(text)
        this.text = text
        this.font = Font("Fira Code", Font.PLAIN, 18)
        this.background = darculaBackgroundColor
        this.caretColor = Color.white
        this.selectionColor = textSelectionColor
        this.document.addDocumentListener(EditorListener(this))
        this.addKeyListener(KeywordListener(this))
        paintEditor(this)
    }

    public fun setContent(content: String) {
        this.text = content
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
        shouldSave = false
        nextStateStack.push(prevStateStack.pop())
        this.text = prevStateStack.peek()
        shouldSave = true
    }

    fun nextState() {
        if (nextStateStack.isEmpty()) return
        shouldSave = false
        prevStateStack.push(nextStateStack.pop())
        this.text = prevStateStack.peek()
        shouldSave = true
    }
}