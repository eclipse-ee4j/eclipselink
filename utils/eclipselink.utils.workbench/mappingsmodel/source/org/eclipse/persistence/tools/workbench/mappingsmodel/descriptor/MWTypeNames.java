package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

public interface MWTypeNames {
	// primitive types
	public final static String P_BOOLEAN_TYPE_NAME		= "boolean";
	public final static String P_BYTE_TYPE_NAME			= "byte";
	public final static String P_CHAR_TYPE_NAME			= "char";
	public final static String P_DOUBLE_TYPE_NAME		= "double";
	public final static String P_FLOAT_TYPE_NAME		= "float";
	public final static String P_INT_TYPE_NAME			= "int";
	public final static String P_LONG_TYPE_NAME			= "long";
	public final static String P_SHORT_TYPE_NAME		= "short";
	public final static String P_VOID_TYPE_NAME 		= "void";
	public final static String[] PRIMITIVE_TYPES 		= {P_BOOLEAN_TYPE_NAME, 
		 												   P_BYTE_TYPE_NAME,
														   P_CHAR_TYPE_NAME,
														   P_DOUBLE_TYPE_NAME,
														   P_FLOAT_TYPE_NAME,
														   P_INT_TYPE_NAME,
														   P_LONG_TYPE_NAME,
														   P_SHORT_TYPE_NAME,
														   P_VOID_TYPE_NAME};
		 
	// basic types
	public final static String OBJECT_TYPE_NAME 		= "java.lang.Object";
	public final static String STRING_TYPE_NAME 		= "java.lang.String";
	public static final String SERIALIZABLE_TYPE_NAME 	= "java.io.Serializable";
	
	// "collection" types
	public final static String COLLECTION_TYPE_NAME 	= "java.util.Collection";
	public final static String MAP_TYPE_NAME 			= "java.util.Map";
	public final static String SET_TYPE_NAME 			= "java.util.Set";
	
	// toplink types
	public final static String INDIRECT_CONTAINER_TYPE_NAME 	= "org.eclipse.persistence.indirection.IndirectContainer";
	public final static String VALUE_HOLDER_INTERFACE_TYPE_NAME = "org.eclipse.persistence.indirection.ValueHolderInterface";
	public final static String VALUE_HOLDER_TYPE_NAME			= "org.eclipse.persistence.indirection.ValueHolder";

}
