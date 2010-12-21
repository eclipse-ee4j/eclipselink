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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.GroupBox;
import org.eclipse.persistence.tools.workbench.framework.uitools.Pane;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.DescriptorComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public final class MethodAccessingPanel extends AbstractSubjectPanel {

	private ListCellRenderer methodListCellRenderer;	
	
	private PropertyValueModel methodAccessingHolder;

	public MethodAccessingPanel(PropertyValueModel mappingHolder, WorkbenchContextHolder contextHolder) {
		super(mappingHolder, contextHolder);
	}
	protected void initialize(ValueModel subjectHolder) {
	    super.initialize(subjectHolder);
        this.methodAccessingHolder = buildMethodAccessingHolder();
	}

	protected void initializeLayout() {
		
		GridBagConstraints constraints = new GridBagConstraints();
	
        Collection components = new ArrayList();
        
		// Add method accessing
		JCheckBox methodAccessingCheckBox = buildMethodAccessingCheckBox();
		Pane methodAccessingPanel = new Pane(new GridBagLayout());

		GroupBox groupBox = new GroupBox(methodAccessingCheckBox, methodAccessingPanel);

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);

		add(groupBox, constraints);

			// Get method widgets
			JComponent getMethodWidgets = buildLabeledComponent("GET_METHOD_LABEL", buildGetMethodChooser());
            components.add(getMethodWidgets);
			constraints.gridx			= 0;
			constraints.gridy			= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(0, 0, 0, 0);
	
			methodAccessingPanel.add(getMethodWidgets, constraints);

			// Set method widgets
			JComponent setMethodWidgets = buildLabeledComponent("SET_METHOD_LABEL", buildSetMethodChooser());
            components.add(setMethodWidgets);

			constraints.gridx			= 0;
			constraints.gridy			= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill			= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 0, 0, 0);
	
			methodAccessingPanel.add(setMethodWidgets, constraints);
	
		addHelpTopicId(this, "mapping.methodAccessing");
        
        new ComponentEnabler(this.methodAccessingHolder, components);
	}
	
	
	// ************* use method accessing ************
	
	private JCheckBox buildMethodAccessingCheckBox() {
		JCheckBox checkBox = buildCheckBox("USE_METHOD_ACCESSING_CHECK_BOX", new CheckBoxModelAdapter(this.methodAccessingHolder));
		return checkBox;
	}
	
	private PropertyValueModel buildMethodAccessingHolder() {
		return new PropertyAspectAdapter(getSubjectHolder(), MWMapping.USES_METHOD_ACCESSING_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWMapping) this.subject).usesMethodAccessing());
			}
			protected void setValueOnSubject(Object value) {
				((MWMapping) this.subject).setUsesMethodAccessing(((Boolean) value).booleanValue());
			}
		};
	}


	// **************** Get method ********************************************

	private ListChooser buildGetMethodChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildGetMethodComboBoxModel(), 
				this.getWorkbenchContextHolder(),
                DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()),
				this.buildGetMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(getMethodListCellRenderer());
		return listChooser;
	}
	
	private CachingComboBoxModel buildGetMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildGetMethodHolder(), this.getSubjectHolder()) {
				protected ListIterator listValueFromSubject(Object subject) {
					return MethodAccessingPanel.this.orderedGetMethodChoices((MWMapping) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildGetMethodHolder() {
		PropertyValueModel propertyValueModel = 
			new PropertyAspectAdapter(getSubjectHolder(), MWMapping.GET_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWMapping) this.subject).getGetMethod();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWMapping) this.subject).setGetMethod((MWMethod) value);
			}
		};
		
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}
	
	ListIterator orderedGetMethodChoices(MWMapping mapping) {
		return CollectionTools.sort(this.getMethodChoices(mapping)).listIterator();
	}
	
	private Iterator getMethodChoices(MWMapping mapping) {
		return mapping.candidateGetMethods();
	}
	
	private DefaultListChooserDialog.Builder buildGetMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("GET_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("GET_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildMethodStringConverter());
		return builder;
	}
    
	
	// **************** Set method ********************************************
	
	private ListChooser buildSetMethodChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildSetMethodComboBoxModel(), 
				this.getWorkbenchContextHolder(),
                DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()), 
				this.buildSetMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(getMethodListCellRenderer());

		return listChooser;
	}

	private CachingComboBoxModel buildSetMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildSetMethodHolder(), this.getSubjectHolder()) {
				protected ListIterator listValueFromSubject(Object subject) {
					return MethodAccessingPanel.this.orderedSetMethodChoices((MWMapping) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildSetMethodHolder() {
		PropertyValueModel propertyValueModel = 
			new PropertyAspectAdapter(getSubjectHolder(), MWMapping.SET_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWMapping) this.subject).getSetMethod();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWMapping) this.subject).setSetMethod((MWMethod) value);
			}
		};
		
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}

	ListIterator orderedSetMethodChoices(MWMapping mapping) {
		return CollectionTools.sort(this.setMethodChoices(mapping)).listIterator();
	}
	
	private Iterator setMethodChoices(MWMapping mapping) {
		return mapping.candidateSetMethods();
	}
	
	private DefaultListChooserDialog.Builder buildSetMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SET_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SET_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildMethodStringConverter());
		return builder;
	}
	
	
	// **************** Get/Set method common *********************************
	
	/** We want to display the signature, but we want filtering based only on the method name */
	private StringConverter buildMethodStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return (o == null) ? "" : ((MWMethod) o).getName();
			}
		};
	}
	
	/** Display the signature */
	private ListCellRenderer getMethodListCellRenderer() {
		if (this.methodListCellRenderer == null) {
			this.methodListCellRenderer = new AdaptableListCellRenderer(new MethodCellRendererAdapter(resourceRepository()));
		}
		return this.methodListCellRenderer;
	}
}
