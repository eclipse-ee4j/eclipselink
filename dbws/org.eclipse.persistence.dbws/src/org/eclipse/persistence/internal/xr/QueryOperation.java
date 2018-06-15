/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports
import static org.eclipse.persistence.internal.helper.ClassConstants.STRING;
import static org.eclipse.persistence.internal.oxm.Constants.BASE_64_BINARY_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.DATE_TIME_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.INT_QNAME;
import static org.eclipse.persistence.internal.oxm.Constants.TIME_QNAME;
import static org.eclipse.persistence.internal.xr.Util.DEFAULT_ATTACHMENT_MIMETYPE;
import static org.eclipse.persistence.internal.xr.Util.EMPTY_STR;
import static org.eclipse.persistence.internal.xr.Util.SLASH_CHAR;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
import static org.eclipse.persistence.internal.xr.Util.TEMP_DOC;
import static org.eclipse.persistence.internal.xr.Util.sqlToXmlName;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_INSTANCE_URL;
import static org.eclipse.persistence.oxm.XMLConstants.SCHEMA_URL;
import static org.eclipse.persistence.oxm.XMLConstants.XMLNS_URL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

// Java extension imports
import javax.activation.DataHandler;
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.databaseaccess.OutputParameterForCallableStatement;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.conversion.Base64;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.internal.security.PrivilegedGetDeclaredMethod;
import org.eclipse.persistence.internal.security.PrivilegedInvokeConstructor;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatModel;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;

/**
 * <p><b>INTERNAL:</b>An XR QueryOperation is an executable representation of a <tt>SELECT</tt>
 * operation on the database.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings({"serial", "unchecked"/*, "rawtypes"*/})
public class QueryOperation extends Operation {
    public static final String ORACLEOPAQUE_STR = "oracle.sql.OPAQUE";
    private static final String IORACLEOPAQUE_STR = "oracle.jdbc.OracleOpaque";
    protected static final String RESULT_STR = "result";
    protected static final String XMLTYPEFACTORY_STR = "org.eclipse.persistence.internal.platform.database.oracle.xdb.XMLTypeFactoryImpl";
    protected static final String GETSTRING_METHOD = "getString";
    protected static final String ATTACHMENT_STR = "/attachment";
    protected static final String CURSOR_OF_STR = "cursor of ";
    protected static final String DATAHANDLER_STR = "DataHandler";
    protected static final String RESULTS_STR = "results";
    protected static final String VALUEOBJECT_STR = "ValueObject";
    protected static final String VALUE_STR = "value";
    protected static final String SIMPLEXML_FORMAT_STR = "/simple-xml-format";
    protected static final String SIMPLEXML_STR = "simpleXML";
    protected static final String DATABASEQUERY_STR = "databasequery";
    protected static final String ITEMS_STR = "ITEMS";
    protected static final String XSD_STR = "xmlns:xsd";
    protected static final String XSI_STR = "xmlns:xsi";
    protected static final String XSITYPE_STR = "xsi:type";
    protected static final String BASE64_BINARY_STR = "xsd:base64Binary";

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
        return result != null && result.isCollection();
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
            if (!resultType.getNamespaceURI().equals(SCHEMA_URL)) {
                boolean sxf = resultType.getLocalPart().equals(DEFAULT_SIMPLE_XML_FORMAT_TAG) ||
                    resultType.getLocalPart().equals(CURSOR_OF_STR + DEFAULT_SIMPLE_XML_FORMAT_TAG);
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

    // Made static final for performance reasons.
    private static final class DataHandlerInstantiationPolicy extends InstantiationPolicy {
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
                    @Override
                    public void initializeDatabaseQuery(XRServiceAdapter xrService, QueryOperation queryOperation) {
                        // do nothing
                    }
                    @Override
                    public void initializeArguments(XRServiceAdapter xrService,
                        QueryOperation queryOperation, DatabaseQuery databaseQuery) {
                        // do nothing
                    }
                    @Override
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
                    descriptor.setAlias(DATAHANDLER_STR);
                    descriptor.setJavaClass(DataHandler.class);
                    descriptor.setInstantiationPolicy(new DataHandlerInstantiationPolicy(attachment.getMimeType()));
                    XMLBinaryDataMapping mapping = new XMLBinaryDataMapping();
                    mapping.setAttributeName(RESULTS_STR);
                    mapping.setAttributeAccessor(new AttributeAccessor() {
                        @Override
                        public Object getAttributeValueFromObject(Object object)
                            throws DescriptorException {
                            Object result = null;
                            DataHandler dataHandler = (DataHandler)object;
                            try {
                                result = dataHandler.getContent();
                                if (result instanceof InputStream) {
                                    try (InputStream is = (InputStream) result) {
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
                            } catch (IOException e) {
                                // ignore
                            }
                            return result;
                        }
                        @Override
                        public void setAttributeValueInObject(Object object, Object value)
                            throws DescriptorException {
                        }
                    });
                    mapping.setXPath(DEFAULT_SIMPLE_XML_FORMAT_TAG + SLASH_CHAR +
                        DEFAULT_SIMPLE_XML_TAG + ATTACHMENT_STR);
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
            descriptor.setAlias(VALUEOBJECT_STR);
            descriptor.setJavaClass(ValueObject.class);
            XMLDirectMapping mapping = new XMLDirectMapping();
            mapping.setAttributeName(VALUE_STR);
            mapping.setXPath(VALUE_STR);
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
                xmlTag.setAttributeName(SIMPLEXML_STR);
                xmlTag.setXPath(DEFAULT_SIMPLE_XML_TAG);
                simpleXMLFormatDescriptor.addMapping(xmlTag);
                NamespaceResolver nr = new NamespaceResolver();
                simpleXMLFormatDescriptor.setNamespaceResolver(nr);
                XMLSchemaURLReference schemaReference = new XMLSchemaURLReference(EMPTY_STR);
                schemaReference.setSchemaContext(SIMPLEXML_FORMAT_STR);
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
     * @see  Operation
     */
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        DatabaseQuery query = queryHandler.getDatabaseQuery();

        if (query.getProperty(DATABASEQUERY_STR) != null) {
            query = (DatabaseQuery) query.getProperty(DATABASEQUERY_STR);
        }

        // a named query created via ORM metadata processing does not have
        // parameters set, however, the operation should
        List<Object> argVals = new ArrayList<>();
        if (query.getArguments().size() == 0) {
            int idx = 0;
            for (Parameter param : getParameters()) {
                // for custom SQL query (as configured via ORM metadata
                // processing) we add args by position
                query.addArgument(Integer.toString(++idx), Util.SCHEMA_2_CLASS.get(param.getType()));
                argVals.add(invocation.getParameter(param.getName()));
            }
        } else {
            // need to set argument values
            for (Parameter param : getParameters()) {
                argVals.add(invocation.getParameter(param.getName()));
            }
        }
        // for SimpleXML + DataReadQuery we need to set MAP result type
        if (isSimpleXMLFormat() && query.isDataReadQuery()) {
            ((DataReadQuery) query).setResultType(DataReadQuery.MAP);
        }

        // now execute the query
        Object value = xrService.getORSession().getActiveSession().executeQuery(query, argVals);

        if (value != null) {
            // a recent change in core results in an empty vector being returned in cases
            // where before we'd expect an int value (typically 1) - need to handle this
            if (result != null && (result.getType() == INT_QNAME || result.getType().equals(SXF_QNAME))) {
                if (value instanceof ArrayList && ((ArrayList<?>) value).isEmpty()) {
                    ((ArrayList<Integer>) value).add(1);
                } else  if (value instanceof Vector && ((Vector<?>) value).isEmpty()) {
                    ((Vector<Integer>) value).add(1);
                }
            }

            // JPA spec returns an ArrayList<Object[]> for stored procedure queries - will need to unwrap.
            // Note that for legacy deployment XML projects this is not the case.
            if (value instanceof ArrayList) {
                ArrayList<?> returnedList = (ArrayList<?>) value;
                if (returnedList.size() > 0 && returnedList.get(0) instanceof Object[]) {
                    Object[] objs = (Object[]) returnedList.get(0);
                    if (isCollection()) {
                        value = new ArrayList<Object>();
                        for (Object obj : objs) {
                            ((ArrayList<Object>) value).add(obj);
                        }
                    } else {
                        value = objs[0];
                    }
                }
            }

            // handle SimpleXML
            if (isSimpleXMLFormat()) {
                value = createSimpleXMLFormat(xrService, value);
            } else {
                if (!isCollection() && value instanceof Vector) {
                    // JPAQuery will return a single result in a Vector
                    if (((Vector<?>) value).isEmpty()) {
                        return null;
                    }
                    value = ((Vector<?>) value).firstElement();
                }

                QName resultType = getResultType();
                if (resultType != null) {
                    // handle binary content
                    if (isAttachment() || (!isCollection() && resultType.equals(BASE_64_BINARY_QNAME))) {
                        String mimeType = DEFAULT_ATTACHMENT_MIMETYPE;
                        if (isAttachment() && result.getAttachment().getMimeType() != null) {
                            mimeType = result.getAttachment().getMimeType();
                        }
                        // handle BLOB types
                        if (value instanceof Blob) {
                            value = ((XMLConversionManager) xrService.getOXSession().
                                    getDatasourcePlatform().getConversionManager()).
                                    convertObject(value, ClassConstants.APBYTE);
                        }
                        return AttachmentHelper.buildAttachmentHandler((byte[])value, mimeType);
                    }
                    if (resultType.getNamespaceURI().equals(SCHEMA_URL)) {
                        // handle primitive types
                        ValueObject vo = new ValueObject();
                        vo.value = value;
                        value = vo;
                    } else {
                        Object targetObject = value;
                        if (xrService.descriptorsByQName.containsKey(resultType)) {
                            XMLDescriptor xdesc = xrService.descriptorsByQName.get(resultType);
                            ClassDescriptor desc = xrService.getORSession().getDescriptorForAlias(xdesc.getAlias());
                            if (desc.isAggregateDescriptor() && !desc.isObjectRelationalDataTypeDescriptor() && !desc.isRelationalDescriptor()) {
                                if (isCollection()) {
                                    XRDynamicEntity_CollectionWrapper xrCollWrapper = new XRDynamicEntity_CollectionWrapper();
                                    Vector<AbstractRecord> results = (Vector<AbstractRecord>)value;
                                    for (int i = 0, len = results.size(); i < len; i++) {
                                        Object o = desc.getObjectBuilder().buildNewInstance();
                                        populateTargetObjectFromRecord(desc.getMappings(), results.get(i), o, (AbstractSession)xrService.getORSession());
                                        xrCollWrapper.add(o);
                                    }
                                    targetObject = xrCollWrapper;
                                } else {
                                    targetObject = desc.getObjectBuilder().buildNewInstance();
                                    populateTargetObjectFromRecord(desc.getMappings(), (AbstractRecord) value, targetObject, (AbstractSession)xrService.getORSession());
                                }
                            } else if (isCollection() && value instanceof Vector) {
                                // could be a collection of populated objects, in which case we just return it
                                if (((Vector<?>) value).size() > 0 && !(((Vector<?>) value).get(0) instanceof AbstractRecord)) {
                                    return value;
                                }
                                XRDynamicEntity_CollectionWrapper xrCollWrapper = new XRDynamicEntity_CollectionWrapper();
                                Vector<AbstractRecord> results = (Vector<AbstractRecord>)value;
                                for (int i = 0, len = results.size(); i < len; i++) {
                                    Object o = desc.getObjectBuilder().buildNewInstance();
                                    populateTargetObjectFromRecord(desc.getMappings(), results.get(i), o, (AbstractSession)xrService.getORSession());
                                    xrCollWrapper.add(o);
                                }
                                targetObject = xrCollWrapper;
                            } else if (value instanceof AbstractRecord) {
                                targetObject = desc.getObjectBuilder().buildNewInstance();
                                populateTargetObjectFromRecord(desc.getMappings(), (AbstractRecord) value, targetObject, (AbstractSession)xrService.getORSession());
                            }
                        }
                        if (value instanceof ArrayList) {
                            XMLDescriptor xdesc = xrService.descriptorsByQName.get(resultType);
                            ClassDescriptor desc = xrService.getORSession().getDescriptorForAlias(xdesc.getAlias());
                            targetObject = desc.getObjectBuilder().buildNewInstance();
                            Object[] objs = new Object[1];
                            objs[0] = ((ArrayList<?>)value).get(0);
                            DatabaseRecord dr = new DatabaseRecord();
                            dr.add(new DatabaseField(ITEMS_STR), objs);
                            populateTargetObjectFromRecord(desc.getMappings(), (AbstractRecord) dr, targetObject, (AbstractSession)xrService.getORSession());
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
        ReadObjectQuery roq = new ReadObjectQuery();
        roq.setSession(session);
        for (DatabaseMapping dm : mappings) {
            dm.readFromRowIntoObject(record, null, targetObject, null, roq, session, true);
        }
    }

    public Object createSimpleXMLFormat(XRServiceAdapter xrService, Object value) {
        XMLRoot xmlRoot = new XMLRoot();
        SimpleXMLFormat simpleXMLFormat = result.getSimpleXMLFormat();
        String tempSimpleXMLFormatTag = SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
        String simpleXMLFormatTag = simpleXMLFormat.getSimpleXMLFormatTag();
        if (simpleXMLFormatTag != null && !EMPTY_STR.equals(simpleXMLFormatTag)) {
            tempSimpleXMLFormatTag = simpleXMLFormatTag;
        }
        xmlRoot.setLocalName(tempSimpleXMLFormatTag);
        String tempXMLTag = DEFAULT_SIMPLE_XML_TAG;
        String xmlTag = simpleXMLFormat.getXMLTag();
        if (xmlTag != null && !EMPTY_STR.equals(xmlTag)) {
            tempXMLTag = xmlTag;
        }
        Vector<DatabaseRecord> records = null;
        if (value instanceof ArrayList) {
            // JPA query results in a list of raw values
            // Here we have raw values returned as opposed to DatabaseRecords - this means
            // we need to figure out the tag names based on the call's output parameters.
            // assumes JPAQuery
            JPAQuery jpaQuery = (JPAQuery) queryHandler.getDatabaseQuery();
            // to match field names with results, we need to gather the database fields from each of the Output parameters
            List<DatabaseField> paramFlds = new ArrayList<DatabaseField>();
            DatasourceCall dsCall = (DatasourceCall) jpaQuery.getDatabaseQuery().getDatasourceCall();
            for (Object obj : dsCall.getParameters()) {
                if (obj instanceof OutputParameterForCallableStatement) {
                    paramFlds.add(((OutputParameterForCallableStatement) obj).getOutputField());
                } else if (obj instanceof Object[]) {
                    Object[] objArray = (Object[]) obj;
                    for (int i = 0; i < objArray.length; i++) {
                        Object o = objArray[i];
                        if (o instanceof OutputParameterForCallableStatement) {
                            paramFlds.add(((OutputParameterForCallableStatement) o).getOutputField());
                        }
                    }
                }
            }
            // now create a record using DatabaseField/value pairs
            DatabaseRecord dr = new DatabaseRecord();
            if (paramFlds.size() > 0) {
                for (int i=0; i <  ((ArrayList<?>) value).size(); i++) {
                    dr.add(paramFlds.get(i), ((ArrayList<?>) value).get(i));
                }
            } else {
                dr.add(new DatabaseField(RESULT_STR), ((ArrayList<?>) value).get(0));
            }
            records = new Vector<DatabaseRecord>();
            records.add(dr);
        } else if (value instanceof Vector) {
            Class<?> vectorContent = ((Vector<?>)value).firstElement().getClass();
            if (DatabaseRecord.class.isAssignableFrom(vectorContent)) {
                records = (Vector<DatabaseRecord>)value;
            } else {
                records = new Vector<DatabaseRecord>();
                DatabaseRecord dr = new DatabaseRecord();
                dr.add(new DatabaseField(RESULT_STR), ((Vector<?>)value).firstElement());
                records.add(dr);
            }
        } else {
            records = new Vector<DatabaseRecord>();
            DatabaseRecord dr = new DatabaseRecord();
            dr.add(new DatabaseField(RESULT_STR), value);
            records.add(dr);
        }
        SimpleXMLFormatModel simpleXMLFormatModel = new SimpleXMLFormatModel();
        XMLConversionManager conversionManager =
            (XMLConversionManager) xrService.getOXSession().getDatasourcePlatform().getConversionManager();
        SessionLog log = xrService.getOXSession().getSessionLog();
        for (DatabaseRecord dr : records) {
            Element rowElement = TEMP_DOC.createElement(tempXMLTag);
            for (DatabaseField field : dr.getFields()) {
                // handle complex types, i.e. ones we have a descriptor for
                if (field instanceof ObjectRelationalDatabaseField) {
                    ObjectRelationalDatabaseField ordtField = (ObjectRelationalDatabaseField) field;
                    if (xrService.getOXSession().getDescriptor(ordtField.getType()) != null) {
                        xrService.getXMLContext().createMarshaller().marshal(dr.get(field), rowElement);
                        continue;
                    }
                }
                Object fieldValue = dr.get(field);
                if (fieldValue != null) {
                    if (fieldValue instanceof Calendar) {
                        Calendar cValue = (Calendar)fieldValue;
                        fieldValue = conversionManager.convertObject(cValue, STRING, DATE_TIME_QNAME);
                    }
                    if (fieldValue instanceof Date) {
                        Date dValue = (Date)fieldValue;
                        fieldValue = conversionManager.convertObject(dValue, STRING, DATE_QNAME);
                    } else if (fieldValue instanceof Time) {
                        Time tValue = (Time)fieldValue;
                        fieldValue = conversionManager.convertObject(tValue, STRING, TIME_QNAME);
                    } else if (fieldValue instanceof Timestamp) {
                        Timestamp tsValue = (Timestamp)fieldValue;
                        fieldValue = conversionManager.convertObject(tsValue, STRING, DATE_TIME_QNAME);
                    } else if (fieldValue instanceof Blob) {
                        fieldValue = conversionManager.convertObject(fieldValue, ClassConstants.APBYTE);
                    } else if (SQLXML.class.isAssignableFrom(fieldValue.getClass())) {
                        // handle XMLType case where an oracle.jdbc.driver.OracleSQLXML instance was returned
                        SQLXML sqlXml = (SQLXML) fieldValue;
                        try {
                            String str = sqlXml.getString();
                            sqlXml.free();
                            // Oracle 12c appends a \n character to the xml string
                            fieldValue = str.endsWith("\n") ? str.substring(0, str.length() - 1) : str;
                        } catch (SQLException e) {
                            log.logThrowable(SessionLog.FINE, SessionLog.DBWS, e);
                        }
                    } else if (fieldValue.getClass().getName().equalsIgnoreCase(ORACLEOPAQUE_STR)) {
                        // handle XMLType case where an oracle.sql.OPAQUE instance was returned
                        try {
                            Class<?> oracleOPAQUE;
                            Class<?> xmlTypeFactoryClass;
                            Constructor<?> xmlTypeFactoryConstructor;
                            Object xmlTypeFactory;
                            Method getStringMethod;
                            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                                oracleOPAQUE = AccessController.doPrivileged(new PrivilegedClassForName(IORACLEOPAQUE_STR, true, this.getClass().getClassLoader()));
                                xmlTypeFactoryClass = AccessController.doPrivileged(new PrivilegedClassForName(XMLTYPEFACTORY_STR, true, this.getClass().getClassLoader()));
                                xmlTypeFactoryConstructor = AccessController.doPrivileged(new PrivilegedGetConstructorFor(xmlTypeFactoryClass, new Class[0], true));
                                xmlTypeFactory = AccessController.doPrivileged(new PrivilegedInvokeConstructor(xmlTypeFactoryConstructor, new Object[0]));
                                getStringMethod = AccessController.doPrivileged(new PrivilegedGetDeclaredMethod(xmlTypeFactoryClass, GETSTRING_METHOD, new Class[] {oracleOPAQUE}));
                                fieldValue = AccessController.doPrivileged(new PrivilegedMethodInvoker(getStringMethod, fieldValue, new Object[] {}));
                            } else {
                                oracleOPAQUE = PrivilegedAccessHelper.getClassForName(IORACLEOPAQUE_STR, false, this.getClass().getClassLoader());
                                xmlTypeFactoryClass = PrivilegedAccessHelper.getClassForName(XMLTYPEFACTORY_STR, true, this.getClass().getClassLoader());
                                xmlTypeFactoryConstructor = PrivilegedAccessHelper.getConstructorFor(xmlTypeFactoryClass, new Class[0], true);
                                xmlTypeFactory = PrivilegedAccessHelper.invokeConstructor(xmlTypeFactoryConstructor, new Object[0]);
                                getStringMethod = PrivilegedAccessHelper.getDeclaredMethod(xmlTypeFactoryClass, GETSTRING_METHOD, new Class[] {oracleOPAQUE});
                                fieldValue = PrivilegedAccessHelper.invokeMethod(getStringMethod, xmlTypeFactory, new Object[] {fieldValue});
                            }
                        } catch (RuntimeException x) {
                            throw x;
                        } catch (ReflectiveOperationException | PrivilegedActionException e) {
                            // if the required resources are not available there's nothing we can do...
                            log.logThrowable(SessionLog.FINE, SessionLog.DBWS, e);
                        }
                    }

                    String elementName;
                    if (field.getName() == null || (elementName = sqlToXmlName(field.getName())).equals(EMPTY_STR)) {
                        // return arg from stored function has no name
                       elementName = RESULT_STR;
                    }
                    Element columnElement = TEMP_DOC.createElement(elementName);
                    rowElement.appendChild(columnElement);
                    String fieldValueString = fieldValue.toString();
                    // handle binary content - attachments dealt with in invoke() above
                    if (result.getType().equals(BASE_64_BINARY_QNAME)) {
                        fieldValueString = Helper.buildHexStringFromBytes(Base64.base64Encode((byte[])fieldValue));
                        columnElement.setAttributeNS(XMLNS_URL, XSD_STR, SCHEMA_URL);
                        columnElement.setAttributeNS(XMLNS_URL, XSI_STR, SCHEMA_INSTANCE_URL);
                        columnElement.setAttributeNS(SCHEMA_INSTANCE_URL, XSITYPE_STR, BASE64_BINARY_STR);
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
