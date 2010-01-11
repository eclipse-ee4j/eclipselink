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


/**
 * This class models a class file interface method ref constant.
 * 
 * See "The Java Virtual Machine Specification" Chapter 4.
 */
public class InterfaceMethodRefConstant extends AbstractMethodRefConstant {

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	InterfaceMethodRefConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}
	
	public String description() {
		return "interface method ref";
	}

	public void accept(Visitor visitor) {
		super.accept(visitor);
		visitor.visit(this);
	}

}
