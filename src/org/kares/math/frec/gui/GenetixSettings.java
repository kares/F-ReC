
package org.kares.math.frec.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.kares.math.frec.core.GPModelGenetix;
import org.kares.math.frec.core.Genetix;

/**
 * Class <code> GenetixSettings </code> is an gui panel
 * which is used to set the parameters of genetic programming.
 * An instance of this class is the first think the user sees
 * when runing the application.
 */
public class GenetixSettings extends JPanel {
    
    private JLabel
        genSizeLabel, genMaxLabel, mutationProbabilityLabel, crossingProbabilityLabel,
        minFunctionLengthLabel, maxFunctionLengthLabel,
        reproductionProbabilityLabel, selectionProbabilityLabel, dataSizeLabel;
    
    private JTextField
        genSize, genMax, mutationProbability, crossingProbability,
        minFunctionLength, maxFunctionLength,
        reproductionProbability, selectionProbability, dataSize;
    
    private JCheckBox arbitraryMutations, arbitraryCrossings;
    private JComboBox modelNameCombo;
    private String selectedModelName;
    
    private ActionListener readyListener = null;
    private ActionListener cancelListener = null;

    public GenetixSettings() {
        buildUI();
    }       

    private GenetixSettings buildUI() {
        dataSizeLabel = new JLabel(" Training data size ");
        genSizeLabel = new JLabel(" Size of generation ");
        genMaxLabel = new JLabel(" Generation max     ");
        minFunctionLengthLabel = new JLabel(" Min f(x) length      ");
        maxFunctionLengthLabel = new JLabel(" Max f(x) length      ");
        mutationProbabilityLabel = new JLabel(" Mutation  probab.  ");
        crossingProbabilityLabel = new JLabel(" Crossing  probab.  ");
        reproductionProbabilityLabel = new JLabel(" Reproduct probab.");
        selectionProbabilityLabel = new JLabel(" Selection  probab. ");

        arbitraryMutations   = new JCheckBox(" Use arbitrary mutations ");
        arbitraryCrossings = new JCheckBox(" Use arbitrary crossings  ");

        EmptyBorder border = new EmptyBorder(5,0,5,2);

        dataSizeLabel.setBorder(border);
        genSizeLabel.setBorder(border);
        genMaxLabel.setBorder(border);
        minFunctionLengthLabel.setBorder(border);
        maxFunctionLengthLabel.setBorder(border);
        mutationProbabilityLabel.setBorder(border);
        crossingProbabilityLabel.setBorder(border);
        reproductionProbabilityLabel.setBorder(border);
        selectionProbabilityLabel.setBorder(border);

        dataSize = new JTextField("", 8);
        genSize = new JTextField("", 8);
        genMax = new JTextField("", 8);
        minFunctionLength = new JTextField("", 8);
        maxFunctionLength = new JTextField("", 8);
        mutationProbability = new JTextField("", 8);
        crossingProbability = new JTextField("", 8);
        reproductionProbability = new JTextField("", 8);
        selectionProbability = new JTextField("", 8);

        final JButton okay = new JButton("   OK   ");
        okay.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ( validateInput() ) {
                    //GenetixSettings.this.setVisible(false);
                    if (readyListener != null) readyListener.actionPerformed(e);
                }
            }
            
        });

        final JButton cancel = new JButton(" Cancel ");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //GenetixSettings.this.setVisible(false);
                if (cancelListener != null) cancelListener.actionPerformed(e);
            }

        });

        final Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(dataSizeLabel, c);
        panel.add(dataSizeLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(dataSize, c);
        panel.add(dataSize);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(genSizeLabel, c);
        panel.add(genSizeLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(genSize, c);
        panel.add(genSize);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(genMaxLabel, c);
        panel.add(genMaxLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(genMax, c);
        panel.add(genMax);
        /*
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(minFunctionLengthLabel, c);
        panel.add(minFunctionLengthLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(minFunctionLength, c);
        panel.add(minFunctionLength);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(maxFunctionLengthLabel, c);
        panel.add(maxFunctionLengthLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(maxFunctionLength, c);
        panel.add(maxFunctionLength);
        */
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(mutationProbabilityLabel, c);
        panel.add(mutationProbabilityLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(mutationProbability, c);
        panel.add(mutationProbability);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(crossingProbabilityLabel, c);
        panel.add(crossingProbabilityLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(crossingProbability, c);
        panel.add(crossingProbability);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(selectionProbabilityLabel, c);
        panel.add(selectionProbabilityLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(selectionProbability, c);
        panel.add(selectionProbability);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(reproductionProbabilityLabel, c);
        panel.add(reproductionProbabilityLabel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(reproductionProbability, c);
        panel.add(reproductionProbability);

        Panel p1 = new Panel();
        p1.add(new JLabel());
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p1, c);
        panel.add(p1);

        Panel p2 = new Panel();
        p2.add(arbitraryMutations, BorderLayout.CENTER);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p2, c);
        panel.add(p2);

        Panel p3 = new Panel();
        p3.add(arbitraryCrossings, BorderLayout.CENTER);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p3, c);
        panel.add(p3);

        modelNameCombo = new JComboBox(getGenetixClassNames());
        modelNameCombo.setBackground(Color.WHITE);

        Panel p4 = new Panel(new FlowLayout());
        p4.add(new JLabel(" Model :      "));
        p4.add(modelNameCombo);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p4, c);
        panel.add(p4);

        selectedModelName = (String) modelNameCombo.getSelectedItem();
        modelNameCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                selectedModelName = (String) modelNameCombo.getSelectedItem();
                boolean advSettings =
                    GPModelGenetix.class.isAssignableFrom(getSelectedModelClass());
                reproductionProbability.setEnabled(advSettings);
                selectionProbability.setEnabled(advSettings);
            }
            
        });

        Panel p5 = new Panel();
        p5.add(new JLabel());
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p5, c);
        panel.add(p5);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        Panel p6 = new Panel(new FlowLayout());
        p6.add(new JLabel(" Genetix Settings Panel "));
        add(p6, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        Panel p7 = new Panel(new FlowLayout());
        p7.add(okay);
        p7.add(cancel);
        add(p7,  BorderLayout.SOUTH);

        return this;
    }

    public boolean validateInput() {
        boolean valid = true; String text;
        final Color invalidColor = Color.RED;

        if ( (text = dataSize.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            dataSize.setBackground(invalidColor);
        } else {
            try {
                Integer.parseInt(text);
            }
            catch (NumberFormatException e) {
                valid = false;
                dataSize.setBackground(invalidColor);
            }
        }

        if ( (text = genSize.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            genSize.setBackground(invalidColor);
        } else {
            try {
                Integer.parseInt(text);
            }
            catch (NumberFormatException e) {
                valid = false;
                genSize.setBackground(invalidColor);
            }
        }

        if ( (text = genMax.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            genMax.setBackground(invalidColor);
        } else {
            try {
                Integer.parseInt(text);
            }
            catch (NumberFormatException e) {
                valid = false;
                genMax.setBackground(invalidColor);
            }
        }

        /*
        int minFxLen = -1;
        if ( (text = minFunctionLength.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            minFunctionLength.setBackground(invalidColor);
        } else {
            try {
                minFxLen = Integer.parseInt(text);
                if (minFxLen < 0) {
                    valid = false;
                    minFunctionLength.setBackground(invalidColor);
                }
            }
            catch (NumberFormatException e) {
                valid = false;
                minFunctionLength.setBackground(invalidColor);
            }
        }

        if ( (text = maxFunctionLength.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            maxFunctionLength.setBackground(invalidColor);
        } else {
            try {
                int maxFxLen = Integer.parseInt(text);
                if ( minFxLen >= 0 && maxFxLen <= minFxLen ) {
                    valid = false;
                    maxFunctionLength.setBackground(invalidColor);
                }
            }
            catch (NumberFormatException e) {
                valid = false;
                maxFunctionLength.setBackground(invalidColor);
            }
        }
        */

        if ( (text = mutationProbability.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            mutationProbability.setBackground(invalidColor);
        } else {
            try {
                float probabValue = Float.parseFloat(text);
                if (probabValue < 0F || probabValue > 1.0F) {
                    valid = false;
                    mutationProbability.setBackground(invalidColor);
                }
            }
            catch (NumberFormatException e) {
                valid = false;
                mutationProbability.setBackground(invalidColor);
            }
        }

        if ( (text = crossingProbability.getText()) == null
                || text.trim().length() == 0 ) {
            valid = false;
            crossingProbability.setBackground(invalidColor);
        } else {
            try {
                float probabValue = Float.parseFloat(text);
                if (probabValue < 0F || probabValue > 1.0F) {
                    valid = false;
                    crossingProbability.setBackground(invalidColor);
                }
            }
            catch (NumberFormatException e) {
                valid = false;
                crossingProbability.setBackground(invalidColor);
            }
        }

        if ( reproductionProbability.isEnabled() ) {
            if ( (text = reproductionProbability.getText()) == null
                    || text.trim().length() == 0 ) {
                valid = false;
                reproductionProbability.setBackground(invalidColor);
            } else {
                try {
                    float probabValue = Float.parseFloat(text);
                    if (probabValue < 0F || probabValue > 1.0F) {
                        valid = false;
                        reproductionProbability.setBackground(invalidColor);
                    }
                }
                catch (NumberFormatException e) {
                    valid = false;
                    reproductionProbability.setBackground(invalidColor);
                }
            }
        }

        if ( selectionProbability.isEnabled() ) {
            if ( (text = selectionProbability.getText()) == null
                    || text.trim().length() == 0 ) {
                valid = false;
                selectionProbability.setBackground(invalidColor);
            } else {
                try {
                    float probabValue = Float.parseFloat(text);
                    if (probabValue < 0F || probabValue > 1.0F) {
                        valid = false;
                        selectionProbability.setBackground(invalidColor);
                    }
                }
                catch (NumberFormatException e) {
                    valid = false;
                    selectionProbability.setBackground(invalidColor);
                }
            }
        }

        return valid;
    }

    /**
     * Method to receive this panel's input data.
     *
     * @return The size of the training data created from the graph.
     */        
    
    public int getDataSize() {
        return Integer.parseInt(dataSize.getText());
    }

    public void setDataSize(int dataSize) {
        if (dataSize < 0) throw new IllegalArgumentException("< 0");
        this.dataSize.setText(Integer.toString(dataSize));
    }
    
    /**
     * Method to receive this panel's input data.
     *
     * @return The size of one generation.
     */        
    
    public int getGenSize() {
        return Integer.parseInt(genSize.getText());
    }

    public void setGenSize(int genSize) {
        if (genSize < 0) throw new IllegalArgumentException("< 0");
        this.genSize.setText(Integer.toString(genSize));
    }

    /**
     * Method to receive this panel's input data.
     *
     * @return The maximal generation limit.
     */    
    
    public int getGenMax() {
        return Integer.parseInt(genMax.getText());
    }    

    public void setGenMax(int genMax) {
        if (genMax < 0) throw new IllegalArgumentException("< 0");
        this.genMax.setText(Integer.toString(genMax));
    }

    /**
     * Method to receive this panel's input data.
     *
     * @return The mutation probability.
     */        
    
    public float getGenMutationProbability() {
        return Float.parseFloat(mutationProbability.getText());
    }

    public void setGenMutationProbability(float probability) {
        if (probability < 0 || probability > 1.0F) {
            throw new IllegalArgumentException("should be >= 0 && <= 1");
        }
        if (probability == 1.0F) {
            this.mutationProbability.setText("1.0");
        }
        else {
            int p = (int) (probability * 1000);
            this.mutationProbability.setText("0." + Integer.toString(p));
        }
    }

    /**
     * Method to receive this panel's input data.
     *
     * @return The crossing probability.
     */            
    
    public float getGenCrossingProbability() {
        return Float.parseFloat(crossingProbability.getText());
    }  

    public void setGenCrossingProbability(float probability) {
        if (probability < 0 || probability > 1.0F) {
            throw new IllegalArgumentException("should be >= 0 && <= 1");
        }
        if (probability == 1.0F) {
            this.crossingProbability.setText("1.0");
        }
        else {
            int p = (int) (probability * 1000);
            this.crossingProbability.setText("0." + Integer.toString(p));
        }
    }

    /**
     * Method to receive this panel's input data.
     *
     * @return The reproduction probability.
     */            
    
    public float getGenReproductProbability() {
        return Float.parseFloat(reproductionProbability.getText());
    }        

    public void setGenReproductProbability(float probability) {
        if (probability < 0 || probability > 1.0F) {
            throw new IllegalArgumentException("should be >= 0 && <= 1");
        }
        if (probability == 1.0F) {
            this.reproductionProbability.setText("1.0");
        }
        else {
            int p = (int) (probability * 1000);
            this.reproductionProbability.setText("0." + Integer.toString(p));
        }
    }

    /**
     * Method to receive this panel's input data.
     *
     * @return The selection probability.
     */            
    
    public float getGenSelectionProbability() {
        return Float.parseFloat(selectionProbability.getText());
    }

    public void setGenSelectionProbability(float probability) {
        if (probability < 0 || probability > 1.0F) {
            throw new IllegalArgumentException("should be >= 0 && <= 1");
        }
        if (probability == 1.0F) {
            this.selectionProbability.setText("1.0");
        }
        else {
            int p = (int) (probability * 1000);
            this.selectionProbability.setText("0." + Integer.toString(p));
        }
    }

    public boolean isArbitraryMutations() {
        return arbitraryMutations.isSelected();
    }

    public void setArbitraryMutations(boolean flag) {
        arbitraryMutations.setSelected(flag);
    }
    
    public boolean isArbitraryCrossings() {
        return arbitraryCrossings.isSelected();
    }

    public void setArbitraryCrossings(boolean flag) {
        arbitraryCrossings.setSelected(flag);
    }

    public int getMinFunctionLength() {
        return Integer.parseInt(minFunctionLength.getText());
    }

    public void setMinFunctionLength(int length) {
        if (length < 0) throw new IllegalArgumentException("< 0");
        this.minFunctionLength.setText(Integer.toString(length));
    }

    public int getMaxFunctionLength() {
        return Integer.parseInt(maxFunctionLength.getText());
    }

    public void setMaxFunctionLength(int length) {
        if (length < 0) throw new IllegalArgumentException("< 0");
        this.maxFunctionLength.setText(Integer.toString(length));
    }

    /**
     * This indicates that the object is in advanced mode
     * that means that more setting parameters will be used
     * and an advanced genetical programming will be used
     * instead of the basic one.
     *
     * @return The state indicating if the advanced checkbox
     * has been selected.
     */
    public String getSelectedModelName() {
        return selectedModelName;
    }

    public Class getSelectedModelClass() {
        return (Class) modelClassNames.get(selectedModelName);
    }

    private static Map modelClassNames = new LinkedHashMap();
    static {
        for (Iterator i = Genetix.getGenetixModels().iterator(); i.hasNext();) {
            Class genetixModel = (Class) i.next();
            String name = genetixModel.getName();
            name = name.substring(name.lastIndexOf('.') + 1);
            modelClassNames.put(name, genetixModel);
        }
    }

    private String[] getGenetixClassNames() {
        Collection names = modelClassNames.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    public void setReadyListener(ActionListener readyListener) {
        this.readyListener = readyListener;
        //return this;
    }

    public void setCancelListener(ActionListener cancelListener) {
        this.cancelListener = cancelListener;
        //return this;
    }

    /*
    public static void main(String[] args) {
        GenetixSettings set = new GenetixSettings();
        final JFrame frame = new JFrame();
        frame.getContentPane().add(set);
        frame.pack();
        frame.setVisible(true);
    }
    */
    
}
