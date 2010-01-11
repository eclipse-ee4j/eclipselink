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
 
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import static java.lang.Integer.MIN_VALUE;

import org.xml.sax.Attributes;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
//import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.PLSQLargument.IN;
import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.PLSQLargument.INOUT;
import static org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel.PLSQLargument.OUT;

@SuppressWarnings("serial")
public class PLSQLCallModelTestProject extends Project {

    public PLSQLCallModelTestProject() {
        setName("PLSQLDeploymentProject");

        addDescriptor(buildDatabaseTypeWrapperDescriptor());
        addDescriptor(buildJDBCTypeWrapperDescriptor());
        addDescriptor(buildSimplePLSQLTypeWrapperDescriptor());
        addDescriptor(buildComplexPLSQLTypeWrapperDescriptor());
        addDescriptor(buildPLSQLargumentDescriptor());
        addDescriptor(buildPLSQLStoredProcedureCallDescriptor());
        addDescriptor(buildPLSQLrecordDescriptor());
        
        setDatasourceLogin(new XMLLogin(new DOMPlatform()));
    }

    private ClassDescriptor buildComplexPLSQLTypeWrapperDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ComplexPLSQLTypeWrapper.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);
        descriptor.descriptorIsAggregate();

        XMLCompositeObjectMapping wrappedDatabaseTypeMapping = new XMLCompositeObjectMapping();
        wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
        wrappedDatabaseTypeMapping.setXPath(".");
        wrappedDatabaseTypeMapping.setReferenceClass(PLSQLrecord.class);
        descriptor.addMapping(wrappedDatabaseTypeMapping);

        return descriptor;
    }

    private ClassDescriptor buildSimplePLSQLTypeWrapperDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SimplePLSQLTypeWrapper.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);
        descriptor.descriptorIsAggregate();

        XMLDirectMapping wrappedDatabaseTypeMapping = new XMLDirectMapping();
        wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
        wrappedDatabaseTypeMapping.setXPath("@plsql-type");
        EnumTypeConverter oraclePLSQLTypesEnumTypeConverter = new EnumTypeConverter(
        wrappedDatabaseTypeMapping, OraclePLSQLTypes.class, false);
        wrappedDatabaseTypeMapping.setConverter(oraclePLSQLTypesEnumTypeConverter);
        descriptor.addMapping(wrappedDatabaseTypeMapping);

        return descriptor;
    }

    private ClassDescriptor buildJDBCTypeWrapperDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JDBCTypeWrapper.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);
        descriptor.descriptorIsAggregate();

        XMLDirectMapping wrappedDatabaseTypeMapping = new XMLDirectMapping();
        wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
        wrappedDatabaseTypeMapping.setXPath("@jdbc-type");
        EnumTypeConverter jdbcTypesEnumTypeConverter = new EnumTypeConverter(
        wrappedDatabaseTypeMapping, JDBCTypes.class, false);
        wrappedDatabaseTypeMapping.setConverter(jdbcTypesEnumTypeConverter);
        descriptor.addMapping(wrappedDatabaseTypeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildPLSQLrecordDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLrecord.class);
        descriptor.descriptorIsAggregate();

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("recordName");
        nameMapping.setXPath("@plsql-record");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping typeNameMapping = new XMLDirectMapping();
        typeNameMapping.setAttributeName("typeName");
        typeNameMapping.setXPath("type-name/text()");
        descriptor.addMapping(typeNameMapping);

        XMLDirectMapping compatibleTypeMapping = new XMLDirectMapping();
        compatibleTypeMapping.setAttributeName("compatibleType");
        compatibleTypeMapping.setXPath("compatible-type/text()");
        descriptor.addMapping(compatibleTypeMapping);

        XMLCompositeCollectionMapping fieldsMapping = new XMLCompositeCollectionMapping();
        fieldsMapping.setAttributeName("fields");
        fieldsMapping.setReferenceClass(PLSQLargument.class);
        fieldsMapping.setXPath("fields/field");
        descriptor.addMapping(fieldsMapping);

        return descriptor;
    }

    public static final String PLSQL_RECORD_INDICATOR = "plsql-record";
    public static final String PLSQL_TYPE_INDICATOR = "plsql-type";
    public static final String JDBC_TYPE_INDICATOR = "jdbc-type";
    protected ClassDescriptor buildDatabaseTypeWrapperDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseTypeWrapper.class);
        descriptor.getInheritancePolicy().setClassExtractor(new ClassExtractor() {
            @Override
            public Class<?> extractClassFromRow(Record databaseRow, Session session) {
                if (databaseRow instanceof UnmarshalRecord) {
                    UnmarshalRecord unmarshalRecord = (UnmarshalRecord)databaseRow;
                    Attributes attributes = unmarshalRecord.getAttributes();
                    for (int i = 0, l = attributes.getLength(); i < l; i++) {
                        String qName = attributes.getQName(i);
                        if (PLSQL_RECORD_INDICATOR.equals(qName)) {
                            return ComplexPLSQLTypeWrapper.class;
                        }
                        if (PLSQL_TYPE_INDICATOR.equals(qName)) {
                            return SimplePLSQLTypeWrapper.class;
                        }
                        return JDBCTypeWrapper.class;
                    }
                }
                Class<?> clz = null;
                DOMRecord domRecord = (DOMRecord)databaseRow;
                Object o = domRecord.get("@" + PLSQL_RECORD_INDICATOR);
                if (o != null) {
                    clz = ComplexPLSQLTypeWrapper.class;
                }
                else {
                    o = domRecord.get("@" + PLSQL_TYPE_INDICATOR);
                    if (o != null) {
                        clz = SimplePLSQLTypeWrapper.class; 
                    }
                    else {
                        o = domRecord.get("@" + JDBC_TYPE_INDICATOR);
                        if (o != null) {
                            clz = JDBCTypeWrapper.class;
                        }
                    }
                }
                return clz;
            }
        });
        descriptor.descriptorIsAggregate();

        return descriptor;
    }

    protected ClassDescriptor buildPLSQLargumentDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLargument.class);
        descriptor.descriptorIsAggregate();

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping indexMapping = new XMLDirectMapping();
        indexMapping.setAttributeName("index");
        indexMapping.setXPath("index/text()");
        indexMapping.setNullValue(-1);
        descriptor.addMapping(indexMapping);

        XMLDirectMapping directionMapping = new XMLDirectMapping();
        directionMapping.setAttributeName("direction");
        directionMapping.setXPath("direction/text()");
        ObjectTypeConverter directionConverter = new ObjectTypeConverter();
        directionConverter.addConversionValue("IN", IN);
        directionConverter.addConversionValue("INOUT", INOUT);
        directionConverter.addConversionValue("OUT", OUT);
        directionMapping.setConverter(directionConverter);
        directionMapping.setNullValue(IN);
        descriptor.addMapping(directionMapping);

        XMLDirectMapping lengthMapping = new XMLDirectMapping();
        lengthMapping.setAttributeName("length");
        lengthMapping.setXPath("length/text()");
        lengthMapping.setNullValue(255);
        descriptor.addMapping(lengthMapping);

        XMLDirectMapping precisionMapping = new XMLDirectMapping();
        precisionMapping.setAttributeName("precision");
        precisionMapping.setXPath("precision/text()");
        precisionMapping.setNullValue(MIN_VALUE);
        descriptor.addMapping(precisionMapping);

        XMLDirectMapping scaleMapping = new XMLDirectMapping();
        scaleMapping.setAttributeName("scale");
        scaleMapping.setXPath("scale/text()");
        scaleMapping.setNullValue(MIN_VALUE);
        descriptor.addMapping(scaleMapping);

        XMLDirectMapping cursorOutputMapping = new XMLDirectMapping();
        cursorOutputMapping.setAttributeName("cursorOutput");
        cursorOutputMapping.setXPath("@cursorOutput");
        cursorOutputMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(cursorOutputMapping);

        XMLCompositeObjectMapping databaseTypeMapping = new XMLCompositeObjectMapping();
        databaseTypeMapping.setAttributeName("databaseTypeWrapper");
        databaseTypeMapping.setReferenceClass(DatabaseTypeWrapper.class);
        databaseTypeMapping.setXPath(".");
        descriptor.addMapping(databaseTypeMapping);

        return descriptor;
    }

    protected XMLDescriptor buildPLSQLStoredProcedureCallDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLStoredProcedureCall.class);
        descriptor.setDefaultRootElement("plsql-stored-procedure-call");
        descriptor.descriptorIsAggregate();

        XMLDirectMapping procedureNameMapping = new XMLDirectMapping();
        procedureNameMapping.setAttributeName("procedureName");
        procedureNameMapping.setXPath("procedure-name/text()");
        descriptor.addMapping(procedureNameMapping);

        XMLCompositeCollectionMapping argumentsMapping = new XMLCompositeCollectionMapping();
        argumentsMapping.setAttributeName("arguments");
        argumentsMapping.setXPath("arguments/argument");
        argumentsMapping.setReferenceClass(PLSQLargument.class);
        descriptor.addMapping(argumentsMapping);

        return descriptor;
    }
}
