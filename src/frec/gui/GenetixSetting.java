
package frec.gui;

import frec.core.GPModelGenetix;
import frec.core.Genetix;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    //private JRadioButton advan;
    private JCheckBox cmut, ccross;
    private JComboBox combo;
    private String selModel;
    
    private boolean isReady = false;
    private ActionListener readyListener = null;

    /**
     * Constructor that creates a new <code>GenetixSetting</code>
     * object. All the necessary components are initialized.
     * After constructing the panel is isReady to be shown.
     */    
    
    public GenetixSetting() 
    {
        buildUI();
    }       

    public GenetixSetting buildUI()
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

        //trepro.setEnabled(false);
        //tsel.setEnabled(false);

        JButton okay = new JButton("  OK  ");
        okay.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                isReady = true;
                readyListener.actionPerformed(e);
            }
        });

        JButton exit = new JButton(" EXIT ");
        exit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
                System.exit(0);
            }
        });

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

        selModel = (String) combo.getSelectedItem();
        combo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                selModel = (String) combo.getSelectedItem();
                boolean advSettings =
                    GPModelGenetix.class.isAssignableFrom(getSelModelClass());
                trepro.setEnabled(advSettings);
                tsel.setEnabled(advSettings);
            }
        });

        p = new Panel();
        p.add(new JLabel());
     	c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(p, c);
        panel.add(p);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        p = new Panel(new FlowLayout());
        p.add(new JLabel("GENETIX SETTING PANEL"));
        add(p, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        p = new Panel(new FlowLayout());
        p.add(okay);
        p.add(exit);
        add(p,  BorderLayout.SOUTH);

        return this;
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

    public boolean getArbitraryMutationsUsage()
    {
        return cmut.isSelected();
    }    
    
    public boolean getArbitraryCrossingsUsage()
    {
        return ccross.isSelected();
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
    public String getSelModelName()
    {
        return selModel;
    }

    public Class getSelModelClass()
    {
        return (Class) modelClassNames.get(selModel);
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

    private String[] getGenetixClassNames()
    {
        Collection names = modelClassNames.keySet();
        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * This indicates that the object is isReady, meaning
     * that the user has finished setting the parameters. 
     *
     * @deprecated
     * @return The state of this panel.
     */            
    public boolean isReady()
    {
        return isReady;
    }   

    public GenetixSetting setReadyListener(ActionListener readyListener)
    {
        this.readyListener = readyListener;
        return this;
    }

    public static void main(String[] args)
    {
        GenetixSetting set = new GenetixSetting();
        final JFrame frame = new JFrame();
        frame.getContentPane().add(set);
        frame.pack();
        frame.setVisible(true);
    }
    
}
