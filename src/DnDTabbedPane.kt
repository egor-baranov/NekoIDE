import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*


class DnDTabbedPane : JTabbedPane(), ActionListener {
    private val FLAVOR = DataFlavor(
        DataFlavor.javaJVMLocalObjectMimeType, NAME
    )
    private var mIsDrawRect = false
    private val mLineRect: Rectangle2D = Rectangle2D.Double()
    private val mLineColor = Color(0, 100, 255)
    var acceptor: TabAcceptor? = null

    private fun getTabTransferData(a_event: DropTargetDropEvent): TabTransferData? {
        try {
            return a_event.transferable.getTransferData(FLAVOR) as TabTransferData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getTabTransferData(a_event: DropTargetDragEvent): TabTransferData? {
        try {
            return a_event.transferable.getTransferData(FLAVOR) as TabTransferData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getTabTransferData(a_event: DragSourceDragEvent): TabTransferData? {
        try {
            return a_event.dragSourceContext
                .transferable.getTransferData(FLAVOR) as TabTransferData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    internal inner class TabTransferable(a_tabbedPane: DnDTabbedPane?, a_tabIndex: Int) :
        Transferable {
        private var mData: TabTransferData? = null
        override fun getTransferData(flavor: DataFlavor): Any {
            return mData!!
            // return DnDTabbedPane.this;
        }

        override fun getTransferDataFlavors(): Array<DataFlavor?> {
            val f = arrayOfNulls<DataFlavor>(1)
            f[0] = FLAVOR
            return f
        }

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            return flavor.humanPresentableName == NAME
        }

        init {
            mData = TabTransferData(this@DnDTabbedPane, a_tabIndex)
        }
    }

    internal inner class TabTransferData {
        var tabbedPane: DnDTabbedPane? = null
        var tabIndex = -1

        constructor() {}
        constructor(a_tabbedPane: DnDTabbedPane?, a_tabIndex: Int) {
            tabbedPane = a_tabbedPane
            tabIndex = a_tabIndex
        }

    }

    private fun buildGhostLocation(a_location: Point): Point {
        var ret = Point(a_location)
        when (getTabPlacement()) {
            TOP -> {
                ret.y = 1
                ret.x -= s_glassPane.ghostWidth / 2
            }
            BOTTOM -> {
                ret.y = height - 1 - s_glassPane.ghostHeight
                ret.x -= s_glassPane.ghostWidth / 2
            }
            LEFT -> {
                ret.x = 1
                ret.y -= s_glassPane.ghostHeight / 2
            }
            RIGHT -> {
                ret.x = width - 1 - s_glassPane.ghostWidth
                ret.y -= s_glassPane.ghostHeight / 2
            }
        }
        ret = SwingUtilities.convertPoint(
            this@DnDTabbedPane,
            ret, s_glassPane
        )
        return ret
    }

    internal inner class CDropTargetListener : DropTargetListener {
        override fun dragEnter(e: DropTargetDragEvent) { // System.out.println("DropTarget.dragEnter: " + DnDTabbedPane.this);
            if (isDragAcceptable(e)) {
                e.acceptDrag(e.dropAction)
            } else {
                e.rejectDrag()
            } // if
        }

        override fun dragExit(e: DropTargetEvent) { // System.out.println("DropTarget.dragExit: " + DnDTabbedPane.this);
            mIsDrawRect = false
        }

        override fun dropActionChanged(e: DropTargetDragEvent) {}
        override fun dragOver(e: DropTargetDragEvent) {
            val data = getTabTransferData(e)
            if (getTabPlacement() == TOP
                || getTabPlacement() == BOTTOM
            ) {
                initTargetLeftRightLine(getTargetTabIndex(e.location), data)
            } else {
                initTargetTopBottomLine(getTargetTabIndex(e.location), data)
            } // if-else
            repaint()
            if (hasGhost()) {
                s_glassPane.setPoint(buildGhostLocation(e.location))
                s_glassPane.repaint()
            }
        }

        override fun drop(a_event: DropTargetDropEvent) { // System.out.println("DropTarget.drop: " + DnDTabbedPane.this);
            if (isDropAcceptable(a_event)) {
                convertTab(
                    getTabTransferData(a_event),
                    getTargetTabIndex(a_event.location)
                )
                a_event.dropComplete(true)
            } else {
                a_event.dropComplete(false)
            } // if-else
            mIsDrawRect = false
            repaint()
        }

        private fun isDragAcceptable(e: DropTargetDragEvent): Boolean {
            val t = e.transferable ?: return false
            // if
            val flavor = e.currentDataFlavors
            if (!t.isDataFlavorSupported(flavor[0])) {
                return false
            } // if
            val data = getTabTransferData(e)
            if (this@DnDTabbedPane === data!!.tabbedPane
                && data!!.tabIndex >= 0
            ) {
                return true
            } // if
            if (this@DnDTabbedPane !== data!!.tabbedPane) {
                if (acceptor != null) {
                    return acceptor!!.isDropAcceptable(data!!.tabbedPane, data.tabIndex)
                } // if
            } // if
            return false
        }

        private fun isDropAcceptable(e: DropTargetDropEvent): Boolean {
            val t = e.transferable ?: return false
            // if
            val flavor = e.currentDataFlavors
            if (!t.isDataFlavorSupported(flavor[0])) {
                return false
            } // if
            val data = getTabTransferData(e)
            if (this@DnDTabbedPane === data!!.tabbedPane
                && data!!.tabIndex >= 0
            ) {
                return true
            } // if
            if (this@DnDTabbedPane !== data!!.tabbedPane) {
                if (acceptor != null) {
                    return acceptor!!.isDropAcceptable(data!!.tabbedPane, data.tabIndex)
                } // if
            } // if
            return false
        }
    }

    private var mHasGhost = true
    fun setPaintGhost(flag: Boolean) {
        mHasGhost = flag
    }

    fun hasGhost(): Boolean {
        return mHasGhost
    }

    /**
     * returns potential index for drop.
     * @param a_point point given in the drop site component's coordinate
     * @return returns potential index for drop.
     */
    private fun getTargetTabIndex(a_point: Point): Int {
        val isTopOrBottom = (getTabPlacement() == TOP
                || getTabPlacement() == BOTTOM)
        // if the pane is empty, the target index is always zero.
        if (tabCount == 0) {
            return 0
        } // if
        for (i in 0 until tabCount) {
            val r = getBoundsAt(i)
            if (isTopOrBottom) {
                r.setRect(r.x - r.width / 2.toDouble(), r.y.toDouble(), r.width.toDouble(), r.height.toDouble())
            } else {
                r.setRect(r.x.toDouble(), r.y - r.height / 2.toDouble(), r.width.toDouble(), r.height.toDouble())
            } // if-else
            if (r.contains(a_point)) {
                return i
            } // if
        } // for
        val r = getBoundsAt(tabCount - 1)
        if (isTopOrBottom) {
            val x = r.x + r.width / 2
            r.setRect(x.toDouble(), r.y.toDouble(), width - x.toDouble(), r.height.toDouble())
        } else {
            val y = r.y + r.height / 2
            r.setRect(r.x.toDouble(), y.toDouble(), r.width.toDouble(), height - y.toDouble())
        } // if-else
        return if (r.contains(a_point)) tabCount else -1
    }

    override fun insertTab(title: String?, icon: Icon?, component: Component?, tip: String?, index: Int) {
        super.insertTab(title, icon, component, tip, index)
//        val label = JLabel(title)
//        label.preferredSize = Dimension(100, 20)
//        setTabComponentAt(index, label)
        val pnlTab = JPanel(GridBagLayout())
        val label = JLabel(title)
        label.isOpaque = false
        label.preferredSize = Dimension(100, 20)
        setTabComponentAt(index, label)
//        val buttonIcon = ImageIO.read(File("C:\\Users\\enter\\IdeaProjects\\NekoIDE\\res\\icons\\icons8-delete-24.png"))
//
//        val closeButton = JButton(ImageIcon(buttonIcon))
//        closeButton.preferredSize = Dimension(16, 16)
//        closeButton.border = BorderFactory.createEmptyBorder()
//        closeButton.isContentAreaFilled = false
//
//        val gbc = GridBagConstraints()
//        gbc.gridx = 0
//        gbc.gridy = 0
//        gbc.weightx = 1.0
//
//        pnlTab.add(label, gbc)
//
//        gbc.gridx++
//        gbc.weightx = 0.0
//        pnlTab.add(closeButton, gbc)
//
//        setTabComponentAt(index, pnlTab)

//        closeButton.addActionListener(this)
    }

    private fun convertTab(a_data: TabTransferData?, a_targetIndex: Int) {
        var aTargetIndex = a_targetIndex
        val source = a_data!!.tabbedPane
        val sourceIndex = a_data.tabIndex
        if (sourceIndex < 0) {
            return
        } // if
        val cmp = source!!.getComponentAt(sourceIndex)
        val str = source.getTitleAt(sourceIndex)
        if (this !== source) {
            source.remove(sourceIndex)
            if (aTargetIndex == tabCount) {
                addTab(str, cmp)
            } else {
                if (aTargetIndex < 0) {
                    aTargetIndex = 0
                } // if
                insertTab(str, null, cmp, null, aTargetIndex)
                val label = JLabel(str)
                label.preferredSize = Dimension(100, 20)
                setTabComponentAt(tabCount - 1, label)
            } // if
            selectedComponent = cmp
            // System.out.println("press="+sourceIndex+" next="+a_targetIndex);
            return
        } // if
        if (aTargetIndex < 0 || sourceIndex == aTargetIndex) { //System.out.println("press="+prev+" next="+next);
            return
        } // if
        selectedIndex = when {
            aTargetIndex == tabCount -> { //System.out.println("last: press="+prev+" next="+next);
                source.remove(sourceIndex)
                addTab(str, cmp)
                tabCount - 1
            }
            sourceIndex > aTargetIndex -> { //System.out.println("   >: press="+prev+" next="+next);
                source.remove(sourceIndex)
                insertTab(str, null, cmp, null, aTargetIndex)
                aTargetIndex
            }
            else -> { //System.out.println("   <: press="+prev+" next="+next);
                source.remove(sourceIndex)
                insertTab(str, null, cmp, null, aTargetIndex - 1)
                aTargetIndex - 1
            }
        }
    }

    private fun initTargetLeftRightLine(next: Int, a_data: TabTransferData?) {
        if (next < 0) {
            mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
            mIsDrawRect = false
            return
        } // if
        if (a_data!!.tabbedPane === this
            && (a_data!!.tabIndex == next
                    || next - a_data.tabIndex == 1)
        ) {
            mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
            mIsDrawRect = false
        } else if (tabCount == 0) {
            mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
            mIsDrawRect = false
            return
        } else if (next == 0) {
            val rect = getBoundsAt(0)
            mLineRect.setRect(
                -LINEWIDTH / 2.toDouble(),
                rect.y.toDouble(),
                LINEWIDTH.toDouble(),
                rect.height.toDouble()
            )
            mIsDrawRect = true
        } else if (next == tabCount) {
            val rect = getBoundsAt(tabCount - 1)
            mLineRect.setRect(
                rect.x + rect.width - LINEWIDTH / 2.toDouble(), rect.y.toDouble(),
                LINEWIDTH.toDouble(), rect.height.toDouble()
            )
            mIsDrawRect = true
        } else {
            val rect = getBoundsAt(next - 1)
            mLineRect.setRect(
                rect.x + rect.width - LINEWIDTH / 2.toDouble(), rect.y.toDouble(),
                LINEWIDTH.toDouble(), rect.height.toDouble()
            )
            mIsDrawRect = true
        }
    }

    private fun initTargetTopBottomLine(next: Int, a_data: TabTransferData?) {
        if (next < 0) {
            mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
            mIsDrawRect = false
            return
        } // if
        if (a_data!!.tabbedPane === this
            && (a_data!!.tabIndex == next
                    || next - a_data.tabIndex == 1)
        ) {
            mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
            mIsDrawRect = false
        } else if (tabCount == 0) {
            mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
            mIsDrawRect = false
            return
        } else if (next == tabCount) {
            val rect = getBoundsAt(tabCount - 1)
            mLineRect.setRect(
                rect.x.toDouble(), rect.y + rect.height - LINEWIDTH / 2.toDouble(),
                rect.width.toDouble(), LINEWIDTH.toDouble()
            )
            mIsDrawRect = true
        } else if (next == 0) {
            val rect = getBoundsAt(0)
            mLineRect.setRect(
                rect.x.toDouble(),
                -LINEWIDTH / 2.toDouble(),
                rect.width.toDouble(),
                LINEWIDTH.toDouble()
            )
            mIsDrawRect = true
        } else {
            val rect = getBoundsAt(next - 1)
            mLineRect.setRect(
                rect.x.toDouble(), rect.y + rect.height - LINEWIDTH / 2.toDouble(),
                rect.width.toDouble(), LINEWIDTH.toDouble()
            )
            mIsDrawRect = true
        }
    }

    private fun initGlassPane(
        c: Component,
        tabPt: Point,
        a_tabIndex: Int
    ) { //Point p = (Point) pt.clone();
        rootPane.glassPane = s_glassPane
        if (hasGhost()) {
            val rect = getBoundsAt(a_tabIndex)
            var image = BufferedImage(
                c.width,
                c.height, BufferedImage.TYPE_INT_ARGB
            )
            val g = image.graphics
            c.paint(g)
            image = image.getSubimage(rect.x, rect.y, rect.width, rect.height)
            s_glassPane.setImage(image)
        } // if
        s_glassPane.setPoint(buildGhostLocation(tabPt))
        s_glassPane.isVisible = true
    }

    private val tabAreaBound: Rectangle
        get() {
            val lastTab = getUI().getTabBounds(this, tabCount - 1)
            return Rectangle(0, 0, width, lastTab.y + lastTab.height)
        }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (mIsDrawRect) {
            val g2 = g as Graphics2D
            g2.paint = mLineColor
            g2.fill(mLineRect)
        } // if
    }

    interface TabAcceptor {
        fun isDropAcceptable(a_component: DnDTabbedPane?, a_index: Int): Boolean
    }

    companion object {
        const val serialVersionUID = 1L
        private const val LINEWIDTH = 3
        private const val NAME = "TabTransferData"
        private val s_glassPane = GhostGlassPane()
    }

    init {
        val dsl: DragSourceListener = object : DragSourceListener {
            override fun dragEnter(e: DragSourceDragEvent) {
                e.dragSourceContext.cursor = DragSource.DefaultMoveDrop
            }

            override fun dragExit(e: DragSourceEvent) {
                e.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
                mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
                mIsDrawRect = false
                s_glassPane.setPoint(Point(-1000, -1000))
                s_glassPane.repaint()
            }

            override fun dragOver(e: DragSourceDragEvent) { //e.getLocation()
                //This method returns a Point indicating the cursor location in screen coordinates at the moment
                val data = getTabTransferData(e)
                if (data == null) {
                    e.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
                    return
                } // if
                /*
                Point tabPt = e.getLocation();
                SwingUtilities.convertPointFromScreen(tabPt, DnDTabbedPane.this);
                if (DnDTabbedPane.this.contains(tabPt)) {
                    int targetIdx = getTargetTabIndex(tabPt);
                    int sourceIndex = data.getTabIndex();
                    if (getTabAreaBound().contains(tabPt)
                            && (targetIdx >= 0)
                            && (targetIdx != sourceIndex)
                            && (targetIdx != sourceIndex + 1)) {
                        e.getDragSourceContext().setCursor(
                                DragSource.DefaultMoveDrop);

                        return;
                    } // if

                    e.getDragSourceContext().setCursor(
                            DragSource.DefaultMoveNoDrop);
                    return;
                } // if
                */e.dragSourceContext.cursor = DragSource.DefaultMoveDrop
            }

            override fun dragDropEnd(e: DragSourceDropEvent) {
                mIsDrawRect = false
                mLineRect.setRect(0.0, 0.0, 0.0, 0.0)
                // m_dragTabIndex = -1;
                if (hasGhost()) {
                    s_glassPane.isVisible = false
                    s_glassPane.setImage(null)
                }
            }

            override fun dropActionChanged(e: DragSourceDragEvent) {}
        }
        val dgl = DragGestureListener { e ->
            // System.out.println("dragGestureRecognized");
            val tabPt = e.dragOrigin
            val dragTabIndex = indexAtLocation(tabPt.x, tabPt.y)
            if (dragTabIndex < 0) {
                return@DragGestureListener
            } // if
            initGlassPane(e.component, e.dragOrigin, dragTabIndex)
            try {
                e.startDrag(
                    DragSource.DefaultMoveDrop,
                    TabTransferable(this@DnDTabbedPane, dragTabIndex), dsl
                )
            } catch (idoe: InvalidDnDOperationException) {
                idoe.printStackTrace()
            }
        }
        //dropTarget =
        DropTarget(
            this, DnDConstants.ACTION_COPY_OR_MOVE,
            CDropTargetListener(), true
        )
        DragSource().createDefaultDragGestureRecognizer(
            this,
            DnDConstants.ACTION_COPY_OR_MOVE, dgl
        )
        acceptor = object : TabAcceptor {
            override fun isDropAcceptable(a_component: DnDTabbedPane?, a_index: Int): Boolean {
                return true
            }
        }
    }

    override fun actionPerformed(p0: ActionEvent?) {
        val selected: Component = selectedComponent
        remove(selected)
    }
}

internal class GhostGlassPane : JPanel() {
    private val mComposite: AlphaComposite
    private val mLocation = Point(0, 0)
    private var mDraggingGhost: BufferedImage? = null
    fun setImage(draggingGhost: BufferedImage?) {
        mDraggingGhost = draggingGhost
    }

    fun setPoint(a_location: Point) {
        mLocation.x = a_location.x
        mLocation.y = a_location.y
    }

    // if
    val ghostWidth: Int
        get() = if (mDraggingGhost == null) {
            0
        } else mDraggingGhost!!.getWidth(this) // if

    // if
    val ghostHeight: Int
        get() = if (mDraggingGhost == null) {
            0
        } else mDraggingGhost!!.getHeight(this) // if

    public override fun paintComponent(g: Graphics) {
        if (mDraggingGhost == null) {
            return
        } // if
        val g2 = g as Graphics2D
        g2.composite = mComposite
        g2.drawImage(mDraggingGhost, mLocation.getX().toInt(), mLocation.getY().toInt(), null)
    }

    companion object {
        const val serialVersionUID = 1L
    }

    init {
        isOpaque = false
        mComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)
    }
}