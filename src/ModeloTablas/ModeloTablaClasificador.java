/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ModeloTablas;

import Guardador.Guardador;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author gastr
 */
public class ModeloTablaClasificador extends AbstractTableModel {

    private int[][] tabla;
    private final String titulos[] = {"Legajo", "Imagenes"};
    private final Class[] tipoColumnas = {int.class, String.class};

    public ModeloTablaClasificador() {
        Guardador conexion = new Guardador();
        tabla = conexion.getCantidadDeImagenesLegajo();
    }

    @Override
    public int getRowCount() {
        return tabla[0].length;
    }

    @Override
    public int getColumnCount() {
        return titulos.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return tabla[rowIndex][columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return tipoColumnas[columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return titulos[column];
    }

    
}
