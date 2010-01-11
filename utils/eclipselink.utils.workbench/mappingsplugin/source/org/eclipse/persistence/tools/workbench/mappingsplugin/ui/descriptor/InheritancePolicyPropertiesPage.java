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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInheritancePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.DescriptorCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping.MappingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;


public abstract class InheritancePolicyPropertiesPage extends ScrollablePropertiesPage {
	
	protected final static int EDITOR_WEIGHT = 6;
	private PropertyValueModel inheritancePolicyHolder;
    private PropertyValueModel isRootHolder;
	
	protected ClassIndicatorPolicySubPanel classIndicatorPolicyPanel;
	
	public InheritancePolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected void initialize(PropertyValueModel descriptorNodeHolder) {
		super.initialize(descriptorNodeHolder);
		this.inheritancePolicyHolder = buildInheritancePolicyHolder();
		this.isRootHolder = buildIsRootHolder();
	}

	protected MWMappingDescriptor getDescriptor() {
		return (MWMappingDescriptor) getSelectionHolder().getValue();
	}

	protected MWDescriptorInheritancePolicy getInheritancePolicy() {
		return (MWDescriptorInheritancePolicy) this.inheritancePolicyHolder.getValue();
	}

	protected abstract String helpTopicIdPrefix();

	protected String helpTopicId() {
		return helpTopicIdPrefix() + ".inheritance";
	}
	
	protected void addRootListener(final RootListener listener) {
		getIsRootHolder().addPropertyChangeListener(
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					boolean enabled = Boolean.TRUE.equals(evt.getNewValue());
					listener.updateRootStatus(enabled);
				}
			}
		);
	}

	protected void addAllRootListeners(Collection listeners) {
		Iterator listenerIter = listeners.iterator();
		while(listenerIter.hasNext()) {
			RootListener listener = (RootListener)listenerIter.next();
			addRootListener(listener);
		}
	}
	
	private void setEverythingEnabled(Container container, boolean enabled) {
		container.setEnabled(enabled);

		for (int index = 0; index < container.getComponentCount(); index++) {
			Component component = container.getComponent(index);
			component.setEnabled(enabled);

			if (component instanceof Container) {
				setEverythingEnabled((Container)component, enabled);
			}
		}
	}

	protected PropertyChangeListener buildIsChildListener(final Container container) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				boolean enabled = Boolean.FALSE.equals(evt.getNewValue());
				setEverythingEnabled(container, enabled);
			}
		};
	}
	
	protected RadioButtonModelAdapter buildIsRootParentDescriptorRadioButtonModel(PropertyValueModel isRootHolder) {
		return new RadioButtonModelAdapter(isRootHolder, Boolean.TRUE);
	}
	
	protected RadioButtonModelAdapter buildIsChildDescriptorRadioButtonModel(PropertyValueModel isRootHolder) {
		return new RadioButtonModelAdapter(isRootHolder, Boolean.FALSE);
	}

	protected PropertyValueModel buildIsRootHolder() {
		return new PropertyAspectAdapter(this.inheritancePolicyHolder, MWDescriptorInheritancePolicy.IS_ROOT_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((MWDescriptorInheritancePolicy)subject).isRoot());	
			}
			protected void setValueOnSubject(Object value) {
				((MWDescriptorInheritancePolicy)subject).setIsRoot(((Boolean)value).booleanValue());
			}

		};
	}

	private PropertyValueModel buildProjectHolder() {
		return new PropertyAspectAdapter(getSelectionHolder()) {
			protected Object getValueFromSubject() {
				return ((MWDescriptor) subject).getProject();
			}
		};
	}
	


	public static DefaultListChooserDialog.Builder buildParentDescriptorChooserDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("PARENT_DESCRIPTOR_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("PARENT_DESCRIPTOR_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(buildDescriptorStringConverter());
		return builder;
	}
	
	private static StringConverter buildDescriptorStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return o == null ? "" : ((MWDescriptor) o).shortName();
			}
		};
	}

	private ListChooser buildParentDescriptorChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildParentDescriptorComboBoxModel(),
				getWorkbenchContextHolder(),
                MappingComponentFactory.buildDescriptorNodeSelector(getWorkbenchContextHolder()),
				buildParentDescriptorChooserDialogBuilder()
			);
		listChooser.setRenderer(new AdaptableListCellRenderer(new DescriptorCellRendererAdapter(resourceRepository())));
		return listChooser;
	}
	
	private CachingComboBoxModel buildParentDescriptorComboBoxModel() {
		return new ExtendedComboBoxModel(
			new IndirectComboBoxModel(this.buildParentDescriptorHolder(), this.inheritancePolicyHolder) {
				protected ListIterator listValueFromSubject(Object subject) {
					return InheritancePolicyPropertiesPage.this.sortedCandidateParentDescriptors((MWDescriptorInheritancePolicy) subject);
				}
			}
		);
	}
	
	private PropertyValueModel buildParentDescriptorHolder() {
		return new PropertyAspectAdapter(this.inheritancePolicyHolder, MWDescriptorInheritancePolicy.PARENT_DESCRIPTOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorInheritancePolicy) subject).getParentDescriptor();
			}
		
			protected void setValueOnSubject(Object value) {
				((MWDescriptorInheritancePolicy) subject).setParentDescriptor((MWMappingDescriptor) value);
			}	
		};	
	}
	
	private ListIterator sortedCandidateParentDescriptors(MWDescriptorInheritancePolicy inheritancePolicy) {
		return CollectionTools.sort(inheritancePolicy.candidateParentDescriptors()).listIterator();
	}
	
	protected ButtonModel buildIsRootDescriptorCheckBoxAdapter() {
		return new CheckBoxModelAdapter(this.isRootHolder);
	}

	private PropertyValueModel buildInheritancePolicyHolder() {
		return new PropertyAspectAdapter(getSelectionHolder(), MWMappingDescriptor.INHERITANCE_POLICY_PROPERTY) {
			protected Object getValueFromSubject(){
				MWDescriptorPolicy policy = ((MWMappingDescriptor) this.subject).getInheritancePolicy();
				return policy.isActive() ? policy : null;
			}
		};
	}
	
	protected JPanel buildIsChildPanel(boolean isChild) {
		
		JPanel parentDescriptorPanel = new JPanel(new BorderLayout());
			
			ListChooser parentDescriptorChooser = buildParentDescriptorChooser();
			parentDescriptorChooser.setEnabled(isChild);
			JLabel parentDescriptorLabel = buildLabel("PARENT_DESC");
			parentDescriptorLabel.setEnabled(isChild);
			parentDescriptorLabel.setLabelFor(parentDescriptorChooser);
				
			parentDescriptorPanel.add(parentDescriptorLabel, BorderLayout.LINE_START);
			parentDescriptorPanel.add(parentDescriptorChooser, BorderLayout.CENTER);
		
		addHelpTopicId(parentDescriptorPanel, helpTopicId() + ".parentDesc");

		return parentDescriptorPanel;
	}
	
	protected PropertyValueModel getInheritancePolicyHolder() {
		return this.inheritancePolicyHolder;
	}

	protected PropertyValueModel getIsRootHolder() {
		return this.isRootHolder;
	}

	public ClassIndicatorPolicySubPanel getClassIndicatorPolicyPanel() {
		return this.classIndicatorPolicyPanel;
	}

}
