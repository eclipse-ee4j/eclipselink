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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


final class ClassAttributeAccessorsPanel 
	extends AbstractPanel 
{
	private ValueModel attributeHolder;
	private PropertyValueModel attributeTypeHolder;
	private PropertyValueModel attributeValueTypeHolder;
	private PropertyValueModel attributeTypeDimensionalityHolder;		
	
	private JLabel getMethodLabel;		
	private JLabel setMethodLabel;		
	private JButton generateGetSetMethodsButton;
	
	private JPanel valueGetSetMethodsPanel;	
		private JLabel valueGetMethodLabel;		
		private JLabel valueSetMethodLabel;		
		private JButton generateValueGetSetMethodsButton;
		
	private JPanel addRemoveMethodsPanel;	
		private JLabel addMethodLabel;		
		private JLabel removeMethodLabel;		
		private JButton generateAddRemoveMethodsButton;
	
	private ListCellRenderer methodListCellRenderer;	
	
	
	// **************** Constructors ******************************************
	protected ClassAttributeAccessorsPanel(ValueModel attributeHolder, WorkbenchContextHolder contextHolder) {
		super(contextHolder);
		this.attributeHolder = attributeHolder;

		PropertyChangeListener attributeTypeDeclarationListener = this.buildAttributeTypeDeclarationListener();

		this.attributeTypeHolder = this.buildAttributeTypeAdapter();
		this.attributeTypeHolder.addPropertyChangeListener(ValueModel.VALUE, attributeTypeDeclarationListener);

		this.attributeTypeDimensionalityHolder = this.buildAttributeDimensionalityAdapter();
		this.attributeTypeDimensionalityHolder.addPropertyChangeListener(ValueModel.VALUE, attributeTypeDeclarationListener);

		this.attributeValueTypeHolder = this.buildAttributeValueTypeAdapter();
		this.attributeValueTypeHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildAttributeValueTypeListener());

		this.initializeLayout();
	}
	
	
	// **************** Initialization ****************************************
	
	private PropertyChangeListener buildAttributeTypeDeclarationListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ClassAttributeAccessorsPanel.this.updateGetAndSetMethodLabels();
				ClassAttributeAccessorsPanel.this.updateAccessorPanelVisibility();
			}
		};
	}
	
	private PropertyValueModel buildAttributeTypeAdapter() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getType();
			}
		};	
	}
	
	private PropertyValueModel buildAttributeDimensionalityAdapter() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.DIMENSIONALITY_PROPERTY) {
			protected Object getValueFromSubject() {
				return new Integer(((MWClassAttribute) this.subject).getDimensionality());
			}
		};
	}
	
	private PropertyValueModel buildAttributeValueTypeAdapter() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.VALUE_TYPE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getValueType();
			}
		};	
	}
	
	private PropertyChangeListener buildAttributeValueTypeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				ClassAttributeAccessorsPanel.this.updateAccessorPanelVisibility();
			}
		};
	}
	
	private void initializeLayout() {
		this.setBorder(BorderFactory.createEmptyBorder());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		Collection components = new ArrayList();

		//	get and set methods panel
		JPanel getSetMethodsPanel = new JPanel(new GridBagLayout());
		getSetMethodsPanel.setBorder(BorderFactory.createTitledBorder((String) null));
		
			//	get method label
			this.getMethodLabel = SwingComponentFactory.buildLabel("GET_METHOD_LABEL", this.resourceRepository());
			components.add(this.getMethodLabel);	
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);
			getSetMethodsPanel.add(this.getMethodLabel, constraints);
		
			// 	get method chooser
			ListChooser getMethodChooser = this.buildGetMethodListChooser();
			components.add(getMethodChooser);
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 2;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			getSetMethodsPanel.add(getMethodChooser, constraints);
			
			//	set method label
			this.setMethodLabel = SwingComponentFactory.buildLabel("SET_METHOD_LABEL", this.resourceRepository());
			components.add(this.setMethodLabel);	
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);
			getSetMethodsPanel.add(this.setMethodLabel, constraints);
		
			// 	set method chooser
			ListChooser setMethodChooser = this.buildSetMethodListChooser();
			components.add(setMethodChooser);
			constraints.gridx		= 1;
			constraints.gridy		= 1;
			constraints.gridwidth	= 2;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			getSetMethodsPanel.add(setMethodChooser, constraints);
			
			// 	generate the get and set methods  button
			this.generateGetSetMethodsButton = SwingComponentFactory.buildButton("GENERATE_METHODS_BUTTON_TEXT", this.resourceRepository());
			this.generateGetSetMethodsButton.addActionListener(this.buildGenerateGetSetMethodsActionListener());
			components.add(this.generateGetSetMethodsButton);	
			constraints.gridx		= 2;
			constraints.gridy		= 2;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_END;
			constraints.insets		= new Insets(10, 5, 5, 5);
			getSetMethodsPanel.add(this.generateGetSetMethodsButton, constraints);
		
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		this.add(getSetMethodsPanel, constraints);
		
		
		//	value get and set methods panel
		this.valueGetSetMethodsPanel = new JPanel(new GridBagLayout());
		this.valueGetSetMethodsPanel.setBorder(BorderFactory.createTitledBorder((String) null));
		
			//	value get method label
			this.valueGetMethodLabel = SwingComponentFactory.buildLabel("VALUE_GET_METHOD_LABEL", this.resourceRepository());
			components.add(this.valueGetMethodLabel);	
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);
			this.valueGetSetMethodsPanel.add(this.valueGetMethodLabel, constraints);
		
			// 	value get method chooser
			ListChooser valueGetMethodChooser = this.buildValueGetMethodListChooser();
			components.add(valueGetMethodChooser);
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 2;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			this.valueGetSetMethodsPanel.add(valueGetMethodChooser, constraints);
			
			//	value set method label
			this.valueSetMethodLabel = SwingComponentFactory.buildLabel("VALUE_SET_METHOD_LABEL", this.resourceRepository());
			components.add(this.valueSetMethodLabel);	
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 5, 5);
			this.valueGetSetMethodsPanel.add(this.valueSetMethodLabel, constraints);
		
			// 	value set method chooser
			ListChooser valueSetMethodChooser = this.buildValueSetMethodListChooser();
			components.add(valueSetMethodChooser);
			constraints.gridx		= 1;
			constraints.gridy		= 1;
			constraints.gridwidth	= 2;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			this.valueGetSetMethodsPanel.add(valueSetMethodChooser, constraints);
			
			// 	generate the value get and set methods button
			this.generateValueGetSetMethodsButton = SwingComponentFactory.buildButton("GENERATE_METHODS_BUTTON_TEXT", this.resourceRepository());
			this.generateValueGetSetMethodsButton.addActionListener(this.buildGenerateValueGetSetMethodsActionListener());
			components.add(this.generateValueGetSetMethodsButton);	
			constraints.gridx		= 2;
			constraints.gridy		= 2;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_END;
			constraints.insets		= new Insets(10, 5, 5, 5);
			this.valueGetSetMethodsPanel.add(this.generateValueGetSetMethodsButton, constraints);
		
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		this.add(this.valueGetSetMethodsPanel, constraints);
		this.setPanelVisible(this.valueGetSetMethodsPanel, false);
		
		//	add and remove methods panel
		this.addRemoveMethodsPanel = new JPanel(new GridBagLayout());
		this.addRemoveMethodsPanel.setBorder(BorderFactory.createTitledBorder((String) null));
		
			//	add method label
			this.addMethodLabel = SwingComponentFactory.buildLabel("ADD_METHOD_LABEL", this.resourceRepository());
			components.add(this.addMethodLabel);	
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 0, 0);
			this.addRemoveMethodsPanel.add(this.addMethodLabel, constraints);
		
			// 	add method chooser
			ListChooser addMethodChooser =  this.buildAddMethodListChooser();
			components.add(addMethodChooser);
			constraints.gridx		= 1;
			constraints.gridy		= 0;
			constraints.gridwidth	= 2;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			this.addRemoveMethodsPanel.add(addMethodChooser, constraints);
			
			//	remove method label
			this.removeMethodLabel = SwingComponentFactory.buildLabel("REMOVE_METHOD_LABEL", this.resourceRepository());
			components.add(this.removeMethodLabel);	
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_START;
			constraints.insets		= new Insets(5, 5, 5, 0);
			this.addRemoveMethodsPanel.add(this.removeMethodLabel, constraints);
		
			// 	remove method chooser
			ListChooser removeMethodChooser = this.buildRemoveMethodListChooser();
			components.add(removeMethodChooser);
			constraints.gridx		= 1;
			constraints.gridy		= 1;
			constraints.gridwidth	= 2;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.CENTER;
			constraints.insets		= new Insets(5, 5, 0, 5);
			this.addRemoveMethodsPanel.add(removeMethodChooser, constraints);
			
			// 	generate the add and remove methods button
			this.generateAddRemoveMethodsButton = SwingComponentFactory.buildButton("GENERATE_METHODS_BUTTON_TEXT", this.resourceRepository());
			this.generateAddRemoveMethodsButton.addActionListener(this.buildGenerateAddRemoveMethodsActionListener());
			components.add(this.generateAddRemoveMethodsButton);	
			constraints.gridx		= 2;
			constraints.gridy		= 2;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.NONE;
			constraints.anchor		= GridBagConstraints.LINE_END;
			constraints.insets		= new Insets(10, 5, 5, 5);
			this.addRemoveMethodsPanel.add(this.generateAddRemoveMethodsButton, constraints);
		
		constraints.gridx		= 0;
		constraints.gridy		= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 5, 5);
		this.add(this.addRemoveMethodsPanel, constraints);
		this.setPanelVisible(this.addRemoveMethodsPanel, false);

		new ComponentEnabler(this.buildAttributeTypeBooleanHolder(), components);

		this.addHelpTopicId(this, this.helpTopicId());
	}
	
	private ValueModel buildAttributeTypeBooleanHolder() {
		return new TransformationPropertyValueModel(this.attributeTypeHolder) {
			protected Object transform(Object value) {
				return value == null ? Boolean.FALSE : Boolean.TRUE;
			}
		};
	}

	private ActionListener buildGenerateGetSetMethodsActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClassAttributeAccessorsPanel.this.getAttribute().generateGetAndSetMethods();
			}
		};
	}
	
	private ActionListener buildGenerateValueGetSetMethodsActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClassAttributeAccessorsPanel.this.getAttribute().generateValueGetAndSetMethods();
			}
		};
	}

	private ActionListener buildGenerateAddRemoveMethodsActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClassAttributeAccessorsPanel.this.getAttribute().generateAddAndRemoveMethods();
			}
		};
	}
	
	private String helpTopicId() {
		return "descriptor.classInfo.attributes.accessors";	
	}
	
	
	// **************** Get method ********************************************
		
	private ListChooser buildGetMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildGetMethodComboBoxModel(),
				this.getWorkbenchContextHolder(),
				this.buildGetMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(this.getMethodListCellRenderer());	
		return listChooser;
	}
	
	private CachingComboBoxModel buildGetMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildGetMethodHolder(), this.attributeHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return ClassAttributeAccessorsPanel.this.orderedGetMethodChoices((MWClassAttribute) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildGetMethodHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.GET_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getGetMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setGetMethod((MWMethod) value);
			}
		};
	}
	
	ListIterator orderedGetMethodChoices(MWClassAttribute attribute) {
		return CollectionTools.sort(attribute.candidateGetMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildGetMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("GET_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("GET_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildMethodStringConverter());
		return builder;
	}
	
	
	// **************** Set method ********************************************
	
	private ListChooser buildSetMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildSetMethodComboBoxModel(), 
				this.getWorkbenchContextHolder(),
				this.buildSetMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(this.getMethodListCellRenderer());	
		return listChooser;
	}
	
	private CachingComboBoxModel buildSetMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildSetMethodHolder(), this.attributeHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return ClassAttributeAccessorsPanel.this.orderedSetMethodChoices((MWClassAttribute) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildSetMethodHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.SET_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getSetMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setSetMethod((MWMethod) value);
			}
		};
	}
	
	ListIterator orderedSetMethodChoices(MWClassAttribute attribute) {
		return CollectionTools.sort(attribute.candidateSetMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildSetMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("SET_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("SET_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildMethodStringConverter());
		return builder;
	}
	
	
	// **************** Value get method **************************************
	
	private ListChooser buildValueGetMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildValueGetMethodComboBoxModel(),
				this.getWorkbenchContextHolder(),
				this.buildValueGetMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(this.getMethodListCellRenderer());	
		return listChooser;
	}
	
	private CachingComboBoxModel buildValueGetMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildValueGetMethodHolder(), this.attributeHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return ClassAttributeAccessorsPanel.this.orderedValueGetMethodChoices((MWClassAttribute) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildValueGetMethodHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.VALUE_GET_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getValueGetMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setValueGetMethod((MWMethod) value);
			}
		};
	}
	
	ListIterator orderedValueGetMethodChoices(MWClassAttribute attribute) {
		return CollectionTools.sort(attribute.candidateValueGetMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildValueGetMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("VALUE_GET_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("VALUE_GET_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildMethodStringConverter());
		return builder;
	}
	
	
	// ************* value set method ************
	
	private ListChooser buildValueSetMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildValueSetMethodComboBoxModel(), 
				this.getWorkbenchContextHolder(),
				this.buildValueSetMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(this.getMethodListCellRenderer());
		return listChooser;
	}
	
	private CachingComboBoxModel buildValueSetMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildValueSetMethodHolder(), this.attributeHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return ClassAttributeAccessorsPanel.this.orderedValueSetMethodChoices((MWClassAttribute) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildValueSetMethodHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.VALUE_SET_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getValueSetMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setValueSetMethod((MWMethod) value);
			}
		};
	}
	
	ListIterator orderedValueSetMethodChoices(MWClassAttribute attribute) {
		return CollectionTools.sort(attribute.candidateValueSetMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildValueSetMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("VALUE_SET_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("VALUE_SET_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildMethodStringConverter());
		return builder;
	}
	
	
	// **************** Add method ********************************************
	
	private ListChooser buildAddMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildAddMethodComboBoxModel(),
				this.getWorkbenchContextHolder(),
				this.buildAddMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(this.getMethodListCellRenderer());	
		return listChooser;
	}
	
	private CachingComboBoxModel buildAddMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildAddMethodHolder(), this.attributeHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return ClassAttributeAccessorsPanel.this.orderedAddMethodChoices((MWClassAttribute) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildAddMethodHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.ADD_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getAddMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setAddMethod((MWMethod) value);
			}
		};
	}
	
	ListIterator orderedAddMethodChoices(MWClassAttribute attribute) {
		return CollectionTools.sort(attribute.candidateAddMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildAddMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("ADD_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("ADD_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildMethodStringConverter());
		return builder;
	}
	
	
	// **************** Remove method *****************************************
	
	private ListChooser buildRemoveMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildRemoveMethodComboBoxModel(),
				getWorkbenchContextHolder(),
				buildRemoveMethodChooserDialogBuilder()
			);
		listChooser.setRenderer(this.getMethodListCellRenderer());
		return listChooser;
	}
	
	private CachingComboBoxModel buildRemoveMethodComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildRemoveMethodHolder(), this.attributeHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return ClassAttributeAccessorsPanel.this.orderedRemoveMethodChoices((MWClassAttribute) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildRemoveMethodHolder() {
		return new PropertyAspectAdapter(this.attributeHolder, MWClassAttribute.REMOVE_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWClassAttribute) this.subject).getRemoveMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWClassAttribute) this.subject).setRemoveMethod((MWMethod) value);
			}
		};
	}
	
	ListIterator orderedRemoveMethodChoices(MWClassAttribute attribute) {
		return CollectionTools.sort(attribute.candidateRemoveMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildRemoveMethodChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("REMOVE_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("REMOVE_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(this.buildMethodStringConverter());
		return builder;
	}
	
	
	// **************** Common to all accessors *******************************
	
	/** We want to display the signature, but we want filtering based only on the method name */
	private StringConverter buildMethodStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return (o == null) ? "" : ((MWMethod) o).getName();
			}
		};
	}
	
	private ListCellRenderer getMethodListCellRenderer() {
		if (this.methodListCellRenderer == null) {
			this.methodListCellRenderer = new AdaptableListCellRenderer(new MethodCellRendererAdapter(this.resourceRepository()));
		}
		return this.methodListCellRenderer;
	}
	
	
	// **************** Behavior **********************************************
	
	void updateGetAndSetMethodLabels() {
		if (getAttribute() != null && getAttribute().isValueHolder()) {
			this.getMethodLabel.setText(this.resourceRepository().getString("VALUE_HOLDER_GET_METHOD_LABEL"));
			this.setMethodLabel.setText(this.resourceRepository().getString("VALUE_HOLDER_SET_METHOD_LABEL"));
		}
		else {
			this.getMethodLabel.setText(this.resourceRepository().getString("GET_METHOD_LABEL"));
			this.setMethodLabel.setText(this.resourceRepository().getString("SET_METHOD_LABEL"));
		}
	}
	
	void updateAccessorPanelVisibility() {
		this.setPanelVisible(this.valueGetSetMethodsPanel, getAttribute() != null && getAttribute().canHaveValueGetAndSetMethods());
		this.setPanelVisible(this.addRemoveMethodsPanel, getAttribute() != null && getAttribute().canHaveAddAndRemoveMethods());
	}
	
	private void setPanelVisible(JPanel panel, boolean visible) {
		panel.setVisible(visible);
		revalidate();
		repaint();	
	}
	
	
	// **************** Convenience *******************************************
	
	MWClassAttribute getAttribute() {
		return (MWClassAttribute) this.attributeHolder.getValue();
	}
	
}
