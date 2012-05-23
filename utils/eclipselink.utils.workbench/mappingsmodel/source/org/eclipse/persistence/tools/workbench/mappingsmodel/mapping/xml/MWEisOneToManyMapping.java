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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWCollectionContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWIndirectableContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWListContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSetContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml.MWEisInteraction;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathContext;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWEisOneToManyMapping 
	extends MWEisReferenceMapping 
	implements MWIndirectableContainerMapping, MWXpathContext, MWMapContainerMapping
{
	// **************** Variables *********************************************
	
	private volatile String foreignKeyLocation;
		public static final String FOREIGN_KEY_LOCATION_PROPERTY = "foreignKeyLocation";
		public static final String KEYS_ON_TARGET = "keysOnTarget";
		public static final String KEYS_ON_SOURCE = "keysOnSource";
	
	//if KEYS_ON_SOURCE is the foreignKeyLocation, then the grouping element applies
	private MWXmlField foreignKeyGroupingElement;
	
	private volatile MWContainerPolicy containerPolicy;
        
	private volatile MWEisInteraction deleteAllInteraction;
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWEisOneToManyMapping() {
		super();
	}
	
	public MWEisOneToManyMapping(MWEisDescriptor descriptor, MWClassAttribute attribute, String attributeName) {
		super(descriptor, attribute, attributeName);
	}

	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.foreignKeyLocation = KEYS_ON_TARGET;
		this.foreignKeyGroupingElement = new MWXmlField(this);
		this.deleteAllInteraction = new MWEisInteraction(this);
	}
	
	/**
	 * 1:n mappings always have a selection interaction
	 */
	protected boolean requiresSelectionInteraction() {
		return true;
	}
	
    protected void initialize(MWClassAttribute attribute, String name) {
        super.initialize(attribute, name);
        
        //TODO wow this is not pretty! and it's copied in other collection mappings
        if (attribute.isValueHolder()) {
               if (attribute.getValueType().isAssignableToMap()) {
                    this.containerPolicy = new MWMapContainerPolicy(this);
                }
                else if (attribute.getValueType().isAssignableToList()) {
                    this.containerPolicy = new MWListContainerPolicy(this);
                }
                else if (attribute.getValueType().isAssignableToSet()) {
                    this.containerPolicy = new MWSetContainerPolicy(this);
                }
                else if (attribute.getValueType().isAssignableToCollection()){
                    this.containerPolicy = new MWCollectionContainerPolicy(this);
                }
                else { //This is the default in the runtime
                    this.containerPolicy = new MWListContainerPolicy(this);
                }
            setUseValueHolderIndirection();
        }
        else {
            if (attribute.isAssignableToMap()) {
                this.containerPolicy = new MWMapContainerPolicy(this);
            }
            else if (attribute.isAssignableToList()) {
                this.containerPolicy = new MWListContainerPolicy(this);
            }
            else if (attribute.isAssignableToSet()) {
                this.containerPolicy = new MWSetContainerPolicy(this);
            }
            else if (attribute.isAssignableToCollection()){
                this.containerPolicy = new MWCollectionContainerPolicy(this);
            }
            else { //This is the default in the runtime
                this.containerPolicy = new MWListContainerPolicy(this);
            }
            setUseTransparentIndirection();
        }
    }

	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.foreignKeyGroupingElement);
		children.add(this.containerPolicy);
		children.add(this.deleteAllInteraction);
	}
	
	
	// **************** Foreign Key Location ***************
	
	public boolean foreignKeysAreOnSource() {
		return this.foreignKeyLocation == KEYS_ON_SOURCE;
	}
	
	public boolean foreignKeysAreOnTarget() {
		return this.foreignKeyLocation == KEYS_ON_TARGET;
	}
	
	public void setForeignKeysOnSource() {
		this.setForeignKeyLocation(KEYS_ON_SOURCE);
	}
	
	public void setForeignKeysOnTarget() {
		this.setForeignKeyLocation(KEYS_ON_TARGET);
		this.getForeignKeyGroupingElement().setXpath("");
		this.clearXmlFieldPairs();
	}
	
	private void setForeignKeyLocation(String newValue) {
		String oldValue = this.foreignKeyLocation;
		this.foreignKeyLocation = newValue;
		firePropertyChanged(FOREIGN_KEY_LOCATION_PROPERTY, oldValue, newValue);
	}
	
	
	// **************** Grouping element **************************************
	
	public MWXmlField getForeignKeyGroupingElement() {
		return this.foreignKeyGroupingElement;
	}
	
	
	// **************** Container policy **************************************
	
	public MWContainerPolicy getContainerPolicy() {
		return this.containerPolicy;
	}
	
	private void setContainerPolicy(MWContainerPolicy containerPolicy) {
		Object oldValue = this.containerPolicy;
		this.containerPolicy = containerPolicy;
		firePropertyChanged(CONTAINER_POLICY_PROPERTY, oldValue, containerPolicy);
	}
	
	public MWMapContainerPolicy setMapContainerPolicy() {
		if (this.containerPolicy instanceof MWMapContainerPolicy) {
			return (MWMapContainerPolicy) this.containerPolicy;
		}
		MWMapContainerPolicy cp = new MWMapContainerPolicy(this);
		this.setContainerPolicy(cp);
		return cp;
	}
	
	public MWCollectionContainerPolicy setCollectionContainerPolicy() {
		if (this.containerPolicy instanceof MWCollectionContainerPolicy) {
			return (MWCollectionContainerPolicy) this.containerPolicy;
		}
		MWCollectionContainerPolicy cp = new MWCollectionContainerPolicy(this);
		this.setContainerPolicy(cp);
		return cp;
	}

    public MWListContainerPolicy setListContainerPolicy() {
        if (this.containerPolicy instanceof MWListContainerPolicy) {
            return (MWListContainerPolicy) this.containerPolicy;
        }
        MWListContainerPolicy cp = new MWListContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
    
    public MWSetContainerPolicy setSetContainerPolicy() {
        if (this.containerPolicy instanceof MWSetContainerPolicy) {
            return (MWSetContainerPolicy) this.containerPolicy;
        }
        MWSetContainerPolicy cp = new MWSetContainerPolicy(this);
        this.setContainerPolicy(cp);
        return cp;
    }
    
	public Iterator candidateKeyMethods(Filter keyMethodFilter) {
		return (this.getReferenceDescriptor() == null) ?
			NullIterator.instance()
		:
			new FilteringIterator(this.getReferenceDescriptor().getMWClass().allInstanceMethods(), keyMethodFilter);
	}
	
	
	// **************** Delete All interaction ********************************
	
	public MWEisInteraction getDeleteAllInteraction() {
		return this.deleteAllInteraction;
	}
	
	
	// **************** MWXmlMapping contract *********************************
	
	public MWXmlField firstMappedXmlField() {
		return this.getForeignKeyGroupingElement().isResolved() ?
			this.getForeignKeyGroupingElement()
		:
			super.firstMappedXmlField();
	}
	
	
	// **************** MWXpathContext implementation *************************
	//   This mapping works as the context for the grouping element.
	//   MWXmlFieldPair acts as the context for the source and target fields.
	
	public MWSchemaContextComponent schemaContext(MWXmlField xmlField) {
		return this.eisDescriptor().getSchemaContext();
	}
	
	public MWXpathSpec xpathSpec(MWXmlField xmlField) {
		return this.buildXpathSpec();
	}
	
	protected MWXpathSpec buildXpathSpec() {
		return new MWXpathSpec() {
			public boolean mayUseCollectionData() {
				return true;
			}
			
			public boolean mayUseComplexData() {
				return true;
			}
			
			public boolean mayUseSimpleData() {
				return false;
			}
		};
	}
	
	/** 
	 * Return true if a source field may use a collection xpath
	 * @see MWEisReferenceMapping#sourceFieldMayUseCollectionXpath()
	 */
	public boolean sourceFieldMayUseCollectionXpath() {
		return true;
	}
	
	
	// ************* MWIndirectableCollectionMapping implementation ***********
	
	public boolean usesTransparentIndirection() {
		return this.getIndirectionType() == TRANSPARENT_INDIRECTION;
	}
	
	public void setUseTransparentIndirection() {
		this.setIndirectionType(TRANSPARENT_INDIRECTION);	
	}
	
	
	// ************* Problem Handling *****************************************
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		
		this.checkFieldPairs(newProblems);
		
		this.checkGroupingElement(newProblems);
		this.checkGroupingElementForeignKeys(newProblems);
		
		this.checkSelectionInteraction(newProblems);
		this.checkDeleteAllInteraction(newProblems);
		
		this.addUsesTransparentIndirectionWhileMaintainsBiDirectionalRelationship(newProblems);
	}
	
	private void checkFieldPairs(List newProblems) {
		if (this.foreignKeysAreOnSource() && this.xmlFieldPairsSize() == 0) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_1_TO_M_FIELD_PAIRS_NOT_SPECIFIED));
		}
	}
	
	private void checkGroupingElement(List newProblems) {
		if (this.xmlFieldPairsSize() > 1 && this.getForeignKeyGroupingElement() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_FOREIGN_KEY_GROUPING_ELEMENT_NOT_SPECIFIED));
		}
	}
	
	private void checkGroupingElementForeignKeys(List newProblems) {
		MWXmlField groupingElement = this.getForeignKeyGroupingElement();
		
		if (! groupingElement.isSpecified() || ! groupingElement.isResolved()) {
			return;
		}
		
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			MWXmlField foreignKeyField = ((MWXmlFieldPair) stream.next()).getSourceXmlField();
			
			if (foreignKeyField.isSpecified() && ! groupingElement.containsXmlField(foreignKeyField)) {
				newProblems.add(this.buildProblem(ProblemConstants.MAPPING_FOREIGN_KEY_NOT_CONTAINED_BY_GROUPING_ELEMENT));
				break;
			}
		}
	}
	
	private void checkSelectionInteraction(List newProblems) {
		if ( ! this.getSelectionInteraction().isSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_SELECTION_INTERACTION_NOT_SPECIFIED));
		}
	}
	
	private void checkDeleteAllInteraction(List newProblems) {
		if (this.getDeleteAllInteraction().isSpecified() && ! this.isPrivateOwned()) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_DELETE_ALL_INTERACTION_SPECIFIED_BUT_NOT_PRIVATE_OWNED));
		}
	}
	
	private void addUsesTransparentIndirectionWhileMaintainsBiDirectionalRelationship(List newProblems) {
		if (this.maintainsBidirectionalRelationship() && (this.usesNoIndirection() || this.usesValueHolderIndirection())) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_COLLECTION_MAINTTAINSBIDIRECTIONAL_NO_TRANSPARENT_INDIRECTION));
		}
	}
	
	// **************** Morphing **********************************************
	
	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWEisOneToManyMapping(this);
	}
	
	public void initializeFromMWCompositeCollectionMapping(MWCompositeCollectionMapping oldMapping) {
	    super.initializeFromMWCompositeCollectionMapping(oldMapping);
        //getContainerPolicy().initializeFrom(oldMapping.getContainerPolicy());
	}
    
	protected void initializeFromMWIndirectableContainerMapping(MWIndirectableContainerMapping oldMapping) {
		super.initializeFromMWIndirectableContainerMapping(oldMapping);
		
		if (oldMapping.usesTransparentIndirection()) {
			this.setUseTransparentIndirection();
		}
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths */
	public void resolveXpaths() {
		super.resolveXpaths();
		this.foreignKeyGroupingElement.resolveXpaths();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		super.schemaChanged(change);
		this.foreignKeyGroupingElement.schemaChanged(change);
	}
	
	
	// **************** Runtime Conversion ************************************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new EISOneToManyMapping();
	}
	
	public DatabaseMapping runtimeMapping() {
		EISOneToManyMapping mapping = (EISOneToManyMapping) super.runtimeMapping();
		
		if (this.getForeignKeyGroupingElement().isSpecified()) {
			mapping.setForeignKeyGroupingElement(this.getForeignKeyGroupingElement().runtimeField().getName());
		}
		
		for (Iterator stream = this.xmlFieldPairs(); stream.hasNext(); ) {
			((MWXmlFieldPair) stream.next()).addRuntimeForeignKeyField(mapping, this.getForeignKeyGroupingElement());
		}
		
		// *** Indirection ***
		if (this.usesTransparentIndirection()) {
			mapping.setIndirectionPolicy(new TransparentIndirectionPolicy());
		}
		
        mapping.setContainerPolicy(getContainerPolicy().runtimeContainerPolicy());
		
		if (this.getDeleteAllInteraction().isSpecified()) {
			mapping.setDeleteAllCall(this.getDeleteAllInteraction().runtimeInteraction());
		}
		
		return mapping;
	}
	
	
	// **************** TopLink methods ***************************************
		
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWEisOneToManyMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWEisReferenceMapping.class);
		
		// use an object type mapping so we can preserve object identity
		ObjectTypeConverter foreignKeyLocationConverter = new ObjectTypeConverter();
		foreignKeyLocationConverter.addConversionValue(KEYS_ON_TARGET, KEYS_ON_TARGET);
		foreignKeyLocationConverter.addConversionValue(KEYS_ON_SOURCE, KEYS_ON_SOURCE);
		XMLDirectMapping foreignKeyLocationMapping = new XMLDirectMapping();
		foreignKeyLocationMapping.setAttributeName("foreignKeyLocation");
		foreignKeyLocationMapping.setXPath("foreign-key-location/text()");
		foreignKeyLocationMapping.setNullValue(KEYS_ON_SOURCE);
		foreignKeyLocationMapping.setConverter(foreignKeyLocationConverter);
		descriptor.addMapping(foreignKeyLocationMapping);
		
		XMLCompositeObjectMapping groupingElementMapping = new XMLCompositeObjectMapping();
		groupingElementMapping.setAttributeName("foreignKeyGroupingElement");
		groupingElementMapping.setReferenceClass(MWXmlField.class);
		groupingElementMapping.setGetMethodName("getGroupingElementForTopLink");
		groupingElementMapping.setSetMethodName("setGroupingElementForTopLink");
		groupingElementMapping.setXPath("foreign-key-grouping-element");
		descriptor.addMapping(groupingElementMapping);
		
		XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
		containerPolicyMapping.setAttributeName("containerPolicy");
		containerPolicyMapping.setReferenceClass(MWContainerPolicy.MWContainerPolicyRoot.class);
		containerPolicyMapping.setXPath("container-policy");
		descriptor.addMapping(containerPolicyMapping);
						
		XMLCompositeObjectMapping deleteAllInteractionMapping = new XMLCompositeObjectMapping();
		deleteAllInteractionMapping.setAttributeName("deleteAllInteraction");
		deleteAllInteractionMapping.setReferenceClass(MWEisInteraction.class);
		deleteAllInteractionMapping.setXPath("delete-all-interaction");
		descriptor.addMapping(deleteAllInteractionMapping);
		
		return descriptor;	
	}
	
	private MWXmlField getGroupingElementForTopLink() {
		return ("".equals(this.foreignKeyGroupingElement.getXpath())) ? null : this.foreignKeyGroupingElement;
	}
	
	private void setGroupingElementForTopLink(MWXmlField xmlField) {
		this.foreignKeyGroupingElement = ((xmlField == null) ? new MWXmlField(this) : xmlField);
	}
}
