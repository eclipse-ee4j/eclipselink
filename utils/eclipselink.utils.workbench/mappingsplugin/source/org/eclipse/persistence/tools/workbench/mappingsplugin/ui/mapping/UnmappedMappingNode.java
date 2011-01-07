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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.persistence.tools.workbench.framework.app.SelectionActionsPolicy;
import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor.MappingDescriptorNode;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;


/**
 * This is not a "typical" node in that it is a "virtual" node that does
 * not have a corresponding object in the model. Unmapped Mapping
 * Nodes are created with an attribute that actually belongs elsewhere
 * in the model tree (under its Class).
 */
public final class UnmappedMappingNode 
	extends MappingNode 
{
	/**
	 * We don't have a corresponding object in the model,
	 * we just have the unmapped attribute.
	 */
	private MWClassAttribute classAttribute;


	// ************ constructors ************

	public UnmappedMappingNode(MWClassAttribute classAttribute, ApplicationContext context, SelectionActionsPolicy mappingNodeTypePolicy, MappingDescriptorNode parent) {
		// no model!
		super(null, context, mappingNodeTypePolicy, parent);
		this.classAttribute = classAttribute;
	}

	
	// ********** AbstractTreeNodeValueModel overrides **********

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}
		UnmappedMappingNode other = (UnmappedMappingNode) o;
		return this.classAttribute == other.classAttribute;
	}

	public int hashCode() {
		return this.classAttribute.hashCode();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.classAttribute.getName());
	}


	// ********** AbstractApplicationNode overrides **********
	
	/**
	 * this node does not have a value; do not call this method
	 * willy-nilly on a collection of heterogeneous nodes - narrow
	 * down the collection to the relevant nodes  ~bjv
	 */
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	protected String buildDisplayString() {
		return this.classAttribute.getName();
	}

	protected String buildIconKey() {
		return "mapping.unmapped";
	}

	/**
	 * an unmapped mapping is never dirty
	 */
	protected boolean buildDirtyFlag() {
		return false;
	}

	/**
	 * make our title look like the title for a real mapping
	 */
	protected String buildPropertiesPageTitleText() {
		return this.classAttribute.nameWithShortType();
	}

	/**
	 * an unmapped mapping never has any application problems
	 */
	protected void addExclusiveApplicationProblemsTo(List list) {
		// no problems
	}

	/**
	 * an unmapped mapping never has problems
	 */
	protected boolean valueHasBranchProblems() {
		return false;
	}

	/**
	 * an unmapped mapping never has any application problems
	 */
	public boolean containsBranchApplicationProblemFor(Problem problem) {
		return false;
	}

	public void addValuePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		// do nothing, no model to listen to
	}

	public void removeValuePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		// do nothing, no model to unlisten :) to
	}
	
	protected void engageValue(String[] propertyNames, PropertyChangeListener listener) {
		// do nothing, no model to listen to
	}

	protected void disengageValue(String[] propertyNames, PropertyChangeListener listener) {
		// do nothing, no model to unlisten :) to
	}

	protected void engageValue(String[] listNames, ListChangeListener listener) {
		// do nothing, no model to listen to
	}

	protected void disengageValue(String[] listNames, ListChangeListener listener) {
		// do nothing, no model to unlisten :) to
	}

	protected String accessibleNameKey() {
		return "ACCESSIBLE_UNMAPPED_MAPPING_NODE";
	}

	public String helpTopicID() {
		return "mapping.unmapped";
	}


	// ********** MappingsApplicationNode overrides **********

	protected Class propertiesPageClass() {
		return UnmappedMappingPropertiesPage.class;
	}


	// ************ MappingNode overrides ************

	public boolean isMapped() {
		return false;
	}

	public boolean isAutoMappable() {
		return true;
	}

	protected void attributeNameChanged() {
		super.attributeNameChanged();
		this.displayStringChanged();
		this.propertiesPageTitleTextChanged();
	}

	/**
	 * call #isMapped() before calling this method
	 */
	public MWMapping getMapping() {
		throw new UnsupportedOperationException();
	}

	public MWClassAttribute instanceVariable() {
		return this.classAttribute;
	}

	public boolean mappingIsInherited() {
		return CollectionTools.contains(this.descriptor().inheritedAttributes(), this.classAttribute);
	}

	void remove() {
		this.removeInstanceVariable();
	}


	// ********** public API **********

	public String getName() {
		return this.classAttribute.getName();
	}

}
