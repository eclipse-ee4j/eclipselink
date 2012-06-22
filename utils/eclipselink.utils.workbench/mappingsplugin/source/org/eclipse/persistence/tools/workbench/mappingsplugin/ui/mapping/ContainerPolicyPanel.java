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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.DefaultingContainerClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CompositeCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;


public final class ContainerPolicyPanel 
	extends AbstractSubjectPanel
{
	private PropertyValueModel containerPolicyHolder;
	PropertyValueModel containerTypeHolder;
	private PropertyValueModel defaultingContainerClassHolder;
	private PropertyValueModel mapContainerPolicyHolder;
	private JCheckBox useSortingCheckBox;

	private static final String CONTAINER_TYPE_COLLECTION = "collection";
	private static final String CONTAINER_TYPE_LIST = "list";
	private static final String CONTAINER_TYPE_MAP = "map";
	private static final String CONTAINER_TYPE_SET = "set";


	public ContainerPolicyPanel(ValueModel containerMappingHolder, WorkbenchContextHolder contextHolder) {
		super(containerMappingHolder, contextHolder);
	}

	protected void initialize(ValueModel containerMappingHolder) {
		super.initialize(containerMappingHolder);
		this.containerTypeHolder = this.buildContainerTypeHolder();
		this.containerPolicyHolder = this.buildContainerPolicyHolder();
		this.mapContainerPolicyHolder = this.buildMapContainerPolicyHolder(containerPolicyHolder);
		this.defaultingContainerClassHolder = this.buildDefaultingContainerClassHolder(containerPolicyHolder);
	}

	/**
	 * build a value model that gets and sets the locally-defined container types
	 * that can be used by the radio button model adapters
	 */
	private PropertyValueModel buildContainerTypeHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				MWContainerPolicy cp = ((MWContainerMapping) this.subject).getContainerPolicy();
				if (cp instanceof MWCollectionContainerPolicy) {
					return CONTAINER_TYPE_COLLECTION;
				} else if (cp instanceof MWListContainerPolicy) {
					return CONTAINER_TYPE_LIST;
				} else if (cp instanceof MWSetContainerPolicy) {
					return CONTAINER_TYPE_SET;
				} else if (cp instanceof MWMapContainerPolicy) {
					return CONTAINER_TYPE_MAP;
				}
				return null;
			}

			protected void setValueOnSubject(Object value) {
				if (value == CONTAINER_TYPE_COLLECTION) {
					((MWContainerMapping) this.subject).setCollectionContainerPolicy();
				} else if (value == CONTAINER_TYPE_LIST) {
					((MWContainerMapping) this.subject).setListContainerPolicy();
				} else if (value == CONTAINER_TYPE_SET) {
					((MWContainerMapping) this.subject).setSetContainerPolicy();
				} else if (value == CONTAINER_TYPE_MAP) {
					((MWMapContainerMapping) this.subject).setMapContainerPolicy();
				}
			}
		};
	}

	/**
	 * return the container mapping's container policy
	 */
	private PropertyValueModel buildContainerPolicyHolder() {
		return new PropertyAspectAdapter(this.getSubjectHolder(), MWContainerMapping.CONTAINER_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWContainerMapping) this.subject).getContainerPolicy();
			}
		};
	}

	/**
	 * if the container policy is a MAP container policy return it;
	 * otherwise return null
	 */
	private PropertyValueModel buildMapContainerPolicyHolder(PropertyValueModel containerPolicyHolder) {
		return new FilteringPropertyValueModel(containerPolicyHolder) {
			protected boolean accept(Object value) {
				return (value instanceof MWMapContainerPolicy);
			}
		};
	}

	/**
	 * return the container policy's "defaulting container class"
	 */
	private PropertyValueModel buildDefaultingContainerClassHolder(PropertyValueModel containerPolicyHolder) {
		return new PropertyAspectAdapter(containerPolicyHolder) {
			protected Object getValueFromSubject() {
				return ((MWContainerPolicy) this.subject).getDefaultingContainerClass(); 
			}
		};
	}

	protected void initializeLayout() {
		GridBagConstraints constraints = new GridBagConstraints();
		ButtonGroup buttonGroup = new ButtonGroup();
		Insets offset = BorderFactory.createTitledBorder("m").getBorderInsets(this);
		offset.left += 5; offset.right += 5;

		// Container Type group box
		JPanel containerTypeGroupBox = new JPanel(new GridBagLayout());
		containerTypeGroupBox.setBorder(BorderFactory.createCompoundBorder(
			buildTitledBorder("CONTAINER_POLICY_GROUP_BOX"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		add(containerTypeGroupBox, constraints);

		// Container Type sub-pane
		JPanel containerTypeSubPane = new JPanel(new GridLayout(1, 4, 20, 0));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
		containerTypeGroupBox.add(containerTypeSubPane, constraints);

		// This pushes all the radio buttons to the left so they won't be spread out
		Spacer spacer = new Spacer();
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(0, 0, 0, 0);
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

		// Map container type radio button
		JRadioButton mapContainerTypeRadioButton = this.buildMapContainerTypeRadioButton();
		containerTypeSubPane.add(mapContainerTypeRadioButton);
		buttonGroup.add(mapContainerTypeRadioButton);

		// Override default container class check box
		JCheckBox overrideDefaultContainerClassCheckBox = this.buildOverrideDefaultContainerClassCheckBox();
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(10, offset.left, 0, 0);
		add(overrideDefaultContainerClassCheckBox, constraints);
		addAlignLeft(overrideDefaultContainerClassCheckBox);

		// Container class chooser
		ClassChooserPanel containerClassChooserPanel = ClassChooserTools.buildPanel(
			this.buildContainerClassHolder(),
			this.buildClassRepositoryHolder(),
			ClassChooserTools.buildDeclarableReferenceFilter(),
			new JLabel(overrideDefaultContainerClassCheckBox.getText()),
			this.getWorkbenchContextHolder()
		);
		containerClassChooserPanel.setEnabled(overrideDefaultContainerClassCheckBox.isSelected());
		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(10, 5, 0, offset.right);
		add(containerClassChooserPanel, constraints);
		addPaneForAlignment(containerClassChooserPanel);
		overrideDefaultContainerClassCheckBox.addItemListener(buildContainerClassChooserEnabler(containerClassChooserPanel));

		// Key Method chooser
		JComponent keyMethodWidgets = this.buildLabeledComponent(
			"CONTAINER_POLICY_KEY_METHOD_CHOOSER",
			this.buildKeyMethodChooser());
		keyMethodWidgets.setEnabled(mapContainerTypeRadioButton.isSelected());
		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.insets = new Insets(5, offset.left, 0, offset.right);
		add(keyMethodWidgets, constraints);
		mapContainerTypeRadioButton.addItemListener(buildKeyMethodWidgetsEnabler(keyMethodWidgets));

		// Use sorting check box
		this.useSortingCheckBox = this.buildUseSortingCheckBox();
		useSortingCheckBox.setEnabled(setContainerTypeRadioButton.isSelected());
		constraints.gridx = 0;
		constraints.gridy = 6;
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
		constraints.gridy = 6;
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

	}

	/**
	 * Collection radio button
	 */
	private JRadioButton buildCollectionContainerTypeRadioButton() {
		return this.buildRadioButton(
			"CONTAINER_POLICY_COLLECTION_CLASS_RADIO_BUTTON",
			this.buildCollectionContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildCollectionContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.containerTypeHolder, CONTAINER_TYPE_COLLECTION);
	}

	/**
	 * List radio button
	 */
	private JRadioButton buildListContainerTypeRadioButton() {
		return this.buildRadioButton(
			"CONTAINER_POLICY_LIST_CLASS_RADIO_BUTTON",
			this.buildListContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildListContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.containerTypeHolder, CONTAINER_TYPE_LIST);
	}

	/**
	 * Map radio button
	 */
	private JRadioButton buildMapContainerTypeRadioButton() {
		return  this.buildRadioButton(
			"CONTAINER_POLICY_MAP_CLASS_RADIO_BUTTON",
			this.buildMapContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildMapContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.containerTypeHolder, CONTAINER_TYPE_MAP);
	}

	/**
	 * Set radio button
	 */
	private JRadioButton buildSetContainerTypeRadioButton() {
		return this.buildRadioButton(
			"CONTAINER_POLICY_SET_CLASS_RADIO_BUTTON",
			this.buildSetContainerTypeRadioButtonAdapter()
		);
	}

	private ButtonModel buildSetContainerTypeRadioButtonAdapter() {
		return new RadioButtonModelAdapter(this.containerTypeHolder, CONTAINER_TYPE_SET);
	}

	/**
	 * container class chooser
	 */
	private PropertyValueModel buildContainerClassHolder() {
		return new PropertyAspectAdapter(this.defaultingContainerClassHolder, DefaultingContainerClass.CONTAINER_CLASS_PROPERTY, DefaultingContainerClass.USES_DEFAULT_CONTAINER_CLASS_PROPERTY) {
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

	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return ((MWModel) subject()).getRepository();
			}
		};
	}

	/**
	 * enable the container class chooser when the "override default
	 * container class" check box is checked
	 */
	private ItemListener buildContainerClassChooserEnabler(final ClassChooserPanel containerClassChooserPanel) {
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				containerClassChooserPanel.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		};
	}

	/**
	 * override default container class check box
	 */
	private JCheckBox buildOverrideDefaultContainerClassCheckBox() {
		return this.buildCheckBox(
			"CONTAINER_POLICY_OVERRIDE_DEFAULT_CLASS_CHECK_BOX",
			this.buildOverrideDefaultClassCheckBoxAdapter()
		);
	}

	private ButtonModel buildOverrideDefaultClassCheckBoxAdapter() {
		return new CheckBoxModelAdapter(buildOverrideDefaultClassHolder());
	}

	private PropertyValueModel buildOverrideDefaultClassHolder() {
		return new TransformationPropertyValueModel(this.buildUseDefaultContainerClassHolder()) {
			private Boolean oppositeValue(Boolean value) {
				return (value == null) ? null : Boolean.valueOf( ! value.booleanValue());
			}
			protected Object reverseTransform(Object value) {
				return this.oppositeValue((Boolean) value);
			}
			protected Object transform(Object value) {
				return this.oppositeValue((Boolean) value);
			}
		};
	}

	private PropertyValueModel buildUseDefaultContainerClassHolder() {
		return new PropertyAspectAdapter(this.defaultingContainerClassHolder, DefaultingContainerClass.USES_DEFAULT_CONTAINER_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((DefaultingContainerClass) this.subject).usesDefaultContainerClass());
			}
			
			protected void setValueOnSubject(Object value) {
				((DefaultingContainerClass) this.subject).setUseDefaultContainerClass(((Boolean) value).booleanValue());
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

	/**
	 * key method chooser
	 */
	private ListChooser buildKeyMethodChooser() {
		ListChooser chooser = new DefaultListChooser(this.buildKeyMethodComboBoxModel(), getWorkbenchContextHolder());
		chooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(resourceRepository())));
		return chooser;
	}

	private ComboBoxModel buildKeyMethodComboBoxModel() {
		return new ComboBoxModelAdapter(
			this.buildSortedCandidateKeyMethodsHolder(),
			this.buildKeyMethodHolder()
		);
	}

	/**
	 * sort the candidate methods
	 * OMG - what a freakin' mess: getting these methods and listening for changes...
	 */
	private ListValueModel buildSortedCandidateKeyMethodsHolder() {
		return new SortedListValueModelAdapter(this.buildCandidateKeyMethodsHolder());
	}

	/**
	 * return only the candidate methods
	 */
	private CollectionValueModel buildCandidateKeyMethodsHolder() {
		return new FilteringCollectionValueModel(buildReferenceTypeMethodsSignatureAdapter()) {
			protected boolean accept(Object value) {
				return ((MWMethod) value).isCandidateMapContainerPolicyKeyMethod();
			}
		};
	}

	/**
	 * listen to the reference type's lineage methods' signatures, because if one
	 * of the signatures changes the list must be re-filtered
	 */
	private ListValueModel buildReferenceTypeMethodsSignatureAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildReferenceTypeLineageMethodsAdapter(), MWMethod.SIGNATURE_PROPERTY);
	}

	/**
	 * return all the methods in the reference type's lineage
	 */
	private CollectionValueModel buildReferenceTypeLineageMethodsAdapter() {
		return new CompositeCollectionValueModel(this.buildReferenceTypeLineageAdapter()) {
			protected CollectionValueModel transform(Object value) {
				return ContainerPolicyPanel.this.buildMethodsAdapter((MWClass) value);
			}
		};
	}

	/**
	 * return the specified type's methods
	 */
	CollectionValueModel buildMethodsAdapter(MWClass type) {
		return new CollectionAspectAdapter(MWClass.METHODS_COLLECTION, type) {
			protected Iterator getValueFromSubject() {
				return ((MWClass) this.subject).methods();
			}
			protected int sizeFromSubject() {
				return ((MWClass) this.subject).methodsSize();
			}
		};
	}

	/**
	 * return the reference type lineage
	 */
	private CollectionValueModel buildReferenceTypeLineageAdapter() {
		return new CollectionAspectAdapter(this.buildReferenceTypeAdapter(), MWClass.SUPERCLASSES_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWClass) this.subject).lineage();
			}
		};
	}

	/**
	 * return the reference type
	 */
	private ValueModel buildReferenceTypeAdapter() {
		return new PropertyAspectAdapter(this.buildReferenceDescriptorAdapter(), MWDescriptor.MW_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptor) this.subject).getMWClass();
			}
		};
	}

	/**
	 * return the reference descriptor
	 */
	private ValueModel buildReferenceDescriptorAdapter() {
		return new PropertyAspectAdapter(this.buildMapContainerMappingAdapter(), MWReferenceObjectMapping.REFERENCE_DESCRIPTOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWReferenceObjectMapping) this.subject).getReferenceDescriptor();
			}
		};
	}

	/**
	 * return the map container mapping (e.g. MWOneToManyMapping)
	 */
	private ValueModel buildMapContainerMappingAdapter() {
		return new TransformationPropertyValueModel(this.mapContainerPolicyHolder) {
			protected Object transform(Object value) {
				return (value == null) ? null : ((MWMapContainerPolicy) value).getMapContainerMapping();
			}
			protected Object reverseTransform(Object value) {
				throw new UnsupportedOperationException();
			}
		};
	}

	private PropertyValueModel buildKeyMethodHolder() {
		return new PropertyAspectAdapter(this.mapContainerPolicyHolder, MWMapContainerPolicy.KEY_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWMapContainerPolicy) this.subject).getKeyMethod();
			}
			protected void setValueOnSubject(Object value) {
				((MWMapContainerPolicy) this.subject).setKeyMethod((MWMethod) value);
			}
		};
	}

	/**
	 * enable the key method chooser when the "map" radio button is selected
	 */
	private ItemListener buildKeyMethodWidgetsEnabler(final JComponent keyMethodWidgets) {
		return new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				keyMethodWidgets.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		};
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
