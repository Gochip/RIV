/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Deteccion;

import Guardador.Guardador;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author gastr
 */
public class ModeloTablaPersonas extends AbstractTableModel{
    private final ArrayList<Persona> listaPersonas;
    private final String titulos[] = {"Legajo","Nombre","Apellido"};
    private final Class[] tipoColumnas = {int.class,String.class,String.class};

    public ModeloTablaPersonas() {
        Guardador guardador = new Guardador();
        listaPersonas = guardador.getPersonas();
    }

    @Override
    public int getRowCount() {
        return listaPersonas.size();
    }

    @Override
    public int getColumnCount() {
        return titulos.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0 : return listaPersonas.get(rowIndex).getLegajo();
            case 1 : return listaPersonas.get(rowIndex).getNombre();
            case 2 : return listaPersonas.get(rowIndex).getApellido();
            default: return null;
        }
    }   
}