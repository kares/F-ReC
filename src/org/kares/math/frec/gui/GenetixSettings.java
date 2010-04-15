/*
 * Copyright 2004 Karol Bucek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * A settings panel for configuring parameters of the computation.
 * 
 * @author kares
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
        final GridBagLayout gridLayout = new GridBagLayout();
        final GridBagConstraints gridSetup = new GridBagConstraints();
        panel.setLayout(gridLayout);

        gridSetup.fill = GridBagConstraints.BOTH;
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(dataSizeLabel, gridSetup);
        panel.add(dataSizeLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(dataSize, gridSetup);
        panel.add(dataSize);
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(genSizeLabel, gridSetup);
        panel.add(genSizeLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(genSize, gridSetup);
        panel.add(genSize);
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(genMaxLabel, gridSetup);
        panel.add(genMaxLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(genMax, gridSetup);
        panel.add(genMax);
        
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(minFunctionLengthLabel, gridSetup);
        panel.add(minFunctionLengthLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(minFunctionLength, gridSetup);
        panel.add(minFunctionLength);
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(maxFunctionLengthLabel, gridSetup);
        panel.add(maxFunctionLengthLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(maxFunctionLength, gridSetup);
        panel.add(maxFunctionLength);
        
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(mutationProbabilityLabel, gridSetup);
        panel.add(mutationProbabilityLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(mutationProbability, gridSetup);
        panel.add(mutationProbability);
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(crossingProbabilityLabel, gridSetup);
        panel.add(crossingProbabilityLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(crossingProbability, gridSetup);
        panel.add(crossingProbability);
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(selectionProbabilityLabel, gridSetup);
        panel.add(selectionProbabilityLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(selectionProbability, gridSetup);
        panel.add(selectionProbability);
        gridSetup.gridwidth = GridBagConstraints.RELATIVE;
        gridLayout.setConstraints(reproductionProbabilityLabel, gridSetup);
        panel.add(reproductionProbabilityLabel);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(reproductionProbability, gridSetup);
        panel.add(reproductionProbability);

        Panel p1 = new Panel();
        p1.add(new JLabel());
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(p1, gridSetup);
        panel.add(p1);

        Panel p2 = new Panel();
        p2.add(arbitraryMutations, BorderLayout.CENTER);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(p2, gridSetup);
        panel.add(p2);

        Panel p3 = new Panel();
        p3.add(arbitraryCrossings, BorderLayout.CENTER);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(p3, gridSetup);
        panel.add(p3);

        modelNameCombo = new JComboBox(getGenetixClassNames());
        modelNameCombo.setBackground(Color.WHITE);

        Panel p4 = new Panel(new FlowLayout());
        p4.add(new JLabel(" Model :      "));
        p4.add(modelNameCombo);
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(p4, gridSetup);
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
     	gridSetup.gridwidth = GridBagConstraints.REMAINDER; //end row
        gridLayout.setConstraints(p5, gridSetup);
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

    /**
     * Validate the input and return true if its valid.
     * @return False if validation failed, true otherwise.
     */
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
     * @return The data size settings value.
     */
    public int getDataSize() {
        return Integer.parseInt(dataSize.getText());
    }

    /**
     * @param dataSize
     */
    public void setDataSize(int dataSize) {
        if (dataSize < 0) throw new IllegalArgumentException("< 0");
        this.dataSize.setText(Integer.toString(dataSize));
    }
    
    /**
     * @return The configured size of one generation.
     */
    public int getGenSize() {
        return Integer.parseInt(genSize.getText());
    }

    /**
     * @param genSize
     */
    public void setGenSize(int genSize) {
        if (genSize < 0) throw new IllegalArgumentException("< 0");
        this.genSize.setText(Integer.toString(genSize));
    }

    /**
     * @return The maximum limit per generation.
     */
    public int getGenMax() {
        return Integer.parseInt(genMax.getText());
    }    

    /**
     * @param genMax
     */
    public void setGenMax(int genMax) {
        if (genMax < 0) throw new IllegalArgumentException("< 0");
        this.genMax.setText(Integer.toString(genMax));
    }

    /**
     * @return The configured mutation probability.
     */
    public float getGenMutationProbability() {
        return Float.parseFloat(mutationProbability.getText());
    }

    /**
     * @param probability
     */
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
     * @return The configured crossing probability.
     */            
    public float getGenCrossingProbability() {
        return Float.parseFloat(crossingProbability.getText());
    }  

    /**
     * @param probability
     */
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
     * @return The configured reproduction probability.
     */
    public float getGenReproductProbability() {
        return Float.parseFloat(reproductionProbability.getText());
    }        

    /**
     * @param probability
     */
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
     * @return The configured selection probability.
     */
    public float getGenSelectionProbability() {
        return Float.parseFloat(selectionProbability.getText());
    }

    /**
     * @param probability
     */
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

    /**
     * @return True if arbitrary mutations were selected.
     */
    public boolean isArbitraryMutations() {
        return arbitraryMutations.isSelected();
    }

    /**
     * @param flag
     */
    public void setArbitraryMutations(boolean flag) {
        arbitraryMutations.setSelected(flag);
    }
    
    /**
     * @return @return True if arbitrary crossings were selected.
     */
    public boolean isArbitraryCrossings() {
        return arbitraryCrossings.isSelected();
    }

    /**
     * @param flag
     */
    public void setArbitraryCrossings(boolean flag) {
        arbitraryCrossings.setSelected(flag);
    }

    /**
     * @return The minimum function length limit.
     */
    public int getMinFunctionLength() {
        return Integer.parseInt(minFunctionLength.getText());
    }

    /**
     * @param length
     */
    public void setMinFunctionLength(int length) {
        if (length < 0) throw new IllegalArgumentException("< 0");
        this.minFunctionLength.setText(Integer.toString(length));
    }

    /**
     * @return The maximum function length limit.
     */
    public int getMaxFunctionLength() {
        return Integer.parseInt(maxFunctionLength.getText());
    }

    /**
     * @param length
     */
    public void setMaxFunctionLength(int length) {
        if (length < 0) throw new IllegalArgumentException("< 0");
        this.maxFunctionLength.setText(Integer.toString(length));
    }

    /**
     * @return The selected Genetix model name.
     */
    public String getSelectedModelName() {
        return selectedModelName;
    }

    /**
     * @see #getSelectedModelName()
     * @return The selected Genetix class.
     */
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

    /**
     * Set a listener to invoke when the UI is ready (confirmed). 
     * @param cancelListener
     */
    public void setReadyListener(ActionListener readyListener) {
        this.readyListener = readyListener;
        //return this;
    }

    /**
     * Set a listener to invoke when the UI is canceled. 
     * @param cancelListener
     */
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
