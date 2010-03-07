
package frec.gui;

import frec.jcm.core.*;
import frec.jcm.draw.*;
import frec.jcm.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

    /**
     * Class <code> OutputGraphPanel </code> is an output graphical
     * user interface used to provide a graphical environment
     * for the output data.
     */

public class OutputGraphPanel extends JCMPanel 
{
   private DisplayCanvas canvas;
   private LimitControlPanel controlPanel;
   private Parser parser;
   private Variable x;
   private VariableSlider xSlider;
   private ExpressionInput input;
   private Function func;
   private Graph graph;
   private Grid grid;
   private Crosshair crosshair;
   private DrawString info;
   
   private String[] funcs;
   private int length;
   private int index;
   
    /**
     * Constructor that constructs a new panel.
     * Adds the necessary components to this panel 
     * and sets all the listeners.
     *
     * @param funcs An array of string that represents the
     * (parsed) function (output) objects created.
     */     
   
   public OutputGraphPanel()
   {  
      graph = new Graph();
      x = new Variable("x");
      parser = new Parser();
      parser.add(x);

      canvas = new DisplayCanvas();
      canvas.setHandleMouseZooms(true);
      canvas.add( new Panner() );
      
      controlPanel = new LimitControlPanel(LimitControlPanel.ALL_BUTTONS);
      controlPanel.addCoords(canvas);
      
      grid = new Grid();
      grid.setVisible(false);
      canvas.add(grid);
      
      JRadioButton showGrid = new JRadioButton("Show Grid")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    if (isSelected()) grid.setVisible(false);
                    else grid.setVisible(true);
                }
            }
       }; 
      controlPanel.addComponent(showGrid);
       
      JButton prev = new JButton("Prev Func")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    if (index > 0) showFunc(--index);
                }
            }
       }; 
      controlPanel.addComponent(prev);          
      
      JButton next = new JButton("Next Func")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    if (index < length-1) showFunc(++index);
                }
            }
       }; 
      controlPanel.addComponent(next);      
   }
   
   public OutputGraphPanel(String[] funcs)
   {
       this();
       setDisplayedFuncs(funcs);
   }

    /**
     * After constructing this panel further initialization
     * is needed for the panel to be shown. This initialization
     * accepts a <code>DrawedGraph</code> object that was created
     * using the <code>InputGraphPanel</code>.
     *
     * @param drawGraph The graph drawed by the user from the <code>InputGraphPanel</code>.
     */      
   
   public synchronized void init() 
   {
      CoordinateRect coords = canvas.getCoordinateRect();
      VariableInput xInput = new VariableInput();
      
      xSlider = new VariableSlider( coords.getValueObject(CoordinateRect.XMIN), 
                                    coords.getValueObject(CoordinateRect.XMAX) );
      info = new DrawString("x = #\nf(x) = #");
      info.setFont( new Font("SansSerif",Font.BOLD,12) );
      info.setColor( new Color(0,100,0) );
      info.setOffset(10);
      
      input = new ExpressionInput();
      input.setParser(parser);
            
      //setBackground(Color.lightGray);      
      
      JCMPanel topLeft = new JCMPanel(new FlowLayout());
      topLeft.setInsetGap(2);
      topLeft.add(new JLabel(" Displayed: f(x) = "));

      JCMPanel topCenter = new JCMPanel();
      topCenter.setInsetGap(3);
      topCenter.add(input);
      
      JCMPanel center = new JCMPanel();
      center.setInsetGap(3);
      center.add(canvas);              
      
      JCMPanel botCenter = new JCMPanel();
      botCenter.setInsetGap(3);
      botCenter.add(xSlider);        
      
      JCMPanel botRight = new JCMPanel(new FlowLayout());
      botRight.setInsetGap(3);
      botRight.add(xInput);       
      
      GridBagLayout layout = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      this.setLayout(layout);

      c.fill = GridBagConstraints.BOTH;
      c.weightx = 0.05;
      c.weighty = 0.00;
      c.gridheight = 1;
 
      layout.setConstraints(topLeft, c);
      this.add(topLeft);
      
      c.weightx = 0.90;
      c.gridwidth = GridBagConstraints.RELATIVE;
      
      layout.setConstraints(topCenter, c);
      this.add(topCenter);      
      
      c.weightx = 0.05;
      c.gridwidth = GridBagConstraints.REMAINDER; //end row
      
      JPanel panel = new JPanel();
      layout.setConstraints(panel, c);
      this.add(panel);        
      
      c.weightx = 0.00;
      c.weighty = 0.90;
      c.gridheight = 10;
      c.gridwidth = GridBagConstraints.RELATIVE;
      
      layout.setConstraints(center, c);
      this.add(center); 
      
      c.gridwidth = GridBagConstraints.REMAINDER; //end row
      layout.setConstraints(controlPanel, c);
      this.add(controlPanel);
      
      c.weighty = 0.00;
      c.gridheight = 1;
      c.gridwidth = GridBagConstraints.RELATIVE;
      
      layout.setConstraints(botCenter, c);
      this.add(botCenter);
      
      c.gridwidth = GridBagConstraints.REMAINDER; //end row
      
      layout.setConstraints(botRight, c);
      this.add(botRight);   
      
      showFunc(0);
      
      canvas.add(new Axes("X","Y"));
      canvas.add(graph);
      canvas.add(info);
      canvas.add( new DrawBorder(Color.darkGray, 3) );
      
      this.gatherInputs();
      
      Controller controller = this.getController();
      coords.setOnChange(controller);   
      controller.add(new Tie(xSlider,xInput));
      
      canvas.add(new Crosshair(xSlider, func)); 
 
   }  // end init()  
   
   public void init(DrawGraph drawGraph)
   {
       init();
       addDrawGraph(drawGraph);
   }
   
   private void showFunc(int i)
   {
       input.setText(funcs[i]);
       func = input.getFunction(x); 
       input.checkInput();
       graph.setFunction(func);
       info.setValues(new Value[]{ xSlider, new ValueMath(func, xSlider) });
   }    
   
   public void setDisplayedFuncs(String[] funcs)
   {
      this.funcs = funcs;
      this.length = funcs.length;
   }   
   
   public void setAxisLimits(double[] limits)
   {
       controlPanel.setLimits(limits);
       canvas.getCoordinateRect().setLimits(limits);
       canvas.getCoordinateRect().setRestoreBuffer();
   }
   
   public void addDrawGraph(DrawGraph drawGraph)
   {
       canvas.add(drawGraph);
       //drawGraph.setCoords(canvas.getCoordinateRect());
   }
   
} // end class SimpleGraph