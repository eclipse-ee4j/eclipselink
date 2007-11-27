package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.plsqlcallmodel;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class PLSQLCallModelTestProject extends Project {
    public PLSQLCallModelTestProject() {
        super();
        addDescriptor(getPLSQLCallDescriptor());
        addDescriptor(getPLSQLArgumentDescriptor());
        addDescriptor(getBaseObjectDescriptor());
        addDescriptor(getJDBCObjectDescriptor());
        addDescriptor(getPLSQLObjectDescriptor());
        addDescriptor(getPLSQLComplexDescriptor());
    }
    
    public XMLDescriptor getPLSQLCallDescriptor() {
        XMLDescriptor plsqlCallDescriptor = new XMLDescriptor();
        plsqlCallDescriptor.setJavaClass(PLSQLCall.class);
        plsqlCallDescriptor.descriptorIsAggregate();
        plsqlCallDescriptor.setDefaultRootElement("plsql-call");
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        plsqlCallDescriptor.addMapping(idMapping);
        XMLCompositeCollectionMapping argumentsMapping = new XMLCompositeCollectionMapping();
        argumentsMapping.setAttributeName("arguments");
        argumentsMapping.setXPath("arguments/argument");
        argumentsMapping.setReferenceClass(PLSQLargument.class);
        plsqlCallDescriptor.addMapping(argumentsMapping);
        return plsqlCallDescriptor;
    }
    
    public XMLDescriptor getPLSQLArgumentDescriptor() {
        XMLDescriptor plsqlArgumentDescriptor = new XMLDescriptor();
        plsqlArgumentDescriptor.setJavaClass(PLSQLargument.class);
        plsqlArgumentDescriptor.descriptorIsAggregate();
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        plsqlArgumentDescriptor.addMapping(nameMapping);
        XMLCompositeObjectMapping databaseTypeMapping = new XMLCompositeObjectMapping();
        databaseTypeMapping.setAttributeName("type");
        databaseTypeMapping.setReferenceClass(BaseObject.class);
        databaseTypeMapping.setXPath(".");
        plsqlArgumentDescriptor.addMapping(databaseTypeMapping);
        XMLCompositeObjectMapping databaseType2Mapping = new XMLCompositeObjectMapping();
        databaseType2Mapping.setAttributeName("secondarytype");
        databaseType2Mapping.setReferenceClass(BaseObject.class);
        databaseType2Mapping.setXPath("secondary-type");
        plsqlArgumentDescriptor.addMapping(databaseType2Mapping);
        
        return plsqlArgumentDescriptor;
    }

    public XMLDescriptor getBaseObjectDescriptor() {
        XMLDescriptor baseObjectDescriptor = new XMLDescriptor();
        baseObjectDescriptor.setJavaClass(BaseObject.class);
        baseObjectDescriptor.descriptorIsAggregate();
        baseObjectDescriptor.setDefaultRootElement("type");
        baseObjectDescriptor.getInheritancePolicy().setClassExtractor(new ClassExtractor() {
            public Class<?> extractClassFromRow(Record databaseRow, Session session) {
                if (databaseRow instanceof UnmarshalRecord) {
                    UnmarshalRecord urec = (UnmarshalRecord) databaseRow;
                    Attributes atts = urec.getAttributes();
                    for (int i=0; i<atts.getLength(); i++) {
                        String qName = atts.getQName(i);
                        if (qName.equals("plsql-type")) {
                            return PLSQLObject.class;
                        }
                        if (qName.equals("plsql-record")) {
                            return ComplexPLSQLObject.class;
                        }
                        return JDBCObject.class;
                    }
                }
                DOMRecord drec = (DOMRecord) databaseRow;
                if (drec.get(new XMLField("@plsql-type")) != null) {
                    return PLSQLObject.class;
                }
                if (drec.get(new XMLField("@plsql-record")) != null) {
                    return ComplexPLSQLObject.class;
                }
                return JDBCObject.class;
            }
        });
        return baseObjectDescriptor;
    }

    public XMLDescriptor getJDBCObjectDescriptor() {
        XMLDescriptor jdbcObjectDescriptor = new XMLDescriptor();
        jdbcObjectDescriptor.setJavaClass(JDBCObject.class);
        jdbcObjectDescriptor.descriptorIsAggregate();
        jdbcObjectDescriptor.getInheritancePolicy().setParentClass(BaseObject.class);
        jdbcObjectDescriptor.setDefaultRootElementType(new QName("jdbc"));
        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("name");
        typeMapping.setXPath("@jdbc-type");
        jdbcObjectDescriptor.addMapping(typeMapping);
        return jdbcObjectDescriptor;
    }

    public XMLDescriptor getPLSQLObjectDescriptor() {
        XMLDescriptor plsqlObjectDescriptor = new XMLDescriptor();
        plsqlObjectDescriptor.setJavaClass(PLSQLObject.class);
        plsqlObjectDescriptor.descriptorIsAggregate();
        plsqlObjectDescriptor.getInheritancePolicy().setParentClass(BaseObject.class);
        plsqlObjectDescriptor.setDefaultRootElementType(new QName("plsql"));
        XMLDirectMapping typeMapping2 = new XMLDirectMapping();
        typeMapping2.setAttributeName("name");
        typeMapping2.setXPath("@plsql-type");
        plsqlObjectDescriptor.addMapping(typeMapping2);
        return plsqlObjectDescriptor;
    }

    public XMLDescriptor getPLSQLComplexDescriptor() {
        XMLDescriptor plsqlComplexDescriptor = new XMLDescriptor();
        plsqlComplexDescriptor.setJavaClass(ComplexPLSQLObject.class);
        plsqlComplexDescriptor.descriptorIsAggregate();
        plsqlComplexDescriptor.getInheritancePolicy().setParentClass(BaseObject.class);
        plsqlComplexDescriptor.setDefaultRootElementType(new QName("plsql-record"));
        XMLDirectMapping nameMapping2 = new XMLDirectMapping();
        nameMapping2.setAttributeName("name");
        nameMapping2.setXPath("@plsql-record");
        plsqlComplexDescriptor.addMapping(nameMapping2);
        XMLCompositeCollectionMapping fieldsMapping = new XMLCompositeCollectionMapping();
        fieldsMapping.setAttributeName("fields");
        fieldsMapping.setReferenceClass(PLSQLargument.class);
        fieldsMapping.setXPath("fields/field");
        plsqlComplexDescriptor.addMapping(fieldsMapping);
        return plsqlComplexDescriptor;
    }
}