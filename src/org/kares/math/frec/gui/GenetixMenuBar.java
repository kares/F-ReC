/*
 * Copyright 2004 Karol Bucek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kares.math.frec.gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.*;

// NOTE: UNUSED
class GenetixMenuBar extends JMenuBar {
    private JMenu menu;
    private JMenuItem menuItem;
    private JRadioButtonMenuItem rbMenuItem;
    private JCheckBoxMenuItem cbMenuItem;
    private Color dfColor = Color.LIGHT_GRAY.darker();

    public GenetixMenuBar() {
        super();
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
