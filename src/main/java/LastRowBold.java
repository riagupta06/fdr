import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class LastRowBold extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel parent = (JLabel) super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        if(row == table.getRowCount()-1) parent.setFont(
                parent.getFont().deriveFont(Font.BOLD));
        return parent;
    }
}
