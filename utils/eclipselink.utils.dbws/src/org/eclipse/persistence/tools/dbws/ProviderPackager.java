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
 *     Mike Norman - Oct 30 2008: some re-work of DBWSPackager hierarchy
 ******************************************************************************/
package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

//java eXtension imports
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

//EclipseLink imports
import _dbws.ProviderListener;
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DOT_CLASS;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.PROVIDER_LISTENER_SOURCE_FILE;

/**
 * <p>
 * <b>INTERNAL:</b> ProviderPackager extends {@link XRPackager}. It is responsible for generating<br>
 * the {@link ServletContextListener} and the JAX-WS {@link Provider} and saves the generated WSDL
 * to ${stageDir}
 * <pre>
 * ${PACKAGER_ROOT}
 *   | DBWSProvider.class     -- code-generated javax.xml.ws.Provider
 *   | ProviderListener.class -- code-generated javax.servlet.ServletContextListener
 * </pre>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class ProviderPackager extends XRPackager {

    public static final String PROVIDER_NAME = "_dbws.DBWSProvider";

    public static final String PROVIDER_LISTENER_SOURCE =
        "package _dbws;\n\n" +
        "import javax.servlet.ServletContext;\n" +
        "import javax.servlet.ServletContextEvent;\n" +
        "import javax.servlet.ServletContextListener;\n\n" +
        "public class ProviderListener implements ServletContextListener {\n\n" +
        "    public static ServletContext SC = null;\n\n" +
        "    public  ProviderListener() {\n" +
        "        super();\n" +
        "    }\n\n" +
        "    public void contextInitialized(ServletContextEvent sce) {\n" +
        "        SC = sce.getServletContext();\n" +
        "    }\n\n" +
        "    public void contextDestroyed(ServletContextEvent sce) {\n" +
        "        // no-op\n" +
        "    }\n" +
        "}\n";

    public static final String DBWS_PROVIDER_SOURCE_PREAMBLE =
        "package _dbws;\n" +
        "\n//Java extension libraries\n" +
        "import javax.annotation.PostConstruct;\n" +
        "import javax.annotation.PreDestroy;\n" +
        "import javax.annotation.Resource;\n" +
        "import javax.servlet.ServletContext;\n" +
        "import javax.xml.soap.SOAPMessage;\n" +
        "import javax.xml.ws.BindingType;\n" +
        "import javax.xml.ws.Provider;\n" +
        "import javax.xml.ws.ServiceMode;\n" +
        "import javax.xml.ws.WebServiceContext;\n" +
        "import javax.xml.ws.WebServiceProvider;\n" +
        "import static javax.xml.ws.Service.Mode.MESSAGE;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING;\n" +
        "\n//EclipseLink imports\n" +
        "import " + ProviderHelper.class.getName() + ";\n" +
        "\n@WebServiceProvider(\n";
    public static final String DBWS_PROVIDER_SOURCE_WSDL_LOCATION =
        "    wsdlLocation = \"WEB-INF/wsdl/eclipselink-dbws.wsdl\",\n";
    public static final String DBWS_PROVIDER_SOURCE_SERVICE_NAME =
        "    serviceName = \"";
    public static final String DBWS_PROVIDER_SOURCE_PORT_NAME =
        "\",\n    portName = \"";
    public static final String DBWS_PROVIDER_SOURCE_TARGET_NAMESPACE =
        "\",\n    targetNamespace = \"";
    public static final String DBWS_PROVIDER_SOURCE_SUFFIX =
        "\"\n)\n@ServiceMode(MESSAGE)\n";

    public static final String DBWS_PROVIDER_SOAP12_BINDING =
        "@BindingType(value=SOAP12HTTP_BINDING)\n";
    public static final String DBWS_PROVIDER_SOAP11_MTOM_BINDING =
        "@BindingType(value=SOAP11HTTP_MTOM_BINDING)\n";
    public static final String DBWS_PROVIDER_SOAP12_MTOM_BINDING =
        "@BindingType(value=SOAP12HTTP_MTOM_BINDING)\n";

    public static final String DBWS_PROVIDER_SOURCE_CLASSDEF =
        "public class DBWSProvider extends ProviderHelper implements Provider<SOAPMessage> {\n\n" +
        "    // Container injects wsContext here\n" +
        "    @Resource\n" +
        "    protected WebServiceContext wsContext;\n\n" +
        "    public  DBWSProvider() {\n" +
        "        super();\n" +
        "    }\n\n" +
        "    @PostConstruct\n" +
        "    public void init() {\n" +
        "        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();\n" +
        "        ServletContext sc = ProviderListener.SC;\n" +
        "        boolean mtomEnabled = false;\n" +
        "        BindingType thisBindingType = this.getClass().getAnnotation(BindingType.class);\n" +
        "        if (thisBindingType != null) {\n" +
        "            if (thisBindingType.value().toLowerCase().contains(\"mtom=true\")) {\n" +
        "                mtomEnabled = true;\n" +
        "            }\n" +
        "        }\n" +
        "        super.init(parentClassLoader, sc, mtomEnabled);\n" +
        "    }\n\n" +
        "    @Override\n" +
        "    public SOAPMessage invoke(SOAPMessage request) {\n" +
        "        if (wsContext != null) {\n" +
        "            setMessageContext(wsContext.getMessageContext());\n" +
        "        }\n" +
        "        return super.invoke(request);\n" +
        "    }\n\n" +
        "    @Override\n" +
        "    @PreDestroy\n" +
        "    public void destroy() {\n" +
        "        super.destroy();\n" +
        "    }\n" +
        "};\n";

    public ProviderPackager() {
        this(new WarArchiver(),"provider", noArchive);
    }
    protected ProviderPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

    @Override
    public Archiver buildDefaultArchiver() {
        return new WarArchiver(this);
    }

    @Override
    public OutputStream getWSDLStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_WSDL));
    }

    @Override
    public OutputStream getProviderClassStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_PROVIDER_CLASS_FILE));
    }

    @Override
    public OutputStream getProviderSourceStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, DBWS_PROVIDER_SOURCE_FILE));
    }

    @Override
    public OutputStream getProviderListenerSourceStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, PROVIDER_LISTENER_SOURCE_FILE));
    }

    @Override
    public OutputStream getProviderListenerClassStream() throws FileNotFoundException {
        return new FileOutputStream(new File(stageDir, PROVIDER_LISTENER_CLASS_FILE));

    }

    static final int DEFAULT_BUFFER_SIZE = 4096;
    @Override
    public void writeProvider(OutputStream sourceProviderStream, OutputStream classProviderStream,
        OutputStream sourceProviderListenerStream, OutputStream classProviderListenerStream,
        DBWSBuilder builder) {

        StringBuilder source = new StringBuilder(DBWS_PROVIDER_SOURCE_PREAMBLE);
        String wsdlPathPrevix = getWSDLPathPrefix();
        if (wsdlPathPrevix != null) {
            source.append(DBWS_PROVIDER_SOURCE_WSDL_LOCATION);
        }
        source.append(DBWS_PROVIDER_SOURCE_SERVICE_NAME);
        String serviceName = builder.getWSDLGenerator().getServiceName();
        source.append(serviceName);
        source.append(DBWS_PROVIDER_SOURCE_PORT_NAME);
        source.append(serviceName + "Port");
        source.append(DBWS_PROVIDER_SOURCE_TARGET_NAMESPACE);
        source.append(builder.getWSDLGenerator().getServiceNameSpace());
        source.append(DBWS_PROVIDER_SOURCE_SUFFIX);
        if (builder.usesSOAP12()) {
            if (builder.mtomEnabled()) {
                source.append(DBWS_PROVIDER_SOAP12_MTOM_BINDING);
            }
            else {
                source.append(DBWS_PROVIDER_SOAP12_BINDING);
            }
        }
        else {
            if (builder.mtomEnabled()) {
                source.append(DBWS_PROVIDER_SOAP11_MTOM_BINDING);
            }
            // else the default BindingType, don't have to explicitly set it
        }
        source.append(DBWS_PROVIDER_SOURCE_CLASSDEF);
        if (sourceProviderStream != __nullStream) {
            OutputStreamWriter osw =
                new OutputStreamWriter(new BufferedOutputStream(sourceProviderStream));
            try {
                osw.write(source.toString());
                osw.flush();
            }
            catch (IOException e) {}
        }

        if (classProviderStream != __nullStream) {
            InMemoryCompiler providerCompiler = new InMemoryCompiler(PROVIDER_NAME);
            if (providerCompiler.getCompiler() == null) {
                throw new IllegalStateException("DBWSBuilder cannot compile DBWSProvider code\n" +
                    "Please ensure that tools.jar is on your classpath");
            }
            byte[] bytes = providerCompiler.compile(source);
            if (bytes.length == 0) {
                DiagnosticCollector<JavaFileObject> collector = providerCompiler.getDiagnosticsCollector();
                StringBuilder diagBuf =
                    new StringBuilder("DBWSBuilder cannot generate ProviderListener code " +
                        "(likely servlet jar missing from classpath)\n");
                for (Diagnostic<? extends JavaFileObject> d : collector.getDiagnostics()) {
                    if (d.getKind() == Diagnostic.Kind.ERROR) {
                        diagBuf.append(d.getMessage(null));
                        diagBuf.append("\n");
                    }
                }
                throw new IllegalStateException(diagBuf.toString());
            }
            else {
                try {
                    classProviderStream.write(bytes, 0, bytes.length);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (sourceProviderListenerStream != __nullStream) {
            OutputStreamWriter osw =
                new OutputStreamWriter(new BufferedOutputStream(sourceProviderListenerStream));
            try {
                osw.write(PROVIDER_LISTENER_SOURCE);
                osw.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (classProviderListenerStream != __nullStream) {
            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                    ProviderListener.class.getName().replace('.', '/') + DOT_CLASS);
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = is.read(buffer))) {
                    classProviderListenerStream.write(buffer, 0, n);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}