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
package org.eclipse.persistence.tools.workbench.utility.diff;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * Given a pair of objects to compare, use Java reflection to compare the
 * objects' field values. Allow for field-specific differentiators. A field can
 * be specified a "reference" field, meaning that only the "keys" of the fields'
 * values will be compared (via Differentiator#keyDiff(Object, Object));
 * as opposed to a "composite" field, where all the values of the field's fields
 * are compared normally. By default, all fields are considered "composite"
 * fields.
 * 
 * By default the "key" differentiator is another "reflective" differentiator
 * that compares the values of the "key" fields. This "key" differentiator
 * can be replaced if the objects' keys are not directly represented by
 * one or more fields.
 * 
 * Unless overridden, "container" fields get type-specific differentiators.
 * Any fields whose declared types are compatible with List, Collection,
 * Map get the appropriate differentiators. The same applies to any
 * fields whose declared type is an array.
 * 
 * @see DiffEngine
 * TODO add support for multi-dimensional arrays and collections
 */
public class ReflectiveDifferentiator implements Differentiator {

	/** the class of objects the differentiator will compare */
	private final Class javaClass;

	/**
	 * the default field differentiator, used to compare the
	 * fields with no field-specific differentiator specified;
	 * typically this is either an EqualityDifferentiator or a
	 * DiffEngine.RecordingDifferentiator
	 */
	private final Differentiator defaultFieldDifferentiator;

	/** the field differentiators, keyed by field */
	private final Map fieldDifferentiators;

	/**
	 * the key differentiator; by default this compares "key" fields
	 */
	private KeyDifferentiator keyDifferentiator;

	/**
	 * by default this is false; but some "value" objects implement #equals(Object)
	 * "incorrectly", so we must use a reflective differentiator to compare them and
	 * set this flag to true
	 */
	private boolean comparesValueObjects;

	/**
	 * specialized differentiators for "container" fields;
	 * these are lazy-initialized so they are only instantiated when
	 * actually needed
	 */
	private OrderedContainerDifferentiator listDifferentiator;
	private OrderedContainerDifferentiator arrayDifferentiator;
	private ContainerDifferentiator collectionDifferentiator;
	private ContainerDifferentiator unorderedArrayDifferentiator;
	private ContainerDifferentiator mapDifferentiator;


	// ********** constructors/initialization **********

	public ReflectiveDifferentiator(Class javaClass) {
		this(javaClass, EqualityDifferentiator.instance());
	}

	public ReflectiveDifferentiator(Class javaClass, Differentiator defaultFieldDifferentiator) {
		super();
		if (javaClass.isInterface()) {
			throw new IllegalArgumentException("interfaces cannot be compared reflectively: " + javaClass.getName());
		}
		this.javaClass = javaClass;
		if (defaultFieldDifferentiator == null) {
			throw new NullPointerException();
		}
		this.defaultFieldDifferentiator = defaultFieldDifferentiator;
		// TODO add 'compareStaticFields' setting to constructor parms?
		this.fieldDifferentiators = this.buildDefaultFieldDifferentiators(false);
		this.keyDifferentiator = new DefaultKeyDifferentiator();
		this.comparesValueObjects = false;
	}

	private Map buildDefaultFieldDifferentiators(boolean compareStaticFields) {
		Map differentiators = new HashMap();
		Field[] declaredFields = this.javaClass.getDeclaredFields();
		for (int i = declaredFields.length; i-- > 0; ) {
			Field field = declaredFields[i];
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers()) && ! compareStaticFields) {
				continue;	// skip static fields unless the flag is true
			}
			differentiators.put(field, this.defaultFieldDifferentiator(field));
		}
		return differentiators;
	}

	/**
	 * return the default differentiator for the specified field
	 */
	private Differentiator defaultFieldDifferentiator(Field field) {
		Class fieldType = field.getType();
		if (List.class.isAssignableFrom(fieldType)) {
			return this.getListDifferentiator();
		}
		if (Collection.class.isAssignableFrom(fieldType)) {
			return this.getCollectionDifferentiator();
		}
		if (Map.class.isAssignableFrom(fieldType)) {
			return this.getMapDifferentiator();
		}
		if (fieldType.isArray()) {
			return this.getArrayDifferentiator();
		}
		return this.defaultFieldDifferentiator;
	}

	/**
	 * return the default differentiator for the specified field
	 */
	private Differentiator defaultFieldDifferentiator(String fieldName) {
		return this.defaultFieldDifferentiator(this.field(fieldName));
	}


	// ********** Differentiator implementation **********

	/**
	 * @see Differentiator#diff(Object, Object)
	 */
	public Diff diff(Object object1, Object object2) {
		return this.diff(object1, object2, this.fieldDifferentiators, DifferentiatorAdapter.NORMAL);
	}

	/**
	 * @see Differentiator#keyDiff(Object, Object)
	 */
	public Diff keyDiff(Object object1, Object object2) {
		return this.keyDifferentiator.keyDiff(object1, object2);
	}

	Diff diff(Object object1, Object object2, Map differentiators, DifferentiatorAdapter adapter) {
		if (object1 == object2) {
			return new NullDiff(object1, object2, this);
		}
		if (this.diffIsFatal(object1, object2)) {
			return new SimpleDiff(object1, object2, this.fatalDescriptionTitle(), this);
		}

		ReflectiveFieldDiff[] diffs = new ReflectiveFieldDiff[differentiators.size()];
		int i = 0;
		for (Iterator stream = differentiators.entrySet().iterator(); stream.hasNext(); ) {
			Map.Entry entry = (Map.Entry) stream.next();
			Field field = (Field) entry.getKey();
			Differentiator fieldDifferentiator = (Differentiator) entry.getValue();
			Object fieldValue1 = this.fieldValue(field, object1);
			Object fieldValue2 = this.fieldValue(field, object2);
			Diff fieldDiff = adapter.diff(fieldDifferentiator, fieldValue1, fieldValue2);
			diffs[i++] = new ReflectiveFieldDiff(field, fieldDiff, this);
		}
		// put the field diffs in alphabetical order
		return new ReflectiveDiff(this.javaClass, object1, object2, (ReflectiveFieldDiff[]) CollectionTools.sort(diffs), this);
	}

	/**
	 * @see Differentiator#comparesValueObjects()
	 */
	public boolean comparesValueObjects() {
		return this.comparesValueObjects;
	}


	// ********** default container field differentiators **********

	private OrderedContainerDifferentiator getListDifferentiator() {
		if (this.listDifferentiator == null) {
			this.listDifferentiator = OrderedContainerDifferentiator.forLists(this.defaultFieldDifferentiator);
		}
		return this.listDifferentiator;
	}

	private OrderedContainerDifferentiator getArrayDifferentiator() {
		if (this.arrayDifferentiator == null) {
			this.arrayDifferentiator = OrderedContainerDifferentiator.forArrays(this.defaultFieldDifferentiator);
		}
		return this.arrayDifferentiator;
	}

	private ContainerDifferentiator getCollectionDifferentiator() {
		if (this.collectionDifferentiator == null) {
			this.collectionDifferentiator = ContainerDifferentiator.forCollections(this.defaultFieldDifferentiator);
		}
		return this.collectionDifferentiator;
	}

	private ContainerDifferentiator getUnorderedArrayDifferentiator() {
		if (this.unorderedArrayDifferentiator == null) {
			this.unorderedArrayDifferentiator = ContainerDifferentiator.forArrays(this.defaultFieldDifferentiator);
		}
		return this.unorderedArrayDifferentiator;
	}

	private ContainerDifferentiator getMapDifferentiator() {
		if (this.mapDifferentiator == null) {
			this.mapDifferentiator = ContainerDifferentiator.forMaps(this.defaultFieldDifferentiator, this.defaultFieldDifferentiator);
		}
		return this.mapDifferentiator;
	}


	// ********** queries **********

	public Class getJavaClass() {
		return this.javaClass;
	}

	public Differentiator getDefaultFieldDifferentiator() {
		return this.defaultFieldDifferentiator;
	}

	public KeyDifferentiator getKeyDifferentiator() {
		return this.keyDifferentiator;
	}

	private boolean diffIsFatal(Object object1, Object object2) {
		if (object1 == null) {
			return true;
		}
		if (object2 == null) {
			return true;
		}
		return (object1.getClass() != object2.getClass());
	}

	protected String fatalDescriptionTitle() {
		return "Objects cannot be compared via reflection (" + this.javaClass.getName() + ")";
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return StringTools.buildToStringFor(this, ClassTools.shortNameFor(this.javaClass));
	}
	

	// ********** behavior **********

	/**
	 * allow clients to replace the default key differentiator
	 */
	public void setKeyDifferentiator(KeyDifferentiator keyDifferentiator) {
		this.keyDifferentiator = keyDifferentiator;
	}

	/**
	 * allow clients to configure the differentiator for comparing "value" objects
	 */
	public void setComparesValueObjects(boolean comparesValueObjects) {
		this.comparesValueObjects = comparesValueObjects;
	}

	public Differentiator setFieldDifferentiator(String fieldName, Differentiator differentiator) {
		return this.setFieldDifferentiator(this.field(fieldName), differentiator);
	}

	private Differentiator setFieldDifferentiator(Field field, Differentiator differentiator) {
		if (differentiator == null) {
			throw new NullPointerException();
		}
		Object prev = this.fieldDifferentiators.put(field, differentiator);
		if (prev != this.defaultFieldDifferentiator(field)) {
			throw new IllegalArgumentException("duplicate field differentiator: " + field.getName());
		}
		return differentiator;
	}

	public Differentiator replaceFieldDifferentiator(String fieldName, Differentiator differentiator) {
		return this.replaceFieldDifferentiator(this.field(fieldName), differentiator);
	}

	private Differentiator replaceFieldDifferentiator(Field field, Differentiator differentiator) {
		if (differentiator == null) {
			throw new NullPointerException();
		}
		Object prev = this.fieldDifferentiators.put(field, differentiator);
		if (prev == this.defaultFieldDifferentiator(field)) {
			throw new IllegalArgumentException("field differentiator not present: " + field.getName());
		}
		return differentiator;
	}

	public Differentiator getFieldDifferentiator(String fieldName) {
		return (Differentiator) this.fieldDifferentiators.get(this.field(fieldName));
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public Differentiator setKeyFieldDifferentiator(String fieldName, Differentiator differentiator) {
		return this.setKeyFieldDifferentiator(this.field(fieldName), differentiator);
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	private Differentiator setKeyFieldDifferentiator(Field field, Differentiator differentiator) {
		if (this.keyDifferentiator instanceof DefaultKeyDifferentiator) {
			return ((DefaultKeyDifferentiator) this.keyDifferentiator).setKeyFieldDifferentiator(field, differentiator);
		}
		throw new IllegalStateException("the default key differentiator is not being used");
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public Differentiator replaceKeyFieldDifferentiator(String fieldName, Differentiator differentiator) {
		return this.replaceKeyFieldDifferentiator(this.field(fieldName), differentiator);
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	private Differentiator replaceKeyFieldDifferentiator(Field field, Differentiator differentiator) {
		if (this.keyDifferentiator instanceof DefaultKeyDifferentiator) {
			return ((DefaultKeyDifferentiator) this.keyDifferentiator).replaceKeyFieldDifferentiator(field, differentiator);
		}
		throw new IllegalStateException("the default key differentiator is not being used");
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public Differentiator getKeyFieldDifferentiator(String fieldName) {
		if (this.keyDifferentiator instanceof DefaultKeyDifferentiator) {
			return ((DefaultKeyDifferentiator) this.keyDifferentiator).getKeyFieldDifferentiator(this.field(fieldName));
		}
		throw new IllegalStateException("the default key differentiator is not being used");
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public Differentiator removeKeyFieldDifferentiator(String fieldName) {
		if (this.keyDifferentiator instanceof DefaultKeyDifferentiator) {
			return ((DefaultKeyDifferentiator) this.keyDifferentiator).removeKeyFieldDifferentiator(this.field(fieldName));
		}
		throw new IllegalStateException("the default key differentiator is not being used");
	}

	/**
	 * "checked" exceptions suck
	 */
	private Object fieldValue(Field field, Object object) {
		try {
			return field.get(object);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * "checked" exceptions suck
	 */
	private Field field(String fieldName) {
		Field field = null;
		try {
			field = this.javaClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			throw new IllegalArgumentException(fieldName);
		}
		field.setAccessible(true);
		return field;
	}


	// ********** ignored fields **********

	public NullDifferentiator ignoreFieldNamed(String fieldName) {
		return (NullDifferentiator) this.setFieldDifferentiator(fieldName, NullDifferentiator.instance());
	}

	public NullDifferentiator ignoreFieldsNamed(String fieldName) {
		return this.ignoreFieldNamed(fieldName);
	}

	public void ignoreFieldsNamed(String fieldName1, String fieldName2) {
		this.ignoreFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void ignoreFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.ignoreFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void ignoreFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.ignoreFieldNamed(fieldNames[i]);
		}
	}


	// ********** key fields **********

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public Differentiator addKeyFieldNamed(String fieldName) {
		return this.setKeyFieldDifferentiator(fieldName, this.defaultFieldDifferentiator(fieldName));
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public Differentiator addKeyFieldsNamed(String fieldName) {
		return this.addKeyFieldNamed(fieldName);
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public void addKeyFieldsNamed(String fieldName1, String fieldName2) {
		this.addKeyFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public void addKeyFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addKeyFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	/**
	 * convenience method, when using the default key differentiator;
	 * throw an IllegalStateException if the default key differentiator is not being used
	 */
	public void addKeyFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addKeyFieldNamed(fieldNames[i]);
		}
	}


	// ********** reference (as opposed to composite) fields **********

	private ReferenceDifferentiator addReferenceFieldNamed(String fieldName, Differentiator differentiator) {
		return (ReferenceDifferentiator) this.setFieldDifferentiator(fieldName, new ReferenceDifferentiator(differentiator));
	}

	public Differentiator addReferenceFieldNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName, this.defaultFieldDifferentiator(fieldName));
	}

	public Differentiator addReferenceFieldsNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName);
	}

	public void addReferenceFieldsNamed(String fieldName1, String fieldName2) {
		this.addReferenceFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addReferenceFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addReferenceFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addReferenceFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addReferenceFieldNamed(fieldNames[i]);
		}
	}


	// ********** (composite) list fields **********

	public OrderedContainerDifferentiator addListFieldNamed(String fieldName) {
		return (OrderedContainerDifferentiator) this.setFieldDifferentiator(fieldName, this.getListDifferentiator());
	}

	public OrderedContainerDifferentiator addListFieldsNamed(String fieldName) {
		return this.addListFieldNamed(fieldName);
	}

	public void addListFieldsNamed(String fieldName1, String fieldName2) {
		this.addListFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addListFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addListFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addListFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addListFieldNamed(fieldNames[i]);
		}
	}


	// ********** reference (as opposed to composite) list fields **********

	public ReferenceDifferentiator addReferenceListFieldNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName, this.getListDifferentiator());
	}

	public ReferenceDifferentiator addReferenceListFieldsNamed(String fieldName) {
		return this.addReferenceListFieldNamed(fieldName);
	}

	public void addReferenceListFieldsNamed(String fieldName1, String fieldName2) {
		this.addReferenceListFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addReferenceListFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addReferenceListFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addReferenceListFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addReferenceListFieldNamed(fieldNames[i]);
		}
	}


	// ********** (composite) array fields **********

	public OrderedContainerDifferentiator addArrayFieldNamed(String fieldName) {
		return (OrderedContainerDifferentiator) this.setFieldDifferentiator(fieldName, this.getArrayDifferentiator());
	}

	public OrderedContainerDifferentiator addArrayFieldsNamed(String fieldName) {
		return this.addArrayFieldNamed(fieldName);
	}

	public void addArrayFieldsNamed(String fieldName1, String fieldName2) {
		this.addArrayFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addArrayFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addArrayFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addArrayFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addArrayFieldNamed(fieldNames[i]);
		}
	}


	// ********** reference (as opposed to composite) array fields **********

	public ReferenceDifferentiator addReferenceArrayFieldNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName, this.getArrayDifferentiator());
	}

	public ReferenceDifferentiator addReferenceArrayFieldsNamed(String fieldName) {
		return this.addReferenceArrayFieldNamed(fieldName);
	}

	public void addReferenceArrayFieldsNamed(String fieldName1, String fieldName2) {
		this.addReferenceArrayFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addReferenceArrayFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addReferenceArrayFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addReferenceArrayFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addReferenceArrayFieldNamed(fieldNames[i]);
		}
	}


	// ********** (composite) collection fields **********

	public ContainerDifferentiator addCollectionFieldNamed(String fieldName) {
		return (ContainerDifferentiator) this.setFieldDifferentiator(fieldName, this.getCollectionDifferentiator());
	}

	public ContainerDifferentiator addCollectionFieldsNamed(String fieldName) {
		return this.addCollectionFieldNamed(fieldName);
	}

	public void addCollectionFieldsNamed(String fieldName1, String fieldName2) {
		this.addCollectionFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addCollectionFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addCollectionFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addCollectionFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addCollectionFieldNamed(fieldNames[i]);
		}
	}


	// ********** reference (as opposed to composite) collection fields **********

	public ReferenceDifferentiator addReferenceCollectionFieldNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName, this.getCollectionDifferentiator());
	}

	public ReferenceDifferentiator addReferenceCollectionFieldsNamed(String fieldName) {
		return this.addReferenceCollectionFieldNamed(fieldName);
	}

	public void addReferenceCollectionFieldsNamed(String fieldName1, String fieldName2) {
		this.addReferenceCollectionFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addReferenceCollectionFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addReferenceCollectionFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addReferenceCollectionFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addReferenceCollectionFieldNamed(fieldNames[i]);
		}
	}


	// ********** (composite) unordered array fields **********

	public ContainerDifferentiator addUnorderedArrayFieldNamed(String fieldName) {
		return (ContainerDifferentiator) this.setFieldDifferentiator(fieldName, this.getUnorderedArrayDifferentiator());
	}

	public ContainerDifferentiator addUnorderedArrayFieldsNamed(String fieldName) {
		return this.addUnorderedArrayFieldNamed(fieldName);
	}

	public void addUnorderedArrayFieldsNamed(String fieldName1, String fieldName2) {
		this.addUnorderedArrayFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addUnorderedArrayFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addUnorderedArrayFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addUnorderedArrayFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addUnorderedArrayFieldNamed(fieldNames[i]);
		}
	}


	// ********** reference (as opposed to composite) unordered array fields **********

	public ReferenceDifferentiator addReferenceUnorderedArrayFieldNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName, this.getUnorderedArrayDifferentiator());
	}

	public ReferenceDifferentiator addReferenceUnorderedArrayFieldsNamed(String fieldName) {
		return this.addReferenceUnorderedArrayFieldNamed(fieldName);
	}

	public void addReferenceUnorderedArrayFieldsNamed(String fieldName1, String fieldName2) {
		this.addReferenceUnorderedArrayFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addReferenceUnorderedArrayFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addReferenceUnorderedArrayFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addReferenceUnorderedArrayFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addReferenceUnorderedArrayFieldNamed(fieldNames[i]);
		}
	}


	// ********** (composite) map fields **********

	public ContainerDifferentiator addMapFieldNamed(String fieldName) {
		return (ContainerDifferentiator) this.setFieldDifferentiator(fieldName, this.getMapDifferentiator());
	}

	public ContainerDifferentiator addMapFieldsNamed(String fieldName) {
		return this.addMapFieldNamed(fieldName);
	}

	public void addMapFieldsNamed(String fieldName1, String fieldName2) {
		this.addMapFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addMapFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addMapFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addMapFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addMapFieldNamed(fieldNames[i]);
		}
	}


	// ********** reference (as opposed to composite) map fields **********

	public ReferenceDifferentiator addReferenceMapFieldNamed(String fieldName) {
		return this.addReferenceFieldNamed(fieldName, this.getMapDifferentiator());
	}

	public ReferenceDifferentiator addReferenceMapFieldsNamed(String fieldName) {
		return this.addReferenceMapFieldNamed(fieldName);
	}

	public void addReferenceMapFieldsNamed(String fieldName1, String fieldName2) {
		this.addReferenceMapFieldsNamed(new String[] {fieldName1, fieldName2});
	}

	public void addReferenceMapFieldsNamed(String fieldName1, String fieldName2, String fieldName3) {
		this.addReferenceMapFieldsNamed(new String[] {fieldName1, fieldName2, fieldName3});
	}

	public void addReferenceMapFieldsNamed(String[] fieldNames) {
		for (int i = fieldNames.length; i-- > 0; ) {
			this.addReferenceMapFieldNamed(fieldNames[i]);
		}
	}


	// ********** pluggable interface for the "key" differentiator **********

	/**
	 * This defines the interface required of a "key" differentiator. The "key"
	 * differentiator is used by the reflective differentiator when it is
	 * executing a "key" diff.
	 * See the default implementation below.
	 */
	public interface KeyDifferentiator {
		Diff keyDiff(Object object1, Object object2);
	}


	/**
	 * The default "key" differentiator uses a set of user-specified
	 * "key field" differentiators to compare a subset of the objects'
	 * fields. Typically it will use the parent reflective differentiator's
	 * default field differentiator.
	 * 
	 * @see ReflectiveDifferentiator#addKeyFieldNamed(String) and
	 * related methods
	 */
	private class DefaultKeyDifferentiator implements KeyDifferentiator {
		/** the key field differentiators, keyed by field; if this is empty, there is no "primary key" */
		private Map keyFieldDifferentiators;

		DefaultKeyDifferentiator() {
			super();
			this.keyFieldDifferentiators = new HashMap();
		}

		public Diff keyDiff(Object object1, Object object2) {
			// borrow some of ReflectiveDifferentiator's code
			return ReflectiveDifferentiator.this.diff(object1, object2, this.keyFieldDifferentiators, DifferentiatorAdapter.KEY);
		}

		Differentiator setKeyFieldDifferentiator(Field field, Differentiator differentiator) {
			Object prev = this.replaceKeyFieldDifferentiator(field, differentiator);
			if (prev != null) {
				throw new IllegalArgumentException("duplicate key field differentiator: " + field.getName());
			}
			return differentiator;
		}

		Differentiator replaceKeyFieldDifferentiator(Field field, Differentiator differentiator) {
			if (differentiator == null) {
				throw new NullPointerException();
			}
			return (Differentiator) this.keyFieldDifferentiators.put(field, differentiator);
		}

		Differentiator getKeyFieldDifferentiator(Field field) {
			return (Differentiator) this.keyFieldDifferentiators.get(field);
		}

		Differentiator removeKeyFieldDifferentiator(Field field) {
			return (Differentiator) this.keyFieldDifferentiators.remove(field);
		}

	}


	/**
	 * This key differentiator will reflectively invoke the specified method
	 * on both of the objects and diff the return values with an equality
	 * differentiator. Subclasses can override #diffKeys(Object, Object)
	 * to use a different differentiator.
	 */
	public static class SimpleMethodKeyDifferentiator implements KeyDifferentiator {
		protected String methodName;
		protected static final Object NULL_KEY = new Object();

		public SimpleMethodKeyDifferentiator(String methodName) {
			super();
			this.methodName = methodName;
		}

		public Diff keyDiff(Object object1, Object object2) {
			return this.diffKeys(this.key(object1), this.key(object2));
		}

		protected Diff diffKeys(Object key1, Object key2) {
			return EqualityDifferentiator.instance().diff(key1, key2);
		}

		/**
		 * If the object is null, we return a unique key that can be used
		 * to match up null objects.
		 */
		protected Object key(Object object) {
			return (object == null) ? NULL_KEY : ClassTools.invokeMethod(object, this.methodName);
		}

		protected Object nullKey() {
			return NULL_KEY;
		}
	
		public String toString() {
			return StringTools.buildToStringFor(this, this.methodName);
		}

	}

}
