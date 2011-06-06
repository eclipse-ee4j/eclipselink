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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNominative;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassDescription;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassNotFoundException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalConstructor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalMethod;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.ChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.events.NullChangeNotifier;
import org.eclipse.persistence.tools.workbench.utility.iterators.ChainIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TreeIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.internal.codegen.ClassDefinition;

/**
 * This class models a Java class or interface.
 * See MWClassRepository class comment for lots more information.
 * 
 * @see MWClassRepository
 */
public final class MWClass extends MWModel 
	implements ClassDescription, MWModifiable, MWNominative {
	
	private volatile String name;
		public static final String NAME_PROPERTY = "name";

	// declaringType is null unless this type is a "member" type
	private MWClassHandle declaringTypeHandle;
		public static final String DECLARING_TYPE_PROPERTY = "declaringType";

	private MWModifier modifier;		// pseudo-final

	// convention is to name this flag 'interface', but 'interface' is a Java keyword
	private volatile boolean interfaceFlag;
		public static final String INTERFACE_PROPERTY = "interface";

	private MWClassHandle superclassHandle;
		public static final String SUPERCLASS_PROPERTY = "superclass";
		public static final String SUPERCLASSES_COLLECTION = "superclasses";

	// this will be null if it is not known
	private volatile Date lastRefreshTimestamp;
		public static final String LAST_REFRESH_TIMESTAMP_PROPERTY = "lastRefreshTimestamp";

	private Collection interfaceHandles;
		public static final String INTERFACES_COLLECTION = "interfaces";
		private NodeReferenceScrubber interfaceScrubber;

	private Collection attributes;
		public static final String ATTRIBUTES_COLLECTION = "attributes";

	private Collection ejb20Attributes;
		public static final String EJB20_ATTRIBUTES_COLLECTION = "ejb20Attributes";

	// virtual attribute for unknown primary key mapping
	private volatile MWClassAttribute unknownPrimaryKeyAttribute;
		public static final String UNKNOWN_PK_ATTRIBUTE_PROPERTY = "unknownPKAttribute";

	private Collection methods;
		public final static String METHODS_COLLECTION = "methods";

	private Collection typeHandles;
		public static final String TYPES_COLLECTION = "types";
		private NodeReferenceScrubber typeScrubber;

	// this flag is determined by the class name and cached
	private volatile boolean primitive;
	
	// flag core types (primitives, jdk classes, TopLink classes)
	// since we don't have visibility to the repository during cloning
	// (the backpointer is set during the repository's #postBuild();
	// this is private and should not be needed by anything else...
	private boolean coreType;		// virtually 'final'

	// if the type is a "core" type, this flag will be true until a client
	// faults in the type's members (attributes, constructors, methods, types)
	private volatile boolean partiallyPopulatedCoreType;

	// this flag will be true while we are refreshing a core type
	// so we can suppress change notifications
	private volatile boolean coreTypeRefreshInProgress;

	private static PrimitiveWrapperPair[] primitiveWrapperPairs;
	private static PrimitiveWrapperPair voidPrimitiveWrapperPair;

	public static final String LEGACY_50_STUB_ATTRIBUTE_NAME = "stub";
	public static final Boolean LEGACY_50_STUB_NULL_VALUE = Boolean.FALSE;

	
	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWClass() {
		super();
	}
	
	MWClass(MWClassRepository repository, String name, boolean coreType) {
		super(repository);
		this.name = name;
		this.coreType = coreType;
		this.partiallyPopulatedCoreType = coreType;
	}


	// ********** initialization **********
	
	/**
	 * initialize transient state
	 */
	protected void initialize() {
		super.initialize();
		this.modifier = new MWModifier(this);
		// 'primitive' is transient but calculated from 'name', which is persistent

		// all types start off as "user" types
		this.partiallyPopulatedCoreType = false;

		this.coreTypeRefreshInProgress = false;
	}
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.declaringTypeHandle = new MWClassHandle(this, this.defaultDeclaringType(), this.buildDeclaringTypeScrubber());
	
		this.interfaceFlag = this.defaultInterfaceFlag();

		// if the type is built by hand, this is left null
		this.lastRefreshTimestamp = null;

		this.interfaceHandles = new Vector();
		this.attributes = new Vector();
		this.ejb20Attributes = new Vector();
		this.unknownPrimaryKeyAttribute = null; // hack for unknown PK attribute
		this.methods = new Vector();
		this.typeHandles = new Vector();
	}
	
	/**
	 * initialize persistent state that depends on the name;
	 * this is called by the class repository *after* the type
	 * has been added, so that circular references can be
	 * resolved correctly; in particular: String's superclass
	 * is Object, and Object has a method, toString(), whose
	 * return time is String; this would fault in an extra version
	 * of String to the repository, causing identity problems :-(
	 */
	void initializeNameDependentState() {	
		// once we have the name, we can set the default values for
		// the primitive flag and superclass
		this.primitive = this.defaultPrimitiveFlag();
		this.superclassHandle = new MWClassHandle(this, this.buildSuperclassScrubber());
		this.superclassHandle.setType(this.defaultSuperclass());
	}
	
	private MWClass defaultDeclaringType() {
		return null;
	}
	
	private boolean defaultInterfaceFlag() {
		return false;
	}
	
	private boolean defaultPrimitiveFlag() {
		return CollectionTools.contains(primitiveClassNames(), this.getName());
	}
	
	private MWClass defaultSuperclass() {
		if (this.requiresSuperclass()) {
			return this.objectType();
		}
		return null;
	}
	
	
	/**
	 * re-initialize the type to its default settings
	 */
	public void clear() {
		if (this.isCoreType()) {
			throw new IllegalStateException();
		}
		this.setDeclaringType(this.defaultDeclaringType());
		this.getModifier().clear();
		this.setInterface(this.defaultInterfaceFlag());
		this.setSuperclass(this.defaultSuperclass());
		this.clearInterfaces();
		this.clearAttributes();
		this.clearEjb20Attributes();
		this.clearMethods();
		this.clearTypes();
	}


	// ********** AbstractNodeModel overrides **********

	/**
	 * suppress change notification when we are refreshing a "core" type;
	 * this is reasonable because no clients will have ever "seen" an
	 * unrefreshed "core" type, so they will not need any change notification
	 * (this also prevents any NPE that may occur when a client starts
	 * listening to the type and triggers change events while synchronizing
	 * with the type for the first time)
	 */
	public ChangeNotifier getChangeNotifier() {
		return this.coreTypeRefreshInProgress ?
			NullChangeNotifier.instance()
		:
			super.getChangeNotifier();
	}


	// ********** accessors **********
	
	// ***** name
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		String old = this.name;
		this.name = name;
		if (this.attributeValueHasChanged(old, name)) {
			// synch up the repository before notifying everyone
			try {
				this.getRepository().typeRenamed(old, name);
			} catch (RuntimeException ex) {
				this.name = old;		// restore the name before re-throwing the exception
				throw ex;
			}
			this.firePropertyChanged(NAME_PROPERTY, old, name);
			this.getProject().nodeRenamed(this);
		}
	}

	public String fullName() {
		return this.getName();
	}
	
	public String shortName() {
		return ClassTools.shortNameForClassNamed(this.getName());
	}
	
	public String packageName() {
		return ClassTools.packageNameForClassNamed(this.getName());
	}
	
	public String packageDisplayName() {
		return packageDisplayNameForClassNamed(this.getName());
	}


	// ***** declaringType
	/**
	 * the declaring type can be null
	 * for normal, top-level classes and interfaces
	 */
	public MWClass getDeclaringType() {
		return this.declaringTypeHandle.getType();
	}
	
	/**
	 * the declaring type can be set to null
	 * for normal, top-level classes and interfaces
	 */
	public void setDeclaringType(MWClass declaringType) {
		Object old = this.declaringTypeHandle.getType();
		this.declaringTypeHandle.setType(declaringType);
		this.firePropertyChanged(DECLARING_TYPE_PROPERTY, old, declaringType);
	}
	

	// ***** modifier
	public MWModifier getModifier() {
		return this.modifier;
	}
	

	// ***** primitive
	public boolean isPrimitive() {
		return this.primitive;
	}
	
	
	// ***** interface
	public boolean isInterface() {
		return this.interfaceFlag;
	}
	
	public void setInterface(boolean interfaceFlag) {
		if (this.isNonReferenceType() && interfaceFlag) {
			throw new IllegalStateException("A primitive cannot be converted to an interface: " + this.getName());
		}
		boolean old = this.interfaceFlag;
		this.interfaceFlag = interfaceFlag;
		// if we change the interface flag, we need to reset the superclass
		if (old != interfaceFlag) {
			this.setSuperclass(this.defaultSuperclass());
		}
		// fire this after setting the superclass. 
		// otherwise we hit problems in MWClassRepository.typeChanged(MWClass)
		this.firePropertyChanged(INTERFACE_PROPERTY, old, interfaceFlag);
		// if we change the interface flag, we need to notify the methods
		if (old != interfaceFlag) {
			this.notifyMethodsOfAllowedModifiersChange();
		}
	}
	
	/**
	 * ...as opposed to an interface
	 */
	public boolean isClass() {
		return ! this.isInterface();
	}


	// ***** superclass
	/**
	 * the superclass can be null
	 * (for java.lang.Object, primitives, and interfaces)
	 */
	public MWClass getSuperclass() {
		return this.superclassHandle.getType();
	}

	/**
	 * the superclass can be set to null
	 * (for java.lang.Object, primitives, and interfaces)
	 */
	public void setSuperclass(MWClass superclass) {
		if (this.requiresSuperclass() && (superclass == null)) {
			throw new IllegalStateException("Superclass required: " + this);
		}
		if (this.cannotHaveSuperclass() && (superclass != null)) {
			throw new IllegalStateException("Superclass not allowed: " + this);
		}
		Object old = this.superclassHandle.getType();
		this.superclassHandle.setType(superclass);
		this.firePropertyChanged(SUPERCLASS_PROPERTY, old, superclass);
		if ((old != superclass) && ! this.isCoreType()) {
			// notify everyone the hierarchy has changed;
			// in particular, descriptors may need to remove mappings etc.
			this.superclassesChanged();
			this.getRepository().hierarchyChanged(this);
			this.getProject().hierarchyChanged(this);
		}
	}


	// ***** lastRefreshTimestamp
	/**
	 * this will be null if it is not known
	 */
	public Date getLastRefreshTimestamp() {
		return this.lastRefreshTimestamp;
	}

	/**
	 * PRIVATE - this can only be set internally
	 */
	private void setLastRefreshTimestamp(Date lastRefreshTimestamp) {
		Object old = this.lastRefreshTimestamp;
		this.lastRefreshTimestamp = lastRefreshTimestamp;
		this.firePropertyChanged(LAST_REFRESH_TIMESTAMP_PROPERTY, old, lastRefreshTimestamp);
	}
	
	private Date getLastRefreshTimestampForTopLink() {
		Date value = null;
		if (getRepository().isPersistLastRefresh()) {
			value = this.lastRefreshTimestamp;
		}
		return value;
	}
	
	private void setLastRefreshTimestampForTopLink(Date value) {
		this.lastRefreshTimestamp = value;
	}
	
	// ***** coreType
	/**
	 * return whether the type is a "core" type:
	 *     - a non-reference class (void, int, double, etc.)
	 *     - a jdk class (java.lang.Object, java.util.Vector, etc.)
	 *     - a TopLink class (Session, ValueHolder, etc.)
	 * when first constructed, core types are only partially populated
	 * with their class declaration;
	 * this method should only be used internally and by the repository...
	 */
	boolean isCoreType() {
		return this.coreType;
	}


	// ***** interfaces
	private Iterator interfaceHandles() {
		return new CloneIterator(this.interfaceHandles) {
			protected void remove(Object current) {
				MWClass.this.removeInterfaceHandle((MWClassHandle) current);
			}
			public String toString() {
				return "MWClass.interfaceHandles()";
			}
		};
	}

	void removeInterfaceHandle(MWClassHandle handle) {
		this.interfaceHandles.remove(handle);
		this.fireItemRemoved(INTERFACES_COLLECTION, handle.getType());
	}

	public Iterator interfaces() {
		return new TransformationIterator(this.interfaceHandles()) {
			protected Object transform(Object next) {
				return ((MWClassHandle) next).getType();
			}
			public String toString() {
				return "MWClass.interfaces()";
			}
		};
	}

	public int interfacesSize() {
		return this.interfaceHandles.size();
	}

	public void addInterface(MWClass type) {
		this.interfaceHandles.add(new MWClassHandle(this, type, this.interfaceScrubber()));
		this.fireItemAdded(INTERFACES_COLLECTION, type);
	}

	public void addInterfaces(Collection interfaces) {
		this.addInterfaces(interfaces.iterator());
	}

	public void addInterfaces(Iterator interfaces) {
		while (interfaces.hasNext()) {
			this.addInterface((MWClass) interfaces.next());
		}
	}

	public void removeInterface(MWClass type) {
		for (Iterator stream = this.interfaces(); stream.hasNext(); ) {
			if (stream.next() == type) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(type.toString());
	}

	public void removeInterfaces(Collection interfaces) {
		this.removeInterfaces(interfaces.iterator());
	}
	
	public void removeInterfaces(Iterator interfaces) {
		while (interfaces.hasNext()) {
			this.removeInterface((MWClass) interfaces.next());
		}
	}

	public void clearInterfaces() {
		// use #interfaceHandles() for minor performance tweak:
		for (Iterator stream = this.interfaceHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}
	
	
	// ***** attributes
	public Iterator attributes() {
		this.checkForPartiallyPopulatedCoreType();
		return new CloneIterator(this.attributes) {
			protected void remove(Object current) {
				MWClass.this.removeAttribute((MWClassAttribute) current);
			}
			public String toString() {
				return "MWClass.attributes()";
			}
		};
	}
	
	public int attributesSize() {
		this.checkForPartiallyPopulatedCoreType();
		return this.attributes.size();
	}
	
	public MWClassAttribute addAttribute(String attributeName) {
		return this.addAttribute(attributeName, this.objectType());
	}
	
	public MWClassAttribute addAttribute(String attributeName, MWClass attributeType) {
		return this.addAttribute(attributeName, attributeType, 0);
	}
	
	public MWClassAttribute addAttribute(String attributeName, MWClass attributeType, int attributeDimensionality) {
		return this.addAttribute(this.buildAttribute(attributeName, attributeType, attributeDimensionality));
	}
	
	MWClassAttribute addAttribute(ExternalField externalField) {
		return this.addAttribute(new MWClassAttribute(this, externalField));
	}
	
	private MWClassAttribute addAttribute(MWClassAttribute attribute) {
		this.checkForPartiallyPopulatedCoreType();
		MWClassAttribute ejb20Attribute = this.ejb20AttributeNamed(attribute.getName());
		if (ejb20Attribute != null) {
			// TODO what if ejb 2.0 attribute is in superclass?
			this.removeEjb20Attribute(ejb20Attribute);
		}
		this.addItemToCollection(attribute, this.attributes, ATTRIBUTES_COLLECTION);
		return attribute;
	}
	
	public void removeAttribute(MWClassAttribute attribute) {
		this.checkForPartiallyPopulatedCoreType();
		this.removeNodeFromCollection(attribute, this.attributes, ATTRIBUTES_COLLECTION);
	}
	
	public void removeAttributes(Collection attrs) {
		this.removeAttributes(attrs.iterator());
	}
	
	public void removeAttributes(Iterator attrs) {
		while (attrs.hasNext()) {
			this.removeAttribute((MWClassAttribute) attrs.next());
		}
	}
	
	public void clearAttributes() {
		this.removeAttributes(this.attributes());
	}
	
	
	// ***** ejb20Attributes
	public Iterator ejb20Attributes() {
		return new CloneIterator(this.ejb20Attributes) {
			protected void remove(Object current) {
				MWClass.this.removeEjb20Attribute((MWClassAttribute) current);
			}
			public String toString() {
				return "MWClass.ejb20Attributes()";
			}
		};
	}
	
	public Iterator nonEjb20Attributes() {
		return this.attributes();
	}
	
	public int ejb20AttributesSize() {
		return this.ejb20Attributes.size();
	}
	
	public MWClassAttribute addEjb20Attribute(String attributeName, MWClass attributeType) {
		return this.addEjb20Attribute(attributeName, attributeType, 0);
	}
	
	public MWClassAttribute addEjb20Attribute(String attributeName, MWClass attributeType, int attributeDimensionality) {
		MWClassAttribute ejbAttribute = this.addEjb20Attribute(this.buildEjb20Attribute(attributeName, attributeType, attributeDimensionality));
		ejbAttribute.generateGetAndSetMethods();
		return ejbAttribute;
	}
	
	private MWClassAttribute addEjb20AttributeInternal(String attributeName, MWClass attributeType, int attributeDimensionality) {
		return this.addEjb20Attribute(this.buildEjb20Attribute(attributeName, attributeType, attributeDimensionality));
	}
	
	private MWClassAttribute addEjb20Attribute(MWClassAttribute attribute) {
		// do not allow an EJB 2.0 attribute to replace a normal Java attribute
		if (this.attributeNamed(attribute.getName()) != null) {
			// what if attribute is in superclass?
			return null;
		}
		this.addItemToCollection(attribute, this.ejb20Attributes, EJB20_ATTRIBUTES_COLLECTION);
		return attribute;
	}
	
	public void removeEjb20Attribute(MWClassAttribute attribute) {
		this.removeNodeFromCollection(attribute, this.ejb20Attributes, EJB20_ATTRIBUTES_COLLECTION);
	}
	
	public void removeEjb20Attributes(Collection attrs) {
		this.removeEjb20Attributes(attrs.iterator());
	}
	
	public void removeEjb20Attributes(Iterator attrs) {
		while (attrs.hasNext()) {
			this.removeEjb20Attribute((MWClassAttribute) attrs.next());
		}
	}
	
	public void clearEjb20Attributes() {
		this.removeEjb20Attributes(this.ejb20Attributes());
	}
	

	// ***** unknown PK attribute
	public boolean usesUnknownPrimaryKeyAttribute() {
		return this.unknownPrimaryKeyAttribute != null;
	}

	public MWClassAttribute getUnknownPrimaryKeyAttribute() {
		return this.unknownPrimaryKeyAttribute;
	}

	// ***** methods
	public Iterator methods() {
		this.checkForPartiallyPopulatedCoreType();
		return new CloneIterator(this.methods) {
			protected void remove(Object current) {
				MWClass.this.removeMethod((MWMethod) current);
			}
			public String toString() {
				return "MWClass.methods()";
			}
		};
	}
	
	public int methodsSize() {
		this.checkForPartiallyPopulatedCoreType();
		return this.methods.size();
	}
	
	public MWMethod addMethod(String methodName) {
		return this.addMethod(this.buildMethod(methodName));
	}
	
	public MWMethod addMethod(String methodName, MWClass returnType) {
		return this.addMethod(this.buildMethod(methodName, returnType));
	}
	
	public MWMethod addMethod(String methodName, MWClass returnType, int dimensionality) {
		return this.addMethod(this.buildMethod(methodName, returnType, dimensionality));
	}
	
	private MWMethod addMethod(MWMethod method) {
		this.checkForPartiallyPopulatedCoreType();
		this.methods.add(method);
		this.fireItemAdded(METHODS_COLLECTION, method);
		return method;
	}
	
	public void removeMethod(MWMethod method) {
		this.checkForPartiallyPopulatedCoreType();
		this.removeNodeFromCollection(method, this.methods, METHODS_COLLECTION);
	}
	
	public void removeMethods(Collection methodList) {
		this.removeMethods(methodList.iterator());
	}
	
	public void removeMethods(Iterator methodStream) {
		while (methodStream.hasNext()) {
			this.removeMethod((MWMethod) methodStream.next());
		}
	}
	
	public void clearMethods() {
		this.removeMethods(this.methods());
	}


	// ***** types
	private Iterator typeHandles() {
		this.checkForPartiallyPopulatedCoreType();
		return new CloneIterator(this.typeHandles) {
			protected void remove(Object current) {
				MWClass.this.removeTypeHandle((MWClassHandle) current);
			}
			public String toString() {
				return "MWClass.typeHandles()";
			}
		};
	}

	void removeTypeHandle(MWClassHandle handle) {
		// this method is only called from the CloneIterator created in #typeHandles()
		// and the scrubber built in #buildTypeScrubber()
		// so there is no need to call #checkForPartiallyPopulatedCoreType()
		this.typeHandles.remove(handle);
		this.fireItemRemoved(TYPES_COLLECTION, handle.getType());
	}

	public Iterator types() {
		return new TransformationIterator(this.typeHandles()) {
			protected Object transform(Object next) {
				return ((MWClassHandle) next).getType();
			}
			public String toString() {
				return "MWClass.types()";
			}
		};
	}

	public int typesSize() {
		this.checkForPartiallyPopulatedCoreType();
		return this.typeHandles.size();
	}

	public void addType(MWClass type) {
		this.checkForPartiallyPopulatedCoreType();
		this.typeHandles.add(new MWClassHandle(this, type, this.typeScrubber()));
		this.fireItemAdded(TYPES_COLLECTION, type);
	}

	public void addTypes(Collection types) {
		this.addTypes(types.iterator());
	}

	public void addTypes(Iterator types) {
		while (types.hasNext()) {
			this.addType((MWClass) types.next());
		}
	}

	public void removeType(MWClass type) {
		for (Iterator stream = this.types(); stream.hasNext(); ) {
			if (stream.next() == type) {
				stream.remove();
				return;
			}
		}
		throw new IllegalArgumentException(type.toString());
	}

	public void removeTypes(Collection types) {
		this.removeTypes(types.iterator());
	}

	public void removeTypes(Iterator types) {
		while (types.hasNext()) {
			this.removeType((MWClass) types.next());
		}
	}

	public void clearTypes() {
		// use #typeHandles() for minor performance tweak:
		for (Iterator stream = this.typeHandles(); stream.hasNext(); ) {
			stream.next();
			stream.remove();
		}
	}


	// ********** Modifiable implementation **********

	public boolean supportsAbstract() {
		return true;
	}
	
	public boolean canBeSetAbstract() {
		return ! this.getModifier().isFinal();
	}
	
	public boolean canBeSetFinal() {
		return ! this.isAbstract();
	}
	
	public boolean supportsInterface() {
		return true;
	}
	
	public boolean canBeSetInterface() {
		return true;
	}
	
	public boolean supportsNative() {
		return false;
	}
	
	public boolean canBeSetNative() {
		return false;
	}
	
	public boolean canBeSetPackage() {
		return true;
	}
	
	public boolean canBeSetPrivate() {
		return this.isMemberType();
	}
	
	public boolean canBeSetProtected() {
		return this.isMemberType();
	}
	
	public boolean canBeSetPublic() {
		return true;
	}
	
	public boolean canBeSetStatic() {
		return this.isMemberType();
	}
	
	/**
	 * member types are implied to be strictfp...
	 */
	public boolean isStrict() {
		return this.getModifier().isStrict()
			|| ((this.getDeclaringType() != null) && this.getDeclaringType().isStrict());
	}
	
	public boolean supportsStrict() {
		return true;
	}
	
	public boolean canBeSetStrict() {
		return true;
	}
	
	public boolean supportsSynchronized() {
		return false;
	}
	
	public boolean canBeSetSynchronized() {
		return false;
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
	 * 'abstract'' and 'final are mutually exclusive
	 */
	private static final int ALLOWED_MODIFIERS_FLAGS = Modifier.ABSTRACT | Modifier.FINAL;

	/**
	 * if any of these modifiers change for the class, the class's
	 * methods' "allowed" modifiers may have changed (e.g. if the
	 * class is no longer 'abstract', none of the class's methods
	 * can be 'abstract').
	 */
	private static final int METHOD_ALLOWED_MODIFIERS_FLAGS = Modifier.ABSTRACT | Modifier.FINAL | Modifier.STRICT;

	public void modifierChanged(int oldCode, int newCode) {
		this.firePropertyChanged(MODIFIER_CODE_PROPERTY, oldCode, newCode);
		if (MWModifier.anyFlagsAreDifferent(ALLOWED_MODIFIERS_FLAGS, oldCode, newCode)) {
			this.modifier.allowedModifiersChanged();
		}
		if (MWModifier.anyFlagsAreDifferent(METHOD_ALLOWED_MODIFIERS_FLAGS, oldCode, newCode)) {
			this.notifyMethodsOfAllowedModifiersChange();
		}
		// TODO only "member" classes can be marked 'private', 'protected', or 'static'
	}

	private void notifyMethodsOfAllowedModifiersChange() {
		// no need to fault in the methods for a "core" type
		synchronized (this.methods) {
			for (Iterator stream = this.methods.iterator(); stream.hasNext(); ) {
				((MWMethod) stream.next()).allowedModifiersChanged();
			}
		}
	}

	public void accessLevelChanged(String oldValue, String newValue) {
		this.firePropertyChanged(MODIFIER_ACCESS_LEVEL_PROPERTY, oldValue, newValue);
	}
	
	/**
	 * return the external class descriptions corresponding to the type;
	 * any one of these external class descriptions can be used to
	 * refresh the type;
	 * @see MWClassRepository#refreshTypeFor(ExternalClassDescription)
	 */
	public Iterator externalClassDescriptions() {
		return this.getRepository().externalClassDescriptionsNamed(this.getName());
	}
	
	
	// ********** queries **********
	
	/**
	 * @see org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription#getAdditionalInfo()
	 */
	public String getAdditionalInfo() {
		return this.getProject().getName();
	}
	
	/**
	 * return whether the type is a member type of another type
	 */
	public boolean isMemberType() {
		return this.getDeclaringType() != null;
	}
	
	/**
	 * return whether the type is a "normal", reference type,
	 * i.e. it is *not* a primitive or void
	 */
	public boolean isReferenceType() {
		return ! this.isNonReferenceType();
	}
	
	/**
	 * return whether the type is a non-reference type,
	 * i.e. it is a primitive or void
	 */
	public boolean isNonReferenceType() {
		return this.isPrimitive() || this.isVoid();
	}
	
	/**
	 * interfaces are implied to be abstract...
	 */
	public boolean isAbstract() {
		return this.getModifier().isAbstract()
			|| this.isInterface();
	}

	/**
	 * return whether the type can be instantiated
	 */
	public boolean isConcrete() {
		return ! this.isAbstract();
	}

	/**
	 * return the class's declaring class lineage,
	 * starting with, and including, itself and
	 * up to, and including, the top-level class
	 */
	public Iterator declaringTypeLineage() {
		return new ChainIterator(this) {
			protected Object nextLink(Object currentLink) {
				return ((MWClass) currentLink).getDeclaringType();
			}
			public String toString() {
				return "MWClass.declaringTypeLineage()";
			}
		};
	}
	
	/**
	 * return whether the class's declaring class lineage,
	 * as described above, contains the specified type
	 */
	public boolean declaringTypeLineageContains(MWClass type) {
		return CollectionTools.contains(this.declaringTypeLineage(), type);
	}
	
	/**
	 * return the class's declaring classes,
	 * starting with, and including, its immediate declaring class
	 * up to, and including, the top-level class
	 */
	public Iterator declaringTypes() {
		MWClass dt = this.getDeclaringType();
		if (dt == null) {
			return NullIterator.instance();
		}
		return dt.declaringTypeLineage();
	}

	/**
	 * return whether the type is simply a placeholder,
	 * devoid of any state derived from an "external" class
	 */
	public boolean isStub() {
		if (this.isCoreType()) {
			// "core" types are never "stubs"
			return false;
		}

		return this.declaringTypeIsDefaultValue()
			&& this.modifier.isDefaultValue()
				// ignore the interface flag
			&& this.superclassIsDefaultValue()
			&& this.interfaceHandles.isEmpty()
			&& (this.getComment().length() == 0)
			&& this.attributes.isEmpty()		// don't fault in 'attributes'
			&& this.ejb20Attributes.isEmpty()
			&& (this.unknownPrimaryKeyAttribute == null)
			&& this.methods.isEmpty()		// don't fault in 'methods'
			&& this.typeHandles.isEmpty();		// don't fault in 'typeHandles'
	}

	/**
	 * return whether the type has been fully populated
	 * with state derived from an "external" class
	 */
	public boolean isFullyPopulated() {
		return ! this.isStub();
	}

	/**
	 * return whether the type and ALL its super-types (superclasses up
	 * to, and including, java.lang.Object and super-interfaces) are
	 * fully populated with state derived from their respective "external"
	 * classes; if everything is fully populated we can legitimately determine
	 * whether this type is assignable to or from another type whose
	 * super-types are all fully-populated also
	 */
	public boolean isFullyTyped() {
		for (Iterator stream = this.lineageIncludingInterfaces(); stream.hasNext(); ) {
			if (((MWClass) stream.next()).isStub()) {
				return false;
			}
		}
		return true;
	}

	private boolean declaringTypeIsDefaultValue() {
		return this.getDeclaringType() == this.defaultDeclaringType();
	}
	
	private boolean superclassIsDefaultValue() {
		if (this.cannotHaveSuperclass()) {
			return this.getSuperclass() == null;
		}
		// look at the name since we may not have visibility to the class repository yet...
		return this.getSuperclass().getName().equals(java.lang.Object.class.getName());
	}
	
	/**
	 * return the class's interfaces, and their interfaces, and so on;
	 * do NOT include interfaces from the class's superclasses;
	 * include the class itself if it is an interface
	 */
	Iterator expandedInterfaces() {
		class ExpandedInterfacesTreeIterator extends TreeIterator {
			ExpandedInterfacesTreeIterator(Object root) {
				super(root);
			}
			ExpandedInterfacesTreeIterator(Iterator roots) {
				super(roots);
			}
			protected Iterator children(Object next) {
				return ((MWClass) next).interfaces();
			}
		}
		return this.isInterface() ?
				new ExpandedInterfacesTreeIterator(this)
			:
				new ExpandedInterfacesTreeIterator(this.interfaces());
	}
	
	/**
	 * return all the interfaces, including those inherited
	 * (either via superclasses or via super-interfaces);
	 * include the class itself if it is an interface
	 */
	public Iterator allInterfaces() {
		return new CompositeIterator(
			new TransformationIterator(this.lineage()) {
				protected Object transform(Object next) {
					return ((MWClass) next).expandedInterfaces();
				}
				public String toString() {
					return "MWClass.allInterfaces()";
				}
			}
		);
	}
	
	/**
	 * return all the interfaces, including those inherited
	 * (either via superclasses or via super-interfaces);
	 * include the class itself if it is an interface;
	 * eliminate any duplicates
	 */
	public Iterator allInterfacesWithoutDuplicates() {
		return CollectionTools.set(this.allInterfaces()).iterator();
	}
	
	public boolean allInterfacesContains(MWClass interfaceX) {
		return CollectionTools.contains(this.allInterfaces(), interfaceX);
	}
	
	/**
	 * return all the attributes, including those inherited
	 */
	public Iterator allAttributes() {
		return new CompositeIterator(
			new TransformationIterator(this.lineage()) {
				protected Object transform(Object next) {
					return ((MWClass) next).attributes();
				}
				public String toString() {
					return "MWClass.allAttributes()";
				}
			}
		);
	}

	/**
	 * return the names of the type's attributes;
	 * duplicate names within a type are not allowed
	 */
	public Iterator attributeNames() {
		return this.attributeNames(this.attributes());
	}

	/**
	 * return the names of all the attributes visible to this type;
	 * duplicate attribute names within a type are not allowed,
	 * but an attribute name that duplicates an attribute name in
	 * a superclass *is* allowed (it just might cause problems with debugging)
	 */
	public Iterator visibleAttributeNames() {
		return this.attributeNames(this.visibleAttributes());
	}

	private Iterator attributeNames(Iterator attrs) {
		return new TransformationIterator(attrs) {
			protected Object transform(Object next) {
				return ((MWClassAttribute) next).getName();
			}
			public String toString() {
				return "MWClass.attributeNames(Iterator)";
			}
		};
	}

	public MWClassAttribute attributeNamed(String attributeName) {
		return this.attributeNamed(this.attributes(), attributeName);
	}

	public boolean containsAttributeNamed(String attributeName) {
		return this.attributeNamed(attributeName) != null;
	}

	/**
	 * include inherited attributes
	 */
	public MWClassAttribute attributeNamedFromAll(String attributeName) {
		return this.attributeNamed(this.allAttributes(), attributeName);
	}
	
	/**
	 * include inherited attributes
	 */
	public boolean containsAttributeNamedFromAll(String attributeName) {
		return this.attributeNamedFromAll(attributeName) != null;
	}

	/**
	 * include EJB 2.0 and inherited attributes
	 */
	public MWClassAttribute attributeNamedFromCombinedAll(String attributeName) {
		return this.attributeNamed(this.allCombinedAttributes(), attributeName);
	}
	
	/**
	 * include EJB 2.0 and inherited attributes
	 */
	public boolean containsAttributeNamedFromCombinedAll(String attributeName) {
		return this.attributeNamedFromCombinedAll(attributeName) != null;
	}

	private MWClassAttribute attributeNamed(Iterator attrs, String attributeName) {
		while (attrs.hasNext()) {
			MWClassAttribute attribute = (MWClassAttribute) attrs.next();
			if (attribute.getName().equals(attributeName)) {
				return attribute;
			}
		}
		return null;
	}
	
	/**
	 * as opposed to class variables and constants
	 */
	public Iterator instanceVariables() {
		return this.instanceVariables(this.attributes());
	}
	
	/**
	 * return all the instance variables, including those inherited
	 */
	public Iterator allInstanceVariables() {
		return this.instanceVariables(this.allAttributes());
	}
	
	private Iterator instanceVariables(Iterator attrs) {
		return new FilteringIterator(attrs) {
			protected boolean accept(Object next) {
				return ((MWClassAttribute) next).isInstanceVariable();
			}
			public String toString() {
				return "MWClass.instanceVariables(Iterator)";
			}
		};
	}
	
	/**
	 * return all attributes in type's hierarchy that are visible
	 * to this type
	 */
	public Iterator visibleAttributes() {
		return this.visibleAttributes(this.allAttributes());
	}
	
	private Iterator visibleAttributes(Iterator attrs) {
		return new FilteringIterator(attrs) {
			protected boolean accept(Object next) {
				MWClassAttribute attribute = (MWClassAttribute) next;
				if (attribute.getDeclaringType() == MWClass.this) {
					return true;
				}
				if (attribute.getModifier().isPrivate()) {
					return false;
				}
				if (attribute.getModifier().isPublic() || attribute.getModifier().isProtected()) {
					return true;
				}
				return MWClass.this.packageName().equals(attribute.getDeclaringType().packageName());
			}
			public String toString() {
				return "MWClass.visibleAttributes(Iterator)";
			}
		};
	}
	
	/**
	 * return all the EJB 2.0 attributes, including those inherited
	 */
	public Iterator allEjb20Attributes() {
		return new CompositeIterator(
			new TransformationIterator(this.lineage()) {
				protected Object transform(Object next) {
					return ((MWClass) next).ejb20Attributes();
				}
				public String toString() {
					return "MWClass.allEjb20Attributes()";
				}
			}
		);
	}
	
	public MWClassAttribute ejb20AttributeNamed(String attributeName) {
		synchronized (this.ejb20Attributes) {
			return this.attributeNamed(this.ejb20Attributes.iterator(), attributeName);
		}
	}

	public boolean containsEjb20AttributeNamed(String attributeName) {
		return this.ejb20AttributeNamed(attributeName) != null;
	}

	boolean ejb20AttributesContains(MWClassAttribute attribute) {
		return this.ejb20Attributes.contains(attribute);
	}

	/**
	 * include inherited EJB 2.0 attributes
	 */
	public MWClassAttribute ejb20AttributeNamedFromAll(String attributeName) {
		return this.attributeNamed(this.allEjb20Attributes(), attributeName);
	}
	
	/**
	 * include inherited EJB 2.0 attributes
	 */
	public boolean containsEjb20AttributeNamedFromAll(String attributeName) {
		return this.ejb20AttributeNamedFromAll(attributeName) != null;
	}

	/**
	 * return the combined normal and EJB 2.0 attributes
	 */
	public Iterator combinedAttributes() {
		return new CompositeIterator(this.attributes(), this.ejb20Attributes());
	}
	
	/**
	 * include EJB 2.0 attributes
	 */
    public MWClassAttribute combinedAttributeNamed(String attributeName) {
        return this.attributeNamed(this.combinedAttributes(), attributeName);
    }
    
	/**
	 * include EJB 2.0 attributes
	 */
	public boolean containsCombinedAttributeNamed(String attributeName) {
		return this.combinedAttributeNamed(attributeName) != null;
	}

	/**
	 * return all the combined normal and EJB 2.0 attributes,
	 * including those inherited and the Unknown PK attribute
	 */
	public Iterator allCombinedAttributes() {
		// Have to check to see if the unknownPrimaryKeyAttribute is null... creating a SingleElementIterator with
		// a null value here will cause NPE's in attributeNamedFromCombinedAll() when looking up the unknownPrimaryKeyAttribute
		return (this.unknownPrimaryKeyAttribute == null) ?
			new CompositeIterator(this.allAttributes(), this.allEjb20Attributes())
		:
			new CompositeIterator(this.allAttributes(), this.allEjb20Attributes(), new SingleElementIterator(this.unknownPrimaryKeyAttribute));
	}

	/**
	 * return all the methods, including those inherited
	 */
	public Iterator allMethods() {
		return new CompositeIterator(
			new TransformationIterator(this.lineage()) {
				protected Object transform(Object next) {
					return ((MWClass) next).methods();
				}
				public String toString() {
					return "MWClass.allMethods()";
				}
			}
		);
	}
	
	public Iterator constructors() {
		return new FilteringIterator(this.methods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isConstructor();
			}
			public String toString() {
				return "MWClass.constructors()";
			}
		};
	}
	
	/**
	 * static and non-static "regular" methods;
	 * essentially, any (local) method you can call on an instance of the class
	 */
	public Iterator nonConstructors() {
		return this.nonConstructors(this.methods());
	}
	
	/**
	 * static and non-static "regular" methods, including those inherited;
	 * essentially, any method you can call on an instance of the class
	 */
	public Iterator allNonConstructors() {
		return this.nonConstructors(this.allMethods());
	}
	
	private Iterator nonConstructors(Iterator methodStream) {
		return new FilteringIterator(methodStream) {
			protected boolean accept(Object next) {
				return ! ((MWMethod) next).isConstructor();
			}
			public String toString() {
				return "MWClass.nonConstructors(Iterator)";
			}
		};
	}
	
	public Iterator instanceMethods() {
		return this.instanceMethods(this.methods());
	}
	
	/**
	 * return all the instance methods, including those inherited
	 */
	public Iterator allInstanceMethods() {
		return this.instanceMethods(this.allMethods());
	}
	
	private Iterator instanceMethods(Iterator methodStream) {
		return new FilteringIterator(methodStream) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isInstanceMethod();
			}
			public String toString() {
				return "MWClass.instanceMethods(Iterator)";
			}
		};
	}
	
	public Iterator staticMethods() {
		return this.staticMethods(this.methods());
	}
	
	/**
	 * return all the static methods, including those inherited
	 */
	public Iterator allStaticMethods() {
		return this.staticMethods(this.allMethods());
	}
	
	private Iterator staticMethods(Iterator methodStream) {
		return new FilteringIterator(methodStream) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isStatic();
			}
			public String toString() {
				return "MWClass.staticMethods(Iterator)";
			}
		};
	}

	/**
	 * any "get" method (zero arguments, return something)
	 */
	public Iterator candidateTopLinkGetMethods() {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateTopLinkGetMethod();
			}
			public String toString() {
				return "MWClass.candidateTopLinkGetMethods()";
			}
		};
	}
	
	/**
	 * any "set" method (one argument)
	 */
	public Iterator candidateTopLinkSetMethods() {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateTopLinkSetMethod();
			}
			public String toString() {
				return "MWClass.candidateTopLinkSetMethods()";
			}
		};
	}
	
	/**
	 * used by Map container policy
	 */
	public Iterator candidateMapContainerPolicyKeyMethods() {
		return new FilteringIterator(this.allMethods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateMapContainerPolicyKeyMethod();
			}
			public String toString() {
				return "MWClass.candidateMapContainerPolicyKeyMethods()";
			}
		};
	}
	
	/**
	 * used by inheritance policy
	 */
	public Iterator candidateClassExtractionMethods() {
		return new FilteringIterator(this.allMethods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateClassExtractionMethod();
			}
			public String toString() {
				return "MWClass.candidateClassExtractionMethods()";
			}
		};
	}
	
	/**
	 * used by clone copy policy
	 */
	public Iterator candidateCloneMethods() {
		return new FilteringIterator(this.allMethods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateCloneMethod();
			}
			public String toString() {
				return "MWClass.candidateCloneMethods()";
			}
		};
	}

	/**
	 * used by events policy
	 */
	public Iterator candidateDescriptorEventMethods() {
		return new FilteringIterator(this.allMethods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateDescriptorEventMethod();
			}
			public String toString() {
				return "MWClass.candidateDescriptorEventMethods()";
			}
		};
	}
	
	/**
	 * used by instantiation policy
	 * NB: this should call #methods() not #allMethods()
	 * If the user wants a static method from a superclass they should choose
	 * the superclass as the factory class
	 */
	public Iterator candidateFactoryMethods() {
		return new FilteringIterator(this.methods()) {		// NOT #allMethods()
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateFactoryMethod();
			}
			public String toString() {
				return "MWClass.candidateFactoryMethods()";
			}
		};
	}
	
	/**
	 * used by instantiation policy
	 * NB: this should call #methods() not #allMethods()
	 * If the user wants a static method from a superclass they should choose
	 * the superclass as the factory class
	 */
	public Iterator candidateInstantiationMethods() {
		return new FilteringIterator(this.methods()) {		// NOT #allMethods()
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateInstantiationMethod();
			}
			public String toString() {
				return "MWClass.candidateInstantiationMethods()";
			}
		};
	}
	
	/**
	 * used by instantiation policy
	 */
	public Iterator candidateFactoryInstantiationMethodsFor(final MWClass type) {
		return new FilteringIterator(this.allMethods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateFactoryInstantiationMethodFor(type);
			}
			public String toString() {
				return "MWClass.candidateFactoryInstantiationMethodsFor(MWClass)";
			}
		};
	}
	
	/**
	 * used by after load policy
	 * NB: this should call #methods() not #allMethods()
	 * If the user wants a static method from a superclass they should choose
	 * the superclass as the afterload class
	 */
	public Iterator candidateDescriptorAfterLoadMethods() {
		return new FilteringIterator(this.methods()) {		// NOT #allMethods()
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateDescriptorAfterLoadMethod();
			}
			public String toString() {
				return "MWClass.candidateDescriptorAfterLoadMethods()";
			}
		};
	}

	/**
	 * used by transformation mapping
	 */
	public Iterator candidateAttributeTransformerMethods() {
		return new FilteringIterator(this.allMethods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateAttributeTransformerMethod();
			}
			public String toString() {
				return "MWClass.candidateAttributeTransformerMethods()";
			}
		};
	}
	
	/**
	 * used by transformation mapping
	 */
	public Iterator candidateFieldTransformerMethods() {
		return new FilteringIterator(this.allMethods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isCandidateFieldTransformerMethod();
			}
			public String toString() {
				return "MWClass.candidateFieldTransformerMethods()";
			}
		};
	}
	
	Iterator candidateGetMethodsFor(final MWClassAttribute attribute) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateGetMethodFor(attribute);
			}
			public String toString() {
				return "MWClass.candidateGetMethodsFor(MWClassAttribute)";
			}
		};
	}
	
	Iterator candidateSetMethodsFor(final MWClassAttribute attribute) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateSetMethodFor(attribute);
			}
			public String toString() {
				return "MWClass.candidateSetMethodsFor(MWClassAttribute)";
			}
		};
	}
	
	Iterator candidateGetMethodsFor(final MWClass type) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateGetMethodFor(type);
			}
			public String toString() {
				return "MWClass.candidateGetMethodsFor(MWClass)";
			}
		};
	}
	
	Iterator candidateSetMethodsFor(final MWClass type) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateSetMethodFor(type);
			}
			public String toString() {
				return "MWClass.candidateSetMethodsFor(MWClass)";
			}
		};
	}
	
	Iterator candidateAddMethodsFor(final MWClass itemType) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateAddMethodFor(itemType);
			}
			public String toString() {
				return "MWClass.candidateAddMethodsFor(MWClass)";
			}
		};
	}
	
	Iterator candidateAddMethodsFor(final MWClass keyType, final MWClass itemType) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateAddMethodFor(keyType, itemType);
			}
			public String toString() {
				return "MWClass.candidateAddMethodsFor(MWClass, MWClass)";
			}
		};
	}
	
	Iterator candidateRemoveMethodsFor(final MWClass itemOrKeyType) {
		return new FilteringIterator(this.methods()) {
			public boolean accept(Object next) {
				return ((MWMethod) next).isCandidateRemoveMethodFor(itemOrKeyType);
			}
			public String toString() {
				return "MWClass.candidateRemoveMethodsFor(MWClass)";
			}
		};
	}

	/**
	 * used to refresh EJB 2.0 attributes
	 */
	private Iterator ejb20GetMethods() {
		return new FilteringIterator(this.methods()) {
			protected boolean accept(Object next) {
				return ((MWMethod) next).isEjb20GetMethod();
			}
			public String toString() {
				return "MWClass.ejb20GetMethods()";
			}
		};
	}
	
	MWMethod ejb20SetMethodFor(MWMethod ejb20GetMethod) {
		for (Iterator stream = this.methods(); stream.hasNext(); ) {
				MWMethod method = (MWMethod) stream.next();
				if (method.isEjb20SetMethodFor(ejb20GetMethod)) {
					return method;
				}
			}
			return null;
		}
	
	MWMethod zeroArgumentMethodNamed(String methodName) {
		for (Iterator stream = this.methods(); stream.hasNext(); ) {
				MWMethod method = (MWMethod) stream.next();
				if (method.hasSignature(methodName)) {
					return method;
				}
			}
			return null;
		}
	
	MWMethod oneArgumentMethodNamed(String methodName, MWClass type) {
		return oneArgumentMethodNamed(methodName, type, 0);
	}
	
	MWMethod oneArgumentMethodNamed(String methodName, MWClass type, int dimensionality) {
		for (Iterator stream = this.methods(); stream.hasNext(); ) {
				MWMethod method = (MWMethod) stream.next();
				if (method.hasSignature(methodName, type, dimensionality)) {
					return method;
				}
			}
			return null;
		}

	MWMethod oneArgumentMethodNamed(String methodName, MWTypeDeclaration declaration) {
		return this.oneArgumentMethodNamed(methodName, declaration.getType(), declaration.getDimensionality());
	}
	
	MWMethod twoArgumentMethodNamed(String methodName, MWClass type1, MWClass type2) {
		for (Iterator stream = this.methods(); stream.hasNext(); ) {
				MWMethod method = (MWMethod) stream.next();
				if (method.hasSignature(methodName, type1, 0, type2, 0)) {
					return method;
				}
			}
			return null;
		}
	
	public MWMethod methodWithSignature(String signature) {
		return this.methodWithSignature(this.methods(), signature);
	}
	
	/**
	 * include inherited methods
	 */
	public MWMethod methodWithSignatureFromAll(String signature) {
		return this.methodWithSignature(this.allMethods(), signature);
	}
	
	private MWMethod methodWithSignature(Iterator methodStream, String signature) {
		while (methodStream.hasNext()) {
			MWMethod method = (MWMethod) methodStream.next();
			if (method.signature().equals(signature)) {
				return method;
			}
		}
		return null;
	}
	
	public MWMethod zeroArgumentConstructor() {
		for (Iterator stream = this.methods(); stream.hasNext(); ) {
				MWMethod method = (MWMethod) stream.next();
				if (method.isZeroArgumentConstructor()) {
					return method;
				}
			}
			return null;
		}
	
	/**
	 * used in rules
	 */
	public boolean hasAccessibleZeroArgumentConstructor() {
		if (this.isObject()) {
			return true;
		}
		return (this.zeroArgumentConstructor() != null)
					|| this.inheritsAccessibleZeroArgumentConstructor();
	}

	private boolean inheritsAccessibleZeroArgumentConstructor() {
		return ( ! this.constructors().hasNext())
				&& (this.getSuperclass() != null)
				&& this.getSuperclass().hasAccessibleZeroArgumentConstructor();
	}
	
	/**
	 * Return whether a variable with this type
	 * can be assigned a value with the specified type.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * 
	 * 	Object.isAssignableFrom(String) => true
	 * 
	 * We only return true if we are certain of the types'
	 * assignability.
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 * @see #mightBeAssignableFrom(MWClass)
	 */
	public boolean isAssignableFrom(MWClass other) {
		if (this == other) {
			// "this" and "other" are the same class or interface
			return true;
		}
		if (CollectionTools.contains(other.superclasses(), this)) {
			// "other" is a subclass of "this"
			return true;
		}
		if (CollectionTools.contains(other.allInterfaces(), this)) {
			// "other" is a subinterface of "this", or "other" implements "this"
			return true;
		}
		if (other.isInterface() && this.isObject()) {
			// any interface can be assigned to type Object
			return true;
		}
		return false;
	}
	
	/**
	 * Return whether a variable with this type
	 * *might* be assigned a value with the specified type.
	 * If we cannot truly determine assignability
	 * (i.e. the type is not "fully typed"), we return true.
	 * We only return true if we are certain of the types'
	 * assignability.
	 * @see #isAssignableFrom(MWClass)
	 */
	public boolean mightBeAssignableFrom(MWClass other) {
		return this.isAssignableFrom(other) || ( ! other.isFullyTyped());
	}
	
	/**
	 * Return whether a value with this type can be assigned
	 * to a variable with the specified type.
	 * For example:
	 * 	Object foo = new String(); // this is valid
	 * 	String foo = new Object(); // this is invalid
	 * 
	 * 	String.isAssignableTo(Object) => true
	 * 
	 * See "The Java Language Specification" 5.1.4
	 * @see java.lang.Class#isAssignableFrom(java.lang.Class)
	 */
	public boolean isAssignableTo(MWClass other) {
		return other.isAssignableFrom(this);
	}
	
	/**
	 * Return whether a value with this type *might* be assigned
	 * to a variable with the specified type.
	 * If we cannot truly determine assignability
	 * (i.e. the type is not "fully typed"), we return true.
	 * @see #isAssignableTo(MWClass)
	 */
	public boolean mightBeAssignableTo(MWClass other) {
		return other.mightBeAssignableFrom(this);
	}
	
	public boolean isBooleanPrimitive() {
		return this == this.booleanPrimitiveType();
	}
	
	public boolean isCharPrimitive() {
		return this == this.charPrimitiveType();
	}
	
	public boolean isBytePrimitive() {
		return this == this.bytePrimitiveType();
	}
	
	public boolean isShortPrimitive() {
		return this == this.shortPrimitiveType();
	}
	
	public boolean isIntPrimitive() {
		return this == this.intPrimitiveType();
	}
	
	public boolean isLongPrimitive() {
		return this == this.longPrimitiveType();
	}
	
	public boolean isFloatPrimitive() {
		return this == this.floatPrimitiveType();
	}
	
	public boolean isDoublePrimitive() {
		return this == this.doublePrimitiveType();
	}
	
	public boolean isObject() {
		return this == this.objectType();
	}
	
	public boolean isSerializable() {
		return this == this.serializableType();
	}
	
	public boolean isCloneable() {
		return this == this.cloneableType();
	}
	
	public boolean isAssignableToCollection() {
		return this.isAssignableTo(this.collectionType());
	}
	
	public boolean mightBeAssignableToCollection() {
		return this.mightBeAssignableTo(this.collectionType());
	}
	
	public boolean isAssignableToList() {
		return this.isAssignableTo(this.listType());
	}
	
	public boolean mightBeAssignableToList() {
		return this.mightBeAssignableTo(this.listType());
	}
	
	public boolean isAssignableToMap() {
		return this.isAssignableTo(this.mapType());
	}
	
	public boolean mightBeAssignableToMap() {
		return this.mightBeAssignableTo(this.mapType());
	}
	
	public boolean isAssignableToSet() {
		return this.isAssignableTo(this.setType());
	}
	
	public boolean mightBeAssignableToSet() {
		return this.mightBeAssignableTo(this.setType());
	}
	
	public boolean mightBeAssignableToSortedSet() {
		return this.mightBeAssignableTo(this.sortedSetType());
	}
	
	public boolean mightBeAssignableToComparator() {
		return this.mightBeAssignableTo(this.comparatorType());
	}
	
	public boolean isAssignableToIndirectContainer() {
		return this.isAssignableTo(this.indirectContainerType());
	}
	
	public boolean mightBeAssignableToIndirectContainer() {
		return this.mightBeAssignableTo(this.indirectContainerType());
	}
	
	public boolean isAssignableToAttributeTransformer() {
		return this.isAssignableTo(this.attributeTransformerType());
	}
	
	public boolean mightBeAssignableToAttributeTransformer() {
		return this.mightBeAssignableTo(this.attributeTransformerType());
	}
	
	public boolean isAssignableToFieldTransformer() {
		return this.isAssignableTo(this.fieldTransformerType());
	}
	
	public boolean mightBeAssignableToFieldTransformer() {
		return this.mightBeAssignableTo(this.fieldTransformerType());
	}
	
	public boolean isContainer() {
		return this.isAssignableToCollection()
				|| this.isAssignableToMap();
	}
	
	public boolean isSubclassOf(MWClass type) {
		return CollectionTools.contains(this.superclasses(), type);
	}
	
	public boolean isValueHolder() {
		return this == this.valueHolderType();
	}
	
	public boolean isVoid() {
		return this.getName().equals(voidClassName());
	}
	
	public boolean isInstantiable() {
		return this.isConcrete() && this.isReferenceType();
	}
	
	public boolean cannotHaveSuperclass() {
		if (this.isNonReferenceType()) {
			return true;
		}
		if (this.isInterface()) {
			return true;
		}
		// we must compare names here, because if we are building java.lang.Object
		// then it must not be in the repository yet...
		if (this.getName().equals(java.lang.Object.class.getName())) {
			return true;
		}
		return false;
	}
	
	public boolean requiresSuperclass() {
		return ! this.cannotHaveSuperclass();
	}
	
	/**
	 * return the class's superclass hierarchy,
	 * starting with, and including, itself and
	 * up to, and including, java.lang.Object
	 */
	public Iterator lineage() {
		return new ChainIterator(this) {
			protected Object nextLink(Object currentLink) {
				return ((MWClass) currentLink).getSuperclass();
			}
			public String toString() {
				return "MWClass.lineage()";
			}
		};
	}
	
	/**
	 * return the class's superclass and interface lineage
	 * starting with, and including, itself and up to,
	 * and including, java.lang.Object
	 */
	public Iterator lineageIncludingInterfaces() {
		return new TreeIterator(this) {
			protected Iterator children(Object next) {
				MWClass type = (MWClass) next;
				MWClass superType = type.getSuperclass();
				return (superType == null) ?
					// we have an interface, a primitive, java.lang.Object, or void
					type.interfaces()
				:
					new CompositeIterator(superType, type.interfaces());
			}
			public String toString() {
				return "MWClass.lineageIncludingInterfaces()";
			}
		};
	}
	
	/**
	 * return the class's superclass hierarchy, 
	 * starting with, and including, itself and 
	 * up to, and including, the specified superclass
	 */
	public Iterator lineageTo(final MWClass superclass) {
		return new ChainIterator(this) {
			protected Object nextLink(Object currentLink) {
				return (currentLink == superclass) ? null : ((MWClass) currentLink).getSuperclass();
			}
			public String toString() {
				return "MWClass.lineageTo(MWClass)";
			}
		};
	}
	
	/**
	 * return whether this type's superclass hierarchy,
	 * as described above, contains the specified type
	 */
	public boolean lineageContains(MWClass type) {
		return CollectionTools.contains(this.lineage(), type);
	}
	
	/**
	 * return the type's superclasses,
	 * starting with, and including, its immediate superclass
	 * up to, and including, java.lang.Object
	 */
	public Iterator superclasses() {
		MWClass sc = this.getSuperclass();
		return (sc == null) ? NullIterator.instance() : sc.lineage();
	}
	
	/**
	 * return the immediate [loaded] subclasses of the class
	 */
	public Iterator subclasses() {
		return this.getRepository().subclassesOf(this);
	}
	
	/**
	 * return the immediate [loaded] subclasses of the class,
	 * all their [loaded] subclasses, and so on
	 */
	public Iterator allSubclasses() {
		return this.getRepository().allSubclassesOf(this);
	}
	
	/**
	 * return the class's entire hierarchy:
	 * - its superclasses
	 * - itself
	 * - all its [loaded] subclasses
	 */
	public Iterator hierarchy() {
		return new CompositeIterator(this.lineage(), this.allSubclasses());
	}
	
	private MWClass booleanPrimitiveType() {
		return this.typeFor(boolean.class);
	}
	
	private MWClass charPrimitiveType() {
		return this.typeFor(char.class);
	}
	
	private MWClass bytePrimitiveType() {
		return this.typeFor(byte.class);
	}
	
	private MWClass shortPrimitiveType() {
		return this.typeFor(short.class);
	}
	
	private MWClass intPrimitiveType() {
		return this.typeFor(int.class);
	}
	
	private MWClass longPrimitiveType() {
		return this.typeFor(long.class);
	}
	
	private MWClass floatPrimitiveType() {
		return this.typeFor(float.class);
	}
	
	private MWClass doublePrimitiveType() {
		return this.typeFor(double.class);
	}
	
	private MWClass objectType() {
		return this.typeFor(Object.class);
	}
	
	private MWClass cloneableType() {
		return this.typeFor(Cloneable.class);
	}
	
	private MWClass serializableType() {
		return this.typeFor(Serializable.class);
	}
	
	private MWClass collectionType() {
		return this.typeFor(Collection.class);
	}
	
	private MWClass listType() {
		return this.typeFor(List.class);
	}
	
	private MWClass mapType() {
		return this.typeFor(Map.class);
	}
	
	private MWClass setType() {
		return this.typeFor(Set.class);
	}
	
	private MWClass sortedSetType() {
		return this.typeFor(SortedSet.class);
	}
	
	private MWClass comparatorType() {
		return this.typeFor(Comparator.class);
	}
	
	private MWClass indirectContainerType() {
		return this.typeFor(IndirectContainer.class);
	}
	
	private MWClass attributeTransformerType() {
		return this.typeFor(AttributeTransformer.class);
	}
	
	private MWClass fieldTransformerType() {
		return this.typeFor(FieldTransformer.class);
	}
	
	private MWClass valueHolderType() {
		return this.typeFor(ValueHolderInterface.class);
	}
	
	public boolean typesContains(MWClass type) {
		return CollectionTools.contains(this.types(), type);
	}
	
	/**
	 * return a table name for the class
	 * that is no longer than the specified max length;
	 * simply convert the short class name to uppercase
	 * and truncate
	 */
	public String defaultTableNameWithLength(int maxLength) {
		String tableName = this.shortName().toUpperCase();
		return (tableName.length() <= maxLength) ?
			tableName
		:
			tableName.substring(0, maxLength);
	}
	

	// ********** behavior **********

	/**
	 * containment hierarchy
	 */
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.modifier);
		children.add(this.declaringTypeHandle);
		children.add(this.superclassHandle);
		synchronized (this.interfaceHandles) { children.addAll(this.interfaceHandles); }
		synchronized (this.typeHandles) { children.addAll(this.typeHandles); }		// don't fault in 'typeHandles'
		synchronized (this.attributes) { children.addAll(this.attributes); }		// don't fault in 'attributes'
		synchronized (this.methods) { children.addAll(this.methods); }		// don't fault in 'methods'
		synchronized (this.ejb20Attributes) { children.addAll(this.ejb20Attributes); }
		//this will be null by default, otherwise add it to children
		//all of this garbage can be removed once we refactor EJB's out from mwclasses
		if (this.unknownPrimaryKeyAttribute != null) {
			children.add(this.unknownPrimaryKeyAttribute);
		}
	}

	private NodeReferenceScrubber buildDeclaringTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClass.this.setDeclaringType(null);
			}
			public String toString() {
				return "MWClass.buildDeclaringTypeScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildSuperclassScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClass.this.setSuperclass(null);
			}
			public String toString() {
				return "MWClass.buildSuperclassScrubber()";
			}
		};
	}

	private NodeReferenceScrubber interfaceScrubber() {
		if (this.interfaceScrubber == null) {
			this.interfaceScrubber = this.buildInterfaceScrubber();
		}
		return this.interfaceScrubber;
	}

	private NodeReferenceScrubber buildInterfaceScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClass.this.removeInterfaceHandle((MWClassHandle) handle);
			}
			public String toString() {
				return "MWClass.buildInterfaceScrubber()";
			}
		};
	}

	private NodeReferenceScrubber typeScrubber() {
		if (this.typeScrubber == null) {
			this.typeScrubber = this.buildTypeScrubber();
		}
		return this.typeScrubber;
	}

	private NodeReferenceScrubber buildTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClass.this.removeTypeHandle((MWClassHandle) handle);
			}
			public String toString() {
				return "MWClass.buildTypeScrubber()";
			}
		};
	}

	/**
	 * Notify the repository that the type has changed.
	 * @see org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel#aspectChanged(java.lang.String)
	 */
	protected void aspectChanged(String aspectName) {
		super.aspectChanged(aspectName);
		this.getRepository().typeChanged(this);
	}

	/**
	 * If we are dealing with an unsaved new project (one created by the user,
	 * not read in from XML files), every object is marked dirty and it doesn't
	 * really matter if a "core" type (and its parent, the class repository) is
	 * marked dirty. But once the project is saved and everything is marked
	 * clean, a "core" type can be marked dirty when its members are faulted in
	 * (@see #checkForPartiallyPopulatedCoreType()). When this happens, we
	 * don't want the "core" type's parent (the class repository) marked dirty
	 * because "core" types are never written out to XML files.
	 */
	protected void markParentBranchDirty() {
		if (this.isCoreType()) {
			// do nothing - the repository isn't really dirty yet
		} else {
			super.markParentBranchDirty();
		}
	}

	/**
	 * reset the "stub" interfaces because they are faulted in during
	 * #postProjectBuild() as "normal" classes;
	 * this method should only be called from MWClassRepository#postProjectBuild()
	 */
	void configureImpliedStubInterfaces() {
		for (Iterator stream = this.interfaces(); stream.hasNext(); ) {
			((MWClass) stream.next()).configureAsImpliedStubInterface();
		}
	}

	/**
	 * this type is among another type's list of interfaces; so,
	 * if this type is a "stub", it is an "implied" interface
	 */
	private void configureAsImpliedStubInterface() {
		if (this.isStub()) {
			// Convert a user stub class into a stub interface, since the Java class *probably* is an interface anyway....
			// We do not want any change notification(model or event) to occur during reading.  This is the reason 
			// for not calling setInterface(true) directly. Otherwise the model notification hierarchyChanged()
			// would be called and other objects postProjectBuild() haven't occurred yet, so NPE's can result.
			this.interfaceFlag = true;
			this.superclassHandle.setType(null);
		}
		// if we have a fully-populated class, the user has messed up;
		// so just leave it alone...
	}

	/**
	 * we will lazily complete the refresh of a "core" type when a client queries for
	 * any non-initialized state (attributes, constructors, methods, types);
	 * this method is synchronized because the validation thread can cause a
	 * "core" type to fault in its non-initialized state and we want to lock the
	 * type during this loading of state
	 */
	private synchronized void checkForPartiallyPopulatedCoreType() {
		if (this.partiallyPopulatedCoreType) {
			this.coreTypeRefreshInProgress = true;
			try {
				this.refreshMembers();
			} catch (ExternalClassNotFoundException ex) {
				// if we can't refresh a "core" type, we are in serious trouble...
				throw new RuntimeException(this.name, ex);
			} finally {
				this.coreTypeRefreshInProgress = false;
			}
		}
	}

	/**
	 * refresh the type from the current set of external class descriptions;
	 * if you would like to force the type to be refreshed from a newly-built
	 * set of external class descriptions, call 
	 * MWClassRepository#refreshExternalClassDescriptions() first;
	 * the type will be refreshed with the "default" external class description
	 * returned by the external class repository
	 * @see ExternalClassRepository#getExternalClassDescription(String)
	 */
	public void refresh() throws ExternalClassNotFoundException {
		this.refresh(DefaultMWClassRefreshPolicy.instance());
	}
	
	public void refresh(MWClassRefreshPolicy refreshPolicy) throws ExternalClassNotFoundException {
		this.getRepository().refreshType(this, refreshPolicy);
	}

	/**
	 * this method can cause a NoClassDefFoundError while
	 * the external class is introspecting the Java class...
	 */
	void refreshDeclaration(ExternalClass externalClass) throws ExternalClassNotFoundException {
		try {
			if ( ! this.getName().equals(externalClass.getName())) {
				throw new IllegalArgumentException(this.getName() + " != " + externalClass.getName());
			}
			this.refreshDeclaringType(externalClass.getDeclaringClass());
			this.getModifier().refresh(externalClass.getModifiers());
			this.setInterface(externalClass.isInterface());
			this.refreshSuperclass(externalClass.getSuperclass());
			this.refreshInterfaces(externalClass.getInterfaces());
		} catch (Throwable t) {
			// TODO catch more specific exceptions, NoClassDefFoundError, ClassNotFoundException, etc
			throw new ExternalClassNotFoundException(this.getName(), t);
		}
		this.setLastRefreshTimestamp(new Date());
	}
	
	void refresh(ExternalClass externalClass) throws ExternalClassNotFoundException {
		this.refresh(externalClass, DefaultMWClassRefreshPolicy.instance());
	}
	
	void refresh(ExternalClass externalClass, MWClassRefreshPolicy refreshPolicy) throws ExternalClassNotFoundException {
		this.refreshDeclaration(externalClass);
		this.refreshMembers(externalClass, refreshPolicy);
	}
	
	private void refreshMembers() throws ExternalClassNotFoundException {
		this.refreshMembers(DefaultMWClassRefreshPolicy.instance());
	}
	
	private void refreshMembers(MWClassRefreshPolicy refreshPolicy) throws ExternalClassNotFoundException {
		this.getRepository().refreshTypeMembers(this, refreshPolicy);
	}

	/**
	 * this method can cause a NoClassDefFoundError while
	 * the external class is introspecting the Java class...
	 * e.g. Class.getDeclaredFields() will choke if one of the field types is missing;
	 * this method is synchronized because the validation thread can cause a
	 * "core" type to fault in its non-initialized state and we want to lock the
	 * type during this loading of state
	 */
	synchronized void refreshMembers(ExternalClass externalClass, MWClassRefreshPolicy refreshPolicy) throws ExternalClassNotFoundException {
		// set the flag first so that, if we encounter any problems, we don't try again
		this.partiallyPopulatedCoreType = false;
		try {
			this.refreshAttributes(externalClass.getDeclaredFields(), refreshPolicy);
			this.refreshConstructors(externalClass.getDeclaredConstructors());
			this.refreshMethods(externalClass.getDeclaredMethods());
			this.refreshTypes(externalClass.getDeclaredClasses());
		} catch (Throwable t) {
			// TODO catch more specific exceptions, NoClassDefFoundError, ClassNotFoundException, etc
			throw new ExternalClassNotFoundException(this.getName(), t);
		}
		refreshPolicy.finalizeRefresh(this);
	}
	
	private void refreshDeclaringType(ExternalClassDescription declaringExternalClassDescription) {
		if (declaringExternalClassDescription == null) {
			this.setDeclaringType(null);
		} else {
			// do *not* force the declaring class to be populated here
			// it will be populated if requested by the user
			this.setDeclaringType(this.typeNamed(declaringExternalClassDescription.getName()));
		}
	}
	
	private void refreshSuperclass(ExternalClassDescription superExternalClassDescription) {
		if (superExternalClassDescription == null) {
			this.setSuperclass(null);
		} else {
			// do *not* force the superclass to be populated here
			// it will be populated if requested by the user
			this.setSuperclass(this.typeNamed(superExternalClassDescription.getName()));
		}
	}
	
	private void refreshInterfaces(ExternalClassDescription[] externalInterfaces) {
		// after we have looped through the external interfaces,
		// 'removedInterfaces' will be left with the interfaces that need to be removed
		Collection removedInterfaces = CollectionTools.collection(this.interfaces());
		
		for (int i = 0; i < externalInterfaces.length; i++) {
			// do *not* force the interface to be populated here
			// it will be populated if requested by the user
			MWClass mwInterface = this.getRepository().typeNamed(externalInterfaces[i].getName());
			mwInterface.configureAsImpliedStubInterface();
			if ( ! removedInterfaces.remove(mwInterface)) {
				this.addInterface(mwInterface);
			}
		}
	
		this.removeInterfaces(removedInterfaces);
	}
	
	private void refreshAttributes(ExternalField[] externalFields, MWClassRefreshPolicy refreshPolicy) {
		refreshPolicy.refreshAttributes(this, externalFields);
	}
	
	private void refreshConstructors(ExternalConstructor[] externalConstructors) {
		// after we have looped through the Java constructors,
		// 'removedConstructors' will be left with the constructors that need to be removed
		Collection removedConstructors = CollectionTools.collection(this.constructors());
		for (int i = 0; i < externalConstructors.length; i++) {
			this.refreshConstructor(externalConstructors[i], removedConstructors);
		}
		this.removeMethods(removedConstructors);
	}
	
	private void refreshConstructor(ExternalConstructor externalConstructor, Collection removedConstructors) {
		MWMethod existingConstructor = this.constructorWithSameSignatureAs(externalConstructor);
		if (existingConstructor == null) {
			// we have a new constructor
			this.addMethod(new MWMethod(this, externalConstructor));
		} else {
			// we need to refresh the existing constructor
			existingConstructor.refresh(externalConstructor);
			removedConstructors.remove(existingConstructor);
		}
	}
	
	private MWMethod constructorWithSameSignatureAs(ExternalConstructor externalConstructor) {
		for (Iterator stream = this.constructors(); stream.hasNext(); ) {
			MWMethod constructor = (MWMethod) stream.next();
			if (constructor.hasSameSignatureAs(externalConstructor)) {
				return constructor;
			}
		}
		return null;
	}
	
	private void refreshMethods(ExternalMethod[] externalMethods) {
		// after we have looped through the Java methods,
		// 'removedMethods' will be left with the methods that need to be removed
		Collection removedMethods = CollectionTools.collection(this.nonConstructors());
		for (int i = 0; i < externalMethods.length; i++) {
			this.refreshMethod(externalMethods[i], removedMethods);
		}
		this.removeMethods(removedMethods);
	}
	
	private void refreshMethod(ExternalMethod externalMethod, Collection removedMethods) {
		if (externalMethod.isSynthetic()) {
			return;	// we are not interested in compiler-generated methods
		}
		MWMethod existingMethod = this.methodWithSameSignatureAs(externalMethod);
		if (existingMethod == null) {
			// we have a new method
			this.addMethod(new MWMethod(this, externalMethod));
		} else {
			// we need to refresh the existing method
			existingMethod.refresh(externalMethod);
			removedMethods.remove(existingMethod);
		}
	}
	
	private MWMethod methodWithSameSignatureAs(ExternalMethod externalMethod) {
		for (Iterator stream = this.nonConstructors(); stream.hasNext(); ) {
			MWMethod method = (MWMethod) stream.next();
			if (method.hasSameSignatureAs(externalMethod)) {
				return method;
			}
		}
		return null;
	}
	
	private void refreshTypes(ExternalClassDescription[] externalClassDescriptions) {
		this.clearTypes();	// clear them out - because we can re-fetch them from the repository
		for (int i = 0; i < externalClassDescriptions.length; i++) {
			// the classes can be stubs - they don't need to be fully-populated
			MWClass mwClass = this.typeNamed(externalClassDescriptions[i].getName());
			this.addType(mwClass);
		}
	}
	
	/**
	 * synchronize the EJB 2.0 attributes with the abstract getters and
	 * setters; cascade up through superclasses
	 */
	public void refreshEjb20Attributes() {
		// after we have looped through the get methods,
		// 'removedEjb20Attributes' will be left with the attributes that need to be removed
		Collection removedEjb20Attributes = CollectionTools.collection(this.ejb20Attributes());
		
		for (Iterator stream = this.ejb20GetMethods(); stream.hasNext(); ) {
			MWMethod ejb20GetMethod = (MWMethod) stream.next();
			MWMethod ejb20SetMethod = ejb20SetMethodFor(ejb20GetMethod);
			if (ejb20SetMethod != null) {
				this.refreshEjb20Attribute(ejb20GetMethod, ejb20SetMethod, removedEjb20Attributes);
			}
		}

		this.removeEjb20Attributes(removedEjb20Attributes);
	
		if (this.getSuperclass() != null) {
			this.getSuperclass().refreshEjb20Attributes();
		}
	}
	
	private void refreshEjb20Attribute(MWMethod ejb20GetMethod, MWMethod ejb20SetMethod, Collection removedEjb20Attributes) {
		String attrName = StringTools.uncapitalize(ejb20GetMethod.getName().substring(3));
		MWClass attrType = ejb20GetMethod.getReturnType();
		int attrDim = ejb20GetMethod.getReturnTypeDimensionality();

		MWClassAttribute ejb20Attribute = this.ejb20AttributeNamed(attrName);
		
		if (ejb20Attribute == null) {
			// we have a new EJB 2.0 attribute
			ejb20Attribute = this.addEjb20AttributeInternal(attrName, attrType, attrDim);
		} else {
			// we need to refresh the existing EJB 2.0 attribute
			ejb20Attribute.setType(attrType);
			ejb20Attribute.setDimensionality(attrDim);
			removedEjb20Attributes.remove(ejb20Attribute);
		}

		ejb20Attribute.setGetMethod(ejb20GetMethod);
		ejb20Attribute.setSetMethod(ejb20SetMethod);
	}

	public MWMethod addZeroArgumentConstructor() {
		MWMethod zeroArgCtor = this.zeroArgumentConstructor();
		if (zeroArgCtor != null) {
			return zeroArgCtor;
		}
		return this.addMethod(this.buildZeroArgumentConstructor());
	}
	

	// ********** model synchronization support **********

	void superclassesChanged() {
		this.fireCollectionChanged(SUPERCLASSES_COLLECTION);
	}
	
	/**
	 * don't cascade the removedAttribute() or attributeAdded()
	 * notifications; we are simply shifting the attribute around
	 */
	void changeFromEjb20(MWClassAttribute attribute) {
		if (this.ejb20Attributes.remove(attribute)) {
			this.fireItemRemoved(EJB20_ATTRIBUTES_COLLECTION, attribute);
			this.attributes.add(attribute);		// don't fault in 'attributes'
			this.fireItemAdded(ATTRIBUTES_COLLECTION, attribute);
		}
	}

	/**
	 * don't cascade the removedAttribute() or attributeAdded()
	 * notifications; we are simply shifting the attribute around
	 */
	void changeToEjb20(MWClassAttribute attribute) {
		if (this.attributes.remove(attribute)) {		// don't fault in 'attributes'
			this.fireItemRemoved(ATTRIBUTES_COLLECTION, attribute);
			this.ejb20Attributes.add(attribute);
			this.fireItemAdded(EJB20_ATTRIBUTES_COLLECTION, attribute);
		}
	}		


	// ********** problem handling **********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		// @see #addDescriptorProblemsTo(List)
	}

	/**
	 * NB: until we have a visible class repository, these problems
	 * are added to the descriptor's problems
	 */
	public void addDescriptorProblemsTo(List currentProblems) {
		this.checkSuperclass(currentProblems);
		this.checkInterfaces(currentProblems);
		this.checkAttributes(currentProblems);		// remove this when we get a VCR
	}

	private void checkSuperclass(List currentProblems) {
		MWClass sc = this.getSuperclass();
		if ((sc != null) && sc.isInterface()) {
			currentProblems.add(this.buildProblem(ProblemConstants.SUPERCLASS_IS_AN_INTERFACE, this.displayStringWithPackage()));
		}
	}

	private void checkInterfaces(List currentProblems) {
		for (Iterator stream = this.interfaces(); stream.hasNext(); ) {
			MWClass type = (MWClass) stream.next();
			if ( ! type.isInterface()) {
				currentProblems.add(this.buildProblem(ProblemConstants.IMPLEMENTED_INTERFACE_NOT_AN_INTERFACE, type.displayStringWithPackage()));
			}
		}
	}
	
	private void checkAttributes(List currentProblems) {
		for (Iterator stream = this.attributes(); stream.hasNext(); ) {
			((MWClassAttribute) stream.next()).addDescriptorProblemsTo(currentProblems);
		}
	}
	

	// ********** displaying and printing **********
	
	public void toString(StringBuffer sb) {
		sb.append(getName());
	}

	/**
	 * e.g. "Object"
	 */
	public String displayString() {
		return this.shortName();
	}
	
	/**
	 * e.g. "Object (java.lang)"
	 */
	public String displayStringWithPackage() {
		StringBuffer sb = new StringBuffer(200);
		sb.append(this.shortName());
		if (this.isReferenceType()) {
			sb.append(" (");
			sb.append(this.packageDisplayName());
			sb.append(')');
		}
		return sb.toString();
	}
	
	void printDefaultReturnValueOn(StringBuffer sb) {
		if (this.isVoid()) {
			throw new IllegalStateException(this.toString());
		}
		if (this.isPrimitive()) {
			if (this.isBooleanPrimitive()) {
				sb.append("false");
			} else {
				sb.append('0');
			}
		} else {
			sb.append("null");
		}
	}
	
	/**
	 * used by Wallace code gen
	 */
	public ClassDefinition classDefinition(MWClassCodeGenPolicy classCodeGenPolicy) {
		if (this.isCoreType()) {
			return null;
		}
		
		ClassDefinition def = new ClassDefinition();
		
		def.setComment(classCodeGenPolicy.classComment(this));
		
		def.setPackageName(this.packageName());
		
		def.setAccessLevel(this.getModifier().accessLevel());
		
		if (this.isInterface()) {
			def.setType(ClassDefinition.INTERFACE_TYPE);
		} else {
			def.setType(ClassDefinition.CLASS_TYPE);
		}
		
		def.setName(this.shortName());

		MWClass superclass = this.getSuperclass();
		if ((superclass != null) && (superclass != this.objectType())) {
			def.setSuperClass(superclass.getName());
		}
		
		for (Iterator stream = this.interfaces(); stream.hasNext(); ) {
			def.addInterface(((MWClass) stream.next()).getName());
		}
		
		for (Iterator stream = this.attributes(); stream.hasNext(); ) {
			def.addAttribute(((MWClassAttribute) stream.next()).attributeDefinition());
		}
		
		for (Iterator stream = this.methods(); stream.hasNext(); ) {
			MWMethod method = (MWMethod) stream.next();
			def.addMethod(method.methodDefinition(classCodeGenPolicy.getMethodCodeGenPolicy(method)));
		}
		
		// calculate imports after everything else has been added
		def.calculateImports();
		
		return def;
	}

	/**
	 * return a concrete container type that is assignable to this type
	 *     Map => HashMap
	 *     Set => HashSet
	 *     Collection or List => ArrayList
	 * if we cannot determine a container type, return null
	 * used for code gen
	 */
	MWClass defaultContainerImplementationType() {
		if (this.isConcrete() && this.isReferenceType() && this.isContainer()) {
			// if we have a concrete "container" type, return it
			return this;
		}
		MWClass container = null;
		if (this.isAssignableToMap()) {
			container = this.typeFor(HashMap.class);
		}
		else if (this.isAssignableToSet()) {
			container = this.typeFor(HashSet.class);
		}
		else if (this.isAssignableToCollection()) {
			container = this.typeFor(ArrayList.class);
		}
		if (container == null || this.isAssignableFrom(container)) {
			return container;
		}
		return null;
	}


	// ********** factory methods **********
	
	private MWClassAttribute buildAttribute(String attributeName, MWClass attributeType, int attributeDimensionality) {
		return new MWClassAttribute(this, attributeName, attributeType, attributeDimensionality);
	}
	
	private MWClassAttribute buildEjb20Attribute(String attributeName, MWClass attributeType, int attributeDimensionality) {
		return this.buildAttribute(attributeName, attributeType, attributeDimensionality);
	}
	
	private MWMethod buildMethod(String methodName) {
		return new MWMethod(this, methodName);
	}
	
	private MWMethod buildMethod(String methodName, MWClass returnType) {
		return new MWMethod(this, methodName, returnType);
	}
	
	private MWMethod buildMethod(String methodName, MWClass returnType, int dimensionality) {
		return new MWMethod(this, methodName, returnType, dimensionality);
	}
	
	private MWMethod buildZeroArgumentConstructor() {
		return MWMethod.buildZeroArgumentConstructor(this);
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWClass.class);
		descriptor.setDefaultRootElement("class");
	
		descriptor.addDirectMapping("name", "name/text()");
	
		XMLDirectMapping isInterfaceMapping = (XMLDirectMapping) descriptor.addDirectMapping("interfaceFlag", "is-interface/text()");
		isInterfaceMapping.setNullValue(Boolean.FALSE);
	
		XMLDirectMapping modifierMapping = (XMLDirectMapping) descriptor.addDirectMapping("modifier", "getModifierForTopLink", "setModifierForTopLink", "modifier/text()");
		modifierMapping.setNullValue(new Integer(0));
	
		XMLCompositeObjectMapping declaringTypeHandleMapping = new XMLCompositeObjectMapping();
		declaringTypeHandleMapping.setAttributeName("declaringTypeHandle");
		declaringTypeHandleMapping.setSetMethodName("setDeclaringTypeHandleForTopLink");
		declaringTypeHandleMapping.setGetMethodName("getDeclaringTypeHandleForTopLink");
		declaringTypeHandleMapping.setReferenceClass(MWClassHandle.class);
		declaringTypeHandleMapping.setXPath("declaring-type-handle");
		descriptor.addMapping(declaringTypeHandleMapping);
		
		XMLCompositeObjectMapping superclassHandleMapping = new XMLCompositeObjectMapping();
		superclassHandleMapping.setAttributeName("superclassHandle");
		superclassHandleMapping.setReferenceClass(MWClassHandle.class);
		superclassHandleMapping.setSetMethodName("setSuperclassHandleForTopLink");
		superclassHandleMapping.setGetMethodName("getSuperclassHandleForTopLink");
		superclassHandleMapping.setXPath("superclass-handle");
		descriptor.addMapping(superclassHandleMapping);
		
		descriptor.addDirectMapping("lastRefreshTimestamp", "getLastRefreshTimestampForTopLink", "setLastRefreshTimestampForTopLink", "last-refresh-timestamp/text()");

		XMLCompositeCollectionMapping interfaceHandlesMapping = new XMLCompositeCollectionMapping();
		interfaceHandlesMapping.setAttributeName("interfaceHandles");
		interfaceHandlesMapping.setSetMethodName("setInterfaceHandlesForTopLink");
		interfaceHandlesMapping.setGetMethodName("getInterfaceHandlesForTopLink");
		interfaceHandlesMapping.setReferenceClass(MWClassHandle.class);
		interfaceHandlesMapping.setXPath("interface-handles/class-handle");
		descriptor.addMapping(interfaceHandlesMapping);
	
		XMLCompositeCollectionMapping attributesMapping = new XMLCompositeCollectionMapping();
		attributesMapping.setAttributeName("attributes");
		attributesMapping.setSetMethodName("setAttributesForTopLink");
		attributesMapping.setGetMethodName("getAttributesForTopLink");
		attributesMapping.setReferenceClass(MWClassAttribute.class);
		attributesMapping.setXPath("attributes/class-attribute");
		descriptor.addMapping(attributesMapping);
	
		XMLCompositeCollectionMapping ejb20AttributesMapping = new XMLCompositeCollectionMapping();
		ejb20AttributesMapping.setAttributeName("ejb20Attributes");
		ejb20AttributesMapping.setSetMethodName("setEjb20AttributesForTopLink");
		ejb20AttributesMapping.setGetMethodName("getEjb20AttributesForTopLink");
		ejb20AttributesMapping.setReferenceClass(MWClassAttribute.class);
		ejb20AttributesMapping.setXPath("ejb-20-attributes/class-attribute");
		descriptor.addMapping(ejb20AttributesMapping);
	
		XMLCompositeObjectMapping unknownPrimaryKeyAttributeMapping = new XMLCompositeObjectMapping();
		unknownPrimaryKeyAttributeMapping.setAttributeName("unknownPrimaryKeyAttribute");
		unknownPrimaryKeyAttributeMapping.setReferenceClass(MWClassAttribute.class);
		unknownPrimaryKeyAttributeMapping.setXPath("unknown-primary-key-attribute");
		descriptor.addMapping(unknownPrimaryKeyAttributeMapping);
	
		XMLCompositeCollectionMapping methodsMapping = new XMLCompositeCollectionMapping();
		methodsMapping.setAttributeName("methods");
		methodsMapping.setSetMethodName("setMethodsForTopLink");
		methodsMapping.setGetMethodName("getMethodsForTopLink");
		methodsMapping.setReferenceClass(MWMethod.class);
		methodsMapping.setXPath("methods/method");
		descriptor.addMapping(methodsMapping);
	
		XMLCompositeCollectionMapping typeHandlesMapping = new XMLCompositeCollectionMapping();
		typeHandlesMapping.setAttributeName("typeHandles");
		typeHandlesMapping.setSetMethodName("setTypeHandlesForTopLink");
		typeHandlesMapping.setGetMethodName("getTypeHandlesForTopLink");
		typeHandlesMapping.setReferenceClass(MWClassHandle.class);
		typeHandlesMapping.setXPath("type-handles/class-handle");
		descriptor.addMapping(typeHandlesMapping);
	
		XMLDirectMapping coreTypeMapping = (XMLDirectMapping) descriptor.addDirectMapping("coreType", "core-type/text()");
		coreTypeMapping.setNullValue(Boolean.FALSE);
	
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
	 * check for null
	 */
	private MWClassHandle getDeclaringTypeHandleForTopLink() {
		return (this.declaringTypeHandle.getType() == null) ? null : this.declaringTypeHandle;
	}
	private void setDeclaringTypeHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildDeclaringTypeScrubber();
		this.declaringTypeHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * check for null
	 */
	private MWClassHandle getSuperclassHandleForTopLink() {
		return (this.superclassHandle.getType() == null) ? null : this.superclassHandle;
	}
	private void setSuperclassHandleForTopLink(MWClassHandle handle) {
		NodeReferenceScrubber scrubber = this.buildSuperclassScrubber();
		this.superclassHandle = ((handle == null) ? new MWClassHandle(this, scrubber) : handle.setScrubber(scrubber));
	}

	/**
	 * sort the interface handles for TopLink
	 */
	private Collection getInterfaceHandlesForTopLink() {
		synchronized (this.interfaceHandles) {
			return new TreeSet(this.interfaceHandles);
		}
	}
	private void setInterfaceHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			((MWClassHandle) stream.next()).setScrubber(this.interfaceScrubber());
		}
		this.interfaceHandles = handles;
	}

	/**
	 * sort the attributes for TopLink
	 */
	private Collection getAttributesForTopLink() {
		// this method will never be called on a "core" type, so we don't need to call #attributes()
		synchronized (this.attributes) {
			return new TreeSet(this.attributes);
		}
	}
	private void setAttributesForTopLink(Collection attributes) {
		this.attributes = attributes;
	}

	/**
	 * sort the EJB 2.0 attributes for TopLink
	 */
	private Collection getEjb20AttributesForTopLink() {
		synchronized (this.ejb20Attributes) {
			return new TreeSet(this.ejb20Attributes);
		}
	}
	private void setEjb20AttributesForTopLink(Collection ejb20Attributes) {
		this.ejb20Attributes = ejb20Attributes;
	}

	/**
	 * sort the methods for TopLink
	 */
	private Collection getMethodsForTopLink() {
		// this method will never be called on a "core" type, so we don't need to call #methods()
		synchronized (this.methods) {
			return new TreeSet(this.methods);
		}
	}
	private void setMethodsForTopLink(Collection methods) {
		this.methods = methods;
	}

	/**
	 * sort the type handles for TopLink
	 */
	private Collection getTypeHandlesForTopLink() {
		// this method will never be called on a "core" type, so we don't need to call #typeHandles()
		synchronized (this.typeHandles) {
			return new TreeSet(this.typeHandles);
		}
	}
	private void setTypeHandlesForTopLink(Collection handles) {
		for (Iterator stream = handles.iterator(); stream.hasNext(); ) {
			MWClassHandle handle = (MWClassHandle) stream.next();
			handle.setScrubber(this.typeScrubber());
		}
		this.typeHandles = handles;
	}

	/**
	 * the primitive flag is derived from the class's name
	 */
	public void postProjectBuild() {
		super.postProjectBuild();
		this.primitive = this.defaultPrimitiveFlag();
		
	}

	// ********** legacy TopLink 6.0 methods **********
	
	public static XMLDescriptor legacy60BuildDescriptor() {
		XMLDescriptor descriptor = MWModel.legacy60BuildStandardDescriptor();
		
		descriptor.setJavaClass(MWClass.class);
		descriptor.setDefaultRootElement("class");
	
		descriptor.addDirectMapping("name",  "legacyGetNameForToplink", "legacySetNameForToplink", "name/text()");
	
		XMLDirectMapping isInterfaceMapping = (XMLDirectMapping) descriptor.addDirectMapping("interfaceFlag", "is-interface/text()");
		isInterfaceMapping.setNullValue(Boolean.FALSE);
	
		XMLDirectMapping modifierMapping = (XMLDirectMapping) descriptor.addDirectMapping("modifier", "getModifierForTopLink", "setModifierForTopLink", "modifier/text()");
		modifierMapping.setNullValue(new Integer(0));
	
		XMLCompositeObjectMapping declaringTypeHandleMapping = new XMLCompositeObjectMapping();
		declaringTypeHandleMapping.setAttributeName("declaringTypeHandle");
		declaringTypeHandleMapping.setSetMethodName("setDeclaringTypeHandleForTopLink");
		declaringTypeHandleMapping.setGetMethodName("getDeclaringTypeHandleForTopLink");
		declaringTypeHandleMapping.setReferenceClass(MWClassHandle.class);
		declaringTypeHandleMapping.setXPath("declaring-type-handle");
		descriptor.addMapping(declaringTypeHandleMapping);
		
		XMLCompositeObjectMapping superclassHandleMapping = new XMLCompositeObjectMapping();
		superclassHandleMapping.setAttributeName("superclassHandle");
		superclassHandleMapping.setReferenceClass(MWClassHandle.class);
		superclassHandleMapping.setSetMethodName("setSuperclassHandleForTopLink");
		superclassHandleMapping.setGetMethodName("getSuperclassHandleForTopLink");
		superclassHandleMapping.setXPath("superclass-handle");
		descriptor.addMapping(superclassHandleMapping);
		
		descriptor.addDirectMapping("lastRefreshTimestamp", "last-refresh-timestamp/text()");

		XMLCompositeCollectionMapping interfaceHandlesMapping = new XMLCompositeCollectionMapping();
		interfaceHandlesMapping.setAttributeName("interfaceHandles");
		interfaceHandlesMapping.setSetMethodName("setInterfaceHandlesForTopLink");
		interfaceHandlesMapping.setGetMethodName("getInterfaceHandlesForTopLink");
		interfaceHandlesMapping.setReferenceClass(MWClassHandle.class);
		interfaceHandlesMapping.setXPath("interface-handles/class-handle");
		descriptor.addMapping(interfaceHandlesMapping);
	
		XMLCompositeCollectionMapping attributesMapping = new XMLCompositeCollectionMapping();
		attributesMapping.setAttributeName("attributes");
		attributesMapping.setSetMethodName("setAttributesForTopLink");
		attributesMapping.setGetMethodName("getAttributesForTopLink");
		attributesMapping.setReferenceClass(MWClassAttribute.class);
		attributesMapping.setXPath("attributes/class-attribute");
		descriptor.addMapping(attributesMapping);
	
		XMLCompositeCollectionMapping ejb20AttributesMapping = new XMLCompositeCollectionMapping();
		ejb20AttributesMapping.setAttributeName("ejb20Attributes");
		ejb20AttributesMapping.setSetMethodName("setEjb20AttributesForTopLink");
		ejb20AttributesMapping.setGetMethodName("getEjb20AttributesForTopLink");
		ejb20AttributesMapping.setReferenceClass(MWClassAttribute.class);
		ejb20AttributesMapping.setXPath("ejb-20-attributes/class-attribute");
		descriptor.addMapping(ejb20AttributesMapping);
	
		XMLCompositeObjectMapping unknownPrimaryKeyAttributeMapping = new XMLCompositeObjectMapping();
		unknownPrimaryKeyAttributeMapping.setAttributeName("unknownPrimaryKeyAttribute");
		unknownPrimaryKeyAttributeMapping.setReferenceClass(MWClassAttribute.class);
		unknownPrimaryKeyAttributeMapping.setXPath("unknown-primary-key-attribute");
		descriptor.addMapping(unknownPrimaryKeyAttributeMapping);
	
		XMLCompositeCollectionMapping methodsMapping = new XMLCompositeCollectionMapping();
		methodsMapping.setAttributeName("methods");
		methodsMapping.setSetMethodName("setMethodsForTopLink");
		methodsMapping.setGetMethodName("getMethodsForTopLink");
		methodsMapping.setReferenceClass(MWMethod.class);
		methodsMapping.setXPath("methods/method");
		descriptor.addMapping(methodsMapping);
	
		XMLCompositeCollectionMapping typeHandlesMapping = new XMLCompositeCollectionMapping();
		typeHandlesMapping.setAttributeName("typeHandles");
		typeHandlesMapping.setSetMethodName("setTypeHandlesForTopLink");
		typeHandlesMapping.setGetMethodName("getTypeHandlesForTopLink");
		typeHandlesMapping.setReferenceClass(MWClassHandle.class);
		typeHandlesMapping.setXPath("type-handles/class-handle");
		descriptor.addMapping(typeHandlesMapping);
	
		XMLDirectMapping coreTypeMapping = (XMLDirectMapping) descriptor.addDirectMapping("coreType", "core-type/text()");
		coreTypeMapping.setNullValue(Boolean.FALSE);
	
		return descriptor;

	}

	/**
	 * legacy projects may contain references to org.eclipse.persistence.publicinterface.Descriptor which has been removed.
	 */
	private String legacyGetNameForToplink() {
		return this.name;
	}
	
	private void legacySetNameForToplink(String legacyName) {
		this.name = MWModel.legacyReplaceToplinkDeprecatedClassReferences(legacyName);
	}
	
	// ********** static methods **********
	
	/**
	 * if the specified class is in the default package, we
	 * will return "(default package)"
	 */
	public static String packageDisplayNameForClassNamed(String className) {
		if (nonReferenceClassNamesContains(className)) {
			return "";
		}
		int lastPeriod = className.lastIndexOf('.');
		if (lastPeriod == -1) {
			return "(default package)";
		}
		return className.substring(0, lastPeriod);
	}
	
	/**
	 * if the specified class is in the default package, we
	 * will return "(default package)"
	 */
	public static String packageDisplayNameFor(Class javaClass) {
		return packageDisplayNameForClassNamed(javaClass.getName());
	}
	
	private static PrimitiveWrapperPair[] buildPrimitiveWrapperPairs() {
		PrimitiveWrapperPair[] result = new PrimitiveWrapperPair[8];
		result[0] = new PrimitiveWrapperPair(boolean.class, java.lang.Boolean.class);
		result[1] = new PrimitiveWrapperPair(char.class, java.lang.Character.class);
		result[2] = new PrimitiveWrapperPair(byte.class, java.lang.Byte.class);
		result[3] = new PrimitiveWrapperPair(short.class, java.lang.Short.class);
		result[4] = new PrimitiveWrapperPair(int.class, java.lang.Integer.class);
		result[5] = new PrimitiveWrapperPair(long.class, java.lang.Long.class);
		result[6] = new PrimitiveWrapperPair(float.class, java.lang.Float.class);
		result[7] = new PrimitiveWrapperPair(double.class, java.lang.Double.class);
		return result;
	}
	
	private static synchronized PrimitiveWrapperPair[] getPrimitiveWrapperPairs() {
		if (primitiveWrapperPairs == null) {
			primitiveWrapperPairs = buildPrimitiveWrapperPairs();
		}
		return primitiveWrapperPairs;
	}
	
	private static Iterator primitiveWrapperPairs() {
		return CollectionTools.iterator(getPrimitiveWrapperPairs());
	}
	
	private static Iterator primitiveClasses(Iterator pairs) {
		return new TransformationIterator(pairs) {
			protected Object transform(Object next) {
				return ((PrimitiveWrapperPair) next).getPrimitiveClass();
			}
			public String toString() {
				return "MWClass.primitiveClasses(Iterator)";
			}
		};
	}
	
	private static Iterator primitiveClassNames(Iterator pairs) {
		return new TransformationIterator(pairs) {
			protected Object transform(Object next) {
				return ((PrimitiveWrapperPair) next).primitiveClassName();
			}
			public String toString() {
				return "MWClass.primitiveClassNames(Iterator)";
			}
		};
	}
	
	private static Iterator wrapperClasses(Iterator pairs) {
		return new TransformationIterator(pairs) {
			protected Object transform(Object next) {
				return ((PrimitiveWrapperPair) next).getWrapperClass();
			}
			public String toString() {
				return "MWClass.wrapperClasses(Iterator)";
			}
		};
	}
	
	/**
	 * return the Java primitive class with the specified name
	 * (e.g. int, char); does *not* include void;
	 * if the name is not for a primitive, return null
	 */
	public static Class primitiveClassNamed(String className) {
		PrimitiveWrapperPair[] pairs = getPrimitiveWrapperPairs();
		int len = pairs.length;
		for (int i = 0; i < len; i++) {
			if (pairs[i].primitiveClassName().equals(className)) {
				return pairs[i].getPrimitiveClass();
			}
		}
		return null;
	}
	
	/**
	 * return the Java primitive classes
	 * (e.g. int, char); does *not* include void
	 */
	public static Iterator primitiveClasses() {
		return primitiveClasses(primitiveWrapperPairs());
	}
	
	/**
	 * return the Java primitive wrapper classes
	 * (e.g. java.lang.Integer, java.lang.Character);
	 * does *not* include java.lang.Void
	 */
	public static Iterator primitiveWrapperClasses() {
		return wrapperClasses(primitiveWrapperPairs());
	}
	
	/**
	 * return the names of the Java primitive classes
	 * (e.g. "int", "char"); does *not* include "void"
	 */
	public static Iterator primitiveClassNames() {
		return primitiveClassNames(primitiveWrapperPairs());
	}
	
	private static synchronized PrimitiveWrapperPair getVoidPrimitiveWrapperPair() {
		if (voidPrimitiveWrapperPair == null) {
			voidPrimitiveWrapperPair = new PrimitiveWrapperPair(void.class, java.lang.Void.class);
		}
		return voidPrimitiveWrapperPair;
	}
	
	public static Class voidClass() {
		return getVoidPrimitiveWrapperPair().getPrimitiveClass();
	}
	
	public static Class voidWrapperClass() {
		return getVoidPrimitiveWrapperPair().getWrapperClass();
	}
	
	public static String voidClassName() {
		return getVoidPrimitiveWrapperPair().primitiveClassName();
	}
	
	private static Iterator nonReferencePrimitiveWrapperPairs() {
		return new CompositeIterator(
			CollectionTools.singletonIterator(getVoidPrimitiveWrapperPair()),
			primitiveWrapperPairs()
		);
	}
	
	/**
	 * return the Java non-reference classes;
	 * this includes the primitives and void
	 * (e.g. int, char, boolean, void)
	 */
	public static Iterator nonReferenceClasses() {
		return primitiveClasses(nonReferencePrimitiveWrapperPairs());
	}
	
	/**
	 * return the names of the Java non-reference classes;
	 * this includes the primitives and void
	 * (e.g. "int", "char", "boolean", "void")
	 */
	public static Iterator nonReferenceClassNames() {
		return primitiveClassNames(nonReferencePrimitiveWrapperPairs());
	}
	
	/**
	 * return whether the specified class name is
	 * the name of a Java non-reference classes;
	 * this includes the primitives and void
	 * (e.g. "int", "char", "boolean", "void")
	 */
	public static boolean nonReferenceClassNamesContains(String className) {
		return CollectionTools.contains(nonReferenceClassNames(), className);
	}
	
	/**
	 * return the Java non-reference wrapper classes;
	 * this includes the primitive wrappers and the void "wrapper"
	 * (e.g. java.lang.Integer, java.lang.Character, java.lang.Void)
	 */
	public static Iterator nonReferenceWrapperClasses() {
		return wrapperClasses(nonReferencePrimitiveWrapperPairs());
	}
	

	// ********** member classes **********
	
	/**
	 * This private class simply associates a primitive with its corresponding
	 * wrapper class (and vice versa). Once all the necessary instances
	 * of this class are built, we can use them to determine a primitive's
	 * wrapper class (or vice versa) with a generic loop of code,
	 * as opposed to a pair of case statements. And if another primitive
	 * is ever added to Java, we're ready. :-)
	 * 
	 * @see MWClass#buildPrimitiveWrapperPairs()
	 */
	private static class PrimitiveWrapperPair {
		private Class primitiveClass;
		private Class wrapperClass;

		PrimitiveWrapperPair(Class primitiveClass, Class wrapperClass) {
			super();
			this.primitiveClass = primitiveClass;
			this.wrapperClass = wrapperClass;
		}

		Class getPrimitiveClass() {
			return this.primitiveClass;
		}

		Class getWrapperClass() {
			return this.wrapperClass;
		}

		String primitiveClassName() {
			return this.getPrimitiveClass().getName();
		}

		String wrapperClassName() {
			return this.getWrapperClass().getName();
		}

	}

}
