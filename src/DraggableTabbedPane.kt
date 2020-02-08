import java.awt.Graphics
import java.awt.Image
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.image.BufferedImage
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JTabbedPane

class DraggableTabbedPane : JTabbedPane() {
    private var dragging = false
    private var tabImage: Image? = null
    private var currentMouseLocation: Point? = null
    private var draggedTabIndex = 0
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        // Are we dragging?
        if (dragging && currentMouseLocation != null && tabImage != null) { // Draw the dragged tab
            g.drawImage(tabImage, currentMouseLocation!!.x, currentMouseLocation!!.y, this)
        }
    }

//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val test = JFrame("Tab test")
//            test.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
//            test.setSize(400, 400)
//            val tabs = DraggableTabbedPane()
//            tabs.addTab("One", JButton("One"))
//            tabs.addTab("Two", JButton("Two"))
//            tabs.addTab("Three", JButton("Three"))
//            tabs.addTab("Four", JButton("Four"))
//            test.add(tabs)
//            test.isVisible = true
//        }
//    }

    init {
        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (!dragging) { // Gets the tab index based on the mouse position
                    val tabNumber = getUI().tabForCoordinate(this@DraggableTabbedPane, e.x, e.y)
                    if (tabNumber >= 0) {
                        draggedTabIndex = tabNumber
                        val bounds = getUI().getTabBounds(this@DraggableTabbedPane, tabNumber)
                        // Paint the tabbed pane to a buffer
                        val totalImage: Image =
                            BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
                        val totalGraphics = totalImage.graphics
                        totalGraphics.clip = bounds
                        // Don't be double buffered when painting to a static image.
                        isDoubleBuffered = false
                        paintComponent(totalGraphics)
                        // Paint just the dragged tab to the buffer
                        tabImage = BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB)
                        val graphics = (tabImage as BufferedImage).graphics
                        graphics.drawImage(
                            totalImage,
                            0,
                            0,
                            bounds.width,
                            bounds.height,
                            bounds.x,
                            bounds.y,
                            bounds.x + bounds.width,
                            bounds.y + bounds.height,
                            this@DraggableTabbedPane
                        )
                        dragging = true
                        repaint()
                    }
                } else {
                    currentMouseLocation = e.point
                    // Need to repaint
                    repaint()
                }
                super.mouseDragged(e)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (dragging) {
                    val tabNumber = getUI().tabForCoordinate(this@DraggableTabbedPane, e.x, 10)
                    if (tabNumber >= 0) {
                        val comp = getComponentAt(draggedTabIndex)
                        val title = getTitleAt(draggedTabIndex)
                        removeTabAt(draggedTabIndex)
                        insertTab(title, null, comp, null, tabNumber)
                    }
                }
                dragging = false
                tabImage = null
            }
        })
    }
}