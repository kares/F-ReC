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

package org.kares.math.frec;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.kares.math.frec.core.*;
import org.kares.math.frec.gui.*;
import org.kares.math.frec.jcm.draw.*;

/**
 * The main entry point - runs an ugly swing/awt GUI.
 * Usable as an applet as well as a standalone application.
 * 
 * @author kares
 */
public class Main extends JApplet implements Runnable {
    
    /*
    static {
        JFrame.setDefaultLookAndFeelDecorated(true); 
        JDialog.setDefaultLookAndFeelDecorated(true);
    }
    */

    static final String VERSION = "1.6";
    static final String APP_NAME = "F-ReC " + VERSION;
    
    private GenetixSettings settings;
    private InputGraphPanel inputPanel;
    private OutputGraphPanel outputPanel;
    private DrawGraph graph;
    private JFrame appFrame;
    private JDialog settingsDialog;
    private JDialog progressDialog;
    private boolean savingEnabled;

    /**
     * @see JApplet#init()
     */
    public void init() {
        inputPanel = new InputGraphPanel() {

            public void storeData() {
                super.storeData();
                inputDataSet();
            }
            
        };
        appFrame = new JFrame(APP_NAME);
        appFrame.getContentPane().add(inputPanel);
        appFrame.setResizable(true);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        settings = new GenetixSettings();
        configureSettings();
        settings.setReadyListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settingsDialog.setVisible(false);
                settingsReady();
            }

        });
        settings.setCancelListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settingsDialog.setVisible(false);
                //settingsDialog.dispose();
            }

        });
        settingsDialog = new JDialog(appFrame, "Settings", true);
        settingsDialog.setContentPane(settings);
        settingsDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * @see JApplet#start()
     */
    public void start() {
        SwingUtilities.invokeLater(this);
    }

    void enableSaving() {
        savingEnabled = true;
    }

    private Genetix genetix;

    private void configureSettings() {
        // set-up some defaults :
        settings.setDataSize(300);
        settings.setGenSize(100);
        settings.setGenMax(100);
        settings.setMinFunctionLength( Genetix.getMinFunctionLength() );
        settings.setMaxFunctionLength( Genetix.getMaxFunctionLength() );
        settings.setGenMutationProbability(0.030F);
        settings.setGenCrossingProbability(0.900F);
        settings.setGenReproductProbability(0.950F);
        settings.setGenSelectionProbability(0.850F);
    }

    private void settingsReady() {
        try {
            genetix = (Genetix) settings.getSelectedModelClass().newInstance();
        }
        catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        if (genetix instanceof GPModelGenetix) {
            ((GPModelGenetix) genetix).setReproductProbability(settings.getGenReproductProbability());
            ((GPModelGenetix) genetix).setSelectionProbability(settings.getGenSelectionProbability());
        }
        genetix.setSavingMode( savingEnabled );
        genetix.setGenerationSize( settings.getGenSize() );
        genetix.setGenerationLimit( settings.getGenMax() );
        genetix.setMutationProbability( settings.getGenMutationProbability() );
        genetix.setCrossingProbability( settings.getGenCrossingProbability() );
        genetix.setArbitraryMutations( settings.isArbitraryMutations() );
        genetix.setArbitraryCrossings( settings.isArbitraryCrossings() );
        Genetix.setMinFunctionLength( settings.getMinFunctionLength() );
        Genetix.setMaxFunctionLength( settings.getMaxFunctionLength() );
        inputPanel.setDataSize( settings.getDataSize() );

        //appFrame.pack();
        //appFrame.setVisible(true);
        /* inputDataSet() */
    }

    private void inputDataSet() {
        final float[] x = inputPanel.getDataX();
        final float[] y = inputPanel.getDataY();

        GenetixProgress progress = new GenetixProgress(genetix.getGenerationLimit());
        progress.setOpaque(true);

        progressDialog = new JDialog(appFrame, "Please Wait ...");
        progressDialog.setContentPane(progress);
        progressDialog.setSize(300, 400);
        progressDialog.pack();
        setDialogCenterLocation(progressDialog);
        progressDialog.setVisible(true);

        genetix.setApproximatingData(x, y);

        graph = inputPanel.getDrawGraph();
        inputPanel.setEnabled(false);

        genetix.setComputedCallback(new Genetix.ComputedCallback() {

            public void onComputed() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() { showResults(); }
                });
            }

        });
        progress.start(genetix); // starts genetix computation
        /*showResults()*/
    }

    private static void setDialogCenterLocation(final JDialog dialog) {
        final JFrame owner = (JFrame) dialog.getOwner();
        final Point screen = owner.getLocationOnScreen();
        Dimension ownerSize = owner.getSize();
        Dimension dialogSize = dialog.getSize();
        int xloc = screen.x + (ownerSize.width - dialogSize.width) / 2;
        int yloc = screen.y + (ownerSize.height - dialogSize.height) / 2;
        dialog.setLocation(xloc, yloc);
    }

    private void showResults() {
        progressDialog.setVisible(false);
        progressDialog.dispose();
        //progressDialog = null;

        final int size = genetix.getGenerationSize() / 2;
        String[] funcs = genetix.getBestFunctionsFormatted(size);
        outputPanel = new OutputGraphPanel(funcs);
        outputPanel.addDrawGraph(graph);
        outputPanel.setAxisLimits(inputPanel.getAxisLimits());

        appFrame.getContentPane().remove(inputPanel);
        appFrame.getContentPane().add(outputPanel);
        appFrame.pack();
        appFrame.setVisible(true);
        outputPanel.repaint();
    }

    public void run() {
        appFrame.pack();
        appFrame.setVisible(true);
        
        settingsDialog.pack();
        setDialogCenterLocation(settingsDialog);
        settingsDialog.setVisible(true);

        /*settingsReady()*/
    }

    /**
     * Main method.
     * @param args
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.init();
        main.enableSaving();
        main.start();
    }    
   
}