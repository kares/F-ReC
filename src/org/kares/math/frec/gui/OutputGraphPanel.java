package org.kares.math.frec.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.kares.math.frec.jcm.awt.*;
import org.kares.math.frec.jcm.data.*;
import org.kares.math.frec.jcm.draw.*;

/**
 * Class <code> OutputGraphPanel </code> is an output graphical
 * user interface used to provide a graphical environment
 * for the output data.
 */
public class OutputGraphPanel extends JCMPanel {

    private final DisplayCanvas canvas;
    private final LimitControlPanel controlPanel;
    private final Parser parser;
    private final Variable x;
    private VariableSlider xSlider;
    private ExpressionInput input;
    private Function function;
    private final Graph graph;
    private final Grid grid;
    private Crosshair crosshair;
    private DrawString info;
    private String[] functionInputs;
    private int length;
    private int index;
    private final JButton prev;
    private final JButton next;

    /**
     * Constructor that constructs a new panel.
     * Adds the necessary components to this panel 
     * and sets all the listeners.
     *
     * @param function An array of string that represents the
     * (parsed) function (output) objects created.
     */
    private OutputGraphPanel() {
        graph = new Graph();
        x = new Variable("x");
        parser = new Parser();
        parser.add(x);

        canvas = new DisplayCanvas();
        canvas.setHandleMouseZooms(true);
        canvas.add(new Panner());

        controlPanel = new LimitControlPanel(LimitControlPanel.ALL_BUTTONS);
        controlPanel.addCoords(canvas);

        grid = new Grid();
        grid.setVisible(false);
        canvas.add(grid);

        final JRadioButton showGrid = new JRadioButton("Show Grid");
        showGrid.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if ( showGrid.isSelected() ) {
                    grid.setVisible(false);
                } else {
                    grid.setVisible(true);
                }
            }

        });
        controlPanel.addComponent(showGrid);

        prev = new JButton("Prev Func");
        controlPanel.addComponent(prev);

        next = new JButton("Next Func");
        controlPanel.addComponent(next);

        prev.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if ( index > 0 ) showFunction(--index);
            }

        });
        next.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if ( index < length - 1 ) showFunction(++index);
            }

        });

        init();
    }

    public OutputGraphPanel(String[] funcs) {
        this();
        setDisplayedFunctions(funcs);
    }

    private void adjustButtonStates() {
        if ( index <= 0 ) {
            prev.setEnabled(false);
        }
        else if ( ! prev.isEnabled() ) {
            prev.setEnabled(true);
        }
        
        if ( index >= length - 1 ) {
            next.setEnabled(false);
        }
        else if ( ! next.isEnabled() ) {
            next.setEnabled(true);
        }
    }

    /**
     * After constructing this panel further initialization
     * is needed for the panel to be shown. This initialization
     * accepts a <code>DrawedGraph</code> object that was created
     * using the <code>InputGraphPanel</code>.
     *
     * @param drawGraph The graph drawed by the user from the <code>InputGraphPanel</code>.
     */
    private void init() {
        CoordinateRect coords = canvas.getCoordinateRect();
        VariableInput xInput = new VariableInput();

        xSlider = new VariableSlider(coords.getValueObject(CoordinateRect.XMIN),
                coords.getValueObject(CoordinateRect.XMAX));
        info = new DrawString("x = #\nf(x) = #");
        info.setFont(new Font("SansSerif", Font.BOLD, 12));
        info.setColor(new Color(0, 100, 0));
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

        canvas.add(new Axes("X", "Y"));
        canvas.add(graph);
        canvas.add(info);
        canvas.add(new DrawBorder(Color.darkGray, 3));

        this.gatherInputs();

        Controller controller = this.getController();
        coords.setOnChange(controller);
        controller.add(new Tie(xSlider, xInput));

    }  // end init()

    private Crosshair getCrosshair() {
        if (crosshair == null) {
            crosshair = new Crosshair(xSlider, function);
            canvas.add(crosshair);
        }
        return crosshair;
    }

    private void showFunction(final int i) {
        input.setText(functionInputs[i]);
        input.checkInput(); // force text parsing
        function = input.getFunction(x);
        graph.setFunction(function);
        Crosshair crosshair = getCrosshair();
        ValueMath funcMath = new ValueMath(function, xSlider);
        crosshair.setPoints(xSlider, funcMath, crosshair.getH(), crosshair.getV());
        info.setValues(new Value[]{xSlider, funcMath});

        adjustButtonStates();
    }

    public void setDisplayedFunctions(final String[] functions) {
        this.functionInputs = functions;
        this.length = functions != null ? functions.length : 0;
        if ( length > 0 ) showFunction(0);
    }

    public void setAxisLimits(double[] limits) {
        controlPanel.setLimits(limits);
        canvas.getCoordinateRect().setLimits(limits);
        canvas.getCoordinateRect().setRestoreBuffer();
    }

    public void addDrawGraph(DrawGraph drawGraph) {
        canvas.add(drawGraph);
        if (drawGraph != null) {
            drawGraph.setCoords(canvas.getCoordinateRect());
        }
    }
    
} // end class SimpleGraph
