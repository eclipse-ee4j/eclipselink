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

// JDK
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorEventsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.common.cell.MethodCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.SwitcherPanel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExtendedComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.Transformer;



/**
 * @version 10.1.3
 */
public final class EventsPolicyPropertiesPage extends ScrollablePropertiesPage
{
	
	public final static int EDITOR_WEIGHT = 11;
	
	private PropertyValueModel eventsPolicyValueModel;
	private SimplePropertyValueModel methodTypesSelectionModel;
	
	private JPanel postXMethodsPanel;
	private JPanel updatingMethodsPanel;
	private JPanel insertingMethodsPanel;
	private JPanel writingMethodsPanel;
	private JPanel deletingMethodsPanel;
	private JList methodTypeSelectionList;

	private String POST_X_METHODS;
	private String UPDATING_METHODS;
	private String INSERTING_METHODS;
	private String WRITING_METHODS;
	private String DELETING_METHODS;

	
	EventsPolicyPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder)
	{
		super(nodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel nodeHolder)
	{
		super.initialize(nodeHolder);
		
		// initialize List items
		POST_X_METHODS = resourceRepository().getString("EVENTS_POLICY_POST_X_METHODS");
		UPDATING_METHODS = resourceRepository().getString("EVENTS_POLICY_UPDATING_METHODS");
		INSERTING_METHODS = resourceRepository().getString("EVENTS_POLICY_INSERTING_METHODS");
		WRITING_METHODS = resourceRepository().getString("EVENTS_POLICY_WRITING_METHODS");
		DELETING_METHODS = resourceRepository().getString("EVENTS_POLICY_DELETING_METHODS");
	}
	
	
	protected String getHelpTopicId()
	{
		return "descriptor.events";
	}

	protected Component buildPage()
	{
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				
		JComponent methodTypeListPanel = buildMethodTypeListPanel();

		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.VERTICAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 0, 0, 0);

		mainPanel.add(methodTypeListPanel, constraints);

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(0, 5, 0, 0);

		SwitcherPanel methodsSwitchingPanel = new SwitcherPanel(buildMethodTypeValueModel(), new MethodTypeTransformer());
		
		mainPanel.add(methodsSwitchingPanel, constraints);
		
		getMethodTypeSelectionList().setSelectedValue(POST_X_METHODS, true);
		
		Dimension originalDimension = getMethodTypeSelectionList().getPreferredSize();
		getMethodTypeSelectionList().setPreferredSize(new Dimension(originalDimension.width + 20, originalDimension.height));

		addHelpTopicId(mainPanel, getHelpTopicId());
		
		return mainPanel;
	}
	
	private JComponent buildMethodTypeListPanel()
	{
		JList methodTypeList = getMethodTypeSelectionList(); 
		return new JScrollPane(methodTypeList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}

	
	private JPanel buildPostXMethodsPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel postXMethodPanel = new JPanel(new GridBagLayout());
		postXMethodPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel postBuildMethodLabel = buildLabel("EVENTS_POLICY_BUILD");
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		postXMethodPanel.add(postBuildMethodLabel, constraints);

		
		// Create the combo box
		ListChooser postBuildMethodChooser = buildPostBuildMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		postXMethodPanel.add(postBuildMethodChooser, constraints);
		postBuildMethodLabel.setLabelFor(postBuildMethodChooser);
		
		
		JLabel postCloneMethodLabel = buildLabel("EVENTS_POLICY_CLONE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		postXMethodPanel.add(postCloneMethodLabel, constraints);

		// Create the combo box
		ListChooser postCloneMethodChooser = buildPostCloneMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 5, 5, 0);

		postXMethodPanel.add(postCloneMethodChooser, constraints);
		postCloneMethodLabel.setLabelFor(postCloneMethodChooser);
		
		JLabel postMergeMethodLabel = buildLabel("EVENTS_POLICY_MERGE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		postXMethodPanel.add(postMergeMethodLabel, constraints);		
		
		// Create the combo box
		ListChooser postMergeMethodChooser = buildPostMergeMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		postXMethodPanel.add(postMergeMethodChooser, constraints);
		postMergeMethodLabel.setLabelFor(postMergeMethodChooser);
		
		
		JLabel postRefreshMethodLabel = buildLabel("EVENTS_POLICY_REFRESH");
		
		constraints.gridx			= 0;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		postXMethodPanel.add(postRefreshMethodLabel, constraints);
		
		// Create the combo box
		ListChooser postRefreshMethodChooser = buildPostRefreshMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 3;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		postXMethodPanel.add(postRefreshMethodChooser, constraints);
		postRefreshMethodLabel.setLabelFor(postRefreshMethodChooser);
		
		addHelpTopicId(postXMethodPanel, getHelpTopicId() + ".postX");
		
		return postXMethodPanel;
	}

	protected JPanel buildDeleteMethodsPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel deletePanel = new JPanel(new GridBagLayout());
		deletePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel preDeleteMethodLabel = buildLabel("EVENTS_POLICY_PRE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		deletePanel.add(preDeleteMethodLabel, constraints);
		
		
		// Create the combo box
		ListChooser preDeleteChooser = buildPreDeleteMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		deletePanel.add(preDeleteChooser, constraints);
		preDeleteMethodLabel.setLabelFor(preDeleteChooser);
		
		JLabel postDeleteMethodLabel = buildLabel("EVENTS_POLICY_POST");
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		deletePanel.add(postDeleteMethodLabel, constraints);
				
		// Create the combo box
		ListChooser postDeleteChooser = buildPostDeleteMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		deletePanel.add(postDeleteChooser, constraints);
		postDeleteMethodLabel.setLabelFor(postDeleteChooser);

		addHelpTopicId(deletePanel, getHelpTopicId() + ".delete");
		
		return deletePanel;
	}

	private JPanel buildInsertMethodsPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel insertPanel = new JPanel(new GridBagLayout());
		insertPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel preInsertMethodLabel = buildLabel("EVENTS_POLICY_PRE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		insertPanel.add(preInsertMethodLabel, constraints);
		
		// Create the combo box
		ListChooser preInsertChooser = buildPreInsertMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		insertPanel.add(preInsertChooser, constraints);
		preInsertMethodLabel.setLabelFor(preInsertChooser);
		
		JLabel aboutToInsertMethodLabel = buildLabel("EVENTS_POLICY_ABOUT_TO");
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		insertPanel.add(aboutToInsertMethodLabel, constraints);

		// Create the combo box
		ListChooser aboutToInsertChooser = buildAboutToInsertMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		insertPanel.add(aboutToInsertChooser, constraints);
		aboutToInsertMethodLabel.setLabelFor(aboutToInsertChooser);
		
		JLabel postInsertMethodLabel = buildLabel("EVENTS_POLICY_POST");
		
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		insertPanel.add(postInsertMethodLabel, constraints);
		
		// Create the combo box
		ListChooser postInsertChooser = buildPostInsertMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		insertPanel.add(postInsertChooser, constraints);
		postInsertMethodLabel.setLabelFor(postInsertChooser);

		addHelpTopicId(insertPanel, getHelpTopicId() + ".insert");

		return insertPanel;
	}

	protected JPanel buildUpdateMethodPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel updatePanel = new JPanel(new GridBagLayout());
		updatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JLabel preUpdateMethodLabel = buildLabel("EVENTS_POLICY_PRE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		updatePanel.add(preUpdateMethodLabel, constraints);
				
		// Create the combo box
		ListChooser preUpdateChooser = buildPreUpdateMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		updatePanel.add(preUpdateChooser, constraints);
		preUpdateMethodLabel.setLabelFor(preUpdateChooser);
		
		JLabel aboutToUpdateMethodLabel = buildLabel("EVENTS_POLICY_ABOUT_TO");
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		updatePanel.add(aboutToUpdateMethodLabel, constraints);
		
		// Create the combo box
		ListChooser aboutToUpdateChooser = buildAboutToUpdateMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		updatePanel.add(aboutToUpdateChooser, constraints);
		aboutToUpdateMethodLabel.setLabelFor(aboutToUpdateChooser);
		
		JLabel postUpdateMethodLabel = buildLabel("EVENTS_POLICY_POST");
		
		constraints.gridx			= 0;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		updatePanel.add(postUpdateMethodLabel, constraints);
		
		// Create the combo box
		ListChooser postUpdateChooser = buildPostUpdateMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		updatePanel.add(postUpdateChooser, constraints);
		postUpdateMethodLabel.setLabelFor(postUpdateChooser);
		
		addHelpTopicId(updatePanel, getHelpTopicId() + ".update");
		
		return updatePanel;
	}

	protected JPanel buildWriteMethodPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel writePanel = new JPanel(new GridBagLayout());
		writePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel preWriteMethodLabel = buildLabel("EVENTS_POLICY_PRE");
		
		constraints.gridx			= 0;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		writePanel.add(preWriteMethodLabel, constraints);
		
		// Create the combo box
		ListChooser preWriteChooser = buildPreWritingMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 5, 5, 0);

		writePanel.add(preWriteChooser, constraints);
		preWriteMethodLabel.setLabelFor(preWriteChooser);
		
		JLabel postWriteMethodLabel = buildLabel("EVENTS_POLICY_POST");
		
		constraints.gridx			= 0;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(0, 0, 5, 0);

		writePanel.add(postWriteMethodLabel, constraints);		
		
		// Create the combo box
		ListChooser postWriteChooser = buildPostWritingMethodListChooser();

		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill			= GridBagConstraints.HORIZONTAL;
		constraints.anchor		= GridBagConstraints.FIRST_LINE_START;
		constraints.insets		= new Insets(0, 5, 5, 0);

		writePanel.add(postWriteChooser, constraints);
		postWriteMethodLabel.setLabelFor(postWriteChooser);

		addHelpTopicId(writePanel, getHelpTopicId() + ".write");
		
		return writePanel;
	}
	
	private PropertyValueModel buildEventsPolicyHolder()
	{
		if (eventsPolicyValueModel == null)
		{
			eventsPolicyValueModel = new PropertyAspectAdapter(getSelectionHolder(),
					MWMappingDescriptor.EVENTS_POLICY_PROPERTY)
			{
				protected Object getValueFromSubject()
				{
					MWDescriptorPolicy policy = ((MWMappingDescriptor) this.subject).getEventsPolicy();
					return policy.isActive() ? policy : null;
				}
			};
		}
		return eventsPolicyValueModel;
	}
	
	private CollectionValueModel buildEventMethodCollectionHolder()
	{
		return new CollectionAspectAdapter(buildEventMethodTypeHolder(),
				MWClass.METHODS_COLLECTION) {
			protected Iterator getValueFromSubject()
			{
				return ((MWClass)subject).allMethods();
			}
		};
	}
	
	private ItemPropertyListValueModelAdapter buildEventMethodPropertyListAdapter()
	{
		return new ItemPropertyListValueModelAdapter(buildEventMethodCollectionHolder(), MWMethod.SIGNATURE_PROPERTY);
	}
	
	private FilteringCollectionValueModel buildEventMethodFilteringCollectionModel()
	{
		return new FilteringCollectionValueModel(buildEventMethodPropertyListAdapter()) {
			protected boolean accept(Object value)
			{
				return ((MWMethod)value).isCandidateDescriptorEventMethod();
			}
		};
	}
	
	private PropertyValueModel buildEventMethodTypeHolder() 
	{
		return new PropertyAspectAdapter(getSelectionHolder(), MWDescriptor.MW_CLASS_PROPERTY) {
			protected Object getValueFromSubject() 
			{
				return ((MWDescriptor)subject).getMWClass();
			}
		};
	}
	
	private JPanel getDeletingMethodsPanel()
	{
		if (deletingMethodsPanel == null)
		{
			deletingMethodsPanel = buildDeleteMethodsPanel();
		}
		return deletingMethodsPanel;
	}
	
	private JPanel getInsertingMethodsPanel()
	{
		if (insertingMethodsPanel == null)
		{
			insertingMethodsPanel = buildInsertMethodsPanel();
		}
		return insertingMethodsPanel;
	}

	private JPanel getPostXMethodsPanel()
	{
		if (postXMethodsPanel == null)
		{
			postXMethodsPanel = buildPostXMethodsPanel();
		}
		return postXMethodsPanel;
	}

	private JPanel getUpdatingMethodsPanel()
	{
		if (updatingMethodsPanel == null)
		{
			updatingMethodsPanel = buildUpdateMethodPanel();
		}
		return updatingMethodsPanel;
	}
	
	private JPanel getWritingMethodsPanel()
	{
		if (writingMethodsPanel == null)
		{
			writingMethodsPanel = buildWriteMethodPanel();
		}
		return writingMethodsPanel;
	}
	
	private JList getMethodTypeSelectionList()
	{
		if (methodTypeSelectionList == null)
		{
			methodTypeSelectionList = buildMethodTypeSelectionJList();
		}
		return methodTypeSelectionList;
	}
	
	private ListChooser buildPostBuildMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
				new ExtendedComboBoxModel(buildPostBuildComboBoxModel()),
                DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPostCloneMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostCloneComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPostMergeMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostMergeComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}

	private ListChooser buildPostRefreshMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostRefreshComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPreDeleteMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPreDeleteComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPostDeleteMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostDeleteComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPreInsertMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPreInsertComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildAboutToInsertMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildAboutToInsertComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPostInsertMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostInsertComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPreUpdateMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPreUpdateComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildAboutToUpdateMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildAboutToUpdateComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPostUpdateMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostUpdateComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPreWritingMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPreWritingComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ListChooser buildPostWritingMethodListChooser()
	{
		ListChooser listChooser = new ListChooser(
			new ExtendedComboBoxModel(buildPostWritingComboBoxModel()),
            DescriptorComponentFactory.buildMethodNodeSelector(getWorkbenchContextHolder()));
		listChooser.setRenderer(new AdaptableListCellRenderer(new MethodCellRendererAdapter(
				resourceRepository())));
		return listChooser;
	}
	
	private ComboBoxModel buildPostBuildComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostBuildMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostCloneComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostCloneMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostMergeComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostMergeMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostRefreshComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostRefreshMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPreDeleteComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPreDeleteMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostDeleteComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostDeleteMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPreInsertComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPreInsertMethodSelectionHolder());
	}
	
	private ComboBoxModel buildAboutToInsertComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildAboutToInsertMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostInsertComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostInsertMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPreUpdateComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPreUpdateMethodSelectionHolder());
	}
	
	private ComboBoxModel buildAboutToUpdateComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildAboutToUpdateMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostUpdateComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostUpdateMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPreWritingComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPreWritingMethodSelectionHolder());
	}
	
	private ComboBoxModel buildPostWritingComboBoxModel()
	{
		return new ComboBoxModelAdapter(
				buildEventMethodFilteringCollectionModel(),
				buildPostWritingMethodSelectionHolder());
	}
	
	private PropertyValueModel buildPostBuildMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_BUILD_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostBuildMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostBuildMethod((MWMethod)value);
			}
		};
	}	
	
	private PropertyValueModel buildPostCloneMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_CLONE_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostCloneMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostCloneMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPostMergeMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_MERGE_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostMergeMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostMergeMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPostRefreshMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_REFRESH_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostRefreshMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostRefreshMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPreDeleteMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.PRE_DELETING_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPreDeletingMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPreDeletingMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPostDeleteMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_DELETING_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostDeletingMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostDeletingMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPreInsertMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.PRE_INSERT_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPreInsertMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPreInsertMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildAboutToInsertMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.ABOUT_TO_INSERT_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getAboutToInsertMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setAboutToInsertMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPostInsertMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_INSERT_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostInsertMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostInsertMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPreUpdateMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.PRE_UPDATE_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPreUpdateMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPreUpdateMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildAboutToUpdateMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.ABOUT_TO_UPDATE_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getAboutToUpdateMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setAboutToUpdateMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPostUpdateMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_UPDATE_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostUpdateMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostUpdateMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPreWritingMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.PRE_WRITING_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPreWritingMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPreWritingMethod((MWMethod)value);
			}
		};
	}
	
	private PropertyValueModel buildPostWritingMethodSelectionHolder()
	{
		return new PropertyAspectAdapter(buildEventsPolicyHolder(),
				MWDescriptorEventsPolicy.POST_WRITING_METHOD_PROPERTY) {
			protected Object getValueFromSubject()
			{
				return ((MWDescriptorEventsPolicy)subject).getPostWritingMethod();
			}

			protected void setValueOnSubject(Object value)
			{
				((MWDescriptorEventsPolicy)subject).setPostWritingMethod((MWMethod)value);
			}
		};
	}
	
	private JList buildMethodTypeSelectionJList()
	{
		ListModelAdapter listModel = new ListModelAdapter(buildMethodTypesModel());
		JList methodTypeList = SwingComponentFactory.buildList(listModel);
		methodTypeList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		methodTypeList.addListSelectionListener(new MethodTypesListDataListener(methodTypeList, buildMethodTypeValueModel()));
		
		return methodTypeList;
	}
	
	private ReadOnlyListValueModel buildMethodTypesModel()
	{
		return new ReadOnlyListValueModel(buildMethodTypeSelectionList());
	}
	
	private List buildMethodTypeSelectionList()
	{
		ArrayList methodTypeList = new ArrayList();
		
		methodTypeList.add(DELETING_METHODS);
		methodTypeList.add(INSERTING_METHODS);
		methodTypeList.add(POST_X_METHODS);
		methodTypeList.add(UPDATING_METHODS);
		methodTypeList.add(WRITING_METHODS);
		
		return methodTypeList;
	}
	
	private SimplePropertyValueModel buildMethodTypeValueModel()
	{
		if (methodTypesSelectionModel == null)
		{
			methodTypesSelectionModel = new SimplePropertyValueModel();
		}
		return methodTypesSelectionModel;
	}
	
	private class MethodTypesListDataListener implements ListSelectionListener
	{
		private JList list;
		private PropertyValueModel selectionModel;
		
		private MethodTypesListDataListener(JList list, PropertyValueModel selectionModel)
		{
			this.selectionModel = selectionModel;
			this.list = list;
		}
		
		public void valueChanged(ListSelectionEvent e)
		{
			if ( ! e.getValueIsAdjusting()) {
				selectionModel.setValue(list.getSelectedValue());
			}
		}
	}
	
	private class MethodTypeTransformer implements Transformer
	{
		
		public Object transform(Object o)
		{
			String selectionChoice = (String)o;
			if (selectionChoice == POST_X_METHODS)
			{
				return getPostXMethodsPanel();
			}
			else if (selectionChoice == DELETING_METHODS)
			{
				return getDeletingMethodsPanel();
			}
			else if (selectionChoice == INSERTING_METHODS)
			{
				return getInsertingMethodsPanel();
			}
			else if (selectionChoice == UPDATING_METHODS)
			{
				return getUpdatingMethodsPanel();
			}
			else if (selectionChoice == WRITING_METHODS)
			{
				return getWritingMethodsPanel();
			}
			else
			{
				return null;
			}
		}
	}
}
