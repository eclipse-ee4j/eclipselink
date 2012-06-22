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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import java.text.Collator;
import java.util.*;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public final class MWObjectTypeConverter 
	extends MWTypeConverter
{
	// **************** Fields ************************************************
		
	/** A list of ValuePair objects, mapping data values to attribute values */
	private Collection valuePairs;
		public final static String VALUE_PAIRS_COLLECTION = "valuePairs";
	
	/** The value in the map that is used by default */
	private volatile ValuePair defaultValuePair;
		public final static String DEFAULT_ATTRIBUTE_VALUE_PROPERTY = "defaultAttributeProperty";
	
	
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only */
	private MWObjectTypeConverter() {
		super();
	}

	public MWObjectTypeConverter(MWConverterMapping parent) {
		super(parent);
	}
	
	//only used for legacy projects
	public MWObjectTypeConverter(MWConverterMapping parent, Map legacyValueMap) {
		super(parent, legacyValueMap);
	}
	
	
	// **************** Initialization ****************************************

	protected void initialize(Node parent) {
		super.initialize(parent);
		this.valuePairs = new Vector();
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		
		synchronized (this.valuePairs) { children.addAll(this.valuePairs); }
	}
	

	// **************** Value Pairs API ***************************************
	
	/** For internal or UI use only */
	public Iterator valuePairs() {
		return new CloneIterator(this.valuePairs);
	}
	
	/** For internal or UI use only */
	public int valuePairsSize() {
		return this.valuePairs.size();
	}
	
	public void addValuePair(String dataValueString, String attributeValueString) 
		throws ConversionValueException, ConversionException
	{
		Object attributeValue = this.buildAttributeValue(attributeValueString);
		Object dataValue = this.buildDataValue(dataValueString);
		this.addValuePair(dataValue, attributeValue);
	}
	
	public ValuePair addValuePair(Object dataValue, Object attributeValue)
		throws ConversionValueException 
	{
		if (this.duplicateDataValue(dataValue)) {
			throw ConversionValueException.duplicateDataValueException(dataValue);
		}
		else if (this.duplicateAttributeValue(attributeValue)) {
			throw ConversionValueException.duplicateAttributeValueException(attributeValue);
		}
		
		ValuePair valuePair = new ValuePair(this, dataValue, attributeValue);
		this.addValuePairInternal(valuePair);
		return valuePair;
	}
	
	public void editValuePair(ValuePair valuePair, String dataValueString, String attributeValueString)
		throws ConversionValueException, ConversionException
	{
		Object dataValue = this.buildDataValue(dataValueString);
		Object attributeValue = this.buildAttributeValue(attributeValueString);
		
		if (! valuePair.dataValue.equals(dataValue) && this.duplicateDataValue(dataValue)) {
			throw ConversionValueException.duplicateDataValueException(dataValue);
		}
		else if (! valuePair.attributeValue.equals(attributeValue) && this.duplicateAttributeValue(attributeValue)) {
			throw ConversionValueException.duplicateAttributeValueException(attributeValue);
		}
		
		valuePair.setDataValue(dataValue);
		valuePair.setAttributeValue(attributeValue);
	}
	
	public void removeValuePair(ValuePair valuePair) {
		this.valuePairs.remove(valuePair);
		this.fireItemRemoved(VALUE_PAIRS_COLLECTION, valuePair);
		
		if (this.defaultValuePair == valuePair)
			this.setDefaultValuePair(null);
	}
	
	public void clearValuePairs() {
		this.valuePairs.clear();
		this.fireCollectionChanged(VALUE_PAIRS_COLLECTION);
		setDefaultValuePair(null);
	}
	
	
	// **************** Default Attribute Value API ***************************
	
	public Object getDefaultAttributeValue() {
		return this.defaultValuePair == null ? null : this.defaultValuePair.attributeValue;
	}
	
	/**
	 * NOTE:  The object specified here *must* be present in the value pairs list, 
	 * 		   otherwise the default attribute value is set to null.
	 *        
	 * (This should only be a consideration if calling this method while building 
	 *  a test project.  If called from the UI, the object will *always* be present 
	 *  in the value pairs list.)
	 */
	public void setDefaultAttributeValue(Object defaultAttributeValue) {
		this.setDefaultValuePair(this.valuePairForAttributeValue(defaultAttributeValue));
	}
	
	public void setDefaultAttributeValue(String newDefaultAttributeValueString) 
		throws ConversionException
	{
			this.setDefaultAttributeValue(this.buildAttributeValue(newDefaultAttributeValueString));
	}
	
	
	// ************** MWConverter implementation ************
	
	/** Should ONLY be used in one place - the UI */
	public String accessibleNameKey() {
		return "ACCESSIBLE_SERIALIZED_MAPPING_NODE";
	}

	public String getType() {
		return OBJECT_TYPE_CONVERTER;
	}

	public String iconKey() {
		return "mapping.objectType";
	}


	// ************** Value Pairs Internal Behavior *************
	
	private Iterator dataValues() {
		return new TransformationIterator(this.valuePairs()) {
			protected Object transform(Object next) {
				return ((ValuePair) next).dataValue;
			}
		};
	}
	
	private Iterator attributeValues() {
		return new TransformationIterator(this.valuePairs()) {
			protected Object transform(Object next) {
				return ((ValuePair) next).attributeValue;
			}
		};
	}
	
	private void addValuePairInternal(ValuePair valuePair) {
		this.valuePairs.add(valuePair);
		this.fireItemAdded(VALUE_PAIRS_COLLECTION, valuePair);
	}
	
	/**
	 * This will always clear out the value pairs and rebuild them.
	 */
	protected void rebuildValuePairs() {
		Iterator valuePairsCopy = valuePairs();
		this.clearValuePairs();
		
		if (getDataType() == null || getAttributeType() == null) {
			return;
		}
		
		for (Iterator stream = valuePairsCopy; stream.hasNext(); ) {
			ValuePair nextPair = (ValuePair) stream.next();
			ValuePair valuePair = null;
			
			try {
				valuePair = this.addValuePair(this.buildDataValue(nextPair.dataValue), this.buildAttributeValue(nextPair.attributeValue));
			}
			catch (ConversionException ce) {
				// do nothing - just don't add the value pair
			}
			catch (ConversionValueException cve) {
				// do nothing - just don't add the value pair
			}
			
			if (this.defaultValuePair == nextPair) {
				this.setDefaultValuePair(valuePair);
			}
		}
	}
	
	private boolean duplicateDataValue(Object dataValue) {
		for (Iterator stream = this.dataValues(); stream.hasNext(); ) {
			if ((stream.next()).equals(dataValue)) {
				return true;
			}	
		}
		
		return false;
	}
	
	private boolean duplicateAttributeValue(Object attributeValue) {
		for (Iterator stream = this.attributeValues(); stream.hasNext(); ) {
			if (stream.next().equals(attributeValue)) {
				return true;
			}	
		}
		
		return false;
	}
	
	/**
	 * Return the value pair for the specified attribute value.
	 */
	private ValuePair valuePairForAttributeValue(Object attributeValue) {
		for (Iterator stream = this.valuePairs(); stream.hasNext(); ) {
			ValuePair valuePair = (ValuePair) stream.next();
			if (valuePair.attributeValue.equals(attributeValue)) {
				return valuePair;
			}
		}
		if (attributeValue == null) {
			return null;
		}
		throw new IllegalArgumentException(String.valueOf(attributeValue));
	}
	
	
	// **************** Default Attribute Value Internal Behavior *************
	
	private void setDefaultValuePair(ValuePair newDefaultValuePair) {
		Object oldDefaultAttributeValue = 
			this.defaultValuePair == null ? null : this.defaultValuePair.attributeValue;
		this.defaultValuePair = newDefaultValuePair;
		Object newDefaultAttributeValue = 
			this.defaultValuePair == null ? null : this.defaultValuePair.attributeValue;
		this.firePropertyChanged(DEFAULT_ATTRIBUTE_VALUE_PROPERTY, oldDefaultAttributeValue, newDefaultAttributeValue);
	}
	
	
	// **************** Miscellaneous Internal Behavior ***********************
	
	private Object buildAttributeValue(Object oldAttributeValue) 
		throws ConversionException
	{
		Class javaClass = null;
		try {
			javaClass = ClassTools.classForTypeDeclaration(getAttributeType().typeName(), getAttributeType().getDimensionality());
		} catch (ClassNotFoundException e) {
			//this is unlikely to happen, so just throw a runtimeException
			throw new RuntimeException(e);
		}
		
		return ConversionManager.getDefaultManager().convertObject(oldAttributeValue, javaClass);
	}
	
	
	private Object buildDataValue(Object oldDataValue) 
		throws ConversionException
	{
		Class javaClass = null;
		try {
			javaClass = ClassTools.classForTypeDeclaration(getDataType().typeName(), getDataType().getDimensionality());
		} catch (ClassNotFoundException e) {
			//this is unlikely to happen, so just throw a runtimeException
			throw new RuntimeException(e);
		}
		
		return ConversionManager.getDefaultManager().convertObject(oldDataValue, javaClass);
	}
	
	
	//************* Problem Handling ************
	
	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkValuePairs(currentProblems);
		
		//this.databaseTypeMatchesFieldTypeTest(currentProblems);
	}
	
	private void checkValuePairs(List currentProblems) {
		if (this.valuePairsSize() == 0) {
			currentProblems.add(buildProblem(ProblemConstants.MAPPING_VALUE_PAIRS_NOT_SPECIFIED));
		}
	}	
	
	//TODO this only applies for relational directToFieldMappings
	//do we need subclasses just for this one thing?
	//Will we need this rule for xml ObjectTypeMappings
//	private void databaseTypeMatchesFieldTypeTest(Set currentProblems) {
//		// if the dictionary is empty then this rule doesn't apply yet
//		if (!dataValues().hasNext())
//			return;
//		MWClass databaseType = getDataType();
//		if (getField() == null)
//			return;
//		MWDatabaseType fieldType = getField().getType();
//		if (databaseType == null || fieldType == null)
//			return;
//		MWDatabaseType fieldTypeShouldBe = getProject().getDatabase().getPlatform().databaseTypeForJavaTypeNamed(databaseType.getName());
//		if (fieldTypeShouldBe == null)
//			return;
//		if(!fieldType.equals(fieldTypeShouldBe)) {
//			currentProblems.add(buildProblem(ProblemConstants.XXXXXX_YYYYY));
//		}
//	}
	
	
	// **************** Runtime conversion ************************************
	
	public Converter runtimeConverter(DatabaseMapping mapping) {
		ObjectTypeConverter converter = new ObjectTypeConverter(mapping);
		
		for (Iterator stream = this.valuePairs(); stream.hasNext(); ) {
			ValuePair valuePair = (ValuePair) stream.next();
			converter.addConversionValue(valuePair.dataValue, valuePair.attributeValue);
		}
		
		converter.setDefaultAttributeValue(this.getDefaultAttributeValue());
		
		return converter;
	}

	
	// **************** TopLink Methods ***************************************
	
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
	
		descriptor.setJavaClass(MWObjectTypeConverter.class);
		descriptor.getInheritancePolicy().setParentClass(MWTypeConverter.class);
				
		// value pairs
		XMLCompositeCollectionMapping valuePairsMapping = new XMLCompositeCollectionMapping();
		valuePairsMapping.setAttributeName("valuePairs");
		valuePairsMapping.setXPath("value-pairs/value-pair");
		valuePairsMapping.setGetMethodName("getValuePairsForTopLink");
		valuePairsMapping.setSetMethodName("setValuePairsForTopLink");
		valuePairsMapping.setReferenceClass(MWObjectTypeConverter.ValuePair.class);
		descriptor.addMapping(valuePairsMapping);
		
		// default attribute value
		//TODO does this need a null value, look at the getter
		descriptor.addDirectMapping("defaultValuePair", "getDefaultAttributeValueForTopLink", "setDefaultAttributeValueForTopLink", "default-attribute-value/text()");
		
		return descriptor;
	}
	
	public void postProjectBuild() {
		super.postProjectBuild();
		
		for (Iterator stream = this.valuePairs(); stream.hasNext(); ) {
			ValuePair nextPair = (ValuePair) stream.next();
			nextPair.attributeValue = this.buildAttributeValue(nextPair.attributeValue);
			nextPair.dataValue = this.buildDataValue(nextPair.dataValue);
		}
	}
	
	/**
	 * sort the mappings for TopLink
	 */
	private Collection getValuePairsForTopLink() {
		return CollectionTools.sort((List) valuePairs);
	}
	private void setValuePairsForTopLink(Collection valuePairs) {
		this.valuePairs = valuePairs;
	}
	
	private Object getDefaultAttributeValueForTopLink() {
		return this.getDefaultAttributeValue();
	}
	
	private void setDefaultAttributeValueForTopLink(Object defaultAttributeValue) {
		this.defaultValuePair = this.valuePairForAttributeValue(defaultAttributeValue);
	}
	
	// **************** Member classes ****************************************
	
	public static class ValuePair
		extends MWModel
		implements Comparable
	{
		Object dataValue;
			public final static String DATA_VALUE_PROPERTY = "dataValue";
		
		Object attributeValue;
			public final static String ATTRIBUTE_VALUE_PROPERTY = "attributeValue";
		
		public final static String DEFAULT_ATTRIBUTE_VALUE_PROPERTY = "defaultAttributeValue";
		
		
		private ValuePair() {
			super();
		}
		
		private ValuePair(MWObjectTypeConverter parent, Object dataValue, Object attributeValue) {
			super(parent);
			this.dataValue = dataValue;
			this.attributeValue = attributeValue;
		}
		
		public Object getDataValue() {
			return this.dataValue;
		}
		
		public String getDataValueAsString() {
			return (String) ConversionManager.getDefaultManager().convertObject(this.dataValue, String.class);
		}
		
		private void setDataValue(Object newDataValue) {
			Object oldDataValue = this.dataValue;
			this.dataValue = newDataValue;
			this.firePropertyChanged(DATA_VALUE_PROPERTY, oldDataValue, newDataValue);
		}
		
		public Object getAttributeValue() {
			return this.attributeValue;
		}
		
		public String getAttributeValueAsString() {
			return (String) ConversionManager.getDefaultManager().convertObject(this.attributeValue, String.class);
		}

		private void setAttributeValue(Object newAttributeValue) {
			Object oldAttributeValue = this.attributeValue;
			this.attributeValue = newAttributeValue;
			this.firePropertyChanged(ATTRIBUTE_VALUE_PROPERTY, oldAttributeValue, newAttributeValue);
		}
		
		public boolean isDefaultAttributeValue() {
			return this.attributeValue == getObjectTypeConverter().getDefaultAttributeValue();
		}
		
		public void setDefaultAttributeValue(boolean isNewDefaultValue) {
			ValuePair oldDefaultAttributeValuePair = this.getObjectTypeConverter().defaultValuePair;
			boolean isOldDefaultValue = oldDefaultAttributeValuePair == this;
			
			if (isNewDefaultValue == isOldDefaultValue) {
				return;
			}
			
			if (isOldDefaultValue) {
				this.getObjectTypeConverter().setDefaultAttributeValue(null);
			}
			else if (isNewDefaultValue) {
				// unset the old default value first
				if (oldDefaultAttributeValuePair != null) {
					oldDefaultAttributeValuePair.setDefaultAttributeValue(false);
				}
				
				this.getObjectTypeConverter().setDefaultAttributeValue(this.attributeValue);
			}
			
			this.firePropertyChanged(DEFAULT_ATTRIBUTE_VALUE_PROPERTY, isOldDefaultValue, isNewDefaultValue);
		}
		
		public MWObjectTypeConverter getObjectTypeConverter() {
			return (MWObjectTypeConverter) this.getMWParent();
		}
		
		public int compareTo(Object o) {
			return Collator.getInstance().compare(this.dataValue.toString(), ((ValuePair) o).dataValue.toString());
		}
		
		
		public static XMLDescriptor buildDescriptor() {	
			XMLDescriptor descriptor = new XMLDescriptor();
			
			descriptor.setJavaClass(MWObjectTypeConverter.ValuePair.class);
			
			descriptor.addDirectMapping("dataValue", "getDataValueForTopLink", "setDataValueForTopLink", "data-value/text()");
			descriptor.addDirectMapping("attributeValue", "getAttributeValueForTopLink", "setAttributeValueForTopLink", "attribute-value/text()");
			
			return descriptor;
		}
		
		private Object getDataValueForTopLink() {
			return this.getDataValueAsString();
		}
		
		private void setDataValueForTopLink(Object dataValue) {
			this.dataValue = dataValue;
		}
		
		private Object getAttributeValueForTopLink() {
			return this.getAttributeValueAsString();
		}
		
		private void setAttributeValueForTopLink(Object attributeValue) {
			this.attributeValue = attributeValue;
		}

		public void toString(StringBuffer sb) {
			sb.append(this.dataValue);
			sb.append(" => ");
			sb.append(this.attributeValue);
		}

	}
	
	
	public static class ConversionValueException 
		extends Exception 
	{
		private int error;
			private final static int DUPLICATE_DATA_VALUE 		= 0;
			private final static int DUPLICATE_ATTRIBUTE_VALUE 	= 1;
		
		private Object value;
		
		private ConversionValueException(int error, Object valueObject) {
			super();
			this.error = error;
			this.value = valueObject;
		}
		
		public static ConversionValueException duplicateDataValueException(Object value) {
			return new ConversionValueException(DUPLICATE_DATA_VALUE, value);
		}
		
		public static ConversionValueException duplicateAttributeValueException(Object value) {
			return new ConversionValueException(DUPLICATE_ATTRIBUTE_VALUE, value);
		}
		
		public boolean isRepeatedDataValue() {
			return this.error == DUPLICATE_DATA_VALUE;
		}
		
		public boolean isRepeatedAttributeValue() {
			return this.error == DUPLICATE_ATTRIBUTE_VALUE;
		}
	}
}
