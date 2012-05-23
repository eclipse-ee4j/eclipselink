package mypackage;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import myotherpackage.MyInterface;
import myotherpackage.TypeInRepeatedPackage;
import myotherpackage.TypeSpecifiedByName;
import mypackage.mysubpackage.ClassInSubpackage;
import mythirdpackage.ClassInThirdPackage;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

/**
 * "No comment" - Richard M. Nixon
 */

public class MyClassWithCalculatedImports extends MySuperclass implements Serializable, MyInterface {

	public TypeSpecifiedByName a_attributeSpecifiedByName;
	public Vector b_attributeSpecifiedByType;
	public String c_attributeWithJavaLangType;
	protected AnotherTypeInTheSamePackage d_attributeWithTypeInSamePackage;
	protected TypeInRepeatedPackage e_attributeWithTypeInRepeatedPackage;
	protected Object f_attributeWithInitialValue = new String();
	private ClassInThirdPackage g_attributeWithTypeInThirdPackage;
	private ValueHolderInterface h_anAttributeWithIndirectionInitialValue = new ValueHolder();
	private mypackage.AmbiguousType i_attributeWithAmbiguousType;
	private myotherpackage.AmbiguousType j_attributeWithAmbiguousType;
	private int k_attributeWithPrimitiveType;

public MyClassWithCalculatedImports() {
}

public void a_methodWithNoArgument() {
}

public void b_methodWithArgumentSpecifiedByName(TypeSpecifiedByName argumentSpecifiedByName) {
}

public void c_methodWithArgumentSpecifiedByType(BigDecimal argumentSpecifiedByType) {
}

protected Vector d_methodWithReturnType() {
}

protected List e_methodWithMethodBody() {
	List myList = (List) c_methodWithReturnType();
	return myList;
}

protected MyClassWithCalculatedImports f_methodWithThisClassAsReturnType() {
}

protected ClassInSubpackage g_methodWithReturnTypeInSubpackage() {
}

private int h_methodWithAmbiguousArgumentType(myotherpackage.AmbiguousType argumentWithAmbiguousType) {
}

private Object i_methodWithMultipleArgumentTypes(AnotherTypeInTheSamePackage argument1, int[] argument2) {
}

private Byte[] j_methodWithArrayReturnType() {
}

private void k_methodWithThrownException() throws IOException {
}

private void l_methodWithArrayArgumentType(URL[] argumentWithArrayType) {
}

}