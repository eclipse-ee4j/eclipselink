// Copyright (c) 2005, 2007, Oracle. All rights reserved.  
package org.eclipse.persistence.internal.weaving;

//ASM imports
import org.eclipse.persistence.internal.libraries.asm.*;

/**
 * INTERNAL:
 * 
 * Used by TopLink's weaving feature to adjust methods to make use of ValueHolders that
 * have been inserted by ClassWeaver.
 * 
 * For FIELD access, changes references to GETFIELD and PUTFIELD to call newly added 
 * convenience methods.
 * 
 * For Property access, modifies the getters and setters to make use of new ValueHolders
 * 
 * Also adds initialization of newly added ValueHolders to constructor.
 * 
 */

public class MethodWeaver extends CodeAdapter implements Constants {

    protected ClassWeaver tcw;
    protected String methodName;
    protected String methodDescriptor = null;
    
    // used to determine if we are at the first line of a method
    protected boolean methodStarted = false;
    
    // Used to control initialization of valueholders in constructor
    protected boolean constructorInitializationDone = false;
    
    public MethodWeaver(ClassWeaver tcw, String methodName, String methodDescriptor, CodeVisitor cv) {
        
        super(cv);
        this.tcw = tcw;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    /**
     * INTERNAL:
     * Modifies getter and setter methods for attributes using property access
     * 
     * In a getter method for 'attributeName', the following line is added at the beginning of the method
     * 
     *  if (!_toplink_attributeName_vh.isInstantiated()){
     *      setFoo((EntityC)_toplink_attributeName_vh.getValue());
     *  }
     * 
     * In a setter method, for 'attributeName', the following line is added at the beginning of the method
     * 
     *  _toplink_attributeName_vh.setValue(argument);
     *  _toplink_attributeName_vh.setIsCoordinatedWithProperty(true);
     * 
     * TODO: In the end, the call to setValue() should be modified to somehow make use of the result of
     * the getter method.  This behavior has not yet been implemented.
     */
    public void addValueHolderReferencesIfRequired(){
        if (methodStarted){
            return;
        }
        AttributeDetails attributeDetails = (AttributeDetails)tcw.classDetails.getGetterMethodToAttributeDetails().get(methodName);
        if (attributeDetails != null && !attributeDetails.isAttributeOnSuperClass()){
            
            cv.visitVarInsn(ALOAD, 0);
            cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_toplink_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
            cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "isInstantiated", "()Z");
            Label l0 = new Label();
            cv.visitJumpInsn(IFNE, l0);
            
            cv.visitVarInsn(ALOAD, 0);
            cv.visitVarInsn(ALOAD, 0);
            cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_toplink_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
            cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "getValue", "()Ljava/lang/Object;");
            cv.visitTypeInsn(CHECKCAST, attributeDetails.getReferenceClassName().replace('.','/'));
            cv.visitMethodInsn(INVOKEVIRTUAL, tcw.classDetails.getClassName(), attributeDetails.getSetterMethodName(), "(L" + attributeDetails.getReferenceClassName().replace('.','/') + ";)V");

            cv.visitLabel(l0);
        } else {
            attributeDetails = (AttributeDetails)tcw.classDetails.getSetterMethodToAttributeDetails().get(methodName);
            if (attributeDetails != null){
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_toplink_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                cv.visitVarInsn(ALOAD, 1);
                cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "setValue", "(Ljava/lang/Object;)V");
                
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, tcw.classDetails.getClassName(), "_toplink_" + attributeDetails.getAttributeName() + "_vh", ClassWeaver.VHI_SIGNATURE);
                cv.visitInsn(ICONST_1);
                cv.visitMethodInsn(INVOKEINTERFACE, ClassWeaver.VHI_SHORT_SIGNATURE, "setIsCoordinatedWithProperty", "(Z)V");
            }
        }
    }

    public void visitInsn (final int opcode) {
        methodStarted = true;
        super.visitInsn(opcode);
    }

    public void visitIntInsn (final int opcode, final int operand) {
        methodStarted = true;
        cv.visitIntInsn(opcode, operand);
    }

    public void visitVarInsn (final int opcode, final int var) {
        methodStarted = true;
        cv.visitVarInsn(opcode, var);
    }

    public void visitTypeInsn (final int opcode, final String desc) {
        methodStarted = true;
        cv.visitTypeInsn(opcode, desc);
    }

    public void visitFieldInsn (final int opcode, final String owner, final String name, final String desc){
        methodStarted = true;
        super.visitFieldInsn(opcode, owner, name, desc); 
    }

    public void visitMethodInsn (final int opcode, final String owner, final String name, final String desc){
        methodStarted = true;
        super.visitMethodInsn(opcode, owner, name, desc);     
    }

    public void visitJumpInsn (final int opcode, final Label label) {
        methodStarted = true;
        cv.visitJumpInsn(opcode, label);
    }

    public void visitLabel (final Label label) {
        cv.visitLabel(label);
    }

    public void visitLdcInsn (final Object cst) {
        methodStarted = true;
        cv.visitLdcInsn(cst);
    }

    public void visitIincInsn (final int var, final int increment) {
        methodStarted = true;
        cv.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn (final int min, final int max, final Label dflt, final Label labels[]){
        methodStarted = true;
        cv.visitTableSwitchInsn(min, max, dflt, labels);
    }

    public void visitLookupSwitchInsn (final Label dflt, final int keys[], final Label labels[]){
        methodStarted = true;
        cv.visitLookupSwitchInsn(dflt, keys, labels);
    }

    public void visitMultiANewArrayInsn (final String desc, final int dims) {
        methodStarted = true;
        cv.visitMultiANewArrayInsn(desc, dims);
    }

    public void visitTryCatchBlock (final Label start, final Label end,final Label handler, final String type){
        methodStarted = true;
        cv.visitTryCatchBlock(start, end, handler, type);
    }

    public void visitMaxs (final int maxStack, final int maxLocals) {
        methodStarted = true;
        cv.visitMaxs(0, 0);
    }

    public void visitLocalVariable (final String name, final String desc, final Label start, final Label end, final int index){
        methodStarted = true;
        cv.visitLocalVariable(name, desc, start, end, index);
    }

    public void visitLineNumber (final int line, final Label start) {
        cv.visitLineNumber(line, start);
    }

    public void visitAttribute (final Attribute attr) {
        methodStarted = true;
        cv.visitAttribute(attr);
    }

    // helper methods
    protected AttributeDetails weaveValueHolders(ClassDetails startingDetails,
        String fieldName) {
        
        if (startingDetails == null) {
            return null;
        } else {
            AttributeDetails attributeDetails = (AttributeDetails)startingDetails.getAttributesMap().get(fieldName);
            if (attributeDetails != null && attributeDetails.weaveValueHolders()) {
                return attributeDetails;
            } else {
                return weaveValueHolders(startingDetails.getSuperClassDetails(), fieldName);
            }
        }
    }

}
