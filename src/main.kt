import java.awt.Color
import java.awt.EventQueue
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class EditorListener(f: IDE) : DocumentListener {

    private var frame: IDE = f
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

class KeywordListener(f: IDE) : KeyListener {

    private var frame: IDE = f


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

private fun createAndShowGUI() {
    val frame = IDE()
    val textEditor = frame.textEditor!!

    textEditor.font = Font("Fira Code", Font.PLAIN, 24)
    textEditor.background = darculaBackgroundColor
    textEditor.caretColor = Color.white
    textEditor.selectionColor = textSelectionColor

    textEditor.document.addDocumentListener(EditorListener(frame))
    textEditor.addKeyListener(KeywordListener(frame))
    paintEditor(textEditor)
}

fun main(args: Array<String>) {
    EventQueue.invokeLater(::createAndShowGUI)
    // val c: Char = 'z'
}