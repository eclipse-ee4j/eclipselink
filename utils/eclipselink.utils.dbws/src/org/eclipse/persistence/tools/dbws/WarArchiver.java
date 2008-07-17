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

// javase imports
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import static java.util.jar.Attributes.Name.MANIFEST_VERSION;

// EclipseLink
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.internal.xr.Util.WSDL_DIR;
import static org.eclipse.persistence.tools.dbws.DBWSBasePackager.__nullStream;
import static org.eclipse.persistence.tools.dbws.Util.CLASSES;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.WEBSERVICES_FILENAME;

public class WarArchiver extends SimpleJarArchiver {

    static final String DEFAULT_WAR_FILENAME = "aDBWSservice.war";
    static final String DEFAULT_MANIFEST =
        MANIFEST_VERSION.toString() + ": 1.0\n" +
        "Created-by: DBWSBuilder WarArchiver 1.0\n\n";

    public WarArchiver(DBWSPackager packager) {
        this(packager, DEFAULT_WAR_FILENAME);
    }
    public WarArchiver(DBWSPackager packager, String warFilename) {
        super(packager, warFilename);
    }

    @Override
    protected Manifest buildManifest() {
        Manifest manifest = null;
        try {
            new Manifest(new ByteArrayInputStream(DEFAULT_MANIFEST.getBytes("ISO-8859-1")));
        }
        catch (Exception e) {
            // e.printStackTrace();
        }
        return manifest;
    }

    @Override
    protected JarEntry getOrJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + META_INF_PATHS[1] + DBWS_OR_XML);
    }

    @Override
    protected JarEntry getOxJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + META_INF_PATHS[1] + DBWS_OX_XML);
    }

    @Override
    protected JarEntry getSchemaJarEntry() {
        return new JarEntry(WEB_INF_DIR + WSDL_DIR + DBWS_SCHEMA_XML);
    }

    @Override
    protected JarEntry getServiceJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + META_INF_PATHS[1] + DBWS_SERVICE_XML);
    }

    @Override
    protected JarEntry getSessionsJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + META_INF_PATHS[1] +
            packager.getSessionsFileName());
    }

    @Override
    protected JarEntry getSWARefJarEntry() {
        return new JarEntry(WEB_INF_DIR + WSDL_DIR + SWAREF_FILENAME);
    }

    @Override
    public String getOrProjectPathPrefix() {
        return META_INF_PATHS[1];
    }

    @Override
    public String getOxProjectPathPrefix() {
        return META_INF_PATHS[1];
    }

    protected ZipEntry getWebXmlJarEntry() {
        return new JarEntry(WEB_INF_DIR + WEB_XML_FILENAME);
    }

    protected ZipEntry getWebservicesJarEntry() {
        return new JarEntry(WEB_INF_DIR + WEBSERVICES_FILENAME);
    }

    protected ZipEntry getDBWSProviderClassJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + "/" + UNDER_DBWS + "/" + DBWS_PROVIDER_CLASS_FILE);
    }

    protected ZipEntry getDBWSProviderSourceJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + "/" + UNDER_DBWS + "/" + DBWS_PROVIDER_SOURCE_FILE);
    }

    protected ZipEntry getWSDLJarEntry() {
        return new JarEntry(WEB_INF_DIR + WSDL_DIR + DBWS_WSDL);
    }

    @Override
    protected void addFilesToJarOutputStream(JarOutputStream jarOutputStream) {
        super.addFilesToJarOutputStream(jarOutputStream);

        /* and more ...
         * web.xml
         * webservices.xml
         * DBWSProvider.class
         * DBWSProvider.java
         * eclipselink-dbws.wsdl</b>
         */
        try {
            jarOutputStream.putNextEntry(getWebXmlJarEntry());
            f = new File(packager.getStageDir(), WEB_XML_FILENAME);
            fis = new FileInputStream(f);
            for (int read = 0; read != -1; read = fis.read(buffer)) {
                jarOutputStream.write(buffer, 0, read);
            }
            fis.close();
            f.deleteOnExit();

            if (packager.getWebservicesXmlStream() != __nullStream) {
                jarOutputStream.putNextEntry(getWebservicesJarEntry());
                f = new File(packager.getStageDir(), WEBSERVICES_FILENAME);
                fis = new FileInputStream(f);
                for (int read = 0; read != -1; read = fis.read(buffer)) {
                    jarOutputStream.write(buffer, 0, read);
                }
                fis.close();
                f.deleteOnExit();
            }

            jarOutputStream.putNextEntry(getDBWSProviderClassJarEntry());
            f = new File(packager.getStageDir(), DBWS_PROVIDER_CLASS_FILE);
            fis = new FileInputStream(f);
            for (int read = 0; read != -1; read = fis.read(buffer)) {
                jarOutputStream.write(buffer, 0, read);
            }
            fis.close();
            f.deleteOnExit();

            jarOutputStream.putNextEntry(getDBWSProviderSourceJarEntry());
            f = new File(packager.getStageDir(), DBWS_PROVIDER_SOURCE_FILE);
            fis = new FileInputStream(f);
            for (int read = 0; read != -1; read = fis.read(buffer)) {
                jarOutputStream.write(buffer, 0, read);
            }
            fis.close();
            f.deleteOnExit();

            jarOutputStream.putNextEntry(getWSDLJarEntry());
            f = new File(packager.getStageDir(), DBWS_WSDL);
            fis = new FileInputStream(f);
            for (int read = 0; read != -1; read = fis.read(buffer)) {
                jarOutputStream.write(buffer, 0, read);
            }
            fis.close();
            f.deleteOnExit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}