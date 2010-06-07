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
 package org.eclipse.persistence.internal.jpa.weaving;

//ASM imports
import org.eclipse.persistence.internal.libraries.asm.*;

/**
 * Processes all the methods of a class to weave in persistence code such as,
 * lazy value holder, change tracking and fetch groups.
 * 
 * For FIELD access, changes references to GETFIELD and PUTFIELD to call weaved get/set methods.
 * 
 * For Property access, modifies the getters and setters.
 * 
 */

public class MethodWeaver extends CodeAdapter implements Constants {

    protected ClassWeaver tcw;
    protected String methodName;
    protected String methodDescriptor = null;
    
    /** Determines if we are at the first line of a method. */
    protected boolean methodStarted = false;
        
    public MethodWeaver(ClassWeaver tcw, String methodName, String methodDescriptor, CodeVisitor cv) {        
        super(cv);
        this.tcw = tcw;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    public void visitInsn (final int opcode) {
        weaveBeginningOfMethodIfRequired();
        if (opcode == RETURN) {
            weaveEndOfMethodIfRequired();
        }
        super.visitInsn(opcode);
    }

    public void visitIntInsn (final int opcode, final int operand) {
        weaveBeginningOfMethodIfRequired();
        cv.visitIntInsn(opcode, operand);
    }

    public void visitVarInsn (final int opcode, final int var) {
        weaveBeginningOfMethodIfRequired();
        cv.visitVarInsn(opcode, var);
    }

    public void visitTypeInsn (final int opcode, final String desc) {
        weaveBeginningOfMethodIfRequired();
        cv.visitTypeInsn(opcode, desc);
    }

    public void visitFieldInsn (final int opcode, final String owner, final String name, final String desc) {
        weaveBeginningOfMethodIfRequired();
        weaveAttributesIfRequired(opcode, owner, name, desc);
    }

    public void visitMethodInsn (final int opcode, final String owner, final String name, final String desc) {
        weaveBeginningOfMethodIfRequired();
        String descClassName = "";
        if (desc.length() > 3){
            descClassName = desc.substring(3, desc.length()-1);
        }
        // Need to find super.clone and add _persistence_post_clone(clone).
        if (this.tcw.classDetails.shouldWeaveInternal() && name.equals("clone") && 
                /* the following will return true if we are calling a method stored on our direct superclass or one of its superclasses
                 * that is involved in our metadata hierarchy and if there are no classes farther up the hierarchy that implement a clone method
                 * The goal is to call _persistence_post_clone() at the highest level in the hierarchy possible
                 * For completeness, we check to ensure the return type is in that same hierarchy */
                this.tcw.classDetails.isInSuperclassHierarchy(owner) && this.tcw.classDetails.isInMetadataHierarchy(descClassName) && 
                (this.tcw.classDetails.getNameOfSuperclassImplementingCloneMethod() == null)) {
            super.visitMethodInsn(opcode, owner, name, desc);
            super.visitTypeInsn(CHECKCAST, this.tcw.classDetails.getClassName());
            super.visitMethodInsn(INVOKEVIRTUAL, this.tcw.classDetails.getClassName(), "_persistence_post_clone", "()Ljava/lang/Object;");
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    public void visitJumpInsn (final int opcode, final Label label) {
        weaveBeginningOfMethodIfRequired();
        cv.visitJumpInsn(opcode, label);
    }

    public void visitLabel (final Label label) {
        weaveBeginningOfMethodIfRequired();
        cv.visitLabel(label);
    }

    public void visitLdcInsn (final Object cst) {
        weaveBeginningOfMethodIfRequired();
        cv.visitLdcInsn(cst);
    }

    public void visitIincInsn (final int var, final int increment) {
        weaveBeginningOfMethodIfRequired();
        cv.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn (final int min, final int max, final Label dflt, final Label labels[]) {
        weaveBeginningOfMethodIfRequired();
        cv.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn (final Label dflt, final int keys[], final Label labels[]) {
        weaveBeginningOfMethodIfRequired();
        cv.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn (final String desc, final int dims) {
        weaveBeginningOfMethodIfRequired();
        cv.visitMultiANewArrayInsn(desc, dims);
    }

    public void visitTryCatchBlock (final Label start, final Label end,final Label handler, final String type) {
        weaveBeginningOfMethodIfRequired();
        cv.visitTryCatchBlock(start, end, handler, type);
    }

    public void visitMaxs (final int maxStack, final int maxLocals) {
        weaveBeginningOfMethodIfRequired();
        cv.visitMaxs(0, 0);
    }

    public void visitLocalVariable (final String name, final String desc, final Label start, final Label end, final int index) {
        weaveBeginningOfMethodIfRequired();
        cv.visitLocalVariable(name, desc, start, end, index);
    }

    public void visitLineNumber (final int line, final Label start) {
        weaveBeginningOfMethodIfRequired();
        cv.visitLineNumber(line, start);
    }

    public void visitAttribute (final Attribute attr) {
        weaveBeginningOfMethodIfRequired();
        cv.visitAttribute(attr);
    }

    /**
     * Change GETFIELD and PUTFIELD for fields that use attribute access to make use of new convenience methods.
     *  
     * A GETFIELD for an attribute named 'variableName' will be replaced by a call to:
     *  
     * _persistence_get_variableName()
     *  
     * A PUTFIELD for an attribute named 'variableName' will be replaced by a call to:
     *  
     * _persistence_set_variableName(variableName)
     */
    public void weaveAttributesIfRequired(int opcode, String owner, String name, String desc) {
        AttributeDetails attributeDetails = tcw.classDetails.getAttributeDetailsFromClassOrSuperClass(name);  
        if ((attributeDetails == null) || (!attributeDetails.hasField()) || (!this.tcw.classDetails.isInMetadataHierarchy(owner))) {
            super.visitFieldInsn(opcode, owner, name, desc);
            return;
        }
        if (opcode == GETFIELD) {
            if (attributeDetails.weaveValueHolders() || tcw.classDetails.shouldWeaveFetchGroups()) {
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), ClassWeaver.PERSISTENCE_GET + name, "()" + attributeDetails.getReferenceClassType().getDescriptor());
            } else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        } else if (opcode == PUTFIELD) {
            if ((attributeDetails.weaveValueHolders()) || (tcw.classDetails.shouldWeaveChangeTracking()) || (tcw.classDetails.shouldWeaveFetchGroups())) {
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), ClassWeaver.PERSISTENCE_SET + name, "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
            } else {
                super.visitFieldInsn(opcode, owner, name, desc);
            }
        }  else {
            super.visitFieldInsn(opcode, owner, name, desc);
        }    
    }
    
    /**
     * Makes modifications to the beginning of a method.
     * 
     * 1. Modifies getter method for attributes using property access
     * 
     * In a getter method for 'attributeName', the following lines are added at the beginning of the method
     *  
     *  _persistence_checkFetched("attributeName");
     *  _persistence_initialize_attributeName_vh();
     *  if (!_persistence_attributeName_vh.isInstantiated()) {
     *      PropertyChangeListener temp_persistence_listener = _persistence_listener;
     *      _persistence_listener = null;
     *      setAttributeName((AttributeType)_persistence_attributeName_vh.getValue());
     *      _persistence_listener = temp_persistence_listener;
     *  }
     *  
     *  2. Modifies setter methods to store old value of attribute
     *  If weaving for fetch groups:
     *  
     *  // if weaving for change tracking:
     *  if(_persistence_listener != null)
     *      // for Objects
     *      AttributeType oldAttribute = getAttribute()
     *      // for primitives
     *      AttributeWrapperType oldAttribute = new AttributeWrapperType(getAttribute());
     *          e.g. Double oldAttribute = new Double(getAttribute());
     *  else
     *      _persistence_checkFetchedForSet("attributeName");
     *  _persistence_propertyChange("attributeName", oldAttribute, argument);
     *  
     *  otherwise (not weaving for fetch groups):
     *  
     *      // for Objects
     *      AttributeType oldAttribute = getAttribute()
     *      // for primitives
     *      AttributeWrapperType oldAttribute = new AttributeWrapperType(getAttribute());
     *          e.g. Double oldAttribute = new Double(getAttribute());
     *  _persistence_propertyChange("attributeName", oldAttribute, argument);
     *  
     *  // if not weaving for change tracking, but for fetch groups only:
     *  _persistence_checkFetchedForSet("attributeName");
     */
    public void weaveBeginningOfMethodIfRequired() {
        if (this.methodStarted){
            return;
        }
        // Must set immediately, as weaving can trigger this method.
        this.methodStarted = true;
        AttributeDetails attributeDetails = tcw.classDetails.getGetterMethodToAttributeDetails().get(methodName);
        boolean isGetMethod = (attributeDetails != null) && this.methodDescriptor.startsWith("()");
        if (isGetMethod && !attributeDetails.hasField()) {
            if (tcw.classDetails.shouldWeaveFetchGroups()) {
                cv.visitVarInsn(ALOAD, 0);
                cv.visitLdcInsn(attributeDetails.getAttributeName());
                // _persistence_checkFetched("attributeName");
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_checkFetched", "(Ljava/lang/String;)V");                
            }
            if (attributeDetails.weaveValueHolders()) {
                // _persistence_initialize_attributeName_vh();
                cv.visitVarInsn(ALOAD, 0);
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_initialize_" + attributeDetails.getAttributeName() + "_vh", "()V");
                
                // if (!_toplink_attributeName_vh.isInstantiated()) {
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "isInstantiated", "()Z");
                Label l0 = new Label();
                cv.visitJumpInsn(IFNE, l0);
    
                // Need to disable change tracking when the set method is called to avoid thinking the attribute changed.
                if (tcw.classDetails.shouldWeaveChangeTracking()) {
                    // PropertyChangeListener temp_persistence_listener = _persistence_listener;
                    cv.visitVarInsn(ALOAD, 0);
                    cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_persistence_listener", ClassWeaver.PCL_SIGNATURE);
                    cv.visitVarInsn(ASTORE, 4);
                    // _persistence_listener = null;
                    cv.visitVarInsn(ALOAD, 0);
                    cv.visitInsn(ACONST_NULL);
                    cv.visitFieldInsn(PUTFIELD, tcw.classDetails.getClassName(), "_persistence_listener", ClassWeaver.PCL_SIGNATURE);
                }
                // setAttributeName((AttributeType)_toplink_attributeName_vh.getValue());
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "getValue", "()Ljava/lang/Object;");
                cv.visitTypeInsn(CHECKCAST, attributeDetails.getReferenceClassName().replace('.','/'));
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), attributeDetails.getSetterMethodName(), "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
                
                if (tcw.classDetails.shouldWeaveChangeTracking()) {
                    // _persistence_listener = temp_persistence_listener;
                    cv.visitVarInsn(ALOAD, 0);
                    cv.visitVarInsn(ALOAD, 4);
                    cv.visitFieldInsn(PUTFIELD, tcw.classDetails.getClassName(), "_persistence_listener", ClassWeaver.PCL_SIGNATURE);
                }
                // }
                cv.visitLabel(l0);
            }        
        } else {
            attributeDetails = tcw.classDetails.getSetterMethodToAttributeDetails().get(methodName);
            boolean isSetMethod = (attributeDetails != null) && this.methodDescriptor.equals(attributeDetails.getSetterMethodSignature());
            if (isSetMethod  && !attributeDetails.hasField()) {
                if(tcw.classDetails.shouldWeaveChangeTracking()) {
                    if(tcw.classDetails.shouldWeaveFetchGroups()) {
                        // if this is a primitive, get the wrapper class
                        String wrapper = ClassWeaver.wrapperFor(attributeDetails.getReferenceClassType().getSort());
                        
                        cv.visitInsn(ACONST_NULL);
                        if (wrapper != null){
                            cv.visitVarInsn(ASTORE, 3);
                        } else {
                            cv.visitVarInsn(ASTORE, 2);
                        }
        
                        cv.visitVarInsn(ALOAD, 0);
                        cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_persistence_listener", "Ljava/beans/PropertyChangeListener;");
                        Label l0 = new Label();
                        cv.visitJumpInsn(IFNULL, l0);
        
                        /**
                         * The code below constructs the following code
                         * 
                         * AttributeType oldAttribute = getAttribute() // for Objects
                         * 
                         * AttributeWrapperType oldAttribute = new AttributeWrapperType(getAttribute()); // for primitives
                         */                
                        // 1st part of invoking constructor for primitives to wrap them
                        if (wrapper != null) {
                            cv.visitTypeInsn(NEW, wrapper);
                            cv.visitInsn(DUP);
                        }
                        
                        // Call the getter
                        // getAttribute()
                        cv.visitVarInsn(ALOAD, 0);
                        cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), attributeDetails.getGetterMethodName(), "()" + attributeDetails.getReferenceClassType().getDescriptor());               
                        if (wrapper != null){
                            // 2nd part of using constructor.
                            cv.visitMethodInsn(INVOKESPECIAL, wrapper, "<init>", "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
                            cv.visitVarInsn(ASTORE, 3);
                        } else {
                            // store the result
                            cv.visitVarInsn(ASTORE, 2);
                        }
                        
                        Label l1 = new Label();
                        cv.visitJumpInsn(GOTO, l1);
                        cv.visitLabel(l0);
                        cv.visitVarInsn(ALOAD, 0);
                        
                        cv.visitLdcInsn(attributeDetails.getAttributeName());
                        cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_checkFetchedForSet", "(Ljava/lang/String;)V");
                        cv.visitLabel(l1);
                        
                        cv.visitVarInsn(ALOAD, 0);
                        cv.visitLdcInsn(attributeDetails.getAttributeName());
                        
                        if (wrapper != null) {
                            cv.visitVarInsn(ALOAD, 3);
                            cv.visitTypeInsn(NEW, wrapper);
                            cv.visitInsn(DUP);
                        } else {
                            cv.visitVarInsn(ALOAD, 2);
                        }
                        // get an appropriate load opcode for the type
                        int opcode = attributeDetails.getReferenceClassType().getOpcode(Constants.ILOAD);
                        cv.visitVarInsn(opcode, 1);
                        if (wrapper != null){
                            cv.visitMethodInsn(INVOKESPECIAL, wrapper, "<init>", "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
                        }
                        cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_propertyChange", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V");
                    } else {
                        // tcw.classDetails.shouldWeaveFetchGroups()
                        /**
                         * The code below constructs the following code
                         * 
                         * AttributeType oldAttribute = getAttribute() // for Objects
                         * 
                         * AttributeWrapperType oldAttribute = new AttributeWrapperType(getAttribute()); // for primitives
                         */                
                        // if this is a primitive, get the wrapper class
                        String wrapper = ClassWeaver.wrapperFor(attributeDetails.getReferenceClassType().getSort());
                        
                        // 1st part of invoking constructor for primitives to wrap them
                        if (wrapper != null) {
                            cv.visitTypeInsn(NEW, wrapper);
                            cv.visitInsn(DUP);
                        }
                        
                        // Call the getter
                        // getAttribute()
                        cv.visitVarInsn(ALOAD, 0);
                        cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), attributeDetails.getGetterMethodName(), "()" + attributeDetails.getReferenceClassType().getDescriptor());               
                        if (wrapper != null){
                            // 2nd part of using constructor.
                            cv.visitMethodInsn(INVOKESPECIAL, wrapper, "<init>", "(" + attributeDetails.getReferenceClassType().getDescriptor() + ")V");
                            cv.visitVarInsn(ASTORE, 3);
                        } else {
                            // store the result
                            cv.visitVarInsn(ASTORE, 2);
                        }

                        // makes use of the value stored in weaveBeginningOfMethodIfRequired to call property change method
                        // _persistence_propertyChange("attributeName", oldAttribute, argument);
                        cv.visitVarInsn(ALOAD, 0);
                        cv.visitLdcInsn(attributeDetails.getAttributeName());
                        cv.visitVarInsn(ALOAD, 2);
                        cv.visitVarInsn(ALOAD, 1);
                        cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_propertyChange", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V");
                    }
                } else {
                    // !tcw.classDetails.shouldWeaveChangeTracking()
                    if(tcw.classDetails.shouldWeaveFetchGroups()) {
                        cv.visitVarInsn(ALOAD, 0);
                        cv.visitLdcInsn(attributeDetails.getAttributeName());
                        // _persistence_checkFetchedForSet("variableName");
                        cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_checkFetchedForSet", "(Ljava/lang/String;)V");
                    }

                }
            }
        }
    }
    
    /**
     * Modifies methods just before the return.
     * 
     * In a setter method for a LAZY mapping, for 'attributeName', the following lines are added at the end of the method.
     * 
     *  _persistence_initialize_attributeName_vh();
     *  _persistence_attributeName_vh.setValue(argument);
     *  _persistence_attributeName_vh.setIsCoordinatedWithProperty(true);
     * 
     * In a setter method for a non-LAZY mapping, the followings lines are added if change tracking is activated:
     * 
     *  _persistence_propertyChange("attributeName", oldAttribute, argument); 
     *  
     *  Note: This code will wrap primitives by adding a call to the primitive constructor.
     */
    public void weaveEndOfMethodIfRequired() {
        AttributeDetails attributeDetails = tcw.classDetails.getSetterMethodToAttributeDetails().get(methodName);
        boolean isSetMethod = (attributeDetails != null) && this.methodDescriptor.equals(attributeDetails.getSetterMethodSignature());
        if (isSetMethod  && !attributeDetails.hasField()) {
            if (attributeDetails.weaveValueHolders()) {
                cv.visitVarInsn(ALOAD, 0);
                cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), "_persistence_initialize_" + attributeDetails.getAttributeName() + "_vh", "()V");
                
                //_toplink_attributeName_vh.setValue(argument);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                cv.visitVarInsn(ALOAD, 1);
                cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "setValue", "(Ljava/lang/Object;)V");
    
                //  _toplink_attributeName_vh.setIsCoordinatedWithProperty(true);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_persistence_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                cv.visitInsn(ICONST_1);
                cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "setIsCoordinatedWithProperty", "(Z)V");
            }
        }
    }
    
}
