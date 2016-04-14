/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import Controladores.CtrlInterfazClasificar;
import ModeloTablas.ModeloTablaClasificador;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.opencv.core.Mat;

/**
 *
 * @author gastr
 */
public class InterfazClasificar extends javax.swing.JFrame {

    private CtrlInterfazClasificar ctrlInterfazClasificar;

    /**
     * Creates new form InterfazClasificar
     */
    public InterfazClasificar() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCamara = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaImagenesClasificador = new javax.swing.JTable();
        lblCaraEncontrada = new javax.swing.JLabel();
        btnNuevaPersona = new javax.swing.JButton();
        btnEliminarSeleccionado = new javax.swing.JButton();
        lblValidacion = new javax.swing.JLabel();
        txtCantidadImagenes = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtLegajo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblCamara.setText("Camara");

        tablaImagenesClasificador.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Legajo", "Imagenes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaImagenesClasificador);

        lblCaraEncontrada.setText("Cara Encontrada");

        btnNuevaPersona.setText("Nueva Persona");
        btnNuevaPersona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaPersonaActionPerformed(evt);
            }
        });

        btnEliminarSeleccionado.setText("Eliminar Seleccionado");
        btnEliminarSeleccionado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSeleccionadoActionPerformed(evt);
            }
        });

        lblValidacion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblValidacion.setForeground(new java.awt.Color(255, 0, 0));

        jLabel1.setText("Cantidad de imagenes");

        jLabel2.setText("Legajo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCaraEncontrada, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblValidacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCamara, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(btnNuevaPersona, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminarSeleccionado, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(txtCantidadImagenes)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtLegajo))))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLegajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantidadImagenes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevaPersona)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCamara, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarSeleccionado))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCaraEncontrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblValidacion, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 458, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNuevaPersonaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaPersonaActionPerformed
        this.limpiarInterfaz();
        if (!"".equals(this.txtCantidadImagenes.getText())) {
            this.ctrlInterfazClasificar.nuevaPersona(Integer.valueOf(this.txtCantidadImagenes.getText()),
                    this.txtLegajo.getText());
        }
        else{
            this.ctrlInterfazClasificar.nuevaPersona(0,this.txtLegajo.getText());
        }
    }//GEN-LAST:event_btnNuevaPersonaActionPerformed

    private void btnEliminarSeleccionadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSeleccionadoActionPerformed
        this.limpiarInterfaz();
        this.ctrlInterfazClasificar.eliminarCarasClasificador(
                (int) this.tablaImagenesClasificador.getValueAt(this.tablaImagenesClasificador.getSelectedRow(), 0));
    }//GEN-LAST:event_btnEliminarSeleccionadoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InterfazClasificar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazClasificar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazClasificar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazClasificar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InterfazClasificar().setVisible(true);
            }
        });
    }

    public void setControlador(CtrlInterfazClasificar ctrlInterfazClasificar) {
        this.ctrlInterfazClasificar = ctrlInterfazClasificar;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminarSeleccionado;
    private javax.swing.JButton btnNuevaPersona;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCamara;
    private javax.swing.JLabel lblCaraEncontrada;
    private javax.swing.JLabel lblValidacion;
    private javax.swing.JTable tablaImagenesClasificador;
    private javax.swing.JTextField txtCantidadImagenes;
    private javax.swing.JTextField txtLegajo;
    // End of variables declaration//GEN-END:variables

    public void setLabelValidacion(String validacion) {
        this.lblValidacion.setText(validacion);
    }

    public void setTxtCantidadImagenes(String string) {
        this.txtCantidadImagenes.setText(string);
    }

    public void setModeloTabla(ModeloTablaClasificador modeloTabla) {
        this.tablaImagenesClasificador.setModel(modeloTabla);
    }
    
    private void limpiarInterfaz(){
        this.setLabelValidacion("");
        this.setTxtCantidadImagenes("");
        this.txtLegajo.setText("");
        this.lblCamara.setIcon(null);
        this.lblCaraEncontrada.setIcon(null);
    }
    
    public void agregarImagenLabel(Image imagen, JLabel label) {
        ImageIcon icon = new ImageIcon(imagen.getScaledInstance(label.getWidth(), label.getHeight(),
                Image.SCALE_SMOOTH));
        label.setIcon(icon);
    }

    public void setLblImagenCamara(Image imagen) {
        this.agregarImagenLabel(imagen, this.lblCamara);
    }

    public void setLblImagenEncontrada(Image imagen) {
        this.agregarImagenLabel(imagen, this.lblCaraEncontrada);
    }

}