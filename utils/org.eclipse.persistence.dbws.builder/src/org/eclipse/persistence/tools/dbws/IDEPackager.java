/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - July 13 2010
 *       fix for https://bugs.eclipse.org/bugs/show_bug.cgi?id=318207
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

//EclipseLink imports
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.internal.xr.Util.WSDL_DIR;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;

public class IDEPackager extends JSR109WebServicePackager {

    public static final String SRC_DIR = "src";

    protected File srcDir;
    protected String srcDirname;
    protected File publicHTMLDir;
    protected String publicHTMLDirname;
    protected File webInfDir;
    protected File wsdlDir;
    protected File underDBWSDir;

    public IDEPackager() {
        super();
    }
    public IDEPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }


    @Override
    public String getArchiverLabel() {
        return "not supported";
    }
    @Override
    public String getAdditionalUsage() {
        return null;
    }

    @Override
    public Archiver buildDefaultArchiver() {
        return null;
    }

    // create streams for IDE project layout

    @Override
    public OutputStream getSchemaStream() throws FileNotFoundException {
        buildWSDLDir();
        return new FileOutputStream(new File(wsdlDir, DBWS_SCHEMA_XML));
    }

    @Override
    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, sessionsFileName));
    }

    @Override
    public OutputStream getServiceStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_SERVICE_XML));
    }

    @Override
    public OutputStream getOrStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_OR_XML));
    }

    @Override
    public OutputStream getOxStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_OX_XML));
    }

    @Override
    public OutputStream getWSDLStream() throws FileNotFoundException {
        buildWSDLDir();
        return new FileOutputStream(new File(wsdlDir, DBWS_WSDL));
    }

    @Override
    public OutputStream getSWARefStream() throws FileNotFoundException {
        if (hasAttachments) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, SWAREF_FILENAME));
        }
        else {
            return XRPackager.__nullStream;
        }
    }

    @Override
    public OutputStream getWebXmlStream() throws FileNotFoundException {
        buildWebInfDir();
        return new FileOutputStream(new File(webInfDir, WEB_XML_FILENAME));
    }

    // we doesn't need .class files (you're in an IDE, just compile the source!)
    @Override
    public OutputStream getProviderClassStream() throws FileNotFoundException {
        return XRPackager.__nullStream;
    }
    @Override
    public OutputStream getProviderSourceStream() throws FileNotFoundException {
        buildUnderDBWS();
        return new FileOutputStream(new File(underDBWSDir, DBWS_PROVIDER_SOURCE_FILE));
    }
    @Override
    public OutputStream getProviderListenerClassStream() throws FileNotFoundException {
        return XRPackager.__nullStream;
    }
    @Override
    public OutputStream getProviderListenerSourceStream() throws FileNotFoundException {
        return new FileOutputStream(new File(underDBWSDir, PROVIDER_LISTENER_SOURCE_FILE));
    }

    protected void buildSrcDir() throws FileNotFoundException {
        srcDir = new File(stageDir, srcDirname);
        if (!srcDir.exists()) {
            boolean worked = srcDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    srcDirname + " under " + stageDir);
            }
        }
    }
    protected void buildUnderDBWS() throws FileNotFoundException {
        buildSrcDir();
        underDBWSDir = new File(srcDir, UNDER_DBWS);
        if (!underDBWSDir.exists()) {
            boolean worked = underDBWSDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + srcDirname + "/" + UNDER_DBWS +
                    " dir under " + stageDir);
            }
        }
    }

    protected void buildPublicHTMLDir() throws FileNotFoundException {
        publicHTMLDir = new File(stageDir, publicHTMLDirname);
        if (!publicHTMLDir.exists()) {
            boolean worked = publicHTMLDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    publicHTMLDirname + " under " + stageDir);
            }
        }
    }

    protected void buildWebInfDir() throws FileNotFoundException {
        buildPublicHTMLDir();
        webInfDir = new File(publicHTMLDir, WEB_INF_DIR);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_DIR + " under " + publicHTMLDirname);
            }
        }
    }

    protected void buildWSDLDir() throws FileNotFoundException {
        buildWebInfDir();
        wsdlDir = new File(webInfDir, WSDL_DIR);
        if (!wsdlDir.exists()) {
            boolean worked = wsdlDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WSDL_DIR + " under " + WEB_INF_DIR);
            }
        }
    }
}