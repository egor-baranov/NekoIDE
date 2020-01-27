import java.awt.*
import java.lang.reflect.Method
import java.util.*

/**
 * This class provides utilitary methods for Swing Material. These are public
 * and thus can be used directly.
 * @author DragShot
 */
object Utils {
    /**
     * A boolean flag for `getScreenSize()`, signaling if
     * `sun.java2d.SunGraphicsEnvironment.getUsableBounds()` is available
     * or not.<br></br><br></br>
     * Values:<br></br>
     *  * `true`: Class/method is available.
     *  * `false`: Class/method is not available.
     */
    private var useSun2D = false
    /**
     * If not `null`, this contains a reference to
     * `sun.java2d.SunGraphicsEnvironment.getUsableBounds()` via
     * Reflection.
     */
    private var getUsableBounds: Method? =
        null//Do it the traditional way//If something doesn't work, fallback to Toolkit//Use sun.java2d.SunGraphicsEnvironment.getUsableBounds()

    /**
     * Checks the area available in the desktop, excluding the taskbar.
     * In order to do this, an attempt to call
     * `sun.java2d.SunGraphicsEnvironment.getUsableBounds()` is
     * performed. If this can't be done, the method falls back to the default
     * `Toolkit.getDefaultToolkit().getScreenSize()`, although such
     * method doesn't exclude the taskbar area.
     * @return A Rectangle with the usable area for maximized windows.
     * @author DragShot
     */
    val screenSize: Rectangle?
        get() {
            var screen: Rectangle
            if (useSun2D) { //Use sun.java2d.SunGraphicsEnvironment.getUsableBounds()
                try {
                    val frame = Frame()
                    val config = frame.graphicsConfiguration
                    screen = getUsableBounds!!.invoke(null, config.device) as Rectangle
                    frame.dispose()
                } catch (ex: Exception) { //If something doesn't work, fallback to Toolkit
                    val size = Toolkit.getDefaultToolkit().screenSize
                    screen = Rectangle(0, 0, size.width, size.height)
                }
            } else { //Do it the traditional way
                val size = Toolkit.getDefaultToolkit().screenSize
                screen = Rectangle(0, 0, size.width, size.height)
            }
            return screen
        }//Don't even bother: window translucency doesn't exist before JDK 1.7

    /**
     * Checks if the translucency effect is supported. Java 6 does not support
     * this. Only Java 7 and higher VMs might do, depending of the Graphics
     * Environment and OS.
     * @return `true` if translucency is supported,
     * `false` otherwise.
     * @author DragShot
     */
    val isTranslucencySupported: Boolean
        get() {
            val nativeTrans: Boolean
            nativeTrans =
                if (System.getProperty("java.version").contains("1.6")) { //Don't even bother: window translucency doesn't exist before JDK 1.7
                    false
                } else {
                    GraphicsEnvironment
                        .getLocalGraphicsEnvironment().defaultScreenDevice
                        .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)
                }
            return nativeTrans
        }

    /**
     * Determines if a given [Color] is dark enough for white text to be
     * seen more easily than black text. This tries to stick to the Material
     * Color Guide as much as possible, and although two or three of the color
     * pairs doesn't match, the results are still good enough.
     *
     * @param color a [Color] to evaluate
     * @return `true` if the provided color is dark, `false`
     * otherwise.
     * @author DragShot
     */
    fun isDark(color: Color): Boolean { //return (color.getRed()*0.299 + color.getGreen()*0.587 + color.getBlue()*0.114) < (0.6*255);
//return (color.getRed() + color.getGreen() + color.getBlue())/3 < (0.63*255);
        return color.red * 0.2125 + color.green * 0.7154 + color.blue * 0.0721 < 0.535 * 255
        //return (color.getRed()*0.21 + color.getGreen()*0.72 + color.getBlue()*0.07) < (0.54*255);
    }

    /**
     * Utilitary method for getting a darker version of a provided Color. Unlike
     * [Color.darker], this decreases color at a fixed step instead of
     * a proportional.
     * @param color the original color
     * @return a [Color] sightly darker than the one input.
     */
    fun darken(color: Color): Color {
        val r = wrapU8B(color.red - 30)
        val g = wrapU8B(color.green - 30)
        val b = wrapU8B(color.blue - 30)
        return Color(r, g, b, color.alpha)
    }

    /**
     * Utilitary method for getting a darker version of a provided Color. Unlike
     * [Color.brighter], this increases color at a fixed step instead of
     * a proportional.
     * @param color the original color
     * @return a [Color] sightly brighter than the one input.
     */
    fun brighten(color: Color): Color {
        val r = wrapU8B(color.red + 30)
        val g = wrapU8B(color.green + 30)
        val b = wrapU8B(color.blue + 30)
        return Color(r, g, b, color.alpha)
    }

    private fun wrapU8B(i: Int): Int {
        return Math.min(255, Math.max(0, i))
    }

    /**
     * Utilitary method for getting a copy of a provided Color but using an
     * specific opacity mask. Intented for use within the library.
     * @param color   the color to use as base
     * @param bitMask the bitmask to apply, where the bits 25 to 32 are used
     * @return a copy of the given color, with a modified alpha value
     */
    fun applyAlphaMask(color: Color, bitMask: Int): Color {
        return Color(color.rgb and 0x00FFFFFF or (bitMask and -0x1000000), true)
    } //Uncomment this block in order to test #isDark() against all the color constants in Material Color

    /*public static void main(String[] args) {
        Field[] fields = MaterialColor.class.getDeclaredFields();
        for (Field field:fields) {
            if (Modifier.isStatic(field.getModifiers()) &&
                    Color.class.isAssignableFrom(field.getType())) {
                try {
                    System.out.println(field.getType().getName()+" "+field.getName()+(isDark((Color)field.get(null)) ? " is dark":" is light"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }*/
    init { //Check if sun.java2d.SunGraphicsEnvironment.getUsableBounds()
//is available.
        var found = false
        var getMethod: Method? = null
        try {
            val sunGE = Class.forName("sun.java2d.SunGraphicsEnvironment")
            val meths = sunGE.declaredMethods
            for (meth in meths) {
                if (meth.name == "getUsableBounds" && Arrays.equals(
                        meth.parameterTypes, arrayOf<Class<*>>(
                            GraphicsDevice::class.java
                        )
                    )
                    && meth.exceptionTypes.size == 0 && (meth.returnType
                            == Rectangle::class.java)
                ) { //We found it!
                    getMethod = meth
                    found = true
                    break
                }
            }
        } catch (ex: ClassNotFoundException) { //It seems not
            found = false
        }
        useSun2D = found
        getUsableBounds = getMethod
    }
}