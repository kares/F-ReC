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
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.kares.math.frec.core.*;

/**
 * A progress indicator panel to be shown while computing.
 * 
 * @author kares
 */
public class GenetixProgress extends JPanel {
	
    private final static int CHECK_TIME = 500;
    
    private final int maxProgress;
    private final JProgressBar progressBar;
    private final JTextArea outputArea;
    private final JButton cancelButton;
    
    private boolean canceled = false;

    /**
     * @param maxProgress The maximum progress value.
     */
    public GenetixProgress(final int maxProgress) {
        super(new BorderLayout());
        
        this.maxProgress = maxProgress;
        progressBar = new JProgressBar(0, maxProgress);
        progressBar.setValue(0);

        progressBar.setStringPainted(true); //get space for the string
        progressBar.setString("");          //but don't paint it

        outputArea = new JTextArea(5, 20);
        outputArea.setMargin(new Insets(3, 3, 3, 3));
        outputArea.setEditable(false);
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
                cancelButton.setEnabled(false);
                canceled = true;
                cancel();
            }
            
        });

        JPanel panel1 = new JPanel();
        panel1.add(new JLabel(" Progress: "));
        panel1.add(progressBar);
        
        JPanel panel2 = new JPanel();
        panel2.add(cancelButton);        

        add(panel1, BorderLayout.PAGE_START);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(panel2, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    } 
    
    /**
     * @return True if the computation was canceled (clicked a cancel button).
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Update the progress.
     * @param genetix The computation actually happening.
     */
    private void updateProgress(final Genetix genetix) {
        int counter = genetix.getGenerationCounter();
        progressBar.setValue(counter);
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
            progressBar.setString(null); // display % string
        }
        
        outputArea.append("Generation: " + counter + " of " + maxProgress + "\n");
        //outputArea.append("Functions created: " + genetix.getFunctionsCreated() + "\n");
        outputArea.append("Best fitness: " + genetix.getBestFitness() + "\n");

        counter = outputArea.getDocument().getLength();
        outputArea.setCaretPosition(counter);
    }

    /**
     * Finish the progress.
     * @param genetix The finished task.
     */
    public void finishProgress(final Genetix genetix) {
        updateProgress(genetix);
        Toolkit.getDefaultToolkit().beep();
        //progressBar.setValue(progressBar.getMinimum());
        progressBar.setString(""); // hide % string
    }

    private void cancel() {
        if (timer != null) timer.stop();
        if (worker != null) worker.interrupt();
    }

    private Timer timer;
    private SwingWorker worker;

    /**
     * Start the progress.
     * @param genetix The task to monitor for progress.
     */
    public void start(final Genetix genetix) {
        timer = new Timer(CHECK_TIME, new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
                updateProgress(genetix);
            }
            
        });
        worker = new SwingWorker() {
        	
            public Object work() {
                genetix.run();
                return null;
            }

            public void interrupt() {
                genetix.stopCompute();
                super.interrupt();
            }

            public void finished() {
                timer.stop();
                finishProgress(genetix);
            }

        };
        timer.start();
        worker.start();
    }
    
}
