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
package org.eclipse.persistence.tools.workbench.mappingsmodel.handles;

import java.util.StringTokenizer;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * MWMethodHandle is used to isolate the painful bits of code
 * necessary to correctly handle references to MWMethods.
 * Since a MWMethod is nested within the XML file
 * for a MWClass, we need to store a reference to a particular
 * method as a pair of instance variables:
 *   - the name of the declaring MWClass
 *   - the signature of the method
 * 
 * This causes no end of pain when dealing with TopLink, property
 * change listeners, backward-compatibility, etc.
 */
public final class MWMethodHandle extends MWHandle {

	/**
	 * This is the actual method.
	 * It is built from the declaring type name and method signature, below.
	 */
	private volatile MWMethod method;

	/**
	 * The declaring type name and method signature are transient. They
	 * are used only to hold their values until postProjectBuild()
	 * is called and we can resolve the actual method.
	 * We do not keep these in synch with the method itself because
	 * we cannot know when the method has been renamed etc.
	 */
	private volatile String methodDeclaringTypeName;
	private volatile String methodSignature;

	/**
	 * default constructor - for TopLink use only
	 */
	private MWMethodHandle() {
		super();
	}

	public MWMethodHandle(MWModel parent, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
	}

	public MWMethodHandle(MWModel parent, MWMethod method, NodeReferenceScrubber scrubber) {
		super(parent, scrubber);
		this.method = method;
	}


	// ********** instance methods **********

	public MWMethod getMethod() {
		return this.method;
	}

	public void setMethod(MWMethod method) {
		this.method = method;
	}

	protected Node node() {
		return getMethod();
	}
	
	public MWMethodHandle setScrubber(NodeReferenceScrubber scrubber) {
		this.setScrubberInternal(scrubber);
		return this;
	}

	public void resolveMethodHandles() {
		super.resolveMethodHandles();
		if (this.methodDeclaringTypeName != null && this.methodSignature != null) {
			// the type will never be null - the repository will auto-generate one if necessary
			this.method = this.typeNamed(this.methodDeclaringTypeName).methodWithSignature(this.methodSignature);
		}
		// Ensure methodDeclaringTypeName and the methodSignature are not
		// used by setting them to null....
		// If the XML is corrupt and only one of these attributes is populated,
		// this will cause the populated attribute to be cleared out if the
		// objects are rewritten.
		this.methodDeclaringTypeName = null;
		this.methodSignature = null;
	}
    
    private String removeArrayTypesFromSignature(String methodSignature) {
        if (methodSignature == null) {
            return null;
        }
        StringBuffer buffer = new StringBuffer(methodSignature.substring(0, methodSignature.indexOf('(') +1));
        String parameters = methodSignature.substring(methodSignature.indexOf('(') + 1, methodSignature.length() -1);
        StringTokenizer tokenizer = new StringTokenizer(parameters, ",");
        while (tokenizer.hasMoreTokens()) {
            String parameterType = tokenizer.nextToken();
            buffer.append(ClassTools.elementTypeNameForClassNamed(parameterType));
            int arrayDepth = ClassTools.arrayDepthForClassNamed(parameterType);
            for(int i = 0; i < arrayDepth; i++) {
                buffer.append("[]");
            }

            if (tokenizer.hasMoreTokens()) {
                buffer.append(",");
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

	/**
	 * Override to delegate comparison to the method itself.
	 * If the handles being compared are in a collection that is being sorted,
	 * NEITHER method should be null.
	 */
	public int compareTo(Object o) {
		return this.method.compareTo(((MWMethodHandle) o).method);
	}

	public void toString(StringBuffer sb) {
		if (this.method == null) {
			sb.append("null");
		} else {
			this.method.toString(sb);
		}
	}


	// ********** TopLink methods **********

	public static XMLDescriptor buildDescriptor(){
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWMethodHandle.class);

		descriptor.addDirectMapping("methodDeclaringTypeName", "getMethodDeclaringTypeNameForTopLink", "setMethodDeclaringTypeNameForTopLink", "method-declaring-type-name/text()");
		descriptor.addDirectMapping("methodSignature", "getMethodSignatureForTopLink", "setMethodSignatureForTopLink", "method-signature/text()");

		return descriptor;
	}

	public static XMLDescriptor legacy60BuildDescriptor(){
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWMethodHandle.class);

		descriptor.addDirectMapping("methodDeclaringTypeName", "getMethodDeclaringTypeNameForTopLink", "setMethodDeclaringTypeNameForTopLink", "method-declaring-type-name/text()");
		descriptor.addDirectMapping("methodSignature", "getMethodSignatureForTopLink", "legacySetMethodSignatureForTopLink", "method-signature/text()");

		return descriptor;
	}
	
	private String getMethodDeclaringTypeNameForTopLink() {
		return (this.method == null) ? null : this.method.getDeclaringType().getName();
	}

	private void setMethodDeclaringTypeNameForTopLink(String methodDeclaringTypeName) {
		this.methodDeclaringTypeName = methodDeclaringTypeName;
	}

	private String getMethodSignatureForTopLink() {
		return (this.method == null) ? null : method.signature();
	}

	private void setMethodSignatureForTopLink(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    private void legacySetMethodSignatureForTopLink(String legacyMethodSignature) {
		this.methodSignature = removeArrayTypesFromSignature(MWModel.legacyReplaceToplinkDepracatedClassReferencesFromSignature(legacyMethodSignature));
    }
    
}
