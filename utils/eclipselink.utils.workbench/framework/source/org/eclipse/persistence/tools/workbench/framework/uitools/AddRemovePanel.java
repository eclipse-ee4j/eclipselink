/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeAdapter;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


public abstract class AddRemovePanel extends AbstractPanel {

	private Adapter adapter;
    private NodeSelector nodeSelector;
		
	/**
	 * This holds the button group orientation choice, the default is bottom
	 * the choices are bottom, top, right, and left and are defined by the statics.
	 */
	private int buttonOrientation;
		public final static int BOTTOM = 2;
		public final static int LEFT   = 4;
		public final static int RIGHT  = 3;
		public final static int TOP    = 1;

    private JButton addButton;
    private JButton removeButton;
	private JButton optionalButton;

	private JButton upButton;
	private JButton downButton;
	
	private ObjectListSelectionModel rowSelectionModel;	
	private PropertyValueModel selectedItemHolder;
		
		
	public AddRemovePanel(ApplicationContext context, Adapter adapter, ListValueModel listModel) {
		this(context, adapter, listModel, BOTTOM);
	}

    public AddRemovePanel(ApplicationContext context, Adapter adapter,  ListValueModel listModel, int buttonOrientation) {
        this(context, adapter, listModel, buttonOrientation, new NodeSelector.DefaultNodeSelector());
    }
    
    public AddRemovePanel(ApplicationContext context, Adapter adapter,  ListValueModel listModel, int buttonOrientation, NodeSelector nodeSelector) {
        super(context);
        this.adapter = adapter;
        this.buttonOrientation = buttonOrientation;
        this.nodeSelector = nodeSelector;
        initialize(listModel);
    }
    
	protected void initialize(ListValueModel listModel) {
		this.rowSelectionModel = buildRowSelectionModel(listModel);
		listModel.addListChangeListener(this.buildListChangeListener());
		this.selectedItemHolder = buildSelectedItemHolder();
		this.addButton = buildAddButton();
		this.removeButton = buildRemoveButton();
		this.optionalButton = buildOptionalButton((this.adapter instanceof OptionAdapter) ? (OptionAdapter) this.adapter : null);
		this.upButton = buildUpButton((this.adapter instanceof UpDownAdapter) ? (UpDownAdapter) this.adapter : null);
		this.downButton = buildDownButton((this.adapter instanceof UpDownAdapter) ? (UpDownAdapter) this.adapter : null);
	}
   
    protected JMenuItem buildGoToMenuItem() {
        JMenuItem menuItem = new JMenuItem(resourceRepository().getString("SELECT_IN_NAVIGATOR_POPUP_MENU_ITEM"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        menuItem.addActionListener(this.buildGoToListener());

        return menuItem;
    }
    
    private ActionListener buildGoToListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddRemovePanel.this.goToSelectedItem();
            }
        };
    }
    
    protected KeyListener buildF3KeyListener() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F3) {
                    AddRemovePanel.this.goToSelectedItem();
                }                
            }
        };
    }
    
    private void goToSelectedItem() {
        if (getSelectedValue() != null) {
            AddRemovePanel.this.nodeSelector.selectNodeFor(getSelectedValue());
        }
    }
    
    public Object getSelectedValue() {
        return getSelectionModel().getSelectedValue();
    }
    
    
	protected ObjectListSelectionModel buildRowSelectionModel(ListValueModel listModel) {
		ObjectListSelectionModel rowSelectionModel = new ObjectListSelectionModel(new ListModelAdapter(listModel));
		rowSelectionModel.addListSelectionListener(this.buildRowSelectionListener());
		return rowSelectionModel;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					AddRemovePanel.this.rowSelectionChanged(e);
				}
			}
		};
	}

	private void rowSelectionChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
        }

		if (getSelectionModel().getSelectedValuesSize() == 1)
		{
			selectedItemHolder.setValue(getSelectionModel().getSelectedValue());
		}
		else
		{
			selectedItemHolder.setValue(null);
		}
        this.updateButtons();
		fireListSelectionEvent(e);
	}
	
	private ListChangeListener buildListChangeListener() {
		return new ListChangeAdapter() {
			public void listChanged(ListChangeEvent e) {
				AddRemovePanel.this.updateButtons();
			}
		};
	}
	
	protected void updateButtons() {
		this.updateAddButton(this.addButton);
        this.updateRemoveButton(this.removeButton);
        this.updateOptionalButton(this.optionalButton);
        this.updateUpButton(this.upButton);
        this.updateDownButton(this.downButton);
	}
    
    protected void updateAddButton(JButton addButton) {
    }
    	
    protected void updateRemoveButton(JButton removeButton) {
        removeButton.setEnabled(this.getSelectionModel().getSelectedValue() != null);
    }
    
    protected void updateOptionalButton(JButton optionalButton) {
    	if (this.adapter instanceof OptionAdapter) {
        	optionalButton.setEnabled(((OptionAdapter) this.adapter).enableOptionOnSelectionChange(this.rowSelectionModel));
    	}
    }
    
	protected void updateUpButton(JButton upButton) {       
        upButton.setEnabled(this.getSelectionModel().getMinSelectionIndex() > 0);
	}
    
    protected void updateDownButton(JButton downButton) {
        downButton.setEnabled(!this.getSelectionModel().isSelectionEmpty() && this.getSelectionModel().getMaxSelectionIndex() < getSelectionModel().getListModel().getSize() - 1);
    }
	

	/**
	 * Adds the given listener to the list. The listener will be notified when
	 * the list's selection will changed.
	 *
	 * @param listener The listener to be added
	 */
	public void addListSelectionListener(ListSelectionListener listener) {
		this.listenerList.add(ListSelectionListener.class, listener);
	}

	public void removeListSelectionListener(ListSelectionListener l) {
		this.listenerList.remove(ListSelectionListener.class, l);
	}
	
	private void fireListSelectionEvent(ListSelectionEvent e) {
		ListSelectionListener[] listeners = (ListSelectionListener[]) this.listenerList.getListeners(ListSelectionListener.class);

		for (int index = listeners.length; --index >= 0;)
			listeners[index].valueChanged(e);
	}
	
	private PropertyValueModel buildSelectedItemHolder() {
		return new SimplePropertyValueModel(null);
	}

	protected void initializeButtonPanel() {
	
		GridBagConstraints constraints = new GridBagConstraints();
		JComponent buttonPanel = buildButtonPanel();
	
		if (this.buttonOrientation == TOP) {
			constraints.gridx  = 0;
			constraints.gridy  = 0;
			constraints.insets = new Insets(0, 0, 5, 0);
			constraints.anchor = GridBagConstraints.LINE_START;
			constraints.fill   = GridBagConstraints.NONE;
		} 
		else if (this.buttonOrientation == LEFT) {
			constraints.gridx  = 0;
			constraints.gridy  = 0;
			constraints.insets = new Insets(0, 0, 0, 5);
			constraints.anchor = GridBagConstraints.PAGE_START;
			constraints.fill   = GridBagConstraints.HORIZONTAL;
		} 
		else if (this.buttonOrientation == RIGHT) {
			constraints.gridx  = 1;
			constraints.gridy  = 0;
			constraints.insets = new Insets(0, 5, 0, 0);
			constraints.anchor = GridBagConstraints.PAGE_START;
			constraints.fill   = GridBagConstraints.HORIZONTAL;
		} 
		else {
			constraints.gridx  = 0;
			constraints.gridy  = 1;
			constraints.insets = new Insets(5, 0, 0, 0);
			constraints.anchor = GridBagConstraints.LINE_END;
			constraints.fill   = GridBagConstraints.NONE;
		}
	
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
	
		add(buttonPanel, constraints);
	}
	
	/**
	 * Creates a container that will properly lay out the given buttons, the
	 * layout will be created once this method has added all its widgets, this
	 * will let any subclass the ability to add or reorder the buttons.
	 *
	 * @param addButton The button used to add a new item
	 * @param removeButton The button used to remove the selected items
	 * @param optionalButton A button that can perform additional operation
	 * over the selected item
	 * @return A new container containing the given widgets
	 */
	protected void buildButtonPanel(JComponent buttonPanel)
	{
		buttonPanel.add(this.addButton);
		buttonPanel.add(this.removeButton);
		buttonPanel.add(this.optionalButton);
		buttonPanel.add(this.upButton);
		buttonPanel.add(this.downButton);
	}

	/**
	 * Creates the button panel where the given buttons will be added to it. If
	 * the order or more buttons need to be added, then
	 * {@link #buildButtonPanel(JComponent, JButton, JButton, JButton)} needs to
	 * be used.
	 *
	 * @param addButton The button used to add a new item
	 * @param removeButton The button used to remove the selected items
	 * @param optionalButton A button that can perform additional operation
	 * over the selected item
	 * @param orientation The position of the button panel in its container
	 * @return A new container containing the given widgets
	 */
	private JComponent buildButtonPanel()
	{
		if (this.buttonOrientation == LEFT) {
			addAlignLeft(this.addButton);
			addAlignLeft(this.removeButton);
			addAlignLeft(this.optionalButton);
			addAlignLeft(this.upButton);
			addAlignLeft(this.downButton);
		}
		else if (this.buttonOrientation == RIGHT) {
			addAlignRight(this.addButton);
			addAlignRight(this.removeButton);
			addAlignRight(this.optionalButton);
			addAlignRight(this.upButton);
			addAlignRight(this.downButton);
		}

		
		AccessibleTitledPanel panel = new AccessibleTitledPanel();
		buildButtonPanel(panel);
		int count = panel.getComponentCount();

		if ((this.buttonOrientation == LEFT) ||
			 (this.buttonOrientation == RIGHT))
		{
			panel.setLayout(new GridLayout(count, 1, 0, 5));
		} 
		else  
		{
			panel.setLayout(new GridLayout(1, count, 5, 0));
		}

		return panel;
	}

	
	// *************** add button *************
	
	protected String addButtonKey() {
		return "ADD_BUTTON";
	}

	protected JButton buildAddButton() {
		JButton addButton = buildButton(addButtonKey());
		addButton.addActionListener(buildAddItemActionHandler());
		return addButton;
	}
	
	private ActionListener buildAddItemActionHandler() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddRemovePanel.this.addNewItem();
			}
		};
	}

	protected void addNewItem() {
		getAdapter().addNewItem(getSelectionModel());
	}
	
	// *************** remove button *************

	protected String removeButtonKey() {
		return "REMOVE_BUTTON";
	}

	protected JButton buildRemoveButton() {
		JButton removeButton = buildButton(removeButtonKey());
		removeButton.addActionListener(buildRemoveItemsActionHandler());
		removeButton.setEnabled(false);
		return removeButton;
	}
	
	private ActionListener buildRemoveItemsActionHandler() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddRemovePanel.this.removeSelectedItems();
			}
		};
	}

	protected void removeSelectedItems() {
		getAdapter().removeSelectedItems(getSelectionModel());				
	}
	
	// *************** optional button *************

	protected JButton buildOptionalButton(OptionAdapter adapter) {
		JButton optionalButton = null;
		if (adapter != null) {
			optionalButton = buildButton(adapter.optionalButtonKey());
			optionalButton.addActionListener(buildOptionalActionHandler());
			optionalButton.setEnabled(false);
		}
		else {
			optionalButton = new JButton();
			optionalButton.setVisible(false);
		}
		return optionalButton;
	}

	private ActionListener buildOptionalActionHandler() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((OptionAdapter) getAdapter()).optionOnSelection(getSelectionModel());
			}
		};
	}

	
	// *************** up button *************

	protected String upButtonKey() {
		return "UP_BUTTON_TEXT";
	}

	protected JButton buildUpButton(UpDownAdapter adapter) {
		JButton upButton = null;
		if (adapter != null) {
		    upButton = buildButton(upButtonKey());
		    upButton.addActionListener(buildUpActionHandler());
		    upButton.setEnabled(false);
		}
		else {
		    upButton = new JButton();
		    upButton.setVisible(false);
		}
		return upButton;
	}

	private ActionListener buildUpActionHandler() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddRemovePanel.this.moveItemsUp();
			}
		};
	}

	protected void moveItemsUp() {
		Object[] selectedValues = getSelectionModel().getSelectedValues();
		((UpDownAdapter) getAdapter()).moveItemsUp(selectedValues);
		getSelectionModel().setSelectedValues(selectedValues);
	}

	// *************** down button *************

	protected String downButtonKey() {
		return "DOWN_BUTTON_TEXT";
	}

	protected JButton buildDownButton(UpDownAdapter adapter) {
		JButton upButton = null;
		if (adapter != null) {
		    upButton = buildButton(downButtonKey());
		    upButton.addActionListener(buildDownActionHandler());
		    upButton.setEnabled(false);
		}
		else {
		    upButton = new JButton();
		    upButton.setVisible(false);
		}
		return upButton;
	}

	private ActionListener buildDownActionHandler() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddRemovePanel.this.moveItemsDown();
			}
		};
	}

	protected void moveItemsDown() {
		Object[] selectedValues = getSelectionModel().getSelectedValues();
		((UpDownAdapter) getAdapter()).moveItemsDown(selectedValues);
		getSelectionModel().setSelectedValues(selectedValues);
	}
	
	public ObjectListSelectionModel getSelectionModel() {
		return this.rowSelectionModel;
	}
	
	public PropertyValueModel getSelectedItemHolder() {
		return this.selectedItemHolder;
	}
	
	protected Adapter getAdapter() {
		return this.adapter;
	}
	
	protected JButton getAddButton() {
		return this.addButton;
	}
	
	protected JButton getRemoveButton() {
		return this.removeButton;
	}
	
	protected JButton getOptionalButton() {
		return this.optionalButton;
	}
	
	protected int getButtonOrientation() {
		return this.buttonOrientation;
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getComponent().setEnabled(enabled);
		this.addButton.setEnabled(enabled);

		int selectedValuesSize = getSelectionModel().getSelectedValuesSize();

		this.removeButton.setEnabled(enabled && (selectedValuesSize > 0));
		this.optionalButton.setEnabled(enabled && (selectedValuesSize == 1));
	}

	public abstract JComponent getComponent();
	
	public void setOptionalButtonEnabled(boolean enabled) {
		this.optionalButton.setEnabled(enabled);
	}	
	
	public void setRemoveButtonEnabled(boolean enabled) {
		this.removeButton.setEnabled(enabled);
	}

	public void setAddButtonEnabled(boolean enabled) {
		this.addButton.setEnabled(enabled);
	}

	public void setSelectedValue(Object anObject, boolean shouldScroll) {
		getSelectionModel().setSelectedValue(anObject);
	}
	
	public interface Adapter {

		/**
		 * Invoked when the user selects the Add button
		 */
		void addNewItem(ObjectListSelectionModel listSelectionModel);
		
		/**
		 * Invoked when the user selects the Remove button
		 */
		void removeSelectedItems(ObjectListSelectionModel listSelectionModel);
	}
	
	public interface OptionAdapter extends Adapter {
	
		/**
		 * Resource string key for the optional button
		 */
		String optionalButtonKey();

		/**
		 * Invoked when the user selects the optional button
		 */
		void optionOnSelection(ObjectListSelectionModel listSelectionModel);
		
		/**
		 * Invoked when selection changes.
		 * Implementation dictates whether button should be enabled.
		 */
		boolean enableOptionOnSelectionChange(ObjectListSelectionModel listSelectionModel);
	}
	
	public interface UpDownAdapter extends Adapter {
	    
	    void moveItemsUp(Object[] items);
	    
	    void moveItemsDown(Object[] items);
	}
	
	public interface UpDownOptionAdapter extends OptionAdapter, UpDownAdapter {
	    //just a convenience for implementing both optionAdapter and UpDownAdapter
	}
}
