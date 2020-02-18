import java.awt.EventQueue
import javax.swing.JFrame
import javax.swing.SwingUtilities
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
    print(strip(""))
    JFrame.setDefaultLookAndFeelDecorated(true)
    EventQueue.invokeLater(::createAndShowGUI)
    // val c: Char = 'z'
}