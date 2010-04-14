
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
        //settings.setMinFunctionLength(FunctionTree.getCodeElementMin());
        //settings.setMaxFunctionLength(FunctionTree.getCodeElementMax());
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
        genetix.setSavingMode(savingEnabled);
        genetix.setGenerationSize(settings.getGenSize());
        genetix.setGenerationLimit(settings.getGenMax());
        genetix.setMutationProbability(settings.getGenMutationProbability());
        genetix.setCrossingProbability(settings.getGenCrossingProbability());
        genetix.setArbitraryMutations(settings.isArbitraryMutations());
        genetix.setArbitraryCrossings(settings.isArbitraryCrossings());
        inputPanel.setDataSize(settings.getDataSize());

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

        //set.pack();
        //set.setVisible(true);
        /*settingsReady()*/
    }
    
    public static void main(String[] args) {
        Main main = new Main();
        main.init();
        main.enableSaving();
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