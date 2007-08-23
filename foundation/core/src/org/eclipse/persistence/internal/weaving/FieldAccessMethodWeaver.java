/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.weaving;

import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;

/**
 * Weaves methods in classes that use JPA Field access to replace access to the mapped fields
 * with a call to a method we are adding through weaving.
 */
public class FieldAccessMethodWeaver extends MethodWeaver {

    public FieldAccessMethodWeaver(ClassWeaver tcw, String methodName, String methodEscriptor,
            CodeVisitor cv) {
        super(tcw, methodName, methodEscriptor, cv);
    }
    
    /**
     *  Change GETFIELD and PUTFIELD for fields that use attribute access to make use of new convenience methods.
     *  
     *  A GETFIELD for an attribute named 'variableName' will be replaced by a call to:
     *  
     *  _persistence_getvariableName()
     *  
     *  A PUTFIELD for an attribute named 'variableName' will be replaced by a call to:
     *  
     *  _persistence_setvariableName(variableName)
     */
    public void weaveAttributesIfRequired(int opcode, String owner, String name, String desc){
        if (!tcw.classDetails.usesAttributeAccess()){
            super.visitFieldInsn(opcode, owner, name, desc);
            return;
        }
        AttributeDetails attributeDetails = tcw.classDetails.getAttributeDetailsFromClassOrSuperClass(name);  
        if ((attributeDetails != null) && (opcode == GETFIELD)){
            if (attributeDetails.weaveValueHolders() || tcw.classDetails.shouldWeaveFetchGroups()) {
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_get" + name, "()" + attributeDetails.getReferenceClassType().getDescriptor());
            } else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }  else if ((attributeDetails != null) && (opcode == PUTFIELD)) {
            if ((attributeDetails.weaveValueHolders()) || (tcw.classDetails.shouldWeaveChangeTracking()) || (tcw.classDetails.shouldWeaveFetchGroups())) {
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_set" + name, "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
            } else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }  else {
            super.visitFieldInsn(opcode, owner, name, desc);
        }    
    }

    public void visitFieldInsn (final int opcode, final String owner, final String name, final String desc){
        methodStarted = true;
        weaveAttributesIfRequired(opcode, owner, name, desc);
    }
    
}
