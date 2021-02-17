/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 *
 * @author 2014130020
 */
public class Transfer implements Transferable {

    public static final DataFlavor stringFlavor = new DataFlavor(String.class, "String");
    
    private String info;

    public Transfer(String info) {
        this.info = info;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(stringFlavor)) {
            return true;
        }
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) {
        if (isDataFlavorSupported(flavor)) {
            return info;
        }
        return null;
    }
}
