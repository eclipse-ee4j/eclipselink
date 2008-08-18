/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import static java.util.logging.Level.FINEST;

// Java extension imports
import javax.xml.namespace.QName;
import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

// EclipseLink imports
import org.eclipse.persistence.internal.xr.Attachment;
import org.eclipse.persistence.internal.xr.CollectionResult;
import org.eclipse.persistence.internal.xr.Parameter;
import org.eclipse.persistence.internal.xr.ProcedureArgument;
import org.eclipse.persistence.internal.xr.ProcedureOutputArgument;
import org.eclipse.persistence.internal.xr.QueryOperation;
import org.eclipse.persistence.internal.xr.Result;
import org.eclipse.persistence.internal.xr.StoredFunctionQueryHandler;
import org.eclipse.persistence.internal.xr.StoredProcedureQueryHandler;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat;
import org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormatProject;
import org.eclipse.persistence.tools.dbws.Util.InOut;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredArgument;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredFunction;
import org.eclipse.persistence.tools.dbws.jdbc.DbStoredProcedure;
import static org.eclipse.persistence.internal.xr.QNameTransformer.SCHEMA_QNAMES;
import static org.eclipse.persistence.internal.xr.sxf.SimpleXMLFormat.DEFAULT_SIMPLE_XML_FORMAT_TAG;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;
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

    @Override
    public void buildOperation(DBWSBuilder builder) {

        super.buildOperation(builder);

        List<DbStoredProcedure> procs = builder.loadProcedures(catalogPattern, schemaPattern,
            procedurePattern, overload);
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
            StoredProcedureQueryHandler spqh;
            if (storedProcedure.isFunction()) {
                spqh = new StoredFunctionQueryHandler();
            }
            else {
              spqh = new StoredProcedureQueryHandler();
            }
            sb = new StringBuilder();
            if (builder.databasePlatform.getClass().getName().matches("Oracle*Platform")) {
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
            builder.logMessage(FINEST, "Building QueryOperation for " + spqh.getName());
            qo.setQueryHandler(spqh);
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
            if (!storedProcedure.isFunction() &&
            	builder.getPlatformClassname().contains("Oracle") &&
            	noOutArguments(storedProcedure)) {
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
                        result.setType(getXMLTypeFromJDBCType(rarg.getJdbcType()));
                    }
                }
                else if (!(builder.getPlatformClassname().contains("Oracle"))) {
                    // if user overrides returnType, assume they're right
                	if (returnType != null) {
                        String nsURI = null;
                        String prefix = null;
                        String localPart = null;
                        int colonIdx = returnType.indexOf(':');
                        result = new Result();
                        if (colonIdx > 0) {
                        	QName qName = null;
                            prefix = returnType.substring(0, colonIdx);
                            nsURI = builder.schema.getNamespaceResolver().resolveNamespacePrefix(prefix);
                            if (nsURI == null) {
                                nsURI = DEFAULT_NS_PREFIX;
                            }
                            localPart = returnType.substring(colonIdx+1);
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
                            result.setType(qName);
                        }
                        else {
                        	result.setType(qNameFromString("{" + builder.getTargetNamespace() +
                        		"}" + returnType, builder.schema));
                        }
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
                                xmlType = qNameFromString("{" +builder.getTargetNamespace() + "}" +
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

}
