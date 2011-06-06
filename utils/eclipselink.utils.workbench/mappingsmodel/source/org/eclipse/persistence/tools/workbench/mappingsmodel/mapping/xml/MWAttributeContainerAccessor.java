package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.List;

import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class MWAttributeContainerAccessor extends MWContainerAccessor {

	// **************** Variables *********************************************
	
	private MWAttributeHandle accessorAttributeHandle;
		public final static String ACCESSOR_ATTRIBUTE_PROPERTY = "accessorAttributeHandle";

	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	public MWAttributeContainerAccessor() {
		super();
	}

	public MWAttributeContainerAccessor(MWNode parent, MWClassAttribute attribute) {
		super(parent);
		this.setAccessorAttribute(attribute);
	}

	// **************** Initialization ****************************************
	
	protected void initialize(Node parentNode) {
		super.initialize(parentNode);
		this.accessorAttributeHandle = new MWAttributeHandle(this, this.buildAccessorAttributeScrubber());
	}
	
	private NodeReferenceScrubber buildAccessorAttributeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWAttributeContainerAccessor.this.setAccessorAttribute(null);
			}
			public String toString() {
				return "MWAttributeContainerAccessor.buildAccessorAttributeScrubber()";
			}
		};
	}
	
	// **************** Problems handling ********************************************
	
	@Override
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.addAttributeConfiguredProblemsToList(currentProblems);
	}
	
	private void addAttributeConfiguredProblemsToList(List currProblems) {
		if (this.getAccessorAttribute() == null) {
			this.buildProblem(ProblemConstants.MAPPING_CONTAINER_ACCESSOR_ATTRIBUTE_NOT_SELECTED);
		}
	}

	// **************** Containment hierarchy ****************************************

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.accessorAttributeHandle);
	}
	
	// **************** Accessor Attribute *************************************
	
	public MWClassAttribute getAccessorAttribute() {
	    return this.accessorAttributeHandle.getAttribute();  
	}
	
	public void setAccessorAttribute(MWClassAttribute newAccessorAttribute) {
	    MWClassAttribute oldAccessorAttribute = this.getAccessorAttribute();
	    this.accessorAttributeHandle.setAttribute(newAccessorAttribute);
	    if (this.attributeValueHasChanged(oldAccessorAttribute, newAccessorAttribute)) {
		    this.firePropertyChanged(ACCESSOR_ATTRIBUTE_PROPERTY, oldAccessorAttribute, newAccessorAttribute);
		    this.firePropertyChanged(CONTAINER_ACCESSOR_PROPERTY, oldAccessorAttribute, newAccessorAttribute);
	    }
	}
	
	// **************** Runtime conversion ************************************
	
	public void adjustRuntimeMapping(AbstractCompositeObjectMapping mapping) {
		((XMLCompositeObjectMapping)mapping).setContainerAttributeName(getAccessorAttribute().getName());
	}
	
	public void adjustRuntimeMapping(AbstractCompositeCollectionMapping mapping) {
		((XMLCompositeCollectionMapping)mapping).setContainerAttributeName(getAccessorAttribute().getName());
	}

	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAttributeContainerAccessor.class);
		descriptor.getInheritancePolicy().setParentClass(MWContainerAccessor.class);
		
		// class handle
		XMLCompositeObjectMapping accessorClassHandleMapping = new XMLCompositeObjectMapping();
		accessorClassHandleMapping.setAttributeName("accessorAttributeHandle");
		accessorClassHandleMapping.setGetMethodName("getAccessorAttributeForTopLink");
		accessorClassHandleMapping.setSetMethodName("setAccessorAttributeForTopLink");
		accessorClassHandleMapping.setReferenceClass(MWAttributeHandle.class);
		accessorClassHandleMapping.setXPath("accessor-attribute-handle");
		descriptor.addMapping(accessorClassHandleMapping);
		
		return descriptor;
	}

	private MWAttributeHandle getAccessorAttributeForTopLink() {
		return (this.accessorAttributeHandle.getAttribute() == null) ? null : this.accessorAttributeHandle;
	}
	
	private void setAccessorAttributeForTopLink(MWAttributeHandle attribute) {
		NodeReferenceScrubber scrubber = this.buildAccessorAttributeScrubber();
		this.accessorAttributeHandle = ((attribute == null) ? new MWAttributeHandle(this, scrubber) : attribute.setScrubber(scrubber));
	}

	// **************** UI support *********************************************

	@Override
	public String accessorDisplayString() {
		MWClassAttribute attribute = this.getAccessorAttribute();
		return (attribute == null) ? null : attribute.displayString();
	}

	@Override
	public boolean isNull() {
		return false;
	}
	
	@Override
	public boolean isMethods() {
		return false;
	}
	
	@Override
	public boolean isAttribute() {
		return true;
	}
}
