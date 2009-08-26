package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import java.util.List;

import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

public final class MWMethodContainerAccessor extends MWContainerAccessor {

    // **************** Variables *********************************************
    
    private MWMethodHandle accessorGetMethodHandle;
    	public final static String ACCESSOR_GET_METHOD_PROPERTY = "accessorGetMethod";

    private MWMethodHandle accessorSetMethodHandle;
    	public final static String ACCESSOR_SET_METHOD_PROPERTY = "accessorSetMethodHandle";

   	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	public MWMethodContainerAccessor() {
		super();
	}

	public MWMethodContainerAccessor(MWNode parent, MWMethod getMethod, MWMethod setMethod) {
		super(parent);
		this.setAccessorGetMethod(getMethod);
		this.setAccessorSetMethod(setMethod);
	}
	
	// **************** Initialization ****************************************
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.accessorGetMethodHandle = new MWMethodHandle(this, this.buildGetMethodScrubber());
		this.accessorSetMethodHandle = new MWMethodHandle(this, this.buildSetMethodScrubber());
	}
	
	// **************** Problems handling ********************************************

	@Override
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.addGetMethodNotSelectedProblemToList(currentProblems);
		this.addSetMethodNotSelectedProblemToList(currentProblems);
	}
	
	private void addGetMethodNotSelectedProblemToList(List currentProblems) {
		if (this.getAccessorGetMethod() == null) {
			this.buildProblem(ProblemConstants.MAPPING_CONTAINER_ACCESSOR_GET_METHOD_NOT_SELECTED);
		}
	}
	
	private void addSetMethodNotSelectedProblemToList(List currentProblems) {
		if (this.getAccessorSetMethod() == null) {
			this.buildProblem(ProblemConstants.MAPPING_CONTAINER_ACCESSOR_SET_METHOD_NOT_SELECTED);
		}
	}
	
	// **************** Containment hierarchy *********************************
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.accessorGetMethodHandle);
		children.add(this.accessorSetMethodHandle);
	}
	
	private NodeReferenceScrubber buildGetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMethodContainerAccessor.this.setAccessorGetMethod(null);
			}
			public String toString() {
				return "MWMethodContainerAccessor.buildGetMethodScrubber()";
			}
		};
	}
	
	private NodeReferenceScrubber buildSetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMethodContainerAccessor.this.setAccessorSetMethod(null);
			}
			public String toString() {
				return "MWMethodContainerAccessor.buildSetMethodScrubber()";
			}
		};
	}

	// **************** Accessor Methods *************************************
	
	public MWMethod getAccessorGetMethod() {
	    return this.accessorGetMethodHandle.getMethod();  
	}
	
	public void setAccessorGetMethod(MWMethod newAccessorMethod) {
	    MWMethod oldAccessorMethod = this.getAccessorGetMethod();
	    this.accessorGetMethodHandle.setMethod(newAccessorMethod);
	    if (this.attributeValueHasChanged(oldAccessorMethod, newAccessorMethod)) {
		    this.firePropertyChanged(ACCESSOR_GET_METHOD_PROPERTY, oldAccessorMethod, newAccessorMethod);
		    this.firePropertyChanged(CONTAINER_ACCESSOR_PROPERTY, oldAccessorMethod, newAccessorMethod);
	    }
	}

	public MWMethod getAccessorSetMethod() {
	    return this.accessorSetMethodHandle.getMethod();  
	}
	
	public void setAccessorSetMethod(MWMethod newAccessorMethod) {
	    MWMethod oldAccessorMethod = this.getAccessorSetMethod();
	    this.accessorSetMethodHandle.setMethod(newAccessorMethod);
	    if (this.attributeValueHasChanged(oldAccessorMethod, newAccessorMethod)) {
		    this.firePropertyChanged(ACCESSOR_SET_METHOD_PROPERTY, oldAccessorMethod, newAccessorMethod);
		    this.firePropertyChanged(CONTAINER_ACCESSOR_PROPERTY, oldAccessorMethod, newAccessorMethod);
	    }
	}

	@Override
	public String accessorDisplayString() {
		MWMethod getMethod = this.getAccessorGetMethod();
		MWMethod setMethod = this.getAccessorSetMethod();

		String displayString = "";
		if(getMethod != null) {
			displayString += getMethod.displayString();
		}
		if(setMethod != null) {
			displayString += "/";
			displayString += setMethod.displayString();
		}
		
		return displayString;
	}
	
	// **************** Runtime conversion ************************************
	
	public void adjustRuntimeMapping(AbstractCompositeObjectMapping mapping) {
		((XMLCompositeObjectMapping)mapping).setContainerGetMethodName(getAccessorGetMethod().getName());
		((XMLCompositeObjectMapping)mapping).setContainerSetMethodName(getAccessorSetMethod().getName());
	}
	
	public void adjustRuntimeMapping(AbstractCompositeCollectionMapping mapping) {
		((XMLCompositeCollectionMapping)mapping).setContainerGetMethodName(getAccessorGetMethod().getName());
		((XMLCompositeCollectionMapping)mapping).setContainerSetMethodName(getAccessorSetMethod().getName());
	}
	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {	
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWMethodContainerAccessor.class);
		descriptor.getInheritancePolicy().setParentClass(MWContainerAccessor.class);

		XMLCompositeObjectMapping getMethodHandleMapping = new XMLCompositeObjectMapping();
		getMethodHandleMapping.setAttributeName("accessorGetMethodHandle");
		getMethodHandleMapping.setGetMethodName("getAccessorGetMethodHandleForTopLink");
		getMethodHandleMapping.setSetMethodName("setAccessorGetMethodHandleForTopLink");
		getMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		getMethodHandleMapping.setXPath("accessor-get-method-handle");
		descriptor.addMapping(getMethodHandleMapping);
		
		XMLCompositeObjectMapping setMethodHandleMapping = new XMLCompositeObjectMapping();
		setMethodHandleMapping.setAttributeName("accessorSetMethodHandle");
		setMethodHandleMapping.setGetMethodName("getAccessorSetMethodHandleForTopLink");
		setMethodHandleMapping.setSetMethodName("setAccessorSetMethodHandleForTopLink");
		setMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		setMethodHandleMapping.setXPath("accessor-set-method-handle");
		descriptor.addMapping(setMethodHandleMapping);
		
		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getAccessorGetMethodHandleForTopLink() {
		return (this.accessorGetMethodHandle.getMethod() == null) ? null : this.accessorGetMethodHandle;
	}
	private void setAccessorGetMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildGetMethodScrubber();
		this.accessorGetMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWMethodHandle getAccessorSetMethodHandleForTopLink() {
		return (this.accessorSetMethodHandle.getMethod() == null) ? null : this.accessorSetMethodHandle;
	}
	private void setAccessorSetMethodHandleForTopLink(MWMethodHandle methodHandle) {
		NodeReferenceScrubber scrubber = this.buildSetMethodScrubber();
		this.accessorSetMethodHandle = ((methodHandle == null) ? new MWMethodHandle(this, scrubber) : methodHandle.setScrubber(scrubber));
	}
	
	@Override
	public boolean isNull() {
		return false;
	}
	
	@Override
	public boolean isMethods() {
		return true;
	}
	
	@Override
	public boolean isAttribute() {
		return false;
	}

}
