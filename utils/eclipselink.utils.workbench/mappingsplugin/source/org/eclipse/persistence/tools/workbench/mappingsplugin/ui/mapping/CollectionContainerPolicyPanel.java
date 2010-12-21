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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.DefaultingContainerClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;


public final class CollectionContainerPolicyPanel
	extends AbstractSubjectPanel
{
	// **************** Variables *********************************************

	private PropertyValueModel containerPolicyHolder;
	private PropertyValueModel containerTypeHolder;
	private PropertyValueModel defaultingContainerClassHolder;
	private JCheckBox useSortingCheckBox;

	private final String CONTAINER_TYPE_COLLECTION = "collection";
	private final String CONTAINER_TYPE_LIST = "list";
	private final String CONTAINER_TYPE_SET = "set";

	// **************** Constructors ******************************************
	
	public CollectionContainerPolicyPanel(PropertyValueModel containerMappingHolder,
	                                      WorkbenchContextHolder contextHolder) {
		super(containerMappingHolder, contextHolder);
	}
    
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ((MWModel) CollectionContainerPolicyPanel.this.subject()).getRepository();
			}
		};
	}

	private PropertyValueModel buildCollectionContainerTypeHolder() {
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWContainerMapping) this.subject).getContainerPolicy();
			}

			protected void setValueOnSubject(Object value) {
				containerTypeHolder.setValue(CONTAINER_TYPE_COLLECTION);
			}
		};

		return new TransformationPropertyValueModel(adapter) {
			protected Object transform(Object value) {
				if (value instanceof MWCollectionContainerPolicy)
					return CONTAINER_TYPE_COLLECTION;

				return null;
			}
		};
	}
	
	private JRadioButton buildCollectionContainerTypeRadioButton() {
		return this.buildRadioButton(
			"CONTAINER_POLICY_COLLECTION_CLASS_RADIO_BUTTON",
			this.buildCollectionContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildCollectionContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.buildCollectionContainerTypeHolder(), CONTAINER_TYPE_COLLECTION);
	}
	
	private void buildContainerClassChooserComponentEnabler(JComponent containerClassWidgets) {
		new ComponentEnabler(buildOverrideDefaultClassHolder(), containerClassWidgets.getComponents());
	}

	private ItemListener buildContainerClassChooserEnabler(final ClassChooserPanel containerClassChooserPanel)
	{
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				containerClassChooserPanel.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		};
	}

	private PropertyValueModel buildContainerClassHolder() {
		return new PropertyAspectAdapter(this.defaultingContainerClassHolder, DefaultingContainerClass.CONTAINER_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((DefaultingContainerClass) this.subject).getContainerClass();
			}
			
			protected void setValueOnSubject(Object value) {
				((DefaultingContainerClass) this.subject).setContainerClass((MWClass) value);
			}
		};
	}
    
	/**
	 * comparator class chooser
	 */
	private PropertyValueModel buildComparatorClassHolder() {
		return new PropertyAspectAdapter(this.containerPolicyHolder, MWContainerPolicy.COMPARATOR_CLASS_PROPERTY, MWContainerPolicy.SORT_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWContainerPolicy) this.subject).getComparatorClass();
			}
			protected void setValueOnSubject(Object value) {
				((MWContainerPolicy) this.subject).setComparatorClass((MWClass) value);
			}
		};
	}

    private PropertyValueModel buildContainerPolicyHolder() {
        return new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
            protected Object getValueFromSubject() {
                return ((MWContainerMapping) this.subject).getContainerPolicy();
            }   
        };
    }
    
	private PropertyValueModel buildContainerTypeHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWContainerMapping) this.subject).getContainerPolicy();
			}

			protected void setValueOnSubject(Object value) {
				if (value == CONTAINER_TYPE_COLLECTION) {
					((MWContainerMapping) this.subject).setCollectionContainerPolicy();
				}
				else if (value == CONTAINER_TYPE_LIST) {
					((MWContainerMapping) this.subject).setListContainerPolicy();
				}
				else if (value == CONTAINER_TYPE_SET) {
					((MWContainerMapping) this.subject).setSetContainerPolicy();
				}
			}
		};
	}

	private PropertyValueModel buildDefaultingContainerClassHolder() {
		return new PropertyAspectAdapter(this.containerPolicyHolder) {
			protected Object getValueFromSubject() {
				return ((MWContainerPolicy) this.subject).getDefaultingContainerClass();
			}
		};
	}

	private PropertyValueModel buildListContainerTypeHolder() {
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWContainerMapping) this.subject).getContainerPolicy();
			}

			protected void setValueOnSubject(Object value) {
				containerTypeHolder.setValue(CONTAINER_TYPE_LIST);
			}
		};

		return new TransformationPropertyValueModel(adapter) {
			protected Object transform(Object value) {
				if (value instanceof MWListContainerPolicy)
					return CONTAINER_TYPE_LIST;

				return null;
			}
		};
	}

	private JRadioButton buildListContainerTypeRadioButton() {
		return this.buildRadioButton(
			"CONTAINER_POLICY_LIST_CLASS_RADIO_BUTTON",
			this.buildListContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildListContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.buildListContainerTypeHolder(), CONTAINER_TYPE_LIST);
	}
	
	private ButtonModel buildOverrideDefaultClassCheckBoxAdapter() {
		return new CheckBoxModelAdapter(buildOverrideDefaultClassHolder());
	}

	private PropertyValueModel buildOverrideDefaultClassHolder() {
		return new TransformationPropertyValueModel(this.buildUseDefaultContainerClassHolder()) {
			
			private Boolean oppositeValue(Boolean value) {
				return (value == null) ? null : Boolean.valueOf(! value.booleanValue());
			}
			
			protected Object reverseTransform(Object value) {
				return this.oppositeValue((Boolean) value);
			}
			protected Object transform(Object value) {
				return this.oppositeValue((Boolean) value);
			}
		};
	}

	private PropertyValueModel buildSetContainerTypeHolder() {
		PropertyAspectAdapter adapter = new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWContainerMapping) this.subject).getContainerPolicy();
			}

			protected void setValueOnSubject(Object value) {
				containerTypeHolder.setValue(CONTAINER_TYPE_SET);
			}
		};


		return new TransformationPropertyValueModel(adapter) {
			protected Object transform(Object value) {
				if (value instanceof MWSetContainerPolicy)
					return CONTAINER_TYPE_SET;

				return null;
			}
		};
	}
	
	/**
	 * uses sorting check box
	 */
	private JCheckBox buildUseSortingCheckBox() {
		return this.buildCheckBox(
			"CONTAINER_POLICY_USE_SORTING_CHECK_BOX",
			this.buildUseSortingCheckBoxAdapter()
		);
	}

	private ButtonModel buildUseSortingCheckBoxAdapter() {
		return new CheckBoxModelAdapter(buildUseSortingHolder());
	}

	private PropertyValueModel buildUseSortingHolder() {
		return new PropertyAspectAdapter(this.containerPolicyHolder, MWContainerPolicy.SORT_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWContainerPolicy)this.subject).usesSorting());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWContainerPolicy)this.subject).setUsesSorting(((Boolean) value).booleanValue());
			}
		};
	}

	private JRadioButton buildSetContainerTypeRadioButton() {
		return this.buildRadioButton(
			"CONTAINER_POLICY_SET_CLASS_RADIO_BUTTON",
			this.buildSetContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildSetContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.buildSetContainerTypeHolder(), CONTAINER_TYPE_SET);
	}
	
	private PropertyValueModel buildUseDefaultContainerClassHolder() {
		return new PropertyAspectAdapter(this.defaultingContainerClassHolder, DefaultingContainerClass.USES_DEFAULT_CONTAINER_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				// return the opposite of usesDefaultContainerClass
				return Boolean.valueOf(((DefaultingContainerClass) this.subject).usesDefaultContainerClass());
			}
			
			protected void setValueOnSubject(Object value) {
				// set the opposite value for useDefaultContainerClass
				((DefaultingContainerClass) this.subject).setUseDefaultContainerClass(((Boolean) value).booleanValue());
			}
		};
	}


	// **************** Initialization ****************************************
	
	protected void initialize(ValueModel subjectHolder) {
		super.initialize(subjectHolder);
		this.containerPolicyHolder = this.buildContainerPolicyHolder();
		this.defaultingContainerClassHolder = buildDefaultingContainerClassHolder();
		this.containerTypeHolder = this.buildContainerTypeHolder();
		this.containerTypeHolder.addPropertyChangeListener(PropertyValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
			}
		});

	}   

	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		ButtonGroup buttonGroup = new ButtonGroup();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;

		// Container Type group box
		JPanel containerTypeGroupBox = new JPanel(new GridBagLayout());
		containerTypeGroupBox.setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("COLLECTION_CONTAINER_POLICY_GROUP_BOX"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 2;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);
		add(containerTypeGroupBox, constraints);

		// Container Type sub-pane
		JPanel containerTypeSubPane = new JPanel(new GridLayout(1, 3, 20, 0));
		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);
		containerTypeGroupBox.add(containerTypeSubPane, constraints);

		// This pushes all the radio buttons to the left so they won't be spread out
		Spacer spacer = new Spacer();
		constraints.gridx       = 1;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);
		containerTypeGroupBox.add(spacer, constraints);

		// List container type radio button
		JRadioButton listContainerTypeRadioButton = this.buildListContainerTypeRadioButton();
		containerTypeSubPane.add(listContainerTypeRadioButton);
		buttonGroup.add(listContainerTypeRadioButton);

		// Set container type radio button
		JRadioButton setContainerTypeRadioButton = this.buildSetContainerTypeRadioButton();
		containerTypeSubPane.add(setContainerTypeRadioButton);
		buttonGroup.add(setContainerTypeRadioButton);

		// Collection container type radio button
		JRadioButton collectionContainerTypeRadioButton = this.buildCollectionContainerTypeRadioButton();
		containerTypeSubPane.add(collectionContainerTypeRadioButton);
		buttonGroup.add(collectionContainerTypeRadioButton);

		// Custom Class check box
		JCheckBox customClassCheckBox = 
			buildCheckBox("COLLECTION_CONTAINER_POLICY_OVERRIDE_DEFAULT_CLASS_CHECK_BOX", buildOverrideDefaultClassCheckBoxAdapter());
		constraints.gridx       = 0;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 0;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(5, offset.left, 0, 0);
		add(customClassCheckBox, constraints);
		addAlignLeft(customClassCheckBox);
		
		// Custom Class chooser
		ClassChooserPanel containerClassChooserPanel = ClassChooserTools.buildPanel(
			this.buildContainerClassHolder(),
			this.buildClassRepositoryHolder(),
			ClassChooserTools.buildDeclarableReferenceFilter(),
			new JLabel(customClassCheckBox.getText()),
			this.getWorkbenchContextHolder()
		);
		containerClassChooserPanel.setEnabled(customClassCheckBox.isSelected());
		constraints.gridx       = 1;
		constraints.gridy       = 2;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.PAGE_START;
		constraints.insets      = new Insets(5, 5, 0, offset.right);
		add(containerClassChooserPanel, constraints);
		addPaneForAlignment(containerClassChooserPanel);
		buildContainerClassChooserComponentEnabler(containerClassChooserPanel);
		customClassCheckBox.addItemListener(buildContainerClassChooserEnabler(containerClassChooserPanel));
		
		// Use sorting check box
		this.useSortingCheckBox = this.buildUseSortingCheckBox();
		useSortingCheckBox.setEnabled(setContainerTypeRadioButton.isSelected());
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(10, offset.left, 0, 0);
		add(useSortingCheckBox, constraints);
		addAlignLeft(useSortingCheckBox);
		setContainerTypeRadioButton.addItemListener(buildUseSortingCheckboxEnabler(useSortingCheckBox));

		// Comparator class chooser
		ClassChooserPanel comparatorClassChooserPanel = ClassChooserTools.buildPanel(
			this.buildComparatorClassHolder(),
			this.buildClassRepositoryHolder(),
			ClassChooserTools.buildDeclarableReferenceFilter(),
			new JLabel(useSortingCheckBox.getText()),
			this.getWorkbenchContextHolder()
		);
		comparatorClassChooserPanel.setEnabled(useSortingCheckBox.isSelected());
		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 5, 0, offset.right);
		add(comparatorClassChooserPanel, constraints);
		addPaneForAlignment(comparatorClassChooserPanel);
		setContainerTypeRadioButton.addItemListener(buildComparatorClassChooserSetEnabler(comparatorClassChooserPanel));
		useSortingCheckBox.addItemListener(buildComparatorClassChooserSortingEnabler(comparatorClassChooserPanel));

		addHelpTopicId(this, "mapping.containerPolicy");
	}
	
	/**
	 * enable the use sorting checkbox when the "set" radio button is selected
	 */
	private ItemListener buildUseSortingCheckboxEnabler(final JComponent useJoiningCheckbox) {
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				useJoiningCheckbox.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		};
	}

	/**
	 * enable the comparator class chooser when the "set" radio button is selected and use sorting checkbox is selected
	 */
	private ItemListener buildComparatorClassChooserSetEnabler(final JComponent comparatorClassChooser) {
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				comparatorClassChooser.setEnabled((e.getStateChange() == ItemEvent.SELECTED) && useSortingCheckBox.isSelected());
			}
		};
	}

	/**
	 * enable the comparator class chooser when the use sorting checkbox is selected
	 */
	private ItemListener buildComparatorClassChooserSortingEnabler(final JComponent comparatorClassChooser) {
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				comparatorClassChooser.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		};
	}

}
