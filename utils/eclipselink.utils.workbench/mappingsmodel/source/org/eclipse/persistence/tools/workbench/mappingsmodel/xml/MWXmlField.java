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
package org.eclipse.persistence.tools.workbench.mappingsmodel.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWDataField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSimpleTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWXmlField
	extends MWModel
	implements MWDataField, MWXmlNode
{
	// **************** Variables *********************************************
	
	/** The string representation of this xpath */
	private volatile String xpath = "";
		public final static String XPATH_PROPERTY = "xpath";
	
	/** Whether this xpath is aggregated into its parent element (xpath = ".") */
	private volatile boolean aggregated = false;
		public final static String AGGREGATED_PROPERTY = "aggregated";
		
	/** 
	 * Whether the field represented by this xpath should use xsi:type.
	 * This xpath must be a "text()" field in order for this to be true.
	 */
	private volatile boolean typed;
		public final static String TYPED_PROPERTY = "typed";
	
	/** 
	 * Whether this xml field should collate text data into a single field
	 * as opposed to writing multiple fields, each with a single text item.
	 * This is only appropriate for direct collection mappings.
	 */
	private volatile boolean useSingleNode = false;
		public final static String USE_SINGLE_NODE_PROPERTY = "useSingleNode";
	
	/** The xpath steps resolved to by the xpath string */
	private transient Vector xpathSteps;
	
	/** Whether the xpath string resolves to a valid schema location */
	private transient boolean resolved;
		public final static String RESOLVED_PROPERTY = "resolved";
	
	/** Whether the xpath string resolves correctly to valid positions */
	private transient boolean validPosition = true;
		public final static String VALID_POSITION_PROPERTY = "validPosition";
	
	/** Whether the xpath string resolves correctly to "text()" */
	private transient boolean validText = true;
		public final static String VALID_TEXT_PROPERTY = "validText";
	
	/** Used for text() xpaths */
	public static final String TEXT = "text()";
	
	/** Used for runtime conversion */
	private transient static Map jdbcTypeMap;
	
	
	// **************** Constructors ******************************************
	
	/** TopLink use only */
	private MWXmlField() {
		super();
	}
	
	public MWXmlField(MWXpathContext parent) {
		super(parent);
	}
	
	
	// **************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		this.xpathSteps = new Vector();
	}
	
	protected void addTransientAspectNamesTo(Set transientAspectNames) {
		super.addTransientAspectNamesTo(transientAspectNames);
		transientAspectNames.add(RESOLVED_PROPERTY);
		transientAspectNames.add(VALID_POSITION_PROPERTY);
		transientAspectNames.add(VALID_TEXT_PROPERTY);
	}
	
	
	// **************** Convenience *******************************************
	
	public MWXpathContext getXpathContext() {
		return (MWXpathContext) this.getParent();
	}
	
	public MWSchemaContextComponent schemaContext() {
		return this.getXpathContext().schemaContext(this);
	}
	
	public MWXpathSpec xpathSpec() {
		return this.getXpathContext().xpathSpec(this);
	}
	
	protected Iterator xpathSteps() {
		return this.xpathSteps.iterator();
	}
	
	
	// **************** Xpath string ******************************************
	
	public String getXpath() {
		return this.xpath;
	}
	
	public void setXpath(String newXpath) {
		if (newXpath == null) {
			newXpath = "";
		}
		
		String oldXpath = this.xpath;
		this.xpath = newXpath;
		if (this.attributeValueHasChanged(oldXpath, newXpath)) {
			this.firePropertyChanged(XPATH_PROPERTY, oldXpath, newXpath);
			this.firePropertyChanged(FIELD_NAME_PROPERTY, newXpath);
			this.resolve();
			
			if ( ! this.xpath.equals("")) {
				this.setAggregated(false);
			}
			
			if (! this.xpath.endsWith(TEXT)) {
				this.setTyped(false);
			}
		}
	}
	
	/** Return true if ends with "text()" */
	public boolean isTextXpath() {
		return this.getXpath().endsWith(TEXT);
	}
	
	/** Return true if contains "@" */
	public boolean isAttributeXpath() {
		return this.getXpath().indexOf('@') != -1;
	}
	
	/** 
	 * Return true if contains any positional information (e.g. "[1]") 
	 * (For now, will assume that if the string contains '[' or ']', it does.)
	 */
	public boolean isPositionalXpath() {
		if (this.isResolved()) {
			for (Iterator stream = this.xpathSteps(); stream.hasNext(); ) {
				if (((MWXpathStep) stream.next()).isPositional()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/** Return true if this points to an attribute or to the text node of an element */
	public boolean isDirect() {
		if (this.isResolved() && ! this.isAggregated()) {
			MWXpathStep lastStep = (MWXpathStep) this.xpathSteps.lastElement();
			return lastStep.isAttribute() || lastStep.isText();
		}
		
		return false;
	}
	
	/** 
	 * Return true is this points to a singular node.
	 * e.g. The XPath "foo" is not singular if there can be multiple foo's.
	 * e.g. The XPath "foo[2]" is always singular.
	 * e.g. The XPath "bar/foo[2]" is not singular if there can be multiple bar's.
	 */
	public boolean isSingular() {
		boolean singular = true;
		
		for (Iterator stream = this.xpathSteps(); stream.hasNext(); ) {
			singular &= ((MWXpathStep) stream.next()).isSingular();
		}
		
		return singular;
	}
	
	/**
	 * Return true if this field "contains" the other field.
	 * Basically this just means that the other field's xpath
	 * "starts with" this field's xpath (plus a "/" character).
	 * 
	 * This won't make sense in *some* cases.  Obviously, an attribute 
	 * xml field can't contain anything, but generally speaking, we won't
	 * end up with any valid xpaths that don't start with elements.
	 */
	public boolean containsXmlField(MWXmlField otherField) {
		return otherField.getXpath().startsWith(this.getXpath());
	}
	
	
	// **************** Aggregated ********************************************
	
	public boolean isAggregated() {
		return this.aggregated;
	}
	
	public void setAggregated(boolean newValue) {
		boolean oldValue = this.aggregated;
		this.aggregated = newValue;
		this.firePropertyChanged(AGGREGATED_PROPERTY, oldValue, newValue);
		
		if (newValue) {
			this.setXpath("");
		}
	}
	
	
	// **************** Typed *************************************************
	
	public boolean isTyped() {
		return this.typed;
	}
	
	public void setTyped(boolean newValue) {
		boolean oldValue = this.typed;
		this.typed = newValue;
		this.firePropertyChanged(TYPED_PROPERTY, oldValue, newValue);
	}
	
	
	// **************** Use single node ***************************************
	
	public boolean usesSingleNode() {
		return this.useSingleNode;
	}
	
	public void setUseSingleNode(boolean newValue) {
		boolean oldValue = this.useSingleNode;
		this.useSingleNode = newValue;
		this.firePropertyChanged(USE_SINGLE_NODE_PROPERTY, oldValue, newValue);
	}
	
	
	
	// **************** Resolution/Validation *********************************
	
	public boolean isSpecified() {
		return ! "".equals(this.xpath) || this.isAggregated();
	}
	
	public boolean isResolved() {
		return this.resolved;
	}
	
	/** Should only be used internally */
	private void setResolved(boolean newValue) {
		boolean oldValue = this.resolved;
		this.resolved = newValue;
		this.firePropertyChanged(RESOLVED_PROPERTY, oldValue, newValue);
	}
	
	/** Should only be used internally */
	private void setValidText(boolean newValue) {
		boolean oldValue = this.validText;
		this.validText = newValue;
		this.firePropertyChanged(VALID_TEXT_PROPERTY, oldValue, newValue);
	}
	
	/** Should only be used internally */
	private void setValidPosition(boolean newValue) {
		boolean oldValue = this.validPosition;
		this.validPosition = newValue;
		this.firePropertyChanged(VALID_POSITION_PROPERTY, oldValue, newValue);
	}
	
	public boolean isValid() {
		return this.isResolved() && this.validText && this.validPosition;
	}
	
	
	// **************** Internal **********************************************
	
	private void resolve() {
		this.xpathSteps.clear();
		
		StringTokenizer tokenizer = new StringTokenizer(this.xpath, "/");
		
		while (tokenizer.hasMoreTokens()) {
			this.xpathSteps.add(new MWXpathStep(this, tokenizer.nextToken()));
		}
		
		MWSchemaContextComponent schemaContext = this.schemaContext();
		boolean resolved = this.isSpecified();
		
		for (Iterator stream = this.xpathSteps(); resolved && stream.hasNext(); ) {
			MWXpathStep nextStep = (MWXpathStep) stream.next();
			schemaContext = nextStep.resolveContext(schemaContext);
			resolved &= nextStep.isResolved();
		}
		
		if (! resolved) {
			this.xpathSteps.clear();
		}
		
		this.setResolved(resolved);
		
		this.validateXpath();
	}
	
	private void resynchXpath() {
		// if we are currently resolved, update the string
		if (this.isResolved()) {
			
			String xpath = "";
			
			for (Iterator stream = this.xpathSteps.iterator(); stream.hasNext(); ) {
				MWXpathStep nextStep = (MWXpathStep) stream.next();
				nextStep.updateStepString();
				xpath += nextStep.getStepString();
				
				if (stream.hasNext()) {
					xpath += "/";
				}
			}
			
			this.setXpath(xpath);
		}
		// otherwise, attempt to resolve
		else {
			this.resolve();
		}
	}
	
	private void validateXpath() {
		if (this.isTextXpath()) {
			if (this.isResolved()) {
				this.validateTextXpath();
			}
			else {
				// if it's not resolved, it can't be valid text
				this.setValidText(false);
			}
		}
		else {
			// if it's not text, it can't be invalid text
			this.setValidText(true);
		}
		
		if (this.isPositionalXpath()) {
			if (this.isResolved()) {
				this.validatePositionalXpath();
			}
			else {
				// if it's not resolved, it can't be valid position
				this.setValidPosition(false);
			}
		}
		else {
			// if it's not positional, it can't be invalid position
			this.setValidPosition(true);
		}
	}
	
	/** Only for resolved text xpaths */
	private void validateTextXpath() {
		boolean valid = true;
		
		for (Iterator stream = this.xpathSteps(); stream.hasNext(); ) {
			MWXpathStep step = (MWXpathStep) stream.next();
			
			if (step.isText()) {
				valid &= step.isValid();
			}
		}
		
		this.setValidText(valid);
	}
	
	/** Only for resolved positional xpaths */
	private void validatePositionalXpath() {
		boolean valid = true;
		
		for (Iterator stream = this.xpathSteps(); stream.hasNext(); ) {
			MWXpathStep step = (MWXpathStep) stream.next();
			
			if (step.isPositional()) {
				valid &= step.isValid();
			}
		}
		
		this.setValidPosition(valid);
	}
	
	
	// **************** Model synchronization *********************************
	
	/** @see MWXmlNode#resolveXpaths() */
	public void resolveXpaths() {
		this.resolve();
	}
	
	/** @see MWXmlNode#schemaChanged(SchemaChange) */
	public void schemaChanged(SchemaChange change) {
		if (change.getChangeType() == SchemaChange.SCHEMA_STRUCTURE_CHANGED) {
			this.resolve();
		}
		else if (change.getChangeType() == SchemaChange.SCHEMA_NAMESPACE_PREFIXES_CHANGED) {
			this.resynchXpath();
		}
	}
	
	
	// **************** MWDataField handling **************************************
	
	public String fieldName() {
		return this.xpath;
	}
	
	
	// **************** Problem handling **************************************

	protected void addProblemsTo(List currentProblems) {
		super.addProblemsTo(currentProblems);
		this.checkXpath(currentProblems);
	}
	
	private void checkXpath(List currentProblems) {
		if (this.isResolved()) {
			if ( ! this.validText) {
				currentProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_VALID_TEXT, this.getXpath()));
			}
			if ( ! this.validPosition) {
				currentProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_VALID_POSITION, this.getXpath()));
			}
		} else {
			if ( ! "".equals(this.getXpath())) {
				currentProblems.add(this.buildProblem(ProblemConstants.XPATH_NOT_RESOLVED, this.getXpath()));
			}
		}
	}
	
	
	// **************** Runtime conversion ************************************
	
	public String runtimeFieldName() {
		return this.xpath;
	}
	
	public DatabaseField runtimeField() {
		if (! this.isSpecified()) {
			return null;
		}
		
		XMLField runtimeField = this.buildRuntimeField();
		runtimeField.setXPath(this.runtimeXpath());
		return runtimeField;
	}
	
	public DatabaseField runtimeField(MWXmlField groupingElement) {
		if (! this.isSpecified()) {
			return null;
		}
		
		XMLField runtimeField = this.buildRuntimeField();
		
		if (groupingElement.isSpecified()) {
			runtimeField.setXPath(this.runtimeXpath(groupingElement));
		}
		else {
			runtimeField.setXPath(this.runtimeXpath());
		}
		
		return runtimeField;
	}
	
	private XMLField buildRuntimeField() {
		XMLField runtimeField = null;
		
		Vector dataTypes = CollectionTools.vector(this.baseBuiltInTypes());
		
		if (dataTypes.size() > 1) {
			runtimeField = new XMLUnionField();
			this.adjustUnionSchemaTypes((XMLUnionField) runtimeField, dataTypes);
		}
		else {
			runtimeField = new XMLField();
			this.adjustNonUnionSchemaTypes(runtimeField, dataTypes);
		}
		
		if (this.isTyped()) {
			runtimeField.setIsTypedTextField(true);
		}
		
		if (this.usesSingleNode()) {
			runtimeField.setUsesSingleNode(true);
		}
		
		return runtimeField;
	}
	
	private String runtimeXpath() {
		if (this.isAggregated()) {
			return ".";
		}
		else {
			return this.getXpath();
		}
	}
	
	private String runtimeXpath(MWXmlField groupingElement) {
		String runtimeXpath = this.runtimeXpath();
		
		if (groupingElement.containsXmlField(this)) {
			String groupingRuntimeXpath = groupingElement.getXpath();
			if (runtimeXpath.length() > groupingRuntimeXpath.length()) {
				return runtimeXpath.substring(groupingRuntimeXpath.length() + 1); // account for the additional "/"
			}
			return runtimeXpath;
		}
		else {
			return runtimeXpath;
		}
	}
	
	private Iterator baseBuiltInTypes() {
		MWSchemaContextComponent component = this.xpathComponent();
		
		if (component == null) {
			return NullIterator.instance();
		}
		else {
			return component.baseBuiltInTypes();
		}
	}
	
	/** Return the component "pointed to" by this xml field */
	private MWSchemaContextComponent xpathComponent() {
		MWSchemaContextComponent xpathComponent = null;
		
		if (TEXT.equals(this.getXpath())) {
			xpathComponent = this.schemaContext();
		}
		
		for (Iterator stream = this.xpathSteps(); stream.hasNext(); ) {
			MWXpathStep nextStep = (MWXpathStep) stream.next();
			
			if (nextStep.xpathComponent() != null) {
				xpathComponent = nextStep.xpathComponent();
			}
		}
		
		return xpathComponent;
	}
	
	private void adjustUnionSchemaTypes(XMLUnionField runtimeField, Vector baseBuiltInTypes) {
		if (! this.isDirect()) {
			return;
		}
		
		for (Iterator stream = baseBuiltInTypes.iterator(); stream.hasNext(); ) {
			MWSimpleTypeDefinition baseBuiltInType = (MWSimpleTypeDefinition) stream.next();
			
			if (XMLConstants.SCHEMA_URL.equals(baseBuiltInType.getNamespaceUrl())) {
				QName schemaType = this.runtimeSchemaType(baseBuiltInType);
				List runtimeSchemaTypes = runtimeField.getSchemaTypes();
				
				if (runtimeSchemaTypes == null || ! runtimeSchemaTypes.contains(schemaType)) {
					runtimeField.addSchemaType(schemaType);
				}
			}
		}
	}
	
	private void adjustNonUnionSchemaTypes(XMLField runtimeField, Vector baseBuiltInTypes) {
		if (! this.isDirect()) {
			return;
		}
		
		// really should only have at most one type here, but iterating makes it convenient
		// for the case that the vector is empty
		for (Iterator stream = baseBuiltInTypes.iterator(); stream.hasNext(); ) {
			MWSimpleTypeDefinition baseBuiltInType = (MWSimpleTypeDefinition) stream.next();
			
			if (XMLConstants.SCHEMA_URL.equals(baseBuiltInType.getNamespaceUrl())) {
				QName runtimeSchemaType = this.runtimeSchemaType(baseBuiltInType);
				
				if (this.shouldSetNonUnionRuntimeSchemaType(runtimeSchemaType)) {
					runtimeField.setSchemaType(runtimeSchemaType);
				}
			}
		}
	}
	
	/** These are the only types that are important if there is only one schema type */
	private boolean shouldSetNonUnionRuntimeSchemaType(QName runtimeSchemaType) {
		return 
			   XMLConstants.DATE_QNAME.equals(runtimeSchemaType)
			|| XMLConstants.TIME_QNAME.equals(runtimeSchemaType)
			|| XMLConstants.DATE_TIME_QNAME.equals(runtimeSchemaType)
			|| XMLConstants.BASE_64_BINARY_QNAME.equals(runtimeSchemaType)
			|| XMLConstants.HEX_BINARY_QNAME.equals(runtimeSchemaType);
	}
	
	private QName runtimeSchemaType(MWSimpleTypeDefinition baseBuiltInType) {
		String baseDataType = (String) this.jdbcTypeMap().get(baseBuiltInType.getName());
		return new QName(XMLConstants.SCHEMA_URL, baseDataType);
	}
	
	private Map jdbcTypeMap() {
		if (jdbcTypeMap == null) {
			buildJdbcTypeMap();
		}
		
		return jdbcTypeMap;
	}
	
	private static void buildJdbcTypeMap() {
		Map typeMap = new HashMap();
		
		// ur-type
		typeMap.put("anySimpleType", "anySimpleType");
		
		// primitive types
		typeMap.put("duration", "anySimpleType");
		typeMap.put("dateTime", "dateTime");  			// transforms as java.util.Calendar
		typeMap.put("time", "time");					// transforms as java.util.Calendar
		typeMap.put("date", "date");					// transforms as java.util.Calendar
		typeMap.put("gYear", "anySimpleType");
		typeMap.put("gYearMonth", "anySimpleType");
		typeMap.put("gMonth", "anySimpleType");
		typeMap.put("gMonthDay", "anySimpleType");
		typeMap.put("gDay", "anySimpleType");
		typeMap.put("boolean", "boolean");				// transforms as boolean
		typeMap.put("base64Binary", "base64Binary");	// transforms as byte[]
		typeMap.put("hexBinary", "hexBinary");			// transforms as byte[]
		typeMap.put("anyURI", "anySimpleType");
		typeMap.put("QName", "anySimpleType");
		typeMap.put("NOTATION", "anySimpleType");		// should be ignored ?
		
		// string types
		typeMap.put("string", "anySimpleType");
		typeMap.put("normalizedString", "anySimpleType");
		typeMap.put("token", "anySimpleType");
		typeMap.put("language", "anySimpleType");
		typeMap.put("Name", "anySimpleType");
		typeMap.put("NMTOKEN", "anySimpleType");
		typeMap.put("NCName", "anySimpleType");
		typeMap.put("ID", "anySimpleType");
		typeMap.put("IDREF", "IDREF");					// transforms as java.lang.Object
		typeMap.put("IDREFS", "IDREFS");				// transforms as ????
		typeMap.put("ENTITY", "anySimpleType");			// should be ignored ?
		
		// number types
		typeMap.put("float", "float");					// transforms as float
		typeMap.put("double", "double");				// transforms as double
		typeMap.put("decimal", "decimal");				// transforms as java.math.BigDecimal
		typeMap.put("integer", "integer");				// transforms as java.math.BigInteger
		typeMap.put("nonPositiveInteger", "integer");	// transforms as java.math.BigInteger
		typeMap.put("negativeInteger", "integer");		// transforms as java.math.BigInteger
		typeMap.put("nonNegativeInteger", "integer");	// transforms as java.math.BigInteger
		typeMap.put("positiveInteger", "integer");		// transforms as java.math.BigInteger
		typeMap.put("unsignedLong", "integer");			// transforms as java.math.BigInteger
		typeMap.put("unsignedInt", "unsignedInt");		// transforms as long
		typeMap.put("unsignedShort", "unsignedShort");	// transforms as int
		typeMap.put("unsignedByte", "unsignedByte");	// transforms as short
		typeMap.put("long", "long");					// transforms as long
		typeMap.put("int", "int");						// transforms as int
		typeMap.put("short", "short");					// transforms as short
		typeMap.put("byte", "byte");					// transforms as byte
		
		jdbcTypeMap = typeMap;
	}
	
	/** 
	 * It is assumed that xmlField1 and xmlField2 are both relative to this schema context.
	 * 
	 * Return -1 if xmlField1 comes before xmlField2, +1 if it comes after, or 0
	 * if there is no order constraint imposed by the schema on the two fields.
	 */
	public static int compareSchemaOrder(MWXmlField xmlField1, MWXmlField xmlField2) {
		if (xmlField1 == xmlField2 
			|| xmlField1 == null || xmlField2 == null
			|| ! xmlField1.isResolved() || ! xmlField2.isResolved()
		) {
			return 0;
		}
		else if (xmlField1.schemaContext() != xmlField2.schemaContext()) {
			throw new IllegalStateException(
				"XML field \"" + xmlField1.getXpath() 
				+ "\" and XML field \"" + xmlField2.getXpath() 
				+ "\" are not in the same context.");
		}
		else {
			return compareSchemaOrder(xmlField1.schemaContext(), xmlField1.xpathSteps(), xmlField2.xpathSteps());
		}
	}
	
	public static int compareSchemaOrder(MWSchemaContextComponent contextComponent, Iterator xpathSteps1, Iterator xpathSteps2) {
		if (! xpathSteps1.hasNext() || ! xpathSteps2.hasNext()) {
			return 0;
		}
		
		MWXpathStep step1 = (MWXpathStep) xpathSteps1.next();
		MWXpathStep step2 = (MWXpathStep) xpathSteps2.next();
		
		int comparison = MWXpathStep.compareSchemaOrder(contextComponent, step1, step2);
		
		if (comparison == 0 && step1.xpathComponent() == step2.xpathComponent()) {
			return compareSchemaOrder(step1.xpathComponent(), xpathSteps1, xpathSteps2);
		}
		else {
			return comparison;
		}
	}
	
	
	// **************** printing/displaying ***************************************
	
	public void toString(StringBuffer sb) {
		super.toString(sb);
		sb.append("\"" + this.xpath + "\"");
	}
	
	public String displayString() {
		return this.xpath;
	}
	
	
	// **************** TopLink methods ***************************************
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWXmlField.class);
		
		((XMLDirectMapping) descriptor.addDirectMapping("xpath", "text()")).setNullValue("");
		((XMLDirectMapping) descriptor.addDirectMapping("aggregated", "@aggregated")).setNullValue(Boolean.FALSE);
		((XMLDirectMapping) descriptor.addDirectMapping("typed", "@typed")).setNullValue(Boolean.FALSE);
		
		return descriptor;
	}
	
	public void postProjectBuild() {
		super.postProjectBuild();
		
		this.resolve();
	}
}
