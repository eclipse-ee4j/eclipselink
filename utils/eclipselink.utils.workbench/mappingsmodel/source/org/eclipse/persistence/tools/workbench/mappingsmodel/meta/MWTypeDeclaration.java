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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

/**
 * This class provides the type information for a variable
 * declaration (instance variable, method parameter, 
 * or method return type):
 *     the type of the variable
 *     the "dimensionality" of the variable
 *         non-arrayed variables have a dimensionality of 0
 *         arrays have a dimensionality of 1 or more
 */
public final class MWTypeDeclaration extends MWModel {

	private MWClassHandle typeHandle;
	private volatile int dimensionality;


	private static final String BRACKETS = "[]";


	// ********** constructors/initialization **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWTypeDeclaration() {
		super();
	}

	public MWTypeDeclaration(MWModel parent) {
		super(parent);
	}

	public MWTypeDeclaration(MWModel parent, MWClass type) {
		this(parent, type, 0);
	}

	public MWTypeDeclaration(MWModel parent, MWClass type, int dimensionality) {
		this(parent);
		this.setType(type);
		this.setDimensionality(dimensionality);
	}

	MWTypeDeclaration(MWModel parent, ExternalClassDescription externalClassDescription) {
		this(parent);
		this.refresh(externalClassDescription);
	}

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.typeHandle = new MWClassHandle(this, this.buildTypeScrubber());
	}


	// ********** accessors **********

	public MWClass getType() {
		return this.typeHandle.getType();
	}

	public void setType(MWClass type) {
		if (type == null) {
			throw new NullPointerException();
		}
		this.typeHandle.setType(type);
	}

	public int getDimensionality() {
		return this.dimensionality;
	}

	// TODO create an IllegalDimensionException
	// JSpinner catches the IllegalArgumentException and beeps at you ... doh 
	public void setDimensionality(int dimensionality) {
		if ((dimensionality != 0) && ( ! this.allowsDimension())) {
			throw new IllegalArgumentException("The type declaration 'void' cannot have a dimension: " + new Integer(dimensionality));
		}
		this.dimensionality = dimensionality;
	}


	// ********** queries **********

	public String typeName() {
		return this.getType().getName();
	}

	public String shortTypeName() {
		return this.getType().shortName();
	}

	boolean matches(MWTypeDeclaration other) {
		return (this.dimensionality == other.getDimensionality())
			&& (this.getType() == other.getType());
	}

	boolean hasSameSignatureAs(ExternalClassDescription externalClassDescription) {
		return (this.dimensionality == externalClassDescription.getArrayDepth())
				&& (this.getType() == this.typeNamed(externalClassDescription.getElementTypeName()));
	}

	boolean hasSameSignatureAs(MWMethodParameter methodParameter) {
		return (this.dimensionality == methodParameter.getDimensionality())
			&& (this.getType() == methodParameter.getType());
	}

	boolean isArray() {
		return ! this.isScalar();
	}

	boolean isScalar() {
		return this.dimensionality == 0;
	}

	boolean isVoid() {
		return (this.dimensionality == 0)
				&& this.getType().isVoid();
	}

	boolean isValueHolder() {
		return (this.dimensionality == 0)
				&& this.getType().isValueHolder();
	}
	
	boolean isTLValueHolder() {
		return (this.dimensionality == 0)
			&& "oracle.toplink.indirection.ValueHolderInterface".equals(this.getType().fullName());
	}

	boolean isBooleanPrimitive() {
		return (this.dimensionality == 0)
				&& this.getType().isBooleanPrimitive();
	}

	/**
	 * return whether the type declaration may be assigned a non-zero
	 * dimension - the return type declaration 'void' cannot
	 */
	boolean allowsDimension() {
		return ! this.getType().isVoid();
	}

	/**
	 * Return whether a variable with this type declaration
	 * can be assigned a value with the specified non-array type.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	boolean isAssignableFrom(MWClass otherType) {
		return this.isAssignableFrom(otherType, 0);
	}

	boolean mightBeAssignableFrom(MWClass otherType) {
		return this.mightBeAssignableFrom(otherType, 0);
	}

	/**
	 * Return whether a variable with this type declaration
	 * can be assigned a value with the specified type declaration.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	boolean isAssignableFrom(MWClass otherType, int otherDimensionality) {
		return this.targetIsAssignableFromSource(this.getType(), this.dimensionality, otherType, otherDimensionality);
	}

	boolean mightBeAssignableFrom(MWClass otherType, int otherDimensionality) {
		return this.targetMightBeAssignableFromSource(this.getType(), this.dimensionality, otherType, otherDimensionality);
	}

	/**
	 * Return whether a variable with this type declaration
	 * can be assigned a value with the specified type declaration.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	boolean isAssignableFrom(MWTypeDeclaration other) {
		return this.isAssignableFrom(other.getType(), other.getDimensionality());
	}

	boolean mightBeAssignableFrom(MWTypeDeclaration other) {
		return this.mightBeAssignableFrom(other.getType(), other.getDimensionality());
	}

	/**
	 * Return whether a value with this type declaration
	 * can be assigned to a variable with the specified non-array type declaration.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	boolean isAssignableTo(MWClass otherType) {
		return this.isAssignableTo(otherType, 0);
	}

	boolean mightBeAssignableTo(MWClass otherType) {
		return this.mightBeAssignableTo(otherType, 0);
	}

	/**
	 * Return whether a value with this type declaration
	 * can be assigned to a variable with the specified type declaration.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	boolean isAssignableTo(MWClass otherType, int otherDimensionality) {
		return this.targetIsAssignableFromSource(otherType, otherDimensionality, this.getType(), this.dimensionality);
	}

	boolean mightBeAssignableTo(MWClass otherType, int otherDimensionality) {
		return this.targetMightBeAssignableFromSource(otherType, otherDimensionality, this.getType(), this.dimensionality);
	}

	/**
	 * Return whether a value with this type declaration
	 * can be assigned to a variable with the specified type declaration.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	boolean isAssignableTo(MWTypeDeclaration other) {
		return this.isAssignableTo(other.getType(), other.getDimensionality());
	}

	boolean mightBeAssignableTo(MWTypeDeclaration other) {
		return this.mightBeAssignableTo(other.getType(), other.getDimensionality());
	}

	private boolean targetIsAssignableFromSource(MWClass targetType, int targetDimensionality, MWClass sourceType, int sourceDimensionality) {
		// e.g. Object[][] := String[][]
		if (targetDimensionality == sourceDimensionality) {
			// delegate to type
			return targetType.isAssignableFrom(sourceType);
		}
		return this.targetIsAssignableFromSource2(targetType, targetDimensionality, sourceType, sourceDimensionality);
	}

	private boolean targetIsAssignableFromSource2(MWClass targetType, int targetDimensionality, MWClass sourceType, int sourceDimensionality) {
		// e.g. Object[][] := String[]
		if (targetDimensionality > sourceDimensionality) {
			// an array cannot be expanded into a larger-dimensioned array
			return false;
		}

		// targetDimensionality < sourceDimensionality
		// all arrays are assignable to three, pre-defined types
		// e.g. Object[] := String[][]
		// (which is reduced to: Object := String[] )
		return targetType.isObject()
				|| targetType.isCloneable()
				|| targetType.isSerializable();
	}

	private boolean targetMightBeAssignableFromSource(MWClass targetType, int targetDimensionality, MWClass sourceType, int sourceDimensionality) {
		// e.g. Object[][] := String[][]
		if (targetDimensionality == sourceDimensionality) {
			// delegate to type
			return targetType.mightBeAssignableFrom(sourceType);
		}
		return this.targetIsAssignableFromSource2(targetType, targetDimensionality, sourceType, sourceDimensionality);
	}

	boolean isAssignableToCollection() {
		return (this.dimensionality == 0)
				&& this.getType().isAssignableToCollection();
	}

	boolean mightBeAssignableToCollection() {
		return (this.dimensionality == 0)
				&& this.getType().mightBeAssignableToCollection();
	}

	boolean isAssignableToList() {
		return (this.dimensionality == 0)
				&& this.getType().isAssignableToList();
	}

	boolean mightBeAssignableToList() {
		return (this.dimensionality == 0)
				&& this.getType().mightBeAssignableToList();
	}

	boolean isAssignableToSet() {
		return (this.dimensionality == 0)
				&& this.getType().isAssignableToSet();
	}

	boolean mightBeAssignableToSet() {
		return (this.dimensionality == 0)
				&& this.getType().mightBeAssignableToSet();
	}

	boolean isAssignableToMap() {
		return (this.dimensionality == 0)
				&& this.getType().isAssignableToMap();
	}

	boolean mightBeAssignableToMap() {
		return (this.dimensionality == 0)
				&& this.getType().mightBeAssignableToMap();
	}

	boolean isAssignableToIndirectContainer() {
		return (this.dimensionality == 0)
				&& this.getType().isAssignableToIndirectContainer();
	}

	boolean mightBeAssignableToIndirectContainer() {
		return (this.dimensionality == 0)
				&& this.getType().mightBeAssignableToIndirectContainer();
	}


	// ********** behavior **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.typeHandle);
	}

	private NodeReferenceScrubber buildTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWTypeDeclaration.this.setType(null);
			}
			public String toString() {
				return "MWTypeDeclaration.buildTypeScrubber()";
			}
		};
	}

	void refresh(ExternalClassDescription externalClassDescription) {
		// set the type first, since allowable dimensions is determined by type
		this.setType(this.typeNamed(externalClassDescription.getElementTypeName()));
		this.setDimensionality(externalClassDescription.getArrayDepth());
	}


	// ********** displaying and printing **********

	/**
	 * e.g. "java.lang.Object[]"
	 */
	public String displayString() {
		int dim = this.dimensionality;
		if (dim == 0) {
			return this.typeName();
		}
		String typeName = this.typeName();
		StringBuffer sb = new StringBuffer(typeName.length() + (dim << 1));
		sb.append(typeName);
		for (int i = dim; i-- > 0; ) {
			sb.append(BRACKETS);
		}
		return sb.toString();
	}

	/**
	 * e.g. "Object[] (java.lang)"
	 */
	public String displayStringWithPackage() {
		int dim = this.dimensionality;
		if (dim == 0) {
			return this.getType().displayStringWithPackage();
		}
		StringBuffer sb = new StringBuffer(200);
		sb.append(this.shortTypeName());
		for (int i = dim; i-- > 0; ) {
			sb.append(BRACKETS);
		}
		if (this.getType().isReferenceType()) {
			sb.append(" (");
			sb.append(this.getType().packageDisplayName());
			sb.append(')');
		}
		return sb.toString();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.displayString());
	}

	void printDeclarationOn(StringBuffer sb) {
		sb.append(this.typeName());
		this.printBracketsOn(sb);
	}

	void printShortDeclarationOn(StringBuffer sb) {
		sb.append(this.shortTypeName());
		this.printBracketsOn(sb);
	}

	String declaration() {
		StringBuffer sb = new StringBuffer(200);
		this.printDeclarationOn(sb);
		return sb.toString();
	}

	void printSignatureOn(StringBuffer sb) {
		this.printDeclarationOn(sb);
	}

	void printShortSignatureOn(StringBuffer sb) {
		this.printShortDeclarationOn(sb);
	}

	String defaultReturnValueString() {
		StringBuffer sb = new StringBuffer(200);
		this.printDefaultReturnValueOn(sb);
		return sb.toString();
	}

	void printDefaultReturnValueOn(StringBuffer sb) {
		if (this.isArray()) {
			sb.append("null");
		} else {
			this.getType().printDefaultReturnValueOn(sb);
		}
	}

	private void printBracketsOn(StringBuffer sb) {
		for (int i = this.dimensionality; i-- > 0; ) {
			sb.append(BRACKETS);
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWTypeDeclaration.class);

		// 'type' will never be null
		XMLCompositeObjectMapping typeHandleMapping = new XMLCompositeObjectMapping();
		typeHandleMapping.setAttributeName("typeHandle");
		typeHandleMapping.setReferenceClass(MWClassHandle.class);
		typeHandleMapping.setGetMethodName("getTypeHandleForTopLink");
		typeHandleMapping.setSetMethodName("setTypeHandleForTopLink");
		typeHandleMapping.setXPath("type-handle");
		descriptor.addMapping(typeHandleMapping);

		XMLDirectMapping dimMapping = (XMLDirectMapping) descriptor.addDirectMapping("dimensionality", "dimensionality/text()");
		dimMapping.setNullValue(new Integer(0));

		return descriptor;
	}

	/**
	 * check for null
	 */
	private MWClassHandle getTypeHandleForTopLink() {
		return (this.typeHandle.getType() == null) ? null : this.typeHandle;
	}
	private void setTypeHandleForTopLink(MWClassHandle typeHandle) {
		NodeReferenceScrubber scrubber = this.buildTypeScrubber();
		this.typeHandle = ((typeHandle == null) ? new MWClassHandle(this, scrubber) : typeHandle.setScrubber(scrubber));
	}

	// ********** legacy TopLink 6.0 methods **********
	
	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();

		descriptor.setJavaClass(MWTypeDeclaration.class);

		// 'type' will never be null
		XMLCompositeObjectMapping typeHandleMapping = new XMLCompositeObjectMapping();
		typeHandleMapping.setAttributeName("typeHandle");
		typeHandleMapping.setReferenceClass(MWClassHandle.class);
		typeHandleMapping.setGetMethodName("getTypeHandleForTopLink");
		typeHandleMapping.setSetMethodName("setTypeHandleForTopLink");
		typeHandleMapping.setXPath("type-handle");
		descriptor.addMapping(typeHandleMapping);

		XMLDirectMapping dimMapping = (XMLDirectMapping) descriptor.addDirectMapping("dimensionality", "dimensionality/text()");
		dimMapping.setNullValue(new Integer(0));

		return descriptor;
	}
	
	// ********** legacy TopLink 5.0 methods **********

	private void legacy50SetTypeNameForTopLink(String typeName) {
		this.typeHandle = new MWClassHandle(this, this.buildTypeScrubber());
		if (ClassTools.classNamedIsArray(typeName)) {
			this.dimensionality = ClassTools.arrayDepthForClassNamed(typeName);
			typeName = ClassTools.elementTypeNameForClassNamed(typeName);
		}
		this.typeHandle.legacySetTypeNameForTopLink(typeName);
	}
	/**
	 * necessary because writes are performed during the read
	 * on class stuff in 5.0 class override code
	 */
	private String legacyGetTypeNameForTopLink() {
		return this.typeHandle.legacyGetTypeNameForTopLink();
	}

}
