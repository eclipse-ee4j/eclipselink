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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAdvancedPropertyAdditionException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWAdvancedPropertyRemovalException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorInterfaceAliasPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWInterfaceAliasDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWNullDescriptorPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;


public final class MWRootEisDescriptor extends MWEisDescriptor 
	implements MWTransactionalDescriptor, MWInterfaceAliasDescriptor {


	private volatile MWDescriptorPolicy returningPolicy;

	private volatile MWDescriptorPolicy interfaceAliasPolicy;
		public final static String INTERFACE_ALIAS_POLICY_PROPERTY = "interfaceAliasPolicy";
	
	// **************** Static methods ************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
			
		descriptor.setJavaClass(MWRootEisDescriptor.class);
		descriptor.getInheritancePolicy().setParentClass(MWXmlDescriptor.class);
			
		XMLCompositeObjectMapping returningPolicyMapping = new XMLCompositeObjectMapping();
		returningPolicyMapping.setAttributeName("returningPolicy");
		returningPolicyMapping.setReferenceClass(MWEisReturningPolicy.class);
		returningPolicyMapping.setSetMethodName("setReturningPolicyForTopLink");
		returningPolicyMapping.setGetMethodName("getReturningPolicyForTopLink");
		returningPolicyMapping.setXPath("returning-policy");
		descriptor.addMapping(returningPolicyMapping);

		XMLCompositeObjectMapping interfaceAliasPolicyMapping = new XMLCompositeObjectMapping();
		interfaceAliasPolicyMapping.setAttributeName("interfaceAliasPolicy");
		interfaceAliasPolicyMapping.setReferenceClass(MWDescriptorInterfaceAliasPolicy.class);
		interfaceAliasPolicyMapping.setSetMethodName("setInterfaceAliasPolicyForTopLink");
		interfaceAliasPolicyMapping.setGetMethodName("getInterfaceAliasPolicyForTopLink");
		interfaceAliasPolicyMapping.setXPath("interface-alias-policy");
		descriptor.addMapping(interfaceAliasPolicyMapping);
			
		return descriptor;
	}	
	


	// **************** Constructors ************
	
	private MWRootEisDescriptor() {
		super();
	}

	public MWRootEisDescriptor(MWEisProject project, MWClass type, String name) {
		super(project, type, name);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		setInitialDefaultRootElement();
		this.returningPolicy = new MWNullDescriptorPolicy(this);
		this.interfaceAliasPolicy = new MWNullDescriptorPolicy(this);
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.interfaceAliasPolicy);
		children.add(this.returningPolicy);
	}
	
	protected MWTransactionalPolicy buildDefaultTransactionalPolicy() {
		return new MWEisTransactionalPolicy(this);
	}
    
	public MWRootEisDescriptor asRootEisDescriptor() {
		return this;
	}
	
	public void applyAdvancedPolicyDefaults(MWProjectDefaultsPolicy defaultsPolicy) {
		defaultsPolicy.applyAdvancedPolicyDefaults(this);
	}
	
	// **************** Convenience ************

	public MWQueryManager getQueryManager() {
		return getTransactionalPolicy().getQueryManager();
	}
	
	
	public boolean isRootDescriptor() {
		return true;
	}

	// **************** Primary key policy ************************************
	
	public MWXmlPrimaryKeyPolicy primaryKeyPolicy() {
		return ((MWEisTransactionalPolicy) this.getTransactionalPolicy()).getPrimaryKeyPolicy();
	}

	
	// **************** Interface Alias Policy API ******************************
		
	public void addInterfaceAliasPolicy() throws MWAdvancedPropertyAdditionException
	{
		if (interfaceAliasPolicy.isActive())
		{
			throw new MWAdvancedPropertyAdditionException(INTERFACE_ALIAS_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		else
		{
			setInterfaceAliasPolicy(new MWDescriptorInterfaceAliasPolicy(this));
		}
	}

	public void removeInterfaceAliasPolicy()
	{
		if (interfaceAliasPolicy.isActive())
		{
			setInterfaceAliasPolicy(new MWNullDescriptorPolicy(this));
		}
		else
		{
			throw new MWAdvancedPropertyRemovalException(INTERFACE_ALIAS_POLICY_PROPERTY, "policy does not exist on the descriptor");
		}
	}
	
	public MWDescriptorPolicy getInterfaceAliasPolicy()
	{
		return interfaceAliasPolicy;
	}

	private MWDescriptorInterfaceAliasPolicy getInterfaceAliasPolicyForTopLink()
	{
		return (MWDescriptorInterfaceAliasPolicy)interfaceAliasPolicy.getPersistedPolicy();
	}	

	protected void setInterfaceAliasPolicy(MWDescriptorPolicy interfaceAliasPolicy)	{
		Object old = this.interfaceAliasPolicy;
		this.interfaceAliasPolicy = interfaceAliasPolicy;
		firePropertyChanged(INTERFACE_ALIAS_POLICY_PROPERTY, old, this.interfaceAliasPolicy);
	}

	private void setInterfaceAliasPolicyForTopLink(MWDescriptorInterfaceAliasPolicy interfaceAliasPolicy)
	{
		if (interfaceAliasPolicy == null)
		{
			this.interfaceAliasPolicy = new MWNullDescriptorPolicy(this);
		}
		else
		{
			this.interfaceAliasPolicy = interfaceAliasPolicy;
		}
	}
	
	/** - returns an iterator on all mappings that map to the given xpath */
	private Iterator mappingsForXpath(String xpath) {
		Collection mappings = new ArrayList();
		
		for (Iterator stream = this.mappings(); stream.hasNext(); ) {
			MWMapping mapping = (MWMapping) stream.next();
			//TODO addWrittenFieldsTo(Collection) is not fully implemented in the mappings model
			Collection writtenFields = new ArrayList();
			mapping.addWrittenFieldsTo(writtenFields);
			if (writtenFields.contains(xpath)) {
				mappings.add(mapping);
			}
		}
		
		return mappings.iterator();
	}
	
	// - returns an iterator on the attributes mapped by all mappings obtained by
	//   getPrimaryKeyMappings()
	public Iterator primaryKeyAttributes() {
		return new TransformationIterator(this.primaryKeyMappings()) {
			protected Object transform(Object next) {
				return ((MWMapping) next).getInstanceVariable();
			}
		};
	}		
		
		
	// - returns an iterator on all mappings in this descriptor that map to primary key fields,
	//   plus all mappings in this descriptor's superdescriptors that do the same
	public Iterator primaryKeyMappings() {
		Collection pkMappings = new Vector();
		
		for (Iterator stream = primaryKeyPolicy().primaryKeyXpaths(); stream.hasNext(); ) {
			CollectionTools.addAll(pkMappings, mappingsForXpath((String) stream.next()));
		}
		
        MWDescriptor parentDescriptor = getInheritancePolicy().getParentDescriptor();
        //TODO remove the instanceof put the primaryKeyMapping() api on MWEisDescriptor and just return an empty collection
		if (parentDescriptor != null && parentDescriptor instanceof MWRootEisDescriptor) {
			CollectionTools.addAll(pkMappings, ((MWRootEisDescriptor) parentDescriptor).primaryKeyMappings());
		}
		
		return pkMappings.iterator();			
	}
	
	
	//	 ********** MWReturningPolicy API**********

	public MWDescriptorPolicy getReturningPolicy() {
		return this.returningPolicy;
	}
	

	
	private void setReturningPolicy(MWDescriptorPolicy returningPolicy) {
		Object old = this.returningPolicy;
		this.returningPolicy = returningPolicy;
		firePropertyChanged( RETURNING_POLICY_PROPERTY, old, returningPolicy);
	}
		
	public void addReturningPolicy() throws MWAdvancedPropertyAdditionException {
		if(this.returningPolicy.isActive()) {
			throw new MWAdvancedPropertyAdditionException( RETURNING_POLICY_PROPERTY, "policy already exists on descriptor");
		}
		else {
			setReturningPolicy(new MWEisReturningPolicy(this));
		}
	}
	
	public void removeReturningPolicy() {
		if(this.returningPolicy.isActive()) {
			getReturningPolicy().dispose();
			setReturningPolicy(new MWNullDescriptorPolicy(this));
		}
		else {
			throw new MWAdvancedPropertyRemovalException( RETURNING_POLICY_PROPERTY, "policy does not exist on the descriptor");
		}
	}

	public boolean supportsReturningPolicy() {
		return true;
	}
	
    public boolean supportsCachingPolicy() {
        return true;
    }
	//************* Problem Handling *************

	/** Check for any problems and add them to the specified collection. */
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.checkPrimaryKeysSpecified(newProblems);
		this.checkWritableMappingsForPrimaryKeys(newProblems);
	}

	private void checkPrimaryKeysSpecified(List newProblems) {
		boolean hasParent = getInheritancePolicy().getParentDescriptor() != null;
		
		if (this.isRootDescriptor()
			&& ! hasParent 
			&& (this.getSchemaContext() != null)
			&& (((MWEisTransactionalPolicy) this.getTransactionalPolicy()).getPrimaryKeyPolicy().primaryKeysSize() == 0))
		{
			newProblems.add(this.buildProblem(ProblemConstants.EIS_DESCRIPTOR_NO_PRIMARY_KEYS_SPECIFIED));
		}
	}
	
	private void checkWritableMappingsForPrimaryKeys(List newProblems) {
		if (((MWEisTransactionalPolicy)getTransactionalPolicy()).getPrimaryKeyPolicy().primaryKeysSize() > 0) {
			for (Iterator stream = ((MWEisTransactionalPolicy)getTransactionalPolicy()).getPrimaryKeyPolicy().primaryKeys(); stream.hasNext(); ) {
				if (this.writableMappingsForField((MWDataField)stream.next()).isEmpty()) {
					newProblems.add(this.buildProblem(ProblemConstants.EIS_ROOT_DESCRIPTOR_NO_WRITABLE_MAPPINGS_FOR_PRIMARY_KEYS));
				}
			}
		}
	}
	
	// ************** Runtime conversion *****************

	public ClassDescriptor buildRuntimeDescriptor() {
		EISDescriptor runtimeDescriptor = (EISDescriptor) super.buildRuntimeDescriptor();

		this.returningPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
		this.interfaceAliasPolicy.adjustRuntimeDescriptor(runtimeDescriptor);
	
		return runtimeDescriptor;
	}

	
	// ************** TopLink only methods *****************
	
	private MWReturningPolicy getReturningPolicyForTopLink() {
		return (MWReturningPolicy) this.returningPolicy.getPersistedPolicy();
	}
	
	private void setReturningPolicyForTopLink(MWReturningPolicy returningPolicy) {
		if (returningPolicy == null) {
			this.returningPolicy = new MWNullDescriptorPolicy(this);
		}
		else {
			this.returningPolicy = returningPolicy;
		}
	}
}
	
