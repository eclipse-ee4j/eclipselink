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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.help.HelpManager;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWReferenceObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformationMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project.ProjectNode;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.NodeSelector;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public class MappingComponentFactory extends SwingComponentFactory {

	// ************* Read only ************
	
	//mappingHolder expects an MWMapping
	public static JCheckBox buildReadOnlyCheckBox(ValueModel mappingHolder, ApplicationContext context) {
		JCheckBox checkBox = 
			buildCheckBox(
				"MAPPING_READ_ONLY_CHECK_BOX", 
				new CheckBoxModelAdapter(buildReadOnlyHolder(mappingHolder)), 
				context.getResourceRepository()
			);
		context.getHelpManager().addTopicID(checkBox, "mapping.readOnly");
		return checkBox;
	}	
	
	private static PropertyValueModel buildReadOnlyHolder(ValueModel mappingHolder) {
		return new PropertyAspectAdapter(mappingHolder, MWMapping.READ_ONLY_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWMapping) subject).isReadOnly());
			}
			protected void setValueOnSubject(Object value) {
				((MWMapping) subject).setReadOnly(((Boolean) value).booleanValue());
			}
		};
	}



	// ************* reference descriptor ************	
	
	//referenceMappingHolder expects an MWReferenceObjectMapping
	public static ListChooser buildReferenceDescriptorChooser(ValueModel referenceMappingHolder, WorkbenchContextHolder contextHolder) {
		ListChooser listChooser = 
			new DefaultListChooser(
				buildExtendedReferenceDescriptorComboBoxModel(referenceMappingHolder),
				contextHolder,
                buildDescriptorNodeSelector(contextHolder),
				buildReferenceChooserDialogBuilder(contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository())
			);
		listChooser.setRenderer(buildDescriptorListRenderer(contextHolder.getWorkbenchContext().getApplicationContext().getResourceRepository()));
		return listChooser;
	}
	
    public static NodeSelector buildDescriptorNodeSelector(final WorkbenchContextHolder contextHolder) {
        return new NodeSelector() {       
            public void selectNodeFor(Object item) {
                ProjectNode projectNode = (ProjectNode) contextHolder.getWorkbenchContext().getNavigatorSelectionModel().getSelectedProjectNodes()[0];
                projectNode.selectDescriptorNodeFor((MWDescriptor) item, contextHolder.getWorkbenchContext().getNavigatorSelectionModel());       
            }
        };
    }
    
	private static ComboBoxModel buildExtendedReferenceDescriptorComboBoxModel(ValueModel referenceMappingHolder) {
		return new ExtendedComboBoxModel(buildReferenceDescriptorComboBoxModel(referenceMappingHolder));
	}
	
	private static ComboBoxModel buildReferenceDescriptorComboBoxModel(ValueModel referenceMappingHolder) {
		return new ComboBoxModelAdapter(
			buildSortedCandidateReferenceDescriptorsListValue(referenceMappingHolder),
			buildReferenceDescriptorValue(referenceMappingHolder)
		); 		
	}
	
	private static ListValueModel buildSortedCandidateReferenceDescriptorsListValue(ValueModel referenceMappingHolder) {
		return new SortedListValueModelAdapter(
			buildNotifyingCandidateReferenceDescriptorsListValue(referenceMappingHolder),
			buildDescriptorComparator()
		);
	}
	
	private static ListValueModel buildNotifyingCandidateReferenceDescriptorsListValue(ValueModel referenceMappingHolder) {
		return new ItemPropertyListValueModelAdapter(
			buildCandidateReferenceDescriptorsCollectionValue(referenceMappingHolder),
			MWDescriptor.NAME_PROPERTY
		);
	}
	
	private static CollectionValueModel buildCandidateReferenceDescriptorsCollectionValue(final ValueModel referenceMappingHolder) {
		return new FilteringCollectionValueModel(buildDescriptorsCollectionValue(referenceMappingHolder)) {
			protected boolean accept(Object value) {
				MWReferenceObjectMapping  referenceMapping = (MWReferenceObjectMapping) referenceMappingHolder.getValue();
				return (referenceMapping == null) ? false : referenceMapping.descriptorIsValidReferenceDescriptor((MWDescriptor) value);
			}
		};
	}
	
	private static CollectionValueModel buildDescriptorsCollectionValue(ValueModel referenceMappingHolder) {
		return new CollectionAspectAdapter(buildProjectValue(referenceMappingHolder), MWProject.DESCRIPTORS_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((MWProject) this.subject).descriptors();
			}
			
			protected int sizeFromSubject() {
				return ((MWProject) this.subject).descriptorsSize();
			}
		};
	}
	
	private static PropertyValueModel buildProjectValue(ValueModel referenceMappingHolder) {
		return new PropertyAspectAdapter(referenceMappingHolder) {
			protected Object getValueFromSubject() {
				return ((MWMapping) subject).getProject();
			}
		};
	}	
	
	private static PropertyValueModel buildReferenceDescriptorValue(ValueModel referenceMappingHolder) {
		return new PropertyAspectAdapter(referenceMappingHolder, MWReferenceObjectMapping.REFERENCE_DESCRIPTOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWReferenceObjectMapping) this.subject).getReferenceDescriptor();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWReferenceObjectMapping) this.subject).setReferenceDescriptor((MWDescriptor) value);
			}
		};
	}
	
	private static Comparator buildDescriptorComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((MWDescriptor) o1).displayStringWithPackage().compareTo(((MWDescriptor) o2).displayStringWithPackage());
			}
		};
	}
	
	private static DefaultListChooserDialog.Builder buildReferenceChooserDialogBuilder(ResourceRepository resourceRepository) {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("REFERENCE_DESCRIPTOR_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("REFERENCE_DESCRIPTOR_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildDescriptorStringConverter(resourceRepository));
		return builder;
	}
	
	private static ListCellRenderer buildDescriptorListRenderer(ResourceRepository resourceRepository) {
		return new AdaptableListCellRenderer(new DescriptorCellRendererAdapter(resourceRepository));
	}
	
	private static StringConverter buildDescriptorStringConverter(final ResourceRepository resourceRepository) {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWDescriptor) o).displayStringWithPackage();
			}
		};
	}
	
	
	// **************** Private Owned *****************************************
	
	//referenceMappingHolder expects an MWReferenceMapping
	public static JCheckBox buildPrivateOwnedCheckBox(
		ValueModel referenceMappingHolder, 
		ResourceRepository resourceRepository,
		HelpManager helpManager
	) {
		JCheckBox checkBox = 
			buildCheckBox(
				"MAPPING_PRIVATE_OWNED_CHECK_BOX", 
				buildPrivateOwnedCheckBoxAdapter(referenceMappingHolder), 
				resourceRepository
			);
		helpManager.addTopicID(checkBox, "mapping.privateOwned");
		return checkBox;
	}
	
	private static ButtonModel buildPrivateOwnedCheckBoxAdapter(ValueModel referenceMappingHolder) {
		return new CheckBoxModelAdapter(buildPrivateOwnedHolder(referenceMappingHolder));
	}
	
	private static PropertyValueModel buildPrivateOwnedHolder(ValueModel referenceMappingHolder) {
		return new PropertyAspectAdapter(referenceMappingHolder, MWReferenceMapping.PRIVATE_OWNED_PROPERTY) {
			protected Object getValueFromSubject() {
				MWReferenceMapping mapping = (MWReferenceMapping) subject;
				return Boolean.valueOf(mapping.isPrivateOwned());
			}
			
			protected void setValueOnSubject(Object value) {
				MWReferenceMapping mapping = (MWReferenceMapping) subject;
				mapping.setPrivateOwned(((Boolean) value).booleanValue());
			}
		};
	}
	
	
	// **************** Uses indirection check box ****************************
	
	/** indirectableMappingHolder must have a MWIndirectableMapping value */
	public static JCheckBox buildUsesIndirectionCheckBox(
		ValueModel indirectableMappingHolder,
		ResourceRepository resourceRepository,
		HelpManager helpManager
	) {
		JCheckBox checkBox =
			buildCheckBox(
				"MAPPING_USE_INDIRECTION_CHECK_BOX", 
				buildUsesIndirectionCheckBoxModel(indirectableMappingHolder),
				resourceRepository
			);
		helpManager.addTopicID(checkBox, "mapping.indirection");
		
		return checkBox;
	}
	
	private static ButtonModel buildUsesIndirectionCheckBoxModel(ValueModel indirectableMappingHolder) {
		return new CheckBoxModelAdapter(buildUsesIndirectionValue(indirectableMappingHolder));
	}
	
	private static PropertyValueModel buildUsesIndirectionValue(ValueModel indirectableMappingHolder) {
		return new PropertyAspectAdapter(indirectableMappingHolder, MWIndirectableMapping.INDIRECTION_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWIndirectableMapping) this.subject).usesValueHolderIndirection());
			}
			
			protected void setValueOnSubject(Object value) {
				boolean useValueHolderIndirection = ((Boolean) value).booleanValue();
				
				if (useValueHolderIndirection) {
					((MWIndirectableMapping) this.subject).setUseValueHolderIndirection();
				}
				else {
					((MWIndirectableMapping) this.subject).setUseNoIndirection();
				}
			}
		};
	}
	
	
	// **************** Mutable check box *************************************
	
	/** transformationMappingHolder must have a MWTransformationMapping value */
	public static JCheckBox buildMutableCheckBox(
		ValueModel transformationMappingHolder,
		ResourceRepository resourceRepository,
		HelpManager helpManager
	) {
		JCheckBox checkBox =
			buildCheckBox(
				"TRANSFORMATION_MAPPING_IS_MUTABLE",
				buildMutableCheckBoxModel(transformationMappingHolder),
				resourceRepository
			);
		
		helpManager.addTopicID(checkBox, "mapping.transformation.mutable");
		
		return checkBox;
	}
	
	private static ButtonModel buildMutableCheckBoxModel(ValueModel transformationMappingHolder) {
		return new CheckBoxModelAdapter(buildMutableValue(transformationMappingHolder));
	}
	
	private static PropertyValueModel buildMutableValue(ValueModel transformationMappingHolder) {
		return new PropertyAspectAdapter(transformationMappingHolder, MWTransformationMapping.MUTABLE_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWTransformationMapping) subject).isMutable());
			}
			
			protected void setValueOnSubject(Object value) {
				((MWTransformationMapping) subject).setMutable(((Boolean) value).booleanValue());
			}
		};
	}
	
	// **************** Advanced button CollectionContainerPolicies ***************
	public static JPanel buildContainerPolicyOptionsBrowser(final WorkbenchContextHolder holder,
            											final PropertyValueModel subjectHolder,
														final String topicId) {
		JPanel subPanel = new ContainerPolicyPanel(subjectHolder, holder);
		return buildContainerOptionsBrowser(holder, subjectHolder, subPanel, topicId);		
	}

	public static JPanel buildDirectMapContainerPolicyOptionsBrowser(final WorkbenchContextHolder holder,
														final PropertyValueModel subjectHolder,
														final String topicId) {
		JPanel subPanel = new DirectMapContainerPolicyPanel(subjectHolder, holder);
		return buildContainerOptionsBrowser(holder, subjectHolder, subPanel, topicId);

	}
	public static JPanel buildCollectionContainerPolicyOptionsBrowser(final WorkbenchContextHolder holder,
											            final PropertyValueModel subjectHolder,
											            final String topicId) {
		
		JPanel subPanel = new CollectionContainerPolicyPanel(subjectHolder, holder);
		return buildContainerOptionsBrowser(holder, subjectHolder, subPanel, topicId);

	}
	
	private static JPanel buildContainerOptionsBrowser(final WorkbenchContextHolder holder,
	                                                   final PropertyValueModel subjectHolder,
													   final JPanel subPanel,
	                                                   final String topicId) {

		GridBagConstraints constraints = new GridBagConstraints();
		
		HelpManager help = holder.getWorkbenchContext().getApplicationContext().getHelpManager();
		
		JPanel container = new JPanel(new GridBagLayout());
		String advancedContainerOptionsTitle = holder.getWorkbenchContext().getApplicationContext().getResourceRepository().getString("COLLECTION_OPTIONS_ADVANCED_BUTTON");

		subPanel.setVisible(false);
		subPanel.setBorder(buildTitledBorder(holder.getWorkbenchContext().getApplicationContext().getResourceRepository(), "COLLECTION_OPTIONS_ADVANCED_BUTTON"));

		JButton button = buildButton("COLLECTION_OPTIONS_ADVANCED_BUTTON", holder.getWorkbenchContext().getApplicationContext().getResourceRepository());
		String buttonText = button.getText();
		buttonText += " >>";
		button.setText(buttonText);
		
		constraints.gridx       = 0;
		constraints.gridy       = 0;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.NONE;
		constraints.anchor      = GridBagConstraints.LINE_START;
		constraints.insets      = new Insets(0, 0, 0, 0);
		
		help.addTopicID(button, topicId + ".advancedContainerOptions");
		container.add(button, constraints);
		
		constraints.gridx       = 0;
		constraints.gridy       = 1;
		constraints.gridwidth   = 1;
		constraints.gridheight  = 1;
		constraints.weightx     = 1;
		constraints.weighty     = 0;
		constraints.fill        = GridBagConstraints.HORIZONTAL;
		constraints.anchor      = GridBagConstraints.CENTER;
		constraints.insets      = new Insets(0, 0, 0, 0);
		
		help.addTopicID(subPanel, topicId + ".advancedContainerOptions");
		container.add(subPanel, constraints);

		button.addActionListener(MappingComponentFactory.buildAdvancedCollectionOptionsButtonActionListener(button, subPanel, advancedContainerOptionsTitle));
		
		return container;
	}
	
	private static ActionListener buildAdvancedCollectionOptionsButtonActionListener(final JButton button, final JPanel selectionPanel, final String title) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectionPanel.isVisible()) {
					selectionPanel.setVisible(false);
					button.setText(title + " >>");
				} else {
					selectionPanel.setVisible(true);
					button.setText("<<");
				}
			}
		};
	}

}
