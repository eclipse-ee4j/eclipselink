/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

//EclipseLink
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.META_INF_PATHS;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.internal.xr.Util.WSDL_DIR;
import static org.eclipse.persistence.tools.dbws.Util.CLASSES;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;

public class WarArchiver extends JarArchiver {

    static final String DEFAULT_WAR_FILENAME = "dbws.war";

    public WarArchiver() {
    }
    public WarArchiver(DBWSPackager packager) {
        super(packager);
    }

    @Override
    public void setFilename(String jarFilename) {
        if (!(jarFilename.endsWith(".war"))) {
            jarFilename += ".war";
        }
        this.jarFilename = jarFilename;
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

    protected ZipEntry getDBWSProviderClassJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + "/" + UNDER_DBWS + "/" + DBWS_PROVIDER_CLASS_FILE);
    }

    protected ZipEntry getDBWSProviderSourceJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + "/" + UNDER_DBWS + "/" + DBWS_PROVIDER_SOURCE_FILE);
    }

    protected ZipEntry getProviderListenerSourceJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + "/" + UNDER_DBWS + "/" + PROVIDER_LISTENER_SOURCE_FILE);
    }

    protected ZipEntry getProviderListenerClassJarEntry() {
        return new JarEntry(WEB_INF_DIR + CLASSES + "/" + UNDER_DBWS + "/" + PROVIDER_LISTENER_CLASS_FILE);
    }

    protected ZipEntry getWSDLJarEntry() {
        return new JarEntry(WEB_INF_DIR + WSDL_DIR + DBWS_WSDL);
    }

    @Override
    protected JarOutputStream buildJarOutputStream() {
        JarOutputStream jarOutputStream = null;
        try {
            if (jarFilename == null || jarFilename.length() == 0) {
                jarFilename = DEFAULT_WAR_FILENAME;
            }
            jarOutputStream = new JarOutputStream(new FileOutputStream(
                new File(packager.getStageDir(), jarFilename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jarOutputStream;
    }

    @Override
    protected void addFilesToJarOutputStream(JarOutputStream jarOutputStream) {
        super.addFilesToJarOutputStream(jarOutputStream);

        /* and more ...
         * web.xml
         * DBWSProvider.class
         * DBWSProvider.java
         * eclipselink-dbws.wsdl
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

            jarOutputStream.putNextEntry(getDBWSProviderClassJarEntry());
            f = new File(packager.getStageDir(), DBWS_PROVIDER_CLASS_FILE);
            fis = new FileInputStream(f);
            for (int read = 0; read != -1; read = fis.read(buffer)) {
                jarOutputStream.write(buffer, 0, read);
            }
            fis.close();
            f.deleteOnExit();

            // DBWS Provider source is optional
            f = new File(packager.getStageDir(), DBWS_PROVIDER_SOURCE_FILE);
            if (f.length() > 0) {
                jarOutputStream.putNextEntry(getDBWSProviderSourceJarEntry());
                fis = new FileInputStream(f);
                for (int read = 0; read != -1; read = fis.read(buffer)) {
                    jarOutputStream.write(buffer, 0, read);
                }
                fis.close();
            }
            f.deleteOnExit();

            // ProviderListener source is optional
            f = new File(packager.getStageDir(), PROVIDER_LISTENER_SOURCE_FILE);
            if (f.length() > 0) {
                jarOutputStream.putNextEntry(getProviderListenerSourceJarEntry());
                fis = new FileInputStream(f);
                for (int read = 0; read != -1; read = fis.read(buffer)) {
                    jarOutputStream.write(buffer, 0, read);
                }
                fis.close();
            }
            f.deleteOnExit();

            jarOutputStream.putNextEntry(getProviderListenerClassJarEntry());
            f = new File(packager.getStageDir(), PROVIDER_LISTENER_CLASS_FILE);
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