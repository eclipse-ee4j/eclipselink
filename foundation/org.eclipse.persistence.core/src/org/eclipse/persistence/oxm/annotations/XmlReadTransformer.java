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
 *     Oracle = 2.2 - Initial contribution
 ******************************************************************************/
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipse.persistence.mappings.transformers.AttributeTransformer;

/**
 * <p><b>Purpose: </b>XmlReadTransformer is used to allow the user direct access to the XML in order to populate their
 * object model. XmlReadTransformer is used in conjunction with XmlWriteTransformers/XmlWriteTransformer to create a customised
 * mapping for a specific attribute. XmlReadTransformer specifies the transformer to be invoked on the
 * unmarshal.
 * 
 * <p><b>Mapping a transformation</b>:  A transformer can be configured to perform both the 
 * XML instance-to-Java attribute transformation at unmarshall time (via attribute transformer) and 
 * the Java attribute-to-XML instance transformation at marshal time (via field transformer).
 *<p><b>Example:</b>
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="employee" type="employee-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="employee-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="name" type="xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="normal-hours" type="normal-hours-type"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="normal-hours-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="start-time" type="xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="end-time" type="xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 * 
 * <p><em>Employee Class</em><br>
 * 
 * <pre>
 * &#64;XmlRootElement(name="employee")
 * public class Employee {
 *   public String name;
 *   &#64;XmlReadTransformer(transformerClass = NormalHoursTransformer.class)
 *   &#64;XmlWriteTransformers({
 *     &#64;XmlWriteTransformer(transformerClass = StartTimeTransformer.class, xmlPath= "normal-hours/start-time/text()"),
 *     &#64;XmlWriteTransformer(transformerClass = EndTimeTransformer.class, xmlPath="normal-hours/end-time/text()")
 *   })
 *   public String[] normalHours;
 * }
 * </pre>
 * 
 * <p><em>Normal Hours Transformer</em><br>
 * <pre>
 * public class NormalHoursTransformer implements AttributeTransformer {
 *   public String[] buildAttributeValue(Record record, Object object, Session session) {
 *     String startTime = (String)record.get("normal-hours/start-time/text()");
 *     String endTime = (String)record.get("normal-hours/end-time/text()");
 *       return new String[]{startTime, endTime};
 *     }
 *     public void initialize(AbstractTransformationMapping mapping) {
 *     }
 *   }
 * }
 * </pre>
 * @see AttributeTransformer
 * @see XmlWriteTransformer
 * @see XmlWriteTransformers   
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlReadTransformer {

    /**
     * User-defined class that must implement the 
     * org.eclipse.persistence.mappings.transformers.AttributeTransformer 
     * interface. The class will be instantiated, its buildAttributeValue will 
     * be used to create the value to be assigned to the attribute.
     * Either transformerClass or method must be specified, but not both.
     */ 
    Class<? extends AttributeTransformer> transformerClass() default AttributeTransformer.class;

    /**
     * The mapped class must have a method with this name which returns a value 
     * to be assigned to the attribute (not assigns the value to the attribute).
     * Either transformerClass or method must be specified, but not both.
     */ 
    String method() default "";

}