/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import org.eclipse.persistence.mappings.transformers.AttributeTransformer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * XmlReadTransformer is used to allow the user direct access to the XML in order to populate their
 * object model. XmlReadTransformer is used in conjunction with {@linkplain XmlWriteTransformers}/{@linkplain XmlWriteTransformer} to create a customised
 * mapping for a specific attribute. XmlReadTransformer specifies the transformer to be invoked on the
 * unmarshal.
 *
 * <p><b>Mapping a transformation:</b> A transformer can be configured to perform both the
 * XML instance-to-Java attribute transformation at unmarshall time (via attribute transformer) and
 * the Java attribute-to-XML instance transformation at marshal time (via field transformer).
 * <p><b>Example:</b>
 * <p>
 * <em>XML Schema</em>
 * {@snippet lang="XML":
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xsd:schema xmlns:xsd="https://www.w3.org/2001/XMLSchema">
 *    <xsd:element name="employee" type="employee-type"/>
 *    <xsd:complexType name="employee-type">
 *      <xsd:sequence>
 *        <xsd:element name="name" type="xsd:string"/>
 *        <xsd:element name="normal-hours" type="normal-hours-type"/>
 *      </xsd:sequence>
 *    </xsd:complexType>
 *    <xsd:complexType name="normal-hours-type">
 *      <xsd:sequence>
 *        <xsd:element name="start-time" type="xsd:string"/>
 *        <xsd:element name="end-time" type="xsd:string"/>
 *      </xsd:sequence>
 *    </xsd:complexType>
 *  </xsd:schema>
 * }
 * <em>Employee Class</em>
 * {@snippet :
 *  @XmlRootElement(name="employee")
 *  public class Employee {
 *      public String name;
 *
 *      @XmlReadTransformer(transformerClass = NormalHoursTransformer.class)
 *      @XmlWriteTransformers({
 *          @XmlWriteTransformer(transformerClass = StartTimeTransformer.class, xmlPath= "normal-hours/start-time/text()"),
 *          @XmlWriteTransformer(transformerClass = EndTimeTransformer.class, xmlPath="normal-hours/end-time/text()")
 *      })
 *      public String[] normalHours;
 *  }
 * }
 * <em>Normal Hours Transformer</em>
 * {@snippet :
 *  public class NormalHoursTransformer implements org.eclipse.persistence.mappings.transformers.AttributeTransformer {
 *
 *      @Override
 *      public String[] buildAttributeValue(DataRecord dRecord, Object o, Session s) {
 *          String startTime = (String) dRecord.get("normal-hours/start-time/text()");
 *          String endTime = (String) dRecord.get("normal-hours/end-time/text()");
 *          return new String[] {startTime, endTime};
 *      }
 *
 *      @Override
 *      public void initialize(AbstractTransformationMapping mapping) {
 *      }
 *   }
 * }
 *
 * @see org.eclipse.persistence.mappings.transformers.AttributeTransformer
 * @see XmlWriteTransformer
 * @see XmlWriteTransformers
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlReadTransformer {

    /**
     * User-defined class that must implement the
     * {@linkplain org.eclipse.persistence.mappings.transformers.AttributeTransformer}
     * interface. The class will be instantiated, its
     * {@linkplain org.eclipse.persistence.mappings.transformers.AttributeTransformer#buildAttributeValue(org.eclipse.persistence.sessions.DataRecord, Object, org.eclipse.persistence.sessions.Session) buildAttributeValue}
     * will be used to create the value to be assigned to the attribute.
     * <p>
     * Either transformerClass or {@linkplain #method()} must be specified, but not both.
     */
    Class<? extends AttributeTransformer> transformerClass() default AttributeTransformer.class;

    /**
     * The mapped class must have a method with this name which returns a value
     * to be assigned to the attribute (not assigns the value to the attribute).
     * <p>
     * Either {@linkplain #transformerClass()} or method must be specified, but not both.
     */
    String method() default "";

}
