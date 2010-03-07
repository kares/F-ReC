
package frec.gui;

import frec.jcm.core.*;
import frec.jcm.draw.*;
import frec.jcm.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

    /**
     * Class <code> InputGraphPanel </code> is an input graphical
     * user interface used to provide data for the application
     * by getting input from the user.
     */

public class InputGraphPanel extends JCMPanel
{
   private DrawingCanvas canvas;
   private DrawGraph drawGraph;
   private LimitControlPanel controlPanel;
   private Parser parser;
   private Variable x;
   private VariableSlider xSlider;
   private ExpressionInput input;
   private Graph graph;
   private Grid grid;
   private Crosshair crosshair;
   private DrawString info;
   private JButton graphFunc;
   
   public float[] datax, datay;
   private int data_size;
   private boolean data = false;
   
    /**
     * Constructor that constructs a new panel.
     * Adds the components to this panel and sets the listeners.
     */   
   
   public InputGraphPanel()
   {
      x = new Variable("x");
      parser = new Parser();
      parser.add(x);

      canvas = new DrawingCanvas();
      canvas.add( new Panner() );
      
      controlPanel = new LimitControlPanel(LimitControlPanel.ALL_BUTTONS);
      controlPanel.addCoords(canvas);  
      
      graph = new Graph();
      grid = new Grid();
      grid.setVisible(false);
      canvas.add(grid);
      
      final JRadioButton showGrid = new JRadioButton("Show Grid")
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
      
      final JRadioButton showFunc = new JRadioButton("Show Func")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    if (isSelected())
                    {
                        ((TextField)input).setText("");
                        input.setEditable(false);
                        xSlider.setEnabled(false);
                        removeFunc();
                        canvas.repaint();
                    }
                    else
                    {
                        ((TextField)input).setText("");
                        input.setEditable(true);
                        xSlider.setEnabled(true);
                    }
                }
            }
       }; 
      controlPanel.addComponent(showFunc);       
   
      final JButton clear = new JButton("Clear")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    drawGraph.deleteLines();
                    canvas.setNewDrawing();
                    canvas.doRedraw(0);
                }
            }
       };
      controlPanel.addComponent(clear);      
      
      final JButton done = new JButton("Done")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    setData();
                    setAxisLimits();
                    canvas.releaseResources();
                }
            }
       };
      controlPanel.addComponent(done);  
      
      graphFunc = new JButton("GRAPH")
      {
            public void processMouseEvent(MouseEvent evt) 
            {
                super.processMouseEvent(evt);
                if (evt.getID() == MouseEvent.MOUSE_PRESSED) 
                {
                    if (showFunc.isSelected())
                    {
                        showFunc();
                        canvas.repaint();
                    }
                }
            }
            
       }; 
   }
   
    /**
     * Method which indicates that this panel
     * already has the input data needed to compute.
     */
   
   public boolean hasData()
   {
       return data;
   }
   
   private void showFunc()
   {
       input.setParser(parser);
       input.checkInput();
       Function func = input.getFunction(x);
       graph.setFunction(func);
       info.setString("x = #\nf(x) = #");
       info.setValues(new Value[] { xSlider, new ValueMath(func, xSlider) });
                                   
       info.setFont( new Font("SansSerif",Font.BOLD,12) );
       info.setColor( new Color(0,100,0) );
       info.setOffset(10);  
       canvas.add(graph);
       crosshair = new Crosshair(xSlider, func);
       canvas.add(crosshair);
   }
   
   private void removeFunc()
   {
       input.setParser(null);
       graph.setFunction(null);  
       canvas.rem(graph);
       canvas.rem(crosshair);
       info.setString("x = \nf(x) = ");
       crosshair = null;
   }   

    /**
     * After constructing this panel further initialization
     * is needed for the panel to be shown.
     */   
   
   public synchronized void init() 
   {
      CoordinateRect coords = canvas.getCoordinateRect(); 
      
      drawGraph = new DrawGraph();
      //drawGraph.setCoords(coords);
      canvas.add(drawGraph);
      
      input = new ExpressionInput();
      input.setEditable(false);
      ((TextField)input).setText(" ... click \"Show Func\" insert \"Function Expression (f(x))\" here and click \"GRAPH\" ... ");
      VariableInput xInput = new VariableInput();   
      
      xSlider = new VariableSlider( coords.getValueObject(CoordinateRect.XMIN), 
                                    coords.getValueObject(CoordinateRect.XMAX) );
      xSlider.setEnabled(false);
           
      //setBackground(Color.lightGray);
      
      JCMPanel topLeft = new JCMPanel(new FlowLayout());
      topLeft.setInsetGap(2);
      topLeft.add(new JLabel(" Showing: f(x) = "));

      JCMPanel topCenter = new JCMPanel();
      topCenter.setInsetGap(3);
      topCenter.add(input);
      
      JCMPanel topRight = new JCMPanel(new FlowLayout());
      //topRight.setInsetGap(1);
      topRight.add(graphFunc);      
      
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
      
      layout.setConstraints(topRight, c);
      this.add(topRight);        
      
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
      
      canvas.add( new Axes("X","Y") );  // Add a set of axes to the DisplayCanvas.

      canvas.add( new DrawBorder(Color.darkGray, 3) );  // Add a 2-pixel dark gray border around
                                                        //   edges of the canvas.
      
      info = new DrawString("x = \nf(x) = ", DrawString.TOP_LEFT);
                                   
      info.setFont( new Font("SansSerif",Font.BOLD,12) );
      info.setColor( new Color(0,100,0) );
      info.setOffset(10);  
      canvas.add(info);  // Add the DrawString to the canvas.       

      this.gatherInputs();
      
      Controller controller = this.getController();  
      
      coords.setOnChange(controller); 
      controller.add( new Tie(xSlider, xInput) ); 
 
   }  // end init()
   
    /**
     * Get the <code>DrawedGraph</code> object which is
     * an abstraction of what the user draws on this panel.
     *
     * @return User drawn graph object representation.
     */     
   
   public DrawGraph getDrawGraph()
   {
       return drawGraph;
   }   
   
    /**
     * This is used to set the data size, that means
     * to set the approximation (size) of data that will
     * be used from the user (input) graph.
     *
     * @param size The new data size.
     */     
   
   public void setDataSize(int size)
   {
       this.data_size = size;
       this.datax = new float[data_size];
       this.datay = new float[data_size];
   }   
   
    /**
     * This returns the axis limits (xmin, xmax, ymin, ymax)
     * of this panel (panel's canvas).
     */        
   
   public double[] getAxisLimits()
   {
       return axisLimits;
   }

   private double[] axisLimits;
   
   private void setAxisLimits()
   {
       axisLimits = controlPanel.getLimits();
   }
   
   private synchronized void setData()
   {
        FunctionPoint[] fp = drawGraph.getFunctionPoints(data_size);
        for (int i=0; i<fp.length; i++)
        {
            datax[i] = (float)fp[i].getArgumentValue();
            datay[i] = (float)fp[i].getFunctionValue();
        }
        data = true;           
   }
   
} // end class SimpleGraph