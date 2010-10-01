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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;
import java.util.List;
import org.w3c.dom.Element;

// Java extension imports
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatModel;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_TAG;
import static org.eclipse.persistence.internal.xr.Util.sqlToXmlName;
import static org.eclipse.persistence.internal.xr.Util.TEMP_DOC;
import static org.eclipse.persistence.oxm.XMLConstants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.DATE_TIME_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.TIME_QNAME;

/**
 * <p><b>INTERNAL:</b>An XR QueryOperation is an executable representation of a <tt>SELECT</tt>
 * operation on the database.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings({"serial", "unchecked"/*, "rawtypes"*/})
public class QueryOperation extends Operation {

    protected Result result;
    protected QueryHandler queryHandler;
    protected boolean userDefined = true;

    public QueryOperation() {
        super();
    }

    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }

    public QueryHandler getQueryHandler() {
        return queryHandler;
    }
    public void setQueryHandler(QueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    public boolean isUserDefined() {
        return userDefined;
    }
    public void setUserDefined(boolean userDefined) {
        this.userDefined = userDefined;
    }

    @Override
    public boolean isCollection() {
        return result.isCollection();
    }

    public boolean isSimpleXMLFormat() {
        return result != null && result.getSimpleXMLFormat() != null;
    }

    public boolean isAttachment() {
        return result != null && result.getAttachment() != null;
    }

    public QName getResultType() {
        if (result != null) {
            return result.getType();
        }
        return null;
    }

    @Override
    public boolean hasResponse() {
        return result != null;
    }

    @Override
    public void validate(XRServiceAdapter xrService) {
        super.validate(xrService);
        QName resultType = result == null ? null : result.getType();
        if (resultType != null) {
            if (!resultType.getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
                boolean sxf = resultType.getLocalPart().equals(DEFAULT_SIMPLE_XML_FORMAT_TAG) ||
                    resultType.getLocalPart().equals("cursor of " + DEFAULT_SIMPLE_XML_FORMAT_TAG);
                // check descriptor for Schema's high-level element type 'resultType'
                if (!sxf && !xrService.descriptorsByQName.containsKey(resultType)) {
                        throw DBWSException.resultHasNoMapping(resultType.toString(), name);
                }
            }
        }
        if (queryHandler != null) {
            queryHandler.validate(xrService, this);
        }
    }


    public class DataHandlerInstantiationPolicy extends InstantiationPolicy {
        protected String mimeType;
        public DataHandlerInstantiationPolicy(String mimeType) {
            super();
            this.mimeType = mimeType;
        }
        @Override
        public Object buildNewInstance() throws DescriptorException {
            return new DataHandler(null, mimeType);
        }
    }

    @Override
    public void initialize(XRServiceAdapter xrService) {
        super.initialize(xrService);
        if (queryHandler == null) {
            // session query instead of named query
            DatabaseQuery dq = xrService.getORSession().getQuery(name);
            if (dq != null) {
                queryHandler = new QueryHandler(){
                    public void initializeDatabaseQuery(XRServiceAdapter xrService, QueryOperation queryOperation) {
                        // do nothing
                    }
                    public void initializeArguments(XRServiceAdapter xrService,
                        QueryOperation queryOperation, DatabaseQuery databaseQuery) {
                        // do nothing
                    }
                    public void initializeCall(XRServiceAdapter xrService,
                        QueryOperation queryOperation, DatabaseQuery databaseQuery) {
                        // do nothing
                    }
                };
                queryHandler.setDatabaseQuery(dq);
            }
        }
        if (queryHandler == null) {
            throw DBWSException.couldNotLocateQueryForSession(name,
                xrService.getORSession().getName());
        }
        queryHandler.initialize(xrService, this);
        Session oxSession = xrService.getOXSession();
        QName resultType = result == null ? null : result.getType();
        addSimpleXMLFormatModelDescriptor(xrService);
        addValueObjectDescriptor(xrService);
        if (resultType == null) {
            if (isAttachment()) {
                Attachment attachment = result.getAttachment();
                XMLDescriptor descriptor =
                    (XMLDescriptor)oxSession.getProject().getClassDescriptor(DataHandler.class);
                if (descriptor == null) {
                    descriptor = new XMLDescriptor();
                    descriptor.setAlias("DataHandler");
                    descriptor.setJavaClass(DataHandler.class);
                    descriptor.setInstantiationPolicy(
                        this.new DataHandlerInstantiationPolicy(attachment.getMimeType()));
                    XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
                    mapping.setAttributeName("results");
                    mapping.setAttributeAccessor(new AttributeAccessor() {
                        @Override
                        public Object getAttributeValueFromObject(Object object)
                            throws DescriptorException {
                            Object result = null;
                            DataHandler dataHandler = (DataHandler)object;
                            try {
                                result = dataHandler.getContent();
                                if (result instanceof InputStream) {
                                    InputStream is = (InputStream)result;
                                    byte[] buf = new byte[2048];
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    int bytesRead = is.read(buf);
                                    while (bytesRead >= 0) {
                                        baos.write(buf, 0, bytesRead);
                                        bytesRead = is.read(buf);
                                    }
                                    result = baos.toByteArray();
                                }
                            }
                            catch (IOException e) {
                                // e.printStackTrace(); ignore
                            }
                            return result;
                        }
                        @Override
                        public void setAttributeValueInObject(Object object, Object value)
                            throws DescriptorException {
                            // TODO - figure out if inbound-path needs to be handled
                        }
                    });
                    mapping.setXPath(DEFAULT_SIMPLE_XML_FORMAT_TAG + "/" +
                        DEFAULT_SIMPLE_XML_TAG + "/attachment");
                    mapping.setSwaRef(true);
                    mapping.setShouldInlineBinaryData(false);
                    mapping.setMimeType(attachment.getMimeType());
                    descriptor.addMapping(mapping);
                    NamespaceResolver nr = new NamespaceResolver();
                    descriptor.setNamespaceResolver(nr);
                    oxSession.getProject().addDescriptor(descriptor);
                    ((DatabaseSessionImpl)oxSession)
                        .initializeDescriptorIfSessionAlive(descriptor);
                    xrService.getXMLContext().storeXMLDescriptorByQName(descriptor);
                }
            }
        }
    }

    protected void addValueObjectDescriptor(XRServiceAdapter xrService) {
        Session oxSession = xrService.getOXSession();
        XMLDescriptor descriptor = (XMLDescriptor)oxSession.getProject().getClassDescriptor(
            ValueObject.class);
        if (descriptor == null) {
            descriptor = new XMLDescriptor();
            descriptor.setAlias("ValueObject");
            descriptor.setJavaClass(ValueObject.class);
            XMLDirectMapping mapping = new XMLDirectMapping();
            mapping.setAttributeName("value");
            mapping.setXPath("value");
            descriptor.addMapping(mapping);
            NamespaceResolver nr = new NamespaceResolver();
            descriptor.setNamespaceResolver(nr);
            oxSession.getProject().addDescriptor(descriptor);
            ((DatabaseSessionImpl)oxSession)
                .initializeDescriptorIfSessionAlive(descriptor);
            xrService.getXMLContext().storeXMLDescriptorByQName(descriptor);
        }
    }

    protected void addSimpleXMLFormatModelDescriptor(XRServiceAdapter xrService) {
        if (isSimpleXMLFormat()) {
            Session oxSession = xrService.getOXSession();
            XMLDescriptor simpleXMLFormatDescriptor = (XMLDescriptor)oxSession.
                getProject().getClassDescriptor(SimpleXMLFormatModel.class);
            if (simpleXMLFormatDescriptor == null) {
                simpleXMLFormatDescriptor = new XMLDescriptor();
                simpleXMLFormatDescriptor.setJavaClass(SimpleXMLFormatModel.class);
                simpleXMLFormatDescriptor.setAlias(DEFAULT_SIMPLE_XML_FORMAT_TAG);
                simpleXMLFormatDescriptor.setDefaultRootElement(DEFAULT_SIMPLE_XML_FORMAT_TAG);
                XMLFragmentCollectionMapping xmlTag = new XMLFragmentCollectionMapping();
                xmlTag.setAttributeName("simpleXML");
                xmlTag.setXPath(DEFAULT_SIMPLE_XML_TAG);
                simpleXMLFormatDescriptor.addMapping(xmlTag);
                NamespaceResolver nr = new NamespaceResolver();
                simpleXMLFormatDescriptor.setNamespaceResolver(nr);
                XMLSchemaURLReference schemaReference = new XMLSchemaURLReference("");
                schemaReference.setSchemaContext("/any");
                schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
                simpleXMLFormatDescriptor.setSchemaReference(schemaReference);
                oxSession.getProject().addDescriptor(simpleXMLFormatDescriptor);
                ((DatabaseSessionImpl)oxSession)
                    .initializeDescriptorIfSessionAlive(simpleXMLFormatDescriptor);
                xrService.getXMLContext().storeXMLDescriptorByQName(simpleXMLFormatDescriptor);
            }
        }
    }

    /**
     * Execute <tt>SELECT</tt> operation on the database
     * @param   xrService parent <code>XRService</code> that owns this <code>Operation</code>
     * @param   invocation contains runtime argument values to be bound to the list of
     *          {@link Parameter}'s.
     * @return  result - the result of the underlying <tt>SELECT</tt> operation on
     *          the database, or <code>null</code>.
     *
     * @see  {@link Operation}
     */
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {

        DatabaseQuery query = queryHandler.getDatabaseQuery();
        List queryArguments = query.getArguments();
        int queryArgumentsSize = queryArguments.size();
        Vector executeArguments = new NonSynchronizedVector();
        for (int i = 0; i < queryArgumentsSize; i++) {
            String argName = (String)queryArguments.get(i);
            executeArguments.add(invocation.getParameter(argName));
        }
        Object value = xrService.getORSession().getActiveSession().executeQuery(query,
            executeArguments);
        if (value != null) {
            if (isSimpleXMLFormat()) {
                value = createSimpleXMLFormat(xrService, value);
            }
            else {
                QName resultType = getResultType();
                // handle binary content
                if (isAttachment() ||
                    (!isCollection() && resultType.equals(BASE_64_BINARY_QNAME))) {
                    String mimeType = DEFAULT_ATTACHMENT_MIMETYPE;
                    if (isAttachment() && result.getAttachment().getMimeType() != null) {
                        mimeType = result.getAttachment().getMimeType();
                    }
                    return AttachmentHelper.buildAttachmentHandler((byte[])value, mimeType);
                }
                if (resultType != null) {
                    if (resultType.getNamespaceURI().equals(W3C_XML_SCHEMA_NS_URI)) {
                        // handle primitive types
                        ValueObject vo = new ValueObject();
                        vo.value = value;
                        value = vo;
                    }
                    else {
                        Object targetObject = value;
                        if (xrService.descriptorsByQName.containsKey(resultType)) {
                            XMLDescriptor xdesc = xrService.descriptorsByQName.get(resultType);
                            ClassDescriptor desc = xrService.getORSession().getDescriptorForAlias(
                                xdesc.getAlias());
                            if (desc.isAggregateDescriptor() && !desc.isObjectRelationalDataTypeDescriptor()) {
                                if (isCollection()) {
                                    XRDynamicEntity_CollectionWrapper xrCollWrapper =
                                        new XRDynamicEntity_CollectionWrapper();
                                    Vector<AbstractRecord> results = (Vector<AbstractRecord>)value;
                                    for (int i = 0, len = results.size(); i < len; i++) {
                                        Object o = desc.getObjectBuilder().buildNewInstance();
                                        populateTargetObjectFromRecord(desc.getMappings(),
                                            results.get(i), o, (AbstractSession)xrService.getORSession());
                                        xrCollWrapper.add(o);
                                    }
                                    targetObject = xrCollWrapper;
                                }
                                else {
                                    targetObject = desc.getObjectBuilder().buildNewInstance();
                                    populateTargetObjectFromRecord(desc.getMappings(),
                                        (AbstractRecord)((Vector)value).get(0), targetObject,
                                            (AbstractSession)xrService.getORSession());
                                }
                            }
                        }
                        value = targetObject;
                    }
                }
            }
        }
        return value;
    }

    protected void populateTargetObjectFromRecord(Vector<DatabaseMapping> mappings,
        AbstractRecord record, Object targetObject, AbstractSession session) {
        for (DatabaseMapping dm : mappings) {
            dm.readFromRowIntoObject(record, null, targetObject, null, session);
        }
    }

    public Object createSimpleXMLFormat(XRServiceAdapter xrService, Object value) {
        XMLRoot xmlRoot = new XMLRoot();
        SimpleXMLFormat simpleXMLFormat = result.getSimpleXMLFormat();
        String tempSimpleXMLFormatTag = SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
        String simpleXMLFormatTag = simpleXMLFormat.getSimpleXMLFormatTag();
        if (simpleXMLFormatTag != null && !"".equals(simpleXMLFormatTag)) {
            tempSimpleXMLFormatTag = simpleXMLFormatTag;
        }
        xmlRoot.setLocalName(tempSimpleXMLFormatTag);
        String tempXMLTag = DEFAULT_SIMPLE_XML_TAG;
        String xmlTag = simpleXMLFormat.getXMLTag();
        if (xmlTag != null && !"".equals(xmlTag)) {
            tempXMLTag = xmlTag;
        }
        Vector<DatabaseRecord> records = null;
        if (value instanceof Vector) {
            records = (Vector<DatabaseRecord>)value;
        }
        else {
            records = new Vector<DatabaseRecord>();
            DatabaseRecord dr = new DatabaseRecord();
            dr.add(new DatabaseField("result"), value);
            records.add(dr);
        }
        SimpleXMLFormatModel simpleXMLFormatModel = new SimpleXMLFormatModel();
        XMLConversionManager conversionManager =
            (XMLConversionManager) xrService.getOXSession().getDatasourcePlatform().getConversionManager();
        for (DatabaseRecord dr : records) {
            Element rowElement = TEMP_DOC.createElement(tempXMLTag);
            for (DatabaseField field : (Vector<DatabaseField>)dr.getFields()) {
                Object fieldValue = dr.get(field);
                if (fieldValue != null) {
                    if (fieldValue instanceof Calendar) {
                        Calendar cValue = (Calendar)fieldValue;
                        fieldValue = conversionManager.convertObject(cValue, STRING,
                          DATE_TIME_QNAME);
                    }
                    if (fieldValue instanceof Date) {
                        Date dValue = (Date)fieldValue;
                        fieldValue = conversionManager.convertObject(dValue, STRING,
                          DATE_QNAME);
                    }
                    else if (fieldValue instanceof Time) {
                        Time tValue = (Time)fieldValue;
                        fieldValue = conversionManager.convertObject(tValue, STRING,
                          TIME_QNAME);
                    }
                    else if (fieldValue instanceof Timestamp) {
                        Timestamp tsValue = (Timestamp)fieldValue;
                        fieldValue = conversionManager.convertObject(tsValue, STRING,
                          DATE_TIME_QNAME);
                    }
                    String elementName = sqlToXmlName(field.getName());
                    Element columnElement = TEMP_DOC.createElement(elementName);
                    rowElement.appendChild(columnElement);
                    String fieldValueString = fieldValue.toString();
                    // handle binary content - attachments dealt with in invoke() above
                    if (result.getType().equals(BASE_64_BINARY_QNAME)) {
                        fieldValueString = Helper.buildHexStringFromBytes(
                            Base64.base64Encode((byte[])fieldValue));
                        columnElement.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI,
                            "xmlns:xsd", W3C_XML_SCHEMA_NS_URI);
                        columnElement.setAttributeNS(XMLNS_ATTRIBUTE_NS_URI,
                            "xmlns:xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
                        columnElement.setAttributeNS(W3C_XML_SCHEMA_INSTANCE_NS_URI,
                            "xsi:type", "xsd:base64Binary");
                    }
                    columnElement.appendChild(TEMP_DOC.createTextNode(fieldValueString));
                }
            }
            simpleXMLFormatModel.simpleXML.add(rowElement);
        }
        xmlRoot.setObject(simpleXMLFormatModel);
        return xmlRoot;
    }
}
