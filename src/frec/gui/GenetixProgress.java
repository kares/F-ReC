
package frec.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import frec.core.*;

public class GenetixProgress extends JPanel
    implements ActionListener
{
    private final static int CHECK_TIME = 500;
    private final static String newline = "\n";
    private final static String ginfo = "Generation: ";
    private final static String finfo = "Functions created: ";
    private final static String binfo = "Best fitness: ";
    private static String minfo;
    
    private JProgressBar progress;
    private Timer timer;
    private Genetix task = null;
    private JTextArea output;
    private JButton cancel;
    
    private boolean canceled = false;
    
    private ActionListener timerAction = new ActionListener()    
    {
            public void actionPerformed(ActionEvent evt) 
            {
                if (task != null)
                {
                    int counter = task.getGenerationCounter();
                    progress.setValue(counter);
                
                    if (progress.isIndeterminate()) 
                    {
                        progress.setIndeterminate(false);
                        progress.setString(null); //display % string
                    }
                
                    output.append(ginfo + counter + minfo + newline);
                    output.append(finfo + GenetixFunction.getFunctionsCreated() + newline);
                    output.append(binfo + task.getBestFitness() + newline);
                    counter = output.getDocument().getLength();
                    output.setCaretPosition(counter);
                    
                    if (task.hasFinishedComputing()) 
                    {
                        Toolkit.getDefaultToolkit().beep();
                        timer.stop();
                        //progressBar.setValue(progressBar.getMinimum());
                        progress.setString(""); //hide % string
                    }
                }
            }
        };

    public GenetixProgress(Genetix task) 
    {
        super(new BorderLayout());
        this.task = task;
        int max = task.getGenerationCounterLimit();
        minfo = " of " + max;

        progress = new JProgressBar(0, max);
        progress.setValue(0);

        progress.setStringPainted(true); //get space for the string
        progress.setString("");          //but don't paint it

        output = new JTextArea(5, 20);
        output.setMargin(new Insets(3 ,3 ,3 ,3 ));
        output.setEditable(false);
        
        cancel = new JButton("Cancel");

        JPanel panel1 = new JPanel();
        panel1.add(new JLabel(" Progress: "));
        panel1.add(progress);
        
        JPanel panel2 = new JPanel();
        panel2.add(cancel);        

        add(panel1, BorderLayout.PAGE_START);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(panel2, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        timer = new Timer(CHECK_TIME, timerAction);
    }
    
    /**
     * Called when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) 
    {
        if (evt.getSource().equals(cancel))
        {
            cancel.setEnabled(false);
            timer.stop();
            canceled = true;
        }
    }    
    
    public boolean isCanceled()
    {
        return canceled;
    }
    
    public void start() //throws
        //java.lang.InterruptedException,
        //java.lang.reflect.InvocationTargetException
    {
        try{
        SwingUtilities.invokeAndWait(new Runnable()
        {
            public void run() 
            { 
                timer.restart(); 
                progress.setIndeterminate(true);
            }
        });
        } catch (Exception e){ e.printStackTrace(); }
    }
    
}
