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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;


/**
 * This class models a class file field.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class Field extends Member {
	private FieldType fieldDescriptor;		// lazy-initialized - so use the getter

	/** constants defined in "The Java Virtual Machine Specification" */
	public static final short ACC_ENUM = 0x4000;

	/**
	 * cleared bits:
	 *     0x8000 reserved for future use - ignore it
	 *               (although the Eclipse compiler sets it for some reason...)
	 *     0x4000 unrecognized by Modifier
	 *     0x2000 unrecognized by Modifier
	 *     0x1000 unrecognized by Modifier
	 *     0x0800 strictfp
	 *     0x0400 abstract
	 *     0x0200 interface
	 *     0x0100 native
	 *     x00020 synchronized
	 */
	public static final int VISIBLE_ACCESS_FLAGS_MASK = 0x00DF;


	/**
	 * Construct a class file field from the specified stream
	 * of byte codes.
	 */
	Field(ClassFileDataInputStream stream, FieldPool pool) throws IOException {
		super(stream, pool);
	}

	short visibleAccessFlagsMask() {
		return VISIBLE_ACCESS_FLAGS_MASK;
	}

	public void printDeclarationOn(PrintWriter writer) {
		this.printModifierOn(writer);
		this.getFieldDescriptor().printDeclarationOn(writer);
		writer.print(' ');
		writer.print(this.name());
		this.getAttributePool().printFieldInitializationClauseOn(writer);
	}

	/**
	 * Return the name that matches the name returned by
	 * java.lang.reflect.Field.getType().getName().
	 */
	public String javaTypeName() {
		return this.getFieldDescriptor().javaName();
	}

	public FieldPool getFieldPool() {
		return (FieldPool) this.getPool();
	}

	/**
	 * Return the constant value assigned to a static field.
	 */
	public Object constantValue() {
		return this.getAttributePool().fieldConstantValue();
	}

	/**
	 * Check a bit that cannot (yet?) be interpreted by the
	 * Modifier static methods. This bit indicates the field
	 * is "being used to hold an element of an enumerated type".
	 */
	public boolean isEnum() {
		return (this.getAccessFlags() & ACC_ENUM) != 0;
	}

	/**
	 * as opposed to synthetic
	 */
	public boolean isDeclaredField() {
		return ! this.isSynthetic();
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
		this.getFieldDescriptor().accept(visitor);
		super.accept(visitor);
	}

	public FieldType getFieldDescriptor() {
		if (this.fieldDescriptor == null) {
			this.fieldDescriptor = FieldType.createFieldType(this.descriptor());
		}
		return this.fieldDescriptor;
	}

}
