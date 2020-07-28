/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.oxm.mappings;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.TransformationMapping;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLTransformationRecord;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;

/**
 * <p>Transformation XML mappings are used to create a custom mapping where one or more XML nodes can
 * be used to create the object to be stored in a Java class's attribute.  To handle the custom
 * requirements at marshal (write) and unmarshall (read) time, a transformation mapping takes instances
 * of org.eclipse.persistence.mappings.transformers (such as AttributeTransformer and FieldTransformer), providing
 * a non-intrusive solution that avoids the need for domain objects to implement any 'special' interfaces.
 *
 * <p><b>Setting the XPath</b>: TopLink XML mappings make use of XPath statements to find the relevant
 * data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
 * The XPath may contain node type, path, and positional information.  The XPath is specified on the
 * field transformer that is set on the mapping.  The XPath is set as the first parameter of the <code>
 * addFieldTransformer</code> method.
 *
 * <p>The following XPath statements may be used to specify the location of XML data relating to an object's
 * name attribute:
 *
 * <table summary="" border="1">
 * <tr>
 * <th id="c1" align="left">XPath</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">@name</td>
 * <td headers="c2">The "@" character indicates that the node is an attribute.</td>
 * </tr>
 * <tr>
 * <td headers="c1">text()</td>
 * <td headers="c2">"text()" indicates that the node is a text node.  In this case the name value in the
 * text node belongs to the context node.</td>
 * </tr>
 * <tr>
 * <td headers="c1">full-name/text()</td>
 * <td headers="c2">The name information is stored in the text node of the full-name element.</td>
 * </tr>
 * <tr>
 * <td headers="c1" style="nowrap">personal-info/name/text()</td>
 * <td headers="c2">The XPath statement may be used to specify any valid path.</td>
 * </tr>
 * <tr>
 * <td headers="c1">name[2]/text()</td>
 * <td headers="c2">The XPath statement may contain positional information.  In this case the name
 * information is stored in the text node of the second occurrence of the name element.</td>
 * </tr>
 * </table>
 *
 * <p><b>Mapping a transformation</b>:  A transformer can be configured to perform both the
 * XML instance-to-Java attribute transformation at unmarshall time (via attribute transformer) and
 * the Java attribute-to-XML instance transformation at marshal time (via field transformer).
 *
 * <!--
 *    <?xml version="1.0" encoding="UTF-8"?>
 *    <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *        <xsd:element name="employee" type="employee-type"/>
 *        <xsd:complexType name="employee-type">
 *            <xsd:sequence>
 *                <xsd:element name="name" type="xsd:string"/>
 *                <xsd:element name="normal-hours" type="normal-hours-type"/>
 *            </xsd:sequence>
 *        </xsd:complexType>
 *        <xsd:complexType name="normal-hours-type">
 *            <xsd:sequence>
 *                <xsd:element name="start-time" type="xsd:string"/>
 *                <xsd:element name="end-time" type="xsd:string"/>
 *            </xsd:sequence>
 *        </xsd:complexType>
 *    </xsd:schema>
 * -->
 *
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
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLTransformationMapping mapping = new XMLTransformationMapping();<br>
 * mapping.setAttributeName("normalHours");<br>
 * mapping.setAttributeTransformerClassName("org.eclipse.persistence.testing.oxm.mappings.transformation.NormalHoursAttributeTransformer");<br>
 * mapping.addFieldTransformer("normal-hours/start-time/text()", new StartTimeTransformer());<br>
 * mapping.addFieldTransformer("normal-hours/end-time/text()", new EndTimeTransformer()");<br>
 * </code>
 *
 * <p><b>More Information</b>: For more information about using the XML Transformation Mapping, see
 * the "Understanding XML Mappings" chapter of the Oracle TopLink Developer's Guide.
 *
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
 public class XMLTransformationMapping extends AbstractTransformationMapping implements TransformationMapping<AbstractSession, AttributeAccessor, ContainerPolicy, ClassDescriptor, DatabaseField, XMLTransformationRecord, XMLRecord>, XMLMapping {
    public XMLTransformationMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isXMLMapping() {
        return true;
    }

    public void addFieldTransformer(String fieldName, FieldTransformer transformer) {
        this.addFieldTransformer(new XMLField(fieldName), transformer);
    }

    public void addFieldTransformerClassName(String fieldName, String className) {
        this.addFieldTransformerClassName(new XMLField(fieldName), className);
    }

    public void addFieldTransformation(String fieldName, String methodName) {
        this.addFieldTransformation(new XMLField(fieldName), methodName);
    }
    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {
        this.writeFromObjectIntoRow(parent, row, session, WriteType.UNDEFINED);
    }

    public void setIsWriteOnly(boolean b) {
        // no op for this mapping.
        // A Transformation Mapping is write only if it has no Attribute Transformations.
    }

    public void preInitialize(AbstractSession session) throws DescriptorException {
        getAttributeAccessor().setIsWriteOnly(this.isWriteOnly());
        getAttributeAccessor().setIsReadOnly(this.isReadOnly());
        super.preInitialize(session);
    }

    /**
     * INTERNAL:
     * Put value into a record keyed on field.
     * @since EclipseLink 2.6.0
     */
    @Override
    public void writeFromAttributeIntoRow(UnmarshalRecord unmarshalRecord, Field field, Object value, boolean isElement) {
        if (null == unmarshalRecord.getTransformationRecord()) {
            unmarshalRecord.setTransformationRecord(new XMLTransformationRecord("ROOT", unmarshalRecord));
        }
        Field toWrite = field;
        if(isElement) {
            boolean isCDATA = unmarshalRecord.isBufferCDATA();
            if(field.isCDATA() != isCDATA) {
                toWrite = new XMLField(field.getName());
                toWrite.setNamespaceResolver(field.getNamespaceResolver());
                toWrite.setIsCDATA(isCDATA);
            }
        }
        unmarshalRecord.getTransformationRecord().put(toWrite, value);
     }

    /**
     * INTERNAL:
     * Extract value from the row and set the attribute to the value in the object.
     * @since EclipseLink 2.6.0
     */
    @Override
    public Object readFromRowIntoObject(XMLRecord row, Object object, AbstractSession executionSession, boolean isTargetProtected) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSession(executionSession);
        return readFromRowIntoObject(row, null, object, null, query, executionSession, isTargetProtected);
    }

}
