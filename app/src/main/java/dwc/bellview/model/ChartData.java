package dwc.bellview.model;

/**
*
* @author Douglas Chesher
*/
public class ChartData {

   public static final int X = 0;
   public static final int Y = 1;
   private double[][] dataSeries1 = null;
   private double[][] dataSeries2 = null;
   private Double domainAxisLowerBound = 0.0;
   private Double domainAxisUpperBound = 1.0;
   private Integer regressionStartIndex = 0;
   private Integer regressionEndIndex = 0;

   public ChartData(Integer numberOfBins) {
       dataSeries1 = new double[2][numberOfBins];
       dataSeries2 = new double[2][1];
       dataSeries2[X][0] = 0;
       dataSeries2[Y][0] = 0;
   }

   /**
    * @return the dataSeries1
    */
   public double[][] getDataSeries1() {
       return dataSeries1;
   }

   /**
    * @param dataSeries1 the dataSeries1 to set
    */
   public void setDataSeries1(double[][] dataSeries1) {
       this.dataSeries1 = dataSeries1;
   }

   /**
    * @return the dataSeries2
    */
   public double[][] getDataSeries2() {
       return dataSeries2;
   }

   /**
    * @param dataSeries2 the dataSeries2 to set
    */
   public void setDataSeries2(double[][] dataSeries2) {
       this.dataSeries2 = dataSeries2;
   }

   /**
    * @return the domainAxisLowerBound
    */
   public Double getDomainAxisLowerBound() {
       return domainAxisLowerBound;
   }

   /**
    * @param domainAxisLowerBound the domainAxisLowerBound to set
    */
   public void setDomainAxisLowerBound(Double domainAxisLowerBound) {
       this.domainAxisLowerBound = domainAxisLowerBound;
   }

   /**
    * @return the domainAxisUpperBound
    */
   public Double getDomainAxisUpperBound() {
       return domainAxisUpperBound;
   }

   /**
    * @param domainAxisUpperBound the domainAxisUpperBound to set
    */
   public void setDomainAxisUpperBound(Double domainAxisUpperBound) {
       this.domainAxisUpperBound = domainAxisUpperBound;
   }

   /**
    * @return the regressionStartIndex
    */
   public Integer getRegressionStartIndex() {
       return regressionStartIndex;
   }

   /**
    * @param regressionStartIndex the regressionStartIndex to set
    */
   public void setRegressionStartIndex(Integer regressionStartIndex) {
       this.regressionStartIndex = regressionStartIndex;
   }

   /**
    * @return the regressionEndIndex
    */
   public Integer getRegressionEndIndex() {
       return regressionEndIndex;
   }

   /**
    * @param regressionEndIndex the regressionEndIndex to set
    */
   public void setRegressionEndIndex(Integer regressionEndIndex) {
       this.regressionEndIndex = regressionEndIndex;
   }

   @Override
   public String toString() {
       StringBuilder sb = new StringBuilder("ChartData:");
       sb.append("[Series 1:\n");
       for (int j = 0; j < this.getDataSeries1()[0].length; j++) {
           if (j == 0) {
               sb.append("[");
           } else {
               sb.append(",[");
           }
           sb.append(this.getDataSeries1()[0][j]).append(",");
           sb.append(this.getDataSeries1()[1][j]);
           sb.append("]\n");
       }
       sb.append("]\n, [Series 2:\n");
       for (int j = 0; j < this.getDataSeries2()[0].length; j++) {
           if (j == 0) {
               sb.append("[");
           } else {
               sb.append(",[");
           }
           sb.append(this.getDataSeries2()[0][j]).append(",");
           sb.append(this.getDataSeries2()[1][j]);
           sb.append("]\n");
       }
       sb.append("]\n");
       sb.append(", Domain_Lo=").append(this.getDomainAxisLowerBound());
       sb.append(", Domain_Hi=").append(this.getDomainAxisUpperBound());
       return sb.toString();
   }
}
