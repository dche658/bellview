
package dwc.bellview.file;

/**
 * 
 * @author Douglas Chesher
 */
public class ColumnHeader {
    private int index;
    private String label;

    public ColumnHeader(int index, String label) {
        this.index = index;
        this.label = label;
    }

    
    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ColumnHeader other = (ColumnHeader) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }
    
    
}
