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

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;

/**
 * This class models a Java method or constructor.
 */
public final class MWMethod 
	extends MWModel implements MWModifiable
{
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	private MWModifier modifier;		// pseudo-final

	/**
	 * the "signature" property is a virtual property that clients can listen to;
	 * those clients will be notified when the method's "extended" signature changes
	 */
	public static final String SIGNATURE_PROPERTY = "signature";
	
	private MWTypeDeclaration returnTypeDeclaration;
		public static final String RETURN_TYPE_PROPERTY = "returnType";
		public static final String RETURN_TYPE_DIMENSIONALITY_PROPERTY = "returnTypeDimensionality";

	private List methodParameters;
		public static final String METHOD_PARAMETERS_LIST = "methodParameters";

	private Collection exceptionTypeHandles;
		public static final String EXCEPTION_TYPES_COLLECTION = "exceptionTypes";
		private NodeReferenceScrubber exceptionTypeScrubber;

	/** this differentiates methods and constructors */
	private volatile boolean constructor;
		public static final String CONSTRUCTOR_PROPERTY = "constructor";

	/** a method can be an "accessor" for an attribute (e.g. getFoo(), setFoo(Foo), addFoo(Foo)) */
	private MWAttributeHandle accessedAttributeHandle;

	private static final MWClass[] EMPTY_TYPE_ARRAY = new MWClass[0];
	private static final int[] EMPTY_INT_ARRAY = new int[0];


	// ********** constructors **********
	
	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWMethod() {
		super();
	}
	
	MWMethod(MWClass declaringType, String name) {
		super(declaringType);
		this.name = name;
		this.setConstructor(name.equals(this.getDeclaringType().shortName()));
	}
	
	MWMethod(MWClass declaringType, String name, MWClass returnType) {
		this(declaringType, name);
		this.setReturnType(returnType);
	}
	
	MWMethod(MWClass declaringType, String name, MWClass returnType, int dimensionality) {
		this(declaringType, name);
		this.setReturnTypeDeclaration(returnType, dimensionality);
	}
	
	MWMethod(MWClass declaringType, ExternalConstructor externalConstructor) {
		// Java constructors return their names fully-qualified, despite what the JavaDocs say
		this(declaringType, shortNameFor(externalConstructor));
		this.setConstructor(true);
		this.initializeParameterTypeDeclarations(externalConstructor.getParameterTypes());
		this.refresh(externalConstructor);
	}

	MWMethod(MWClass declaringType, ExternalMethod externalMethod) {
		this(declaringType, externalMethod.getName());
		this.setConstructor(false);
		this.initializeParameterTypeDeclarations(externalMethod.getParameterTypes());
		this.refresh(externalMethod);
	}
	
	private static String shortNameFor(ExternalConstructor externalConstructor) {
		return ClassTools.shortNameForClassNamed(externalConstructor.getName());
	}
	
	/**
	 * public DeclaringType()
	 */
	static MWMethod buildZeroArgumentConstructor(MWClass declaringType) {
		MWMethod ctor = new MWMethod(declaringType, declaringType.shortName());
		ctor.setConstructor(true);
		return ctor;
	}


	// ********** initialization **********

	/**
	 * initialize transient state
	 */
	protected void initialize() {
		super.initialize();
		this.modifier = new MWModifier(this);
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.returnTypeDeclaration = this.buildDefaultReturnTypeDeclaration();
		this.methodParameters = new Vector();
		this.exceptionTypeHandles = new Vector(); 
		this.accessedAttributeHandle = new MWAttributeHandle(this, this.buildAccessedAttributeScrubber());
	}

	private void initializeParameterTypeDeclarations(ExternalClassDescription[] externalParameters) {
		int len = externalParameters.length;
		for (int i = 0; i < len; i++) {
			this.methodParameters.add(this.buildMethodParameter(externalParameters[i]));
		}
	}


	// ********** accessors **********

	public MWClass getDeclaringType() {
		return (MWClass) this.getParent();
	}


	// ***** name
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		
		if (this.attributeValueHasChanged(old, name)) {
			this.setConstructor(name.equals(this.getDeclaringType().shortName()));
			this.getProject().nodeRenamed(this);
			this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "name");	// don't waste time building the signature - it's ignored
		}
	}


	// ***** modifier
	public MWModifier getModifier() {
		return this.modifier;
	}


	// ***** returnTypeDeclaration
	MWTypeDeclaration getReturnTypeDeclaration() {
		return this.returnTypeDeclaration;
	}
	
	public MWClass getReturnType() {
		if (this.isConstructor()) {
			throw new IllegalStateException("A constructor \"" + this + "\" does not have a return type.");
		}
		return this.returnTypeDeclaration.getType();
	}
	
	public int getReturnTypeDimensionality() {
		if (this.isConstructor()) {
			throw new IllegalStateException("A constructor \"" + this + "\" does not have a return type.");
		}
		return this.returnTypeDeclaration.getDimensionality();
	}
	
	/**
	 * private - for internal use only;
	 * used when converting a regular method to a constructor and vice versa
	 */
	private void setReturnTypeDeclaration(MWTypeDeclaration returnTypeDeclaration) {
		this.returnTypeDeclaration = returnTypeDeclaration;
	}
	
	public void setReturnType(MWClass returnType) {
		Object old = this.getReturnType();
		this.returnTypeDeclaration.setType(returnType);
		this.firePropertyChanged(RETURN_TYPE_PROPERTY, old, returnType);
        if (attributeValueHasChanged(old, returnType)) {
    		if (returnType.isVoid()) {
				this.setReturnTypeDimensionality(0);
    		}
			this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "returnType");	// don't waste time building the signature - it's ignored
        }
	}
	
	private void setReturnTypeDeclaration(MWClass returnType, int dimensionality) {
		this.setReturnType(returnType);
		this.setReturnTypeDimensionality(dimensionality);
	}
	
	public void setReturnTypeDimensionality(int dimensionality) {
		int old = this.getReturnTypeDimensionality();
		this.returnTypeDeclaration.setDimensionality(dimensionality);
		this.firePropertyChanged(RETURN_TYPE_DIMENSIONALITY_PROPERTY, old, dimensionality);
        if (old != dimensionality) {
			this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "returnTypeDimensionality");	// don't waste time building the signature - it's ignored
        }
	}
	
	
	// ***** methodParameters
	public ListIterator methodParameters() {
		return new CloneListIterator(this.methodParameters) {
			protected void remove(int index) {
				MWMethod.this.removeMethodParameter(index);
			}
			public String toString() {
				return "MWMethod.methodParameters()";
			}
		};
	}
	
	public int methodParametersSize() {
		return this.methodParameters.size();
	}
	
	public MWMethodParameter getMethodParameter(int index) {
		return (MWMethodParameter) this.methodParameters.get(index);
	}
	
	/**
	 * return the first parameter type declaration;
	 * useful for when there is only a single parameter
	 */
	public MWMethodParameter getMethodParameter() {
		if (this.methodParametersSize() != 1) {
			throw new IllegalStateException("This method \"" + this + "\" contains more than one parameter type declaration.");
		}
		return this.getMethodParameter(0);
	}
	
	public MWMethodParameter addMethodParameter(MWClass parameterType) {
		return this.addMethodParameter(parameterType, 0);
	}
	
	public MWMethodParameter addMethodParameter(MWClass parameterType, int parameterDimensionality) {
		return this.addMethodParameter(this.buildMethodParameter(parameterType, parameterDimensionality));
	}
	
	private MWMethodParameter addMethodParameter(MWMethodParameter methodParameter) {
		if (methodParameter.getType().isVoid()) {
			throw new IllegalArgumentException("Cannot add a parameter with type void");
		}
		this.addItemToList(methodParameter, this.methodParameters, METHOD_PARAMETERS_LIST);
		this.parameterChanged();
		return methodParameter;
	}
	
	public void removeMethodParameter(int index) {
		this.removeMethodParameter(this.getMethodParameter(index));
	}
	
	public void removeMethodParameter(MWMethodParameter methodParameter) {
		this.removeItemFromList(methodParameter, this.methodParameters, METHOD_PARAMETERS_LIST);
		this.parameterChanged();
	}
    
    public void removeMethodParameters(Iterator params) {
        while (params.hasNext()) {
            this.removeMethodParameter((MWMethodParameter) params.next());
        }
    }

    public void removeMethodParameters(Collection params) {
        this.removeMethodParameters(params.iterator());
    }
    
	public void clearMethodParameters() {
		this.removeMethodParameters(this.methodParameters());
	}

	void parameterChanged() {
		this.getProject().nodeRenamed(this);
		this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "methodParameters");	// don't waste time building the signature - it's ignored
	}


	// ***** exceptionTypes
	private Iterator exceptionTypeHandles() {
		return new CloneIterator(this.exceptionTypeHandles) {
			protected void remove(Object current) {
				MWMethod.this.removeExceptionTypeHandle((MWClassHandle) current);
			}
			public String toString() {
				return "MWMethod.exceptionTypeHandles()";
			}
		};
	}

	void removeExceptionTypeHandle(MWClassHandle handle) {
		this.exceptionTypeHandles.remove(handle);
		this.fireItemRemoved(EXCEPTION_TYPES_COLLECTION, handle.getType());
	}

	public Iterator exceptionTypes() {
		return new TransformationIterator(this.exceptionTypeHandles()) {
			protected Object transform(Object next) {
				return ((MWClassHandle) next).getType();
			}
			public String toString() {
				return "MWMethod.exceptionTypes()";
			}
		};
	}
	
	public int exceptionTypesSize() {
		return this.exceptionTypeHandles.size();
	}

	public boolean containsExceptionType(MWClass exceptionType) {
		synchronized (this.exceptionTypeHandles) {
			for (Iterator stream = this.exceptionTypeHandles.iterator(); stream.hasNext(); ) {
				if (((MWClassHandle) stream.next()).getType() == exceptionType) {
					return true;
				}
			}
			return false;
		}
	}

	public void addExceptionType(MWClass exceptionType) {
		// check to make sure that the exception isn't in there already
		if ( ! this.containsExceptionType(exceptionType)) {
			this.exceptionTypeHandles.add(new MWClassHandle(this, exceptionType, this.exceptionTypeScrubber()));
			this.fireItemAdded(EXCEPTION_TYPES_COLLECTION, exceptionType);
		}
	}
	
	public void addExceptionTypes(Collection exceptionTypes) {
		this.addExceptionTypes(exceptionTypes.iterator());
	}
	
	public void addExceptionTypes(Iterator exceptionTypes) {
		while (exceptionTypes.hasNext()) {
			this.addExceptionType((MWClass) exceptionTypes.next());
		}
	}
	
	public void removeExceptionType(MWClass exceptionType) {
		for (Iterator stream = this.exceptionTypes(); stream.hasNext(); ) {
			if (stream.next() == exceptionType) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(exceptionType.toString());
	}
	
	public void removeExceptionTypes(Collection exceptionTypes) {
		this.removeExceptionTypes(exceptionTypes.iterator());
	}
	
	public void removeExceptionTypes(Iterator exceptionTypes) {
		while (exceptionTypes.hasNext()) {
			this.removeExceptionType((MWClass) exceptionTypes.next());
		}
	}
	
	public void clearExceptionTypes() {
		// use #exceptionTypeHandles() for minor performance tweak:
		for (Iterator stream = this.exceptionTypeHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}
	
	
	// ***** constructor
	public boolean isConstructor() {
		return this.constructor;
	}
	
	public void setConstructor(boolean constructor) {
		boolean old = this.constructor;
		this.constructor = constructor;
		this.firePropertyChanged(CONSTRUCTOR_PROPERTY, old, constructor);
	
		// we need to tweak the return type declaration when changing...
		if (old != constructor) {
			if (constructor) {
				this.setReturnTypeDeclaration(null);
				this.getModifier().setAbstract(false);
				this.getModifier().setFinal(false);
				this.getModifier().setSynchronized(false);
				this.getModifier().setStatic(false);
				this.getModifier().setNative(false);
			} else {
				this.setReturnTypeDeclaration(this.buildDefaultReturnTypeDeclaration());
			}
			// constructors and non-constructors allow different sets of modifiers
			this.allowedModifiersChanged();
			this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "constructor");	// don't waste time building the signature - it's ignored
		}
	}

	
	// ***** accessedAttribute
	MWClassAttribute getAccessedAttribute() {
		return this.accessedAttributeHandle.getAttribute();
	}

	void setAccessedAttribute(MWClassAttribute attribute) {
		if (this.getAccessedAttribute() != null) {
			this.getAccessedAttribute().removeAccessorMethod(this);
		}
		this.accessedAttributeHandle.setAttribute(attribute);
	}	


	// ********** Modifiable implementation **********

	public boolean supportsAbstract() {
		return true;
	}
	
	public boolean canBeSetAbstract() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.getModifier().isPrivate()) {
			return false;
		}
		if (this.getModifier().isStatic()) {
			return false;
		}
		if (this.isFinal()) {
			return false;
		}
		if (this.getModifier().isNative()) {
			return false;
		}
		if (this.isStrict()) {
			return false;
		}
		if (this.getModifier().isSynchronized()) {
			return false;
		}
		return this.getDeclaringType().isAbstract();
	}
	
	public boolean canBeSetFinal() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.isAbstract()) {
			return false;
		}
		return true;
	}
	
	public boolean supportsInterface() {
		return false;
	}
	
	public boolean canBeSetInterface() {
		return false;
	}
	
	public boolean supportsNative() {
		return true;
	}
	
	public boolean canBeSetNative() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.isAbstract()) {
			return false;
		}
		if (this.isStrict()) {
			return false;
		}
		return true;
	}
	
	public boolean canBeSetPackage() {
		return true;
	}
	
	public boolean canBeSetPrivate() {
		return ! this.isAbstract();
	}
	
	public boolean canBeSetProtected() {
		return true;
	}
	
	public boolean canBeSetPublic() {
		return true;
	}
	
	public boolean canBeSetStatic() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.isAbstract()) {
			return false;
		}
		return true;
	}
	
	public boolean supportsStrict() {
		return true;
	}
	
	public boolean canBeSetStrict() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.isAbstract()) {
			return false;
		}
		if (this.getModifier().isNative()) {
			return false;
		}
		return true;
	}
	
	public boolean supportsSynchronized() {
		return true;
	}
	
	public boolean canBeSetSynchronized() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.isAbstract()) {
			return false;
		}
		return true;
	}
	
	public boolean supportsTransient() {
		return false;
	}
	
	public boolean canBeSetTransient() {
		return false;
	}
	
	public boolean supportsVolatile() {
		return false;
	}
	
	public boolean canBeSetVolatile() {
		return false;
	}
	
	/**
	 * if any of these modifiers change for the method, the method's
	 * "allowed" modifiers may have changed (e.g. if the method is
	 * no longer 'abstract', it can now be 'final').
	 */
	private static final int ALLOWED_MODIFIERS_FLAGS =
			Modifier.ABSTRACT |
			Modifier.FINAL |
			Modifier.NATIVE |
			Modifier.PRIVATE |
			Modifier.STATIC |
			Modifier.STRICT |
			Modifier.SYNCHRONIZED;

	public void modifierChanged(int oldCode, int newCode) {
		this.firePropertyChanged(MODIFIER_CODE_PROPERTY, oldCode, newCode);
		this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "modifier");	// don't waste time building the signature - it's ignored
		if (MWModifier.anyFlagsAreDifferent(ALLOWED_MODIFIERS_FLAGS, oldCode, newCode)) {
			this.allowedModifiersChanged();
		}
	}
	
	/**
	 * the method's "allowed" modifiers
	 */
	void allowedModifiersChanged() {
		this.modifier.allowedModifiersChanged();
	}

	public void accessLevelChanged(String oldValue, String newValue) {
		this.firePropertyChanged(MODIFIER_ACCESS_LEVEL_PROPERTY, oldValue, newValue);
	}


	// ********** queries **********

	boolean hasSameSignatureAs(ExternalConstructor externalConstructor) {
		if ( ! this.isConstructor()) {
			return false;
		}
		// Java constructors return a fully-qualified name(!)
		String javaName = shortNameFor(externalConstructor);
		return this.hasSameSignatureAs(javaName, externalConstructor.getParameterTypes());
	}
	
	boolean hasSameSignatureAs(ExternalMethod externalMethod) {
		if (this.isConstructor()) {
			return false;
		}
		return this.hasSameSignatureAs(externalMethod.getName(), externalMethod.getParameterTypes());
	}
	
	private boolean hasSameSignatureAs(String javaName, ExternalClassDescription[] externalParameters) {
		if ( ! this.getName().equals(javaName)) {
			return false;
		}
		int externalParametersLength = externalParameters.length;
		if (this.methodParametersSize() != externalParametersLength) {
			return false;
		}
		for (int i = 0; i < externalParametersLength; i++) {
			if ( ! this.getMethodParameter(i).hasSameSignatureAs(externalParameters[i])) {
				return false;
			}
		}
		return true;
	}
	
	public boolean hasSameSignatureAs(MWMethod method) {
		if ( ! this.getName().equals(method.getName())) {
			return false;
		}
		int methodParametersSize = method.methodParametersSize();
		if (this.methodParametersSize() != methodParametersSize) {
			return false;
		}
		for (int i = 0; i < methodParametersSize; i++) {
			if ( ! this.getMethodParameter(i).hasSameSignatureAs(method.getMethodParameter(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * return true if the method has the specified name and zero arguments
	 */
	boolean hasSignature(String methodName) {
		return this.hasSignature(methodName, EMPTY_TYPE_ARRAY, EMPTY_INT_ARRAY);
	}
	
	/**
	 * return true if the method has the specified name and a single argument
	 * that matches the specified type and dimensionality;
	 * the argument must be an *exact* match
	 */
	boolean hasSignature(String methodName, MWClass type, int dimensionality) {
		return this.hasSignature(methodName, new MWClass[] {type}, new int[] {dimensionality});
	}
	
	/**
	 * return true if the method has the specified name and two arguments
	 * that match the specified types and dimensionalities in the given order;
	 * the arguments must be *exact* matches
	 */
	boolean hasSignature(String methodName, MWClass type1, int dimensionality1, MWClass type2, int dimensionality2) {
		return this.hasSignature(methodName, new MWClass[] {type1, type2}, new int[] {dimensionality1, dimensionality2});
	}

	/**
	 * return true if the method has the specified name and arguments
	 * that match the specified types and dimensionalities in the given order;
	 * the arguments must be *exact* matches
	 */
	boolean hasSignature(String methodName, MWClass[] types, int[] dimensionalities) {
		if (types.length != dimensionalities.length) {
			throw new IllegalArgumentException("arrays are different lengths: " + types.length + " vs. " + dimensionalities.length);
		}
		if ( ! this.getName().equals(methodName)) {
			return false;
		}
		if (this.methodParametersSize() != types.length) {
			return false;
		}
		for (int i = types.length; i-- > 0; ) {
			MWMethodParameter parm = this.getMethodParameter(i);
			if (parm.getType() != types[i]) {
				return false;
			}
			if (parm.getDimensionality() != dimensionalities[i]) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * interface methods are implied to be abstract...
	 */
	public boolean isAbstract() {
		return this.getModifier().isAbstract() || this.getDeclaringType().isInterface();
	}
	
	public boolean isStatic() {
		return this.getModifier().isStatic();
	}
	
	/**
	 * private methods are implied to be final...
	 * methods for final classes are implied to be final...
	 */
	public boolean isFinal() {
		return this.getModifier().isFinal()
			|| this.getModifier().isPrivate()
			|| this.getDeclaringType().getModifier().isFinal();
	}
	
	/**
	 * methods for strictfp classes are implied to be strictfp...
	 */
	public boolean isStrict() {
		return this.getModifier().isStrict()
			|| this.getDeclaringType().isStrict();
	}
	
	public boolean isInstanceMethod() {
		return ( ! this.isConstructor())
			&& ( ! this.isStatic());
	}
	
	public boolean returnTypeIsVoid() {
		return this.returnTypeDeclaration.isVoid();
	}

	public boolean returnTypeIsArray() {
		return this.returnTypeDeclaration.isArray();
	}

	public boolean throwsException(MWClass exceptionType) {
		return this.containsExceptionType(exceptionType);
	}

	/**
	 * return whether the method bears a passing resemblance to a getter:
	 */
	private boolean looksLikeAGetMethod() {
		return this.isInstanceMethod()
			&& ( ! this.returnTypeIsVoid())
			&& (this.methodParametersSize() == 0)
			&& this.getName().startsWith("get");
	}
	
	/**
	 * return whether the method looks like a standard getter and it is abstract
	 */
	public boolean isEjb20GetMethod() {
		return this.looksLikeAGetMethod()
			&& this.isAbstract();
	}

	/**
	 * return whether the method bears a passing resemblance to a getter
	 */
	private boolean looksLikeASetMethod() {
		return this.isInstanceMethod()
			&& this.returnTypeIsVoid()
			&& (this.methodParametersSize() == 1)
			&& this.getName().startsWith("set");
	}
	
	/**
	 * return whether the method looks like a standard setter and
	 * is abstract and is named similarly to the specified get method
	 */
	boolean isEjb20SetMethodFor(MWMethod ejb20GetMethod) {
		return this.looksLikeASetMethod()
			&& this.isAbstract()
			&& this.getName().endsWith(ejb20GetMethod.getName().substring(3))
			&& (this.methodParametersSize() == 1)
			&& this.getMethodParameter(0).matches(ejb20GetMethod.returnTypeDeclaration);
	}

	/**
	 * get, set, value get, value set, add, or remove method
	 */
	public boolean isAccessor() {
		return this.getAccessedAttribute() != null;
	}
	
	public boolean isZeroArgumentConstructor() {
		return this.isConstructor()
			&& this.methodParametersSize() == 0;
	}
	
	/**
	 * return whether this method can be used by TopLink as a get method
	 *     - must have 0 parameters
	 *     - must return something (not void)
	 */
	boolean isCandidateTopLinkGetMethod() {
		return this.isInstanceMethod()
				&& this.methodParametersSize() == 0
				&& ! this.returnTypeDeclaration.isVoid();
	}
	
	/**
	 * return whether this method can be used by TopLink as a set method
	 *     - must have 1 parameter
	 */
	boolean isCandidateTopLinkSetMethod() {
		return this.isInstanceMethod()
				&& this.methodParametersSize() == 1;
	}

	boolean isCandidateGetMethodFor(MWClassAttribute attribute) {
		return this.isCandidateGetMethodFor(attribute.getType(), attribute.getDimensionality());
	}

	boolean isCandidateGetMethodFor(MWClass attributeType) {
		return this.isCandidateGetMethodFor(attributeType, 0);
	}

	boolean isCandidateGetMethodFor(MWClass attributeType, int attributeDimensionality) {
		return this.isZeroArgumentInstanceMethodWithCompatibleReturnType(attributeType, attributeDimensionality);
	}

	/**
	 * the return type need only be *compatible* with the specified type declaration
	 */
	private boolean isZeroArgumentInstanceMethodWithCompatibleReturnType(MWClass type, int dimensionality) {
		return this.isInstanceMethod()
				&& this.methodParametersSize() == 0
				&& this.returnTypeDeclaration.mightBeAssignableFrom(type, dimensionality);
	}
	
	boolean isCandidateSetMethodFor(MWClassAttribute attribute) {
		return this.isCandidateSetMethodFor(attribute.getType(), attribute.getDimensionality());
	}

	boolean isCandidateSetMethodFor(MWClass attributeType) {
		return this.isCandidateSetMethodFor(attributeType, 0);
	}

	boolean isCandidateSetMethodFor(MWClass attributeType, int attributeDimensionality) {
		return this.isInstanceMethodWithCompatibleArgument(attributeType, attributeDimensionality);
	}

	/**
	 * the argument need only be *compatible* with the specified type declaration
	 */
	private boolean isInstanceMethodWithCompatibleArgument(MWClass type, int dimensionality) {
		return this.isInstanceMethod()
				&& this.methodParametersSize() == 1
				&& this.getMethodParameter().mightBeAssignableFrom(type, dimensionality);
	}
	
	boolean isCandidateAddMethodFor(MWClass itemType) {
		return this.isInstanceMethodWithCompatibleArgument(itemType, 0);
	}
	
	boolean isCandidateAddMethodFor(MWClass keyType, MWClass itemType) {
		return this.isInstanceMethod()
				&& this.methodParametersSize() == 2
				&& this.getMethodParameter(0).mightBeAssignableFrom(keyType, 0)
				&& this.getMethodParameter(1).mightBeAssignableFrom(itemType, 0);
	}
	
	boolean isCandidateRemoveMethodFor(MWClass itemOrKeyType) {
		return this.isInstanceMethodWithCompatibleArgument(itemOrKeyType, 0);
	}

	/**
	 * used by map container policy
	 * @see org.eclipse.persistence.internal.queries.MapContainerPolicy
	 */
	public boolean isCandidateMapContainerPolicyKeyMethod() {
		return this.isInstanceMethod()
			&& (this.methodParametersSize() == 0)
			&& ( ! this.returnTypeDeclaration.isVoid());
	}

	/**
	 * used by inheritance policy;
	 *     [public|protected|private] static Class|Object foo(Record)
	 * @see org.eclipse.persistence.descriptors.MethodClassExtractor
	 */
	public boolean isCandidateClassExtractionMethod() {
		return this.isStatic()
			&& this.methodParametersSize() == 1
			&& this.getMethodParameter().getDimensionality() == 0
			&& ((this.getMethodParameter().getType() == this.typeFor(Record.class)))
			&& this.returnTypeDeclaration.isAssignableFrom(this.typeFor(Class.class));
	}

	/**
	 * used by clone copy policy;
	 * must be a zero-argument instance method with
	 * an appropriate return type;
	 * this method will be invoked on a domain object
	 * @see org.eclipse.persistence.descriptors.copying.CloneCopyPolicy#initialize(org.eclipse.persistence.sessions.Session)
	 */
	public boolean isCandidateCloneMethod() {
		return this.isInstanceMethod()
			&& this.methodParametersSize() == 0
			&& this.returnTypeDeclaration.mightBeAssignableFrom(this.getDeclaringType());
	}
	
	/**
	 * used by events policy;
	 * must be a single-argument public method returning void that takes
	 * a DescriptorEvent as the argument
	 * @see org.eclipse.persistence.descriptors.DescriptorEventManager#findMethod(int)
	 */
	public boolean isCandidateDescriptorEventMethod() {
		return this.isInstanceMethod()
			&& this.methodParametersSize() == 1
			&& ((this.getMethodParameter().getType() == this.typeFor(org.eclipse.persistence.descriptors.DescriptorEvent.class))
						|| (this.getMethodParameter().getType() == this.typeFor(org.eclipse.persistence.sessions.Session.class)));
	}
	
	/**
	 * used by instantiation policy;
	 * must be a static zero-argument method with an appropriate return type;
	 * this method will be invoked on the *factory* class to instantiate a new
	 * factory instance
	 * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy
	 */
	public boolean isCandidateFactoryMethod() {
		return this.isCandidateInstantiationMethod();
	}
	
	/**
	 * used by instantiation policy;
	 * must be a static zero-argument method with an appropriate return type;
	 * this method will be invoked on the *descriptor* class to instantiate a new
	 * domain object
	 * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy
	 */
	public boolean isCandidateInstantiationMethod() {
		return this.isCandidateInstantiationMethodFor(this.getDeclaringType());
	}
	
	/**
	 * used by instantiation policy;
	 * must be a static zero-argument method with an appropriate return type;
	 * this method will be invoked on the specified class to instantiate a new
	 * instance of that class
	 * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy
	 */
	private boolean isCandidateInstantiationMethodFor(MWClass type) {
		return this.isStatic()
			&& this.isCandidateFactoryInstantiationMethodFor(type);
	}
	
	/**
	 * used by instantiation policy;
	 * must be a non-constructor zero-argument method with an appropriate return type;
	 * this method will be invoked on a factory to instantiate a new
	 * domain object
	 * @see org.eclipse.persistence.internal.descriptors.InstantiationPolicy
	 */
	public boolean isCandidateFactoryInstantiationMethodFor(MWClass type) {
		return ( ! this.isConstructor())
			&& this.methodParametersSize() == 0
			&& this.returnTypeDeclaration.mightBeAssignableFrom(type);
	}
	
	/**
	 * used by after load policy;
	 * must be a static single-argument method that takes
	 * a descriptor as the argument
	 * @see org.eclipse.persistence.descriptors.ClassDescriptor#applyAmendmentMethod(org.eclipse.persistence.descriptors.DescriptorEvent)
	 */
	public boolean isCandidateDescriptorAfterLoadMethod() {
		return this.isStatic()
			&& this.methodParametersSize() == 1
			&& this.getMethodParameter().getDimensionality() == 0
			&& ((this.getMethodParameter().getType() == this.typeFor(ClassDescriptor.class)));
	}

	/**
	 * used by transformation mapping;
	 * parms must be exact matches:
	 *     [public|protected|private] [not void] foo(Record)
	 *     [public|protected|private] [not void] foo(Record, org.eclipse.persistence.sessions.Session)
	 * @see org.eclipse.persistence.mappings.transformers.MethodBasedAttributeTransformer#initialize(org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping)
	 */
	public boolean isCandidateAttributeTransformerMethod() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.getReturnType().isVoid()) {
			return false;
		}
		if (this.methodParametersSize() == 1) {
			MWMethodParameter parm = this.getMethodParameter(0);
			if (parm.getDimensionality() == 0) {
				if (parm.getType().isAssignableTo(this.typeFor(Record.class))) {
					return true;
				}
				return false;
			}
			return false;
		}
		if (this.methodParametersSize() == 2) {
			MWMethodParameter parm1 = this.getMethodParameter(0);
			MWMethodParameter parm2 = this.getMethodParameter(1);
			if ((parm1.getDimensionality() == 0) && (parm2.getDimensionality() == 0)) {
				if ((parm1.getType().isAssignableTo(this.typeFor(Record.class))) && (parm2.getType() == this.typeFor(org.eclipse.persistence.sessions.Session.class))) {
					return true;
				}
				return false;
			}
			return false;
		}
		return false;	// wrong number of parameters
	}
	
	/**
	 * used by transformation mapping;
	 * parms must be exact matches:
	 *     [public|protected|private] [not void] foo()
	 *     [public|protected|private] [not void] foo(org.eclipse.persistence.sessions.Session)
	 * @see org.eclipse.persistence.mappings.transformers.MethodBasedFieldTransformer#initialize(org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping)
	 */
	public boolean isCandidateFieldTransformerMethod() {
		if (this.isConstructor()) {
			return false;
		}
		if (this.getReturnType().isVoid()) {
			return false;
		}
		if (this.methodParametersSize() == 0) {
			return true;
		}
		if (this.methodParametersSize() == 1) {
			MWMethodParameter parm = this.getMethodParameter(0);
			if (parm.getDimensionality() == 0) {
				if (parm.getType() == this.typeFor(org.eclipse.persistence.sessions.Session.class)) {
					return true;
				}
				return false;
			}
			return false;
		}
		return false;	// wrong number of parameters
	}
	
	/**
	 * Used for code gen
	 */
	NonreflectiveMethodDefinition methodDefinition(MWMethodCodeGenPolicy codeGenPolicy) {
		NonreflectiveMethodDefinition def = new NonreflectiveMethodDefinition();
		
		def.setAccessLevel(this.getModifier().accessLevel());
		
		def.setIsConstructor(this.isConstructor());
		def.setIsAbstract(this.isAbstract());
		
		if ( ! this.isConstructor()) {
			def.setReturnType(this.returnTypeDeclaration.declaration());
		}
		
		def.setName(this.getName());
		
		codeGenPolicy.insertArguments(def);
		
		for (Iterator stream = this.exceptionTypes(); stream.hasNext(); ) {
			def.addException(((MWClass) stream.next()).getName());
		}
		
		codeGenPolicy.insertMethodBody(def);

		return def;
	}


	// ********** behavior **********
	
	/**
	 * containment hierarchy
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.modifier);
		if ( ! this.isConstructor()) {
			children.add(this.returnTypeDeclaration);
		}
		synchronized (this.methodParameters) { children.addAll(this.methodParameters); }
		children.add(this.accessedAttributeHandle);
		synchronized (this.exceptionTypeHandles) { children.addAll(this.exceptionTypeHandles); }
	}

	private NodeReferenceScrubber buildAccessedAttributeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMethod.this.setAccessedAttribute(null);
			}
			public String toString() {
				return "MWMethod.buildAccessedAttributeScrubber()";
			}
		};
	}

	private NodeReferenceScrubber exceptionTypeScrubber() {
		if (this.exceptionTypeScrubber == null) {
			this.exceptionTypeScrubber = this.buildExceptionTypeScrubber();
		}
		return this.exceptionTypeScrubber;
	}

	private NodeReferenceScrubber buildExceptionTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWMethod.this.removeExceptionTypeHandle((MWClassHandle) handle);
			}
			public String toString() {
				return "MWMethod.buildExceptionTypeScrubber()";
			}
		};
	}

	public void nodeRenamed(Node node) {
		super.nodeRenamed(node);
		if (this.isConstructor()) {
			if (this.getDeclaringType() == node) {
				this.setName(this.getDeclaringType().shortName());
			}
		} else {
			if (this.getReturnType() == node) {
				this.firePropertyChanged(MWMethod.SIGNATURE_PROPERTY, "returnType");	// don't waste time building the signature - it's ignored
			}
		}
	}

	void refresh(ExternalConstructor externalConstructor) {
		if ( ! this.hasSameSignatureAs(externalConstructor)) {
			throw new IllegalArgumentException(externalConstructor.toString());
		}
		this.getModifier().refresh(externalConstructor.getModifiers());
		// a constructor does not have a return type declaration
		this.refreshExceptionTypes(externalConstructor.getExceptionTypes());
	}
	
	void refresh(ExternalMethod externalMethod) {
		if ( ! this.hasSameSignatureAs(externalMethod)) {
			throw new IllegalArgumentException(externalMethod.toString());
		}
		this.getModifier().refresh(externalMethod.getModifiers());
		this.returnTypeDeclaration.refresh(externalMethod.getReturnType());
		this.refreshExceptionTypes(externalMethod.getExceptionTypes());
	}
	
	private void refreshExceptionTypes(ExternalClassDescription[] externalExceptions) {
		Collection removedExceptionTypes = CollectionTools.collection(this.exceptionTypes());
		for (int i = externalExceptions.length; i-- > 0; ) {
			if ( ! removedExceptionTypes.remove(this.typeNamed(externalExceptions[i].getName()))) {
				this.addExceptionType(this.typeNamed(externalExceptions[i].getName()));
			}
		}
		this.removeExceptionTypes(removedExceptionTypes);
	}


	// ********** factory methods **********

	private MWMethodParameter buildMethodParameter(ExternalClassDescription externalClassDescription) {
		return new MWMethodParameter(this, externalClassDescription);
	}

	private MWMethodParameter buildMethodParameter(MWClass type, int dimensionality) {
		return new MWMethodParameter(this, type, dimensionality);
	}

	private MWTypeDeclaration buildDefaultReturnTypeDeclaration() {
		return new MWTypeDeclaration(this, this.typeFor(void.class));
	}


	// ********** sorting **********

	/**
	 * override to sort constructors first, then sort by signature
	 * @see MWRPersistentObject.compareTo(Object)
	 */
	public int compareTo(Object object) {
		MWMethod otherMethod = null;
		try {
			otherMethod = (MWMethod) object;
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException("Object \"" + object + "\" must be an instance of MWMethod.");
		}

		if (this.isConstructor() && ( ! otherMethod.isConstructor())) {
			return -1;
		}
		if (( ! this.isConstructor()) && otherMethod.isConstructor()) {
			return 1;
		}
		return super.compareTo(object);
	}


	// ********** displaying and printing **********

	/**
	 * return the fully-qualified signature
	 */
	public String displayString() {
		return this.signature();
	}

	public void toString(StringBuffer sb) {
		sb.append(this.shortSignature());
	}

	public String signature() {
		StringBuffer sb = new StringBuffer(200);
		this.printSignatureOn(sb);
		return sb.toString();
	}

	public String signatureWithReturnType() {
		StringBuffer sb = new StringBuffer(200);
		this.printSignatureWithReturnTypeOn(sb);
		return sb.toString();
	}

	public String shortSignature() {
		StringBuffer sb = new StringBuffer(200);
		this.printShortSignatureOn(sb);
		return sb.toString();
	}

	public String shortSignatureWithReturnType() {
		StringBuffer sb = new StringBuffer(200);
		this.printShortSignatureWithReturnTypeOn(sb);
		return sb.toString();
	}

	public void printSignatureWithReturnTypeOn(StringBuffer sb) {
		this.printSignatureOn(sb);
		this.printReturnTypeOn(sb, true);
	}

	public void printSignatureOn(StringBuffer sb) {
		this.printSignatureOn(sb, true);
	}

	public void printShortSignatureWithReturnTypeOn(StringBuffer sb) {
		this.printShortSignatureOn(sb);
		this.printReturnTypeOn(sb, false);
	}

	public void printShortSignatureOn(StringBuffer sb) {
		this.printSignatureOn(sb, false);
	}

	private void printReturnTypeOn(StringBuffer sb, boolean printFullyQualifiedTypeNames) {
		if ( ! this.isConstructor()) {
			sb.append(" : ");
			if (printFullyQualifiedTypeNames) {
				this.returnTypeDeclaration.printSignatureOn(sb);
			} else {
				this.returnTypeDeclaration.printShortSignatureOn(sb);
			}
		}
	}

	private void printSignatureOn(StringBuffer sb, boolean printFullyQualifiedTypeNames) {
		sb.append(this.name);
		sb.append('(');
		synchronized (this.methodParameters) {
			for (Iterator stream = this.methodParameters.iterator(); stream.hasNext(); ) {
				MWMethodParameter methodParameter = (MWMethodParameter) stream.next();
				if (printFullyQualifiedTypeNames) {
					methodParameter.printSignatureOn(sb);
				} else {
					methodParameter.printShortSignatureOn(sb);
				}
				if (stream.hasNext()) {
					sb.append(", ");
				}
			}
		}
		sb.append(')');
	}
	
	
	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWMethod.class);
	
		descriptor.addDirectMapping("name", "name/text()");
	
		XMLDirectMapping constructorMapping = (XMLDirectMapping) descriptor.addDirectMapping("constructor", "constructor/text()");
		constructorMapping.setNullValue(Boolean.FALSE);
	
		XMLDirectMapping modifierMapping = (XMLDirectMapping) descriptor.addDirectMapping("modifier", "getModifierForTopLink", "setModifierForTopLink", "modifier/text()");
		modifierMapping.setNullValue(new Integer(0));
	
		XMLCompositeObjectMapping returnTypeDeclarationMapping = new XMLCompositeObjectMapping();
		returnTypeDeclarationMapping.setAttributeName("returnTypeDeclaration");
		returnTypeDeclarationMapping.setReferenceClass(MWTypeDeclaration.class);
		returnTypeDeclarationMapping.setXPath("return-type-declaration");
		descriptor.addMapping(returnTypeDeclarationMapping);
	
		XMLCompositeCollectionMapping parameterTypeDeclarationsMapping = new XMLCompositeCollectionMapping();
		parameterTypeDeclarationsMapping.setAttributeName("methodParameters");
		parameterTypeDeclarationsMapping.setReferenceClass(MWMethodParameter.class);
		parameterTypeDeclarationsMapping.setXPath("method-parameters/method-parameter");
		descriptor.addMapping(parameterTypeDeclarationsMapping);
	
		XMLCompositeCollectionMapping exceptionTypeHandlesMapping = new XMLCompositeCollectionMapping();
		exceptionTypeHandlesMapping.setAttributeName("exceptionTypeHandles" );
		exceptionTypeHandlesMapping.setGetMethodName("getExceptionTypeHandlesForTopLink" );
		exceptionTypeHandlesMapping.setSetMethodName("setExceptionTypeHandlesForTopLink" );
		exceptionTypeHandlesMapping.setReferenceClass(MWClassHandle.class);
		exceptionTypeHandlesMapping.setXPath("exception-type-handles/class-handle" );
		descriptor.addMapping(exceptionTypeHandlesMapping);
	
		XMLCompositeObjectMapping accessedAttributeHandleMapping = new XMLCompositeObjectMapping();
		accessedAttributeHandleMapping.setAttributeName("accessedAttributeHandle");
		accessedAttributeHandleMapping.setGetMethodName("getAccessedAttributeHandleForTopLink");
		accessedAttributeHandleMapping.setSetMethodName("setAccessedAttributeHandleForTopLink");
		accessedAttributeHandleMapping.setReferenceClass(MWAttributeHandle.class);
		accessedAttributeHandleMapping.setXPath("accessed-attribute-handle");
		descriptor.addMapping(accessedAttributeHandleMapping);
	
		return descriptor;
	}	

	/**
	 * store the modifier as an int
	 */
	private int getModifierForTopLink() {
		return this.modifier.getCode();
	}
	private void setModifierForTopLink(int code) {
		this.modifier.setCodeForTopLink(code);
	}

	/**
	 * sort the exception type handles for TopLink
	 */
	private Collection getExceptionTypeHandlesForTopLink() {
		synchronized (this.exceptionTypeHandles) {
			return new TreeSet(this.exceptionTypeHandles);
		}
	}
	private void setExceptionTypeHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWClassHandle) stream.next()).setScrubber(this.exceptionTypeScrubber());
		}
		this.exceptionTypeHandles = handles;
	}

	/**
	 * check for null
	 */
	private MWAttributeHandle getAccessedAttributeHandleForTopLink() {
		return (this.accessedAttributeHandle.getAttribute() == null) ? null : this.accessedAttributeHandle;
	}
	private void setAccessedAttributeHandleForTopLink(MWAttributeHandle accessedAttributeHandle) {
		NodeReferenceScrubber scrubber = this.buildAccessedAttributeScrubber();
		this.accessedAttributeHandle = ((accessedAttributeHandle == null) ? new MWAttributeHandle(this, scrubber) : accessedAttributeHandle.setScrubber(scrubber));
	}
	
}
