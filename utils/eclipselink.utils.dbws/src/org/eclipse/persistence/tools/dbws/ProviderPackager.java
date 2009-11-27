/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - Oct 30 2008: some re-work of DBWSPackager hierarchy
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//java eXtension imports
import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.CodeVisitor;
import org.eclipse.persistence.internal.libraries.asm.Label;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.attrs.Annotation;
import org.eclipse.persistence.internal.libraries.asm.attrs.RuntimeVisibleAnnotations;
import org.eclipse.persistence.internal.libraries.asm.attrs.SignatureAttribute;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.XRPackager;
import static org.eclipse.persistence.internal.libraries.asm.Constants.AASTORE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_BRIDGE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_FINAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PRIVATE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PUBLIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_STATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SUPER;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_SYNTHETIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACONST_NULL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ALOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ANEWARRAY;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ARETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ASTORE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.CHECKCAST;
import static org.eclipse.persistence.internal.libraries.asm.Constants.DUP;
import static org.eclipse.persistence.internal.libraries.asm.Constants.GOTO;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_0;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_1;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKEVIRTUAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.RETURN;
import static org.eclipse.persistence.internal.libraries.asm.Constants.V1_5;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_NAME;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_PACKAGE;

/**
 * <p>
 * <b>INTERNAL:</b> ProviderPackager extends {@link XRPackager}. It is responsible for generating<br>
 * the JAX-WS {@link Provider} and saves the generated WSDL to ${stageDir}
 * <pre>
 * ${PACKAGER_ROOT}
 *   | DBWSProvider.class     -- code-generated javax.xml.ws.Provider
 * </pre>
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class ProviderPackager extends XRPackager {

    public static final String ASMIFIED_DBWS_PROVIDER_HELPER =
        ProviderHelper.class.getName().replace('.', '/');
    public static final String ASMIFIED_JAX_WS_PROVIDER =
        "javax/xml/ws/Provider";
    public static final String ASMIFIED_JAX_WS_WEB_SERVICE_PROVIDER =
        "javax/xml/ws/WebServiceProvider";
    public static final String ASMIFIED_JAX_WS_SERVICE_MODE =
        "javax/xml/ws/ServiceMode";
    public static final String ASMIFIED_JSR_250_POSTCONSTRUCT =
        "javax/annotation/PostConstruct";
    public static final String ASMIFIED_JSR_250_PREDESTROY =
        "javax/annotation/PreDestroy";
    public static final String ASMIFIED_JAX_WS_SERVICE =
        "javax/xml/ws/Service";
    public static final String ASMIFIED_SOAP_MESSAGE =
        "javax/xml/soap/SOAPMessage";
    public static final String ASMIFIED_JAX_WS_BINDINGTYPE =
        "javax/xml/ws/BindingType";
    public static final String ASMIFIED_SOAP12_BINDING =
        "javax/xml/ws/soap/SOAPBinding";

    public ProviderPackager() {
        this(new WarArchiver(),"provider", noArchive);
    }
    protected ProviderPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

    @Override
    public Archiver buildDefaultArchiver() {
        return new WarArchiver(this);
    }
    
    @Override
    public OutputStream getWSDLStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_WSDL));
    }

    @Override
    public OutputStream getProviderClassStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_PROVIDER_CLASS_FILE));
    }
    @Override
    @SuppressWarnings("unchecked")
    public void writeProviderClass(OutputStream codeGenProviderStream, DBWSBuilder builder) {
        String serviceName = builder.getWSDLGenerator().getServiceName();
        ClassWriter cw = new ClassWriter(true);
        CodeVisitor cv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME,
            ASMIFIED_DBWS_PROVIDER_HELPER, new String[]{ASMIFIED_JAX_WS_PROVIDER}, 
            DBWS_PROVIDER_NAME + ".java");
        cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "CONTAINER_RESOLVER_CLASSNAME",
            "Ljava/lang/String;", "com.sun.xml.ws.api.server.ContainerResolver", null);

        cv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER, "<init>", "()V");
        cv.visitInsn(RETURN);
        cv.visitMaxs(0, 0);

        // METHOD ATTRIBUTES
        RuntimeVisibleAnnotations methodAttrs0 = new RuntimeVisibleAnnotations();
        Annotation methodAttrs1ann0 = new Annotation("L" + ASMIFIED_JSR_250_POSTCONSTRUCT + ";");
        methodAttrs0.annotations.add(methodAttrs1ann0);

        cv = cw.visitMethod(ACC_PUBLIC, "init", "()V", null, methodAttrs0);
        cv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread",
            "()Ljava/lang/Thread;");
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getContextClassLoader",
            "()Ljava/lang/ClassLoader;");
        cv.visitVarInsn(ASTORE, 1);
        cv.visitInsn(ACONST_NULL);
        cv.visitVarInsn(ASTORE, 2);
        Label l0 = new Label();
        cv.visitLabel(l0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitLdcInsn("com.sun.xml.ws.api.server.ContainerResolver");
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/ClassLoader", "loadClass",
            "(Ljava/lang/String;)Ljava/lang/Class;");
        cv.visitVarInsn(ASTORE, 3);
        Label l1 = new Label();
        cv.visitLabel(l1);
        cv.visitVarInsn(ALOAD, 3);
        cv.visitLdcInsn("getInstance");
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod",
            "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
        cv.visitVarInsn(ASTORE, 4);
        cv.visitVarInsn(ALOAD, 4);
        cv.visitInsn(ACONST_NULL);
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke",
            "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        cv.visitVarInsn(ASTORE, 5);
        cv.visitVarInsn(ALOAD, 5);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        cv.visitLdcInsn("getContainer");
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod",
            "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
        cv.visitVarInsn(ASTORE, 6);
        cv.visitVarInsn(ALOAD, 6);
        cv.visitInsn(ICONST_1);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V");
        cv.visitVarInsn(ALOAD, 6);
        cv.visitVarInsn(ALOAD, 5);
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke",
            "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        cv.visitVarInsn(ASTORE, 7);
        cv.visitVarInsn(ALOAD, 7);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
        cv.visitLdcInsn("getSPI");
        cv.visitInsn(ICONST_1);
        cv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        cv.visitInsn(DUP);
        cv.visitInsn(ICONST_0);
        cv.visitLdcInsn(Type.getType("Ljava/lang/Class;"));
        cv.visitInsn(AASTORE);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod",
            "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
        cv.visitVarInsn(ASTORE, 8);
        cv.visitVarInsn(ALOAD, 8);
        cv.visitInsn(ICONST_1);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V");
        cv.visitVarInsn(ALOAD, 8);
        cv.visitVarInsn(ALOAD, 7);
        cv.visitInsn(ICONST_1);
        cv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        cv.visitInsn(DUP);
        cv.visitInsn(ICONST_0);
        cv.visitLdcInsn(Type.getType("Ljavax/servlet/ServletContext;"));
        cv.visitInsn(AASTORE);
        cv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "invoke",
            "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;");
        cv.visitTypeInsn(CHECKCAST, "javax/servlet/ServletContext");
        cv.visitVarInsn(ASTORE, 2);
        Label l2 = new Label();
        cv.visitLabel(l2);
        Label l3 = new Label();
        cv.visitJumpInsn(GOTO, l3);
        Label l4 = new Label();
        cv.visitLabel(l4);
        cv.visitVarInsn(ASTORE, 3);
        cv.visitLabel(l3);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitVarInsn(ALOAD, 2);
        cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER,
            "init", "(Ljava/lang/ClassLoader;Ljavax/servlet/ServletContext;)V");
        cv.visitInsn(RETURN);
        cv.visitTryCatchBlock(l0, l2, l4, "java/lang/Exception");
        cv.visitMaxs(0, 0);

        cv = cw.visitMethod(ACC_PUBLIC, "invoke",
            "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER,
            "invoke", "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(0, 0);

        // METHOD ATTRIBUTES
        RuntimeVisibleAnnotations methodAttrs1 = new RuntimeVisibleAnnotations();
        Annotation methodAttrs1ann1 = new Annotation("L" + ASMIFIED_JSR_250_PREDESTROY + ";");
        methodAttrs1.annotations.add(methodAttrs1ann1);

        cv = cw.visitMethod(ACC_PUBLIC, "destroy", "()V", null, methodAttrs1);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER,
            "destroy", "()V");
        cv.visitInsn(RETURN);
        cv.visitMaxs(0, 0);

        // synthetic
        cv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "invoke",
            "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitTypeInsn(CHECKCAST, ASMIFIED_SOAP_MESSAGE);
        cv.visitMethodInsn(INVOKEVIRTUAL, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME,
            "invoke", "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";");
        cv.visitInsn(ARETURN);
        cv.visitMaxs(0, 0);

        // CLASS ATRIBUTE
        SignatureAttribute signatureAttr = new SignatureAttribute(
            "L" + ASMIFIED_DBWS_PROVIDER_HELPER + ";L" + ASMIFIED_JAX_WS_PROVIDER +
            "<L" + ASMIFIED_SOAP_MESSAGE + ";>;");
        cw.visitAttribute(signatureAttr);

        // CLASS ATRIBUTE
        RuntimeVisibleAnnotations classAttr = new RuntimeVisibleAnnotations();
        Annotation attrann0 = new Annotation("L" + ASMIFIED_JAX_WS_WEB_SERVICE_PROVIDER + ";");
        String wsdlPathPrevix = getWSDLPathPrefix();
        if (wsdlPathPrevix != null) {
            attrann0.add("wsdlLocation", wsdlPathPrevix + DBWS_WSDL);
        }
        attrann0.add("serviceName", serviceName);
        attrann0.add("portName", serviceName + "Port");
        attrann0.add("targetNamespace", builder.getWSDLGenerator().getServiceNameSpace());
        classAttr.annotations.add(attrann0);
        Annotation attrann1 = new Annotation("L" + ASMIFIED_JAX_WS_SERVICE_MODE + ";");
        attrann1.add("value", new Annotation.EnumConstValue(
            "L" + ASMIFIED_JAX_WS_SERVICE + "$Mode;", "MESSAGE"));
        classAttr.annotations.add(attrann1);
        
        if (builder.usesSOAP12()) {
            Annotation attrann2 = new Annotation("L" + ASMIFIED_JAX_WS_BINDINGTYPE + ";");
            attrann2.add("value", SOAP12HTTP_BINDING);
            classAttr.annotations.add(attrann2);
        }
        cw.visitAttribute(classAttr);
        cv.visitMaxs(0, 0);

        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        try {
            codeGenProviderStream.write(bytes, 0, bytes.length);
        }
        catch (IOException e) {/* ignore */}
    }
}
