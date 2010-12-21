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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInterfaceAliasDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.relational.TableDescriptorNode;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ReadOnlyListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;


/**
 * @version 10.1.3
 */
public class AdvancedPolicyAction extends AbstractFrameworkAction
{
	
	public AdvancedPolicyAction(WorkbenchContext context)
	{
		super(context);
		initializeRepositoryResources();
	}
	
	private void initializeRepositoryResources()
	{
		initializeTextAndMnemonic("ADVANCED_PROPERTIES_MENU_ITEM");
		initializeToolTipText("ADVANCED_PROPERTIES_MENU_ITEM.tooltip");
		initializeIcon("descriptor.advancedProperties");
	}
	

	protected String getResourceString(String key)
	{
		return resourceRepository().getString(key);
	}
	
	private List buildApplicableAdvancedPolicyHolderList()
	{
		List advancedPolicyList = new ArrayList();
		if (supportsMappingDescriptorPolicies())
		{
			advancedPolicyList.add(new AfterLoadingPolicyHolder());
			advancedPolicyList.add(new CopyingPolicyHolder());
			advancedPolicyList.add(new InheritencePolicyHolder());
			advancedPolicyList.add(new InstantiationPolicyHolder());
		}
		// special case, ALL mapping descriptors support events
		// except for 0-X Descriptors.  Post 10.1.3, this will change.
		if (supportsEventsPolicy())
		{
			advancedPolicyList.add(new EventsPolicyHolder());
		}
		// another special case, only table descriptors and eis root 
		// descriptors support interface alias policy
		if (supportsInterfaceAliasPolicy()) {
			advancedPolicyList.add(new InterfaceAliasPolicyHolder());	
		}
		
		if (supportsTransactionalDescriptorProperties())
		{
			advancedPolicyList.add(new ReturningPolicyHolder());
		}
		if (supportsTableDescriptorProperties())
		{
			advancedPolicyList.add(new MultiTableInfoPolicyHolder());
		}
		return advancedPolicyList;
	}
	
	private boolean supportsEventsPolicy()
	{
		ApplicationNode[] selectedNodes = selectedNodes(); 
		for (int i = 0; i < selectedNodes.length; i++)
		{
			if (!((DescriptorNode) selectedNodes[i]).supportsEventsPolicy())
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean supportsInterfaceAliasPolicy() {
		ApplicationNode[] selectedNodes = selectedNodes();
		for (int i = 0; i < selectedNodes.length; i++) {
			if (!((DescriptorNode) selectedNodes[i]).supportsInterfaceAliasPolicy()) {
				return false;
			}
		}
		return true;
	}
	
	private boolean  supportsMappingDescriptorPolicies()
	{
		return true;
	}
	
	private boolean supportsTransactionalDescriptorProperties()
	{
		ApplicationNode[] selectedNodes = selectedNodes(); 
		for (int i = 0; i < selectedNodes.length; i++)
		{
			if (!((DescriptorNode) selectedNodes[i]).supportsTransactionalDescriptorProperties())
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean supportsTableDescriptorProperties()
	{
		ApplicationNode[] selectedNodes = selectedNodes(); 
		for (int i = 0; i < selectedNodes.length; i++)
		{
			if (!((DescriptorNode) selectedNodes[i]).isTableDescriptor())
			{
				return false;
			}
		}
		return true;
	}

	
	protected void execute()
	{
		List advancedPolicyItemList = buildApplicableAdvancedPolicyHolderList();
		ListValueModel advancedPoliciesListModel = 
							buildAdvancedPolicyListModel(advancedPolicyItemList);
		Iterator selectedItems = buildSelectedItems(advancedPolicyItemList.iterator());
		ObjectListSelectionModel selectionModel = buildSelectionModel(advancedPoliciesListModel, selectedItems);
		
		AdvancedPolicyChooser chooser = new AdvancedPolicyChooser(getWorkbenchContext(), advancedPoliciesListModel, selectionModel, buildAdvancedPropertiesCellRenderer());
		chooser.setVisible(true);
		
		if (chooser.wasCanceled())
		{
			return;
		}
		
		List selectedValues = Arrays.asList(selectionModel.getSelectedValues());
		for (Iterator selectedValueIterator = selectedValues.iterator(); selectedValueIterator.hasNext();)
		{
			AdvancedPolicyHolder checkListHolder = (AdvancedPolicyHolder)selectedValueIterator.next();
			if (!checkListHolder.initialSelectionState())
			{
				checkListHolder.addToSelectedNodes();
			}
		}
		
		for (Iterator allValues = advancedPolicyItemList.iterator(); allValues.hasNext();)
		{
			AdvancedPolicyHolder checkListHolder = (AdvancedPolicyHolder)allValues.next();
			if (!selectedValues.contains(checkListHolder)
					&& checkListHolder.initialSelectionState())
			{
				checkListHolder.removeFromSelectedNodes();
			}
		}
		
	}
	
	

	private ObjectListSelectionModel buildSelectionModel(ListValueModel itemHolder, Iterator selectedItems)
	{
		ListModelAdapter adapter = new ListModelAdapter(itemHolder);
		adapter.addListDataListener(new HackableListDataListener());
		ObjectListSelectionModel model =  new ObjectListSelectionModel(adapter);
		model.setSelectedValues(selectedItems);
		
		return model;
	}
	
	private Iterator buildSelectedItems(Iterator allItems)
	{
		Filter iteratorFilter = new Filter() 
		{
			public boolean accept(Object o)
			{
				return ((AdvancedPolicyHolder)o).initialSelectionState();
			}
		};
		return new FilteringIterator(allItems, iteratorFilter);
	}
	
	protected ListValueModel buildAdvancedPolicyListModel(List itemList)
	{
		return new SortedListValueModelAdapter(new ReadOnlyListValueModel(itemList), buildAdvancedPolicyComparator());
	}
	
	private CellRendererAdapter buildAdvancedPropertiesCellRenderer()
	{
		return new AbstractCellRendererAdapter()
		{
			public String buildText(Object value)
			{
				return ((AdvancedPolicyHolder) value).displayString();
			}
		};
	}
	private Comparator buildAdvancedPolicyComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				return Collator.getInstance().compare(((AdvancedPolicyHolder) o1).displayString(), ((AdvancedPolicyHolder) o2).displayString());
			}
		};
	}
	
	protected interface AdvancedPolicyHolder
	{
		
		public void addToSelectedNodes();
		
		public void removeFromSelectedNodes();
		
		public boolean initialSelectionState();
		
		public String displayString();
	}
	
	//TODO Hackable???  This needs to be removed
	private class HackableListDataListener implements ListDataListener
	{
		
		public void contentsChanged(ListDataEvent e)
		{
		}
		public void intervalAdded(ListDataEvent e)
		{
		}
		public void intervalRemoved(ListDataEvent e)
		{
		}
	}
	
	
	private class CopyingPolicyHolder implements AdvancedPolicyHolder
	{
		public void addToSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (!mappingDescriptor.getCopyPolicy().isActive())
				{
					mappingDescriptor.addCopyPolicy();
				}
				
			}			
		}
		
		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (mappingDescriptor.getCopyPolicy().isActive())
				{
					mappingDescriptor.removeCopyPolicy();
				}
				
			}
		}

		public boolean initialSelectionState()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			boolean isSelected = true;
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				isSelected &= mappingDescriptor.getCopyPolicy().isActive();
				
			}
			
			return isSelected;
		}
		
		public String displayString()
		{
			return getResourceString("COPY_POLICY_MAPPING_DESCRIPTOR_ACTION");
		}
	}
		
	private class AfterLoadingPolicyHolder implements AdvancedPolicyHolder
	{
		public void addToSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (!mappingDescriptor.getAfterLoadingPolicy().isActive())
				{
					mappingDescriptor.addAfterLoadingPolicy();
				}
				
			}			
		}
		
		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (mappingDescriptor.getAfterLoadingPolicy().isActive())
				{
					mappingDescriptor.removeAfterLoadingPolicy();
				}
				
			}
		}

		public boolean initialSelectionState()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			boolean isSelected = true;
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				isSelected &= mappingDescriptor.getAfterLoadingPolicy().isActive();
				
			}
			
			return isSelected;
		}
		
		public String displayString()
		{
			return getResourceString("AFTER_LOAD_POLICY_MAPPING_DESCRIPTOR_ACTION");
		}
	}
	
	private class InheritencePolicyHolder implements AdvancedPolicyHolder
	{
		public void addToSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (!mappingDescriptor.getInheritancePolicy().isActive())
				{
					mappingDescriptor.addInheritancePolicy();
				}
				
			}			
		}
		
		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (mappingDescriptor.getInheritancePolicy().isActive())
				{
					mappingDescriptor.removeInheritancePolicy();
				}
				
			}
		}

		public boolean initialSelectionState()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			boolean isSelected = true;
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				isSelected &= mappingDescriptor.getInheritancePolicy().isActive();
				
			}
			
			return isSelected;
		}
		
		public String displayString()
		{
			return getResourceString("INHERITANCE_POLICY_MAPPING_DESCRIPTOR_ACTION");
		}
	}
	
	private class InstantiationPolicyHolder implements AdvancedPolicyHolder
	{
		public void addToSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (!mappingDescriptor.getInstantiationPolicy().isActive())
				{
					mappingDescriptor.addInstantiationPolicy();
				}
				
			}			
		}
		
		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (mappingDescriptor.getInstantiationPolicy().isActive())
				{
					mappingDescriptor.removeInstantiationPolicy();
				}
				
			}
		}

		public boolean initialSelectionState()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			boolean isSelected = true;
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				isSelected &= mappingDescriptor.getInstantiationPolicy().isActive();
				
			}
			
			return isSelected;
		}
		
		public String displayString()
		{
			return getResourceString("INSTANTIATION_POLICY_MAPPING_DESCRIPTOR_ACTION");
		}
	}
	
	public class EventsPolicyHolder implements AdvancedPolicyHolder
	{
		public void addToSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (!mappingDescriptor.getEventsPolicy().isActive())
				{
					mappingDescriptor.addEventsPolicy();
				}
				
			}			
		}
		
		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				if (mappingDescriptor.getEventsPolicy().isActive())
				{
					mappingDescriptor.removeEventsPolicy();
				}
				
			}
		}

		public boolean initialSelectionState()
		{
			ApplicationNode[] selectedNodes = selectedNodes();
			boolean isSelected = true;
			for (int i = 0; i < selectedNodes.length; i++)
			{
				MappingDescriptorNode descNode = (MappingDescriptorNode)selectedNodes[i];
				MWMappingDescriptor mappingDescriptor = descNode.getMappingDescriptor();
				isSelected &= mappingDescriptor.getEventsPolicy().isActive();
				
			}
			
			return isSelected;
		}
		
		public String displayString()
		{
			return getResourceString("EVENTS_POLICY_MAPPING_DESCRIPTOR_ACTION");
		}
	}

	private class InterfaceAliasPolicyHolder implements AdvancedPolicyHolder
	{
		
		public void addToSelectedNodes()
		{
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode )selection[i];
				MWInterfaceAliasDescriptor desc = (MWInterfaceAliasDescriptor)node.getDescriptor(); 
				if (!desc.getInterfaceAliasPolicy().isActive())
				{
					desc.addInterfaceAliasPolicy();
				}
			}
		}

		public boolean initialSelectionState()
		{
			boolean isSelected = true;
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode)selection[i];
				isSelected &= ((MWInterfaceAliasDescriptor)node.getDescriptor()).getInterfaceAliasPolicy().isActive();
			}
			return isSelected;
		}

		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode )selection[i];
				MWInterfaceAliasDescriptor desc = (MWInterfaceAliasDescriptor)node.getDescriptor(); 
				if (desc.getInterfaceAliasPolicy().isActive())
				{
					desc.removeInterfaceAliasPolicy();
				}
			}
		}
		
		public String displayString()
		{
			return getResourceString("INTERFACE_ALIAS_ACTION");
		}
	}

	private class MultiTableInfoPolicyHolder implements AdvancedPolicyHolder
	{
		
		public void addToSelectedNodes()
		{
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode )selection[i];
				MWTableDescriptor desc = (MWTableDescriptor)node.getDescriptor(); 
				if (!desc.getMultiTableInfoPolicy().isActive())
				{
					desc.addMultiTableInfoPolicy();
				}
			}
		}

		public boolean initialSelectionState()
		{
			boolean isSelected = true;
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				TableDescriptorNode node = (TableDescriptorNode )selection[i];
				isSelected &= ((MWTableDescriptor)node.getDescriptor()).getMultiTableInfoPolicy().isActive();
			}
			return isSelected;
		}

		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode )selection[i];
				MWTableDescriptor desc = (MWTableDescriptor)node.getDescriptor(); 
				if (desc.getMultiTableInfoPolicy().isActive())
				{
					desc.removeMultiTableInfoPolicy();
				}
			}
		}
		
		public String displayString()
		{
			return getResourceString("MULTI_TABLE_INFO_ACTION");
		}
	}

	private class ReturningPolicyHolder implements AdvancedPolicyHolder
	{
			
		public void addToSelectedNodes()
		{
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode) selection[i];
				MWTransactionalDescriptor desc = (MWTransactionalDescriptor) node.getDescriptor();
				if (!desc.getReturningPolicy().isActive())
				{
					
					JOptionPane.showMessageDialog(
							getWorkbenchContext().getCurrentWindow(), 
							getResourceString("NATIVE_RETURNING_NOT_SUPPORTED_ON_PLATFORM"), 
							getResourceString("NATIVE_RETURNING_NOT_SUPPORTED_ON_PLATFORM_TITLE"),
							JOptionPane.INFORMATION_MESSAGE);
			
					desc.addReturningPolicy();
				}
			}
		}
		public boolean initialSelectionState()
		{
			boolean isSelected = true;
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode) selection[i];
				isSelected &= ((MWTransactionalDescriptor) node.getDescriptor()).getReturningPolicy().isActive();
			}
			return isSelected;
		}

		public void removeFromSelectedNodes()
		{
			ApplicationNode[] selection = selectedNodes();
			for (int i = 0; i < selection.length; i++)
			{
				MappingDescriptorNode node = (MappingDescriptorNode )selection[i];
				MWTransactionalDescriptor desc = (MWTransactionalDescriptor) node.getDescriptor(); 
				if (desc.getReturningPolicy().isActive())
				{
					desc.removeReturningPolicy();
				}
			}
		}
		
		public String displayString()
		{
			return getResourceString("RETURNING_ACTION");
		}
	}
}


