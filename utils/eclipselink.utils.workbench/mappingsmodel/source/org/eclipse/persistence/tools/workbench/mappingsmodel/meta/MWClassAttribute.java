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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle.NodeReferenceScrubber;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.internal.codegen.NonreflectiveAttributeDefinition;

/**
 * This class models a Java class attribute and, if the attribute
 * is a collection (or map) or value holder, provides an extra bit of type information:
 * 	- the type of the value of the attribute (in the case of a value holder),
 * 	- the type of the elements in the collection (or entry values in a map) 
 * 		(in the case the attribute *or* the value type is a collection (or map))
 *  - the type of the keys for the map (in the case the attribute *or* the value type is a map)
 */
public final class MWClassAttribute extends MWModel 
	implements MWModifiable {
		
	private volatile String name;
		public static final String NAME_PROPERTY = "name";
	
	private MWModifier modifier;		// pseudo-final

	/**
	 * Property change notification occurs here
	 */
	private MWTypeDeclaration typeDeclaration;		// pseudo-final
		public static final String TYPE_PROPERTY = "type";
		public static final String DIMENSIONALITY_PROPERTY = "dimensionality";

	/** "virtual" property corresponding to the attribute's "declaration" */
		public static final String DECLARATION_PROPERTY = "declaration";
	
	/**
	 * if the attribute is a value holder,
	 * this indicates the type of object held by the value holder;
	 * for now there is no need to support arrays...
	 */
	private MWClassHandle valueTypeHandle;
		public static final String VALUE_TYPE_PROPERTY = "valueType";
	
	/**
	 * if the attribute is a collection (or map),
	 * this indicates the type of element held by the collection
	 * 		(or entry values held by the map);
	 * for now there is no need to support arrays...
	 */
	private MWClassHandle itemTypeHandle;
		public static final String ITEM_TYPE_PROPERTY = "itemType";
	
	/**
	 * if the attribute is a map,
	 * this indicates the type of entry keys used by the map;
	 * for now there is no need to support arrays...
	 */
	private MWClassHandle keyTypeHandle;
		public static final String KEY_TYPE_PROPERTY = "keyType";
	
	/**
	 * the accessor used to directly get the attribute
	 */
	private MWMethodHandle getMethodHandle;
		public static final String GET_METHOD_PROPERTY = "getMethod";
	
	/**
	 * the accessor used to directly set the attribute
	 */
	private MWMethodHandle setMethodHandle;
		public static final String SET_METHOD_PROPERTY = "setMethod";
	
	/**
	 * the accessor used to get the value of the attribute (for value holders only)
	 */
	private MWMethodHandle valueGetMethodHandle;
		public static final String VALUE_GET_METHOD_PROPERTY = "valueGetMethod";
	
	/** 
	 * the accessor used to set the value of the attribute (for value holders only)
	 */
	private MWMethodHandle valueSetMethodHandle;
		public static final String VALUE_SET_METHOD_PROPERTY = "valueSetMethod";
	
	/**
	 * the accessor used to add an item to the collection (or map)
	 */
	private MWMethodHandle addMethodHandle;
		public static final String ADD_METHOD_PROPERTY = "addMethod";
	
	/**
	 * the accessor used to remove an item from the collection (or map)
	 */
	private MWMethodHandle removeMethodHandle;
		public static final String REMOVE_METHOD_PROPERTY = "removeMethod";
	
	
	/** method name prefixes and suffixes */
	private static final String GET_PREFIX 	= "get";
	private static final String IS_PREFIX 	= "is";
	private static final String SET_PREFIX 	= "set";
	private static final String ADD_PREFIX 	= "addTo";
	private static final String REMOVE_PREFIX = "removeFrom";
	private static final String HOLDER_SUFFIX = "Holder";
	private static final String CR = StringTools.CR;


	// ********** constructors **********

	/**
	 * Default constructor - for TopLink use only.
	 */
	private MWClassAttribute() {
		super();
	}

	MWClassAttribute(MWClass parent, ExternalField externalField) {
		this(parent, externalField.getName());
		this.refresh(externalField);
	}

	MWClassAttribute(MWClass parent, String name) {
		// default type is java.lang.Object
		this(parent, name, parent.typeFor(java.lang.Object.class));
	}
	
	MWClassAttribute(MWClass parent, String name, MWClass type) {
		this(parent, name, type, 0);
	}

	MWClassAttribute(MWClass parent, String name, MWClass type, int dimensionality) {
		super(parent);
		this.name = name;
		this.initialize(type, dimensionality);
	}
	

	// ********** initialization **********
	
	/**
	 * initalize transient state
	 */
	protected void initialize() {
		super.initialize();
		this.modifier = new MWModifier(this);
	}
	
	/**
	 * some state is determined by the declaring type
	 */
	protected void initialize(Node parent) {	
		super.initialize(parent);
		
		if (this.getDeclaringType().isInterface()) {
			this.modifier.setPublic(true);
			this.modifier.setStatic(true);
			this.modifier.setFinal(true);
		}
		
		this.getMethodHandle = new MWMethodHandle(this, this.buildGetMethodScrubber());
		this.setMethodHandle = new MWMethodHandle(this, this.buildSetMethodScrubber());
		this.valueGetMethodHandle = new MWMethodHandle(this, this.buildValueGetMethodScrubber());
		this.valueSetMethodHandle = new MWMethodHandle(this, this.buildValueSetMethodScrubber());
		this.addMethodHandle = new MWMethodHandle(this, this.buildAddMethodScrubber());
		this.removeMethodHandle = new MWMethodHandle(this, this.buildRemoveMethodScrubber());

		this.valueTypeHandle = new MWClassHandle(this, this.buildValueTypeScrubber());	
		this.itemTypeHandle = new MWClassHandle(this, this.buildItemTypeScrubber());
		this.keyTypeHandle = new MWClassHandle(this, this.buildKeyTypeScrubber());
	}

	private void initialize(MWClass type, int dimensionality) {
		this.typeDeclaration = new MWTypeDeclaration(this, type, dimensionality);
	// do NOT update the nested types here - it's not necessary and it
	// will cause a stack overflow when initializing the base types...  ~bjv
	//	this.updateNestedTypes();
	}
	

	// ********** accessors **********
	
	public MWClass getDeclaringType() {
		return (MWClass) this.getParent();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
		if (this.attributeValueHasChanged(old, name)) {
			this.firePropertyChanged(DECLARATION_PROPERTY, "name");	// don't waste time building the declaration - it's ignored
			this.getProject().nodeRenamed(this);
			this.updateAccessorNames();
		}
	}
	
	public MWModifier getModifier() {
		return this.modifier;
	}
	
	public MWClass getType() {
		return this.typeDeclaration.getType();
	}
	
	public void setType(MWClass type) {
		if (type.isVoid()) {
			throw new IllegalArgumentException("An attribute can not have a type of 'void'");
		}
		MWClass old = this.typeDeclaration.getType();
		this.typeDeclaration.setType(type);
		this.firePropertyChanged(TYPE_PROPERTY, old, type);
		if (old != type) {
			this.firePropertyChanged(DECLARATION_PROPERTY, "type");	// don't waste time building the declaration - it's ignored
			this.updateNestedTypes(old);
			this.updateGetAndSetMethods();
		}
	}
	
	public int getDimensionality() {
		return this.typeDeclaration.getDimensionality();
	}
	
	public void setDimensionality(int dim) {
		int old = this.typeDeclaration.getDimensionality();
		this.typeDeclaration.setDimensionality(dim);
		this.firePropertyChanged(DIMENSIONALITY_PROPERTY, old, dim);
		if (old != dim) {
			this.firePropertyChanged(DECLARATION_PROPERTY, "dimensionality");	// don't waste time building the declaration - it's ignored
			this.updateNestedTypes();
			this.updateGetAndSetMethods();
		}
	}
	
	/**
	 * the type of object held by a value holder
	 */
	public MWClass getValueType() {
		MWClass valueType = this.valueTypeHandle.getType();
		if ((valueType == null) && this.canHaveValueType()) {
			valueType = this.objectType();
		}
		return valueType;
	}
	
	/**
	 * the type of object held by a value holder
	 */
	public void setValueType(MWClass valueType) {
		if (this.canHaveValueType() ^ (valueType != null)) {
			throw new IllegalArgumentException(valueType.toString());
		}
		Object old = this.getValueType();
		this.valueTypeHandle.setType((valueType == this.objectType()) ? null : valueType);
		this.firePropertyChanged(VALUE_TYPE_PROPERTY, this.getValueType());
		if (old != this.getValueType()) {
			this.updateContainerTypes();
			this.updateValueGetAndSetMethods();
		}
	}
	
	/**
	 * the type of items held by a container 
	 * 	(elements by a collection or entry values by a map)
	 */
	public MWClass getItemType() {
		MWClass itemType = this.itemTypeHandle.getType();
		if ((itemType == null) && this.canHaveItemType()) {
			itemType = this.objectType();
		}
		return itemType;
	}
	
	/**
	 * the type of items held by a container 
	 * 	(elements by a collection or entry values by a map)
	 */
	public void setItemType(MWClass itemType) {
		if (this.canHaveItemType() ^ (itemType != null)) {
			throw new IllegalArgumentException(itemType.toString());
		}
		Object old = this.getItemType();
		this.itemTypeHandle.setType((itemType == this.objectType()) ? null : itemType);
		this.firePropertyChanged(ITEM_TYPE_PROPERTY, this.getItemType());
		if (old != this.getItemType()) {
			this.updateAddAndRemoveMethods();
		}
	}
	
	/**
	 * the type of key used by a map
	 */
	public MWClass getKeyType() {
		MWClass keyType = this.keyTypeHandle.getType();
		if ((keyType == null) && this.canHaveKeyType()) {
			keyType = this.objectType();
		}
		return keyType;
	}
	
	/**
	 * the type of key used by a map
	 */
	public void setKeyType(MWClass keyType) {
		if (this.canHaveKeyType() ^ (keyType != null)) {
			throw new IllegalArgumentException(keyType.toString());
		}
		Object old = this.getKeyType();
		this.keyTypeHandle.setType((keyType == this.objectType()) ? null : keyType);
		this.firePropertyChanged(KEY_TYPE_PROPERTY, old, this.getKeyType());
		if (old != this.getKeyType()) {
			this.updateAddAndRemoveMethods();
		}
	}

	public MWMethod getGetMethod() {
		return this.getMethodHandle.getMethod();
	}

	public void setGetMethod(MWMethod getMethod) {
		MWMethod old = this.getGetMethod();
		this.getMethodHandle.setMethod(getMethod);	
		this.firePropertyChanged(GET_METHOD_PROPERTY, old, getMethod);
		if (old != getMethod) {
			if (old != null) {
				old.setAccessedAttribute(null);
			}
			if (getMethod != null) {
				getMethod.setAccessedAttribute(this);
			}
		}
	}
	
	public MWMethod getSetMethod() {
		return this.setMethodHandle.getMethod();
	}

	public void setSetMethod(MWMethod setMethod) {
		MWMethod old = this.getSetMethod();
		this.setMethodHandle.setMethod(setMethod);
		this.firePropertyChanged(SET_METHOD_PROPERTY, old, setMethod);
		if (old != setMethod) {
			if (old != null) {
				old.setAccessedAttribute(null);
			}
			if (setMethod != null) {
				setMethod.setAccessedAttribute(this);
			}
		}
	}
	
	public MWMethod getValueGetMethod() {
		return this.valueGetMethodHandle.getMethod();
	}
	
	public void setValueGetMethod(MWMethod valueGetMethod) {
		MWMethod old = this.getValueGetMethod();
		this.valueGetMethodHandle.setMethod(valueGetMethod);
		this.firePropertyChanged(VALUE_GET_METHOD_PROPERTY, old, valueGetMethod);
		if (old != valueGetMethod) {
			if (old != null) {
				old.setAccessedAttribute(null);
			}
			if (valueGetMethod != null) {
				valueGetMethod.setAccessedAttribute(this);
			}
		}
	}
	
	public MWMethod getValueSetMethod() {
		return this.valueSetMethodHandle.getMethod();
	}
	
	public void setValueSetMethod(MWMethod valueSetMethod) {
		MWMethod old = this.getValueSetMethod();
		this.valueSetMethodHandle.setMethod(valueSetMethod);
		this.firePropertyChanged(VALUE_SET_METHOD_PROPERTY, old, valueSetMethod);
		if (old != valueSetMethod) {
			if (old != null) {
				old.setAccessedAttribute(null);
			}
			if (valueSetMethod != null) {
				valueSetMethod.setAccessedAttribute(this);
			}
		}
	}
	
	public MWMethod getAddMethod() {
		return this.addMethodHandle.getMethod();
	}
	
	public void setAddMethod(MWMethod addMethod) {
		MWMethod old = this.getAddMethod();
		this.addMethodHandle.setMethod(addMethod);	
		this.firePropertyChanged(ADD_METHOD_PROPERTY, old, addMethod);
		if (old != addMethod) {
			if (old != null) {
				old.setAccessedAttribute(null);
			}
			if (addMethod != null) {
				addMethod.setAccessedAttribute(this);
			}
		}
	}
	
	public MWMethod getRemoveMethod() {
		return this.removeMethodHandle.getMethod();
	}
	
	public void setRemoveMethod(MWMethod removeMethod) {
		MWMethod old = this.getRemoveMethod();
		this.removeMethodHandle.setMethod(removeMethod);
		this.firePropertyChanged(REMOVE_METHOD_PROPERTY, old, removeMethod);
		if (old != removeMethod) {
			if (old != null) {
				old.setAccessedAttribute(null);
			}
			if (removeMethod != null) {
				removeMethod.setAccessedAttribute(this);
			}
		}
	}


	// ********** Modifiable implementation **********
	
	public boolean supportsAbstract() {
		return false;
	}
	
	public boolean canBeSetAbstract() {
		return false;
	}
	
	public boolean canBeSetFinal() {
		return ! this.getModifier().isVolatile();
	}
	
	public boolean supportsInterface() {
		return false;
	}
	
	public boolean canBeSetInterface() {
		return false;
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
		return true;
	}
	
	public boolean canBeSetProtected() {
		return true;
	}
	
	public boolean canBeSetPublic() {
		return true;
	}
	
	public boolean canBeSetStatic() {
		return true;
	}
	
	public boolean supportsStrict() {
		return false;
	}
	
	public boolean canBeSetStrict() {
		return false;
	}
	
	public boolean supportsSynchronized() {
		return false;
	}
	
	public boolean canBeSetSynchronized() {
		return false;
	}
	
	public boolean supportsTransient() {
		return true;
	}
	
	public boolean canBeSetTransient() {
		return true;
	}
	
	public boolean supportsVolatile() {
		return true;
	}
	
	public boolean canBeSetVolatile() {
		return ! this.getModifier().isFinal();
	}
	
	// 'final' and 'volatile' are mutually exclusive
	private static final int ALLOWED_MODIFIERS_FLAGS = Modifier.FINAL | Modifier.VOLATILE;

	public void modifierChanged(int oldCode, int newCode) {
		this.firePropertyChanged(MODIFIER_CODE_PROPERTY, oldCode, newCode);
		if (MWModifier.anyFlagsAreDifferent(ALLOWED_MODIFIERS_FLAGS, oldCode, newCode)) {
			this.modifier.allowedModifiersChanged();
		}
	}
	
	public void accessLevelChanged(String oldValue, String newValue) {
		this.firePropertyChanged(MODIFIER_ACCESS_LEVEL_PROPERTY, oldValue, newValue);
	}


	// ********** queries **********

	public boolean isStatic() {
		return this.getModifier().isStatic();
	}
	
	public boolean isFinal() {
		return this.getModifier().isFinal();
	}

	public String typeName() {
		return this.typeDeclaration.typeName();
	}

	public String typeDeclaration() {
		return this.typeDeclaration.declaration();
	}
	
	public boolean isArray() {
		return this.typeDeclaration.isArray();
	}
	
	public boolean isValueHolder() {
		return this.typeDeclaration.isValueHolder();
	}
	
	public boolean isTLValueHolder() {
		return this.typeDeclaration.isTLValueHolder();
	}

	public boolean isBooleanPrimitive() {
		return this.typeDeclaration.isBooleanPrimitive();
	}

	/**
	 * e.g. return true if the attribute type is Object
	 * and the specified type is String
	 */
	public boolean isAssignableFrom(MWClass type) {
		return this.typeDeclaration.isAssignableFrom(type);
	}
	
	public boolean mightBeAssignableFrom(MWClass type) {
		return this.typeDeclaration.mightBeAssignableFrom(type);
	}
	
	/**
	 * e.g. return true if the attribute is Object and the specified type is String
	 */
	public boolean isAssignableTo(MWClass type) {
		return this.typeDeclaration.isAssignableTo(type);
	}
	
	public boolean mightBeAssignableTo(MWClass type) {
		return this.typeDeclaration.mightBeAssignableTo(type);
	}
	
	public boolean isAssignableToCollection() {
		return this.typeDeclaration.isAssignableToCollection();
	}

	public boolean mightBeAssignableToCollection() {
		return this.typeDeclaration.mightBeAssignableToCollection();
	}

    public boolean isAssignableToList() {
        return this.typeDeclaration.isAssignableToList();
    }
    
    public boolean mightBeAssignableToList() {
        return this.typeDeclaration.mightBeAssignableToList();
    }
    
	public boolean isAssignableToMap() {
		return this.typeDeclaration.isAssignableToMap();
	}
	
	public boolean mightBeAssignableToMap() {
		return this.typeDeclaration.mightBeAssignableToMap();
	}
	
	public boolean isAssignableToSet() {
		return this.typeDeclaration.isAssignableToSet();
	}
	
	public boolean mightBeAssignableToSet() {
		return this.typeDeclaration.mightBeAssignableToSet();
	}
	
	public boolean isAssignableToIndirectContainer() {
		return this.typeDeclaration.isAssignableToIndirectContainer();
	}
	
	public boolean mightBeAssignableToIndirectContainer() {
		return this.typeDeclaration.mightBeAssignableToIndirectContainer();
	}
	
	public boolean isContainer() {
		return this.isAssignableToCollection()
				|| this.isAssignableToMap();
	}
	
	public boolean mightBeContainer() {
		return this.mightBeAssignableToCollection()
				|| this.mightBeAssignableToMap();
	}

	public boolean isInstanceVariable() {
		return ! this.isStatic();
	}
	
	public boolean isClassVariable() {
		return this.isStatic() && ! this.isFinal();
	}
	
	public boolean isConstant() {
		return this.isStatic() && this.isFinal();
	}

	public boolean isMappable() {
		return ! this.isStatic() && ! this.isFinal();
	}

	public boolean canHaveValueType() {
		return this.isValueHolder();
	}

	public boolean canHaveItemType() {
		return this.canHaveCollectionElementType()
				|| this.canHaveMapValueType();
	}

	public boolean canHaveCollectionElementType() {
		return this.mightBeAssignableToCollection()
				|| (this.isValueHolder() && this.getValueType().mightBeAssignableToCollection());
	}

	public boolean canHaveMapValueType() {
		return this.canHaveMapKeyAndValueTypes();
	}

	public boolean canHaveKeyType() {
		return this.canHaveMapKeyAndValueTypes();
	}
	
	/**
	 * return whether the attribute is a map and can
	 * have key and item (value) types
	 */
	public boolean canHaveMapKeyAndValueTypes() {
		return this.mightBeAssignableToMap()
				|| (this.isValueHolder() && this.getValueType().mightBeAssignableToMap());
	}

	public boolean canHaveValueGetAndSetMethods() {
		return this.canHaveValueType();
	}

	public boolean canHaveAddAndRemoveMethods() {
		return this.canHaveItemType();
	}

	/**
	 * return the configured get method if it is specified;
	 * otherwise return the "standard" get method if present;
	 * otherwise return null
	 */
	public MWMethod guessGetMethod() {
		MWMethod method = this.getGetMethod();
		return (method != null) ? method : this.standardGetMethod();
	}
	
	/**
	 * return the "standard" get method if present
	 */
	public MWMethod standardGetMethod() {
		return this.getDeclaringType().zeroArgumentMethodNamed(this.standardGetMethodName());
	}

	/**
	 * e.g. 'foo' => "getFoo[Holder]" or "isFoo"
	 */
	private String standardGetMethodName() {
		StringBuffer sb = new StringBuffer(this.getName().length() + 10);

		if (this.isBooleanPrimitive()) {
			sb.append(IS_PREFIX);
		} else {
			sb.append(GET_PREFIX);
		}

		StringTools.capitalizeOn(this.getName(), sb);

		if (this.isValueHolder()) {
			sb.append(HOLDER_SUFFIX);
		}

		return sb.toString();
	}
	
	/**
	 * return the configured set method if it is specified;
	 * otherwise return the "standard" set method if present;
	 * otherwise return null
	 */
	public MWMethod guessSetMethod() {
		MWMethod method = this.getSetMethod();
		return (method != null) ? method : this.standardSetMethod();
	}

	/**
	 * return the "standard" set method if present
	 */
	public MWMethod standardSetMethod() {
		return this.getDeclaringType().oneArgumentMethodNamed(this.standardSetMethodName(), this.typeDeclaration);
	}

	/**
	 * e.g. 'foo' => "setFoo[Holder]"
	 */
	private String standardSetMethodName() {
		StringBuffer sb = new StringBuffer(this.getName().length() + 10);

		sb.append(SET_PREFIX);
		StringTools.capitalizeOn(this.getName(), sb);

		if (this.isValueHolder()) {
			sb.append(HOLDER_SUFFIX);
		}

		return sb.toString();
	}
	
	/**
	 * return the configured "value" get method if it is specified;
	 * otherwise return the "standard" "value" get method if present;
	 * otherwise return null
	 */
	public MWMethod guessValueGetMethod() {
		MWMethod method = this.getValueGetMethod();
		return (method != null) ? method : this.standardValueGetMethod();
	}

	/**
	 * return the "standard" "value" get method if present
	 */
	public MWMethod standardValueGetMethod() {
		return this.getDeclaringType().zeroArgumentMethodNamed(this.standardValueGetMethodName());
	}
	
	/**
	 * e.g. 'foo' => "getFoo" or "isFoo"
	 */
	private String standardValueGetMethodName() {
		StringBuffer sb = new StringBuffer(this.getName().length() + 3);

		if (this.isBooleanPrimitive()) {
			sb.append(IS_PREFIX);
		} else {
			sb.append(GET_PREFIX);
		}

		StringTools.capitalizeOn(this.getName(), sb);

		return sb.toString();
	}
	
	/**
	 * return the configured "value" set method if it is specified;
	 * otherwise return the "standard" "value" set method if present;
	 * otherwise return null
	 */
	public MWMethod guessValueSetMethod() {
		MWMethod method = this.getValueSetMethod();
		return (method != null) ? method : this.standardValueSetMethod();
	}
	
	/**
	 * return the "standard" "value" set method if present
	 */
	public MWMethod standardValueSetMethod() {
		return this.getDeclaringType().oneArgumentMethodNamed(this.standardValueSetMethodName(), this.getValueType());
	}
	
	/**
	 * e.g. 'foo' => "setFoo"
	 */
	private String standardValueSetMethodName() {
		StringBuffer sb = new StringBuffer(this.getName().length() + 3);

		sb.append(SET_PREFIX);
		StringTools.capitalizeOn(this.getName(), sb);

		return sb.toString();
	}
	
	/**
	 * return the configured add method if it is specified;
	 * otherwise return the "standard" add method if present;
	 * otherwise return null
	 */
	public MWMethod guessAddMethod() {
		MWMethod method = this.getAddMethod();
		return (method != null) ? method : this.standardAddMethod();
	}
	
	/**
	 * return the "standard" add method if present
	 */
	public MWMethod standardAddMethod() {
		if (this.canHaveKeyType()) {
			return this.getDeclaringType().twoArgumentMethodNamed(this.standardAddMethodName(), this.getKeyType(), this.getItemType());
		}
		return this.getDeclaringType().oneArgumentMethodNamed(this.standardAddMethodName(), this.getItemType());
	}
	
	/**
	 * e.g. 'bars' => "addToBars"
	 */
	private String standardAddMethodName() {
		StringBuffer sb = new StringBuffer(this.getName().length() + 5);

		sb.append(ADD_PREFIX);
		StringTools.capitalizeOn(this.getName(), sb);

		return sb.toString();
	}
	
	/**
	 * return the configured remove method if it is specified;
	 * otherwise return the "standard" remove method if present;
	 * otherwise return null
	 */
	public MWMethod guessRemoveMethod() {
		MWMethod method = this.getRemoveMethod();
		return (method != null) ? method : this.standardRemoveMethod();
	}
	
	/**
	 * return the "standard" remove method if present
	 */
	public MWMethod standardRemoveMethod() {
		MWClass parmType = this.canHaveKeyType() ? this.getKeyType() : this.getItemType();
		return this.getDeclaringType().oneArgumentMethodNamed(this.standardRemoveMethodName(), parmType);
	}
	
	/**
	 * e.g. 'bars' => "removeFromBars"
	 */
	private String standardRemoveMethodName() {
		StringBuffer sb = new StringBuffer(this.getName().length() + 10);

		sb.append(REMOVE_PREFIX);
		StringTools.capitalizeOn(this.getName(), sb);

		return sb.toString();
	}

	public Iterator candidateGetMethods() {
		return this.getDeclaringType().candidateGetMethodsFor(this);
	}

	public Iterator candidateSetMethods() {
		return this.getDeclaringType().candidateSetMethodsFor(this);
	}

	public Iterator candidateValueGetMethods() {
		return this.canHaveValueType() ?
			this.getDeclaringType().candidateGetMethodsFor(this.getValueType())
		:
			NullIterator.instance();
	}

	public Iterator candidateValueSetMethods() {
		return this.canHaveValueType() ?
			this.getDeclaringType().candidateSetMethodsFor(this.getValueType())
		:
			NullIterator.instance();
	}

	public Iterator candidateAddMethods() {
		return this.canHaveItemType() ?
			this.canHaveKeyType() ?
				this.getDeclaringType().candidateAddMethodsFor(this.getKeyType(), this.getItemType())
			:
				this.getDeclaringType().candidateAddMethodsFor(this.getItemType())
		:
			NullIterator.instance();
	}

	public Iterator candidateRemoveMethods() {
		return this.canHaveItemType() ?
			this.getDeclaringType().candidateRemoveMethodsFor(this.canHaveKeyType() ? this.getKeyType() : this.getItemType())
		:
			NullIterator.instance();
	}

	/**
	 * NB: some elements in the collection may be null
	 */
	public Collection allAccessors() {
		Collection accessors = new ArrayList();
		accessors.add(this.getGetMethod());
		accessors.add(this.getSetMethod());
		accessors.add(this.getValueGetMethod());
		accessors.add(this.getValueSetMethod());
		accessors.add(this.getAddMethod());
		accessors.add(this.getRemoveMethod());
		return accessors;
	}

	public MWMethodCodeGenPolicy accessorCodeGenPolicy(MWMethod accessor, MWClassCodeGenPolicy classCodeGenPolicy) {
		if (accessor == this.getGetMethod()) {
			return new MWGetMethodCodeGenPolicy(accessor, this, classCodeGenPolicy);
		}
		else if (accessor == this.getSetMethod()) {
			return new MWSetMethodCodeGenPolicy(accessor, this, classCodeGenPolicy);
		}
		else if (accessor == this.getValueGetMethod()) {
			return new MWValueGetMethodCodeGenPolicy(accessor, this, classCodeGenPolicy);
		}
		else if (accessor == this.getValueSetMethod()) {
			return new MWValueSetMethodCodeGenPolicy(accessor, this, classCodeGenPolicy);
		}
		else if (accessor == this.getAddMethod()) {
			return new MWAddMethodCodeGenPolicy(accessor, this, classCodeGenPolicy);
		}
		else if (accessor == this.getRemoveMethod()) {
			return new MWRemoveMethodCodeGenPolicy(accessor, this, classCodeGenPolicy);
		}
		else {
			throw new IllegalArgumentException(accessor.toString());
		}
	}
	
	public MWMethodCodeGenPolicy accessorCodeGenPolicy(MWMethod accessor, MWClassAttribute backPointerAttribute, boolean isPrivateOwned, MWClassCodeGenPolicy classCodeGenPolicy) {
		if (backPointerAttribute == null) {
			return this.accessorCodeGenPolicy(accessor, classCodeGenPolicy);
		}
		if (accessor == this.getAddMethod()) {
			return new MWAddMethodCodeGenPolicy(accessor, this, backPointerAttribute, classCodeGenPolicy);
		}
		else if (accessor == this.getRemoveMethod()) {
			return new MWRemoveMethodCodeGenPolicy(accessor, this, backPointerAttribute, isPrivateOwned, classCodeGenPolicy);
		}
		else {
			throw new IllegalArgumentException(accessor.toString());
		}
	}

	public boolean isEjb20Attribute() {
		return this.getDeclaringType().ejb20AttributesContains(this);
	}

	public boolean isUnknownPrimaryKeyAttribute() {
		return this.getDeclaringType().getUnknownPrimaryKeyAttribute() == this;
	}

	/**
	 * code gen:
	 * used by Wallace code gen
	 */
	NonreflectiveAttributeDefinition attributeDefinition() {
		NonreflectiveAttributeDefinition def = new NonreflectiveAttributeDefinition();
		def.setAccessLevel(this.getModifier().accessLevel());
		def.setType(this.typeDeclaration.declaration());
		def.setName(this.getName());
		return def;
	}	
	
	/**
	 * code gen:
	 * initial value is only necessary when the attribute
	 * is a value holder or a container
	 */
	public String initialValueSourceCode(MWClassCodeGenPolicy classCodeGenPolicy) {
		MWClass concreteValueType = null;
		if (this.isValueHolder()) {
			concreteValueType = this.getValueType();
		}
		else if (this.getDimensionality() == 0) {
			concreteValueType = this.getType();
		} else {
			// leave 'concreteValueType' null for an array
			concreteValueType = null;
		}

		if (concreteValueType == null) {
			// "new ValueHolder()" or ""
			return this.initialValueSourceCodeFor(null);
		}

		if (concreteValueType.isContainer()) {
			return this.initialContainerValueSourceCodeFor(classCodeGenPolicy, concreteValueType);
		}

		// "new ValueHolder()" or ""
		return this.initialValueSourceCodeFor(null);
	}

	/**
	 * code gen:
	 * return code to construct a new container, if possible
	 */
	private String initialContainerValueSourceCodeFor(MWClassCodeGenPolicy classCodeGenPolicy, MWClass containerValueType) {
		MWClass containerImplementationType = containerValueType.defaultContainerImplementationType();

		if (containerImplementationType != null) {
			// e.g. "new ValueHolder(new ArrayList())" or "new ArrayList()"
			return this.initialValueSourceCodeFor(containerImplementationType);
		}

		StringBuffer sb = new StringBuffer(200);
		sb.append(CR);
		sb.append("\t\t");
		sb.append(classCodeGenPolicy.collectionImplementationClassNotDeterminedComment(this, containerValueType));
		sb.append(CR);
		sb.append("\t\t");
		// "new ValueHolder()" or ""
		this.appendInitialValueSourceCodeFor(null, sb);
		return sb.toString();
	}

	/**
	 * code gen:
	 * the specified concrete value type is the default "value" type for the attribute
	 * (null if it is unknown); we use the zero-argument constructor if appropriate;
	 * we return an empty string if the attribute is not a value holder and the concrete
	 * value type is null or cannot be instantiated
	 */
	public String initialValueSourceCodeFor(MWClass concreteValueType) {
		StringBuffer sb = new StringBuffer(80);
		this.appendInitialValueSourceCodeFor(concreteValueType, sb);
		return sb.toString();
	}

	private void appendInitialValueSourceCodeFor(MWClass concreteValueType, StringBuffer sb) {
		if (concreteValueType != null) {
			if ( ! concreteValueType.isInstantiable()) {
				concreteValueType = null;
			}
		}

		boolean valueHolder = this.isValueHolder();

		if (valueHolder) {
			sb.append("new ");
			sb.append(this.typeFor(ValueHolder.class).getName());
			sb.append('(');
		}
		
		if (concreteValueType != null) {
			sb.append("new ");
			sb.append(concreteValueType.getName());
			sb.append("()");
		}
		
		if (valueHolder) {
			sb.append(')');
		}
	}

	private MWClass objectType() {
		return this.typeFor(java.lang.Object.class);
	}


	// ********** behavior **********

	void refresh(ExternalField externalField) {
		if ( ! this.getName().equals(externalField.getName())) {
			throw new IllegalArgumentException(externalField.getName());
		}
		this.getModifier().refresh(externalField.getModifiers());
		this.typeDeclaration.refresh(externalField.getType());
		// the value, element, and key types are set manually - they cannot be refreshed from the Java field
	}

	/**
	 * This is used by MWMethod to ensure that a method can only be an accessor for a single 
	 * 	attribute. This is called by the method before the new attribute-accessor relationship
	 * 	is set up.
	 */
	void removeAccessorMethod(MWMethod method) {
		if (method == this.getGetMethod()) {
			this.setGetMethod(null);
		}
		else if (method == this.getSetMethod()) {
			this.setSetMethod(null);
		}
		else if (method == this.getValueGetMethod()) {
			this.setValueGetMethod(null);
		}
		else if (method == this.getValueSetMethod()) {
			this.setValueSetMethod(null);
		}
		else if (method == this.getAddMethod()) {
			this.setAddMethod(null);
		}
		else if (method == this.getRemoveMethod()) {
			this.setRemoveMethod(null);
		}
	}

	/**
	 * - when converting to a value holder move the old, non-value holder, type
	 *   to the value type;
	 * - when converting to a container move the old, non-container, type
	 *   to the element type;
	 * - when converting from a container within a value holder to a container,
	 *   leave the item type alone
	 */
	private void updateNestedTypes(MWClass oldType) {
		if (this.getDimensionality() == 0) {
			if (this.getType().isValueHolder() && ! oldType.isValueHolder()) {
				this.setValueType(oldType);
			}
			if (this.getType().isContainer() && ! oldType.isContainer()) {
				// don't call 'this.getItemType()' because it will not return null at this point
				if (this.itemTypeHandle.getType() == null) {
					this.setItemType(oldType);
				}
			}
		}
		this.updateNestedTypes();
	}
	
	private void updateNestedTypes() {
		this.updateValueType();
		this.updateContainerTypes();
	}

	private void updateValueType() {
		if ( ! this.canHaveValueType()) {
			this.setValueType(null);
		}
		this.updateValueGetAndSetMethods();
	}

	private void updateContainerTypes() {
		this.updateItemType();
		this.updateKeyType();
	}
	
	private void updateItemType() {
		if ( ! this.canHaveItemType()) {
			this.setItemType(null);
		}
		this.updateAddAndRemoveMethods();
	}
	
	private void updateKeyType() {
		if ( ! this.canHaveKeyType()) {
			this.setKeyType(null);
		}
		this.updateAddAndRemoveMethods();
	}

	private void updateGetAndSetMethods() {
		this.updateZeroArgumentMethod(this.getGetMethod(), this.getType(), this.getDimensionality());
		this.updateSingleArgumentMethod(this.getSetMethod(), this.getType(), this.getDimensionality());
	}
	
	private void updateValueGetAndSetMethods() {
		if (this.canHaveValueType()) {
			this.updateZeroArgumentMethod(this.getValueGetMethod(), this.getValueType());
			this.updateSingleArgumentMethod(this.getValueSetMethod(), this.getValueType());
		} else {
			this.setValueGetMethod(null);
			this.setValueSetMethod(null);
		}
	}
	
	private void updateAddAndRemoveMethods() {
		if (this.canHaveItemType()) {
			if (this.canHaveKeyType()) {
				this.updateTwoArgumentMethod(this.getAddMethod(), this.getKeyType(), this.getItemType());
				this.updateSingleArgumentMethod(this.getRemoveMethod(), this.getKeyType());
			} else {
				this.updateSingleArgumentMethod(this.getAddMethod(), this.getItemType());
				this.updateSingleArgumentMethod(this.getRemoveMethod(), this.getItemType());
			}
		} else {
			this.setAddMethod(null);
			this.setRemoveMethod(null);
		}
	}
	
	private void updateZeroArgumentMethod(MWMethod method, MWClass returnType) {
		this.updateZeroArgumentMethod(method, returnType, 0);
	}

	private void updateZeroArgumentMethod(MWMethod method, MWClass returnType, int returnTypeDimensionality) {
		if (method == null) {
			return;
		}
		method.setReturnType(returnType);
		method.setReturnTypeDimensionality(returnTypeDimensionality);
		method.clearMethodParameters();
	}

	private void updateSingleArgumentMethod(MWMethod method, MWClass argumentType) {
		this.updateSingleArgumentMethod(method, argumentType, 0);
	}

	private void updateSingleArgumentMethod(MWMethod method, MWClass argumentType, int argumentDimensionality) {
		this.updateMethodArguments(method, new MWClass[] {argumentType}, new int[] {argumentDimensionality});
	}

	private void updateTwoArgumentMethod(MWMethod method, MWClass argumentType1, MWClass argumentType2) {
		this.updateTwoArgumentMethod(method, argumentType1, 0, argumentType2, 0);
	}

	private void updateTwoArgumentMethod(MWMethod method, MWClass argumentType1, int argumentDimensionality1, MWClass argumentType2, int argumentDimensionality2) {
		this.updateMethodArguments(method, new MWClass[] {argumentType1, argumentType2}, new int[] {argumentDimensionality1, argumentDimensionality2});
	}

	private void updateMethodArguments(MWMethod method, MWClass[] argumentTypes, int[] argumentDimensionalities) {
		if (method == null) {
			return;
		}
		int len = argumentTypes.length;
		if (argumentDimensionalities.length != len) {
			throw new IllegalArgumentException("arrays are different lengths");
		}
		if (method.methodParametersSize() == 0) {
			for (int i = 0; i < len; i++) {
				method.addMethodParameter(argumentTypes[i], argumentDimensionalities[i]);
			}
		} else {
			while (method.methodParametersSize() < len) {
				int index = method.methodParametersSize();
				method.addMethodParameter(argumentTypes[index], argumentDimensionalities[index]);
			}
			while (method.methodParametersSize() > len) {
				method.removeMethodParameter(len);
			}
			for (int i = 0; i < len; i++) {
				method.getMethodParameter(i).setType(argumentTypes[i]);
				method.getMethodParameter(i).setDimensionality(argumentDimensionalities[i]);
			}
		}
	}

	private void updateAccessorNames() {
		if (this.getGetMethod() != null) {
			this.getGetMethod().setName(this.standardGetMethodName());
		}
		if (this.getSetMethod() != null) {
			this.getSetMethod().setName(this.standardSetMethodName());
		}
		if (this.getValueGetMethod() != null) {
			this.getValueGetMethod().setName(this.standardValueGetMethodName());
		}
		if (this.getValueSetMethod() != null) {
			this.getValueSetMethod().setName(this.standardValueSetMethodName());
		}
		if (this.getAddMethod() != null) {
			this.getAddMethod().setName(this.standardAddMethodName());
		}
		if (this.getRemoveMethod() != null) {
			this.getRemoveMethod().setName(this.standardRemoveMethodName());
		}
	}

	/**
	 * Generate methods:
	 *     - get and set methods @see #generateGetAndSetMethods()
	 *     - "value" get and set methods if appropriate @see #generateValueGetAndSetMethods()
	 *     - add and remove methods if appropriate @see #generateAddAndRemoveMethods() .
	 */
	public void generateAllAccessors() {
		this.generateGetAndSetMethods();
		if (this.canHaveValueGetAndSetMethods()) {
			this.generateValueGetAndSetMethods();
		}
		if (this.canHaveAddAndRemoveMethods()) {
			this.generateAddAndRemoveMethods();
		}
	}

	public void generateGetAndSetMethods() {
		this.generateGetMethod();
		this.generateSetMethod();
	}
	
	private void generateGetMethod() {
		MWMethod getMethod = this.guessGetMethod();
		if (getMethod == null) {
			getMethod = this.getDeclaringType().addMethod(this.standardGetMethodName());
			// new getters for value holders, collections, or maps should be protected
			if (this.isValueHolder() || this.isContainer()) {
				getMethod.getModifier().setProtected(true);
			}
		}
		
		getMethod.setReturnType(this.getType());
		getMethod.setReturnTypeDimensionality(this.getDimensionality());
		
		// EJB getters must be abstract; non-EJB getters must not be abstract
		getMethod.getModifier().setAbstract(this.isEjb20Attribute());

		// EJB getters must be public.  
		if (this.isEjb20Attribute()) {
			getMethod.getModifier().setPublic(true);
		}

		this.setGetMethod(getMethod);
	}

	private void generateSetMethod() {
		MWMethod setMethod = this.guessSetMethod();

		if (setMethod == null) {
			setMethod = this.getDeclaringType().addMethod(this.standardSetMethodName());
			setMethod.addMethodParameter(this.getType(), this.getDimensionality());
			// new setters for value holders, collections, or maps should be protected
			if (this.isValueHolder() || this.isContainer()) {
				setMethod.getModifier().setProtected(true);
			}
		}

		// EJB setters must be abstract; non-EJB setters must not be abstract
		setMethod.getModifier().setAbstract(this.isEjb20Attribute());

		// EJB setters must be public and must return void
		if (this.isEjb20Attribute()) {
			setMethod.getModifier().setPublic(true);
			setMethod.setReturnType(this.getRepository().voidType());
		}

		this.setSetMethod(setMethod);
	}

	public void generateValueGetAndSetMethods() {
		this.generateValueGetMethod();
		this.generateValueSetMethod();
	}

	public void generateValueGetMethod() {
		MWMethod valueGetMethod = this.guessValueGetMethod();
		if (valueGetMethod == null) {
			valueGetMethod = this.getDeclaringType().addMethod(this.standardValueGetMethodName());
			// vew value get methods for collections or maps should be protected
			if (this.getValueType().isContainer()) {
				valueGetMethod.getModifier().setProtected(true);
			}
		}
		valueGetMethod.setReturnType(this.getValueType());
		valueGetMethod.setReturnTypeDimensionality(0);
		this.setValueGetMethod(valueGetMethod);
	}
	
	public void generateValueSetMethod() {
		MWMethod valueSetMethod = this.guessValueSetMethod();
		if (valueSetMethod == null) {
			valueSetMethod = this.getDeclaringType().addMethod(this.standardValueSetMethodName());
			valueSetMethod.addMethodParameter(this.getValueType());
			// new value set methods for collections or maps should be protected
			if (this.getValueType().isContainer()) {
				valueSetMethod.getModifier().setProtected(true);
			}
		}
		this.setValueSetMethod(valueSetMethod);
	}
	
	public void generateAddAndRemoveMethods() {
		this.generateAddMethod();
		this.generateRemoveMethod();
	}
	
	private void generateAddMethod() {
		if (this.canHaveKeyType()) {
			this.generateAddMethodForMap();
		} else {
			this.generateAddMethodForCollection();
		}
	}
	
	private void generateAddMethodForCollection() {
		MWMethod addMethod = this.guessAddMethod();
		if (addMethod == null) {
			addMethod = this.getDeclaringType().addMethod(this.standardAddMethodName());
			addMethod.addMethodParameter(this.getItemType());
		}
		this.setAddMethod(addMethod);
	}
	
	private void generateAddMethodForMap() {
		MWMethod addMethod = this.guessAddMethod();
		if (addMethod == null) {
			addMethod = this.getDeclaringType().addMethod(this.standardAddMethodName());
			addMethod.addMethodParameter(this.getKeyType());
			addMethod.addMethodParameter(this.getItemType());
		}
		this.setAddMethod(addMethod);
	}
	
	private void generateRemoveMethod() {
		if (this.canHaveKeyType()) {
			this.generateRemoveMethodForMap();
		} else {
			this.generateRemoveMethodForCollection();
		}
	}
	
	private void generateRemoveMethodForCollection() {
		MWMethod removeMethod = this.guessRemoveMethod();
		if (removeMethod == null) {
			removeMethod = this.getDeclaringType().addMethod(this.standardRemoveMethodName());
			removeMethod.addMethodParameter(this.getItemType());
		}
		this.setRemoveMethod(removeMethod);
	}

	private void generateRemoveMethodForMap() {
		MWMethod removeMethod = this.guessRemoveMethod();
		if (removeMethod == null) {
			removeMethod = this.getDeclaringType().addMethod(this.standardRemoveMethodName());
			removeMethod.addMethodParameter(this.getKeyType());
		}
		this.setRemoveMethod(removeMethod);
	}

	public void setEjb20Attribute(boolean value) {
		if (this.isEjb20Attribute() == value) {
			return;
		}
		if (value) {
			this.getDeclaringType().changeToEjb20(this);
		} else {
			this.getDeclaringType().changeFromEjb20(this);
		}
	}

	public void addAccessorCodeGenPoliciesTo(MWClassCodeGenPolicy classCodeGenPolicy) {
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, this.getGetMethod());
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, this.getSetMethod());
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, this.getValueGetMethod());
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, this.getValueSetMethod());
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, this.getAddMethod());
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, this.getRemoveMethod());
	}

	private void addAccessorCodeGenPolicyTo(MWClassCodeGenPolicy classCodeGenPolicy, MWMethod method) {
		if (method != null) {
			classCodeGenPolicy.addAccessorCodeGenPolicy(method, this.accessorCodeGenPolicy(method, classCodeGenPolicy));
		}
	}

	public void addAccessorCodeGenPoliciesTo(MWClassCodeGenPolicy classCodeGenPolicy, MWMapping mapping) {
		// as of now, only add and remove methods have mapping-added information
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, mapping, this.getAddMethod());
		this.addAccessorCodeGenPolicyTo(classCodeGenPolicy, mapping, this.getRemoveMethod());
	}

	private void addAccessorCodeGenPolicyTo(MWClassCodeGenPolicy classCodeGenPolicy, MWMapping mapping, MWMethod method) {
		if (method != null) {
			classCodeGenPolicy.addAccessorCodeGenPolicy(method, mapping.accessorCodeGenPolicy(method, classCodeGenPolicy));
		}
	}


	// ********** problems ***********

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		// @see #addDescriptorProblemsTo(List)
	}

	/**
	 * NB: until we have a visible class repository, these problems
	 * are added to the descriptor's problems
	 */
	void addDescriptorProblemsTo(List currentProblems) {
		this.checkGetMethod(currentProblems);
		this.checkSetMethod(currentProblems);
		this.checkValueGetMethod(currentProblems);
		this.checkValueSetMethod(currentProblems);
		this.checkAddMethod(currentProblems);
		this.checkRemoveMethod(currentProblems);
	}

	private void checkGetMethod(List currentProblems) {
		MWMethod getMethod = this.getGetMethod();
		if (getMethod == null) {
			return;
		}
		if (getMethod.methodParametersSize() == 0) {
			if ( ! getMethod.getReturnTypeDeclaration().mightBeAssignableFrom(this.typeDeclaration)) {
				currentProblems.add(this.buildProblem(ProblemConstants.GET_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.GET_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkSetMethod(List currentProblems) {
		MWMethod setMethod = this.getSetMethod();
		if (setMethod == null) {
			return;
		}
		if (setMethod.methodParametersSize() == 1) {
			if ( ! setMethod.getMethodParameter().mightBeAssignableTo(this.typeDeclaration)) {
				currentProblems.add(this.buildProblem(ProblemConstants.SET_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.SET_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkValueGetMethod(List currentProblems) {
		MWMethod valueGetMethod = this.getValueGetMethod();
		if (valueGetMethod == null) {
			return;
		}
		if (valueGetMethod.methodParametersSize() == 0) {
			if ( ! valueGetMethod.getReturnTypeDeclaration().mightBeAssignableFrom(this.getValueType())) {
				currentProblems.add(this.buildProblem(ProblemConstants.VALUE_GET_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.VALUE_GET_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkValueSetMethod(List currentProblems) {
		MWMethod valueSetMethod = this.getValueSetMethod();
		if (valueSetMethod == null) {
			return;
		}
		if (valueSetMethod.methodParametersSize() == 1) {
			if ( ! valueSetMethod.getMethodParameter().mightBeAssignableTo(this.getValueType())) {
				currentProblems.add(this.buildProblem(ProblemConstants.VALUE_SET_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.VALUE_SET_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkAddMethod(List currentProblems) {
		if (this.canHaveItemType()) {
			if (this.canHaveKeyType()) {
				this.checkAddMethodForMap(currentProblems);
			} else {
				this.checkAddMethodForCollection(currentProblems);
			}
		}
	}
	
	private void checkAddMethodForCollection(List currentProblems) {
		MWMethod addMethod = this.getAddMethod();
		if (addMethod == null) {
			return;
		}
		if (addMethod.methodParametersSize() == 1) {
			if ( ! addMethod.getMethodParameter().mightBeAssignableTo(this.getItemType())) {
				currentProblems.add(this.buildProblem(ProblemConstants.ADD_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.ADD_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkAddMethodForMap(List currentProblems) {
		MWMethod addMethod = this.getAddMethod();
		if (addMethod == null) {
			return;
		}
		if (addMethod.methodParametersSize() == 2) {
			if ( ! addMethod.getMethodParameter(0).mightBeAssignableTo(this.getKeyType())
					|| ! addMethod.getMethodParameter(1).mightBeAssignableTo(this.getItemType())) {
				currentProblems.add(this.buildProblem(ProblemConstants.MAP_ADD_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.MAP_ADD_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkRemoveMethod(List currentProblems) {
		if (this.canHaveItemType()) {
			if (this.canHaveKeyType()) {
				this.checkRemoveMethodForMap(currentProblems);
			} else {
				this.checkRemoveMethodForCollection(currentProblems);
			}
		}
	}
	
	private void checkRemoveMethodForCollection(List currentProblems) {
		MWMethod removeMethod = this.getRemoveMethod();
		if (removeMethod == null) {
			return;
		}
		if (removeMethod.methodParametersSize() == 1) {
			if ( ! removeMethod.getMethodParameter().mightBeAssignableTo(this.getItemType())) {
				currentProblems.add(this.buildProblem(ProblemConstants.REMOVE_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.REMOVE_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}
	
	private void checkRemoveMethodForMap(List currentProblems) {
		MWMethod removeMethod = this.getRemoveMethod();
		if (removeMethod == null) {
			return;
		}
		if (removeMethod.methodParametersSize() == 1) {
			if ( ! removeMethod.getMethodParameter().mightBeAssignableTo(this.getKeyType())) {
				currentProblems.add(this.buildProblem(ProblemConstants.MAP_REMOVE_METHOD_VS_ATTRIBUTE_MISMATCH, this.getName()));
			}
		} else {
			currentProblems.add(this.buildProblem(ProblemConstants.MAP_REMOVE_METHOD_PARMS_SIZE_INVALID, this.getName()));
		}
	}


	// ********** containment hierarchy **********

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.modifier);
		children.add(this.typeDeclaration);
		children.add(this.getMethodHandle);
		children.add(this.setMethodHandle);
		children.add(this.valueGetMethodHandle);
		children.add(this.valueSetMethodHandle);
		children.add(this.addMethodHandle);
		children.add(this.removeMethodHandle);
		children.add(this.itemTypeHandle);
		children.add(this.valueTypeHandle);
		children.add(this.keyTypeHandle);
	}

	private NodeReferenceScrubber buildGetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setGetMethod(null);
			}
			public String toString() {
				return "MWClassAttribute.buildGetMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildSetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setSetMethod(null);
			}
			public String toString() {
				return "MWClassAttribute.buildSetMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildValueGetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setValueGetMethod(null);
			}
			public String toString() {
				return "MWClassAttribute.buildValueGetMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildValueSetMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setValueSetMethod(null);
			}
			public String toString() {
				return "MWClassAttribute.buildValueSetMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildAddMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setAddMethod(null);
			}
			public String toString() {
				return "MWClassAttribute.buildAddMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildRemoveMethodScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setRemoveMethod(null);
			}
			public String toString() {
				return "MWClassAttribute.buildRemoveMethodScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildValueTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setValueType(null);
			}
			public String toString() {
				return "MWClassAttribute.buildValueTypeScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildItemTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setItemType(null);
			}
			public String toString() {
				return "MWClassAttribute.buildItemTypeScrubber()";
			}
		};
	}

	private NodeReferenceScrubber buildKeyTypeScrubber() {
		return new NodeReferenceScrubber() {
			public void nodeReferenceRemoved(Node node, MWHandle handle) {
				MWClassAttribute.this.setKeyType(null);
			}
			public String toString() {
				return "MWClassAttribute.buildKeyTypeScrubber()";
			}
		};
	}

	public void nodeRenamed(Node node) {
		super.nodeRenamed(node);
		if (this.getType() == node) {
			this.firePropertyChanged(DECLARATION_PROPERTY, "type");	// don't waste time building the declaration - it's ignored
		}
	}


	// ********** displaying and printing **********
		
	public String displayString() {
		return this.getName();
	}
	
	public void toString(StringBuffer sb) {
		sb.append(this.getName());
	}

	/**
	 * e.g. "foo : java.lang.Object"
	 */
	public String nameWithType() {
		return this.nameWithType(true);
	}
	
	/**
	 * e.g. "foo : Object"
	 */
	public String nameWithShortType() {
		return this.nameWithType(false);
	}
	
	private String nameWithType(boolean fullyQualified) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.name);
		sb.append(" : ");		// sorta like the UML convention
		if (fullyQualified) {
			this.typeDeclaration.printDeclarationOn(sb);
		} else {
			this.typeDeclaration.printShortDeclarationOn(sb);
		}
		return sb.toString();
	}
	

	// ********** TopLink methods **********
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWClassAttribute.class);
		
		descriptor.addDirectMapping("name", "name/text()");
		
		XMLDirectMapping modifierMapping = (XMLDirectMapping) descriptor.addDirectMapping("modifier", "getModifierForTopLink", "setModifierForTopLink", "modifier/text()");
		modifierMapping.setNullValue(new Integer(0));
		
		XMLCompositeObjectMapping typeDeclarationMapping = new XMLCompositeObjectMapping();
		typeDeclarationMapping.setAttributeName("typeDeclaration");
		typeDeclarationMapping.setReferenceClass(MWTypeDeclaration.class);
		typeDeclarationMapping.setXPath("type-declaration");
		descriptor.addMapping(typeDeclarationMapping);
		
		XMLCompositeObjectMapping valueTypeHandleMapping = new XMLCompositeObjectMapping();
		valueTypeHandleMapping.setAttributeName("valueTypeHandle");
		valueTypeHandleMapping.setGetMethodName("getValueTypeHandleForTopLink");
		valueTypeHandleMapping.setSetMethodName("setValueTypeHandleForTopLink");
		valueTypeHandleMapping.setReferenceClass(MWClassHandle.class);
		valueTypeHandleMapping.setXPath("value-type-handle");
		descriptor.addMapping(valueTypeHandleMapping);
		
		XMLCompositeObjectMapping itemTypeHandleMapping = new XMLCompositeObjectMapping();
		itemTypeHandleMapping.setAttributeName("itemTypeHandle");
		itemTypeHandleMapping.setGetMethodName("getItemTypeHandleForTopLink");
		itemTypeHandleMapping.setSetMethodName("setItemTypeHandleForTopLink");
		itemTypeHandleMapping.setReferenceClass(MWClassHandle.class);
		itemTypeHandleMapping.setXPath("item-type-handle");
		descriptor.addMapping(itemTypeHandleMapping);
		
		XMLCompositeObjectMapping keyTypeHandleMapping = new XMLCompositeObjectMapping();
		keyTypeHandleMapping.setAttributeName("keyTypeHandle");
		keyTypeHandleMapping.setGetMethodName("getKeyTypeHandleForTopLink");
		keyTypeHandleMapping.setSetMethodName("setKeyTypeHandleForTopLink");
		keyTypeHandleMapping.setReferenceClass(MWClassHandle.class);
		keyTypeHandleMapping.setXPath("key-type-handle");
		descriptor.addMapping(keyTypeHandleMapping);
		
		XMLCompositeObjectMapping getMethodHandleMapping = new XMLCompositeObjectMapping();
		getMethodHandleMapping.setAttributeName("getMethodHandle");
		getMethodHandleMapping.setGetMethodName("getGetMethodHandleForTopLink");
		getMethodHandleMapping.setSetMethodName("setGetMethodHandleForTopLink");
		getMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		getMethodHandleMapping.setXPath("get-method-handle");
		descriptor.addMapping(getMethodHandleMapping);
		
		XMLCompositeObjectMapping setMethodHandleMapping = new XMLCompositeObjectMapping();
		setMethodHandleMapping.setAttributeName("setMethodHandle");
		setMethodHandleMapping.setGetMethodName("getSetMethodHandleForTopLink");
		setMethodHandleMapping.setSetMethodName("setSetMethodHandleForTopLink");
		setMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		setMethodHandleMapping.setXPath("set-method-handle");
		descriptor.addMapping(setMethodHandleMapping);
		
		XMLCompositeObjectMapping valueGetMethodHandleMapping = new XMLCompositeObjectMapping();
		valueGetMethodHandleMapping.setAttributeName("valueGetMethodHandle");
		valueGetMethodHandleMapping.setGetMethodName("getValueGetMethodHandleForTopLink");
		valueGetMethodHandleMapping.setSetMethodName("setValueGetMethodHandleForTopLink");
		valueGetMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		valueGetMethodHandleMapping.setXPath("value-get-method-handle");
		descriptor.addMapping(valueGetMethodHandleMapping);
		
		XMLCompositeObjectMapping valueSetMethodHandleMapping = new XMLCompositeObjectMapping();
		valueSetMethodHandleMapping.setAttributeName("valueSetMethodHandle");
		valueSetMethodHandleMapping.setGetMethodName("getValueSetMethodHandleForTopLink");
		valueSetMethodHandleMapping.setSetMethodName("setValueSetMethodHandleForTopLink");
		valueSetMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		valueSetMethodHandleMapping.setXPath("value-set-method-handle");
		descriptor.addMapping(valueSetMethodHandleMapping);
		
		XMLCompositeObjectMapping addMethodHandleMapping = new XMLCompositeObjectMapping();
		addMethodHandleMapping.setAttributeName("addMethodHandle");
		addMethodHandleMapping.setGetMethodName("getAddMethodHandleForTopLink");
		addMethodHandleMapping.setSetMethodName("setAddMethodHandleForTopLink");
		addMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		addMethodHandleMapping.setXPath("add-method-handle");
		descriptor.addMapping(addMethodHandleMapping);
		
		XMLCompositeObjectMapping removeMethodHandleMapping = new XMLCompositeObjectMapping();
		removeMethodHandleMapping.setAttributeName("removeMethodHandle");
		removeMethodHandleMapping.setGetMethodName("getRemoveMethodHandleForTopLink");
		removeMethodHandleMapping.setSetMethodName("setRemoveMethodHandleForTopLink");
		removeMethodHandleMapping.setReferenceClass(MWMethodHandle.class);
		removeMethodHandleMapping.setXPath("remove-method-handle");
		descriptor.addMapping(removeMethodHandleMapping);
		
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
	
	private MWClassHandle getValueTypeHandleForTopLink() {
		return (this.valueTypeHandle.getType() == null) ? null : this.valueTypeHandle;
	}
	private void setValueTypeHandleForTopLink(MWClassHandle valueTypeHandle) {
		NodeReferenceScrubber scrubber = this.buildValueTypeScrubber();
		this.valueTypeHandle = ((valueTypeHandle == null) ? new MWClassHandle(this, scrubber) : valueTypeHandle.setScrubber(scrubber));
	}
	
	private MWClassHandle getItemTypeHandleForTopLink() {
		return (this.itemTypeHandle.getType() == null) ? null : this.itemTypeHandle;
	}
	private void setItemTypeHandleForTopLink(MWClassHandle itemTypeHandle) {
		NodeReferenceScrubber scrubber = this.buildItemTypeScrubber();
		this.itemTypeHandle = ((itemTypeHandle == null) ? new MWClassHandle(this, scrubber) : itemTypeHandle.setScrubber(scrubber));
	}
	
	private MWClassHandle getKeyTypeHandleForTopLink() {
		return (this.keyTypeHandle.getType() == null) ? null : this.keyTypeHandle;
	}
	private void setKeyTypeHandleForTopLink(MWClassHandle keyTypeHandle) {
		NodeReferenceScrubber scrubber = this.buildKeyTypeScrubber();
		this.keyTypeHandle = ((keyTypeHandle == null) ? new MWClassHandle(this, scrubber) : keyTypeHandle.setScrubber(scrubber));
	}
	
	private MWMethodHandle getGetMethodHandleForTopLink() {
		return (this.getMethodHandle.getMethod() == null) ? null : this.getMethodHandle;
	}
	private void setGetMethodHandleForTopLink(MWMethodHandle getMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildGetMethodScrubber();
		this.getMethodHandle = ((getMethodHandle == null) ? new MWMethodHandle(this, scrubber) : getMethodHandle.setScrubber(scrubber));
	}
	
	private MWMethodHandle getSetMethodHandleForTopLink() {
		return (this.setMethodHandle.getMethod() == null) ? null : setMethodHandle;
	}
	private void setSetMethodHandleForTopLink(MWMethodHandle setMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildSetMethodScrubber();
		this.setMethodHandle = ((setMethodHandle == null) ? new MWMethodHandle(this, scrubber) : setMethodHandle.setScrubber(scrubber));
	}
	
	private MWMethodHandle getValueGetMethodHandleForTopLink() {
		return (this.valueGetMethodHandle.getMethod() == null) ? null : valueGetMethodHandle;
	}
	private void setValueGetMethodHandleForTopLink(MWMethodHandle valueGetMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildValueGetMethodScrubber();
		this.valueGetMethodHandle = ((valueGetMethodHandle == null) ? new MWMethodHandle(this, scrubber) : valueGetMethodHandle.setScrubber(scrubber));
	}
	
	private MWMethodHandle getValueSetMethodHandleForTopLink() {
		return (this.valueSetMethodHandle.getMethod() == null) ? null : valueSetMethodHandle;
	}
	private void setValueSetMethodHandleForTopLink(MWMethodHandle valueSetMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildValueSetMethodScrubber();
		this.valueSetMethodHandle = ((valueSetMethodHandle == null) ? new MWMethodHandle(this, scrubber) : valueSetMethodHandle.setScrubber(scrubber));
	}
	
	private MWMethodHandle getAddMethodHandleForTopLink() {
		return (this.addMethodHandle.getMethod() == null) ? null : this.addMethodHandle;
	}
	private void setAddMethodHandleForTopLink(MWMethodHandle addMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildAddMethodScrubber();
		this.addMethodHandle = ((addMethodHandle == null) ? new MWMethodHandle(this, scrubber) : addMethodHandle.setScrubber(scrubber));
	}
	
	private MWMethodHandle getRemoveMethodHandleForTopLink() {
		return (this.removeMethodHandle.getMethod() == null) ? null : this.removeMethodHandle;
	}
	private void setRemoveMethodHandleForTopLink(MWMethodHandle removeMethodHandle) {
		NodeReferenceScrubber scrubber = this.buildRemoveMethodScrubber();
		this.removeMethodHandle = ((removeMethodHandle == null) ? new MWMethodHandle(this, scrubber) : removeMethodHandle.setScrubber(scrubber));
	}
}
