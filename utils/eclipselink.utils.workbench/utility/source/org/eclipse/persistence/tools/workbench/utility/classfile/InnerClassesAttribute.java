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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models an inner classes (inner class pool) attribute:
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 number_of_classes;
 *     {
 *         u2 inner_class_info_index;
 *         u2 outer_class_info_index;
 *         u2 inner_name_index;
 *         u2 inner_class_access_flags;
 *     }[number_of_classes] classes;
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 * 
 * Note the types of classes:
 *     top-level
 *     nested
 *         member (inner or static)
 *         local	(inner)
 *         anonymous (inner)
 */
public class InnerClassesAttribute extends Attribute {
	private short count;
	private InnerClass[] innerClasses;

	/**
	 * Construct an inner classes attribute from the specified stream
	 * of byte codes.
	 */
	InnerClassesAttribute(ClassFileDataInputStream stream, short nameIndex, AttributePool pool) throws IOException {
		super(stream, nameIndex, pool);
	}

	void initializeInfo(ClassFileDataInputStream stream) throws IOException {
		this.count = stream.readU2();
		short cnt = this.count;
		this.innerClasses = new InnerClass[cnt];
		InnerClass[] classes = this.innerClasses;
		for (short i = 0; i < cnt; i++) {
			classes[i] = new InnerClass(stream, this);
		}
	}

	void displayInfoStringOn(IndentingPrintWriter writer) {
		short cnt = this.count;
		InnerClass[] classes = this.innerClasses;
		for (short i = 0; i < cnt; i++) {
			writer.print(i);
			writer.print(": ");
			classes[i].displayStringOn(writer);
		}
	}

	public String nestedClassName(short index) {
		return this.innerClasses[index].innerClassInfoName();
	}

	public InnerClass innerClassNamed(String className) {
		short cnt = this.count;
		InnerClass[] classes = this.innerClasses;
		for (short i = 0; i < cnt; i++) {
			InnerClass innerClass = classes[i];
			if (innerClass.isNamed(className)) {
				return innerClass;
			}
		}
		return null;
	}

	private InnerClass innerClassNamed(short classNameIndex) {
		short cnt = this.count;
		InnerClass[] classes = this.innerClasses;
		for (short i = 0; i < cnt; i++) {
			InnerClass innerClass = classes[i];
			if (innerClass.isNamed(classNameIndex)) {
				return innerClass;
			}
		}
		return null;
	}

	/**
	 * return the inner class attribute for the class file's main class;
	 * return null if the main class is "top-level" class
	 */
	private InnerClass thisInnerClassAttribute() {
//		return this.innerClassNamed(this.classFile().className());
		return this.innerClassNamed(this.classFile().getDeclaration().getThisClassIndex());
	}

	public boolean isTopLevelClass() {
		return ! this.isNestedClass();
	}

	/**
	 * if there is an inner class attribute for the class file's main class,
	 * it is a "nested" class (as opposed to a "top-level" class);
	 * a "nested" class is either a "member" class or a "local" class
	 * or an "anonymous" class
	 */
	public boolean isNestedClass() {
		return this.thisInnerClassAttribute() != null;
	}

	/**
	 * only "member" classes have "declaring" classes
	 * ("local" and "anonymous" classes do not)
	 */
	public boolean isMemberClass() {
		InnerClass innerClass = this.thisInnerClassAttribute();
		return (innerClass != null) &&
				(innerClass.getOuterClassInfoIndex() != 0);
	}

	/**
	 * "local" classes have no "declaring" classes, but they do have names
	 * ("anonymous" classes do not)
	 */
	public boolean isLocalClass() {
		InnerClass innerClass = this.thisInnerClassAttribute();
		return (innerClass != null) &&
				(innerClass.getOuterClassInfoIndex() == 0) &&
				(innerClass.getInnerClassNameIndex() != 0);
	}

	/**
	 * "anonymous" classes have neither "declaring" classes nor names
	 */
	public boolean isAnonymousClass() {
		InnerClass innerClass = this.thisInnerClassAttribute();
		return (innerClass != null) &&
				(innerClass.getOuterClassInfoIndex() == 0) &&
				(innerClass.getInnerClassNameIndex() == 0);
	}

	/**
	 * only "member" classes have a declaring class
	 * ("top-level", "local", and "anonymous" classes do not)
	 */
	public String declaringClassName() {
		InnerClass innerClass = this.thisInnerClassAttribute();
		return (innerClass == null) ? null : innerClass.declaringClassName();
	}

	/**
	 * only "member" and "local" classes have names
	 * ("anonymous" classes do not)
	 */
	public String nestedClassName() {
		InnerClass innerClass = this.thisInnerClassAttribute();
		return (innerClass == null) ? null : innerClass.innerClassName();
	}

	public short nestedClassAccessFlags() {
		InnerClass innerClass = this.thisInnerClassAttribute();
		return (innerClass == null) ? 0 : innerClass.getInnerClassAccessFlags();
	}

	/**
	 * this will include the compiler-generated names for the
	 * "local" and "anonymous" classes
	 */
	public String[] nestedClassNames() {
		short cnt = this.count;
		if (cnt == 0) {
			return EMPTY_STRING_ARRAY;
		}
		String[] nestedClassNames = new String[cnt];
		for (short i = 0; i < cnt; i++) {
			nestedClassNames[i] = this.nestedClassName(i);
		}
		return nestedClassNames;
	}

	/**
	 * return the subset of "nested" classes that are "member" classes
	 */
	public String[] declaredMemberClassNames() {
		short cnt = this.count;
		if (cnt == 0) {
			return EMPTY_STRING_ARRAY;
		}
		InnerClass[] classes = this.innerClasses;
		List declaredMemberClassNames = new ArrayList(cnt);
		for (short i = 0; i < cnt; i++) {
			classes[i].addDeclaredMemberClassTo(declaredMemberClassNames);
		}
		return (declaredMemberClassNames.size() == 0) ?
			EMPTY_STRING_ARRAY
		:
			(String[]) declaredMemberClassNames.toArray(new String[declaredMemberClassNames.size()]);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		short cnt = this.count;
		InnerClass[] classes = this.innerClasses;
		for (short i = 0; i < cnt; i++) {
			classes[i].accept(visitor);
		}
	}

	public short getCount() {
		return this.count;
	}

	public InnerClass[] getInnerClasses() {
		return this.innerClasses;
	}

	public InnerClass getInnerClass(short index) {
		return this.innerClasses[index];
	}

	void toString(StringBuffer sb) {
		sb.append(this.count);
		sb.append(" inner class(es)");
	}

}
