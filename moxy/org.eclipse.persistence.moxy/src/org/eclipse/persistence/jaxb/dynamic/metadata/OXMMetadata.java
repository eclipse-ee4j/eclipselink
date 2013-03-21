/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.dynamic.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaClassImpl;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMJavaModelInputImpl;
import org.eclipse.persistence.jaxb.javamodel.oxm.OXMObjectFactoryImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaClassImpl;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnum;
import org.eclipse.persistence.jaxb.xmlmodel.XmlEnumValue;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry;

public class OXMMetadata extends Metadata {

    public OXMMetadata(DynamicClassLoader classLoader, Map<String, ?> properties) {
        super(classLoader, properties);
    }

    public JavaModelInput getJavaModelInput() throws JAXBException {
        JavaClass[] elinkClasses = createClassModelFromOXM(dynamicClassLoader);

        // Use the JavaModel to setup a Generator to generate an EclipseLink project
        OXMJavaModelImpl javaModel = new OXMJavaModelImpl(dynamicClassLoader, elinkClasses);
        for (JavaClass javaClass : elinkClasses) {
            try {
                ((OXMJavaClassImpl) javaClass).setJavaModel(javaModel);
            } catch (ClassCastException cce) {
                try {
                    ((OXMObjectFactoryImpl) javaClass).setJavaModel(javaModel);
                    ((OXMObjectFactoryImpl) javaClass).init();
                } catch(ClassCastException cce2) {
                    ((JavaClassImpl)javaClass).setJavaModelImpl(javaModel);
                }
            }
        }

        return new OXMJavaModelInputImpl(elinkClasses, javaModel);
    }

    private JavaClass[] createClassModelFromOXM(DynamicClassLoader dynamicClassLoader) throws JAXBException {
        List<JavaClass> oxmJavaClasses = new ArrayList<JavaClass>();

        Iterator<String> keys = bindings.keySet().iterator();

        while (keys.hasNext()) {
            String pkgName = keys.next();
            XmlBindings b = bindings.get(pkgName);

            if (b.getJavaTypes() != null) {
                List<JavaType> javaTypes = b.getJavaTypes().getJavaType();
                for (Iterator<JavaType> iterator = javaTypes.iterator(); iterator.hasNext();) {
                    JavaType type = iterator.next();
                    //Check to see if it's a static class or if should be treated as dynamic
                    try {
                        Class staticClass = dynamicClassLoader.getParent().loadClass(Helper.getQualifiedJavaTypeName(type.getName(), pkgName));
                        oxmJavaClasses.add(new JavaClassImpl(staticClass, null));
                    } catch(Exception ex) {
                        type.setName(Helper.getQualifiedJavaTypeName(type.getName(), pkgName));
                        oxmJavaClasses.add(new OXMJavaClassImpl(type));
                    }
                }
            }

            if (b.getXmlRegistries() != null) {
                List<XmlRegistry> registries = b.getXmlRegistries().getXmlRegistry();
                for (Iterator<XmlRegistry> iterator = registries.iterator(); iterator.hasNext();) {
                    XmlRegistry reg = iterator.next();
                    oxmJavaClasses.add(new OXMObjectFactoryImpl(reg));
                }
            }

            if (b.getXmlEnums() != null) {
                List<XmlEnum> enums = b.getXmlEnums().getXmlEnum();
                for (Iterator<XmlEnum> iterator = enums.iterator(); iterator.hasNext();) {
                    XmlEnum xmlEnum = iterator.next();

                    List<XmlEnumValue> enumValues = xmlEnum.getXmlEnumValue();
                    List<String> enumValueStrings = new ArrayList<String>();
                    for (Iterator<XmlEnumValue> iterator2 = enumValues.iterator(); iterator2.hasNext();) {
                        XmlEnumValue xmlEnumValue = iterator2.next();
                        enumValueStrings.add(xmlEnumValue.getJavaEnumValue());
                    }

                    oxmJavaClasses.add(new OXMJavaClassImpl(xmlEnum.getJavaEnum(), enumValueStrings));

                    // Trigger a dynamic class generation, because we won't
                    // be creating a descriptor for this
                    dynamicClassLoader.addEnum(xmlEnum.getJavaEnum(), enumValueStrings.toArray());
                }
            }
        }

        JavaClass[] javaClasses = new JavaClass[oxmJavaClasses.size()];
        for (int i = 0; i < javaClasses.length; i++) {
            javaClasses[i] = oxmJavaClasses.get(i);
        }

        return javaClasses;
    }

}