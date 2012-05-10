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
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.persistence.tools.workbench.utility.classfile.descriptor.FieldType;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassFileDataInputStream;


/**
 * There's not much difference between a "method ref" and an
 * "interface method ref".
 */
public abstract class AbstractMethodRefConstant extends MemberRefConstant {
	private FieldType returnDescriptor;		// lazy-initialized - so use the getter
	private FieldType[] parameterDescriptors;		// lazy-initialized - so use the getter

	/**
	 * Construct a class file constant from the specified stream
	 * of byte codes.
	 */
	AbstractMethodRefConstant(ConstantPool pool, byte tag, ClassFileDataInputStream stream) throws IOException {
		super(pool, tag, stream);
	}

	public boolean isConstructor() {
		return this.name().equals(Method.CONSTRUCTOR_NAME);
	}

	public boolean isStaticInitializationMethod() {
		return this.name().equals(Method.STATIC_INITIALIZER_NAME);
	}

	public void accept(Visitor visitor) {
		this.getReturnDescriptor().accept(visitor);
		FieldType[] ptds = this.getParameterDescriptors();
		int len = ptds.length;
		for (int i = 0; i < len; i++) {
			ptds[i].accept(visitor);
		}
	}

	public FieldType getReturnDescriptor() {
		if (this.returnDescriptor == null) {
			this.buildDescriptors();
		}
		return this.returnDescriptor;
	}

	public FieldType[] getParameterDescriptors() {
		if (this.parameterDescriptors == null) {
			this.buildDescriptors();
		}
		return this.parameterDescriptors;
	}

	public FieldType getParameterDescriptor(int index) {
		if (this.parameterDescriptors == null) {
			this.buildDescriptors();
		}
		return this.parameterDescriptors[index];
	}

	private void buildDescriptors() {
		Reader reader = new StringReader(this.descriptor());
		try {
			this.parameterDescriptors = Method.buildParameterDescriptors(reader);
			this.returnDescriptor = FieldType.createFieldType(reader);
		} catch (IOException ex) {
			// this is unlikely when reading a String
			throw new RuntimeException(ex);
		}
	}

}
