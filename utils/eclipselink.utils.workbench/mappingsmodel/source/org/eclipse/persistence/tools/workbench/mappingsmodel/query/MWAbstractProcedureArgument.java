package org.eclipse.persistence.tools.workbench.mappingsmodel.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

/**
 * @version 1.1
 * @since 1.1
 * @author Les Davis
 */
@SuppressWarnings("nls")
public abstract class MWAbstractProcedureArgument extends MWModel
{
	private static HashMap<String, Integer> jdbcTypes;
		public static final String JDBC_TYPES_PROPERTY = "jdbcTypes";
		private static HashMap<Integer, String> jdbcTypesCodes;

	private String passType;
		public static final String PASS_TYPE_PROPERTY = "passType";
		public static final String PARAMETER_TYPE = "paramter type";
		public static final String VALUE_TYPE = "value type";
		
	//limited to values convertible by TopLink runtime DTF
	private String argumentValue;
		public static final String ARGUMENT_VALUE_PROPERTY = "argumentValue";
		
	private String argumentName;
		public static final String ARGUMENT_NAME_PROPERTY = "argumentName";

	private String fieldName;
		public static final String FIELD_NAME_PROPERTY = "fieldName";
		
	private String fieldSqlTypeName;
		public static final String FIELD_SQL_TYPE_PROPERTY = "fieldSqlType";
		
	private String fieldSqlSubTypeName;
		public static final String FIELD_SQL_TYPE_NAME_PROPERTY = "fieldSqlSubTypeName";

	private String fieldJavaClassName;
		public static final String FIELD_JAVA_CLASS_NAME_PROPERTY = "fieldJavaClassName";
		
	private String nestedTypeFieldName;
		public static final String NESTED_TYPE_FIELD_NAME_PROPERTY = "nestedTypeFieldName";
	
	//not persisted
	private transient ConversionManager conversionManager;
		
	/**
 	 * Default constructor for TopLink use only
	 */
	protected MWAbstractProcedureArgument() {
		super();
	}

	MWAbstractProcedureArgument(MWProcedure procedure, String name) {
		super(procedure);
		this.argumentName = name;
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.passType = PARAMETER_TYPE;
	}
	
	/**
	 * Returns <code>true</code> for named stored procedure, <code>false</code>
	 * for un-named
	 */
	abstract boolean isNamed();
	
	public abstract boolean isNamedIn();
	
	public abstract boolean isNamedOut();
	
	public abstract boolean isNamedInOut();
	
	public abstract boolean isUnnamedIn();
	
	public abstract boolean isUnnamedOut();

	public abstract boolean isUnnamedInOut();
	
	public static XMLDescriptor buildDescriptor()
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWAbstractProcedureArgument.class);

		InheritancePolicy ipolicy = descriptor.getInheritancePolicy();
		ipolicy.setClassIndicatorFieldName("@type");
		ipolicy.addClassIndicator(MWProcedureNamedInArgument.class, "procedure-named-argument");
		ipolicy.addClassIndicator(MWProcedureUnamedInArgument.class, "procedure-unamed-argument");
		ipolicy.addClassIndicator(MWProcedureNamedOutputArgument.class, "procedure-named-output-argument");
		ipolicy.addClassIndicator(MWProcedureUnamedOutputArgument.class, "procedure-unamed-output-argument");
		ipolicy.addClassIndicator(MWProcedureNamedInOutputArgument.class, "procedure-named-inoutput-argument");
		ipolicy.addClassIndicator(MWProcedureUnamedInOutputArgument.class, "procedure-unamed-inoutput-argument");

		XMLDirectMapping passTypeMapping = new XMLDirectMapping();
		passTypeMapping.setAttributeName("passType");
		passTypeMapping.setGetMethodName("getPassTypeForTopLink");
		passTypeMapping.setSetMethodName("setPassTypeForTopLink");
		passTypeMapping.setXPath("pass-type/text()");
		descriptor.addMapping(passTypeMapping);
		
		XMLDirectMapping valueMapping = new XMLDirectMapping();
		valueMapping.setAttributeName("argumentValue");
		valueMapping.setGetMethodName("getArgumentValueForTopLink");
		valueMapping.setSetMethodName("setArgumentValueForTopLink");
		valueMapping.setXPath("argument-value/text()");
		descriptor.addMapping(valueMapping);

		XMLDirectMapping argumentNameMapping = new XMLDirectMapping();
		argumentNameMapping.setAttributeName("argumentName");
		argumentNameMapping.setGetMethodName("getArgumentNameForTopLink");
		argumentNameMapping.setSetMethodName("setArgumentNameForTopLink");
		argumentNameMapping.setXPath("argument-name/text()");
		descriptor.addMapping(argumentNameMapping);

		XMLDirectMapping fieldNameMapping = new XMLDirectMapping();
		fieldNameMapping.setAttributeName("fieldName");
		fieldNameMapping.setGetMethodName("getFieldNameForTopLink");
		fieldNameMapping.setSetMethodName("setFieldNameForTopLink");
		fieldNameMapping.setXPath("field-name/text()");
		descriptor.addMapping(fieldNameMapping);
		
		descriptor.addDirectMapping("fieldSqlType", "getFieldSqlTypeNameForTopLink", "setFieldSqlTypeNameForTopLink", "field-sql-type/text()");
		descriptor.addDirectMapping("fieldSqlTypeName", "getFieldSubTypeNameForTopLink", "setFieldTypeSubNameForTopLink", "field-sql-type-name/text()");
		descriptor.addDirectMapping("fieldJavaClassName", "getFieldJavaClassNameForTopLink", "setFieldJavaClassNameForTopLink", "field-java-class-name/text()");
		descriptor.addDirectMapping("nestedTypeFieldName", "getNestedTypeFieldNameForTopLink", "setNestedTypeFieldNameForTopLink", "nested-type-field-name/text()");
		
		return descriptor;
	}
	
	public String getPassType() {
		return this.passType;
	}
	
	public void setPassType(String newValue) {
		String oldValue = this.passType;
		this.passType = newValue;
		firePropertyChanged(PASS_TYPE_PROPERTY, oldValue, newValue);
	}
	
	@SuppressWarnings("unused")
	private String getPassTypeForTopLink() {
		return this.passType;
	}
	
	@SuppressWarnings("unused")
	private void setPassTypeForTopLink(String newValue) {
		this.passType = newValue;
	}
	
	public String getArgumentValue() {
		return this.argumentValue;
	}
	
	//limited to values convertible by TopLink runtime
	public void setArgumentValue(String newValue) {
		Object oldValue = this.argumentValue;
		this.argumentValue = newValue;
		firePropertyChanged(ARGUMENT_VALUE_PROPERTY, oldValue, newValue);
	}
	
	@SuppressWarnings("unused")
	private String getArgumentValueForTopLink() {
		return this.argumentValue;
	}
	
	@SuppressWarnings("unused")
	private void setArgumentValueForTopLink(String newValue) {
		this.argumentValue = newValue;
	}

	/**
	 * Convert the string to an object of the specified class.
	 * If there are any problems, do nothing - the value will not be built.
	 */
	public Object buildValueFromString(String valueTypeName, String valueString) {
		try {
			Class<?> javaClass = ClassTools.classForTypeDeclaration(valueTypeName, 0);
		
			return this.getConversionManager().convertObject(valueString, javaClass);
		}
		catch (ClassNotFoundException ex)
		{
			// it's very unlikely this will happen since
			// we try to restrict the class to common classes (e.g. String, Date)
		}

		throw ConversionException.couldNotBeConverted(valueString, null);
	}
	
	private ConversionManager getConversionManager() {
		if (this.conversionManager == null) {
			this.conversionManager = new ConversionManager();
		}
		return this.conversionManager;
	}

	public String getArgumentName(){
		if (this.argumentName == null) {
			return "";
		} else {
			return this.argumentName;
		}
	}

	 public void setArgumentName(String argumentName){
		 String oldName = this.argumentName;
		 this.argumentName = argumentName;
		 firePropertyChanged(ARGUMENT_NAME_PROPERTY, oldName, argumentName);
	 }

	 @SuppressWarnings("unused")
	 private void setArgumentNameForTopLink(String argumentName){
		 this.argumentName = argumentName;
	 }

	 @SuppressWarnings("unused")
	 private String getArgumentNameForTopLink(){
		 return this.argumentName;
	 }

	 public String getFieldName(){
		 if (this.fieldName == null) {
			 return "";
		 } else {
			 return this.fieldName;
		 }
	 }

	 public void setFieldName(String fieldName){
		 String oldName = this.fieldName;
		 this.fieldName = fieldName;
		 this.firePropertyChanged(FIELD_NAME_PROPERTY,oldName, this.fieldName);
	 }

	 @SuppressWarnings("unused")
	 private void setFieldNameForTopLink(String fieldName){
		 this.fieldName = fieldName;
	 }

	 @SuppressWarnings("unused")
	 private String getFieldNameForTopLink(){
		 return this.fieldName;
	 }

	public int getFieldSqlTypeCode() {
		if (this.fieldSqlTypeName == null) {
			return -1;
		}
		return MWAbstractProcedureArgument.jdbcTypeCodeFor(this.fieldSqlTypeName);
	}
	
	public String getFieldSqlTypeName() {
		if (fieldSqlTypeName == null) {
			return "";
		}
		return fieldSqlTypeName;
	}

	public void setFieldSqlTypeName(String fieldSqlTypeName) {
		String old = this.fieldSqlTypeName;
		this.fieldSqlTypeName = fieldSqlTypeName;
		this.firePropertyChanged(FIELD_SQL_TYPE_PROPERTY, old, this.fieldSqlTypeName);
	}

	private String getFieldSqlTypeNameForTopLink() {
		return this.fieldSqlTypeName;
	}
	
	private void setFieldSqlTypeNameForTopLink(String fieldSqlType) {
		this.fieldSqlTypeName = fieldSqlType;
	}
	
	public String getFieldSubTypeName() {
		if (this.fieldSqlSubTypeName == null) {
			return "";
		} else {
			return fieldSqlSubTypeName;
		}
	}

	public void setFieldSubTypeName(String fieldTypeName) {
		String old = this.fieldSqlSubTypeName;
		this.fieldSqlSubTypeName = fieldTypeName;
		this.firePropertyChanged(FIELD_SQL_TYPE_NAME_PROPERTY, old, this.fieldSqlSubTypeName);
	}

	private String getFieldSubTypeNameForTopLink() {
		return this.fieldSqlSubTypeName;
	}
	
	private void setFieldTypeSubNameForTopLink(String newValue) {
		this.fieldSqlSubTypeName = newValue;
	}
	
	public String getFieldJavaClassName() {
		if (this.fieldJavaClassName == null) {
			return "";
		} else {
			return fieldJavaClassName;
		}
	}

	public void setFieldJavaClassName(String fieldJavaClassName) {
		String old = this.fieldJavaClassName;
		this.fieldJavaClassName = fieldJavaClassName;
		this.firePropertyChanged(FIELD_JAVA_CLASS_NAME_PROPERTY, old, this.fieldJavaClassName);
	}
	
	private String getFieldJavaClassNameForTopLink() {
		return this.fieldJavaClassName;
	}
	
	private void setFieldJavaClassNameForTopLink(String newValue) {
		this.fieldJavaClassName = newValue;
	}
	
	public String getNestedTypeFieldName() {
		if (this.nestedTypeFieldName == null) {
			return "";
		} else {
			return nestedTypeFieldName;
		}
	}

	public void setNestedTypeFieldName(String nestedTypeFieldName) {
		String old = this.nestedTypeFieldName;
		this.nestedTypeFieldName = nestedTypeFieldName;
		this.firePropertyChanged(NESTED_TYPE_FIELD_NAME_PROPERTY, old, this.nestedTypeFieldName);
	}

	private String getNestedTypeFieldNameForTopLink() {
		return this.nestedTypeFieldName;
	}
	
	private void setNestedTypeFieldNameForTopLink(String newValue) {
		this.nestedTypeFieldName = newValue;
	}
	
	public MWProcedure getProcedure() {
		return (MWProcedure)getParent();
	}
	
	public MWQuery getQuery() {
		return getProcedure().getQuery();
	}
	
	public ListIterator<String> getQueryParameterNames() {
		return CollectionTools.listIterator(getQuery().parameterNames());
	}
	
	public static int jdbcTypesSize() {
		return jdbcTypes().size();
	}
	
	public static Iterator<String> jdbcTypeNames() {
		return jdbcTypes().keySet().iterator();
	}
		
	public static int jdbcTypeCodeFor(String name) {
		return jdbcTypes().get(name).intValue();
	}
	
	public static String jdbcTypeNameFor(int value) {
		return jdbcTypeCodes().get(new Integer(value));
	}
	
	public static HashMap<Integer, String> jdbcTypeCodes() {
		if (jdbcTypesCodes == null) {
			jdbcTypesCodes = new HashMap<Integer, String>();
			jdbcTypesCodes.put(new Integer(2003), "ARRAY");
			jdbcTypesCodes.put(new Integer(-5), "BIGINT");
			jdbcTypesCodes.put(new Integer(-2), "BINARY");
			jdbcTypesCodes.put(new Integer(-7), "BIT");
			jdbcTypesCodes.put(new Integer(2004), "BLOB");
			jdbcTypesCodes.put(new Integer(16), "BOOLEAN");
			jdbcTypesCodes.put(new Integer(1), "CHAR");
			jdbcTypesCodes.put(new Integer(2005), "CLOB");
			jdbcTypesCodes.put(new Integer(70), "DATALINK");
			jdbcTypesCodes.put(new Integer(91), "DATE");
			jdbcTypesCodes.put(new Integer(3), "DECIMAL");
			jdbcTypesCodes.put(new Integer(2001), "DISTINCT");
			jdbcTypesCodes.put(new Integer(8), "DOUBLE");
			jdbcTypesCodes.put(new Integer(6), "FLOAT");
			jdbcTypesCodes.put(new Integer(4), "INTEGER");
			jdbcTypesCodes.put(new Integer(2000), "JAVA OBJECT");
			jdbcTypesCodes.put(new Integer(-4), "LONGVARBINARY");
			jdbcTypesCodes.put(new Integer(-1), "LONGVARCHAR");
			jdbcTypesCodes.put(new Integer(0), "NULL");
			jdbcTypesCodes.put(new Integer(2), "NUMERIC");
			jdbcTypesCodes.put(new Integer(1111), "OTHER");
			jdbcTypesCodes.put(new Integer(7), "REAL");
			jdbcTypesCodes.put(new Integer(2006), "REF");
			jdbcTypesCodes.put(new Integer(5), "SMALLINT");
			jdbcTypesCodes.put(new Integer(2002), "STRUCT");
			jdbcTypesCodes.put(new Integer(92), "TIME");
			jdbcTypesCodes.put(new Integer(93), "TIMESTAMP");
			jdbcTypesCodes.put(new Integer(-6), "TINYINT");
			jdbcTypesCodes.put(new Integer(-3), "VARBINARY");
			jdbcTypesCodes.put(new Integer(12), "VARCHAR");
		}
		return jdbcTypesCodes;
	}
	
	private static HashMap<String, Integer> jdbcTypes() {
		if (jdbcTypes == null) {
			jdbcTypes = new HashMap<String, Integer>();
			jdbcTypes.put("ARRAY", new Integer(2003));
			jdbcTypes.put("BIGINT", new Integer(-5));
			jdbcTypes.put("BINARY", new Integer(-2));
			jdbcTypes.put("BIT", new Integer(-7));
			jdbcTypes.put("BLOB", new Integer(2004));
			jdbcTypes.put("BOOLEAN", new Integer(16));
			jdbcTypes.put("CHAR", new Integer(1));
			jdbcTypes.put("CLOB", new Integer(2005));
			jdbcTypes.put("DATALINK", new Integer(70));
			jdbcTypes.put("DATE", new Integer(91));
			jdbcTypes.put("DECIMAL", new Integer(3));
			jdbcTypes.put("DISTINCT", new Integer(2001));
			jdbcTypes.put("DOUBLE", new Integer(8));
			jdbcTypes.put("FLOAT", new Integer(6));
			jdbcTypes.put("INTEGER", new Integer(4));
			jdbcTypes.put("JAVA OBJECT", new Integer(2000));
			jdbcTypes.put("LONGVARBINARY", new Integer(-4));
			jdbcTypes.put("LONGVARCHAR", new Integer(-1));
			jdbcTypes.put("NULL", new Integer(0));
			jdbcTypes.put("NUMERIC", new Integer(2));
			jdbcTypes.put("OTHER", new Integer(1111));
			jdbcTypes.put("REAL", new Integer(7));
			jdbcTypes.put("REF", new Integer(2006));
			jdbcTypes.put("SMALLINT", new Integer(5));
			jdbcTypes.put("STRUCT", new Integer(2002));
			jdbcTypes.put("TIME", new Integer(92));
			jdbcTypes.put("TIMESTAMP", new Integer(93));
			jdbcTypes.put("TINYINT", new Integer(-6));
			jdbcTypes.put("VARBINARY", new Integer(-3));
			jdbcTypes.put("VARCHAR", new Integer(12));
		}
		return jdbcTypes;
	}
	
	public static List<String> buildBasicTypesList() {
		List<String> list = new ArrayList<String>();
		list.add("java.lang.Boolean");
		list.add("java.lang.Byte");
		list.add("java.lang.Character");
		list.add("java.lang.Double");
		list.add("java.lang.Float");
		list.add("java.lang.Integer");
		list.add("java.lang.Long");
		list.add("java.lang.Short");
		list.add("java.lang.String");
		list.add("java.math.BigDecimal");
		list.add("java.math.BigInteger");
		list.add("java.sql.Date");
		list.add("java.sql.Time");
		list.add("java.sql.Timestamp");
		list.add("java.util.Calendar");
		list.add("java.util.Date");
		list.add("java.sql.Blob");
		list.add("java.sql.Clob");
		return list;
	}


}