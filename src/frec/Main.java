
package frec;

import frec.gui.*;
import frec.core.*;
import frec.jcm.draw.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Main extends JApplet implements Runnable {
    /*
    static {
        JFrame.setDefaultLookAndFeelDecorated(true); 
        JDialog.setDefaultLookAndFeelDecorated(true);
    }
    */

    private static final String VERSION = "1.6";
    private static final String APP_NAME = "F-ReC " + VERSION;
    
    private GenetixSetting setting;
    private InputGraphPanel input;
    private OutputGraphPanel output;
    private DrawGraph graph;
    private JFrame gui, set;
    private JDialog dialog;
    private GenetixProgress progress;
    private boolean savingEnabled;

    public void init() {
        input = new InputGraphPanel() {

            public void storeData() {
                super.storeData();
                inputDataSet();
            }
            
        };

        setting = new GenetixSetting();
        setting.setReadyListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                settingsReady();
            }
            
        });

        set = new JFrame(APP_NAME);
        set.getContentPane().add(setting);
        set.setResizable(false);
        
        gui = new JFrame(APP_NAME);
        gui.getContentPane().add(input);
        gui.setResizable(true);
        dialog = new JDialog(gui, "Please Wait ...");        
    }

    public void start() {
        SwingUtilities.invokeLater(this);
    }

    void enableSaving() {
        savingEnabled = true;
    }

    void enableClose() {
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        set.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Genetix genetix;

    private void settingsReady() {
        try {
            genetix = (Genetix) setting.getSelModelClass().newInstance();
        }
        catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }

        if (genetix instanceof GPModelGenetix) {
            ((GPModelGenetix) genetix).setReproductProbability(setting.getGenReproductProb());
            ((GPModelGenetix) genetix).setSelectionProbability(setting.getGenSelectionProb());
        }
        genetix.setSavingMode(savingEnabled);
        genetix.setGenerationSize(setting.getGenSize());
        genetix.setGenerationLimit(setting.getGenMax());
        genetix.setMutationProbability(setting.getGenMutationProb());
        genetix.setCrossingProbability(setting.getGenCrossingProb());
        genetix.setArbitraryMutations(setting.getArbitraryMutationsUsage());
        genetix.setArbitraryCrossings(setting.getArbitraryCrossingsUsage());
        input.setDataSize(setting.getDataSize());

        set.dispose();
        gui.pack();
        gui.setVisible(true);
        /* inputDataSet() */
    }

    private void inputDataSet() {
        final float[] x = input.getDataX();
        final float[] y = input.getDataY();

        progress = new GenetixProgress(genetix.getGenerationLimit());
        progress.setOpaque(true);
        
        dialog.setContentPane(progress);
        Point p = gui.getLocationOnScreen();
        Dimension d1 = gui.getSize();
        dialog.setSize(300, 400);
        Dimension d2 = dialog.getSize();
        int xloc = p.x + (d1.width - d2.width) / 2;
        int yloc = p.y + (d1.height - d2.height) / 2;
        dialog.setLocation(xloc,yloc);
        dialog.pack();
        dialog.setVisible(true);

        genetix.setApproximatingData(x, y);

        graph = input.getDrawGraph();
        input.setEnabled(false);

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

    private void showResults() {
        dialog.setVisible(false);
        //dialog.dispose();
        dialog = null;
        progress = null;

        final int size = genetix.getGenerationSize() / 2;
        String[] funcs = genetix.getBestFunctionsFormatted(size);
        output = new OutputGraphPanel(funcs);
        output.addDrawGraph(graph);
        output.setAxisLimits(input.getAxisLimits());

        gui.getContentPane().remove(input);
        gui.getContentPane().add(output);
        gui.pack();
        gui.setVisible(true);
        output.repaint();
    }

    public void run() {
        set.pack();
        set.setVisible(true);
        /*settingsReady()*/
    }
    
    public static void main(String[] args) {
        Main main = new Main();
        main.init();
        main.enableSaving();
        main.enableClose();
        main.start();
    }    

    /*
    private static class MenuBar extends JMenuBar
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
    */
    
}