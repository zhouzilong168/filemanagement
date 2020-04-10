package frame;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class JTableModel extends AbstractTableModel {

    private Vector<Object> content = null;

    private String[] columnName = null;

    private Object[][] cells;

    private int rowIndex = -1;
    private int columnIndex = -1;

    public JTableModel(final String[] columnName, final Vector content) {
        this.columnName = columnName;
        this.content = content;
    }

    public void updateContent(final Vector content) {
        this.content = content;
    }

    public Vector getContent() {
        return this.content;
    }

    public JTableModel(final String[] columnName, final Object[][] cells) {

        this.content = new Vector();

        this.cells = cells;
        this.columnName = columnName;
        for (int i = 0; i < cells.length; i++) {
            Vector v = new Vector();
            for (int j = 0; j < cells[i].length; j++) {
                v.add(j, cells[i][j]);
            }
            this.content.add(i, v);
        }
    }

    public void addRow() {
        Vector v = new Vector(this.columnName.length);
        v.add(false);
        for (int i = 0; i < this.columnName.length; i++) {
            v.add("");
        }
        this.content.add(v);
    }

    public void addRow(Vector<Vector<Object>> v) {
//    	this.content.add(v);
        for (int i = 0; i < v.size(); i++) {
            for (int j = 0; j < v.get(i).size(); j++) {
                this.content.add(j, v.get(i).get(j));
            }
        }
    }


    public void removeRow(final int row) {
        this.content.remove(row);
    }

    public void removeRows(final int row, final int count) {
        for (int i = 0; i < count; i++) {
            if (this.content.size() > row) {
                this.content.remove(row);
            }
        }
    }

    /**
     *
     */
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if (this.rowIndex == rowIndex || columnIndex == 0) {
            return true;
        } else {
            return false;
        }
        // if (columnIndex == 0) {
        // return false;
        // }
        // return true;
        // return columnIndex == 0;
    }

    public void setValueAt(final Object value, final int row, final int col) {
        if (content.size() > 0) {
            ((Vector) this.content.get(row)).remove(col);
            ((Vector) this.content.get(row)).add(col, value);
        }
        // this.fireTableCellUpdated(row, col);
    }

    public String getColumnName(final int col) {
        return this.columnName[col];
    }

    public int getColumnCount() {
        if (columnName.length > 0) {
            return this.columnName.length;
        }
        return 0;
    }

    public int getRowCount() {
        if (content.size() > 0) {
            return this.content.size();
        }
        return 0;
    }

    public Object getValueAt(final int row, final int col) {
        if (content.size() > 0) {
            Vector verctor = (Vector) this.content.get(row);
            return verctor.get(col);
        }
        return 0;
    }

    /**
     *
     */
    public Class getColumnClass(final int col) {
        if (getValueAt(0, col) != null) {
            return getValueAt(0, col).getClass();
        } else {
            return String.class;
        }
    }

    public void selectAllOrNull(final boolean value) {
        for (int i = 0; i < getRowCount(); i++) {
            this.setValueAt(value, i, 0);
        }
    }

    public void removeRows(final int[] row) {
        for (int i = row.length - 1; i >= 0; i--) {
            this.content.remove(row[i]);
        }
    }

    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return this.rowIndex;
    }

    /**
     * @param rowIndex the rowIndex to set
     */
    public void setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * @return the columnIndex
     */
    public int getColumnIndex() {
        return this.columnIndex;
    }

    /**
     * @param columnIndex the columnIndex to set
     */
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }
}
