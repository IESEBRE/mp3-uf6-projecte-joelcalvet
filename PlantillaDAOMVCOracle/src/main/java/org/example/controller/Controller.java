package org.example.controller;

import org.example.model.entities.Ninja;
import org.example.model.exceptions.DAOException;
import org.example.model.entities.Ninja.Poder;
import org.example.view.ModelComponentsVisuals;
import org.example.model.impls.NinjaDAOJDBCOracleImpl;
import org.example.view.NarutoView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements PropertyChangeListener {

    private ModelComponentsVisuals modelComponentsVisuals = new ModelComponentsVisuals();
    private NinjaDAOJDBCOracleImpl dadesNinja;
    private NarutoView view;

    /**
     * Constructor de la classe
     *
     * @param dadesNinja Implementació de la interfície DAO per a interactuar amb la BD
     * @param view       Vista de l'aplicació
     */
    public Controller(NinjaDAOJDBCOracleImpl dadesNinja, NarutoView view) {
        this.dadesNinja = dadesNinja;
        this.view = view;
        canvis.addPropertyChangeListener(this); // Afegir el listener per a les excepcions
        lligaVistaModel();
        afegirListeners();
        view.setVisible(true);
    }

    /**
     * Mètode per a lligar la vista amb el model
     */
    private void lligaVistaModel() {
        try {
            setModelTaulaNinja(modelComponentsVisuals.getModelTaulaNinja(), dadesNinja.getAll()); // Omplir la taula de Ninjas amb les dades de la BD
        } catch (DAOException e) {
            this.setExcepcio(e);
        }
        JTable taula = view.getTaula();
        taula.setModel(this.modelComponentsVisuals.getModelTaulaNinja());
        taula.getColumnModel().getColumn(3).setMinWidth(0);
        taula.getColumnModel().getColumn(3).setMaxWidth(0);
        taula.getColumnModel().getColumn(3).setPreferredWidth(0);
        JTable taulaPoder = view.getTaulaPoder();
        taulaPoder.setModel(this.modelComponentsVisuals.getModelTaulaPoder());
        view.getComboChakra().setModel(modelComponentsVisuals.getComboBoxModel());
        view.getPestanyes().setEnabledAt(1, false);
        view.getPestanyes().setTitleAt(1, "Poder de ...");
    }

    /**
     * Mètode per a actualitzar la taula de Ninjas
     *
     * @param modelTaulaNinja Model de la taula de Ninjas
     * @param all             Llista de tots els Ninjas
     */
    private void setModelTaulaNinja(DefaultTableModel modelTaulaNinja, List<Ninja> all) {
        modelTaulaNinja.setRowCount(0); // Clear existing rows
        for (Ninja ninja : all) {
            Set<Poder> poders = (Set<Poder>) ninja.getPoder(); // Obtenir els poders directament de l'objecte Ninja
            if (poders == null) {
                System.out.println("Error: No s'han pogut obtenir els poders per al ninja amb ID " + ninja.getId());
                continue;
            }
            modelTaulaNinja.addRow(new Object[]{ninja.getNom(), ninja.getAnys(), ninja.isViu(), ninja, poders});
        }
        SwingUtilities.invokeLater(() -> {
            view.getTaula().setModel(modelTaulaNinja);
            view.getTaula().repaint();
        });
    }

    /**
     * Mètode per a afegir listeners als botons de la vista
     */
    private void afegirListeners() {
        ModelComponentsVisuals modelo = this.modelComponentsVisuals;
        DefaultTableModel model = modelo.getModelTaulaNinja();
        DefaultTableModel modelPoder = modelo.getModelTaulaPoder();
        JTable taula = view.getTaula();
        JTable taulaPoder = view.getTaulaPoder();
        JTextField campNom = view.getCampNom();
        JTextField campAnys = view.getCampAnys();
        JCheckBox caixaVius = view.getCaixaVius();
        JTabbedPane pestanyes = view.getPestanyes();
        SwingUtilities.invokeLater(this::refrescaTaulaNinjas);

        view.getInsertarButton().addActionListener(e -> {
            List<DAOException> excepcions = new ArrayList<>();

            if (pestanyes.getSelectedIndex() == 0) {
                if (campNom.getText().isBlank() || campAnys.getText().isBlank()) {
                    excepcions.add(new DAOException(13));
                } else {
                    try {
                        NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
                        double any = 0;
                        try {
                            any = num.parse(campAnys.getText().trim()).doubleValue();
                        } catch (ParseException ex) {
                            excepcions.add(new DAOException(16));
                        }

                        if (any < 0 || any > 99) excepcions.add(new DAOException(3));

                        if (campNom.getText().length() > 20) excepcions.add(new DAOException(14));
                        if (!campNom.getText().matches("[a-zA-Z0-9 ]+")) excepcions.add(new DAOException(15));

                        int selectedRow = view.getTaula().getSelectedRow();

                        TreeSet<Poder> existingPowers = null; // Inicialitzar a null per defecte
                        if (selectedRow == 1) {
                            Ninja selectedNinja = (Ninja) model.getValueAt(selectedRow, 3);
                            existingPowers = (TreeSet<Poder>) selectedNinja.getPoder();
                        }

                        if (excepcions.isEmpty()) {
                            Ninja al = new Ninja(campNom.getText(), any, caixaVius.isSelected(), existingPowers);
                            dadesNinja.save(al);
                            SwingUtilities.invokeLater(this::refrescaTaulaNinjas);
                            campNom.setText("Naruto Uzumaki");
                            campNom.setSelectionStart(0);
                            campNom.setSelectionEnd(campNom.getText().length());
                            campAnys.setText("33");
                            campNom.requestFocus();
                        }
                    } catch (DAOException ex) {
                        excepcions.add(ex);
                    }
                }
            } else {
                int selectedRow = taula.getSelectedRow();

                Ninja selectedNinja = (Ninja) model.getValueAt(selectedRow, 3);

                Poder.TipusChakra selectedChakra = (Poder.TipusChakra) view.getComboChakra().getSelectedItem();

                // Comprova si el nom del poder ja existeix a la taula
                for (int i = 0; i < modelPoder.getRowCount(); i++) {
                    Poder.TipusChakra chakraTaula = (Poder.TipusChakra) modelPoder.getValueAt(i, 0);
                    if (chakraTaula.equals(selectedChakra)) {
                        excepcions.add(new DAOException(20)); // Excepció per a noms de poder duplicats
                        break;
                    }
                }

                String chakraText = view.getCampChakra().getText();
                if (chakraText.isEmpty()) {
                    excepcions.add(new DAOException(21));
                }

                int quantitatChakra = 0;
                try {
                    quantitatChakra = Integer.parseInt(chakraText);
                    if (quantitatChakra < 0 || quantitatChakra > 10000) {
                        excepcions.add(new DAOException(19));
                    }
                } catch (NumberFormatException ex) {
                    excepcions.add(new DAOException(18));
                }

                if (excepcions.isEmpty()) {
                    Ninja.Poder newPoder = new Ninja.Poder(selectedChakra, quantitatChakra);
                    selectedNinja.getPoder().add(newPoder);
                    try {
                        dadesNinja.savePoder(newPoder, selectedNinja.getId());
                    } catch (DAOException sqlException) {
                        excepcions.add(sqlException);
                    }
                    ompliPoder(selectedNinja, modelPoder, dadesNinja);
                }
            }

            if (!excepcions.isEmpty()) {
                for (DAOException ex : excepcions) {
                    setExcepcio(ex);
                }
            }
        });


        view.getModificarButton().addActionListener(e -> {
            List<DAOException> excepcions = new ArrayList<>();

            int filaSel = view.getTaula().getSelectedRow();
            if (filaSel != -1) {
                Ninja ninja = (Ninja) model.getValueAt(filaSel, 3);
                if (view.getPestanyes().getSelectedIndex() == 0) {
                    // Modifica el Ninja
                    if (campNom.getText().isBlank() || campAnys.getText().isBlank()) {
                        excepcions.add(new DAOException(13));
                    } else {
                        try {
                            NumberFormat num = NumberFormat.getNumberInstance(Locale.getDefault());
                            double any = num.parse(campAnys.getText().trim()).doubleValue();
                            if (any < 0 || any > 99) excepcions.add(new DAOException(3));

                            if (campNom.getText().length() > 20) excepcions.add(new DAOException(14));
                            if (!campNom.getText().matches("[a-zA-Z0-9 ]+")) excepcions.add(new DAOException(15));

                            ninja.setNom(view.getCampNom().getText());
                            ninja.setAnys(Double.parseDouble(view.getCampAnys().getText().replaceAll(",", ".")));
                            ninja.setViu(view.getCaixaVius().isSelected());

                            if (excepcions.isEmpty()) {
                                dadesNinja.update(ninja); // actualitza el ninja a la BD amb el nou valor
                                SwingUtilities.invokeLater(this::refrescaTaulaNinjas); // Refresca la taula en un nou fil d'execució
                            }
                        } catch (ParseException ex) {
                            excepcions.add(new DAOException(16));
                        } catch (DAOException daoException) {
                            excepcions.add(daoException);
                        }
                    }
                } else {
                    // Modifica Poder
                    int filaSelPoder = view.getTaulaPoder().getSelectedRow();
                    if (filaSelPoder != -1) {
                        Poder.TipusChakra selectedChakra = (Poder.TipusChakra) view.getComboChakra().getSelectedItem();
                        String chakraText = view.getCampChakra().getText();
                        if (chakraText.isEmpty()) {
                            excepcions.add(new DAOException(21));
                        }

                        int quantitatChakra = 0;
                        try {
                            quantitatChakra = Integer.parseInt(chakraText);
                            if (quantitatChakra < 0 || quantitatChakra > 10000) {
                                excepcions.add(new DAOException(19));
                            }
                        } catch (NumberFormatException ex) {
                            excepcions.add(new DAOException(18));
                        }

                        if (excepcions.isEmpty()) {
                            Set<Poder> poderList;
                            try {
                                poderList = dadesNinja.getPoders(ninja, ninja.getId());
                            } catch (SQLException | DAOException ex) {
                                throw new RuntimeException(ex);
                            }

                            Poder poderToModify = poderList.stream().collect(Collectors.toList()).get(filaSelPoder);
                            if (poderToModify != null) {
                                poderToModify.setTipusChakra(selectedChakra);
                                poderToModify.setQuantitatChakra(quantitatChakra);
                                try {
                                    dadesNinja.updatePoder(poderToModify, ninja.getId()); // actualitza el poder a la BD amb el nou valor
                                    ompliPoder(ninja, modelPoder, dadesNinja);
                                } catch (DAOException daoException) {
                                    excepcions.add(daoException);
                                }
                            } else {
                                excepcions.add(new DAOException(22)); // Excepció per poder no trobat
                            }
                        }
                    } else {
                        excepcions.add(new DAOException(23)); // Excepció per cap poder seleccionat per modificar
                    }
                }
            } else {
                excepcions.add(new DAOException(24)); // Excepció per cap Ninja seleccionat per modificar
            }

            if (!excepcions.isEmpty()) {
                for (DAOException ex : excepcions) {
                    setExcepcio(ex);
                }
            }
        });



        view.getBorrarButton().addActionListener(e -> {
            if (pestanyes.getSelectedIndex() == 0) {
                int filaSel = view.getTaula().getSelectedRow();
                if (filaSel != -1) {
                    Ninja ninja = (Ninja) modelComponentsVisuals.getModelTaulaNinja().getValueAt(filaSel, 3);
                    try {
                        dadesNinja.delete(ninja);
                        SwingUtilities.invokeLater(this::refrescaTaulaNinjas); // Refresca la taula en un nou fil d'execució
                    } catch (DAOException daoException) {
                        JOptionPane.showMessageDialog(null, daoException.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Has de seleccionar un Ninja per a borrar-lo.");
                }
            } else {
                int filaSel = view.getTaulaPoder().getSelectedRow();
                if (filaSel != -1) {
                    int filaSelNinja = view.getTaula().getSelectedRow();
                    Ninja ninja = (Ninja) modelComponentsVisuals.getModelTaulaNinja().getValueAt(filaSelNinja, 3);
                    Set<Poder> poderList;
                    try {
                        poderList = dadesNinja.getPoders(ninja, ninja.getId());
                    } catch (SQLException | DAOException ex) {
                        throw new RuntimeException(ex);
                    }
                    Poder poderToDelete = poderList.stream().collect(Collectors.toList()).get(filaSel);
                    if (poderToDelete != null) {
                        ninja.getPoder().remove(poderToDelete);
                        try {
                            dadesNinja.deletePoder(poderToDelete, ninja.getId()); // Borra el poder de la BD
                            ompliPoder(ninja, modelPoder, dadesNinja); // Refresca la taula de poders
                        } catch (DAOException daoException) {
                            JOptionPane.showMessageDialog(null, daoException.getMessage());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No s'ha trobat cap poder per a borrar.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Has de seleccionar un Poder per a borrar-lo.");
                }
            }
        });

        taula.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaSel = taula.getSelectedRow();
                if (filaSel != -1) {
                    Ninja ninja = (Ninja) model.getValueAt(filaSel, 3);
                    campNom.setText(ninja.getNom());
                    campAnys.setText(String.valueOf(ninja.getAnys()).replace(".", ","));
                    caixaVius.setSelected(ninja.isViu());
                    view.getPestanyes().setEnabledAt(1, true);
                    view.getPestanyes().setTitleAt(1, "Poder de " + campNom.getText());
                    ompliPoder(ninja, modelPoder, dadesNinja); // Omplir la taula de poders amb els poders del ninja seleccionat
                } else {
                    campNom.setText("");
                    campAnys.setText("");
                    view.getPestanyes().setEnabledAt(1, false);
                    view.getPestanyes().setTitleAt(1, "Poder de ...");
                }
            }
        });
    }

    /**
     * Mètode per a refrescar la taula de Ninjas
     */
    private void refrescaTaulaNinjas() {
        try {
            setModelTaulaNinja(modelComponentsVisuals.getModelTaulaNinja(), dadesNinja.getAll()); // Omplir la taula de Ninjas amb les dades de la BD
        } catch (DAOException e) {
            setExcepcio(e);
        }
    }

    /**
     * Mètode per a omplir la taula de poders amb els poders del ninja seleccionat
     *
     * @param ni        Ninja seleccionat
     * @param modelPoder Model de la taula de poders
     * @param dadesNinja Implementació de la interfície DAO per a interactuar amb la BD
     */
    private static void ompliPoder(Ninja ni, DefaultTableModel modelPoder, NinjaDAOJDBCOracleImpl dadesNinja) {
        if (ni == null) return; // Comprovació de nul·litat
        modelPoder.setRowCount(0);
        try {
            Set<Poder> poders = dadesNinja.getPoders(ni, ni.getId());
            for (Poder poder : poders) {
                modelPoder.addRow(new Object[]{poder.getTipusChakra(), poder.getQuantitatChakra()});
            }
        } catch (DAOException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String PROP_EXCEPCIO = "excepcio";
    private DAOException excepcio;

    public DAOException getExcepcio() {
        return excepcio;
    }

    /**
     * Setter de l'excepció
     *
     * @param excepcio Excepció a establir
     */
    public void setExcepcio(DAOException excepcio) {
        DAOException valorVell = this.excepcio;
        this.excepcio = excepcio;
        canvis.firePropertyChange(PROP_EXCEPCIO, valorVell, excepcio);
    }

    PropertyChangeSupport canvis = new PropertyChangeSupport(this);

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        DAOException rebuda = (DAOException) evt.getNewValue();
        try {
            throw rebuda;
        } catch (DAOException e) {
            switch (evt.getPropertyName()) {
                case PROP_EXCEPCIO:
                    switch (rebuda.getTipo()) {
                        case 0:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            System.exit(1);
                            break;
                        case 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 904, 936, 942, 1000, 1400, 1403, 1722, 1747, 4091, 6502, 12154, 2291, 1407, 12899, 2290, 2292, 17008, 17002:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            break;
                        case 2:
                            JOptionPane.showMessageDialog(null, rebuda.getMessage());
                            view.getCampNom().setSelectionStart(0);
                            view.getCampNom().setSelectionEnd(view.getCampNom().getText().length());
                            view.getCampNom().requestFocus();
                            break;
                    }
            }
        }
    }
}
