/*
 * MegaMek - Copyright (C) 2004 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */

package megamek.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import megamek.client.ui.Messages;
import megamek.client.ui.swing.util.ImageFileFactory;
import megamek.client.ui.swing.util.PlayerColors;
import megamek.common.Player;
import megamek.common.util.DirectoryItems;

/**
 * This dialog allows players to select the camo pattern (or color) used by
 * their units during the game. It automatically fills itself with all the color
 * choices in <code>Settings</code> and all the camo patterns in the
 * "data/iamges/camo" directory tree.
 * <p/>
 * Created on January 19, 2004
 * 
 * @author James Damour
 * @version 1
 */
public class CamoChoiceDialog extends JDialog implements 
        ListSelectionListener {

    private static final long serialVersionUID = 9220162367683378065L;

    /**
     * The parent <code>Frame</code> of this dialog.
     */
    private JFrame frame;

    /**
     * The categorized camo patterns.
     */
    private DirectoryItems camos;

    /**
     * The menu containing the category names.
     */
    JComboBox categories;

    /**
     * The list containing the item names.
     */
    JList items;
    private JScrollPane scrItems;

    /**
     * The "keep old camo" button.
     */
    private JButton keep;

    /**
     * The "select new camo" button.
     */
    JButton select;

    /**
     * The button that launched this dialog
     */
    JButton sourceButton;

    /**
     * The previously selected category.
     */
    String prevCat;

    /**
     * The previously selected item.
     */
    String prevItem;

    /**
     * Player that is changing his pants
     */
    Player player;

    /**
     * Create a dialog that allows players to choose a camo pattern.
     * 
     * @param parent
     *            - the <code>Frame</code> that displays this dialog.
     */
    public CamoChoiceDialog(JFrame parent, JButton button) {

        // Initialize our superclass and record our parent frame.
        super(parent, Messages
                .getString("CamoChoiceDialog.select_camo_pattern"), true); //$NON-NLS-1$
        frame = parent;
        sourceButton = button;

        // Declare local variables.
        Iterator<String> names;
        String name;

        // Parse the camo directory.
        try {
            camos = new DirectoryItems(new File("data/images/camo"), "", //$NON-NLS-1$ //$NON-NLS-2$
                    ImageFileFactory.getInstance());
        } catch (Exception e) {
            camos = null;
        }

        // Use a border layout.
        getContentPane().setLayout(new BorderLayout());

        // Create a pulldown menu for the categories.
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints layout = new GridBagConstraints();
        layout.anchor = GridBagConstraints.CENTER;
        categories = new JComboBox();
        panel.add(categories, layout);

        // Fill the pulldown. Include the "no camo" category.
        // Make sure the "no camo" and "root camo" are at top.
        // Only add the "root camo" category if it contains items.
        categories.addItem(Player.NO_CAMO);
        if (camos != null) {
            if (camos.getItemNames("").hasNext()) { //$NON-NLS-1$
                categories.addItem(Player.ROOT_CAMO);
            }
            names = camos.getCategoryNames();
            while (names.hasNext()) {
                name = names.next();
                if (!"".equals(name)) { //$NON-NLS-1$
                    categories.addItem(name);
                }
            }
        }

        // Refill the item list when a new category is selected.
        // Make sure that the "select new camo" button is updated.
        categories.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    fillList((String) event.getItem());
                    updateButton();
                }
            }
        });

        // Create a list to hold the items in the category.
        items = new JList(new DefaultListModel());
        scrItems = new JScrollPane(items);
        scrItems
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrItems);

        // Update the "select new camo" when an item is selected.
        items.addListSelectionListener(this);
        items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create a panel to hold our buttons.
        // Use a grid bag layout.
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getContentPane().add(panel, BorderLayout.EAST);
        layout = new GridBagConstraints();
        layout.anchor = GridBagConstraints.EAST;
        layout.gridx = 0;
        layout.gridy = 0;
        layout.gridwidth = 1;
        layout.gridheight = 1;
        layout.fill = GridBagConstraints.NONE;
        layout.ipadx = 4;
        layout.ipady = 4;
        layout.weightx = 0.0;
        layout.weighty = 0.0;

        // Add a "spacer" label to push everything else to the bottom.
        layout.weighty = 1.0;
        panel.add(new JLabel(), layout);
        layout.weighty = 0.0;
        layout.gridy++;

        // Add a label for the "keep old camo" button.
        panel.add(new JLabel(Messages
                .getString("CamoChoiceDialog.keep_old_camo")), layout); //$NON-NLS-1$
        layout.gridy++;

        // Create the "keep old camo" button.
        keep = new JButton();
        keep.setPreferredSize(new Dimension(84, 72));
        InputMap inputMap = getRootPane().getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
                "Keep");

        ActionMap actionMap = getRootPane().getActionMap();
        Action keepAction = new AbstractAction() {
            private static final long serialVersionUID = 2096792571263188573L;
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        actionMap.put("Keep", keepAction);
        keep.setAction(keepAction);

        panel.add(keep, layout);
        layout.gridy++;

        // Add a label for the "select new camo" button.
        panel.add(new JLabel(Messages
                .getString("CamoChoiceDialog.select_new_camo")), layout); //$NON-NLS-1$
        layout.gridy++;

        // Create the "select new camo" button.
        select = new JButton();
        select.setPreferredSize(new Dimension(84, 72));
        panel.add(select, layout);

        // Fire the "select new camo" action when the enter key is pressed
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
                "Accept");

        Action acceptAction = new AbstractAction() {
            private static final long serialVersionUID = 402810917672002505L;
            public void actionPerformed(ActionEvent e) {
                // Did the worker change their selection?
                String curCat = (String) categories.getSelectedItem();
                String curItem = (String) items.getSelectedValue();
                if (!curCat.equals(prevCat) || !curItem.equals(prevItem)) {

                    // Save the new values.
                    setPrevSelection(curCat, curItem);

                    // Update the local player's camo info.
                    player.setCamoCategory(prevCat);
                    if (Player.NO_CAMO.equals(prevCat)) {
                        player.setColorIndex(items.getSelectedIndex());
                        player.setCamoFileName(prevItem);
                    } else {
                        player.setCamoFileName(prevItem);
                        player.setColorIndex(0);
                    }
                    sourceButton.setIcon(generateIcon(prevCat, prevItem));
                    
                } // End selection-changed

                // Now exit.
                setVisible(false);
            }
        };

        actionMap.put("Accept", acceptAction);
        select.setAction(acceptAction);

        // Fill the item list with the colors.
        fillList(Player.NO_CAMO);

        // Perform the initial layout.
        pack();
    }
    
    /**
     * A helper function to fill the list with items in the selected category.
     * 
     * @param category
     *            - the <code>String</code> name of the category whose items
     *            should be displayed.
     */
    void fillList(String category) {

        // Clear the list of items.
        ((DefaultListModel) items.getModel()).removeAllElements();

        // If this is the "no camos" category, then
        // fill the item list with the colors.
        if (Player.NO_CAMO.equals(category)) {
            for (String color : Player.colorNames) {
                ((DefaultListModel) items.getModel()).addElement(color);
            }
        }

        // Otherwise, fill the list with the camo names.
        else {

            // Translate the "root camo" category name.
            Iterator<String> camoNames;
            if (Player.ROOT_CAMO.equals(category)) {
                camoNames = camos.getItemNames(""); //$NON-NLS-1$
            } else {
                camoNames = camos.getItemNames(category);
            }

            // Get the camo names for this category.
            while (camoNames.hasNext()) {
                ((DefaultListModel) items.getModel()).addElement(camoNames
                        .next());
            }
        }
        items.setSelectedIndex(0);
    }

    /**
     * A helper function to assign values for the previously selected camo. This
     * function will also set the "keep old camo" button's image.
     * 
     * @param category
     *            - the <code>String</code> category name. This value must be
     *            one of the categories from the <code>DirectoryItems</code>.
     * @param item
     *            - the <code>String</code> name of the item. This value must be
     *            one of the items in the named category from
     *            <code>DirectoryItems</code>.
     */
    void setPrevSelection(String category, String item) {
        prevCat = category;
        prevItem = item;
        keep.setIcon(generateIcon(prevCat, prevItem));
    }

    Icon generateIcon(String cat, String item) {
        String actualCat = cat;
        // Replace the ROOT_CAMO string with "".
        if (Player.ROOT_CAMO.equals(actualCat)) {
            actualCat = ""; //$NON-NLS-1$
        }

        int colorInd = -1;
        //no camo, just color
        if (Player.NO_CAMO.equals(actualCat)) {
            for (int color = 0; color < Player.colorNames.length; color++) {
                if (Player.colorNames[color].equals(item)) {
                    colorInd = color;
                    break;
                }
            }
            if (colorInd == -1) {
                colorInd = 0;
            }
            BufferedImage tempImage = new BufferedImage(84, 72,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = tempImage.createGraphics();
            graphics.setColor(PlayerColors.getColor(colorInd));
            graphics.fillRect(0, 0, 84, 72);
            return new ImageIcon(tempImage);
        }
        
        //an actual camo
        try {
            // We need to copy the image to make it appear.
            Image image = (Image) camos.getItem(actualCat, item);

            return new ImageIcon(image);
        } catch (Exception err) {
            // Print the stack trace and display the message.
            err.printStackTrace();
            JOptionPane
                    .showMessageDialog(
                            frame,
                            err.getMessage(),
                            Messages
                                    .getString("CamoChoiceDialog.error_getting_camo"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Update the "select new camo" button whenever a list item is selected.
     * <p/>
     */
    void updateButton() {
        // Get the category and the item.
        String curCat = (String) categories.getSelectedItem();
        String curItem = (String) items.getSelectedValue();
        if (curItem == null) {
            //nothing selected yet
            select.setIcon(null);
            return;
        }
        select.setIcon(generateIcon(curCat, curItem));
    }

    /**
     * Set the selected category.
     * 
     * @param category
     *            - the <code>String</code> name of the desired category. This
     *            value may be <code>null</code>. If no match is found, the
     *            category will not change.
     */
    private void setCategory(String category) {

        // Get the current selection.
        String cur = (String) categories.getSelectedItem();

        // Do nothing, if the request is for the selected item.
        if (!cur.equals(category)) {

            // Try to find the requested item.
            for (int loop = 0; loop < categories.getItemCount(); loop++) {

                // Did we find it?
                if (categories.getItemAt(loop).equals(category)) {

                    // Select this position.
                    categories.setSelectedIndex(loop);

                    // Fill the list.
                    fillList(category);

                    // Stop looking for the category.
                    break;

                } // End found-requested-category

            } // Check the next category

        } // End new-selection

    }

    /**
     * Set the selected item in the currently-selected category.
     * 
     * @param item
     *            - the <code>String</code> name of the desired item. This value
     *            may be <code>null</code>. If no match is found, the item
     *            selection will not change.
     */
    private void setItemName(String item) {

        // Do nothing is we're passed a null.
        if (item != null) {

            // Get the current selection.
            String cur = (String) items.getSelectedValue();

            // Do nothing, if the request is for the selected item.
            if (!item.equals(cur)) {
                items.setSelectedValue(item, true);
            } // End new-selection

        } // End not-passed-null
    }

    /**
     * Show the dialog. Make sure that all selections have been applied.
     * <p/>
     * Overrides <code>Dialog#setVisible(boolean)</code>.
     */
    @Override
    public void setVisible(boolean visible) {

        // Make sure the "keep" button is set correctly.
        setPrevSelection((String) categories.getSelectedItem(), (String) items
                .getSelectedValue());

        // Make sure the "select" button is set correctly.
        updateButton();

        // Now show the dialog.
        super.setVisible(visible);
    }

    public void valueChanged(ListSelectionEvent event) {
        updateButton();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        if (Player.NO_CAMO.equals(player.getCamoCategory())) {
            setCategory(Player.NO_CAMO);
            setItemName(Player.colorNames[player.getColorIndex()]);
            setPrevSelection(Player.NO_CAMO, Player.colorNames[player
                    .getColorIndex()]);
        } else {
            setCategory(player.getCamoCategory());
            setItemName(player.getCamoFileName());
            setPrevSelection(player.getCamoCategory(), player.getCamoFileName());
        }
        if (sourceButton.isVisible()) {
            sourceButton.setIcon(generateIcon(prevCat, prevItem));
        }
    }
}
