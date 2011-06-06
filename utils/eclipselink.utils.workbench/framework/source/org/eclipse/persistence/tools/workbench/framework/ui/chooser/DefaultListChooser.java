/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ListModel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.NonCachingComboBoxModel;


/**
 * Extend the vanilla list chooser to prompt the user with a custom dialog.
 */
public class DefaultListChooser 
	extends ListChooser 
{	
	/** The client-provided workbenchContextHolder. */
	private WorkbenchContextHolder contextHolder;
	
	/** The client-provided long list browser dialog builder. */
	private DefaultListChooserDialog.Builder builder;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor. */
	protected DefaultListChooser(ComboBoxModel model) {
		super(new NonCachingComboBoxModel(model));
	}
	
	/** Construct a chooser for the specified model, using the specified context holder. */
	public DefaultListChooser(ComboBoxModel model, WorkbenchContextHolder contextHolder) {
		this(new NonCachingComboBoxModel(model), contextHolder, DefaultListChooserDialog.Builder.DEFAULT_INSTANCE);
	}
	
	/** Construct a chooser for the specified model, using the specified context holder and browser builder. */
	public DefaultListChooser(CachingComboBoxModel model, WorkbenchContextHolder contextHolder, DefaultListChooserDialog.Builder builder) {
		this(model);
        initialize(contextHolder);
		this.builder = builder;
	}
    
    /** Construct a chooser for the specified model, using the specified context holder and browser builder. */
    public DefaultListChooser(ComboBoxModel model, WorkbenchContextHolder contextHolder, DefaultListChooserDialog.Builder builder) {
        this(new NonCachingComboBoxModel(model), contextHolder, builder);
    }
    
    /** Construct a chooser for the specified model, using the specified context holder, and node selector. */
    public DefaultListChooser(ComboBoxModel model, WorkbenchContextHolder contextHolder, NodeSelector nodeSelector) {
        this(new NonCachingComboBoxModel(model), contextHolder, nodeSelector, DefaultListChooserDialog.Builder.DEFAULT_INSTANCE);
   }   
    
    /** Construct a chooser for the specified model, using the specified context holder, browser builder and node selector. */
    public DefaultListChooser(CachingComboBoxModel model, WorkbenchContextHolder contextHolder, NodeSelector nodeSelector, DefaultListChooserDialog.Builder builder) {
        super(model, nodeSelector);
        initialize(contextHolder);
        this.builder = builder;
    }	
   
    /** Construct a chooser for the specified model, using the specified context holder, browser builder and node selector. */
    public DefaultListChooser(ComboBoxModel model, WorkbenchContextHolder contextHolder, NodeSelector nodeSelector, DefaultListChooserDialog.Builder builder) {
        this(new NonCachingComboBoxModel(model), contextHolder, nodeSelector, builder);
    }   
      
    protected void initialize(WorkbenchContextHolder ctxHolder) {
        this.contextHolder = ctxHolder;
        JMenuItem menuItem = new JMenuItem(resourceRepository().getString("SELECT_IN_NAVIGATOR_POPUP_MENU_ITEM"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        menuItem.addActionListener(this.buildGoToListener());
        
        getHelpManager().addItemsToPopupMenuForComponent(new JMenuItem[] {menuItem}, this);
    }

    
	// **************** Browsing **********************************************
	
	/** Use a custom dialog to prompt the user for a selection. */
	protected void browse() {
		DefaultListChooserDialog dialog = this.buildDialog();
		dialog.show();
		
		if (dialog.wasConfirmed()) {
			this.getModel().setSelectedItem(dialog.selection());
		}
	}
	
	/**
	 * Subclasses can extend this method to customize the dialog
	 * before returning it (e.g. you could tweak the cell renderer
	 * used to display the entries in the filtering list panel).
	 */
	protected DefaultListChooserDialog buildDialog() {
		// the dialog should have the same cell renderer as this chooser
		if (this.builder.getListCellRenderer() == null) {
			this.builder.setListCellRenderer(this.getRenderer());
		}
		
		return this.builder.buildDialog(
			this.getWorkbenchContext(),
			this.convertToArray(this.getModel()),
			this.getModel().getSelectedItem()
		);
	}
	
	/**
	 * Convert the list of objects in the specified list model
	 * into an array.
	 */
	protected Object[] convertToArray(ListModel model) {
		int size = model.getSize();
		Object[] result = new Object[size];
		for (int i = 0; i < size; i++) {
			result[i] = model.getElementAt(i);
		}
		return result;
    }
    
    private ActionListener buildGoToListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListChooser.this.goToSelectedItem();
            }
        };
    }
    
	// **************** Convenience methods ***********************************
	
    protected HelpManager getHelpManager() {
        return getApplicationContext().getHelpManager();
    }
	protected ApplicationContext getApplicationContext() {
		return this.getWorkbenchContext().getApplicationContext();
	}
	
	protected WorkbenchContext getWorkbenchContext() {
		return this.contextHolder.getWorkbenchContext();
	}
	
	protected ResourceRepository resourceRepository() {
		return this.getApplicationContext().getResourceRepository();
	}
}
