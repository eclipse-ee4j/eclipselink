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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModelWrapper;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * If an ApplicationNode can have more than one PropertiesPage, use this class.
 * It provides support for adding and removing PropertiesPages (or other components)
 * to/from a tabbed pane. Typically the tab pages will be subclasses of
 * ScrollablePropertiesPage.
 * 
 * This tabbed properties page supports two types of PropertiesPages:
 * 
 * static - instantiated and added at the time of subclass initialization.
 * dynamic - created and added by a ComponentBuilder based on changes indicated
 * by a provided ValueModel.
 * 
 * Concurrent use of static and dynamic tabs is supported.
 * 
 * In order to determine the ordering of tabs in the PropertiesPage, a weight-based
 * system has been employed.  Valid weight values are 0..Integer.MAX_VALUE.  0 is the
 * great weight (priority).  Generally, the given weight of a page should correspond to 
 * the desired index of the tab (from left to right) when all static and dynamic tabs are present in the PropertiesPage.
 * In the case that mutiple tabs of the same weight exist, they will appear in the order
 * that they were added.  Because of this, the weights can be tweeked to account for 
 * PropertyPage inheritance allowing the correct tab ordering in the case that tabs are
 * added in varying orders and appear dynamically.  The default for a tab added without
 * weight is 0 (highest weight).     
 *  
 * If your ApplicationNode will only have one PropertiesPage use TitledPropertiesPage.
 * 
 * @see ScrollablePropertiesPage
 * @see TitledPropertiesPage
 */
public abstract class TabbedPropertiesPage
	extends AbstractPropertiesPage
{
	JTabbedPane tabbedPane;
	private List componentTabWeightHolders;
	private List dynamicTabHandlers;
	
	private Component previouslySelectedComponent;

	protected static final int DEFAULT_WEIGHT = 0;
	
	protected TabbedPropertiesPage(WorkbenchContext context) {
		super(context);
	}

	protected TabbedPropertiesPage(PropertyValueModel nodeHolder, WorkbenchContextHolder contextHolder) {
		super(nodeHolder, contextHolder);
	}

	protected void initializeLayout() {
		this.add(this.buildTitlePanel(), BorderLayout.PAGE_START);

		this.tabbedPane = this.buildTabbedPane();
		this.add(this.tabbedPane, BorderLayout.CENTER);
		
		this.initializeTabs();	
	}
	
	protected JTabbedPane buildTabbedPane() {
		JTabbedPane result = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		result.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));		
	
		return result;
	}

	/**
	 * Subclasses should implement this method and call the various
	 * #addTab() methods to add the properties pages that should
	 * appear when the node is selected. These tabs can be specified
	 * as static (created and added at initialization) or as dynamic
	 * (created and added dynamically based on model changes). 
	 * 
	 */
	protected abstract void initializeTabs();
	

	protected int tabCount() {
		return this.tabbedPane.getTabCount();
	}

	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.componentTabWeightHolders = new ArrayList();
		this.dynamicTabHandlers = new ArrayList();
	}

	/**
	 * Adds a new tab for the given <code>Component</code> with specified <code>tabWeight</code>.
	 */
	protected void addTab(Component component, int tabWeight, String tabTitleKey) {
		this.addTab(this.buildDefaultComponentBuilder(component), tabWeight, tabTitleKey);
	}
	
	/**
	 * Adds a new tab for the given <code>Component</code> with <code>DEFAULT_WEIGHT</code> set as 
	 * the tab weight.
	 */
	protected void addTab(Component component, String tabTitleKey) {
		this.addTab(component, DEFAULT_WEIGHT, tabTitleKey);
	}
	
	/**
	 * Adds a new tab for the <code>Component</code> with <code>tabWeight</code> set as 
	 * the tab weight.  The tab created for the component is then set as selected.
	 */
	protected void addTabAndSelect(Component component, int tabWeight, String tabTitleKey) {
		this.addTab(component, tabWeight, tabTitleKey);
		this.tabbedPane.setSelectedComponent(component);
	}

	/**
	 * Adds a new tab for the <code>Component</code> with <code>DEFAULT_WEIGHT</code> set as 
	 * the tab weight.  The tab created for the component is then set as selected.
	 */
	protected void addTabAndSelect(Component component, String tabTitleKey) {
		this.addTabAndSelect(component, DEFAULT_WEIGHT, tabTitleKey);
	}
	
	/**
	 * Adds a new tab, using the <code>ComponentBuilder</code> to create the new <code>Component</code> using <code>tabWeight</code> as 
	 * the tab weight. The given <code>ValueModel</code> should return a <code>Boolean</code> value to determine if the tab should be created
	 * based upon the state of the underlying model.
	 */
	protected void addTab(ValueModel enabledValueModel, int tabWeight, ComponentBuilder pageBuilder, String tabTitleKey) {
		DynamicTabHandler handler = new DynamicTabHandler(pageBuilder, tabWeight, tabTitleKey, enabledValueModel); 
		handler.engage();
		handler.nodeSet(getNode());
		this.dynamicTabHandlers.add(handler);
	}
	
	/**
	 * Adds a new tab for the given <code>Component</code> with specified <code>tabWeight</code>.
	 * The given <code>ValueModel</code> should return a <code>Boolean</code> value to determine if the tab should be displayed
	 * based upon the state of the underlying model.
	 */
	protected void addTab(ValueModel enabledValueModel, int tabWeight, Component component, String tabTitleKey) {
		this.addTab(enabledValueModel, tabWeight, this.buildDefaultComponentBuilder(component), tabTitleKey);
	}
	
	/**
	 * Adds a new tab, using the <code>ComponentBuilder</code> to create the new <code>Component</code> using <code>tabWeight</code> as 
	 * the tab weight.  The tab created for the component is then set as selected.
	 */
	protected void addTab(ComponentBuilder pageBuilder, int tabWeight, String tabTitleKey) {
		Component component = pageBuilder.buildComponent(getNode());
		this.insertTab(component, tabWeight, tabTitleKey);
	}
	
	/**
	 * Sets the tab represented by the given <code>Component</code> as selected.
	 */
	protected void setSelectedTab(Component component) {
		this.tabbedPane.setSelectedComponent(component);
	}
	
	/**
	 * WARNING: be very carful using this method.  In the case where the tab
	 * editor specificied by this component is static, only the Component will
	 * be removed from the tab.  However, if the specified Component belongs to
	 * a dynamic tab editor, the enabling mode, all listeners, etc will be completely
	 * removed for the Component.  This is necessary as the TabbedPropertiesPage
	 * would be left in an inconsistent state otherwise. 
	 */
	protected void destroyTab(Component component) {		
		this.removeTab(component);
		// quick sanity check.  since it is possible that 
		// dynamic tabs have not been created yet, we don't
		// want to remove the first one we encounter.
		if (component == null) return;
		for (Iterator handlers = this.dynamicTabHandlers.iterator(); handlers.hasNext(); ) {
			DynamicTabHandler handler = (DynamicTabHandler)handlers.next();
			if (handler.getComponent() == component) {
				handler.disengage();
				this.dynamicTabHandlers.remove(handler);
				return;
			}
		}
	}
	
	/**
	 * Necessary override.  When the node is changed, the enabledStateModel given with a dynamic tab editor,
	 * should handle adding and removing the tab when the is engaged by the contained selectionHolder.  However,
	 * when the properties page has it's applicationNode set to null, properties in the model can change without the
	 * tabbed properties page's knowledge since it has been disengaged. Upon engaging with the same model object,
	 * or a different one, the dynamic tabs need to be added or removed based on the new model node's state.
	 */
	public void setNode(ApplicationNode node, WorkbenchContext context) {
		if (node == null) {
			this.previouslySelectedComponent = this.tabbedPane.getSelectedComponent();
		}

		super.setNode(node, context);
		
		for (Iterator handlers = this.dynamicTabHandlers.iterator(); handlers.hasNext(); ) {
			DynamicTabHandler handler = (DynamicTabHandler) handlers.next();
			// let the handler know that a new node has been set, and adjust accordingly...
			handler.nodeSet(node);
		}
		if (node != null) {
			if (CollectionTools.contains(this.tabbedPane.getComponents(), this.previouslySelectedComponent)) {
				this.setSelectedTab(this.previouslySelectedComponent);
			} else {
				this.setSelectedTab(this.tabbedPane.getComponentAt(0));
			}
		}
	}
	
	void insertTab(Component component, int pageWeight, String tabTitleKey) {
		ComponentTabWeightHolder tabWeightHolder = new ComponentTabWeightHolder(component, pageWeight);
		int pageIndex = this.insertTabWeightHolder(tabWeightHolder);
		this.tabbedPane.insertTab(this.resourceRepository().getString(tabTitleKey), null, component, null, pageIndex);
	}
	
	private int insertTabWeightHolder(ComponentTabWeightHolder tabWeightHolder) {
		// assume insertion index is the end of the list unless otherwise specified.
		int insertionIndex = this.componentTabWeightHolders.size();
		for (int i = 0; i < this.componentTabWeightHolders.size(); i++) {
			ComponentTabWeightHolder currentHolder = (ComponentTabWeightHolder) this.componentTabWeightHolders.get(i);
			if (currentHolder.getPageWeight() > tabWeightHolder.getPageWeight()) {
				insertionIndex = i;
				break;
			}
		}
		this.componentTabWeightHolders.add(insertionIndex, tabWeightHolder);
		return insertionIndex;
	}
	
	/**
	 * @see <code>destroyTab</code> for external removal
	 */
	void removeTab(Component component) {		
		this.tabbedPane.remove(component);	
		this.removeTabWeightHolderFor(component);
	}
	
	private void removeTabWeightHolderFor(Component component) {
		ComponentTabWeightHolder holderToRemove = null;
		for (Iterator stream = this.componentTabWeightHolders.iterator(); stream.hasNext(); ) {
			ComponentTabWeightHolder holder = (ComponentTabWeightHolder) stream.next();
			if (holder.getComponent() == component) {
				holderToRemove = holder;
				// Since we are sorting here and this code is called quite 
				// frequently by the UI, it is reasonable to use a break here
				// for performance.
				break;
			}
		}
		if (holderToRemove != null) {
			this.componentTabWeightHolders.remove(holderToRemove);
		} else {
			throw new RuntimeException("DEBUG: Could not find the correct holder for tab.");
		}
	}
	
	private ComponentBuilder buildDefaultComponentBuilder(Component component) {
		return new DefaultComponentBuilder(component); 
	}

	/**
	 * Registered with the Dynamic tab's ValueModel, this class handles adding and
	 * removing the associated tab editor based upon the existence of the property
	 * in the descriptor model.
	 */
	private class DynamicTabHandler implements PropertyChangeListener {
		// need to hold on to the Component since it is not
		// gauranteed that the ComponentBuilder will return
		// the same instance every time....
		private Component component;
		private String tabTitleKey;
		private int tabWeight;
		private ComponentBuilder componentBuilder;
		private boolean hasTabBeenAdded;
		private ValueModel enabledStateModel;

		private DynamicTabHandler(ComponentBuilder componentBuilder, int tabWeight, String tabTitleKey, ValueModel enabledStateModel) {
			this.componentBuilder = componentBuilder;
			this.tabTitleKey = tabTitleKey;
			this.tabWeight = tabWeight;
			this.enabledStateModel = enabledStateModel;
		}

		protected void engage() {
			this.enabledStateModel.addPropertyChangeListener(ValueModel.VALUE, this);
		}

		protected void disengage() {
			this.enabledStateModel.removePropertyChangeListener(ValueModel.VALUE, this);
		}

		public void propertyChange(PropertyChangeEvent event) {
			Boolean properyEnabled = (Boolean) event.getNewValue();

			// if the property is enabled
			if (properyEnabled.booleanValue()) {
				this.addPropertiesPage();
			} else {
				this.removePropertiesPage();
			}
		}

		protected void nodeSet(ApplicationNode applicationNode) {
			if (applicationNode != null) {
				boolean isPropertyEnabled = ((Boolean) this.enabledStateModel.getValue()).booleanValue();

				if (isPropertyEnabled && ! this.hasTabBeenAdded) {
					this.addPropertiesPage();
				} else if (!isPropertyEnabled && this.hasTabBeenAdded) {
					this.removePropertiesPage();
				}
			}
		}

		private void addPropertiesPage() {
			this.component = buildComponent();
			TabbedPropertiesPage.this.insertTab(this.component, this.tabWeight, this.tabTitleKey);
			TabbedPropertiesPage.this.setSelectedTab(this.component);
			this.hasTabBeenAdded = true;
		}

		private void removePropertiesPage() {
			int componentIndex = TabbedPropertiesPage.this.tabbedPane.indexOfComponent(this.component);
			int selectedIndex = TabbedPropertiesPage.this.tabbedPane.getSelectedIndex();

			TabbedPropertiesPage.this.removeTab(this.component);

			if (componentIndex < selectedIndex) {
				TabbedPropertiesPage.this.tabbedPane.setSelectedIndex(selectedIndex - 1);
			}
			this.component = null;
			this.hasTabBeenAdded = false;
		}

		private Component buildComponent() {
			DynamicTabNodeHolder holder = new DynamicTabNodeHolder(getNodeHolder(), this.enabledStateModel);
			return this.componentBuilder.buildComponent(holder);
		}

		protected Component getComponent() {
			return this.component;
		}
	}
	
	/**
	 * This class represents a value pairing of the specified tab's Component with
	 * the associated weight. This class is used in tracking the location of an
	 * existing tab as well as providing a way to measure its positioning weight
	 * in the system.
	 */
	private class ComponentTabWeightHolder {
		private Component component;
		private int pageWeight;
		
		private ComponentTabWeightHolder(Component component, int pageWeight) {
			this.pageWeight = pageWeight;
			this.component = component;
		}
		
		int getPageWeight() {
			return this.pageWeight;
		}
		
		Component getComponent() {
			return this.component;
		}

	}
	
	/**
	 * This class is used by implementor of this class may want to pass a concrete instance
	 * of a Component in the case where the Component's tab is statically placed.
	 * In this case, the desired behavior is a ComponentBuilder that caches and returns
	 * the specified instance.
	 */
	private class DefaultComponentBuilder implements ComponentBuilder {
		private Component component;
		
		private DefaultComponentBuilder(Component component) {
			this.component = component;
		}

		public Component buildComponent(PropertyValueModel nodeHolder) {
			return this.component;
		}
	}
	
	/**
	 * Defines a proxy ApplicationNode PVM holder for all a dynamic properties page.  This
	 * class requires the original node holder from the parent TabbedPropertiesPage as well
	 * as a ValueModel that returns a Boolean describing the whether the properties page should
	 * be shown.  When the Boolean ValueModel returns false, this node holder acts as if the
	 * node is null.  Otherwise, all events and value are propogated from the underlying node holder. 
	 */
	private class DynamicTabNodeHolder extends PropertyValueModelWrapper {
		private ValueModel enabledStateModel;
		/** This listens to the enabled state model holder. */
		protected PropertyChangeListener enabledStateChangeListener;

		private DynamicTabNodeHolder(PropertyValueModel nodeHolder, ValueModel enabledStateModel) {
			super(nodeHolder);
			this.enabledStateModel = enabledStateModel;
		}
		
		protected void initialize() {
			super.initialize();
			this.enabledStateChangeListener = this.buildEnabledStateChangeListener();
		}
		
		protected PropertyChangeListener buildEnabledStateChangeListener() {
			return new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					enabledStateModelChanged(e);
				}
			};
		}

		public Object getValue() {
			return (this.enabledStateModelValue()) ? this.valueHolder.getValue() : null;
		}

		public void setValue(Object value) {
			// do nothing for now..
		}

		protected void engageValueHolder() {
			super.engageValueHolder();
			this.enabledStateModel.addPropertyChangeListener(VALUE, this.enabledStateChangeListener);
		}

		protected void disengageValueHolder() {
			this.enabledStateModel.removePropertyChangeListener(VALUE, this.enabledStateChangeListener);
			super.disengageValueHolder();
		}

		protected void valueChanged(PropertyChangeEvent e) {
			if (this.enabledStateModelValue()){
				this.firePropertyChanged(VALUE, e.getOldValue(), e.getNewValue());
			}
		}

		protected void enabledStateModelChanged(PropertyChangeEvent e) {
			if (this.enabledStateModelValue()) {
				this.firePropertyChanged(VALUE, null, this.valueHolder.getValue());
			} else {
				this.firePropertyChanged(VALUE, this.valueHolder.getValue(), null);
			}
		}
		
		private boolean enabledStateModelValue() {
			return ((Boolean) this.enabledStateModel.getValue()).booleanValue();
		}

	}

}
