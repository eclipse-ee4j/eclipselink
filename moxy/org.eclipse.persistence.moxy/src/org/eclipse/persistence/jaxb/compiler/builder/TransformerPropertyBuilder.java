/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6 - initial implementation
package org.eclipse.persistence.jaxb.compiler.builder;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.compiler.TypeInfo;
import org.eclipse.persistence.jaxb.compiler.XMLProcessor;
import org.eclipse.persistence.jaxb.compiler.builder.helper.TransformerReflectionHelper;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.xmlmodel.XmlTransformation.XmlWriteTransformer;

/**
 * This class is building properties from write transformers.
 *
 * @author Martin Vojtek
 *
 */
public class TransformerPropertyBuilder {

    private final Property property;
    private final TypeInfo typeInfo;
    private final Helper helper;
    private final String attributeToken;

    public TransformerPropertyBuilder(Property property, TypeInfo typeInfo, Helper helper, String attributeToken) {
        this.property = property;
        this.typeInfo = typeInfo;
        this.helper = helper;
        this.attributeToken = attributeToken;
    }

    public java.util.List<Property> buildProperties() {
        java.util.List<Property> props = new ArrayList<Property>();
        for (XmlWriteTransformer writeTransformer : property.getXmlTransformation().getXmlWriteTransformer()) {
            props.add(buildPropertyFromTransformer(getPropertyName(property, writeTransformer), typeInfo, writeTransformer));
        }
        return props;
    }

    private String getPropertyName(Property property, XmlWriteTransformer writeTransformer) {
        String xpath = writeTransformer.getXmlPath();
        return XMLProcessor.getNameFromXPath(xpath, property.getPropertyName(), xpath.contains(attributeToken));
    }

    private Property buildPropertyFromTransformer(String pname, TypeInfo typeInfo, XmlWriteTransformer writeTransformer) {
        Property prop = new Property(helper);
        prop.setPropertyName(pname);
        prop.setXmlPath(writeTransformer.getXmlPath());
        prop.setSchemaName(new QName(pname));
        // figure out the type based on transformer method return type
        prop.setType(getReturnTypeFromTransformer(typeInfo, writeTransformer));
        return prop;
    }

    private JavaClass getReturnTypeFromTransformer(TypeInfo typeInfo, XmlWriteTransformer writeTransformer) {
        JavaClass jClass = getTransformerJavaClass(typeInfo, writeTransformer);
        if (writeTransformer.isSetTransformerClass()) {
            return getTransformerReflectionHelper().getReturnTypeForWriteTransformationMethodTransformer(jClass);
        } else {
            return getTransformerReflectionHelper().getReturnTypeForWriteTransformationMethod(writeTransformer.getMethod(), jClass);
        }
    }

    private JavaClass getTransformerJavaClass(TypeInfo typeInfo, XmlWriteTransformer writeTransformer) {
        if (writeTransformer.isSetTransformerClass()) {
            try {
                return helper.getJavaClass(writeTransformer.getTransformerClass());
            } catch (JAXBException x) {
                throw JAXBException.transformerClassNotFound(writeTransformer.getTransformerClass());
            }
        } else {
            return helper.getJavaClass(typeInfo.getJavaClassName());
        }
    }

    private TransformerReflectionHelper getTransformerReflectionHelper() {
        return new TransformerReflectionHelper(helper);
    }
}
