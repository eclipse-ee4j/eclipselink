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
package org.eclipse.persistence.tools.workbench.uitools.chooser;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;

import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.Combo;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.uitools.swing.NonCachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;


/**
 * This component provides a way to handle selecting an item from a
 * list that may grow too large to be handled conveniently by a combo-box. 
 * If the list's size is less than the designated "long" list size, 
 * the choice list will be displayed in a normal combo-box popup; 
 * otherwise, a dialog will be used to prompt the user to choose a selection.
 * 
 * To change the browse mechanism, subclasses may 
 * 	- override the method #buildBrowser()
 *  - override the method #browse(), in which case the method 
 * 		#buildBrowser() may be ignored.
 */
public class ListChooser 
	extends Combo
{
	
	/** the size of a "long" list - anything smaller is a "short" list */
	int longListSize = DEFAULT_LONG_LIST_SIZE;
	
	/** the default size of a "long" list, which is 20 (to match JOptionPane's behavior) */
	public static final int DEFAULT_LONG_LIST_SIZE = 20;
	
	/** property change associated with long list size */
	public static final String LONG_LIST_SIZE_PROPERTY = "longListSize";
	
    static JLabel prototypeLabel = new JLabel("Prototype", new EmptyIcon(17), SwingConstants.LEADING);

    /** 
	 * whether the chooser is choosable.  if a chooser is not choosable,
	 * it only serves as a display widget.  a user may not change its 
	 * selected value.
	 */
	boolean choosable = true;
	
	/** property change associated with choosable */
	public static final String CHOOSABLE_PROPERTY = "choosable";
	
	/** the browser used to make a selection from the long list - typically via a dialog */
	private ListBrowser browser;
	
    private NodeSelector nodeSelector;
    
	/** INTERNAL - The popup is being shown.  Used to prevent infinite loop. */
	boolean popupAlreadyInProgress;
	
	
	// **************** Constructors ******************************************
	
	/**
	 * Construct a list chooser for the specified model.
	 */
	public ListChooser(ComboBoxModel model) {
		this(model, new NodeSelector.DefaultNodeSelector());
	}
	
    public ListChooser(CachingComboBoxModel model) {
        this(model, new NodeSelector.DefaultNodeSelector());
    }
    
	public ListChooser(ComboBoxModel model, NodeSelector nodeSelector) {
        this(new NonCachingComboBoxModel(model), nodeSelector);
    }
    
    public ListChooser(CachingComboBoxModel model, NodeSelector nodeSelector) {
        super(model);
        this.initialize();
        this.nodeSelector = nodeSelector;
    }
	// **************** Initialization ****************************************
	
	protected void initialize() {
		this.addPopupMenuListener(this.buildPopupMenuListener());
		this.setRenderer(new DefaultListCellRenderer());
        this.addKeyListener(buildF3KeyListener());
        
        //These are used to workaround problems with Swing trying to 
        //determine the size of a comboBox with a large model
        setPrototypeDisplayValue(prototypeLabel);
        getListBox().setPrototypeCellValue(prototypeLabel);
	}
	
    
    private JList getListBox() {
        return (JList) ClassTools.getFieldValue(this.ui, "listBox");
    }
    
	/** 
	 * When the popup is about to be shown, the event is consumed, and 
	 * PopupHandler determines whether to reshow the popup or to show
	 * the long list browser.
	 */
	private PopupMenuListener buildPopupMenuListener() {
		return new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				ListChooser.this.aboutToShowPopup();
			}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// do nothing
			}
			public void popupMenuCanceled(PopupMenuEvent e) {
				// do nothing
			}
			public String toString() {
				return "pop-up menu listener";
			}
		};
	}
	
	/**
	 * If this code is being reached due to the PopupHandler already being in progress,
	 * then do nothing.  Otherwise, set the flag to true and launch the PopupHandler.
	 */
	void aboutToShowPopup() {
		if (this.popupAlreadyInProgress) {
			return;
		}
		
		this.popupAlreadyInProgress = true;
		EventQueue.invokeLater(new PopupHandler());
	}
 
    
	private KeyListener buildF3KeyListener() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F3) {
                    goToSelectedItem();
                }                
            }
			public String toString() {
				return "F3 key listener";
			}
        };
    }
    
    public void goToSelectedItem() {
        if (getSelectedItem() != null) {
            ListChooser.this.nodeSelector.selectNodeFor(getSelectedItem());
        }
    }
    
	// **************** Browsing **********************************************
	
	/** 
	 * Lazily initialize because subclasses may have further initialization to do
	 * before browser can be built.
	 */
	protected void browse() {
		if (this.browser == null) {
			this.browser = this.buildBrowser();
		}
		
		this.browser.browse(this);
	}
	
	/**
	 * Return the "browser" used to make a selection from the long list,
	 * typically via a dialog.
	 */
	protected ListChooser.ListBrowser buildBrowser() {
		return new SimpleListBrowser();
	}
	
	
	// **************** Choosable functionality *******************************
	
	/** override behavior - consume selection if chooser is not choosable */
	public void setSelectedIndex(int anIndex) {
		if (this.choosable) {
			super.setSelectedIndex(anIndex);
		}
	}
	
	private void updateArrowButton() {
		try {
			BasicComboBoxUI comboBoxUi = (BasicComboBoxUI) ListChooser.this.getUI();
			JButton arrowButton = (JButton) ClassTools.getFieldValue(comboBoxUi, "arrowButton");
			arrowButton.setEnabled(this.isEnabled() && this.choosable);
		}
		catch (Exception e) {
			// this is a huge hack to try and make the combo box look right,
			// so if it doesn't work, just swallow the exception
		}
	}
	
	
    // **************** List Caching *******************************

    void cacheList() {
        ((CachingComboBoxModel) getModel()).cacheList();
    }
    
    void uncacheList() {
        ((CachingComboBoxModel) getModel()).uncacheList();
    }

    boolean listIsCached() {
        return ((CachingComboBoxModel) getModel()).isCached();
    }
    
	// **************** Public ************************************************
	
	public int getLongListSize() {
		return this.longListSize;
	}
	
	public void setLongListSize(int newLongListSize) {
		int oldLongListSize = this.longListSize;
		this.longListSize = newLongListSize;
		this.firePropertyChange(LONG_LIST_SIZE_PROPERTY, oldLongListSize, newLongListSize);
	}
	
	public boolean isChoosable() {
		return this.choosable;
	}
	
	public void setChoosable(boolean newValue) {
		boolean oldValue = this.choosable;
		this.choosable = newValue;
		this.firePropertyChange(CHOOSABLE_PROPERTY, oldValue, newValue);
		this.updateArrowButton();
	}
	
	// **************** Handle selecting null as a value **********************

	private boolean selectedIndexIsNoneSelectedItem(int index) {
		return index == -1 &&
				 getModel().getSize() > 0 &&
				 getModel().getElementAt(0) == null;
	}

	public int getSelectedIndex() {
        boolean listNotCached = !listIsCached();
        if (listNotCached) {
            cacheList();
        }
        
		int index = super.getSelectedIndex();

		// Use index 0 to show the <none selected> item since the actual value is
		// null and JComboBox does not handle null values
		if (selectedIndexIsNoneSelectedItem(index)) {
			index = 0;
        }

        if (listNotCached) {
            uncacheList();
        }
		return index;
   }
	
	//wrap the renderer to deal with the prototypeDisplayValue
    public void setRenderer(final ListCellRenderer aRenderer) {
        super.setRenderer(new ListCellRenderer(){
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == prototypeLabel) {
                    return prototypeLabel;
                }
                return aRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
    }
    
    
	// **************** Member classes ****************************************
	
	/**
	 * Define the API required by this ListChooser when it must
	 * prompt the user to select an item from the "long" list.
	 */
	public interface ListBrowser 
	{
		/**
		 * Prompt the user to make a selection from the specified
		 * combo-box's model.
		 */
		void browse(ListChooser parentChooser);
	}
	
	
	/**
	 * Runnable class that consumes popup window and determines whether
	 * to reshow popup or to launch browser, based on the size of the list.
	 */
	private class PopupHandler
		implements Runnable
	{
		/** The mouse event */
		private MouseEvent lastMouseEvent;
		
		/** The component from which the last mouse event was thrown */
		private JComponent eventComponent;
		
		/** The location of the component at the time the last mouse event was thrown */
		private Point componentLocation;
		
		/** The location of the mouse at the time the last mouse event was thrown */
		private Point mouseLocation;
		
		
		private PopupHandler() {
			this.initialize();
		}
		
		private void initialize() {
			AWTEvent event = EventQueue.getCurrentEvent();
			
			if (event instanceof MouseEvent) {
				this.lastMouseEvent = (MouseEvent) event;
				this.eventComponent = (JComponent) this.lastMouseEvent.getSource();
				this.componentLocation = this.eventComponent.getLocationOnScreen();
				this.mouseLocation = this.lastMouseEvent.getPoint();
			}
			else {
				this.eventComponent = null;
				this.componentLocation = null;
				this.mouseLocation = null;
			}
		}
		
		public void run() {
			ListChooser.this.hidePopup();
			
            cacheList();
			if (ListChooser.this.choosable == true) {
				// If the combo box model is of sufficient length, the browser will be shown.
				// Asking the combo box model for its size should be enough to ensure that 
				//  its size is recalculated.
				if (ListChooser.this.getModel().getSize() > ListChooser.this.longListSize) {
					this.checkComboBoxButton();
					ListChooser.this.browse();
				}
				else {
					ListChooser.this.showPopup();
					this.checkMousePosition();
				}
			}
            if (listIsCached()) {
                uncacheList();
            }
			
			ListChooser.this.popupAlreadyInProgress = false;
		}
		
		/** If this is not done, the button never becomes un-pressed */
		private void checkComboBoxButton() {
			try {
				BasicComboBoxUI comboBoxUi = (BasicComboBoxUI) ListChooser.this.getUI();
				JButton arrowButton = (JButton) ClassTools.getFieldValue(comboBoxUi, "arrowButton");
				arrowButton.getModel().setPressed(false);
			}
			catch (Exception e) {
				// this is a huge hack to try and make the combo box look right,
				// so if it doesn't work, just swallow the exception
			}
		}
		
		/**
		 * Moves the mouse back to its original position before any jiggery pokery that we've done.
		 */
		private void checkMousePosition() {
			if (this.eventComponent == null) {
				return;
			}
			
			final Point newComponentLocation = this.eventComponent.getLocationOnScreen();
			boolean componentMoved = 
				newComponentLocation.x - this.componentLocation.x != 0
				|| newComponentLocation.y - this.componentLocation.y != 0;
			
			if (componentMoved) {
				try {
					new Robot().mouseMove(
						newComponentLocation.x + this.mouseLocation.x,
						newComponentLocation.y + this.mouseLocation.y
					);
				}
				catch (AWTException ex) {
					// move failed - do nothing
				}
			}
		}
	}
}
