/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports
import java.io.FilterOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Java extension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.dbws.DBWSModelProject;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.ComplexType;
import org.eclipse.persistence.internal.oxm.schema.model.Element;
import org.eclipse.persistence.internal.oxm.schema.model.Sequence;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.DeleteOperation;
import org.eclipse.persistence.internal.xr.InsertOperation;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.UpdateOperation;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.tools.dbws.jdbc.DbColumn;
import org.eclipse.persistence.tools.dbws.jdbc.DbTable;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectConfig;
import org.eclipse.persistence.internal.sessions.factories.model.project.ProjectXMLConfig;
import static org.eclipse.persistence.internal.xr.Util.getClassFromJDBCType;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_LABEL;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.tools.dbws.Util.CREATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.FINDALL_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.META_INF_DIR;
import static org.eclipse.persistence.tools.dbws.Util.REMOVE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.PK_QUERYNAME;
import static org.eclipse.persistence.tools.dbws.Util.THE_INSTANCE_NAME;
import static org.eclipse.persistence.tools.dbws.Util.UPDATE_OPERATION_NAME;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;

// Ant imports
import org.apache.tools.ant.BuildException;
import static org.apache.tools.ant.Project.MSG_VERBOSE;
import static org.apache.tools.ant.Project.MSG_INFO;

public class GenerateFromTables extends BaseGenerator {

    protected ArrayList<Table> tables = new ArrayList<Table>();
    protected List<DbTable> dbTables = new ArrayList<DbTable>();

    public GenerateFromTables() {
        super();
    }

    private DatabasePlatform databasePlatform;

    @SuppressWarnings("unchecked")
    public void buildOROXProjects() throws BuildException {

        if (tables == null || tables.isEmpty()) {
            log("No tables specified", MSG_INFO);
            return;
        }

        databasePlatform = getDatabasePlatform();
        boolean isOracle = databasePlatform instanceof OraclePlatform;
        for (Table table : tables) {
            String catalogPattern =
                isOracle ? null : table.getCatalogPattern();
            String schemaPattern = table.getSchemaPattern();
            String tableNamePattern = table.getTableNamePattern();
            dbTables.addAll(checkTables(JDBCHelper.buildDbTable(getConnection(),
                databasePlatform, catalogPattern, schemaPattern, tableNamePattern)));
        }
        if (dbTables.isEmpty()) {
            log("No matching tables", MSG_INFO);
            return;
        }

        log("Building TopLink OR Project " + DBWS_OR_XML, MSG_VERBOSE);
        orProject = new org.eclipse.persistence.sessions.Project();
        orProject.setName(projectName + "-" + DBWS_OR_LABEL);
        log("Building TopLink OX Project " + DBWS_OX_XML, MSG_VERBOSE);
        oxProject = new org.eclipse.persistence.sessions.Project();
        oxProject.setName(projectName + "-" + DBWS_OX_LABEL);
        for (DbTable dbTable : dbTables) {
            RelationalDescriptor desc = new RelationalDescriptor();
            orProject.addDescriptor(desc);
            String tableName = dbTable.getName();
            String tablename_lc = dbTable.getName().toLowerCase();
            desc.addTableName(tableName);
            desc.setAlias(tablename_lc);
            String generatedJavaClassName = getGeneratedJavaClassName(tableName);
            desc.setJavaClassName(generatedJavaClassName);
            desc.useWeakIdentityMap();
            XMLDescriptor xdesc = new XMLDescriptor();
            oxProject.addDescriptor(xdesc);
            xdesc.setJavaClassName(generatedJavaClassName);
            xdesc.setAlias(tablename_lc);
            NamespaceResolver nr = new NamespaceResolver();
            nr.put("xsi", W3C_XML_SCHEMA_INSTANCE_NS_URI);
            nr.put("xsd", W3C_XML_SCHEMA_NS_URI);
            xdesc.setNamespaceResolver(nr);
            xdesc.setDefaultRootElement(tablename_lc);
            for (DbColumn dbColumn : dbTable.getColumns()) {
                String columnName = dbColumn.getName();
                int jdbcType = dbColumn.getJDBCType();
                String dmdTypeName = dbColumn.getJDBCTypeName();
                log("Building mappings for " + tableName + "." + columnName, MSG_VERBOSE);
                DirectToFieldMapping dtfm = new DirectToFieldMapping();
                dtfm.setAttributeClassificationName(getClassFromJDBCType(dmdTypeName,
                databasePlatform).getName());
                String fieldName = columnName.toLowerCase();
                dtfm.setAttributeName(fieldName);
                DatabaseField databaseField = new DatabaseField(columnName, tableName);
                databaseField.setSqlType(jdbcType);
                dtfm.setField(databaseField);
                desc.addMapping(dtfm);
                if (dbColumn.isPK()) {
                    desc.addPrimaryKeyField(databaseField);
                }
                XMLDirectMapping xdm = new XMLDirectMapping();
                xdm.setAttributeName(fieldName);
                xdm.setXPath(fieldName + "/text()");
                XMLField xmlField = (XMLField)xdm.getField();
                xmlField.setSchemaType(getXMLTypeFromJDBCType(jdbcType));
                xdesc.addMapping(xdm);
          }
          ReadObjectQuery roq = new ReadObjectQuery();
          roq.setReferenceClassName(generatedJavaClassName);
          Expression expression = null;
          Expression builder = new ExpressionBuilder();
          Expression subExp1;
          Expression subExp2;
          Expression subExpression;
          List primaryKeyFields = desc.getPrimaryKeyFields();
          for (int index = 0; index < primaryKeyFields.size(); index++) {
              DatabaseField primaryKeyField = (DatabaseField)primaryKeyFields.get(index);
              subExp1 = builder.getField(primaryKeyField);
              subExp2 = builder.getParameter(primaryKeyField.getName().toLowerCase());
              subExpression = subExp1.equal(subExp2);
              if (expression == null) {
                  expression = subExpression;
              }
              else {
                  expression = expression.and(subExpression);
              }
              roq.addArgument(primaryKeyField.getName().toLowerCase());
          }
          roq.setSelectionCriteria(expression);
          desc.getQueryManager().addQuery(PK_QUERYNAME, roq);
          ReadAllQuery raq = new ReadAllQuery();
          roq.setReferenceClassName(generatedJavaClassName);
          desc.getQueryManager().addQuery(FINDALL_QUERYNAME, raq);
      }
      DatabaseLogin databaseLogin = new DatabaseLogin();
      databaseLogin.removeProperty("user");
      databaseLogin.removeProperty("password");
      databaseLogin.setDriverClassName(null);
      databaseLogin.setConnectionString(null);
      orProject.setLogin(databaseLogin);
      XMLLogin xmlLogin = new XMLLogin();
      xmlLogin.setDatasourcePlatform(new SAXPlatform());
      xmlLogin.getProperties().remove("user");
      xmlLogin.getProperties().remove("password");
      oxProject.setLogin(xmlLogin);
    }

    @SuppressWarnings("unchecked")
    public void addDBWSToStream(FilterOutputStream fos) throws IOException {

        initXRServiceModel();
        for (Iterator i = getORProject().getOrderedDescriptors().iterator(); i.hasNext();) {
            ClassDescriptor desc = (ClassDescriptor)i.next();
            String tablename_lc = desc.getAlias();
            QueryOperation findByPKQueryOperation = new QueryOperation();
            findByPKQueryOperation.setName(PK_QUERYNAME + "_" + tablename_lc);
            NamedQueryHandler nqh1 = new NamedQueryHandler();
            nqh1.setName(PK_QUERYNAME);
            nqh1.setDescriptor(tablename_lc);
            Result result = new Result();
            QName theInstanceType = new QName(tablename_lc + "Type");
            result.setType(theInstanceType);
            findByPKQueryOperation.setResult(result);
            findByPKQueryOperation.setQueryHandler(nqh1);
            for (Iterator j = desc.getPrimaryKeyFields().iterator(); j.hasNext();) {
                DatabaseField field = (DatabaseField)j.next();
                Parameter p = new Parameter();
                p.setName(field.getName().toLowerCase());
                p.setType(getXMLTypeFromJDBCType(field.getSqlType()));
                findByPKQueryOperation.getParameters().add(p);
            }
            xrServiceModel.getOperations().put(findByPKQueryOperation.getName(), findByPKQueryOperation);
            QueryOperation findAllOperation = new QueryOperation();
            findAllOperation.setName(FINDALL_QUERYNAME + "_" + tablename_lc);
            NamedQueryHandler nqh2 = new NamedQueryHandler();
            nqh2.setName(FINDALL_QUERYNAME);
            nqh2.setDescriptor(tablename_lc);
            Result result2 = new CollectionResult();
            result2.setType(theInstanceType);
            findAllOperation.setResult(result2);
            findAllOperation.setQueryHandler(nqh2);
            xrServiceModel.getOperations().put(findAllOperation.getName(), findAllOperation);
            InsertOperation insertOperation = new InsertOperation();
            insertOperation.setName(CREATE_OPERATION_NAME + "_" + tablename_lc);
            Parameter theInstance = new Parameter();
            theInstance.setName(THE_INSTANCE_NAME);
            theInstance.setType(theInstanceType);
            insertOperation.getParameters().add(theInstance);
            xrServiceModel.getOperations().put(insertOperation.getName(), insertOperation);
            UpdateOperation updateOperation = new UpdateOperation();
            updateOperation.setName(UPDATE_OPERATION_NAME + "_" + tablename_lc);
            updateOperation.getParameters().add(theInstance);
            xrServiceModel.getOperations().put(updateOperation.getName(), updateOperation);
            DeleteOperation deleteOperation = new DeleteOperation();
            deleteOperation.setName(REMOVE_OPERATION_NAME + "_" + tablename_lc);
            deleteOperation.getParameters().add(theInstance);
            xrServiceModel.getOperations().put(deleteOperation.getName(), deleteOperation);
        }
        // check for additional operations
        for (Table table : tables) {
            if (table.operations != null) {
                ArrayList<Op> additionalOperations = table.operations.getOperations();
                if (additionalOperations.size() > 0) {
                    for (Op op : additionalOperations) {
                        op.buildOperation(xrServiceModel, schema, getDatabasePlatform(),
                            getConnection());
                    }
                }
            }
        }
        XMLContext context = new XMLContext(new DBWSModelProject());
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.marshal(xrServiceModel, fos);
    }

    @Override
    public ProjectConfig buildORProjectConfig() {
        ProjectXMLConfig orProjectXMLConfig = new ProjectXMLConfig();
        orProjectXMLConfig.setProjectString(META_INF_DIR + DBWS_OR_XML);
        return orProjectXMLConfig;
    }

    @Override
    public ProjectConfig buildOXProjectConfig() {
        ProjectXMLConfig oxProjectXMLConfig = new ProjectXMLConfig();
        oxProjectXMLConfig.setProjectString(META_INF_DIR + DBWS_OX_XML);
        return oxProjectXMLConfig;
    }

    @SuppressWarnings("unchecked")
    public void addSchemaToStream(FilterOutputStream fos) throws IOException {

        super.addSchemaToStream(fos);
        for (DbTable dbTable : dbTables) {
            ClassDescriptor desc = null;
            for (Iterator i = getORProject().getOrderedDescriptors().iterator(); i.hasNext();) {
                desc = (ClassDescriptor)i.next();
                if (desc.getAlias().equalsIgnoreCase(dbTable.getName())) {
                    break;
                }
            }
            String tablename_lc = desc.getAlias();
            ComplexType complexType = new ComplexType();
            complexType.setName(tablename_lc + "Type");
            Sequence sequence = new Sequence();
            Iterator j = desc.getMappings().iterator();
            while (j.hasNext()) {
                DirectToFieldMapping mapping = (DirectToFieldMapping)j.next();
                Element element = new Element();
                DatabaseField field = mapping.getField();
                element.setName(field.getName().toLowerCase());
                if (desc.getPrimaryKeyFields().contains(field)) {
                    element.setMinOccurs("1");
                }
                else {
                    element.setMinOccurs("0");
                }
                for (DbColumn dbColumn : dbTable.getColumns()) {
                    if (dbColumn.getName().equals(field.getName())) {
                        int jdbcType = dbColumn.getJDBCType();
                        QName qName = getXMLTypeFromJDBCType(jdbcType);
                        StringBuilder sb = new StringBuilder();
                        if (qName.getNamespaceURI().equalsIgnoreCase(W3C_XML_SCHEMA_NS_URI)) {
                            sb.append("xsd:");
                        }
                        sb.append(qName.getLocalPart());
                        element.setType(sb.toString());
                        sequence.addElement(element);
                        break;
                    }
                }
            }
            complexType.setSequence(sequence);
            schema.addTopLevelComplexTypes(complexType);
            Element wrapperElement = new Element();
            wrapperElement.setName(tablename_lc);
            wrapperElement.setType(tablename_lc + "Type");
            schema.addTopLevelElement(wrapperElement);
        }
        XMLContext context = new XMLContext(new SchemaModelProject());
        XMLMarshaller marshaller = context.createMarshaller();
        marshaller.marshal(schema, fos);
    }

    public String getGeneratedJavaClassName(String tableName) {
        String first = tableName.substring(0, 1).toUpperCase();
        String rest = tableName.toLowerCase().substring(1);
        return projectName.toLowerCase() + "." + first + rest;
    }

    public List<DbTable> checkTables(List<DbTable> dbTables) {
        List<DbTable> supportedTables = new ArrayList<DbTable>(dbTables.size());
        for (DbTable dbTable : dbTables) {
            boolean unSupportedColumnType = false;
            for (DbColumn dbColumn : dbTable.getColumns()) {
                switch (dbColumn.getJDBCType()) {
                    case Types.ARRAY :
                    case Types.STRUCT :
                    case Types.OTHER :
                    case Types.DATALINK :
                    case Types.JAVA_OBJECT :
                        unSupportedColumnType = true;
                        break;
                }
            }
            if (!unSupportedColumnType) {
                supportedTables.add(dbTable);
            }
        }
        return supportedTables;
    }

    public void addTable(Table table) {
        tables.add(table);
    }

}
