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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.xml;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.AddRemoveListPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWXmlPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml.XpathChooserDialog;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;


final class EisPrimaryKeysPanel 
	extends AbstractSubjectPanel 
{
	// **************** Constructors ******************************************
	
	EisPrimaryKeysPanel(ValueModel eisDescriptorHolder, WorkbenchContextHolder ctxHolder) {
		super(new GridBagLayout(), eisDescriptorHolder, ctxHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initializeLayout() {
		this.setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("PRIMARY_KEYS_PANEL.TITLE"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// primary keys - add/remove list panel
		AddRemoveListPanel addRemoveListPanel = this.buildPrimaryKeysList();

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		this.add(addRemoveListPanel, constraints);
		this.addPaneForAlignment(addRemoveListPanel);
		this.addHelpTopicId(this, this.helpTopicId());
	}
	
	private AddRemoveListPanel buildPrimaryKeysList() {
		AddRemoveListPanel listPanel = new AddRemoveListPanel(this.getApplicationContext(), this.buildPrimaryKeysAddRemoveAdapter(), 
									 						  this.buildPrimaryKeysHolder(), AddRemoveListPanel.RIGHT);
		listPanel.setBorder(buildStandardEmptyBorder());
		listPanel.setCellRenderer(this.buildPrimaryKeysListCellRenderer());
		return listPanel;
	}
	
	private AddRemoveListPanel.Adapter buildPrimaryKeysAddRemoveAdapter() {
		return new AddRemoveListPanel.Adapter() {	
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				listSelectionModel.setSelectedValues(EisPrimaryKeysPanel.this.addPrimaryKeys());
			}
			
			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				EisPrimaryKeysPanel.this.removePrimaryKeys(listSelectionModel.getSelectedValues());
			}
		};
	}
	
	private Object[] addPrimaryKeys() {
		MWXmlPrimaryKeyPolicy primaryKeyPolicy = this.xmlDescriptor().primaryKeyPolicy();
		MWXmlField primaryKey = primaryKeyPolicy.buildEmptyPrimaryKey();
		
		XpathChooserDialog.promptToSelectXpath(primaryKey, this.getWorkbenchContext());
		
		if (primaryKey.isSpecified()) {
			primaryKeyPolicy.addPrimaryKey(primaryKey);
			return new Object[] {primaryKey};
		}
		else {
			return new Object[0];
		}
	}
	
	private void removePrimaryKeys(Object[] primaryKeys) {
		for (int i = 0; i < primaryKeys.length; i ++) {
			this.xmlDescriptor().primaryKeyPolicy().removePrimaryKey((MWXmlField) primaryKeys[i]);
		}
	}
	
	private ListValueModel buildPrimaryKeysHolder() {
		return new CollectionListValueModelAdapter(
			new CollectionAspectAdapter(this.buildPrimaryKeyPolicyHolder(), MWXmlPrimaryKeyPolicy.PRIMARY_KEYS_COLLECTION) {
				protected Iterator getValueFromSubject() {
					return ((MWXmlPrimaryKeyPolicy) this.subject).primaryKeys();
				}
				
				protected int sizeFromSubject() {
					return ((MWXmlPrimaryKeyPolicy) this.subject).primaryKeysSize();
				}
			}
		);
	}
	
	private ValueModel buildPrimaryKeyPolicyHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder()) {
			protected Object getValueFromSubject() {
				return ((MWXmlDescriptor) this.subject).primaryKeyPolicy();
			}
		};
	}
	
	private ListCellRenderer buildPrimaryKeysListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((MWXmlField) value).getXpath();
			}
		};
	}
	
	
	// **************** Internal **********************************************
	
	private MWXmlDescriptor xmlDescriptor() {
		return (MWXmlDescriptor) this.getSubjectHolder().getValue();
	}
	
	
	// **************** Public ************************************************
	
	public String helpTopicId() {
		return "descriptor.eis.primaryKeys";
	}
}
