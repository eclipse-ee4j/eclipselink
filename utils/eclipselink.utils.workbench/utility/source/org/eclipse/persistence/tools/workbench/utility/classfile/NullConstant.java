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
package org.eclipse.persistence.tools.workbench.utility.classfile;

import java.io.IOException;

import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;


/**
 * This class models a null class file constant, the first entry
 * in the constant pool.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class NullConstant extends Constant {

	/**
	 * Construct a "null" class file constant.
	 */
	NullConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}

	void initialize(ClassFileDataInputStream stream) throws IOException {
		// do nothing
	}

	public void displayStringOn(IndentingPrintWriter writer) {
		super.displayStringOn(writer);
		writer.println();
	}
	
	public String description() {
		return "null";
	}

	public Object value() {
		return null;
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

}
