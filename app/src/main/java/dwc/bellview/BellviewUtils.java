package dwc.bellview;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 * @author Douglas Chesher
 */
public class BellviewUtils {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("dwc.bellview.messages");

    public static enum OS {
        LINUX, MAC, WINDOWS
    };

    public BellviewUtils() {
    }

    public static String getMessage(String key) {
        return resourceBundle.getString(key);
    }

    public static String getMessage(String key, Object... obj) {
        return MessageFormat.format(resourceBundle.getString(key), obj);
    }
    
    public static String numericToString(Double val, Integer precision) {
    	MathContext ctx = new MathContext(precision);
    	BigDecimal num = new BigDecimal(val);
		BigDecimal rounded = num.round(ctx);
		String str;
		if (rounded.scale()>0) {
			str = rounded.toString();
		} else {
			BigInteger intVal = rounded.toBigInteger();
			str = String.valueOf(intVal.intValue());
		}
		return str;
    }

    /**
     * Print a two dimensional array to string.
     *
     * @param arr Array to be converted to a String representation
     * @return String representation of the array.
     */
    public static String arrayToString(double[][] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (j > 0) {
                    sb.append("\t");
                }
                sb.append(arr[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static OS getOS() {
        String vers = System.getProperty("os.name").toLowerCase();
        if (vers.indexOf("linux") != -1) {
            return OS.LINUX;
        } else if (vers.indexOf("mac") != -1) {
            return OS.MAC;
        } else {
            return OS.WINDOWS;
        }
    }

    public static String getFilenameExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int start;
            if (filename.length() > 5)
                start = filename.length() - 5;
            else 
                start = 0;
            int startPos = filename.indexOf('.', start)+1;
            String extn = filename.substring(startPos, filename.length());
            return extn;
        }
    }
}
