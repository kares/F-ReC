
package frec.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.File;

    /**
     * Class <code> GenetixSetting </code> is an gui panel
     * which is used to set the parameters of genetic programming.
     * An instance of this class is the first think the user sees
     * when runing the application.
     */

public class GenetixSetting extends JPanel 
{    
    private JLabel size, max, mut, cross, minf, maxf, repro, sel, data;
    private JTextField tsize, tmax, tmut, tcross, tminf, tmaxf, trepro, tsel, tdata;
    private JRadioButton advan;
    private JCheckBox cmut, ccross;
    private JComboBox combo;
    private String sel_genetix;
    private boolean isReady = false;

    /**
     * Constructor that creates a new <code>GenetixSetting</code>
     * object. All the necessary components are initialized.
     * After constructing the panel is isReady to be shown.
     */    
    
    public GenetixSetting() 
    {
        data  = new JLabel(" Training data size ");
        size  = new JLabel(" Size of generation ");
        max   = new JLabel(" Generation max     ");
        minf  = new JLabel(" Min f(x) length      ");
        maxf  = new JLabel(" Max f(x) length      ");
        mut   = new JLabel(" Mutation  probab.  ");
        cross = new JLabel(" Crossing  probab.  ");
        repro = new JLabel(" Reproduct probab.");
        sel   = new JLabel(" Selection  probab. ");
        
        cmut   = new JCheckBox(" Use arbitrary mutations ");
        ccross = new JCheckBox(" Use arbitrary crossings  ");
        
        EmptyBorder border = new EmptyBorder(5,0,5,2);
        
        data .setBorder(border);
        size .setBorder(border);
        max  .setBorder(border);
        minf .setBorder(border);
        maxf .setBorder(border);
        mut  .setBorder(border);
        cross.setBorder(border);
        repro.setBorder(border);
        sel  .setBorder(border);
        
        tdata  = new JTextField("300", 8);
        tsize  = new JTextField("100", 8);
        tmax   = new JTextField("200", 8);
        tminf  = new JTextField("2", 8);
        tmaxf  = new JTextField("8", 8);
        tmut   = new JTextField("0.025", 8);
        tcross = new JTextField("0.900", 8);
        trepro = new JTextField("0.950", 8);
        tsel   = new JTextField("0.850", 8);      
        
        trepro.setEnabled(false);
        tsel.setEnabled(false);
        
        JButton okay = new JButton("  OK  ");
        okay.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt) 
            {
                isReady = true;
                GenetixSetting.this.setVisible(false);
            }
        }); 
        
        JButton exit = new JButton(" EXIT ");
        exit.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt) 
            {
                GenetixSetting.this.setVisible(false);
                System.exit(0);
            }
        });         

        /*
        JRadioButton basic = new JRadioButton("GAModel");
        advan = new JRadioButton("GPModel")
        {
            protected void fireStateChanged() 
            {
                if (trepro.isEnabled())
                {
                    trepro.setEnabled(false);
                    tsel.setEnabled(false);                
                }
                else
                {
                    trepro.setEnabled(true);
                    tsel.setEnabled(true);                
                }                    
            }
        };

        ButtonGroup group = new ButtonGroup();
        group.add(basic);
        group.add(advan);
        basic.setSelected(true);
        */
        
        Panel panel = new Panel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panel.setLayout(layout);

        c.fill = GridBagConstraints.BOTH;        
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(data, c);
        panel.add(data);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tdata, c);
        panel.add(tdata);        
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(size, c);
        panel.add(size);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tsize, c);
        panel.add(tsize);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(max, c);
        panel.add(max);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tmax, c);
        panel.add(tmax);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(minf, c);
        panel.add(minf);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tminf, c);
        panel.add(tminf);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(maxf, c);
        panel.add(maxf);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tmaxf, c);
        panel.add(tmaxf); 
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(mut, c);
        panel.add(mut);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tmut, c);
        panel.add(tmut);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(cross, c);
        panel.add(cross);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tcross, c);
        panel.add(tcross);
        c.gridwidth = GridBagConstraints.RELATIVE;
        layout.setConstraints(sel, c);
        panel.add(sel);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(tsel, c);
        panel.add(tsel);                
        c.gridwidth = GridBagConstraints.RELATIVE;        
        layout.setConstraints(repro, c);
        panel.add(repro);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(trepro, c);
        panel.add(trepro);
        
        Panel p = new Panel();
        p.add(new JLabel());
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p, c);
        panel.add(p);        
        
        p = new Panel();
        p.add(cmut, BorderLayout.CENTER);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p, c);
        panel.add(p);
        
        p = new Panel();
        p.add(ccross, BorderLayout.CENTER);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p, c);
        panel.add(p);
        
        //p = new Panel();
        //p.add(basic);
        //p.add(advan);        
        //c.gridwidth = GridBagConstraints.REMAINDER; //end row
        //layout.setConstraints(p, c);
        //panel.add(p);  
        
        combo = new JComboBox(getGenetixClassNames());
        combo.setBackground(Color.WHITE);
        p = new Panel(new FlowLayout());
        p.add(new JLabel(" Genetix :    "));
        p.add(combo);
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p, c);
        panel.add(p); 
        
        sel_genetix = (String)combo.getSelectedItem();
        combo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e) 
            {
                sel_genetix = (String)combo.getSelectedItem();
                if (sel_genetix.equals("GAModelGenetix"))
                {
                    trepro.setEnabled(false);
                    tsel.setEnabled(false);                
                }
                else
                {
                    trepro.setEnabled(true);
                    tsel.setEnabled(true);                
                }                   
                  
            }
        });
        
        p = new Panel();
        p.add(new JLabel());
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p, c);
        panel.add(p);          
        
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        p = new Panel(new FlowLayout());
        p.add(new JLabel("GENETIX SETTING PANEL"));        
        this.add(p, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
        p = new Panel(new FlowLayout());
        p.add(okay);
        p.add(exit);        
        this.add(p,  BorderLayout.SOUTH);
    }       

    /**
     * Method to receive this panel's input data.
     *
     * @return The size of the training data created from the graph.
     */        
    
    public int getDataSize()
    {
        return Integer.parseInt(tdata.getText());
    }    
    
    /**
     * Method to receive this panel's input data.
     *
     * @return The size of one generation.
     */        
    
    public int getGenSize()
    {
        return Integer.parseInt(tsize.getText());
    }
    
    /**
     * Method to receive this panel's input data.
     *
     * @return The maximal generation limit.
     */    
    
    public int getGenMax()
    {
        return Integer.parseInt(tmax.getText());
    }    

    /**
     * Method to receive this panel's input data.
     *
     * @return The mutation probability.
     */        
    
    public float getGenMutationProb()
    {
        return Float.parseFloat(tmut.getText());
    }        

    /**
     * Method to receive this panel's input data.
     *
     * @return The crossing probability.
     */            
    
    public float getGenCrossingProb()
    {
        return Float.parseFloat(tcross.getText());
    }  
    
    /**
     * Method to receive this panel's input data.
     *
     * @return The reproduction probability.
     */            
    
    public float getGenReproductProb()
    {
        return Float.parseFloat(trepro.getText());
    }        
    
    /**
     * Method to receive this panel's input data.
     *
     * @return The selection probability.
     */            
    
    public float getGenSelectionProb()
    {
        return Float.parseFloat(tsel.getText());
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
    
    public String getGenModel()
    {
        return sel_genetix;
    }

    public boolean getArbitraryMutationsUsage()
    {
        return cmut.isSelected();
    }    
    
    public boolean getArbitraryCrossingsUsage()
    {
        return ccross.isSelected();
    }
    
    /**
     * This indicates that the object is isReady, meaning
     * that the user has finished setting the parameters. 
     *
     * @return The state of this panel.
     */            
    
    public boolean isReady()
    {
        return isReady;
    }   
    
    
    private String[] getGenetixClassNames()
    {
        return new String[] {
            "GAModelGenetix", 
            "GPModelGenetix", 
            "MyModelGenetix"
        };
    }
   
    public static void main(String[] args)
    {
        GenetixSetting set = new GenetixSetting();
        JFrame f = new JFrame();
        f.getContentPane().add(set);
        f.pack();
        f.show();
    }
    
}
