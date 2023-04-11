package dwc.bellview;

import java.text.NumberFormat;

/** Provides static methods for display results to the desired level of precision.
 * This still needs some work as it isn't quite right.
 *
 * @author Douglas Chesher
 */
public class SignificantFigures {
	/** Creates a new instance of SignificantFigures */
    public SignificantFigures() {
    }
    
    public static String format(double v, int significantFigures) {
        String str;
        int corr = ((int)(Math.log10(Math.abs(v))));
        
        int decimalFigures = significantFigures - corr;
        if (decimalFigures < 0) decimalFigures = 0;
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(decimalFigures);
        str = nf.format(v);
        //System.out.println("SigFig "+significantFigures+" Max fraction "+decimalFigures+" c="+c+" corr="+corr+" :"+str);
        return str;
    }
    public static void main(String[] args) {
        System.out.println(SignificantFigures.format(1230,3));
        System.out.println(SignificantFigures.format(1230,4));
        System.out.println(SignificantFigures.format(1230,10));
        System.out.println(SignificantFigures.format(1230.23,3));
        System.out.println(SignificantFigures.format(30.233264,3));
        System.out.println(SignificantFigures.format(0.2345,3));
        System.out.println(SignificantFigures.format(0.0345,3));
        System.out.println(SignificantFigures.format(0.003456548,4));
        
    }
}
