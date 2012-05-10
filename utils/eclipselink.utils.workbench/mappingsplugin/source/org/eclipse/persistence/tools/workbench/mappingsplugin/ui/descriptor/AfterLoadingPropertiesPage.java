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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassChooserPanel;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooserDialog;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.Spacer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassChooserTools;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta.ClassRepositoryHolder;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValuePropertyPropertyValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;


/**
 * @version 10.0.3
 */
final class AfterLoadingPropertiesPage 
	extends ScrollablePropertiesPage
{
	// **************** Variables *********************************************
	
	final static int EDITOR_WEIGHT = 12;
	
	private PropertyValueModel afterLoadingPolicyHolder;
	
	private PropertyValueModel postLoadMethodClassHolder;
	
	private PropertyValueModel postLoadMethodHolder;
	
	
	// **************** Constructors ******************************************
	
	AfterLoadingPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.afterLoadingPolicyHolder = this.buildAfterLoadingPolicyHolder();
		this.postLoadMethodClassHolder = this.buildPostLoadMethodClassHolder();
		this.postLoadMethodHolder = this.buildPostLoadMethodHolder();
	}
	
	private PropertyValueModel buildAfterLoadingPolicyHolder() {
		return new PropertyAspectAdapter(this.getSelectionHolder(), MWMappingDescriptor.AFTER_LOADING_POLICY_PROPERTY) {
			protected Object getValueFromSubject() {
				MWDescriptorPolicy policy = ((MWMappingDescriptor) this.subject).getAfterLoadingPolicy();
				return policy.isActive() ? policy : null;
			}
		};
	}
	
	private PropertyValueModel buildPostLoadMethodClassHolder() {
		return new PropertyAspectAdapter(this.afterLoadingPolicyHolder, MWDescriptorAfterLoadingPolicy.POST_LOAD_METHOD_CLASS_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorAfterLoadingPolicy) this.subject).getPostLoadMethodClass();
			}
			
			protected void setValueOnSubject(Object value) {
				MWClass type = (MWClass) value;
				((MWDescriptorAfterLoadingPolicy) this.subject).setPostLoadMethodClass(type);
				
				//TODO possibly inform the user that we are adding this class to the repository
				//we refresh the first instance of this class that we find on the classpath.
				//in the 'Manage Non-Descriptor Classes' dialog they can choose which one to refresh
				if (type != null) {
					try {
						if (type.isStub()) {
							type.refresh();
						}	
					} catch (ExternalClassNotFoundException exception) {
						showClassLoadingException();
					}
				}
			}
		};
	}
	
	private PropertyValueModel buildPostLoadMethodHolder() {
		PropertyValueModel propertyValueModel = new PropertyAspectAdapter(this.afterLoadingPolicyHolder, MWDescriptorAfterLoadingPolicy.POST_LOAD_METHOD_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWDescriptorAfterLoadingPolicy) this.subject).getPostLoadMethod();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWDescriptorAfterLoadingPolicy) this.subject).setPostLoadMethod((MWMethod)value);
			}
		};
		return new ValuePropertyPropertyValueModelAdapter(propertyValueModel, MWMethod.SIGNATURE_PROPERTY);
	}
	
	protected Component buildPage() {
		JPanel afterLoadingExecutePanel = new JPanel(new GridBagLayout());
		afterLoadingExecutePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Create the title label
		JLabel titleLabel = this.buildLabel("AFTER_LOAD_POLICY_DESCRIPTOR");
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 0, 10, 0);
		afterLoadingExecutePanel.add(titleLabel, constraints);
		
		// Create the label
		JLabel inClassLabel = this.buildLabel("AFTER_LOAD_POLICY_CLASS");
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		afterLoadingExecutePanel.add(inClassLabel, constraints);
		
		ClassChooserPanel postLoadMethodClassChooserPanel = this.buildPostLoadMethodClassChooserPanel(inClassLabel);
		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 5, 0, 0);
		afterLoadingExecutePanel.add(postLoadMethodClassChooserPanel, constraints);
		this.addPaneForAlignment(postLoadMethodClassChooserPanel);
		
		// Add the label
		JLabel staticMethodLabel = buildLabel("AFTER_LOAD_POLICY_STATIC_METHOD");
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 0, 0, 0);
		afterLoadingExecutePanel.add(staticMethodLabel, constraints);
		
		// Create the combo box
		ListChooser staticMethodChooser = buildPostLoadMethodListChooser();
		staticMethodLabel.setLabelFor(staticMethodChooser);
		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(5, 5, 0, 0);
		afterLoadingExecutePanel.add(staticMethodChooser, constraints);

		// Filler
		Spacer spacer = new Spacer();
		constraints.gridx = 2;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 0, 0);
		afterLoadingExecutePanel.add(spacer, constraints);
		addAlignRight(spacer);

		JPanel emptyPanel = new JPanel();
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		
		afterLoadingExecutePanel.add(emptyPanel, constraints);

		addHelpTopicId(afterLoadingExecutePanel, getHelpTopicId());		
		
		return afterLoadingExecutePanel;
	}
	
	// **************** Class chooser initialization **************************
	
	private ClassChooserPanel buildPostLoadMethodClassChooserPanel(JLabel label) {
		return ClassChooserTools.buildPanel(
				this.postLoadMethodClassHolder,
				this.buildClassRepositoryHolder(),
				ClassChooserTools.buildDeclarableReferenceFilter(),
				label,
				this.getWorkbenchContextHolder()
		);
	}
	
	private ClassRepositoryHolder buildClassRepositoryHolder() {
		return new ClassRepositoryHolder() {
			public MWClassRepository getClassRepository() {
				return AfterLoadingPropertiesPage.this.descriptor().getRepository();
			}
		};
	}
	
	
	// **************** Method chooser initialization *************************
	
	private ListChooser buildPostLoadMethodListChooser() {
		ListChooser listChooser = 
			new DefaultListChooser(
				this.buildExtendedPostLoadMethodComboBoxModel(),
				this.getWorkbenchContextHolder(),
				this.buildPostLoadMethodListDialogBuilder()
			);
		listChooser.setRenderer(DescriptorComponentFactory.buildMethodRenderer(resourceRepository()));
		this.updatePostLoadMethodChooser(listChooser);
		
		this.postLoadMethodClassHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildPostLoadMethodClassListener(listChooser));
		
		return listChooser;
	}
	
	private CachingComboBoxModel buildExtendedPostLoadMethodComboBoxModel() {
		return new ExtendedComboBoxModel(this.buildPostLoadMethodComboBoxModel());
	}
	
	private CachingComboBoxModel buildPostLoadMethodComboBoxModel() {
		return new IndirectComboBoxModel(this.postLoadMethodHolder, this.postLoadMethodClassHolder) {
			protected ListIterator listValueFromSubject(Object subject) {
				return AfterLoadingPropertiesPage.this.orderedPostLoadMethodChoices((MWClass) subject);
			}
		};
	}
	
	ListIterator orderedPostLoadMethodChoices(MWClass mwClass) {
		return CollectionTools.sort(mwClass.candidateDescriptorAfterLoadMethods()).listIterator();
	}
	
	private DefaultListChooserDialog.Builder buildPostLoadMethodListDialogBuilder() {
		DefaultListChooserDialog.Builder builder = new DefaultListChooserDialog.Builder();
		builder.setTitleKey("POST_LOAD_METHOD_LIST_BROWSER_DIALOG.title");
		builder.setListBoxLabelKey("POST_LOAD_METHOD_LIST_BROWSER_DIALOG.listLabel");
		builder.setStringConverter(DescriptorComponentFactory.buildMethodStringConverter());
		return builder;
	}

	
	private PropertyChangeListener buildPostLoadMethodClassListener(final ListChooser listChooser) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				AfterLoadingPropertiesPage.this.updatePostLoadMethodChooser(listChooser);
			}
		};
	}
	
	private String getHelpTopicId() {
		return "descriptor.afterloading";
	}

	void showClassLoadingException() {
        LabelArea label = new LabelArea(resourceRepository().getString("PROBLEM_LOADING_CLASS")); 
		JOptionPane.showMessageDialog(this, label, resourceRepository().getString("PROBLEM_LOADING_CLASS_TITLE"), JOptionPane.ERROR_MESSAGE);
		this.postLoadMethodClassHolder.setValue(null);	
	}
	
	// **************** Behavior **********************************************
	
	void updatePostLoadMethodChooser(ListChooser listChooser) {
		listChooser.setEnabled(this.postLoadMethodClassHolder.getValue() != null);
	}
	
	
	// **************** Convenience *******************************************
	
	MWMappingDescriptor descriptor() {
		return (MWMappingDescriptor) this.selection();
	}
}
