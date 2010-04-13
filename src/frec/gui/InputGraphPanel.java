package frec.gui;

import frec.jcm.data.*;
import frec.jcm.draw.*;
import frec.jcm.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Class <code> InputGraphPanel </code> is an input graphical
 * user interface used to provide hasData for the application
 * by getting input from the user.
 */
public class InputGraphPanel extends JCMPanel {

    private final DrawingCanvas drawCanvas;
    private DrawGraph drawGraph;
    private final LimitControlPanel controlPanel;
    private final Parser parser;
    private final Variable x;
    private VariableSlider xSlider;
    private ExpressionInput input;
    private final Graph graph;
    private final Grid grid;
    private Crosshair crosshair;
    private DrawString info;
    private final JButton doneButton;
    private final JButton showGraphButton;
    private double[] axisLimits;

    private float[] dataX, dataY;
    private int dataSize;
    private boolean hasData = false;

    /**
     * Constructor that constructs a new panel.
     * Adds the components to this panel and sets the listeners.
     */
    public InputGraphPanel() {
        x = new Variable("x");
        parser = new Parser();
        parser.add(x);

        drawCanvas = new DrawingCanvas();
        drawCanvas.add(new Panner());

        controlPanel = new LimitControlPanel(LimitControlPanel.ALL_BUTTONS);
        controlPanel.addCoords(drawCanvas);

        final DrawListener drawListener = new DrawListener();
        drawCanvas.addMouseListener(drawListener);
        drawCanvas.addMouseMotionListener(drawListener);

        graph = new Graph();
        grid = new Grid();
        grid.setVisible(false);
        drawCanvas.add(grid);

        final JRadioButton showGrid = new JRadioButton("Show Grid");
        showGrid.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                grid.setVisible( showGrid.isSelected() );
            }
            
        });
        controlPanel.addComponent(showGrid);

        /*
        final JRadioButton showFunction = new JRadioButton("Show Func");
        showFunction.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if (showFunction.isSelected()) {
                    input.setText("");
                    input.setEditable(false);
                    xSlider.setEnabled(false);
                    removeFunction();
                    canvas.repaint();
                } else {
                    input.setText("");
                    input.setEditable(true);
                    xSlider.setEnabled(true);
                }
            }
        });
        controlPanel.addComponent(showFunction);
        */

        final JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                drawCanvas.resetDrawing();
                drawCanvas.doRedraw(0);
            }

        });
        controlPanel.addComponent(clearButton);

        doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                storeData();
                setAxisLimits();
                drawCanvas.releaseResources();
            }
            
        });
        doneButton.setEnabled(false);
        controlPanel.addComponent(doneButton);

        showGraphButton = new JButton("Graph");
        final ActionListener showGraphHandler = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                showFunction();
            }
            
        };
        showGraphButton.addActionListener(showGraphHandler);

        drawGraph = new DrawGraph();
        //drawGraph.setCoords(coords);
        drawCanvas.add(drawGraph);

        input = new ExpressionInput();
        input.setParser(parser);
        input.setEditable(false);
        //input.setText(" ... click \"Show Func\" insert \"Function Expression (f(x))\" here and click \"GRAPH\" ... ");
        final String inputText =
                " HINT: type function expression (e.g. sin(x)) here and click '"+ showGraphButton.getText() +"' to show graph ";
        input.setText(inputText);
        input.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (inputText.equals(input.getText())) {
                    input.setText("");
                    input.setEditable(true);
                }
            }

        });
        // handle pressing enter in the TextField :
        input.addActionListener(showGraphHandler);

        CoordinateRect coords = drawCanvas.getCoordinateRect();
        xSlider = new VariableSlider(coords.getValueObject(CoordinateRect.XMIN),
                coords.getValueObject(CoordinateRect.XMAX));
        xSlider.setEnabled(false);
        
        init();
    }

    /**
     * After constructing this panel further initialization
     * is needed for the panel to be shown.
     */
    private void init() {
        final VariableInput xInput = new VariableInput();

        //setBackground(Color.lightGray);

        JCMPanel topLeft = new JCMPanel(new FlowLayout());
        topLeft.setInsetGap(2);
        topLeft.add(new JLabel(" Sample Function: f(x) = "));

        JCMPanel topCenter = new JCMPanel();
        topCenter.setInsetGap(3);
        topCenter.add(input);

        JCMPanel topRight = new JCMPanel(new FlowLayout());
        //topRight.setInsetGap(1);
        topRight.add(showGraphButton);

        JCMPanel center = new JCMPanel();
        center.setInsetGap(3);
        center.add(drawCanvas);

        JCMPanel botCenter = new JCMPanel();
        botCenter.setInsetGap(3);
        botCenter.add(xSlider);

        JCMPanel botRight = new JCMPanel(new FlowLayout());
        botRight.setInsetGap(3);
        botRight.add(xInput);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(layout);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.05;
        c.weighty = 0.00;
        c.gridheight = 1;

        layout.setConstraints(topLeft, c);
        add(topLeft);

        c.weightx = 0.90;
        c.gridwidth = GridBagConstraints.RELATIVE;

        layout.setConstraints(topCenter, c);
        add(topCenter);

        c.weightx = 0.05;
        c.gridwidth = GridBagConstraints.REMAINDER; //end row

        layout.setConstraints(topRight, c);
        add(topRight);

        c.weightx = 0.00;
        c.weighty = 0.90;
        c.gridheight = 10;
        c.gridwidth = GridBagConstraints.RELATIVE;

        layout.setConstraints(center, c);
        add(center);

        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        layout.setConstraints(controlPanel, c);
        add(controlPanel);

        c.weighty = 0.00;
        c.gridheight = 1;
        c.gridwidth = GridBagConstraints.RELATIVE;

        layout.setConstraints(botCenter, c);
        add(botCenter);

        c.gridwidth = GridBagConstraints.REMAINDER; //end row

        layout.setConstraints(botRight, c);
        add(botRight);

        drawCanvas.add(new Axes("X", "Y"));  // Add a set of axes to the DisplayCanvas.
        drawCanvas.add(new DrawBorder(Color.darkGray, 3));  // Add a 2-pixel dark gray border around
        //   edges of the canvas.

        info = new DrawString("x = \nf(x) = ", DrawString.TOP_LEFT);
        info.setFont(new Font("SansSerif", Font.BOLD, 12));
        info.setColor(new Color(0, 100, 0));
        info.setOffset(10);
        drawCanvas.add(info);  // Add the DrawString to the canvas.

        gatherInputs();

        CoordinateRect coords = drawCanvas.getCoordinateRect();
        final Controller controller = getController();
        coords.setOnChange(controller);
        controller.add(new Tie(xSlider, xInput));

    }  // end init()

    /**
     * Method which indicates that this panel
     * already has the input hasData needed to compute.
     */
    public boolean hasData() {
        return hasData;
    }

    private transient Color validInputBackground = null;

    private void showFunction() {
        input.checkInput();
        final Color invalidColor = Color.RED;
        if ( input.getErrorMessage() == null ) {
            if (invalidColor.equals(input.getBackground())) {
                input.setBackground(validInputBackground);
            }
            Function func = input.getFunction(x);
            graph.setFunction(func);
            info.setString("x = #\nf(x) = #");
            info.setValues(new Value[]{xSlider, new ValueMath(func, xSlider)});
            //info.setFont(new Font("SansSerif", Font.BOLD, 12));
            //info.setColor(new Color(0, 100, 0));
            //info.setOffset(10);
            drawCanvas.add(graph);
            crosshair = new Crosshair(xSlider, func);
            drawCanvas.add(crosshair);
            drawCanvas.repaint();
        }
        else {
            Color inputColor = input.getBackground();
            if ( ! invalidColor.equals(inputColor) ) {
                validInputBackground = inputColor;
                input.setBackground(invalidColor);
                // TODO show error message
            }
        }
    }

    private void removeFunction() {
        input.setParser(null);
        graph.setFunction(null);
        drawCanvas.rem(graph);
        drawCanvas.rem(crosshair);
        info.setString("x = \nf(x) = ");
        crosshair = null;
    }

    /**
     * Get the <code>DrawedGraph</code> object which is
     * an abstraction of what the user draws on this panel.
     *
     * @return User drawn graph object representation.
     */
    public DrawGraph getDrawGraph() {
        return drawGraph;
    }

    /**
     * This is used to set the hasData size, that means
     * to set the approximation (size) of hasData that will
     * be used from the user (input) graph.
     *
     * @param size The new hasData size.
     */
    public void setDataSize(int size) {
        this.dataSize = size;
        this.dataX = new float[dataSize];
        this.dataY = new float[dataSize];
    }

    /**
     * This returns the axis limits (xmin, xmax, ymin, ymax)
     * of this panel (panel's canvas).
     */
    public double[] getAxisLimits() {
        return axisLimits;
    }

    private void setAxisLimits() {
        axisLimits = controlPanel.getLimits();
    }

    public void storeData() {
        FunctionPoint[] fp = drawGraph.getFunctionPoints(dataSize);
        if ( fp != null && fp.length > 0 ) {
            for (int i = 0; i < fp.length; i++) {
                dataX[i] = (float) fp[i].getArgumentValue();
                dataY[i] = (float) fp[i].getFunctionValue();
            }
            hasData = true;
        }
        else {
            hasData = false;
        }
    }

    public float[] getDataX() {
        if ( ! hasData )
            throw new IllegalStateException("data not yet stored");
        return dataX;
    }

    public float[] getDataY() {
        if ( ! hasData )
            throw new IllegalStateException("data not yet stored");
        return dataY;
    }

    private void checkGraphLines() {
        if ( ! drawGraph.isEmpty() ) {
            doneButton.setEnabled(true);
        }
        else {
            doneButton.setEnabled(false);
        }
    }

    private class DrawListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {
            checkGraphLines();
        }

        public void mousePressed(MouseEvent e) {
            checkGraphLines();
        }

        public void mouseReleased(MouseEvent e) {
            checkGraphLines();
        }

        public void mouseEntered(MouseEvent e) {
            checkGraphLines();
        }

        public void mouseExited(MouseEvent e) {
            checkGraphLines();
        }
        
    }

} // end class SimpleGraph
