
package frec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import frec.gui.*;
import frec.core.*;
import frec.jcm.draw.DrawGraph;

public final class Main extends JApplet implements Runnable
{
    /*
    static 
    { 
        JFrame.setDefaultLookAndFeelDecorated(true); 
        JDialog.setDefaultLookAndFeelDecorated(true);
    }
    */
    
    private static String versionName = "F-ReC 1.5";
    
    private Genetix genetix;
    private GenetixSetting setting;
    private InputGraphPanel input;
    private OutputGraphPanel output;
    private DrawGraph graph;
    private JFrame gui, set;
    private JDialog dialog;
    private GenetixProgress progress;
    private boolean savingEnabled;

    public void init()
    {
        input = new InputGraphPanel();
        input.init();
        setting = new GenetixSetting();
        set = new JFrame(versionName);
        set.getContentPane().add(setting);
        set.setResizable(false);
        gui = new JFrame(versionName);
        gui.getContentPane().add(input);
        gui.setResizable(true);
        //gui.setJMenuBar(new MenuBar());
        set.pack();
        set.show();        
        dialog = new JDialog(gui, "Please Wait ...");        
    }

    public void start()
    {
        while (!setting.isReady())
        {
            try{ Thread.sleep(500); }
            catch (InterruptedException e)
            { System.out.println("Main - interrupted : " + e.getMessage()); }
        }
 
        String genx = setting.getGenModel();
        if (genx.equals("GPModelGenetix"))
        {
            genetix = new GPModelGenetix();
            ((GPModelGenetix)genetix).setReproductProbability(setting.getGenReproductProb());
            ((GPModelGenetix)genetix).setSelectionProbability(setting.getGenSelectionProb());                
        }
        else
        if (genx.equals("MyModelGenetix"))
        {
            genetix = new MyModelGenetix();
            ((MyModelGenetix)genetix).setReproductProbability(setting.getGenReproductProb());
            ((MyModelGenetix)genetix).setSelectionProbability(setting.getGenSelectionProb());                
        }        
        else genetix = new GAModelGenetix();
          
        input.setDataSize(setting.getDataSize());
            
        genetix.setSavingMode(savingEnabled);
        genetix.setGenerationSize(setting.getGenSize());
        genetix.setGenerationCounterLimit(setting.getGenMax());
        genetix.setMutationProbability(setting.getGenMutationProb());
        genetix.setCrossingProbability(setting.getGenCrossingProb());
        genetix.setArbitraryMutations(setting.getArbitraryMutationsUsage());
        genetix.setArbitraryCrossings(setting.getArbitraryCrossingsUsage());
            
        setting = null;
        set.dispose();
        gui.pack();
        gui.show();
        set = null;
        
        while (!input.hasData())
        {
            try{ Thread.sleep(500); }
            catch (InterruptedException e)
            { System.out.println("Main - interrupted : " + e.getMessage()); }
        }
        
        float[] x = input.datax;
        float[] y = input.datay;

        progress = new GenetixProgress(genetix);
        progress.setOpaque(true);
        dialog.setContentPane(progress);
        dialog.pack();
        Point p = gui.getLocationOnScreen();
        Dimension d1 = gui.getSize();
        dialog.setSize(300, 400);
        Dimension d2 = dialog.getSize();
        int xloc = p.x + (d1.width - d2.width) / 2;
        int yloc = p.y + (d1.height - d2.height) / 2;
        dialog.setLocation(xloc,yloc);
        dialog.show();                
        progress.start();
            
        genetix.setApproximatingData(x,y);
        new Thread(genetix).start();
            
        graph = input.getDrawGraph();
        input.setEnabled(false);             

        while (!genetix.hasFinishedComputing())
        {
            try{ Thread.sleep(1000); }
            catch (InterruptedException e)
            { System.out.println("Main - interrupted : " + e.getMessage()); }
        }
          
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
        progress = null;
            
        String[] funcs = genetix.getBestResults(genetix.getGenerationSize() / 2);
        output = new OutputGraphPanel(funcs);
        output.init(graph);
        output.setAxisLimits(input.getAxisLimits());

        gui.getContentPane().remove(input);
        gui.getContentPane().add(output);
        input = null;
        gui.pack();
        gui.show();
        output.repaint();
    }

    public void enableSaving()
    {
        savingEnabled = true;
    }

    public void enableClose()
    {
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        set.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void run()
    {
        SwingUtilities.invokeLater(this);
    }
    
    public static void main(String [] args)
    {          
        Main main = new Main();
        main.init();
        main.enableSaving();
        main.enableClose();
        main.start();
    }    

    protected class MenuBar extends JMenuBar
    {
        private JMenu menu;
        private JMenuItem menuItem;
        private JRadioButtonMenuItem rbMenuItem;
        private Color dfColor = Color.LIGHT_GRAY.darker();

        public MenuBar()
        {
            this.setBackground(dfColor);

            //Build the first menu.
            menu = new JMenu("File");
            menu.setBackground(dfColor);
            menu.setMnemonic(KeyEvent.VK_F);
            menu.getAccessibleContext().setAccessibleDescription("File options");
            this.add(menu);      
    
            //a group of JMenuItems
            menuItem = new JMenuItem("New", KeyEvent.VK_N);
            menu.add(menuItem);    
            menuItem = new JMenuItem("Open", KeyEvent.VK_O);
            menu.add(menuItem);
            menuItem = new JMenuItem("Save", KeyEvent.VK_S);
            menu.add(menuItem);

            ButtonGroup group;
            //a group of radio button menu items
            menu.addSeparator();
            group = new ButtonGroup();
            rbMenuItem = new JRadioButtonMenuItem("Use Graph Drawing");
            group.add(rbMenuItem);
            menu.add(rbMenuItem);
            rbMenuItem = new JRadioButtonMenuItem("New Graph Drawing");
            rbMenuItem.setSelected(true);
            group.add(rbMenuItem);
            menu.add(rbMenuItem);
    
            //a group of radio button menu items
            menu.addSeparator();
            group = new ButtonGroup();
            rbMenuItem = new JRadioButtonMenuItem("Use Current Limits");
            group.add(rbMenuItem);
            menu.add(rbMenuItem);
            rbMenuItem = new JRadioButtonMenuItem("Set Default Limits");
            rbMenuItem.setSelected(true);
            group.add(rbMenuItem);
            menu.add(rbMenuItem);    
            //second menu in the menu bar.
            menu = new JMenu("Control");
            menu.setBackground(dfColor);
            menu.setMnemonic(KeyEvent.VK_C);
            menu.getAccessibleContext().setAccessibleDescription("Axis Controls");
            this.add(menu);      
    
            //a group of JMenuItems
            menuItem = new JMenuItem("Set Limits");
            menu.add(menuItem);    
            menuItem = new JMenuItem("Save Limits");
            menu.add(menuItem);
            menuItem = new JMenuItem("Restore Limits");
            menu.add(menuItem); 
            menu.addSeparator();
            menuItem = new JMenuItem("Zoom In");
            menu.add(menuItem);    
            menuItem = new JMenuItem("Zoom Out");
            menu.add(menuItem);
            menuItem = new JMenuItem("Equalize Axes");
            menu.add(menuItem);  
    
            //third menu in the menu bar.
            menu = new JMenu("Setting");
            menu.setBackground(dfColor);
            menu.setMnemonic(KeyEvent.VK_S);
            menu.getAccessibleContext().setAccessibleDescription("Genetix Settings");
            this.add(menu);      
   
            //fourth menu in the menu bar.
            menu = new JMenu("Help");
            menu.setBackground(dfColor);
            menu.setMnemonic(KeyEvent.VK_H);
            menu.getAccessibleContext().setAccessibleDescription("Help");
            this.add(menu);          
        }
    }
}