/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.uitools.FilteringListPanel;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


/**
 * This dialog uses the FilteringListPanel to assist the user in making
 * a selection from a long list of objects.
 */
public class DefaultListChooserDialog 
	extends AbstractDialog 
{
	// **************** Variables *********************************************
	
	/** The panel displaying the list and a filter field */
	private FilteringListPanel filteringListPanel;

	/** Holds all the settings used by the dialog when editing the name. */
	Builder builder;


	// ********** constructors **********

	/**
	 * Do not use this constructor if the owner is a Dialog.
	 * @see #createDialog()
	 */
	public DefaultListChooserDialog(WorkbenchContext context, Builder builder) {
		super(context);
		this.builder = builder;
		initialize2();
	}

	public DefaultListChooserDialog(WorkbenchContext context, Dialog owner, Builder builder) {
		super(context, owner);
		this.builder = builder;
		initialize2();
	}

	// ********** initialization **********

	protected void initialize2() {
		this.setTitle(resourceRepository().getString(this.builder.getTitleKey()));
		this.filteringListPanel = this.buildFilteringListPanel();
	}
	
	protected FilteringListPanel buildFilteringListPanel() {
		FilteringListPanel panel =
			new FilteringListPanel(this.builder.getCompleteList(), this.builder.getInitialSelection(),  this.builder.getStringConverter()) {
				protected ListCellRenderer buildDefaultCellRenderer() {
					if (DefaultListChooserDialog.this.builder.getListCellRenderer() != null) {
						return DefaultListChooserDialog.this.builder.getListCellRenderer();
					}
					return super.buildDefaultCellRenderer();
				}
			};
		this.helpManager().addTopicID(panel.getTextField(), this.builder.getHelpTopicId());
		this.helpManager().addTopicID(panel.getListBox(), this.builder.getHelpTopicId());
		this.configureLabel(panel.getTextFieldLabel(), this.builder.getTextFieldLabelKey());
		this.configureLabel(panel.getListBoxLabel(), this.builder.getListBoxLabelKey());
		panel.getListBox().addListSelectionListener(this.buildListBoxSelectionListener());
		panel.getListBox().addMouseListener(this.buildListBoxMouseListener());
		
		return panel;
	}

	/**
	 * Configure the specified label's text and mnemonic.
	 */
	protected void configureLabel(JLabel label, String key) {
		label.setText(this.resourceRepository().getString(key));
		label.setDisplayedMnemonic(this.resourceRepository().getMnemonic(key));
	}
	
	/**
	 * Double-clicking on a selection in the list box will automatically
	 * make the selection.
	 */
	protected MouseListener buildListBoxMouseListener() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					DefaultListChooserDialog.this.clickOK();
				}
			}
		};
	}
	
	/**
	 * The OK button will update according to whether an item is selected
	 */
	protected ListSelectionListener buildListBoxSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( ! e.getValueIsAdjusting()) {
					DefaultListChooserDialog.this.updateOK();
				}
			}
		};
	}
	
	// open up access for inner class
	protected void clickOK() {
		super.clickOK();
	}
	
	// open up access for inner class
	protected void updateOK() {
		this.getOKAction().setEnabled(! this.filteringListPanel.getListBox().isSelectionEmpty());
	}


	// ********** AbstractDialog implementation **********

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#buildMainPanel()
	 */
	protected Component buildMainPanel() {
		return this.filteringListPanel;
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#getHelpTopicID()
	 */
	protected String helpTopicId() {
		return this.builder.getHelpTopicId();
	}

	/**
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#initialFocusComponent()
	 */
	protected Component initialFocusComponent() {
		return this.filteringListPanel.getTextField();
	}

	/**
	 * Override to set an explicit size instead of using #pack().
	 * @see org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog#prepareToShow()
	 */
	protected void prepareToShow() {
		this.setSize(300, 485);	// use Golden Ratio
		this.setLocationRelativeTo(this.getParent());
		this.updateOK();
	}


	// ********** public API **********

	/**
	 * Allow the filtering list panel to be tweaked by clients.
	 */
	public FilteringListPanel getFilteringListPanel() {
		return this.filteringListPanel;
	}

	/**
	 * Return the object selected by the user.
	 */
	public Object selection() {
		if ( ! this.wasConfirmed()) {
			throw new IllegalStateException();
		}
		return this.filteringListPanel.getSelection();
	}




	// ********** member classes **********
	
	/**
	 * Configure an instance of this class to build a NewNameDialog.
	 * 
	 * Subclasses should probably override #buildDialog(WorkbenchContext, Builder)
	 * to return the appropriate subclass of NewNameDialog.
	 */
	public static class Builder implements Cloneable {
		private Object[] completeList;
		private Object initialSelection;
		private StringConverter stringConverter;
		private ListCellRenderer listCellRenderer;
		private String titleKey;
		private String textFieldLabelKey;
		private String listBoxLabelKey;
		private String helpTopicId;
	
	
		static Builder DEFAULT_INSTANCE = new Builder();
		
			// ********** constructors/initialization **********
	
		public Builder() {
			super();
			this.initialize();
		}
	
		protected void initialize() {
			this.completeList = null;
			this.initialSelection = null;
			this.stringConverter = StringConverter.DEFAULT_INSTANCE;
			this.titleKey = "DEFAULT_LONG_LIST_BROWSER_DIALOG.TITLE";
			this.textFieldLabelKey = "DEFAULT_LONG_LIST_BROWSER_DIALOG.TEXT_FIELD_LABEL";
			this.listBoxLabelKey = "DEFAULT_LONG_LIST_BROWSER_DIALOG.LIST_BOX_LABEL";
			this.helpTopicId = "default";
		}
	
	
		// ********** dialog instantiation **********
	
		public DefaultListChooserDialog buildDialog(WorkbenchContext context, Object[] list,  Object initSel) {
			return this.buildDialog(context, (Builder) this.clone(), list, initSel);
		}
	
		protected Object clone() {
			Builder clone;
			try {
				clone = (Builder) super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new RuntimeException(ex);
			}
			return clone;
		}
	
		protected DefaultListChooserDialog buildDialog(WorkbenchContext context, Builder clone, Object[] list, Object initSel) {
			if (clone.getCompleteList() == null) {
				clone.setCompleteList(list);
			}
			
			if (clone.getInitialSelection() == null) {
				clone.setInitialSelection(initSel);
			}
			
			if (context.getCurrentWindow() instanceof Dialog) {
				return new DefaultListChooserDialog(context, (Dialog) context.getCurrentWindow(), clone);
			}
			
			return new DefaultListChooserDialog(context, clone);

		}
	
	
		// ********** settings **********
	
		/**
		 * The title of the dialog. The default is null.
		 */
		public void setTitleKey(String title) {
			this.titleKey = title;
		}
		public String getTitleKey() {
			return this.titleKey;
		}
	
		/**
		 * The description displayed above the text entry field.
		 * The default is null.
		 */
		public void setTextFieldLabelKey(String textFieldLabel) {
			this.textFieldLabelKey = textFieldLabel;
		}
		public String getTextFieldLabelKey() {
			return this.textFieldLabelKey;
		}
	
		/**
		 * The description displayed above the text entry field.
		 * The default is null.
		 */
		public void setListBoxLabelKey(String listBoxLabel) {
			this.listBoxLabelKey = listBoxLabel;
		}
		public String getListBoxLabelKey() {
			return this.listBoxLabelKey;
		}
				
		/**
		 * The help topic id for the dialog.
		 * The default is null
		 */
		public String getHelpTopicId() {
			return this.helpTopicId;
		}		
		public void setHelpTopicId(String helpTopidId) {
			this.helpTopicId = helpTopidId;
		}	
		
		/**
		 * 
		 */
		public Object[] getCompleteList() {
			return this.completeList;
		}		
		public void setCompleteList(Object[] completeList) {
			this.completeList = completeList;
		}
		
		/**
		 * 
		 */
		public Object getInitialSelection() {
			return this.initialSelection;
		}		
		public void setInitialSelection(Object initialSelection) {
			this.initialSelection = initialSelection;
		}
		
		/**
		 * 
		 */
		public StringConverter getStringConverter() {
			return this.stringConverter;
		}		
		public void setStringConverter(StringConverter stringConverter) {
			this.stringConverter = stringConverter;
		}
		
		public ListCellRenderer getListCellRenderer() {
			return this.listCellRenderer;
		}
		
		public void setListCellRenderer(ListCellRenderer listCellRenderer) {
			this.listCellRenderer = listCellRenderer;
		}
	
	}


}
