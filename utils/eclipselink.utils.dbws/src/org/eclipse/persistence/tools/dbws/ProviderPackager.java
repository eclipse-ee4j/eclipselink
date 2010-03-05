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
import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING;
import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;
import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING;

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
import static org.eclipse.persistence.internal.libraries.asm.Constants.ACC_PROTECTED;
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
import static org.eclipse.persistence.internal.libraries.asm.Constants.GETFIELD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.GOTO;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_0;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ICONST_1;
import static org.eclipse.persistence.internal.libraries.asm.Constants.IFEQ;
import static org.eclipse.persistence.internal.libraries.asm.Constants.IFNULL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ILOAD;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKEINTERFACE;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESPECIAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKESTATIC;
import static org.eclipse.persistence.internal.libraries.asm.Constants.INVOKEVIRTUAL;
import static org.eclipse.persistence.internal.libraries.asm.Constants.ISTORE;
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
    public static final String ASMIFIED_JAVA_LANG_CLASS = 
        "java/lang/Class";
    public static final String ASMIFIED_JAVA_LANG_CLASSLOADER = 
        "java/lang/ClassLoader";
    public static final String ASMIFIED_JAVA_LANG_EXCEPTION =
        "java/lang/Exception";
    public static final String ASMIFIED_JAVA_LANG_STRING =
        "java/lang/String";
    public static final String ASMIFIED_JAVA_LANG_THREAD =
        "java/lang/Thread";
    public static final String ASMIFIED_JAVA_LANG_OBJECT =
        "java/lang/Object";
    public static final String ASMIFIED_JAVA_LANG_ANNOTATION =
        "java/lang/annotation/Annotation";
    public static final String ASMIFIED_JAVA_LANG_REFLECT_METHOD = 
        "java/lang/reflect/Method";
    public static final String ASMIFIED_JAVAX_SERVLET_CONTEXT =
        "javax/servlet/ServletContext";
    public static final String ASMIFIED_JAX_WS_BINDINGTYPE =
        "javax/xml/ws/BindingType";
    public static final String ASMIFIED_JAX_WS_PROVIDER =
        "javax/xml/ws/Provider";
    public static final String ASMIFIED_JAX_WS_WEB_SERVICE_PROVIDER =
        "javax/xml/ws/WebServiceProvider";
    public static final String ASMIFIED_JAX_WS_WEB_SERVICE_CONTEXT =
        "javax/xml/ws/WebServiceContext";
    public static final String ASMIFIED_JAX_WS_SERVICE_MODE =
        "javax/xml/ws/ServiceMode";
    public static final String ASMIFIED_JAX_WS_MESSAGE_CONTEXT =
        "javax/xml/ws/handler/MessageContext";
    public static final String ASMIFIED_JSR_250_POSTCONSTRUCT =
        "javax/annotation/PostConstruct";
    public static final String ASMIFIED_JSR_250_PREDESTROY =
        "javax/annotation/PreDestroy";
    public static final String ASMIFIED_JSR_250_RESOURCE =
        "javax/annotation/Resource";
    public static final String ASMIFIED_JAX_WS_SERVICE =
        "javax/xml/ws/Service";
    public static final String ASMIFIED_SOAP_MESSAGE =
        "javax/xml/soap/SOAPMessage";
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
            "L" + ASMIFIED_JAVA_LANG_STRING + ";", "com.sun.xml.ws.api.server.ContainerResolver",
            null);

        // FIELD ATTRIBUTES
        RuntimeVisibleAnnotations fieldAttrs1 = new RuntimeVisibleAnnotations();
        Annotation fieldAttrs1ann0 = new Annotation("L" + ASMIFIED_JSR_250_RESOURCE + ";");
        fieldAttrs1.annotations.add(fieldAttrs1ann0);
        cw.visitField(ACC_PROTECTED, "wsContext", "L" + ASMIFIED_JAX_WS_WEB_SERVICE_CONTEXT + ";",
            null, fieldAttrs1);
        
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
        cv.visitMethodInsn(INVOKESTATIC, ASMIFIED_JAVA_LANG_THREAD, "currentThread",
            "()L" + ASMIFIED_JAVA_LANG_THREAD + ";");
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_THREAD, "getContextClassLoader",
            "()L" + ASMIFIED_JAVA_LANG_CLASSLOADER + ";");
        cv.visitVarInsn(ASTORE, 1);
        cv.visitInsn(ACONST_NULL);
        cv.visitVarInsn(ASTORE, 2);
        Label l0 = new Label();
        cv.visitLabel(l0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitLdcInsn("com.sun.xml.ws.api.server.ContainerResolver");
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_CLASSLOADER, "loadClass",
            "(L" + ASMIFIED_JAVA_LANG_STRING + ";)L" + ASMIFIED_JAVA_LANG_CLASS + ";");
        cv.visitVarInsn(ASTORE, 3);
        Label l1 = new Label();
        cv.visitLabel(l1);
        cv.visitVarInsn(ALOAD, 3);
        cv.visitLdcInsn("getInstance");
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, ASMIFIED_JAVA_LANG_CLASS);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_CLASS, "getMethod",
            "(L" + ASMIFIED_JAVA_LANG_STRING + ";[L" + ASMIFIED_JAVA_LANG_CLASS + 
            ";)L" + ASMIFIED_JAVA_LANG_REFLECT_METHOD + ";");
        cv.visitVarInsn(ASTORE, 4);
        cv.visitVarInsn(ALOAD, 4);
        cv.visitInsn(ACONST_NULL);
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, ASMIFIED_JAVA_LANG_OBJECT);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_REFLECT_METHOD, "invoke",
            "(L" + ASMIFIED_JAVA_LANG_OBJECT + ";[L" + ASMIFIED_JAVA_LANG_OBJECT +
            ";)L" + ASMIFIED_JAVA_LANG_OBJECT + ";");
        cv.visitVarInsn(ASTORE, 5);
        cv.visitVarInsn(ALOAD, 5);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_OBJECT, "getClass",
            "()L" + ASMIFIED_JAVA_LANG_CLASS  + ";");
        cv.visitLdcInsn("getContainer");
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, ASMIFIED_JAVA_LANG_CLASS);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_CLASS, "getMethod",
            "(L" + ASMIFIED_JAVA_LANG_STRING + ";[L" + ASMIFIED_JAVA_LANG_CLASS +
            ";)L" + ASMIFIED_JAVA_LANG_REFLECT_METHOD + ";");
        cv.visitVarInsn(ASTORE, 6);
        cv.visitVarInsn(ALOAD, 6);
        cv.visitInsn(ICONST_1);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_REFLECT_METHOD, "setAccessible", "(Z)V");
        cv.visitVarInsn(ALOAD, 6);
        cv.visitVarInsn(ALOAD, 5);
        cv.visitInsn(ICONST_0);
        cv.visitTypeInsn(ANEWARRAY, ASMIFIED_JAVA_LANG_OBJECT);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_REFLECT_METHOD, "invoke",
            "(L" + ASMIFIED_JAVA_LANG_OBJECT + ";[L" + ASMIFIED_JAVA_LANG_OBJECT + ";)L" +
            ASMIFIED_JAVA_LANG_OBJECT + ";");
        cv.visitVarInsn(ASTORE, 7);
        cv.visitVarInsn(ALOAD, 7);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_OBJECT, "getClass", "()L" +
            ASMIFIED_JAVA_LANG_CLASS + ";");
        cv.visitLdcInsn("getSPI");
        cv.visitInsn(ICONST_1);
        cv.visitTypeInsn(ANEWARRAY, ASMIFIED_JAVA_LANG_CLASS);
        cv.visitInsn(DUP);
        cv.visitInsn(ICONST_0);
        cv.visitLdcInsn(Type.getType("L" + ASMIFIED_JAVA_LANG_CLASS + ";"));
        cv.visitInsn(AASTORE);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_CLASS, "getMethod",
            "(L" + ASMIFIED_JAVA_LANG_STRING + ";[L" + ASMIFIED_JAVA_LANG_CLASS + ";)L" +
            ASMIFIED_JAVA_LANG_REFLECT_METHOD + ";");
        cv.visitVarInsn(ASTORE, 8);
        cv.visitVarInsn(ALOAD, 8);
        cv.visitInsn(ICONST_1);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_REFLECT_METHOD, "setAccessible", "(Z)V");
        cv.visitVarInsn(ALOAD, 8);
        cv.visitVarInsn(ALOAD, 7);
        cv.visitInsn(ICONST_1);
        cv.visitTypeInsn(ANEWARRAY, ASMIFIED_JAVA_LANG_OBJECT);
        cv.visitInsn(DUP);
        cv.visitInsn(ICONST_0);
        cv.visitLdcInsn(Type.getType("L" + ASMIFIED_JAVAX_SERVLET_CONTEXT + ";"));
        cv.visitInsn(AASTORE);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_REFLECT_METHOD, "invoke",
            "(L" + ASMIFIED_JAVA_LANG_OBJECT + ";[L" + ASMIFIED_JAVA_LANG_OBJECT + ";)L" +
            ASMIFIED_JAVA_LANG_OBJECT + ";");
        cv.visitTypeInsn(CHECKCAST, ASMIFIED_JAVAX_SERVLET_CONTEXT);
        cv.visitVarInsn(ASTORE, 2);
        Label l2 = new Label();
        cv.visitLabel(l2);
        Label l3 = new Label();
        cv.visitJumpInsn(GOTO, l3);
        Label l4 = new Label();
        cv.visitLabel(l4);
        cv.visitVarInsn(ASTORE, 3);
        cv.visitLabel(l3);
        cv.visitInsn(ICONST_0);
        cv.visitVarInsn(ISTORE, 3);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_OBJECT, "getClass",
            "()L" + ASMIFIED_JAVA_LANG_CLASS + ";");
        cv.visitLdcInsn(Type.getType("L" + ASMIFIED_JAX_WS_BINDINGTYPE + ";"));
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_CLASS, "getAnnotation",
            "(L" + ASMIFIED_JAVA_LANG_CLASS + ";)L" +
            ASMIFIED_JAVA_LANG_ANNOTATION + ";");
        cv.visitTypeInsn(CHECKCAST, ASMIFIED_JAX_WS_BINDINGTYPE);
        cv.visitVarInsn(ASTORE, 4);
        cv.visitVarInsn(ALOAD, 4);
        Label l5 = new Label();
        cv.visitJumpInsn(IFNULL, l5);
        cv.visitVarInsn(ALOAD, 4);
        cv.visitMethodInsn(INVOKEINTERFACE, ASMIFIED_JAX_WS_BINDINGTYPE, "value",
            "()L" + ASMIFIED_JAVA_LANG_STRING + ";");
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_STRING, "toLowerCase",
            "()L" + ASMIFIED_JAVA_LANG_STRING + ";");
        cv.visitLdcInsn("mtom=true");
        cv.visitMethodInsn(INVOKEVIRTUAL, ASMIFIED_JAVA_LANG_STRING, "contains",
            "(L" +
            "java/lang/CharSequence" +
            ";)Z");
        cv.visitJumpInsn(IFEQ, l5);
        cv.visitInsn(ICONST_1);
        cv.visitVarInsn(ISTORE, 3);
        cv.visitLabel(l5);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitVarInsn(ALOAD, 2);
        cv.visitVarInsn(ILOAD, 3);
        cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER, "init",
            "(L" + ASMIFIED_JAVA_LANG_CLASSLOADER + ";L" + ASMIFIED_JAVAX_SERVLET_CONTEXT + ";Z)V");
        cv.visitInsn(RETURN);
        cv.visitTryCatchBlock(l0, l2, l4, ASMIFIED_JAVA_LANG_EXCEPTION);
        cv.visitMaxs(0, 0);

        cv = cw.visitMethod(ACC_PUBLIC, "invoke",
            "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";", null, null);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitFieldInsn(GETFIELD, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME, "wsContext",
            "L" + ASMIFIED_JAX_WS_WEB_SERVICE_CONTEXT + ";");
        Label l6 = new Label();
        cv.visitJumpInsn(IFNULL, l6);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitFieldInsn(GETFIELD, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME, "wsContext",
            "L" + ASMIFIED_JAX_WS_WEB_SERVICE_CONTEXT + ";");
        cv.visitMethodInsn(INVOKEINTERFACE, ASMIFIED_JAX_WS_WEB_SERVICE_CONTEXT, "getMessageContext",
            "()L" + ASMIFIED_JAX_WS_MESSAGE_CONTEXT + ";");
        cv.visitMethodInsn(INVOKEVIRTUAL, DBWS_PROVIDER_PACKAGE + "/" + DBWS_PROVIDER_NAME,
            "setMessageContext", "(L" + ASMIFIED_JAX_WS_MESSAGE_CONTEXT + ";)V");
        cv.visitLabel(l0);
        cv.visitVarInsn(ALOAD, 0);
        cv.visitVarInsn(ALOAD, 1);
        cv.visitMethodInsn(INVOKESPECIAL, ASMIFIED_DBWS_PROVIDER_HELPER, "invoke",
            "(L" + ASMIFIED_SOAP_MESSAGE + ";)L" + ASMIFIED_SOAP_MESSAGE + ";");
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
            "(L" + ASMIFIED_JAVA_LANG_OBJECT + ";)L" +
            ASMIFIED_JAVA_LANG_OBJECT + ";", null, null);
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

        Annotation attrann2 = new Annotation("L" + ASMIFIED_JAX_WS_BINDINGTYPE + ";");
        if (builder.usesSOAP12()) {
            if (builder.mtomEnabled()) {
                attrann2.add("value", SOAP12HTTP_MTOM_BINDING);
            }
            else {
                attrann2.add("value", SOAP12HTTP_BINDING);
            }
        }
        else {
            if (builder.mtomEnabled()) {
                attrann2.add("value", SOAP11HTTP_MTOM_BINDING);
            }
            // else the default BindingType, don't have to explicitly set it
        }
        if (attrann2.elementValues.size() == 1) {
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
