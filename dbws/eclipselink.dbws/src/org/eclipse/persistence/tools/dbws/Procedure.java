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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

// Java extension imports
import javax.xml.namespace.QName;
import static java.sql.Types.ARRAY;
import static java.sql.Types.DATALINK;
import static java.sql.Types.JAVA_OBJECT;
import static java.sql.Types.OTHER;
import static java.sql.Types.STRUCT;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProcedureArgument;
import org.eclipse.persistence.internal.xr.ProcedureOutputArgument;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.StoredFunctionQueryHandler;
import org.eclipse.persistence.internal.xr.StoredProcedureQueryHandler;
import org.eclipse.persistence.internal.xr.XRServiceModel;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.platform.database.oracle.OraclePlatform;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredFunction;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;
import static org.eclipse.persistence.tools.dbws.Util.SXF_QNAME;
import static org.eclipse.persistence.tools.dbws.Util.addSimpleXMLFormat;
import static org.eclipse.persistence.tools.dbws.Util.getXMLTypeFromJDBCType;
import static org.eclipse.persistence.tools.dbws.Util.noOutArguments;
import static org.eclipse.persistence.tools.dbws.Util.qNameFromString;
import static org.eclipse.persistence.tools.dbws.Util.InOut.IN;
import static org.eclipse.persistence.tools.dbws.Util.InOut.INOUT;
import static org.eclipse.persistence.tools.dbws.Util.InOut.OUT;

// Ant imports
import static org.apache.tools.ant.Project.MSG_INFO;

public class Procedure extends Op {

    protected String catalogPattern;
    protected String schemaPattern;
    protected String procedurePattern;
    protected int overload; // Oracle-specific

    public Procedure() {
        super();
    }

    @Override
    public void buildOperation(XRServiceModel xrServiceModel, Schema schema,
        DatabasePlatform databasePlatform, Connection connection) {
        super.buildOperation(xrServiceModel, schema, databasePlatform, connection);

        List<DbStoredProcedure> procs = JDBCHelper.buildStoredProcedure(
            connection, databasePlatform, catalogPattern, schemaPattern, procedurePattern);
        procs = checkStoredProcedures(procs, getOverload());
        for (DbStoredProcedure storedProcedure : procs) {
            StringBuilder sb = new StringBuilder();
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
            QueryOperation qo = new QueryOperation();
            qo.setName(sb.toString());
            StoredProcedureQueryHandler spqh;
            if (storedProcedure.isFunction()) {
                spqh = new StoredFunctionQueryHandler();
            }
            else {
              spqh = new StoredProcedureQueryHandler();
            }
            sb = new StringBuilder();
            if (databasePlatform instanceof OraclePlatform) {
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
                if (storedProcedure.getCatalog() != null && storedProcedure.getCatalog().length() > 0) {
                    sb.append(storedProcedure.getCatalog());
                    sb.append('.');
                }
                if (storedProcedure.getSchema() != null && storedProcedure.getSchema().length() > 0) {
                    sb.append(storedProcedure.getSchema());
                    sb.append('.');
                }
            }
            sb.append(storedProcedure.getName());
            spqh.setName(sb.toString());
            log("Building QueryOperation for " + spqh.getName(), MSG_INFO);
            qo.setQueryHandler(spqh);
            SimpleXMLFormat sxf = null;
            if (isSimpleXMLFormat) {
                sxf = new SimpleXMLFormat();
            }
            if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
                if (sxf == null) {
                    sxf = new SimpleXMLFormat();
                }
                sxf.setSimpleXMLFormatTag(simpleXMLFormatTag);
            }
            if (xmlTag != null && xmlTag.length() > 0) {
                if (sxf == null) {
                    sxf = new SimpleXMLFormat();
                }
                sxf.setXMLTag(xmlTag);
            }
            Result result = null;
            if (!storedProcedure.isFunction() && noOutArguments(storedProcedure)) {
                result = new Result();
                result.setType(new QName(W3C_XML_SCHEMA_NS_URI, "int", "xsd")); // rowcount
            }
            else {
                if (storedProcedure.isFunction()) {
                    DbStoredFunction storedFunction = (DbStoredFunction)storedProcedure;
                    DbStoredArgument rarg = storedFunction.getReturnArg();
                    if (rarg.getJdbcTypeName().contains("CURSOR")) {
                        result = new CollectionResult();
                    }
                    else {
                        result = new Result();
                        result.setType(getXMLTypeFromJDBCType(rarg.getJdbcType()));
                    }
                }
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
                    QName xmlType = getXMLTypeFromJDBCType(arg.getJdbcType());
                    if (direction == IN) {
                        parm = new Parameter();
                        parm.setName(argName);
                        parm.setType(xmlType);
                        pa = new ProcedureArgument();
                        pa.setName(argName);
                        pa.setParameterName(argName);
                        spqh.getInArguments().add(pa);
                    }
                    else {
                        // the first OUT/INOUT arg determines singleResult vs. collectionResult
                        pa = new ProcedureOutputArgument();
                        ProcedureOutputArgument pao = (ProcedureOutputArgument)pa;
                        pao.setName(argName);
                        pao.setParameterName(argName);
                        if (arg.getJdbcTypeName().contains("CURSOR") &&
                            returnType == null) { // if user overrides returnType, assume they're right
                            pao.setResultType(SXF_QNAME);
                            if (result == null) {
                                result = new CollectionResult();
                                result.setType(SXF_QNAME);
                            }
                        }
                        else {
                            // if user overrides returnType, assume they're right
                            // Hmm, multiple OUT's gonna be a problem - later!
                            if (returnType != null && sxf == null) {
                                xmlType = qNameFromString(returnType, schema);
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
                            spqh.getInOutArguments().add(pao);
                        }
                        else {
                            spqh.getOutArguments().add(pao);
                        }
                    }
                    if (parm != null) {
                        qo.getParameters().add(parm);
                    }
                }
            }
            if (sxf != null) {
                result.setSimpleXMLFormat(sxf);
            }
            qo.setResult(result);
            xrServiceModel.getOperations().put(qo.getName(), qo);
        }

        // check to see if the schema requires sxfType to be added
        if (schema.getTopLevelElements().get("simple-xml-format") == null) {
            addSimpleXMLFormat(schema);
        }
    }

    public List<DbStoredProcedure> checkStoredProcedures(List<DbStoredProcedure> procedures,
        int oracleOverload) {

        List<DbStoredProcedure> supportedProcedures =
            new ArrayList<DbStoredProcedure>(procedures.size());

        List<DbStoredProcedure> copyOfProcedures =
            new ArrayList<DbStoredProcedure>(procedures.size());

        List<DbStoredProcedure> overloadedProcedure =
            new ArrayList<DbStoredProcedure>(1);
        if (oracleOverload == 0) {
            copyOfProcedures.addAll(procedures);
        }
        else {
            // For Oracle, storedProcedures can be overloaded - we are looking for
            // a specific version
            for (DbStoredProcedure storedProcedure : procedures) {
                if (storedProcedure.getOverload() == oracleOverload) {
                    overloadedProcedure.add(storedProcedure);
                    break;
                }
            }
            copyOfProcedures.addAll(overloadedProcedure);
        }
        for (DbStoredProcedure storedProcedure : copyOfProcedures) {
            boolean unSupportedArgType = false;
            for (DbStoredArgument arg : storedProcedure.getArguments()) {
                int jdbcType = arg.getJdbcType();
                if (jdbcType == OTHER) {
                    // For Oracle, the only way to get anything 'out' of a Stored Procedure
                    // is via a CURSOR - so this type of 'OTHER' is allowed
                    if (arg.getJdbcTypeName().contains("CURSOR") && arg.getInOut() == OUT) {
                        continue;
                    }
                    else {
                        unSupportedArgType = true;
                        break;
                    }
                }
                else if (jdbcType == ARRAY ||
                         jdbcType == STRUCT ||
                         jdbcType == DATALINK ||
                         jdbcType == JAVA_OBJECT) {
                        unSupportedArgType = true;
                        break;
                }
            }
            if (!unSupportedArgType) {
                supportedProcedures.add(storedProcedure);
            }
        }
        return supportedProcedures;
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
}
