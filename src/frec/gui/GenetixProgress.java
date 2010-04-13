
package frec.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import frec.core.*;
import java.awt.Toolkit;

public class GenetixProgress extends JPanel
{
    private final static int CHECK_TIME = 500;
    private final static String ginfo = "Generation: ";
    private final static String finfo = "Functions created: ";
    private final static String binfo = "Best fitness: ";
    private static String minfo;
    
    private JProgressBar progress;
    private JTextArea output;
    private JButton cancel;
    
    private boolean canceled = false;

    public GenetixProgress(final int maxProgress)
    {
        super(new BorderLayout());
        //this.task = genetix;
        //int max = genetix.getGenerationLimit();
        minfo = " of " + maxProgress;

        progress = new JProgressBar(0, maxProgress);
        progress.setValue(0);

        progress.setStringPainted(true); //get space for the string
        progress.setString("");          //but don't paint it

        output = new JTextArea(5, 20);
        output.setMargin(new Insets(3 ,3 ,3 ,3 ));
        output.setEditable(false);
        
        cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                cancel.setEnabled(false);
                canceled = true;
                cancel();
            }
        });

        JPanel panel1 = new JPanel();
        panel1.add(new JLabel(" Progress: "));
        panel1.add(progress);
        
        JPanel panel2 = new JPanel();
        panel2.add(cancel);        

        add(panel1, BorderLayout.PAGE_START);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(panel2, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    } 
    
    public boolean isCanceled()
    {
        return canceled;
    }

    //

    public void updateProgress(final Genetix genetix)
    {
        int counter = genetix.getGenerationCounter();
        progress.setValue(counter);
        if (progress.isIndeterminate())
        {
            progress.setIndeterminate(false);
            progress.setString(null); //display % string
        }

        output.append(ginfo + counter + minfo + "\n");
        output.append(finfo + genetix.getFunctionsCreated() + "\n");
        output.append(binfo + genetix.getBestFitness() + "\n");

        counter = output.getDocument().getLength();
        output.setCaretPosition(counter);
    }

    public void finishProgress(final Genetix genetix)
    {
        updateProgress(genetix);
        Toolkit.getDefaultToolkit().beep();
        //progressBar.setValue(progressBar.getMinimum());
        progress.setString(""); //hide % string
    }

    private void cancel()
    {
        if (timer != null) timer.stop();
        if (worker != null) worker.interrupt();
    }

    private Timer timer;
    private SwingWorker worker;

    public void start(final Genetix genetix)
    {
        timer = new Timer(CHECK_TIME, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateProgress(genetix);
            }
        });
        worker = new SwingWorker()
        {
            public Object work()
            {
                genetix.run();
                return null;
            }

            public void interrupt() {
                genetix.stopCompute();
                super.interrupt();
            }

            public void finished()
            {
                timer.stop();
                finishProgress(genetix);
            }

        };
        timer.start();
        worker.start();
    }
    
}
