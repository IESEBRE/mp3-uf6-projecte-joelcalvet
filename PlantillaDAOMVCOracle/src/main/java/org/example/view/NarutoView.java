package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class NarutoView extends JFrame{
    private JTabbedPane pestanyes;
    private JTable taula;
    private JScrollPane scrollPane1;
    private JButton insertarButton;
    private JButton modificarButton;
    private JButton borrarButton;
    private JTextField campNom;
    private JTextField campAnys;
    private JCheckBox caixaVius;
    private JPanel panel;
    private JTable taulaPoder;
    private JComboBox comboChakra;
    private JTextField campChakra;
    //private JTabbedPane PanelPestanya;

    //Getters


    public JTable getTaulaPoder() {
        return taulaPoder;
    }

    public JComboBox getComboChakra() {
        return comboChakra;
    }

    public JTextField getCampChakra() {
        return campChakra;
    }

    public JTabbedPane getPestanyes() {
        return pestanyes;
    }

    public JTable getTaula() {
        return taula;
    }

    public JButton getBorrarButton() {
        return borrarButton;
    }

    public JButton getModificarButton() {
        return modificarButton;
    }

    public JButton getInsertarButton() {
        return insertarButton;
    }

    public JTextField getCampNom() {
        return campNom;
    }

    public JTextField getCampAnys() {
        return campAnys;
    }

    public JCheckBox getCaixaVius() {
        return caixaVius;
    }


    //Constructor de la classe
    public NarutoView() {


        //Per poder vore la finestra
        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(false);
    }

        private void createUIComponents() {
        // TODO: place custom component creation code here
        scrollPane1 = new JScrollPane();
        taula = new JTable();
        pestanyes = new JTabbedPane();
        taula.setModel(new DefaultTableModel());
        taula.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane1.setViewportView(taula);

    }
}
