/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.List;
import static java.sql.Types.ARRAY;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static java.util.logging.Level.FINEST;

// Java extension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.NamedQueryHandler;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProcedureArgument;
import org.eclipse.persistence.internal.xr.ProcedureOutputArgument;
import org.eclipse.persistence.internal.xr.QueryHandler;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.StoredFunctionQueryHandler;
import org.eclipse.persistence.internal.xr.StoredProcedureQueryHandler;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl.SqlTypeWithMethods;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredFunction;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;
import org.eclipse.persistence.tools.dbws.oracle.PLSQLStoredArgument;
import static org.eclipse.persistence.internal.xr.QNameTransformer.SCHEMA_QNAMES;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
import static org.eclipse.persistence.oxm.XMLConstants.ANY_QNAME;
import static org.eclipse.persistence.tools.dbws.Util.SXF_QNAME_CURSOR;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.noOutArguments;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;

public class ProcedureOperationModel extends OperationModel {

    protected String catalogPattern;
    protected String schemaPattern;
    protected String procedurePattern;
    protected int overload; // Oracle-specific
    protected SqlTypeWithMethods typ; // cache JPub description of operation
    protected boolean isAdvancedJDBC = false;

    public ProcedureOperationModel() {
        super();
    }

    public String getCatalogPattern() {
        return catalogPattern;
    }
    public void setCatalogPattern(String catalogPattern) {
        if ("null".equalsIgnoreCase(catalogPattern)) {
            this.catalogPattern = null;
        }
        else {
            this.catalogPattern = catalogPattern;
        }
    }

    public String getSchemaPattern() {
        return schemaPattern;
    }
    public void setSchemaPattern(String schemaPattern) {
        if ("null".equalsIgnoreCase(schemaPattern)) {
            this.schemaPattern = null;
        }
        else {
            this.schemaPattern = schemaPattern;
        }
    }

    public String getProcedurePattern() {
        return procedurePattern;
    }
    public void setProcedurePattern(String procedurePattern) {
        this.procedurePattern = procedurePattern;
    }

    public int getOverload() {
        return overload;
    }
    public void setOverload(int overload) {
        this.overload = overload;
    }

    @Override
    public boolean isProcedureOperation() {
        return true;
    }

    public boolean isAdvancedJDBCProcedureOperation() {
        return isAdvancedJDBC;
    }
    public void setIsAdvancedJDBCProcedureOperation(boolean isAdvancedJDBC) {
        this.isAdvancedJDBC = isAdvancedJDBC;
    }

    public boolean isPLSQLProcedureOperation() {
        return false;
    }

    public SqlTypeWithMethods getJPubType() {
        return typ;
    }
    public void setJPubType(SqlTypeWithMethods typ) {
        this.typ = typ;
    }

    @Override
    public void buildOperation(DBWSBuilder builder) {

        super.buildOperation(builder);
        boolean isOracle = builder.databasePlatform.getClass().getName().contains("Oracle");
        boolean isMySQL = builder.databasePlatform.getClass().getName().contains("MySQL");
        List<DbStoredProcedure> procs = builder.loadProcedures(this, isOracle);
        for (DbStoredProcedure storedProcedure : procs) {
            StringBuilder sb = new StringBuilder();
            if (name == null || name.length() == 0) {
                if (storedProcedure.getOverload() > 0) {
                    sb.append(storedProcedure.getOverload());
                    sb.append('_');
                }
                if (storedProcedure.getCatalog() != null && storedProcedure.getCatalog().length() > 0) {
                    sb.append(storedProcedure.getCatalog());
                    sb.append('_');
                }
                if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                    sb.append(storedProcedure.getSchema());
                    sb.append('_');
                }
                sb.append(storedProcedure.getName());
            }
            else {
                sb.append(name);
            }
            QueryOperation qo = new QueryOperation();
            qo.setName(sb.toString());
            QueryHandler qh;
            if (storedProcedure.isFunction()) {
                qh = new StoredFunctionQueryHandler();
            }
            else {
              qh = new StoredProcedureQueryHandler();
            }
            sb = new StringBuilder();
            if (isOracle) {
                if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                    sb.append(storedProcedure.getSchema());
                    sb.append('.');
                }
                if (storedProcedure.getCatalog() != null && storedProcedure.getCatalog().length() > 0) {
                    sb.append(storedProcedure.getCatalog());
                    sb.append('.');
                }
            }
            else {
                if (!isMySQL) {
                    if (storedProcedure.getCatalog() != null && storedProcedure.getCatalog().length() > 0) {
                        sb.append(storedProcedure.getCatalog());
                        sb.append('.');
                    }
                }
                if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                    sb.append(storedProcedure.getSchema());
                    sb.append('.');
                }
            }
            sb.append(storedProcedure.getName());
            ((StoredProcedureQueryHandler)qh).setName(sb.toString());
            builder.logMessage(FINEST, "Building QueryOperation for " + sb.toString());
            // before assigning queryHandler, check for named query in OR project
            List<DatabaseQuery> queries = builder.getOrProject().getQueries();
            if (queries.size() > 0) {
                for (DatabaseQuery q : queries) {
                    if (q.getName().equals(qo.getName())) {
                        qh = new NamedQueryHandler();
                        ((NamedQueryHandler)qh).setName(qo.getName());
                    }
                }
            }
            qo.setQueryHandler(qh);
            SimpleXMLFormat sxf = null;
            if (isSimpleXMLFormat() || getReturnType() == null) {
                sxf = new SimpleXMLFormat();
            }
            if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
                sxf.setSimpleXMLFormatTag(simpleXMLFormatTag);
            }
            if (xmlTag != null && xmlTag.length() > 0) {
                if (sxf == null) {
                    sxf = new SimpleXMLFormat();
                }
                sxf.setXMLTag(xmlTag);
            }
            Result result = null;
            if (!storedProcedure.isFunction() && isOracle && noOutArguments(storedProcedure)) {
                result = new Result();
                result.setType(new QName(W3C_XML_SCHEMA_NS_URI, "int", "xsd")); // rowcount
            }
            else {
                if (storedProcedure.isFunction()) {
                    DbStoredFunction storedFunction = (DbStoredFunction)storedProcedure;
                    DbStoredArgument rarg = storedFunction.getReturnArg();
                    if (rarg.getJdbcTypeName().contains("CURSOR")) {
                        result = new CollectionResult();
                        result.setType(SXF_QNAME_CURSOR);
                    }
                    else {
                        result = new Result();
                        int rargJdbcType = rarg.getJdbcType();
                        switch (rargJdbcType) {
                            case STRUCT:
                            case ARRAY:
                            case OTHER:
                                if (returnType != null) {
                                    result.setType(buildCustomQName(returnType, builder));
                                }
                                else {
                                    result.setType(ANY_QNAME);
                                }
                                break;
                            default :
                                if (isOracle) {
                                    result.setType(OracleHelper.getXMLTypeFromJDBCType(
                                        rarg, builder.getTargetNamespace()));
                                }
                                else {
                                    result.setType(getXMLTypeFromJDBCType(rargJdbcType));
                                }
                                break;
                        }
                    }
                }
                else if (!isOracle) {
                    // if user overrides returnType, assume they're right
                    if (returnType != null) {
                        result = new Result();
                        result.setType(buildCustomQName(returnType, builder));
                    }
                    else {
                        if (isCollection) {
                            result = new CollectionResult();
                            if (isSimpleXMLFormat()) {
                                result.setType(SXF_QNAME_CURSOR);
                            }
                        }
                        else {
                            result = new Result();
                            result.setType(SXF_QNAME);
                        }
                    }
                }
                // if it is Oracle, then return types are determined by first OUT parameter (below)
            }
            if (binaryAttachment) {
                Attachment attachment = new Attachment();
                attachment.setMimeType("application/octet-stream");
                result.setAttachment(attachment);
            }
            for (DbStoredArgument arg : storedProcedure.getArguments()) {
                String argName = arg.getName();
                if (argName != null) {
                    ProcedureArgument pa = null;
                    Parameter parm = null;
                    InOut direction = arg.getInOut();
                    QName xmlType = null;
                    switch (arg.getJdbcType()) {
                        case STRUCT:
                        case ARRAY:
                        case OTHER:
                            String typeString =
                                builder.topTransformer.generateSchemaAlias(arg.getJdbcTypeName());
                            xmlType = buildCustomQName(typeString, builder);
                            break;
                        default :
                            if (isOracle) {
                                xmlType = OracleHelper.getXMLTypeFromJDBCType(
                                    arg, builder.getTargetNamespace());
                            }
                            else {
                                xmlType = getXMLTypeFromJDBCType(arg.getJdbcType());
                            }
                            break;
                    }
                    if (direction == IN) {
                        parm = new Parameter();
                        parm.setName(argName);
                        parm.setType(xmlType);
                        pa = new ProcedureArgument();
                        pa.setName(argName);
                        pa.setParameterName(argName);
                        if (qh instanceof StoredProcedureQueryHandler) {
                            ((StoredProcedureQueryHandler)qh).getInArguments().add(pa);
                        }
                    }
                    else {
                        // the first OUT/INOUT arg determines singleResult vs. collectionResult
                        pa = new ProcedureOutputArgument();
                        ProcedureOutputArgument pao = (ProcedureOutputArgument)pa;
                        pao.setName(argName);
                        pao.setParameterName(argName);
                        if (arg.getJdbcTypeName().contains("CURSOR") &&
                            returnType == null) { // if user overrides returnType, assume they're right
                            pao.setResultType(SXF_QNAME_CURSOR);
                            if (result == null) {
                                result = new CollectionResult();
                                result.setType(SXF_QNAME_CURSOR);
                            }
                        }
                        else {
                            // if user overrides returnType, assume they're right
                            // Hmm, multiple OUT's gonna be a problem - later!
                            if (returnType != null && sxf == null) {
                                xmlType = qNameFromString("{" + builder.getTargetNamespace() + "}" +
                                    returnType, builder.schema);
                            }
                            pao.setResultType(xmlType);
                            if (result == null) {
                                if (isCollection) {
                                    result = new CollectionResult();
                                }
                                else {
                                    result = new Result();
                                }
                                result.setType(xmlType);
                            }
                        }
                        if (direction == INOUT) {
                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getInOutArguments().add(pao);
                            }
                        }
                        else {
                            if (qh instanceof StoredProcedureQueryHandler) {
                                ((StoredProcedureQueryHandler)qh).getOutArguments().add(pao);
                            }
                        }
                    }
                    if (arg instanceof PLSQLStoredArgument) {
                        pa.setComplexTypeName(((PLSQLStoredArgument)arg).getPlSqlTypeName());
                    }
                    if (parm != null) {
                        qo.getParameters().add(parm);
                    }
                }
            }
            if (sxf != null) {
                result.setSimpleXMLFormat(sxf);
                // check to see if the O-X project needs descriptor for SimpleXMLFormat
                if (builder.oxProject.getDescriptorForAlias(DEFAULT_SIMPLE_XML_FORMAT_TAG) == null) {
                    SimpleXMLFormatProject sxfProject = new SimpleXMLFormatProject();
                    builder.oxProject.addDescriptor(sxfProject.buildXRRowSetModelDescriptor());
                }
            }
            qo.setResult(result);
            builder.xrServiceModel.getOperations().put(qo.getName(), qo);
        }

        // check to see if the schema requires sxfType to be added
        if (requiresSimpleXMLFormat(builder.xrServiceModel) && builder.schema.getTopLevelElements().
            get("simple-xml-format") == null) {
            addSimpleXMLFormat(builder.schema);
        }
    }

    protected QName buildCustomQName(String typeString, DBWSBuilder builder) {
        QName qName = null;
        String nsURI = null;
        String prefix = null;
        String localPart = null;
        int colonIdx = typeString.indexOf(':');
        if (colonIdx > 0) {
            prefix = typeString.substring(0, colonIdx);
            nsURI = builder.schema.getNamespaceResolver().resolveNamespacePrefix(prefix);
            if (nsURI == null) {
                nsURI = DEFAULT_NS_PREFIX;
            }
            localPart = typeString.substring(colonIdx+1);
            if (W3C_XML_SCHEMA_NS_URI.equals(nsURI)) {
                qName = SCHEMA_QNAMES.get(localPart);
                if (qName == null) { // unknown W3C_XML_SCHEMA_NS_URI type ?
                    qName = new QName(W3C_XML_SCHEMA_NS_URI, localPart,
                        prefix == null ? DEFAULT_NS_PREFIX : prefix);
                }
            }
            else {
                qName = new QName(nsURI == null ? NULL_NS_URI : nsURI,
                    localPart, prefix == null ? DEFAULT_NS_PREFIX : prefix);
            }
        }
        else {
            qName = qNameFromString("{" + builder.getTargetNamespace() +
                "}" + typeString, builder.schema);
        }
        return qName;
    }
}
