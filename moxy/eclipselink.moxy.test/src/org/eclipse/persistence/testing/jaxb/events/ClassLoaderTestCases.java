/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.events;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.internal.libraries.asm.AnnotationVisitor;
import org.eclipse.persistence.internal.libraries.asm.ClassWriter;
import org.eclipse.persistence.internal.libraries.asm.MethodVisitor;
import org.eclipse.persistence.internal.libraries.asm.Opcodes;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.xml.sax.helpers.DefaultHandler;

public class ClassLoaderTestCases extends TestCase {

    private static final String CLASS_NAME = "ClassLoaderChild";

    private Class classLoaderChildClass;
    private JAXBContext jaxbContext;

    @Override
    protected void setUp() throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, CLASS_NAME, null, ClassLoaderRoot.class.getName().replace('.', '/'), null);
        AnnotationVisitor xmlTypeAV = cw.visitAnnotation("Ljavax/xml/bind/annotation/XmlRootElement;", true);
        xmlTypeAV.visit("name", "root");
        xmlTypeAV.visitEnd();

        // Write Constructor:
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassLoaderRoot.class.getName().replace('.', '/'), "<init>", "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        JaxbClassLoader classLoader = new JaxbClassLoader(ClassLoaderRoot.class.getClassLoader());
        classLoaderChildClass = classLoader.generateClass(CLASS_NAME, cw.toByteArray());
        jaxbContext = JAXBContextFactory.createContext(new Class[] {classLoaderChildClass}, null);
    }

    public void testMarshalJSON() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        ClassLoaderRoot classLoaderChild = (ClassLoaderRoot) classLoaderChildClass.newInstance();

        assertEquals(0, classLoaderChild.beforeMarshalCalled);
        assertEquals(0, classLoaderChild.afterMarshalCalled);
        marshaller.marshal(classLoaderChild, new StringWriter());
        assertEquals(1, classLoaderChild.beforeMarshalCalled);
        assertEquals(1, classLoaderChild.afterMarshalCalled);
    }

    public void testMarshalXML() throws Exception {
        Marshaller marshaller = jaxbContext.createMarshaller();
        ClassLoaderRoot classLoaderChild = (ClassLoaderRoot) classLoaderChildClass.newInstance();

        assertEquals(0, classLoaderChild.beforeMarshalCalled);
        assertEquals(0, classLoaderChild.afterMarshalCalled);
        marshaller.marshal(classLoaderChild, new DefaultHandler());
        assertEquals(1, classLoaderChild.beforeMarshalCalled);
        assertEquals(1, classLoaderChild.afterMarshalCalled);
    }

    public void testUnmarshalJSON() throws Exception {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        StringReader xml = new StringReader("{\"root\" : {}}");
        ClassLoaderRoot classLoaderChild = (ClassLoaderRoot) unmarshaller.unmarshal(xml);
        assertEquals(1, classLoaderChild.beforeUnmarshalCalled);
        assertEquals(1, classLoaderChild.afterUnmarshalCalled);
     }

    public void testUnmarshalXML() throws Exception {
       Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
       StringReader xml = new StringReader("<root/>");
       ClassLoaderRoot classLoaderChild = (ClassLoaderRoot) unmarshaller.unmarshal(xml);
       assertEquals(1, classLoaderChild.beforeUnmarshalCalled);
       assertEquals(1, classLoaderChild.afterUnmarshalCalled);
    }

}
