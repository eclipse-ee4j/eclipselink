/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.dbws;

//javase imports
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.xml.sax.Attributes;

// Java extension imports
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.BatchQueryOperation;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.DeleteOperation;
import org.eclipse.persistence.internal.xr.InsertOperation;
import org.eclipse.persistence.internal.xr.JPQLQueryHandler;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProcedureArgument;
import org.eclipse.persistence.internal.xr.ProcedureOutputArgument;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.QNameTransformer;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.SQLQueryHandler;
import org.eclipse.persistence.internal.xr.StoredFunctionQueryHandler;
import org.eclipse.persistence.internal.xr.StoredProcedureQueryHandler;
import org.eclipse.persistence.internal.xr.UpdateOperation;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_TAG;
import static org.eclipse.persistence.oxm.XMLConstants.BOOLEAN_QNAME;

@SuppressWarnings({"serial", "unchecked"/*, "rawtypes"*/})
public class DBWSModelProject extends Project {

    public NamespaceResolver ns;

    public DBWSModelProject() {
        setName("DBWSModelProject");

        ns = new NamespaceResolver();
        ns.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);

        addDescriptor(buildServiceDescriptor());
        addDescriptor(buildAttachmentDescriptor());
        addDescriptor(buildResultDescriptor());
        addDescriptor(buildCollectionResultDescriptor());
        addDescriptor(buildParameterDescriptor());
        addDescriptor(buildSimpleXMLFormatDescriptor());
        addDescriptor(buildNamedQueryDescriptor());
        addDescriptor(buildSqlQueryDescriptor());
        addDescriptor(buildJpqlQueryDescriptor());
        addDescriptor(buildStoredProcedureQueryDescriptor());
        addDescriptor(buildProcedureArgumentDescriptor());
        addDescriptor(buildProcedureOutputArgumentDescriptor());
        addDescriptor(buildStoredFunctionQueryDescriptor());
        addDescriptor(buildQueryDescriptor());
        addDescriptor(buildInsertDescriptor());
        addDescriptor(buildDeleteDescriptor());
        addDescriptor(buildUpdateDescriptor());
        addDescriptor(buildBatchQueryDescriptor());

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(ns);
        }
    }

    protected XMLDescriptor buildServiceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DBWSModel.class);
        descriptor.setDefaultRootElement("dbws");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLDirectMapping sessionFile = new XMLDirectMapping();
        sessionFile.setAttributeName("sessionsFile");
        sessionFile.setXPath("sessions-file/text()");
        descriptor.addMapping(sessionFile);

        XMLChoiceCollectionMapping operationsMapping = new XMLChoiceCollectionMapping();
        operationsMapping.setAttributeName("operations");
        operationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                return ((XRServiceModel)object).getOperationsList();
            }
            public void setAttributeValueInObject(Object object, Object value) {
                Vector v = (Vector)value;
                XRServiceModel dbwsModel = (XRServiceModel)object;
                Map<String, Operation> operations = dbwsModel.getOperations();
                for (Iterator i = v.iterator(); i.hasNext();) {
                    Object obj = i.next();
                    if (obj instanceof Operation) {
                        Operation op = (Operation)obj;
                        operations.put(op.getName(), op);
                    }
                }
            }
          });
        operationsMapping.addChoiceElement("insert", InsertOperation.class);
        operationsMapping.addChoiceElement("query", QueryOperation.class);
        operationsMapping.addChoiceElement("update", UpdateOperation.class);
        operationsMapping.addChoiceElement("delete", DeleteOperation.class);
        operationsMapping.addChoiceElement("batch-sql-query", BatchQueryOperation.class);
        descriptor.addMapping(operationsMapping);
        return descriptor;
    }

    // query
    //
    // name,parameter*,result,named-query|sql|jpql|stored-procedure|stored-function
    protected XMLDescriptor buildQueryDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryOperation.class);
        descriptor.setDefaultRootElement("query");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLCompositeCollectionMapping parameters = new XMLCompositeCollectionMapping();
        parameters.setAttributeName("parameters");
        parameters.setReferenceClass(Parameter.class);
        parameters.setXPath("parameter");
        descriptor.addMapping(parameters);

        XMLCompositeObjectMapping resultMapping = new XMLCompositeObjectMapping();
        resultMapping.setAttributeName("result");
        resultMapping.setReferenceClass(Result.class);
        resultMapping.setXPath( "result");
        descriptor.addMapping(resultMapping);

        XMLChoiceObjectMapping queryHandlerMapping = new XMLChoiceObjectMapping();
        queryHandlerMapping.setAttributeName("queryHandler");
        queryHandlerMapping.addChoiceElement("jpql", JPQLQueryHandler.class);
        queryHandlerMapping.addChoiceElement("named-query", NamedQueryHandler.class);
        queryHandlerMapping.addChoiceElement("sql", SQLQueryHandler.class);
        queryHandlerMapping.addChoiceElement("stored-procedure",
            StoredProcedureQueryHandler.class);
        queryHandlerMapping.addChoiceElement("stored-function",
            StoredFunctionQueryHandler.class);
        descriptor.addMapping(queryHandlerMapping);

        return descriptor;
    }

    /**
     * Build an XMLDescriptor for BatchQueryOperation.
     */
    protected XMLDescriptor buildBatchQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BatchQueryOperation.class);
        descriptor.setDefaultRootElement("batch-sql-query");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLCompositeDirectCollectionMapping statementsMapping = new XMLCompositeDirectCollectionMapping();
        statementsMapping.setAttributeName("batchSql");
        XMLField f1 = new XMLField("batch-statement/text()");
        f1.setIsCDATA(true);
        statementsMapping.setField(f1);
        descriptor.addMapping(statementsMapping);
        return descriptor;
    }

    // parameter
    //
    // name,type
    protected XMLDescriptor buildParameterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Parameter.class);

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLTransformationMapping type = new XMLTransformationMapping();
        type.setAttributeName("type");
        QNameTransformer qNameTransformer = new QNameTransformer("type/text()");
        type.addFieldTransformer("type/text()", qNameTransformer);
        type.setAttributeTransformer(qNameTransformer);
        descriptor.addMapping(type);

        XMLDirectMapping optional = new XMLDirectMapping();
        optional.setAttributeName("optional");
        optional.setXPath("@optional");
        optional.setNullValue(false);
        descriptor.addMapping(optional);

        return descriptor;
    }

    // attachment
    //
    // mime-type
    protected XMLDescriptor buildAttachmentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Attachment.class);

        XMLDirectMapping mimeType = new XMLDirectMapping();
        mimeType.setAttributeName("mimeType");
        mimeType.setXPath("mime-type/text()");
        descriptor.addMapping(mimeType);

        return descriptor;
    }

    // result
    //
    // type|attachment
    protected XMLDescriptor buildResultDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Result.class);
        descriptor.setDefaultRootElement("result");

        XMLTransformationMapping type = new XMLTransformationMapping();
        type.setAttributeName("type");
        QNameTransformer qNameTransformer = new QNameTransformer("type/text()");
        type.addFieldTransformer("type/text()", qNameTransformer);
        type.setAttributeTransformer(qNameTransformer);
        descriptor.addMapping(type);

        XMLCompositeObjectMapping attachment = new XMLCompositeObjectMapping();
        attachment.setAttributeName("attachment");
        attachment.setXPath("attachment");
        attachment.setReferenceClass(Attachment.class);
        descriptor.addMapping(attachment);

        XMLCompositeObjectMapping sxf = new XMLCompositeObjectMapping();
        sxf.setAttributeName("simpleXMLFormat");
        sxf.setXPath("simple-xml-format");
        sxf.setReferenceClass(SimpleXMLFormat.class);
        descriptor.addMapping(sxf);

        XMLDirectMapping isCollection = new XMLDirectMapping();
        isCollection.setAttributeAccessor(new AttributeAccessor() {
            @Override
            public String getAttributeName() {
                return "isCollection";
            }
            @Override
            public Object getAttributeValueFromObject(Object object) throws DescriptorException {
                if (object instanceof CollectionResult) {
                    return Boolean.TRUE;
                }
                return null;
            }
            @Override
            public void setAttributeValueInObject(Object object, Object value)
                throws DescriptorException {
            }
        });
        isCollection.setXPath("@isCollection");
        descriptor.addMapping(isCollection);

        XMLField isColl = new XMLField("@isCollection");
        isColl.setSchemaType(BOOLEAN_QNAME);
        descriptor.getInheritancePolicy().setClassIndicatorField(isColl);
        descriptor.getInheritancePolicy().setClassExtractor(new ClassExtractor() {
            @Override
            public Class<?> extractClassFromRow(Record record, Session session) {
                Class<?> clz = Result.class;
                UnmarshalRecord uRecord = (UnmarshalRecord)record;
                Attributes attrs = uRecord.getAttributes();
                if (attrs != null) {
                    for (int i = 0, l = attrs.getLength(); i < l; i++) {
                        String attributeName = attrs.getQName(i);
                        if (attributeName.equals("isCollection")) {
                            String value = attrs.getValue(i);
                            if (value.equalsIgnoreCase("true")) {
                                clz = CollectionResult.class;
                                break;
                            }
                        }
                    }
                }
                return clz;
            }
        });

        return descriptor;
    }

    // result isCollection="true"
    //
    protected XMLDescriptor buildCollectionResultDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CollectionResult.class);
        descriptor.getInheritancePolicy().setParentClass(Result.class);
        descriptor.setDefaultRootElement("result");

        return descriptor;
    }

    // simple-xml-format
    //
    // simple-xml-format-tag?,simple-xml-tag?
    protected XMLDescriptor buildSimpleXMLFormatDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SimpleXMLFormat.class);
        descriptor.setDefaultRootElement(DEFAULT_SIMPLE_XML_FORMAT_TAG);

        XMLDirectMapping simpleXMLFormatTag = new XMLDirectMapping();
        simpleXMLFormatTag.setAttributeName("simpleXMLFormatTag");
        simpleXMLFormatTag.setXPath(DEFAULT_SIMPLE_XML_FORMAT_TAG + "-tag/text()");
        descriptor.addMapping(simpleXMLFormatTag);

        XMLDirectMapping xmlTag = new XMLDirectMapping();
        xmlTag.setAttributeName("xmlTag");
        xmlTag.setXPath(DEFAULT_SIMPLE_XML_TAG + "-tag/text()");
        descriptor.addMapping(xmlTag);

        return descriptor;
    }

    // named-query
    //
    // name,descriptor?
    protected XMLDescriptor buildNamedQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedQueryHandler.class);
        descriptor.setDefaultRootElement("named-query");

        XMLDirectMapping queryName = new XMLDirectMapping();
        queryName.setAttributeName("name");
        queryName.setXPath("name/text()");
        descriptor.addMapping(queryName);

        XMLDirectMapping alias = new XMLDirectMapping();
        alias.setAttributeName("descriptor");
        alias.setXPath("descriptor/text()");
        descriptor.addMapping(alias);

        return descriptor;
    }

    // sql <![CDATA[ ... ]]>
    protected XMLDescriptor buildSqlQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SQLQueryHandler.class);
        descriptor.setDefaultRootElement("sql");

        XMLDirectMapping queryName = new XMLDirectMapping();
        queryName.setAttributeName("sqlString");
        queryName.setXPath("text()");
        queryName.setIsCDATA(true);
        descriptor.addMapping(queryName);

        return descriptor;
    }

    // jpql <![CDATA[ ... ]]>
    protected XMLDescriptor buildJpqlQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JPQLQueryHandler.class);
        descriptor.setDefaultRootElement("jpql");

        XMLDirectMapping queryName = new XMLDirectMapping();
        queryName.setAttributeName("jpqlString");
        queryName.setXPath("text()");
        queryName.setIsCDATA(true);
        descriptor.addMapping(queryName);

        return descriptor;
    }

    // stored-procedure
    //
    // name, in-argument*, inout-argument*, out-argument*
    protected XMLDescriptor buildStoredProcedureQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureQueryHandler.class);
        descriptor.setDefaultRootElement("stored-procedure");

        XMLDirectMapping procedureName = new XMLDirectMapping();
        procedureName.setAttributeName("name");
        procedureName.setXPath("name/text()");
        descriptor.addMapping(procedureName);

        XMLCompositeCollectionMapping inArguments = new XMLCompositeCollectionMapping();
        inArguments.setAttributeName("inArguments");
        inArguments.setXPath("in-argument");
        inArguments.setReferenceClass(ProcedureArgument.class);
        descriptor.addMapping(inArguments);

        XMLCompositeCollectionMapping inOutArguments = new XMLCompositeCollectionMapping();
        inOutArguments.setAttributeName("inOutArguments");
        inOutArguments.setXPath("inout-argument");
        inOutArguments.setReferenceClass(ProcedureOutputArgument.class);
        descriptor.addMapping(inOutArguments);

        XMLCompositeCollectionMapping outArguments = new XMLCompositeCollectionMapping();
        outArguments.setAttributeName("outArguments");
        outArguments.setXPath("out-argument");
        outArguments.setReferenceClass(ProcedureOutputArgument.class);
        descriptor.addMapping(outArguments);

        return descriptor;
    }

    protected XMLDescriptor buildStoredFunctionQueryDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredFunctionQueryHandler.class);
        descriptor.setDefaultRootElement("stored-function");

        XMLDirectMapping procedureName = new XMLDirectMapping();
        procedureName.setAttributeName("name");
        procedureName.setXPath("name/text()");
        descriptor.addMapping(procedureName);

        XMLCompositeCollectionMapping inArguments = new XMLCompositeCollectionMapping();
        inArguments.setAttributeName("inArguments");
        inArguments.setXPath("in-argument");
        inArguments.setReferenceClass(ProcedureArgument.class);
        descriptor.addMapping(inArguments);

        XMLCompositeCollectionMapping inOutArguments = new XMLCompositeCollectionMapping();
        inOutArguments.setAttributeName("inOutArguments");
        inOutArguments.setXPath("inout-argument");
        inOutArguments.setReferenceClass(ProcedureOutputArgument.class);
        descriptor.addMapping(inOutArguments);

        XMLCompositeCollectionMapping outArguments = new XMLCompositeCollectionMapping();
        outArguments.setAttributeName("outArguments");
        outArguments.setXPath("out-argument");
        outArguments.setReferenceClass(ProcedureOutputArgument.class);
        descriptor.addMapping(outArguments);

        return descriptor;
    }

    // in-argument
    //
    // name, parameter?
    protected XMLDescriptor buildProcedureArgumentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ProcedureArgument.class);

        XMLDirectMapping argumentName = new XMLDirectMapping();
        argumentName.setAttributeName("name");
        argumentName.setXPath("name");
        descriptor.addMapping(argumentName);

        XMLDirectMapping parameterName = new XMLDirectMapping();
        parameterName.setAttributeName("parameterName");
        parameterName.setXPath("parameter");
        descriptor.addMapping(parameterName);

        XMLDirectMapping complexTypeName = new XMLDirectMapping();
        complexTypeName.setAttributeName("complexTypeName");
        complexTypeName.setXPath("complex-type");
        descriptor.addMapping(complexTypeName);

        return descriptor;
    }

    // inout-argument and out-argument
    //
    // name, parameter?, type?
    protected XMLDescriptor buildProcedureOutputArgumentDescriptor() {

        XMLDescriptor descriptor = buildProcedureArgumentDescriptor();
        descriptor.setJavaClass(ProcedureOutputArgument.class);

        XMLTransformationMapping resultType = new XMLTransformationMapping();
        resultType.setAttributeName("resultType");
        QNameTransformer qNameTransformer = new QNameTransformer("type/text()");
        resultType.addFieldTransformer("type/text()", qNameTransformer);
        resultType.setAttributeTransformer(qNameTransformer);
        descriptor.addMapping(resultType);

        return descriptor;
    }

    // insert
    //
    // name, parameter
    protected XMLDescriptor buildInsertDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InsertOperation.class);
        descriptor.setDefaultRootElement("insert");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLCompositeCollectionMapping parameters = new XMLCompositeCollectionMapping();
        parameters.setAttributeName("parameters");
        parameters.setReferenceClass(Parameter.class);
        parameters.setXPath("parameter");
        descriptor.addMapping(parameters);

        return descriptor;
    }

    // delete
    //
    // name, parameter
    protected XMLDescriptor buildDeleteDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DeleteOperation.class);
        descriptor.setDefaultRootElement("delete");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLDirectMapping descriptorName = new XMLDirectMapping();
        descriptorName.setAttributeName("descriptorName");
        descriptorName.setXPath("descriptor-name/text()");
        descriptor.addMapping(descriptorName);

        XMLCompositeCollectionMapping parameters = new XMLCompositeCollectionMapping();
        parameters.setAttributeName("parameters");
        parameters.setReferenceClass(Parameter.class);
        parameters.setXPath("parameter");
        descriptor.addMapping(parameters);

        return descriptor;
    }

    // update
    //
    // name, parameter
    protected XMLDescriptor buildUpdateDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UpdateOperation.class);
        descriptor.setDefaultRootElement("update");

        XMLDirectMapping name = new XMLDirectMapping();
        name.setAttributeName("name");
        name.setXPath("name/text()");
        descriptor.addMapping(name);

        XMLCompositeCollectionMapping parameters = new XMLCompositeCollectionMapping();
        parameters.setAttributeName("parameters");
        parameters.setReferenceClass(Parameter.class);
        parameters.setXPath("parameter");
        descriptor.addMapping(parameters);

        return descriptor;
    }
}
