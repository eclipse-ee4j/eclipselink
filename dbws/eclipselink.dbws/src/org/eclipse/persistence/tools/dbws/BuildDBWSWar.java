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
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

// Java extension imports
import javax.wsdl.WSDLException;

// EclipseLink imports
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SESSIONS_XML;
import static org.eclipse.persistence.tools.dbws.Util.DEFAULT_WSDL_LOCATION_URI;
import static org.eclipse.persistence.tools.dbws.Util.INDEX_HTML;
import static org.eclipse.persistence.tools.dbws.Util.META_INF_DIR;
import static org.eclipse.persistence.tools.dbws.Util.ORACLE_WEBSERVICES_DESCRIPTOR;
import static org.eclipse.persistence.tools.dbws.Util.ORACLE_WEBSERVICES_FILE;
import static org.eclipse.persistence.tools.dbws.Util.WAR_CLASSES_DIR;
import static org.eclipse.persistence.tools.dbws.Util.WEB_INF_DIR;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_POSTSCRIPT;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_PREAMBLE;
import static org.eclipse.persistence.tools.dbws.Util.WSDL_FILE;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD;
import static org.eclipse.persistence.tools.dbws.Util.WSI_SWAREF_XSD_FILE;

// Ant imports
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.filters.StringInputStream;
import org.apache.tools.ant.taskdefs.JDBCTask;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import static org.apache.tools.ant.Project.MSG_VERBOSE;

public class BuildDBWSWar extends Jar implements TaskContainer {

    protected static final int BUF_SIZE = 1024;

    protected Path classPath;
    protected OROXProjectGenerator generator;
    protected String projectName;
    protected String contextRoot;
    protected String dataSource;
    protected String sessionsFileName;
    protected String sessionCustomizerClassName;
    protected String wsdlLocationURI;
    protected String targetNamespace;
    protected String logLevel;
    protected String platformClassName;
    protected boolean jarMode = false;

    public BuildDBWSWar() {
    }

    public void addTask(Task task) {
        // ignore
    }

    @Override
    public void execute() throws BuildException {
        generator.buildOROXProjects();
        super.execute();
    }

    // last chance to alter the contents of the compress'd stream;
    // add ZipEntry's for all the DBWS artifacts
    protected void finalizeZipOutputStream(ZipOutputStream zOut) throws IOException, BuildException {
        addIndexHTMLToArchive(zOut);
        addOracleWebservicesXMLToArchive(zOut);
        addWebXMLToArchive(zOut);
        addOrProjectToArchive(zOut);
        addOxProjectToArchive(zOut);
        addSessionsXMLToArchive(zOut);
        addDatabaseWebServiceToArchive(zOut);
        addSchemaToArchive(zOut);
        addAttachmentSchemaToArchive(zOut);
        addWSDLToArchive(zOut);
        super.finalizeZipOutputStream(zOut);
    }

    protected void addIndexHTMLToArchive(ZipOutputStream zOut) throws IOException {
        if (!jarMode) {
            byte[] buffer = new byte[BUF_SIZE];
            int bytesRead;
            log("Adding index.html to archive", MSG_VERBOSE);
            ZipEntry zipEntry = new ZipEntry("index.html");
            zOut.putNextEntry(zipEntry);
            StringBuilder sb = new StringBuilder(INDEX_HTML);
            DataInputStream dis = new DataInputStream(new StringInputStream(sb.toString()));
            while ((bytesRead = dis.read(buffer, 0, BUF_SIZE)) != -1) {
                zOut.write(buffer, 0, bytesRead);
            }
            zOut.flush();
            dis.close();
            zOut.closeEntry();
        }
    }

    protected void addOracleWebservicesXMLToArchive(ZipOutputStream zOut) throws IOException {
        if (!jarMode) {
            byte[] buffer = new byte[BUF_SIZE];
            int bytesRead;
            log("Adding " + ORACLE_WEBSERVICES_FILE + " to archive", MSG_VERBOSE);
            ZipEntry zipEntry = new ZipEntry(ORACLE_WEBSERVICES_FILE);
            zOut.putNextEntry(zipEntry);
            StringBuilder sb = new StringBuilder(ORACLE_WEBSERVICES_DESCRIPTOR);
            DataInputStream dis = new DataInputStream(new StringInputStream(sb.toString()));
            while ((bytesRead = dis.read(buffer, 0, BUF_SIZE)) != -1) {
                zOut.write(buffer, 0, bytesRead);
            }
            dis.close();
            zOut.flush();
            zOut.closeEntry();
        }
    }

    protected void addWebXMLToArchive(ZipOutputStream zOut) throws IOException {
        if (!jarMode) {
            byte[] buffer = new byte[BUF_SIZE];
            int bytesRead;
            log("Adding " + WEB_INF_DIR + "web.xml to archive", MSG_VERBOSE);
            ZipEntry zipEntry = new ZipEntry(WEB_INF_DIR + "web.xml");
            zOut.putNextEntry(zipEntry);
            StringBuilder sb = new StringBuilder(WEB_XML_PREAMBLE);
            sb.append(getContextRoot());
            sb.append(WEB_XML_POSTSCRIPT);
            DataInputStream dis = new DataInputStream(new StringInputStream(sb.toString()));
            while ((bytesRead = dis.read(buffer, 0, BUF_SIZE)) != -1) {
                zOut.write(buffer, 0, bytesRead);
            }
            zOut.flush();
            dis.close();
            zOut.closeEntry();
        }
    }

    protected void addOrProjectToArchive(ZipOutputStream zOut) throws IOException {
        Project p = null;
        String zipEntryStr = META_INF_DIR + DBWS_OR_XML;
        if (!jarMode) {
          zipEntryStr = WEB_INF_DIR + WAR_CLASSES_DIR + zipEntryStr;
        }
        String logString = "Adding " + zipEntryStr + " to archive";
        p = generator.getORProject();
        if (p != null) {
            log(logString, MSG_VERBOSE);
            ZipEntry zipEntry = new ZipEntry(zipEntryStr);
            zOut.putNextEntry(zipEntry);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLProjectWriter.write(p, new OutputStreamWriter(baos));
            zOut.write(baos.toByteArray(), 0, baos.size());
        }
        zOut.flush();
        zOut.closeEntry();
    }

    protected void addOxProjectToArchive(ZipOutputStream zOut) throws IOException {
        Project p = null;
        String zipEntryStr = META_INF_DIR + DBWS_OX_XML;
        if (!jarMode) {
          zipEntryStr = WEB_INF_DIR + WAR_CLASSES_DIR + zipEntryStr;
        }
        String logString = "Adding " + zipEntryStr + " to archive";
        p = generator.getOXProject();
        if (p != null) {
            log(logString, MSG_VERBOSE);
            ZipEntry zipEntry = new ZipEntry(zipEntryStr);
            zOut.putNextEntry(zipEntry);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLProjectWriter.write(p, new OutputStreamWriter(baos));
            zOut.write(baos.toByteArray(), 0, baos.size());
        }
        zOut.flush();
        zOut.closeEntry();
    }

    protected void addSessionsXMLToArchive(ZipOutputStream zOut) throws IOException {
        ZipEntry zipEntry = null;
        String zipEntryStr = META_INF_DIR;
        if (sessionsFileName != null && sessionsFileName.length() > 0) {
            zipEntryStr += sessionsFileName;
        }
        else {
            zipEntryStr += DBWS_SESSIONS_XML;
        }
        if (!jarMode) {
          zipEntryStr = WEB_INF_DIR + WAR_CLASSES_DIR + zipEntryStr;
        }
        log("Adding " + zipEntryStr + " to archive", MSG_VERBOSE);
        zipEntry = new ZipEntry(zipEntryStr);
        zOut.putNextEntry(zipEntry);
        generator.addSessionsXMLToStream(zOut);
        zOut.flush();
        zOut.closeEntry();
    }

    protected void addAttachmentSchemaToArchive(ZipOutputStream zOut) throws IOException, BuildException {
        if (generator.hasAttachments()) {
          log("Adding " + WSI_SWAREF_XSD_FILE + " to archive", MSG_VERBOSE);
          byte[] buffer = new byte[BUF_SIZE];
          int bytesRead;
          ZipEntry zipEntry = new ZipEntry(WSI_SWAREF_XSD_FILE);
          zOut.putNextEntry(zipEntry);
          DataInputStream dis = new DataInputStream(new StringInputStream(WSI_SWAREF_XSD));
          while ((bytesRead = dis.read(buffer, 0, BUF_SIZE)) != -1) {
              zOut.write(buffer, 0, bytesRead);
          }
          zOut.flush();
          zOut.closeEntry();
        }
    }

    protected void addWSDLToArchive(ZipOutputStream zOut) throws IOException, BuildException {
        if (!jarMode) {
            String logStr = "Adding " + WSDL_FILE + " to archive";
            log(logStr, MSG_VERBOSE);
            ZipEntry zipEntry = new ZipEntry(WSDL_FILE);
            zOut.putNextEntry(zipEntry);
            try {
                generator.addWSDLToStream(zOut);
            }
            catch (WSDLException e) {
                throw new BuildException(e.getMessage());
            }
            zOut.flush();
            zOut.closeEntry();
        }
    }

    protected void addDatabaseWebServiceToArchive(ZipOutputStream zOut) throws IOException {
        String zipEntryStr = META_INF_DIR + DBWS_SERVICE_XML;
        if (!jarMode) {
          zipEntryStr = WEB_INF_DIR + WAR_CLASSES_DIR + zipEntryStr;
        }
        log("Adding " + zipEntryStr + " to archive", MSG_VERBOSE);
        ZipEntry zipEntry = new ZipEntry(zipEntryStr);
        zOut.putNextEntry(zipEntry);
        generator.addDBWSToStream(zOut);
        zOut.flush();
        zOut.closeEntry();
    }

    protected void addSchemaToArchive(ZipOutputStream zOut) throws IOException {
        log("Adding " + DBWS_SCHEMA_XML + " to war", MSG_VERBOSE);
        ZipEntry zipEntry = new ZipEntry(DBWS_SCHEMA_XML);
        zOut.putNextEntry(zipEntry);
        generator.addSchemaToStream(zOut);
        zOut.flush();
        zOut.closeEntry();
    }

    // 'cause we do not know what changes the nested GenerateXXX element
    // may require, force the archive to <b>always</b> be updated
    protected ArchiveState getResourcesToAdd(FileSet[] filesets, File zipFile, boolean needsUpdate) {
        return super.getResourcesToAdd(filesets, zipFile, true);
    }

    protected void passParametersToGenerator() {
      generator.set_ProjectName(getProjectName());
      generator.set_SessionsFileName(getSessionsFileName());
      generator.set_SessionCustomizerClassName(getSessionCustomizerClassName());
      generator.set_ContextRoot(getContextRoot());
      generator.set_DataSource(getDataSource());
      generator.set_WSDLLocationURI(getWsdlLocationURI());
      generator.set_TargetNamespace(getTargetNameSpace());
      generator.set_LogLevel(getLogLevel());
      generator.set_PlatformClassName(getPlatformClassName());
    }
    public void addConfiguredGenerateFromTables(GenerateFromTables generator) {
        this.generator = generator;
        passParametersToGenerator();
    }

    public void addConfiguredGenerateFromSQL(GenerateFromSQL generator) {
        this.generator = generator;
        passParametersToGenerator();
    }

    public void addConfiguredGenerateFromStoredProcedures(GenerateFromStoredProcedures generator) {
        this.generator = generator;
        passParametersToGenerator();
    }

    @Override
    public void setDestFile(File destfile) {
        if (destfile.getAbsolutePath().endsWith(".jar")) {
          jarMode = true;
        }
        super.setDestFile(destfile);
    }

    public void setClasspath(Path classpath) {
        if (classPath == null) {
            classPath = classpath;
        } else {
            classPath.append(classpath);
        }
    }

    public Path getClasspath() {
        return ((JDBCTask)generator).getClasspath();
    }

    public Path createClasspath() {
        if (classPath == null) {
            classPath = new Path(getProject());
        }
        return classPath.createPath();
    }

    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSessionsFileName() {
        return sessionsFileName;
    }
    public void setSessionsFileName(String sessionsFileName) {
        this.sessionsFileName = sessionsFileName;
    }

    public String getSessionCustomizerClassName() {
        return sessionCustomizerClassName;
    }
    public void setSessionCustomizerClassName(String sessionCustomizerClassName) {
        this.sessionCustomizerClassName = sessionCustomizerClassName;
    }

    public String getContextRoot() {
        if (contextRoot == null) {
            contextRoot = "/" + projectName;
        }
        return contextRoot;
    }
    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }

    public String getDataSource() {
      return dataSource;
    }
    public void setDataSource(String dataSource) {
      this.dataSource = dataSource;
    }

    public String getWsdlLocationURI() {
        if (wsdlLocationURI == null) {
          setWsdlLocationURI(DEFAULT_WSDL_LOCATION_URI);
        }
        return wsdlLocationURI;
    }
    public void setWsdlLocationURI(String wsdlLocationURI) {
        this.wsdlLocationURI = wsdlLocationURI;
    }

    public String getTargetNameSpace() {
        if (targetNamespace == null) {
          setTargetNamespace("urn:" + projectName);
        }
        return targetNamespace;
    }
    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getLogLevel() {
        if (logLevel == null) {
            setLogLevel("off");
        }
        return logLevel;
    }
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    public String getPlatformClassName() {
        return platformClassName;
    }
    public void setPlatformClassName(String platformClassName) {
      this.platformClassName = platformClassName;
    }
}
