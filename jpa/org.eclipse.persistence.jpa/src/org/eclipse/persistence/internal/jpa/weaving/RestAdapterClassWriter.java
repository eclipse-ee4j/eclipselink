package org.eclipse.persistence.internal.jpa.weaving;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.EclipseLinkClassWriter;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;

public class RestAdapterClassWriter implements EclipseLinkClassWriter, Opcodes {

    public static final String REFERENCE_ADAPTER_SHORT_SIGNATURE = "org/eclipse/persistence/jpa/rs/util/ReferenceAdapter";
    public static final String ADAPTER_INNER_CLASS_NAME = "PersistenceRestAdapter";
    
    protected String parentClassName;
    
    public RestAdapterClassWriter(String parentClassName){
        this.parentClassName = parentClassName;
    }
    
    public String getClassName(){
        return parentClassName + "." + ADAPTER_INNER_CLASS_NAME;
    }
    
    public String getASMParentClassName(){
        return parentClassName.replace('.', '/');
    }
    
    public String getASMClassName(){
        return getASMParentClassName() + "/" + ADAPTER_INNER_CLASS_NAME;
    }
    
    @Override
    public byte[] writeClass(DynamicClassLoader loader, String className)
            throws ClassNotFoundException {
        
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, getASMClassName(), "L" + REFERENCE_ADAPTER_SHORT_SIGNATURE + "<L" + getASMParentClassName() + ";>;", REFERENCE_ADAPTER_SHORT_SIGNATURE, null);

        cw.visitInnerClass(getASMClassName(), getASMParentClassName(), ADAPTER_INNER_CLASS_NAME, ACC_PUBLIC + ACC_STATIC);

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, REFERENCE_ADAPTER_SHORT_SIGNATURE, "<init>", "()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;Lorg/eclipse/persistence/jpa/rs/PersistenceContext;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, REFERENCE_ADAPTER_SHORT_SIGNATURE, "<init>", "(Ljava/lang/String;Lorg/eclipse/persistence/jpa/rs/PersistenceContext;)V");        
        mv.visitInsn(RETURN);
        mv.visitMaxs(3, 3);
        mv.visitEnd();
        
        cw.visitEnd();
        
        return cw.toByteArray();
    }

    @Override
    public boolean isCompatible(EclipseLinkClassWriter writer) {
        return false;
    }

    @Override
    public Class<?> getParentClass() {
        return null;
    }
    
    @Override
    public String getParentClassName() {
        return parentClassName;
    }

}
