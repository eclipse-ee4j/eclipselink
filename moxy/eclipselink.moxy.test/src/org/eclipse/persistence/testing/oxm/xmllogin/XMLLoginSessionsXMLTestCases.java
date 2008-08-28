package org.eclipse.persistence.testing.oxm.xmllogin;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigWriter;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.internal.sessions.factories.model.login.AppendNewElementsOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.DescriptorLevelDocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.IgnoreNewElementsOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.NoDocumentPreservationPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.RelativePositionOrderingPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.XMLBinderPolicyConfig;
import org.eclipse.persistence.internal.sessions.factories.model.login.XMLLoginConfig;
import org.eclipse.persistence.internal.sessions.factories.model.session.DatabaseSessionConfig;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.factories.XMLSessionConfigLoader;

public class XMLLoginSessionsXMLTestCases extends TestCase {

    private DatabaseSessionConfig m_sessionConfig;
    private XMLLoginConfig m_loginConfig;

    private final String SESSION_NAME = "XMLLoginSessionsXMLTestCases";
    private final String SESSION_FILE_NAME = "XMLLoginSessionsXMLTestCases-sessions.xml";
    
    public XMLLoginSessionsXMLTestCases(String name) {
        super(name);
    }

    public void setUp() {
        m_sessionConfig = new DatabaseSessionConfig();
        m_sessionConfig.setName(SESSION_NAME);
        m_loginConfig = new XMLLoginConfig();
        m_sessionConfig.setLoginConfig(m_loginConfig);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmllogin.XMLLoginSessionsXMLTestCases" };
        junit.textui.TestRunner.main(arguments);
    }
    
    // Bug 242452 - test 'DatasourcePlatform' in sessions.xml
    public void testDataSourcePlatform() {
        DatabaseSessionConfig roundTrippedConfig;
        XMLLoginConfig roundTrippedLoginConfig;
        
        // DatasourcePlatform = SAXPlatform
        // ================================        
        m_loginConfig.setPlatformClass(new SAXPlatform().getClass().getName());
        
        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig();
        
        assertEquals(m_loginConfig.getPlatformClass(), roundTrippedLoginConfig.getPlatformClass());
        
        // DatasourcePlatform = DOMPlatform
        // ================================        
        m_loginConfig.setPlatformClass(new DOMPlatform().getClass().getName());
        
        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig();
        
        assertEquals(m_loginConfig.getPlatformClass(), roundTrippedLoginConfig.getPlatformClass());
    }

    // Bug - test 'EqualNamespaceResolvers' in sessions.xml
    public void testEqualNamespaceResolvers() {
        DatabaseSessionConfig roundTrippedConfig; 
        XMLLoginConfig roundTrippedLoginConfig; 
        
        // EqualNamespaceResolvers = true
        // ==============================
        m_loginConfig.setEqualNamespaceResolvers(true);

        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig();
        
        assertEquals(m_loginConfig.getEqualNamespaceResolvers(), roundTrippedLoginConfig.getEqualNamespaceResolvers());
        
        // EqualNamespaceResolvers = false
        // ===============================
        m_loginConfig.setEqualNamespaceResolvers(false);

        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig();
        
        assertEquals(m_loginConfig.getEqualNamespaceResolvers(), roundTrippedLoginConfig.getEqualNamespaceResolvers());
    }
    
    // Bug - test 'DocumentPreservationPolicy' in sessions.xml
    public void testDocumentPreservationPolicy() {
        DatabaseSessionConfig roundTrippedConfig; 
        XMLLoginConfig roundTrippedLoginConfig; 
        
        // DocumentPreservationPolicy = DescriptorLevelDocumentPreservationPolicy
        // NodeOrderingPolicy = AppendNewElementsOrderingPolicy
        // ======================================================================
        m_loginConfig.setDocumentPreservationPolicy(new DescriptorLevelDocumentPreservationPolicyConfig());
        m_loginConfig.getDocumentPreservationPolicy().setNodeOrderingPolicy(new AppendNewElementsOrderingPolicyConfig());

        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig(); 
        
        assertEquals(m_loginConfig.getDocumentPreservationPolicy().getClass(), roundTrippedLoginConfig.getDocumentPreservationPolicy().getClass());
        assertEquals(m_loginConfig.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass(), 
                roundTrippedLoginConfig.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass());
        
        // DocumentPreservationPolicy = NoDocumentPreservationPolicy
        // NodeOrderingPolicy = IgnoreNewElementsOrderingPolicy
        // =========================================================
        m_loginConfig.setDocumentPreservationPolicy(new NoDocumentPreservationPolicyConfig());
        m_loginConfig.getDocumentPreservationPolicy().setNodeOrderingPolicy(new IgnoreNewElementsOrderingPolicyConfig());

        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig(); 
        
        assertEquals(m_loginConfig.getDocumentPreservationPolicy().getClass(), roundTrippedLoginConfig.getDocumentPreservationPolicy().getClass());
        assertEquals(m_loginConfig.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass(), 
                roundTrippedLoginConfig.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass());
        
        // DocumentPreservationPolicy = XMLBinderPolicy
        // NodeOrderingPolicy = RelativePositionOrderingPolicy
        // ===================================================
        m_loginConfig.setDocumentPreservationPolicy(new XMLBinderPolicyConfig());
        m_loginConfig.getDocumentPreservationPolicy().setNodeOrderingPolicy(new RelativePositionOrderingPolicyConfig());

        roundTrippedConfig = writeAndReadSessionsXML();
        roundTrippedLoginConfig = (XMLLoginConfig) roundTrippedConfig.getLoginConfig(); 
        
        assertEquals(m_loginConfig.getDocumentPreservationPolicy().getClass(), roundTrippedLoginConfig.getDocumentPreservationPolicy().getClass());
        assertEquals(m_loginConfig.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass(), 
                roundTrippedLoginConfig.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass());
    }

    // Write the SessionConfig out to sessions.xml, then read it back in and return
    // the round-tripped SessionConfig.
    public DatabaseSessionConfig writeAndReadSessionsXML() {
        SessionConfigs configs = new SessionConfigs();
        configs.addSessionConfig(m_sessionConfig);
        configs.setVersion("0");
        XMLSessionConfigWriter.write(configs, SESSION_FILE_NAME);
        
        XMLSessionConfigLoader sessionLoader = new XMLSessionConfigLoader(SESSION_FILE_NAME);
        SessionConfigs readConfigs = sessionLoader.loadConfigsForMappingWorkbench(this.getClass().getClassLoader());
        
        // There is only one session in this sessions.xml
        return (DatabaseSessionConfig) readConfigs.getSessionConfigs().firstElement();
    }
    
    public void tearDown() {
        File tempFile = new File(SESSION_FILE_NAME);
        tempFile.delete();
    }
    
}