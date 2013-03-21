/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.oxm.mappings;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.DirectCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.XMLContainerMapping;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.Session;

/**
 * <p>Composite direct collection XML mappings map a collection of simple types (String, Number, Date,
 * etc.) to and from a sequence of composite XML nodes.
 *
 * <p><b>Composite direct collection XML mappings can be used in the following scenarios</b>:<ul>
 * <li> Mapping to Multiple Text Nodes </li>
 * <li> Mapping to Multiple Attributes </li>
 * <li> Mapping to a Single Text Node </li>
 * <li> Mapping to a Single Attribute </li>
 * <li> Mapping to a List of Unions </li>
 * <li> Mapping a Union of Lists </li>
 * <li> Specifying the Content Type of a Collection </li>
 * </ul>
 *
 * <p><b>Setting the XPath</b>: TopLink XML mappings make use of an XPath statement to find the relevant
 * data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
 * The XPath may contain node type, path, and positional information.  The XPath is specified on the
 * mapping using the <code>setXPath</code> method.
 *
 * <p>The following XPath statements may be used to specify the location of XML data relating to an object's
 * name attribute:
 *
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">XPath</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">@tasks</td>
 * <td headers="c2">The "@" character indicates that the node is an attribute.  This XPath applies only to the single
 * attribute node case; each member of the collection is mapped to a single node.</td>
 * </tr>
 * <tr>
 * <td headers="c1">tasks/@task</td>
 * <td headers="c2">The "@" character indicates that the node is an attribute.  The information is stored
 * in the attribute node of the tasks element.</td>
 * </tr>
 * <tr>
 * <td headers="c1">text()</td>
 * <td headers="c2">"text()" indicates that the node is a text node.  In this case the task value in the
 * text node belongs to the context node.</td>
 * </tr>
 * <tr>
 * <td headers="c1">tasks/text()</td>
 * <td headers="c2">The task information is stored in the text node of the tasks element.</td>
 * </tr>
 * <tr>
 * <td headers="c1" nowrap="true">tasks/task/text()</td>
 * <td headers="c2">The XPath statement may be used to specify any valid path.</td>
 * </tr>
 * <tr>
 * <td headers="c1">task[2]/text()</td>
 * <td headers="c2">The XPath statement may contain positional information.  In this case the task
 * information is stored in the text node of the second occurrence of the task element.</td>
 * </tr>
 * </table>
 *
 * <p><b>Mapping to a Single Text Node</b>: By default, TopLink maps each member of a collection
 * to it's own node.  It is possible, however, to mapping a collection to a single node;  here the contents of
 * the node is treated as a space-separated list.  This behavior is set on the mapping using the <code>
 * setUsesSingleNode </code> method, with 'true' as the parameter.
 *
 * <!--
 * <?xml version="1.0" encoding="UTF-8"?><br>
 * <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"><br>
 *     <xsd:element name="employee" type="employee-type"/><br>
 *     <xsd:complexType name="employee-type"><br>
 *         <xsd:sequence><br>
 *             <xsd:element name="tasks" type="tasks-type"/><br>
 *         </xsd:sequence><br>
 *     </xsd:complexType><br>
 *     <xsd:simpleType name="tasks-type"><br>
 *         <xsd:list itemType="xsd:string"/><br>
 *     </xsd:simpleType><br>
 * </xsd:schema><br>
 * -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="employee" type="employee-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="employee-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="tasks" type="tasks-type"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:simpleType name="tasks-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:list itemType="xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:simpleType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLCompositeDirectCollectionMapping tasksMapping = new XMLCompositeDirectCollectionMapping();<br>
 * tasksMapping.setAttributeName("tasks");<br>
 * tasksMapping.setXPath("tasks/text()");<br>
 * tasksMapping.setUsesSingleNode(true);<br>
 * </code>
 *
 * <p><b>Specifying the Content Type of a Collection</b>: By default, TopLink will treat the node values
 * read in by a composite direct collection XML mapping as objects of type String. You can override this behavior
 * by specifying the type of the collection's contents.
 *
 * <!--
 * <?xml version="1.0" encoding="UTF-8"?><br>
 * <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"><br>
 *     <xsd:element name="employee" type="employee-type"/><br>
 *     <xsd:complexType name="employee-type"><br>
 *         <xsd:sequence><br>
 *             <xsd:element name="vacation" type="xsd:string" maxOccurs="unbounded"/><br>
 *         </xsd:sequence><br>
 *     </xsd:complexType><br>
 * </xsd:schema><br>
 *  -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="employee" type="employee-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="employee-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="vacation" type="xsd:string" maxOccurs="unbounded"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLCompositeDirectCollectionMapping tasksMapping = new XMLCompositeDirectCollectionMapping();<br>
 * tasksMapping.setAttributeName("vacationDays");<br>
 * tasksMapping.setXPath("vacation/text()");<br>
 * tasksMapping.setAttributeElementClass(Calendar.class);<br>
 * </code>
 *
 * <p><b>Mapping to a List of Unions</b>:
 *
 * <!--
 * <?xml version="1.0" encoding="UTF-8"?>
 * <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *     <xsd:element name="vacation" type="listOfUnions"/>
 *     <xsd:simpleType name="listOfUnions">
 *         <xsd:list>
 *             <xsd:simpleType>
 *                 <xsd:union memberTypes="xsd:date xsd:integer"/>
 *             </xsd:simpleType>
 *         </xsd:list>
 *     </xsd:simpleType>
 * </xsd:schema>
 * -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="vacation" type="listOfUnions"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:simpleType name="listOfUnions"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:list&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:simpleType&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:union memberTypes="xsd:date xsd:integer"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:simpleType&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:list&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:simpleType&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();<br>
 * mapping.setAttributeName("myattribute");<br>
 * XMLUnionField field = new XMLUnionField("listOfUnions/text()");<br>
 * mapping.addSchemaType(new QName(url,XMLConstants.INT));<br>
 * mapping.addSchemaType(new QName(url,XMLConstants.DATE));<br>
 * mapping.setField(field);<br>
 * mapping.useSingleElement(false);<br>
 * </code>
 *
 * <p><b>More Information</b>: For more information about using the XML Composite Direct
 * Collection Mapping, see the "Understanding XML Mappings" chapter of the Oracle TopLink
 * Developer's Guide.
 *
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class XMLCompositeDirectCollectionMapping extends AbstractCompositeDirectCollectionMapping implements DirectCollectionMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, Session, XMLUnmarshaller, XMLRecord>, XMLMapping, XMLNillableMapping {
    private boolean isCDATA;
    private boolean isDefaultEmptyContainer = XMLContainerMapping.EMPTY_CONTAINER_DEFAULT;
    private boolean isWriteOnly;
    private AbstractNullPolicy nullPolicy;
    protected boolean reuseContainer;
    private boolean isCollapsingStringValues;
    private boolean isNormalizingStringValues;
    private AbstractNullPolicy wrapperNullPolicy;

    public XMLCompositeDirectCollectionMapping() {
        super();
        this.nullPolicy = new NullPolicy();
        this.nullPolicy.setNullRepresentedByEmptyNode(true);
    }

    /**
     * INTERNAL:
     */
    public boolean isXMLMapping() {
        return true;
    }

    /**
     * INTERNAL:
     * Initialize the mapping.
     */
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        if (this.getField() instanceof XMLField) {
            if (valueConverter instanceof TypeConversionConverter) {
                TypeConversionConverter converter = (TypeConversionConverter) valueConverter;
                this.getField().setType(converter.getObjectClass());
            }
            String xpathString = ((XMLField) getField()).getXPath();
            if (this.isAbstractCompositeDirectCollectionMapping() && (xpathString.indexOf(XMLConstants.ATTRIBUTE) == -1) && (!xpathString.endsWith(XMLConstants.TEXT))) {
                throw DescriptorException.invalidXpathForXMLDirectMapping(this);
            }
        }

        ContainerPolicy cp = getContainerPolicy();
        if (cp != null) {
            if (cp.getContainerClass() == null) {
                Class cls = session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(cp.getContainerClassName());
                cp.setContainerClass(cls);
            }
        }
        ((XMLField) this.getField()).setIsCDATA(this.isCDATA());
    }

    /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    public void setXPath(String xpathString) {
        if ((xpathString.indexOf(XMLConstants.ATTRIBUTE) == -1) && (!xpathString.endsWith(XMLConstants.TEXT))) {
            xpathString += '/' + XMLConstants.TEXT;
        }
        setField(new XMLField(xpathString));
    }

    /**
     * Get the XPath String
     * @return String the XPath String associated with this Mapping
     */
    public String getXPath() {
        return getFieldName();
    }

    public void useCollectionClassName(String concreteContainerClassName) {
        this.setContainerPolicy(new CollectionContainerPolicy(concreteContainerClassName));
    }

    /**
     * INTERNAL:
     * Build the nested collection from the database row.
     */
    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
        ContainerPolicy cp = this.getContainerPolicy();

        DOMRecord domRecord = (DOMRecord) row;

        Object fieldValue = domRecord.getValues(this.getField(), this.getNullPolicy());
        if (fieldValue == null) {
            if (reuseContainer) {
                Object currentObject = ((XMLRecord) row).getCurrentObject();
                Object container = getAttributeAccessor().getAttributeValueFromObject(currentObject);
                return container != null ? container : cp.containerInstance();
            } else {
                return cp.containerInstance();
            }
        }

        Vector fieldValues = this.getDescriptor().buildDirectValuesFromFieldValue(fieldValue);
        if (fieldValues == null) {
            if (reuseContainer) {
                Object currentObject = ((XMLRecord) row).getCurrentObject();
                Object container = getAttributeAccessor().getAttributeValueFromObject(currentObject);
                return container != null ? container : cp.containerInstance();
            } else {
                return cp.containerInstance();
            }
        }

        Object result = null;
        if (reuseContainer) {
            Object currentObject = ((XMLRecord) row).getCurrentObject();
            Object container = getAttributeAccessor().getAttributeValueFromObject(currentObject);
            result = container != null ? container : cp.containerInstance();
        } else {
            result = cp.containerInstance(fieldValues.size());
        }

        for (Enumeration stream = fieldValues.elements(); stream.hasMoreElements();) {
            Object element = stream.nextElement();
            element = convertDataValueToObjectValue(element, executionSession, ((XMLRecord) row).getUnmarshaller());
            if (element != null && element.getClass() == ClassConstants.STRING) {
                if (isCollapsingStringValues) {
                    element = XMLConversionManager.getDefaultXMLManager().collapseStringValue((String)element);
                } else if(isNormalizingStringValues) {
                    element = XMLConversionManager.getDefaultXMLManager().normalizeStringValue((String)element);
                }
            }
            cp.addInto(element, result, sourceQuery.getSession());
        }
        return result;
    }

    /**
     * INTERNAL:
     */
    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) {
        if (this.isReadOnly()) {
            return;
        }

        Object attributeValue = this.getAttributeValueFromObject(object);
        if (attributeValue == null) {
            row.put(this.getField(), null);
            return;
        }

        ContainerPolicy cp = this.getContainerPolicy();

        Vector elements = new Vector(cp.sizeFor(attributeValue));
        Object iter = cp.iteratorFor(attributeValue);
        if (null != iter) {
            while (cp.hasNext(iter)) {
                Object element = cp.next(iter, session);
                element = convertObjectValueToDataValue(element, session, ((XMLRecord) row).getMarshaller());

                if (element != null) {
                    elements.addElement(element);
                } else if(!usesSingleNode()){
                    AbstractNullPolicy nullPolicy = getNullPolicy();
                    if (nullPolicy == null) {
                        elements.addElement(null);
                    } else {
                        if (nullPolicy.getMarshalNullRepresentation() == XMLNullRepresentationType.XSI_NIL) {
                            elements.addElement(XMLRecord.NIL);
                        } else if (nullPolicy.getMarshalNullRepresentation() == XMLNullRepresentationType.ABSENT_NODE) {
                            // Do nothing
                        } else {
                            elements.addElement(XMLConstants.EMPTY_STRING);
                        }
                    }
                }
            }
        }

        Object fieldValue = null;
        if (!elements.isEmpty()) {
            fieldValue = this.getDescriptor().buildFieldValueFromDirectValues(elements, elementDataTypeName, session);
        }
        row.put(this.getField(), fieldValue);
    }

    public void writeSingleValue(Object value, Object parent, XMLRecord record, AbstractSession session) {
        Object element = convertObjectValueToDataValue(value, session, record.getMarshaller());
        record.add(this.getField(), element);
    }

    public void setIsCDATA(boolean CDATA) {
        isCDATA = CDATA;
    }

    public boolean isCDATA() {
        return isCDATA;
    }

    public void setIsWriteOnly(boolean b) {
        this.isWriteOnly = b;
    }

    public boolean isWriteOnly() {
        return isWriteOnly;
    }

    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        if (isWriteOnly()) {
            return;
        }
        super.setAttributeValueInObject(object, value);
    }

    public void preInitialize(AbstractSession session) throws DescriptorException {
        getAttributeAccessor().setIsWriteOnly(this.isWriteOnly());
        getAttributeAccessor().setIsReadOnly(this.isReadOnly());
        super.preInitialize(session);
    }

    public AbstractNullPolicy getNullPolicy() {
        return this.nullPolicy;
    }

    public void setNullPolicy(AbstractNullPolicy value) {
        this.nullPolicy = value;
    }

    /**
     * Return true if the original container on the object should be used if
     * present.  If it is not present then the container policy will be used to
     * create the container.
     */
    public boolean getReuseContainer() {
        return reuseContainer;
    }

    /**
     * Specify whether the original container on the object should be used if
     * present.  If it is not present then the container policy will be used to
     * create the container.
     */
    public void setReuseContainer(boolean reuseContainer) {
        this.reuseContainer = reuseContainer;
    }

    /**
     * PUBLIC:
     * Returns true if this mapping is normalizing string values on unmarshal before adding
     * them to the collection. Normalize replaces any CR, LF or Tab characters with a
     * single space character.
     */
    public boolean isNormalizingStringValues() {
        return this.isNormalizingStringValues;
    }

    /**
     * PUBLIC:
     * Indicates that this mapping should normalize all string values before adding them
     * to the collection on unmarshal. Normalize replaces any CR, LF or Tab characters with a
     * single space character.
     * @param normalize
     */
    public void setNormalizingStringValues(boolean normalize) {
        this.isNormalizingStringValues = normalize;
    }


    /**
     * PUBLIC:
     * Indicates that this mapping should collapse all string values before adding them
     * to the collection on unmarshal. Collapse removes leading and trailing whitespaces, and replaces
     * any sequence of whitespace characters with a single space.
     * @param normalize
     */
    public void setCollapsingStringValues(boolean collapse) {
        this.isCollapsingStringValues = collapse;
    }

    /**
     * PUBLIC:
     * Returns true if this mapping should collapse all string values before adding them
     * to the collection. Collapse removes leading and trailing whitespaces, and replaces
     * any sequence of whitespace characters with a single space.
     * @param normalize
     */
    public boolean isCollapsingStringValues() {
        return this.isCollapsingStringValues;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return isDefaultEmptyContainer;
    }

    /**
     * INTERNAL
     * Indicate whether by default an empty container should be set on the
     * field/property if the collection is not present in the XML document.
     * @since EclipseLink 2.3.3
     */
    public void setDefaultEmptyContainer(boolean defaultEmptyContainer) {
        this.isDefaultEmptyContainer = defaultEmptyContainer;
    }

    public AbstractNullPolicy getWrapperNullPolicy() {
        return this.wrapperNullPolicy;
    }

    public void setWrapperNullPolicy(AbstractNullPolicy policy) {
        this.wrapperNullPolicy = policy;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertObjectValueToDataValue(Object value, Session session, XMLMarshaller marshaller) {
        if (hasValueConverter()) {
            if (valueConverter instanceof XMLConverter) {
                return ((XMLConverter)valueConverter).convertObjectValueToDataValue(value, session, marshaller);
            } else {
                return valueConverter.convertObjectValueToDataValue(value, session);
            }
        }
        return value;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertDataValueToObjectValue(Object value, Session session, XMLUnmarshaller unmarshaller) {
        if (hasValueConverter()) {
            if (valueConverter instanceof XMLConverter) {
                return ((XMLConverter)valueConverter).convertDataValueToObjectValue(value, session, unmarshaller);
            } else {
                return valueConverter.convertDataValueToObjectValue(value, session);
            }
        }
        return value;
    }

}