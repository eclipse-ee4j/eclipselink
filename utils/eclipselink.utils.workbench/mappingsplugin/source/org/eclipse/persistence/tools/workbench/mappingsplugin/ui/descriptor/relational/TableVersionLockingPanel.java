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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.ColumnCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.VersionLockingPanel;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.relational.RelationalMappingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class TableVersionLockingPanel extends VersionLockingPanel {

	TableVersionLockingPanel(ValueModel lockingPolicyHolder, WorkbenchContextHolder contextHolder) {
		super(lockingPolicyHolder, contextHolder);
	}
	
	protected JPanel buildVersionLockingFieldChooser()
	{
		Pane lockingPanel = new Pane(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		JLabel lockingFieldLabel = buildLabel("RELATIONAL_LOCKING_POLICY_DATABASE_FIELD");

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 5);

		lockingPanel.add(lockingFieldLabel, constraints);
		
		ListChooser lockingChooser = buildVersionLockingFieldListChooser();		
		
		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		lockingPanel.add(lockingChooser, constraints);
		
		lockingFieldLabel.setLabelFor(lockingChooser);
		
		return lockingPanel;
	}
	
	// ************* version locking field ***********
	private ListChooser buildVersionLockingFieldListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildLockingFieldBoxModel(), 
				this.getWorkbenchContextHolder(),
                RelationalMappingComponentFactory.buildColumnNodeSelector(getWorkbenchContextHolder()),
				this.buildDatabaseFieldChooserDialogBuilder()
			);
		listChooser.setRenderer(buildColumnListRenderer());
		return listChooser;
	}
	
	private ComboBoxModel buildLockingFieldBoxModel() {
		return RelationalMappingComponentFactory.buildExtendedColumnComboBoxModel(buildLockingFieldSelectionHolder(), buildDescriptorHolder());
	}
	
	private PropertyValueModel buildLockingFieldSelectionHolder() {
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(getSubjectHolder(), MWTableDescriptorLockingPolicy.VERSION_LOCKING_COLUMN_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorLockingPolicy) this.subject).getVersionLockField();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWDescriptorLockingPolicy) this.subject).setVersionLockField((MWDataField)value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWColumn.QUALIFIED_NAME_PROPERTY, MWColumn.DATABASE_TYPE_PROPERTY);
	}

	private DefaultListChooserDialog.Builder buildDatabaseFieldChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("VERSION_LOCKING_FIELD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("VERSION_LOCKING_FIELD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildColumnStringConverter());
		return builder;
	}
	
	private PropertyValueModel buildDescriptorHolder() {
		return new PropertyAspectAdapter(getSubjectHolder()) {
			protected Object getValueFromSubject() {
				return ((MWTransactionalPolicy) ((MWTableDescriptorLockingPolicy) subject).getParent()).getParent();			
			}
		};
	}
	
	private StringConverter buildColumnStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWColumn) o).qualifiedName();
			}
		};
	}
	
	private ListCellRenderer buildColumnListRenderer() {
		return new AdaptableListCellRenderer(new ColumnCellRendererAdapter(this.resourceRepository()));
	}
}
