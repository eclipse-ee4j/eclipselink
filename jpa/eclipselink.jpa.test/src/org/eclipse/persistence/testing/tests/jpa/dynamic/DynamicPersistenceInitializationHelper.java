package org.eclipse.persistence.testing.tests.jpa.dynamic;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

//java eXtension imports
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;

//Eclipselink imports
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryProvider;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.Archive;
import org.eclipse.persistence.internal.jpa.deployment.ArchiveFactoryImpl;
import org.eclipse.persistence.internal.jpa.deployment.JPAInitializer;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceInitializationHelper;
import org.eclipse.persistence.internal.jpa.deployment.PersistenceUnitProcessor;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;

public class DynamicPersistenceInitializationHelper extends PersistenceInitializationHelper {

    static final String CUSTOM_PROTOCOL = "string:";

    protected URL dynamicTestUrl = null; 
    
    public DynamicPersistenceInitializationHelper(String dynamicPersistenceXML) {
        super();
        try {
            dynamicTestUrl = new URL(null, CUSTOM_PROTOCOL, 
                new StringHandler(dynamicPersistenceXML));
        }
        catch (MalformedURLException e) {
            // ignore
        }
    }
    
    @Override
    public Enumeration<URL> getPersistenceResources(ClassLoader classloader) {
        NonSynchronizedVector urls = new NonSynchronizedVector(1);
        if (dynamicTestUrl != null) {
            urls.add(dynamicTestUrl);
        }
        return urls.elements();
    }
    
    @Override
    public JPAInitializer getInitializer(ClassLoader classLoader, Map map) {
        return new DynamicPersistenceJPAInitializer(classLoader, map, this);
    }

    private static final class DynamicPersistenceJPAInitializer extends JPAInitializer {

        Map map;
        PersistenceInitializationHelper helper;
        DynamicPersistenceJPAInitializer(ClassLoader classLoader, Map map, PersistenceInitializationHelper helper) {
            this.initializationClassloader = classLoader;
            this.map = map;
            this.map.put(PersistenceUnitProperties.WEAVING, "false");
            this.helper = helper;
            shouldCreateInternalLoader = false;
        }
        
        @Override
        public void initialize(Map m, PersistenceInitializationHelper persistenceHelper) {
            try {
                Archive archive = new ArchiveFactoryImpl() {
                    @Override
                    protected boolean isJarInputStream(URL url) throws IOException {
                        return false;
                    }
                }.createArchive(helper.getPersistenceResources(initializationClassloader).nextElement());
                SEPersistenceUnitInfo persistenceUnitInfo = 
                    PersistenceUnitProcessor.getPersistenceUnits(archive, 
                        initializationClassloader).iterator().next();
                String puName = persistenceUnitInfo.getPersistenceUnitRootUrl() +
                    "../" + persistenceUnitInfo.getPersistenceUnitName();
                EntityManagerFactoryProvider.persistenceUnits.put(puName, persistenceUnitInfo);
                EntityManagerSetupImpl emSetupImpl = new EntityManagerSetupImpl();
                emSetupImpl.setPersistenceInitializationHelper(helper);
                emSetupImpl.predeploy(persistenceUnitInfo, m);
                EntityManagerFactoryProvider.addEntityManagerSetupImpl(puName, emSetupImpl);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void registerTransformer(ClassTransformer transformer, PersistenceUnitInfo persistenceUnitInfo) {
        }
        @Override
        protected ClassLoader createTempLoader(Collection col, boolean shouldOverrideLoadClassForCollectionMembers) {
            return null;
        }
        @Override
        protected ClassLoader createTempLoader(Collection col) {
            return null;
        }
        @Override
        public void checkWeaving(Map properties) {
        }
    }
    
    private static final class StringHandler extends URLStreamHandler {
        ByteArrayInputStream bais = null;
        public StringHandler(String dynamicPersistenceXML) {
            bais = new ByteArrayInputStream(dynamicPersistenceXML.getBytes());
        }
        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return new StringURLConnection(url, bais);
        }
    }
    private static final class StringURLConnection extends URLConnection {
        ByteArrayInputStream bais;
        protected StringURLConnection(URL url, ByteArrayInputStream bais) {
            super(url);
            this.bais = bais;
        }     
        @Override
        public InputStream getInputStream() throws IOException {
            return bais;
        }
        @Override
        public void connect() throws IOException {
        }
    }
}