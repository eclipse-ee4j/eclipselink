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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

//TODO this should be refactored further with a policy for classNameIsIndicator ~kfm
public abstract class MWClassIndicatorFieldPolicy extends MWAbstractClassIndicatorPolicy {

	private volatile boolean classNameIsIndicator;
		public final static String CLASS_NAME_IS_INDICATOR_PROPERTY = "classNameIsIndicator";

	private transient volatile MWTypeDeclaration indicatorType;	
		public final static String INDICATOR_TYPE_PROPERTY = "indicatorType";
		 
 
	private Collection classIndicatorValues;
		public final static String CLASS_INDICATOR_VALUES_COLLECTION = "classIndicatorValues";
	
	// only used by 4.5 projects
	private volatile Class legacyIndicatorType;
 	
	private volatile ConversionManager conversionManager;	// used to convert objects to various classes


	
	//These are only used for backward compatibility of 3.5 through 4.5 projects
	//Should not be used anywhere else
	public static Class[] ALLOWED_INDICATOR_TYPES = new Class[] {String.class, Integer.class, Boolean.class, Long.class};
	public static Class DEFAULT_INDICATOR_TYPE = String.class;
 


	// *************** static methods **************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWClassIndicatorFieldPolicy.class);	
		descriptor.getInheritancePolicy().setParentClass(MWAbstractClassIndicatorPolicy.class);


		XMLDirectMapping classNameIndicatorMapping = (XMLDirectMapping)descriptor.addDirectMapping("classNameIsIndicator", "class-name-is-indicator/text()");
		classNameIndicatorMapping.setNullValue(Boolean.FALSE);
		
		XMLCompositeObjectMapping indicatorTypeMapping = new XMLCompositeObjectMapping();
		indicatorTypeMapping.setAttributeName("indicatorType");	
		indicatorTypeMapping.setReferenceClass(MWTypeDeclaration.class);
		indicatorTypeMapping.setXPath("indicator-type");
		descriptor.addMapping(indicatorTypeMapping);

		XMLCompositeCollectionMapping classIndicatorValuesMapping = new XMLCompositeCollectionMapping();
		classIndicatorValuesMapping.setAttributeName("classIndicatorValues");
		classIndicatorValuesMapping.setGetMethodName("getIndicatorValuesForTopLink");
		classIndicatorValuesMapping.setSetMethodName("setIndicatorValuesForTopLink");
		classIndicatorValuesMapping.setReferenceClass(MWClassIndicatorValue.class);
		classIndicatorValuesMapping.setXPath("class-indicator-values/class-indicator-value");
		descriptor.addMapping(classIndicatorValuesMapping);

		return descriptor;
	}


	// *************** constructors **************

	protected MWClassIndicatorFieldPolicy() {
		super();
	}

	protected MWClassIndicatorFieldPolicy(MWClassIndicatorPolicy.Parent parent) {
		this(parent, NullIterator.instance());
	}

	protected MWClassIndicatorFieldPolicy(MWClassIndicatorPolicy.Parent parent, Iterator descriptorsAvailableForIndication) {
		super(parent);
		setDescriptorsAvailableForIndicatorDictionary(descriptorsAvailableForIndication);
	}
	
	/**
	 * initialize persistent state
	 */
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.indicatorType = new MWTypeDeclaration(this, this.typeFor(DEFAULT_INDICATOR_TYPE));
		this.classIndicatorValues = new Vector();
		this.classNameIsIndicator = false;
	}

	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.classIndicatorValues) { children.addAll(this.classIndicatorValues); }
		if (this.indicatorType != null) {
			children.add(this.indicatorType);
		}
	}
	
	
	// *************** accessors **************
	
	public String getType() {
		return CLASS_INDICATOR_FIELD_TYPE;
	}

    public abstract MWDataField getField();

	public MWClassIndicatorValue addIndicator(Object value, MWMappingDescriptor descriptor) {
		MWClassIndicatorValue indicator = new MWClassIndicatorValue(this, descriptor, value);
		this.classIndicatorValues.add(indicator);
		fireItemAdded(CLASS_INDICATOR_VALUES_COLLECTION, indicator);
		return indicator;
	}
	
	public void removeIndicator(MWClassIndicatorValue indicator) {
		this.removeItemFromCollection(indicator, this.classIndicatorValues, CLASS_INDICATOR_VALUES_COLLECTION);
	}
	
	public void removeIndicatorFor(MWDescriptor descriptor) {
		for (Iterator stream = this.classIndicatorValues(); stream.hasNext(); ) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) stream.next();
			if (value.getDescriptorValue() == descriptor) {
				this.removeIndicator(value);
				return;
			}
		}
	}
	
	public void setClassNameIsIndicator(boolean classNameIsIndicator) {
		boolean oldValue = this.classNameIsIndicator;
		this.classNameIsIndicator = classNameIsIndicator;
		if (oldValue != classNameIsIndicator) {
			if (classNameIsIndicator) {
				setIndicatorType(null);
			}
			else {
				setIndicatorType(new MWTypeDeclaration(this, typeNamed(DEFAULT_INDICATOR_TYPE.getName())));
				setDescriptorsAvailableForIndicatorDictionary(((MWDescriptorInheritancePolicy) getParent()).getAllDescriptorsAvailableForIndicatorDictionary().iterator());
			}
		}

		firePropertyChanged(CLASS_NAME_IS_INDICATOR_PROPERTY, oldValue, classNameIsIndicator);
	}


	public void setIndicatorType(MWTypeDeclaration newIndicatorType) {
		MWTypeDeclaration oldValue = getIndicatorType();
		this.indicatorType = newIndicatorType;
		convertValues();
		firePropertyChanged(INDICATOR_TYPE_PROPERTY, oldValue, newIndicatorType);
	}

	public boolean classNameIsIndicator() {
		return this.classNameIsIndicator;
	}



	/**
	 * Convert the string to an object of the specified class.
	 * If there are any problems, do nothing - the value will not be built.
	 */
	public Object buildIndicatorValueFromString(String valueString) 
	{
		try 
		{
			Class javaClass = ClassTools.classForTypeDeclaration(getIndicatorType().typeName(), getIndicatorType().getDimensionality());
			return this.getConversionManager().convertObject(valueString, javaClass);
		}
		catch (ClassNotFoundException ex) 
		{
			// it's very unlikely this will happen since
			// we try to restrict the class to common classes (e.g. String, Date)
		}
		
		throw ConversionException.couldNotBeConverted(valueString, null);
	}

	public void clearIndicatorValues() {
		Iterator values = classIndicatorValues();
		
		while (values.hasNext()) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) values.next();
			removeIndicator(value);
		}
	}
	
	public void convertValues() {
		if (getIndicatorType() == null) {
			clearIndicatorValues();
		}
		
		for (Iterator i = classIndicatorValues(); i.hasNext(); ) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) i.next();
			Object before = value.getIndicatorValue();
			
			if (before != null) {
				try {
					Object after = getConversionManager().convertObject(before, ClassTools.classForTypeDeclaration(getIndicatorType().typeName(), getIndicatorType().getDimensionality()));
					value.setIndicatorValue(after);
				} 
				catch (ClassNotFoundException ex) {			
					throw new RuntimeException(ex);
				}	
				catch (ConversionException ex) {
					value.setIndicatorValue(null);
				}
			}
		}
	}
	
	
	public MWClassIndicatorValue getClassIndicatorValueForDescriptor(MWMappingDescriptor descriptor) 
	{
		Iterator i = classIndicatorValues();
		while (i.hasNext()) {
			MWClassIndicatorValue indicatorValue = (MWClassIndicatorValue) i.next();
			if (indicatorValue.getDescriptorValue().equals(descriptor)) {
				return indicatorValue;
			}
		}
		return null;
	}
		
	public Iterator classIndicatorValues() {
		return new CloneIterator(this.classIndicatorValues);
	}

	public int classIndicatorValuesSize() {
		return this.classIndicatorValues.size();
	}
	
	public ConversionManager getConversionManager() {
		if (this.conversionManager == null) {
			this.conversionManager = new ConversionManager();
		}
		return this.conversionManager;
	}
		
	public MWDescriptor getDescriptorForIndicator(Object indicator) {
		Iterator i = includedClassIndicatorValues();
		while (i.hasNext()) {
			MWClassIndicatorValue indicatorValue = (MWClassIndicatorValue) i.next();
			
			if (indicatorValue.getIndicatorValue() != null && indicatorValue.getIndicatorValue().equals(indicator)) {
				return indicatorValue.getDescriptorValue();
			}
		}
		return null;
	}
		
	public Object getIndicatorForDescriptor(MWMappingDescriptor descriptor) {
		Iterator i = classIndicatorValues();
		while (i.hasNext()) {
			MWClassIndicatorValue indicatorValue = (MWClassIndicatorValue) i.next();
			if (indicatorValue.getDescriptorValue().equals(descriptor)) {
				return indicatorValue.getIndicatorValue();
			}
		}
		return null;
	}
	
	public MWTypeDeclaration getIndicatorType() {
		return this.indicatorType;
	}
		
	public Iterator includedClassIndicatorValues() {
		return getIndicatorValuesForTopLink().iterator();
	}

	public boolean isRepeatedIndicatorValue(Object indicatorValue) {
		Iterator values = classIndicatorValues();
		while (values.hasNext()) {
			Object value = ((MWClassIndicatorValue) values.next()).getIndicatorValue();
			if (value != null && value.equals(indicatorValue)) {
				return true;
			}
		}
		
		return false;
	}
	

	public void rebuildClassIndicatorValues(Collection descriptors) {
		for (Iterator i = classIndicatorValues(); i.hasNext();) {
			MWClassIndicatorValue indicatorValue = (MWClassIndicatorValue) i.next();
			if (!descriptors.contains(indicatorValue.getDescriptorValue()) ){
				removeIndicator(indicatorValue);
			}	
		}
		
		setDescriptorsAvailableForIndicatorDictionary(descriptors.iterator());
	}
	
	public void setDescriptorsAvailableForIndicatorDictionary(Iterator descriptors) {	
		if (classNameIsIndicator()) {
			setIndicatorType(null);
			return;
		}
		while (descriptors.hasNext()) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) descriptors.next();
			if (getClassIndicatorValueForDescriptor(descriptor) == null)
				addIndicator(null, descriptor);
		}	
	}
	
	public void setDescriptorsAvailableForIndicatorDictionaryForTopLink(Iterator descriptors) {	
		if (classNameIsIndicator()) {
			setIndicatorType(null);
			return;
		}
		while (descriptors.hasNext()) {
			MWMappingDescriptor descriptor = (MWMappingDescriptor) descriptors.next();
			if (getClassIndicatorValueForDescriptor(descriptor) == null) {
				MWClassIndicatorValue value = addIndicator(null, descriptor);
				value.setInclude(false);
			}
		}	
	}
	
	public void resetDescriptorAvailableForIndication(Iterator descriptors) {
		Iterator currentIndicators = classIndicatorValues();
		while (currentIndicators.hasNext()) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) currentIndicators.next();
			removeIndicator(value);
		}
	
		setDescriptorsAvailableForIndicatorDictionary(descriptors);
	}

	protected abstract boolean fieldSpecified();

	
	//*************** Problem Handling *************
	
	public void checkClassIndicatorField(List newProblems) {
		if ( ! this.fieldSpecified()) {
			newProblems.add(this.buildProblem(ProblemConstants.NO_CLASS_INDICATOR_FOR_ROOT_CLASS));
		}
	}

	
	// ************* Runtime Conversion ***********

	public void adjustRuntimeInheritancePolicy(InheritancePolicy runtimeInheritancePolicy) {
		Iterator indicatorValues = includedClassIndicatorValues();
		while (indicatorValues.hasNext()) { 
			Object value = ((MWClassIndicatorValue)indicatorValues.next()).getIndicatorValue();
			if (value != null)
				runtimeInheritancePolicy.addClassNameIndicator(getDescriptorForIndicator(value).getMWClass().fullName(), value);
		}
		runtimeInheritancePolicy.setShouldUseClassNameAsIndicator(classNameIsIndicator());
	}


	// ************* TopLink only methods ***********

	public void postProjectBuild() {
		super.postProjectBuild();
		// convert the values because they are converted to strings when written out
		this.convertValues();
	}

	private List getIndicatorValuesForTopLink() {
		List topLinkValues = new ArrayList();
		for (Iterator stream = classIndicatorValues(); stream.hasNext(); ) {
			MWClassIndicatorValue value = (MWClassIndicatorValue) stream.next();
			if (value.isInclude()) {
				topLinkValues.add(value);
			}
		}
		return topLinkValues;
	}
		
	private void setIndicatorValuesForTopLink(List indicatorValues) {
		this.classIndicatorValues = indicatorValues;		
	}
	
}
