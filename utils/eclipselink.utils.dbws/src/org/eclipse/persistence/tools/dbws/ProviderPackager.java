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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

//java eXtension imports
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.XRPackager;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_CLASS_FILE;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;

/**
 * <p>
 * <b>INTERNAL:</b> ProviderPackager extends {@link XRPackager}. It is responsible for generating<br>
 * the JAX-WS {@link Provider} and saves the generated WSDL to ${stageDir}
 * <pre>
 * ${PACKAGER_ROOT}
 *   | DBWSProvider.class     -- code-generated javax.xml.ws.Provider
 * </pre>
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class ProviderPackager extends XRPackager {

    public static final String DBWS_PROVIDER_SOURCE_PREAMBLE =
        "package _dbws;\n" +
        "\n//javase imports\n" +
        "import java.lang.reflect.Method;\n" +
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
        "import javax.xml.ws.handler.MessageContext;\n" +
        "import javax.xml.ws.soap.SOAPBinding;\n" +
        "import static javax.xml.ws.Service.Mode.MESSAGE;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING;\n" +        
        "\n//EclipseLink imports\n" +
        "import " + ProviderHelper.class.getName() + ";\n" +
        "\n" +
        "@WebServiceProvider(\n";
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
        "public class DBWSProvider extends ProviderHelper implements Provider<SOAPMessage> {\n" +
        "\n" +
        "    // Container injects wsContext here\n" +
        "    @Resource\n" +
        "    protected WebServiceContext wsContext;\n" +
        "    public  DBWSProvider() {\n" +
        "        super();\n" +
        "    }\n" +
        "    private static final String CONTAINER_RESOLVER_CLASSNAME =\n" +
        "        \"com.sun.xml.ws.api.server.ContainerResolver\";\n" +
        "    @PostConstruct\n" +
        "    public void init() {\n" +
        "        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();\n" +
        "        ServletContext sc = null;\n" +
        "        //ServletContext sc = \n" +
        "        //    ContainerResolver.getInstance().getContainer().getSPI(ServletContext.class);\n" +
        "        try {\n" +
        "            Class<?> containerResolverClass = parentClassLoader.loadClass(\n" +
        "                CONTAINER_RESOLVER_CLASSNAME);\n" +
        "            Method getInstanceMethod = containerResolverClass.getMethod(\"getInstance\");\n" +
        "            Object containerResolver = getInstanceMethod.invoke(null);\n" +
        "            Method getContainerMethod = containerResolver.getClass().getMethod(\"getContainer\");\n" +
        "            getContainerMethod.setAccessible(true);\n" +
        "            Object container = getContainerMethod.invoke(containerResolver);\n" +
        "            Method getSPIMethod = container.getClass().getMethod(\"getSPI\", Class.class);\n" +
        "            getSPIMethod.setAccessible(true);\n" +
        "            sc = (ServletContext)getSPIMethod.invoke(container, ServletContext.class);\n" +
        "        }\n" +
        "        catch (Exception e) {\n" +
        "            // if the above doesn't work, then maybe we are running in JavaSE 6 'containerless' mode\n" +
        "            // we can live with a null ServletContext (just use the parentClassLoader to load resources \n" +
        "        }\n" +
        "        boolean mtomEnabled = false;\n" +
        "        BindingType thisBindingType = this.getClass().getAnnotation(BindingType.class);\n" +
        "        if (thisBindingType != null) {\n" +
        "            if (thisBindingType.value().toLowerCase().contains(\"mtom=true\")) {\n" +
        "                mtomEnabled = true;\n" +
        "            }\n" +
        "        }\n" +
        "        super.init(parentClassLoader, sc, mtomEnabled);\n" +
        "    }\n" +
        "    @Override\n" +
        "    public SOAPMessage invoke(SOAPMessage request) {\n" +
        "        if (wsContext != null) {\n" +
        "            setMessageContext(wsContext.getMessageContext());\n" +
        "        }\n" +
        "        return super.invoke(request);\n" +
        "    }\n" +
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
    public void writeProvider(OutputStream sourceProviderStream,
        OutputStream codeGenProviderStream, DBWSBuilder builder) {
        
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
            catch (IOException e) {/* ignore */}
        }
        
        if (codeGenProviderStream != __nullStream) {
            DBWSProviderCompiler providerCompiler = new DBWSProviderCompiler();
            if (providerCompiler.getCompiler() == null) {
                throw new IllegalStateException("DBWSBuilder cannot compile DBWSProvider code\n" +
                    "Please ensure that tools.jar is on your classpath");
            }
            byte[] bytes = providerCompiler.compile(source.toString());
            if (bytes.length == 0) {
                DiagnosticCollector<JavaFileObject> collector = 
                    providerCompiler.getDiagnosticsCollector();
                StringBuilder diagBuf = 
                    new StringBuilder("DBWSBuilder cannot generate DBWSProvider code " +
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
                    codeGenProviderStream.write(bytes, 0, bytes.length);
                }
                catch (IOException e) {/* ignore */}
            }
        }
    }
}