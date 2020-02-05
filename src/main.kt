import java.awt.EventQueue
import java.awt.Insets
import javax.swing.UIManager

private fun createAndShowGUI() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    val frame = IDE()
}

fun main(args: Array<String>) {
    EventQueue.invokeLater(::createAndShowGUI)
    // val c: Char = 'z'
}