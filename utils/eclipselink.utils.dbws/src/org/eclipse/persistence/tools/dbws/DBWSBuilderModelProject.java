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
 *     Mike Norman - May 01 2008, created DBWS tools package
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// javase imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

// Java extension libraries
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// TopLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

@SuppressWarnings({"serial", "unchecked"/*, "rawtypes"*/})
public class DBWSBuilderModelProject extends Project {

    protected NamespaceResolver ns;

    public DBWSBuilderModelProject() {
        setName("DBWSBuilderModelProject");

        ns = new NamespaceResolver();
        ns.put("xsd", W3C_XML_SCHEMA_NS_URI);

        addDescriptor(buildAssociationDescriptor());
        addDescriptor(buildDBWSBuilderModelDescriptor());
        addDescriptor(buildTableOperationModelDescriptor());
        addDescriptor(buildProcedureOperationModelDescriptor());
        addDescriptor(buildPLSQLProcedureOperationModelDescriptor());
        addDescriptor(buildSQLOperationModelDescriptor());
        addDescriptor(buildBindingModelDescriptor());

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(ns);
        }
    }

    protected ClassDescriptor buildAssociationDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Association.class);
        descriptor.setDefaultRootElement("property");

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setXPath("@name");
        descriptor.addMapping(keyMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("text()");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }
    protected ClassDescriptor buildDBWSBuilderModelDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DBWSBuilderModel.class);
        descriptor.setDefaultRootElement("dbws-builder");

        XMLCompositeCollectionMapping propertiesMapping = new XMLCompositeCollectionMapping();
        propertiesMapping.setReferenceClass(Association.class);
        propertiesMapping.setAttributeAccessor(new AttributeAccessor() {
            @Override
            public String getAttributeName() {
                return "properties";
            }
            @Override
            public Object getAttributeValueFromObject(Object object) throws DescriptorException {
                DBWSBuilderModel model = (DBWSBuilderModel)object;
                Vector<Association> associations =
                    new Vector<Association>();
                for (Map.Entry<String, String> me : model.properties.entrySet()) {
                    associations.add(new Association(me.getKey(), me.getValue()));
                }
                return associations;
            }
            @Override
            public void setAttributeValueInObject(Object object, Object value)
                throws DescriptorException {
                DBWSBuilderModel model = (DBWSBuilderModel)object;
                Vector<Association> associations =
                    (Vector<Association>)value;
                for (Association a : associations) {
                    model.properties.put((String)a.getKey(), (String)a.getValue());
                }
            }
        });
        propertiesMapping.setXPath("properties/property");
        descriptor.addMapping(propertiesMapping);

        XMLChoiceCollectionMapping operationsMapping = new XMLChoiceCollectionMapping();
        operationsMapping.setAttributeName("operations");
        operationsMapping.setContainerPolicy(new ListContainerPolicy(ArrayList.class));
        operationsMapping.addChoiceElement("table", TableOperationModel.class);
        operationsMapping.addChoiceElement("procedure", ProcedureOperationModel.class);
        operationsMapping.addChoiceElement("plsql-procedure", PLSQLProcedureOperationModel.class);
        operationsMapping.addChoiceElement("sql", SQLOperationModel.class);
        descriptor.addMapping(operationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTableOperationModelDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TableOperationModel.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        ObjectTypeConverter converter = new ObjectTypeConverter();
        converter.addConversionValue("true", Boolean.TRUE);
        converter.addConversionValue("false", Boolean.FALSE);
        converter.setFieldClassification(String.class);

        XMLDirectMapping simpleXMLFormatTagMapping = new XMLDirectMapping();
        simpleXMLFormatTagMapping.setAttributeName("simpleXMLFormatTag");
        simpleXMLFormatTagMapping.setGetMethodName("getSimpleXMLFormatTag");
        simpleXMLFormatTagMapping.setSetMethodName("setSimpleXMLFormatTag");
        simpleXMLFormatTagMapping.setXPath("@simpleXMLFormatTag");
        descriptor.addMapping(simpleXMLFormatTagMapping);

        XMLDirectMapping xmlTagMapping = new XMLDirectMapping();
        xmlTagMapping.setAttributeName("xmlTag");
        xmlTagMapping.setGetMethodName("getXmlTag");
        xmlTagMapping.setSetMethodName("setXmlTag");
        xmlTagMapping.setXPath("@xmlTag");
        descriptor.addMapping(xmlTagMapping);

        XMLDirectMapping isCollectionMapping = new XMLDirectMapping();
        isCollectionMapping.setAttributeName("isCollection");
        isCollectionMapping.setConverter(converter);
        isCollectionMapping.setNullValue(Boolean.FALSE);
        isCollectionMapping.setXPath("@isCollection");
        descriptor.addMapping(isCollectionMapping);

        XMLDirectMapping binaryAttachment = new XMLDirectMapping();
        binaryAttachment.setAttributeName("binaryAttachment");
        binaryAttachment.setConverter(converter);
        binaryAttachment.setNullValue(Boolean.FALSE);
        binaryAttachment.setXPath("@binaryAttachment");
        descriptor.addMapping(binaryAttachment);

        XMLDirectMapping attachmentType = new XMLDirectMapping();
        attachmentType.setAttributeName("attachmentType");
        attachmentType.setXPath("@attachmentType");
        descriptor.addMapping(attachmentType);

        XMLDirectMapping returnTypeMapping = new XMLDirectMapping();
        returnTypeMapping.setAttributeName("returnType");
        returnTypeMapping.setXPath("@returnType");
        descriptor.addMapping(returnTypeMapping);

        XMLDirectMapping catalogPatternMapping = new XMLDirectMapping();
        catalogPatternMapping.setAttributeName("catalogPattern");
        catalogPatternMapping.setXPath("@catalogPattern");
        descriptor.addMapping(catalogPatternMapping);

        XMLDirectMapping schemaPatternMapping = new XMLDirectMapping();
        schemaPatternMapping.setAttributeName("schemaPattern");
        schemaPatternMapping.setXPath("@schemaPattern");
        descriptor.addMapping(schemaPatternMapping);

        XMLDirectMapping tableNamePatternMapping = new XMLDirectMapping();
        tableNamePatternMapping.setAttributeName("tablePattern");
        tableNamePatternMapping.setXPath("@tableNamePattern");
        descriptor.addMapping(tableNamePatternMapping);

        XMLChoiceCollectionMapping additionalOperationsMapping = new XMLChoiceCollectionMapping();
        additionalOperationsMapping.setAttributeName("additionalOperations");
        additionalOperationsMapping.setContainerPolicy(new ListContainerPolicy(ArrayList.class));
        additionalOperationsMapping.addChoiceElement("procedure", ProcedureOperationModel.class);
        additionalOperationsMapping.addChoiceElement("plsql-procedure", PLSQLProcedureOperationModel.class);
        additionalOperationsMapping.addChoiceElement("sql", SQLOperationModel.class);
        descriptor.addMapping(additionalOperationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildProcedureOperationModelDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ProcedureOperationModel.class);
        descriptor.setDefaultRootElement("procedure");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping simpleXMLFormatTagMapping = new XMLDirectMapping();
        simpleXMLFormatTagMapping.setAttributeName("simpleXMLFormatTag");
        simpleXMLFormatTagMapping.setGetMethodName("getSimpleXMLFormatTag");
        simpleXMLFormatTagMapping.setSetMethodName("setSimpleXMLFormatTag");
        simpleXMLFormatTagMapping.setXPath("@simpleXMLFormatTag");
        descriptor.addMapping(simpleXMLFormatTagMapping);

        XMLDirectMapping xmlTagMapping = new XMLDirectMapping();
        xmlTagMapping.setAttributeName("xmlTag");
        xmlTagMapping.setGetMethodName("getXmlTag");
        xmlTagMapping.setSetMethodName("setXmlTag");
        xmlTagMapping.setXPath("@xmlTag");
        descriptor.addMapping(xmlTagMapping);

        ObjectTypeConverter converter = new ObjectTypeConverter();
        converter.addConversionValue("true", Boolean.TRUE);
        converter.addConversionValue("false", Boolean.FALSE);
        converter.setFieldClassification(String.class);

        XMLDirectMapping isCollectionMapping = new XMLDirectMapping();
        isCollectionMapping.setAttributeName("isCollection");
        isCollectionMapping.setConverter(converter);
        isCollectionMapping.setNullValue(Boolean.FALSE);
        isCollectionMapping.setXPath("@isCollection");
        descriptor.addMapping(isCollectionMapping);

        XMLDirectMapping isSimpleXMLFormatMapping = new XMLDirectMapping();
        isSimpleXMLFormatMapping.setAttributeName("isSimpleXMLFormat");
        isSimpleXMLFormatMapping.setConverter(converter);
        isSimpleXMLFormatMapping.setNullValue(Boolean.FALSE);
        isSimpleXMLFormatMapping.setXPath("@isSimpleXMLFormat");
        descriptor.addMapping(isSimpleXMLFormatMapping);

        XMLDirectMapping binaryAttachment = new XMLDirectMapping();
        binaryAttachment.setAttributeName("binaryAttachment");
        binaryAttachment.setConverter(converter);
        binaryAttachment.setNullValue(Boolean.FALSE);
        binaryAttachment.setXPath("@binaryAttachment");
        descriptor.addMapping(binaryAttachment);

        XMLDirectMapping attachmentType = new XMLDirectMapping();
        attachmentType.setAttributeName("attachmentType");
        attachmentType.setXPath("@attachmentType");
        descriptor.addMapping(attachmentType);

        XMLDirectMapping returnTypeMapping = new XMLDirectMapping();
        returnTypeMapping.setAttributeName("returnType");
        returnTypeMapping.setXPath("@returnType");
        descriptor.addMapping(returnTypeMapping);

        XMLDirectMapping catalogPatternMapping = new XMLDirectMapping();
        catalogPatternMapping.setAttributeName("catalogPattern");
        catalogPatternMapping.setXPath("@catalogPattern");
        descriptor.addMapping(catalogPatternMapping);

        XMLDirectMapping schemaPatternMapping = new XMLDirectMapping();
        schemaPatternMapping.setAttributeName("schemaPattern");
        schemaPatternMapping.setXPath("@schemaPattern");
        descriptor.addMapping(schemaPatternMapping);

        XMLDirectMapping procedurePatternMapping = new XMLDirectMapping();
        procedurePatternMapping.setAttributeName("procedurePattern");
        procedurePatternMapping.setXPath("@procedurePattern");
        descriptor.addMapping(procedurePatternMapping);

        XMLDirectMapping isAdvancedJDBCMapping = new XMLDirectMapping();
        isAdvancedJDBCMapping.setAttributeName("isAdvancedJDBC");
        isAdvancedJDBCMapping.setConverter(converter);
        isAdvancedJDBCMapping.setNullValue(Boolean.FALSE);
        isAdvancedJDBCMapping.setXPath("@isAdvancedJDBC");
        descriptor.addMapping(isAdvancedJDBCMapping);

        return descriptor;
    }
    protected ClassDescriptor buildPLSQLProcedureOperationModelDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)buildProcedureOperationModelDescriptor();
        descriptor.setJavaClass(PLSQLProcedureOperationModel.class);
        descriptor.setDefaultRootElement("plsql-procedure");

        return descriptor;
    }

    protected ClassDescriptor buildSQLOperationModelDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SQLOperationModel.class);
        descriptor.setDefaultRootElement("sql");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        ObjectTypeConverter converter = new ObjectTypeConverter();
        converter.addConversionValue("true", Boolean.TRUE);
        converter.addConversionValue("false", Boolean.FALSE);
        converter.setFieldClassification(String.class);

        XMLDirectMapping simpleXMLFormatTagMapping = new XMLDirectMapping();
        simpleXMLFormatTagMapping.setAttributeName("simpleXMLFormatTag");
        simpleXMLFormatTagMapping.setGetMethodName("getSimpleXMLFormatTag");
        simpleXMLFormatTagMapping.setSetMethodName("setSimpleXMLFormatTag");
        simpleXMLFormatTagMapping.setXPath("@simpleXMLFormatTag");
        descriptor.addMapping(simpleXMLFormatTagMapping);

        XMLDirectMapping xmlTagMapping = new XMLDirectMapping();
        xmlTagMapping.setAttributeName("xmlTag");
        xmlTagMapping.setGetMethodName("getXmlTag");
        xmlTagMapping.setSetMethodName("setXmlTag");
        xmlTagMapping.setXPath("@xmlTag");
        descriptor.addMapping(xmlTagMapping);

        XMLDirectMapping isCollectionMapping = new XMLDirectMapping();
        isCollectionMapping.setAttributeName("isCollection");
        isCollectionMapping.setConverter(converter);
        isCollectionMapping.setNullValue(Boolean.FALSE);
        isCollectionMapping.setXPath("@isCollection");
        descriptor.addMapping(isCollectionMapping);

        XMLDirectMapping binaryAttachment = new XMLDirectMapping();
        binaryAttachment.setAttributeName("binaryAttachment");
        binaryAttachment.setConverter(converter);
        binaryAttachment.setNullValue(Boolean.FALSE);
        binaryAttachment.setXPath("@binaryAttachment");
        descriptor.addMapping(binaryAttachment);

        XMLDirectMapping attachmentType = new XMLDirectMapping();
        attachmentType.setAttributeName("attachmentType");
        attachmentType.setXPath("@attachmentType");
        descriptor.addMapping(attachmentType);

        XMLDirectMapping returnTypeMapping = new XMLDirectMapping();
        returnTypeMapping.setAttributeName("returnType");
        returnTypeMapping.setXPath("@returnType");
        descriptor.addMapping(returnTypeMapping);

        // bug 322949
        
        XMLChoiceObjectMapping statementMapping = new XMLChoiceObjectMapping();
        statementMapping.setAttributeName("sql");
        // support old element name 'text' and new name 'statement'
        XMLField f1 = new XMLField("statement/text()");
        f1.setIsCDATA(true);
        statementMapping.addChoiceElement(f1, String.class);
        XMLField f2 = new XMLField("text/text()");
        f2.setIsCDATA(true);
        statementMapping.addChoiceElement(f2, String.class);
        descriptor.addMapping(statementMapping);
        
        XMLDirectMapping buildStatementMapping = new XMLDirectMapping();
        buildStatementMapping.setAttributeName("buildSql");
        buildStatementMapping.setXPath("build-statement/text()");
        buildStatementMapping.setIsCDATA(true);
        descriptor.addMapping(buildStatementMapping);

        XMLCompositeCollectionMapping bindingsMapping = new XMLCompositeCollectionMapping();
        bindingsMapping.setAttributeName("bindings");
        bindingsMapping.setReferenceClass(BindingModel.class);
        bindingsMapping.setContainerPolicy(new ListContainerPolicy(ArrayList.class));
        bindingsMapping.setXPath("binding");
        descriptor.addMapping(bindingsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildBindingModelDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BindingModel.class);
        descriptor.setDefaultRootElement("binding");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setXPath("@type");
        descriptor.addMapping(typeMapping);

        return descriptor;
    }
}
