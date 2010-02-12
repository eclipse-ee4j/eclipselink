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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapContainerPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWNullConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWSerializedObjectConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlTypeConversionConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;

public final class MWRelationalDirectMapMapping extends MWRelationalDirectContainerMapping 
	implements MWDirectMapMapping
{
	
	private MWColumnHandle directKeyColumnHandle;
		public final static String DIRECT_KEY_COLUMN_PROPERTY = "directKeyColumn";

	private volatile MWConverter directKeyConverter;

    private MWDirectMapContainerPolicy containerPolicy;

    
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWRelationalDirectMapMapping() {
		super();
	}

	public MWRelationalDirectMapMapping(MWRelationalClassDescriptor parent, MWClassAttribute attribute, String name) {
		super(parent, attribute, name);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.directKeyColumnHandle = new MWColumnHandle(this, this.buildDirectKeyColumnScrubber());
		this.directKeyConverter = new MWNullConverter(this);
        this.containerPolicy = new MWDirectMapContainerPolicy(this);
	}


    // ************** Containment Hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.directKeyColumnHandle);
        children.add(this.directKeyConverter);
        children.add(this.containerPolicy);
	}

	private NodeReferenceScrubber buildDirectKeyColumnScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWRelationalDirectMapMapping.this.setDirectKeyColumn(null);
			}
			public String toString() {
				return "MWRelationalDirectMapMapping.buildDirectKeyColumnScrubber()";
			}
		};
	}



	// **************** Morphing **************
	
	public MWDirectMapMapping asMWDirectMapMapping() {
		return this;
	}

	protected void initializeOn(MWMapping newMapping) {
		newMapping.initializeFromMWRelationalDirectMapMapping(this);
	}
	

	// **************** MWQueryable interface ***********************
	
	public String iconKey() {
		return "mapping.directMap";
	}


	// **************** Direct Key Column ******************

	public MWColumn getDirectKeyColumn() {
		return this.directKeyColumnHandle.getColumn();
	}
	
	public void setDirectKeyColumn(MWColumn directKeyColumn) {
		checkColumn(directKeyColumn);
		Object old = this.directKeyColumnHandle.getColumn();
		this.directKeyColumnHandle.setColumn(directKeyColumn);
		firePropertyChanged(DIRECT_KEY_COLUMN_PROPERTY, old, directKeyColumn);
	}

	protected void setDirectFieldsNull() {
		super.setDirectFieldsNull();
		setDirectKeyColumn(null);
	}

	// **************** Direct Key Converter ******************

	public MWConverter getDirectKeyConverter() {
		return this.directKeyConverter;
	}
	
	public MWNullConverter setNullDirectKeyConverter() {
		MWNullConverter nullConverter = new MWNullConverter(this);
		this.setDirectKeyConverter(nullConverter);
		return nullConverter;
	}
	
	public MWObjectTypeConverter setObjectTypeDirectKeyConverter() {
		MWObjectTypeConverter objectTypeConverter = new MWObjectTypeConverter(this);
		this.setDirectKeyConverter(objectTypeConverter);
		return objectTypeConverter;
	}
	
	public MWSerializedObjectConverter setSerializedObjectDirectKeyConverter() {
		MWSerializedObjectConverter serializedObjectConverter = new MWSerializedObjectConverter(this);
		this.setDirectKeyConverter(serializedObjectConverter);
		return serializedObjectConverter;
	}
	
	public MWTypeConversionConverter setTypeConversionDirectKeyConverter() {
		MWTypeConversionConverter typeConversionConverter = new MWXmlTypeConversionConverter(this);
		this.setDirectKeyConverter(typeConversionConverter);
		return typeConversionConverter;
	}
	
	
	private void setDirectKeyConverter(MWConverter newConverter) {
		MWConverter oldConverter = this.directKeyConverter;
		this.directKeyConverter = newConverter;
		newConverter.setParent(this); // This step only important when morphing the mapping
		this.firePropertyChanged(DIRECT_KEY_CONVERTER_PROPERTY, oldConverter, newConverter);
	}

    // **************** Container policy **************************************
    
    public MWDirectMapContainerPolicy getContainerPolicy() {
        return this.containerPolicy;
    }


    protected MWClass conatinerPolicyClass() {
        return getContainerPolicy().getDefaultingContainerClass().getContainerClass();
    }
    
    
	// **************** MWRelationalDirectContainerMapping implementation **************
	
    protected int automapNonPrimaryKeyColumnsSize() {
    	return 2;	// key and value columns
    }
    

	//************** Problem Handling **********
	
	protected void addProblemsTo(List newProblems) {
		super.addProblemsTo(newProblems);
		this.checkDirectKeyColumn(newProblems);
	}

	private void checkDirectKeyColumn(List newProblems) {
		if (this.parentDescriptorIsAggregate()) {
			return;
		}
		if (this.getDirectKeyColumn() == null) {
			newProblems.add(this.buildProblem(ProblemConstants.MAPPING_DIRECT_KEY_FIELD_NOT_SPECIFIED));
		}
	}
	


	// **************** Runtime Conversion ******************
	
	protected DatabaseMapping buildRuntimeMapping() {
		return new DirectMapMapping();
	}

	public DatabaseMapping runtimeMapping() {
		DirectMapMapping runtimeMapping = (DirectMapMapping) super.runtimeMapping();

		runtimeMapping.setContainerPolicy(this.containerPolicy.runtimeContainerPolicy());

		runtimeMapping.setKeyConverter(getDirectKeyConverter().runtimeConverter(runtimeMapping));		

		if (getDirectKeyColumn() != null) {
			runtimeMapping.setDirectKeyFieldName(getDirectKeyColumn().qualifiedName());
		}

        return runtimeMapping;
	}


	// **************** TopLink methods *******************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWRelationalDirectMapMapping.class);
		descriptor.getInheritancePolicy().setParentClass(MWRelationalDirectContainerMapping.class);

		XMLCompositeObjectMapping directFieldMapping = new XMLCompositeObjectMapping();
		directFieldMapping.setAttributeName("directKeyColumnHandle");
		directFieldMapping.setGetMethodName("getDirectKeyColumnHandleForTopLink");
		directFieldMapping.setSetMethodName("setDirectKeyColumnHandleForTopLink");
		directFieldMapping.setReferenceClass(MWColumnHandle.class);
		directFieldMapping.setXPath("direct-key-column-handle");
		descriptor.addMapping(directFieldMapping);
	
		XMLCompositeObjectMapping converterMapping = new XMLCompositeObjectMapping();
		converterMapping.setReferenceClass(MWConverter.class);
		converterMapping.setAttributeName("directKeyConverter");
		converterMapping.setGetMethodName("getDirectKeyConverterForTopLink");
		converterMapping.setSetMethodName("setDirectKeyConverterForTopLink");
		converterMapping.setXPath("direct-key-converter");
		descriptor.addMapping(converterMapping);	

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("containerPolicy");
        containerPolicyMapping.setReferenceClass(MWDirectMapContainerPolicy.class);
        containerPolicyMapping.setXPath("container-policy");
        descriptor.addMapping(containerPolicyMapping);
        return descriptor;
	}

	private MWColumnHandle getDirectKeyColumnHandleForTopLink() {
		return (this.directKeyColumnHandle.getColumn() == null) ? null : this.directKeyColumnHandle;
	}	
	private void setDirectKeyColumnHandleForTopLink(MWColumnHandle directKeyColumnHandle) {
		NodeReferenceScrubber scrubber = this.buildDirectKeyColumnScrubber();
		this.directKeyColumnHandle = ((directKeyColumnHandle == null) ? new MWColumnHandle(this, scrubber) : directKeyColumnHandle.setScrubber(scrubber));
	}

	private MWConverter getDirectKeyConverterForTopLink() {
		return (this.directKeyConverter == null) ? null : this.directKeyConverter.getValueForTopLink();
	}
	private void setDirectKeyConverterForTopLink(MWConverter converter) {
		this.directKeyConverter = (converter == null) ? new MWNullConverter(this) : converter;
	}

}
