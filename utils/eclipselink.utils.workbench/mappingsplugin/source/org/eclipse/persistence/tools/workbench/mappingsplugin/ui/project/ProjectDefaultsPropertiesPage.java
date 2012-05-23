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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.ScrollablePropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.CheckList;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


class ProjectDefaultsPropertiesPage extends ScrollablePropertiesPage 
{
	private ObjectListSelectionModel advancedPoliciesSelectionModel;
	private List policyHolders;	
	
	ProjectDefaultsPropertiesPage(PropertyValueModel projectNodeHolder, WorkbenchContextHolder contextHolder) 
	{
		super(projectNodeHolder, contextHolder);
	}
	
	private Comparator buildAdvancedPolicyComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return Collator.getInstance().compare(((UIAdvancedPolicyHolder) o1).displayString(), ((UIAdvancedPolicyHolder) o2).displayString());
			}
		};
	}
	
	private Component buildAdvancedPolicyDefaultsPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel advancedPolicyDefaultsPanel = new JPanel(new GridBagLayout());
		advancedPolicyDefaultsPanel.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("PROJECT_DEFAULTS_POLICY_DESCRIPTOR_ADVANCED_PROPERTIES"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5))
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		advancedPolicyDefaultsPanel.add(buildAdvancedPropertiesDefaultsChooser(), constraints);
		
		addHelpTopicId(advancedPolicyDefaultsPanel, helpTopicId() + ".descriptorAdvancedProperties");
		
		return advancedPolicyDefaultsPanel;
	}

	private CellRendererAdapter buildAdvancedPropertiesCellRenderer()
	{
		return new AbstractCellRendererAdapter()
		{
			public String buildText(Object value)
			{
				return ((UIAdvancedPolicyHolder) value).displayString();
			}
		};
	}
	
	private ListValueModel buildAdvancedPropertiesCollectionModel(List allValues)
	{
		return new SortedListValueModelAdapter(new ReadOnlyListValueModel(allValues), buildAdvancedPolicyComparator());
	}

	protected Component buildAdvancedPropertiesDefaultsChooser()
	{
		this.policyHolders = buildAdvancedPropertyHolders();
		ListValueModel advancedPropertiesCVM = buildAdvancedPropertiesCollectionModel(this.policyHolders); 
				
		this.advancedPoliciesSelectionModel =  new ObjectListSelectionModel(new ListModelAdapter(advancedPropertiesCVM));
						
		CheckList checkList = new CheckList(advancedPropertiesCVM, this.advancedPoliciesSelectionModel, buildAdvancedPropertiesCellRenderer());
		JPanel advancedPropertiesPanel = new JPanel(new BorderLayout());
		advancedPropertiesPanel.setBorder(buildStandardEmptyBorder());
		advancedPropertiesPanel.add(checkList);
		getSelectionHolder().addPropertyChangeListener(PropertyValueModel.VALUE, new ProjectValueChangeListener());
		
		this.advancedPoliciesSelectionModel.addListSelectionListener(new AdvancedPolicyDataListener(this.policyHolders, this.advancedPoliciesSelectionModel));
		
		// since the node holder is sometimes populated with its value during initialization,
		// we need to force a populate on the selection model here.
		if (selection() != null)
		{
			this.advancedPoliciesSelectionModel.setSelectedValues(holderIterator(
					((MWProject)selection()).getDefaultsPolicy().defaultPolicies()));
		}
		
		return advancedPropertiesPanel;
	}
	
	protected List buildAdvancedPropertyHolders()
	{
		ArrayList propertyHolders = new ArrayList();
		
		propertyHolders.add(new UIAdvancedPolicyHolder(MWProjectDefaultsPolicy.AFTER_LOAD_POLICY, "PROJECT_DEFAULTS_POLICY_AFTER_LOADING_POLICY"));
		propertyHolders.add(new UIAdvancedPolicyHolder(MWProjectDefaultsPolicy.COPY_POLICY, "PROJECT_DEFAULTS_POLICY_COPYING_POLICY"));
		propertyHolders.add(new UIAdvancedPolicyHolder(MWProjectDefaultsPolicy.INHERITANCE_POLICY, "PROJECT_DEFAULTS_POLICY_INHERITANCE_POLICY"));
		propertyHolders.add(new UIAdvancedPolicyHolder(MWProjectDefaultsPolicy.INSTANTIATION_POLICY, "PROJECT_DEFAULTS_POLICY_INSTANTIATION_POLICY"));
		
		return propertyHolders;
	}
	
	protected Component buildCachingDefaultsPanel()
	{
		return new JPanel();
	}
	
	private Component buildCreationDefaultsPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel creationDefaultsPanel = new JPanel(new GridBagLayout());
		creationDefaultsPanel.setBorder(BorderFactory.createCompoundBorder
		(
			buildTitledBorder("PROJECT_DEFAULTS_POLICY_CREATION"),
			BorderFactory.createEmptyBorder(0, 5, 5, 5))
		);

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 2;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);

		creationDefaultsPanel.add(buildAdvancedPolicyDefaultsPanel(), constraints);

		constraints.gridx      = 1;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		creationDefaultsPanel.add(buildFieldAccessingPanel(), constraints);

		constraints.gridx      = 1;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 5, 0, 0);

		creationDefaultsPanel.add(buildNamedQueriesPanel(), constraints);

		return creationDefaultsPanel;
	}
	
	private Component buildFieldAccessingPanel()
	{
		FieldAccessingPanel fieldAccessingPanel = new FieldAccessingPanel(getSelectionHolder(), getApplicationContext());
		addHelpTopicId(fieldAccessingPanel, helpTopicId() + ".fieldAccessing");
		
		return fieldAccessingPanel;
	}

	protected JPanel buildNamedQueriesPanel()
	{
		return new JPanel();
	}

	protected Component buildPage() 
	{
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(0, 0, 0, 0);
		panel.add(buildCreationDefaultsPanel() , constraints);

		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.HORIZONTAL;
		constraints.anchor     = GridBagConstraints.PAGE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		panel.add(buildCachingDefaultsPanel(), constraints);

		addHelpTopicId(panel, helpTopicId());
		return panel;
	}

	protected PropertyValueModel buildSelectionHolder()
	{
		return new TransformationPropertyValueModel(super.buildSelectionHolder())
		{
			protected Object transform(Object value)
			{
				if (value == null)
					return null;

				return ((MWProject) value).getDefaultsPolicy();
			}
		};
	}
	
	private UIAdvancedPolicyHolder getPolicyHolderFor(String policyId)
	{
		for (Iterator iter = this.policyHolders.iterator(); iter.hasNext();)
		{
			UIAdvancedPolicyHolder holder = (UIAdvancedPolicyHolder) iter.next();

			if (holder.isOfType(policyId))
			{
				return holder;
			}
		}

		return null;
	}

	protected String helpTopicId()
	{
		return "project.defaults";
	}
	
	private Iterator holderIterator(Iterator policyIdIter)
	{
		return new TransformationIterator(policyIdIter) 
		{
			protected Object transform(Object next)
			{
				return getPolicyHolderFor((String)next);
			}
		};
	}
	
	protected MWProjectDefaultsPolicy projectDefaults()
	{
		return (MWProjectDefaultsPolicy) selection();
	}
	
	private class AdvancedPolicyDataListener implements ListSelectionListener
	{
		private List allPolicyHolders;
		private ObjectListSelectionModel selectionModel;
		
		private AdvancedPolicyDataListener(List allPolicyHolders, ObjectListSelectionModel selectionModel)
		{
			this.allPolicyHolders = allPolicyHolders;
			this.selectionModel = selectionModel;
		}
		
		public void valueChanged(ListSelectionEvent e)
		{
			if (e.getValueIsAdjusting())
			{
				return;
			}
			Object[] selectedValues = this.selectionModel.getSelectedValues();
			List valuesToRemove = new ArrayList(this.allPolicyHolders);
			CollectionTools.removeAll(valuesToRemove, selectedValues);
			// add selected policies
			for (int i = 0; i < selectedValues.length; i++)
			{
				UIAdvancedPolicyHolder holder = (UIAdvancedPolicyHolder)selectedValues[i];
				holder.addPolicyDefault();
			}
			// remove unselected policies
			for (Iterator iter = valuesToRemove.iterator(); iter.hasNext();)
			{
				UIAdvancedPolicyHolder holder = (UIAdvancedPolicyHolder) iter.next();
				holder.removePolicyDefault();
			}
		}
	}
	
	/**
	 * Class acts as a listener to set the selected values based upon the node selection. 
	 */
	private class ProjectValueChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			if (selection() != null)
			{
				MWProject project = projectDefaults().getProject();
				Iterator selectedPolicies = project.getDefaultsPolicy().defaultPolicies();
				advancedPoliciesSelectionModel.setSelectedValues(holderIterator(selectedPolicies));
			}
		}
	}
	
	public class UIAdvancedPolicyHolder 
	{
		private String policyId;
		private String resourceKey;
		
		public UIAdvancedPolicyHolder(String advancedPolicyId, String resourceKey)
		{
			this.policyId = advancedPolicyId;
			this.resourceKey = resourceKey;
		}
		
		public void addPolicyDefault()
		{
			if (!projectDefaults().containsAdvancePolicyDefault(this.policyId))
			{
				projectDefaults().addAdvancedPolicyDefault(this.policyId);
			}
		}

		public String displayString()
		{
			try
			{
				return resourceRepository().getString(this.resourceKey);
			}
			catch (NullPointerException e)
			{
				// Ignore, this only happens when JAWS is running
				// and the Project page is released
				return this.resourceKey;
			}
		}
		
		public boolean isOfType(String policyId)
		{
			return this.policyId == policyId;
		}

		public void removePolicyDefault()
		{
			if (projectDefaults().containsAdvancePolicyDefault(this.policyId))
			{
				projectDefaults().removeAdvancedPolicyDefault(this.policyId);
			}
		}
		
		public String toString()
		{
			return StringTools.buildToStringFor(this);
		}
		
	}
}
