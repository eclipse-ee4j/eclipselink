package org.eclipse.persistence.testing.oxm.xmllogin;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.oxm.documentpreservation.DescriptorLevelDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.documentpreservation.NoDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.documentpreservation.XMLBinderPolicy;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.IgnoreNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.RelativePositionOrderingPolicy;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.platform.SAXPlatform;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;

public class XMLLoginDeploymentXMLTestCases extends TestCase {
    
    private Project m_project;
    private XMLLogin m_login;
    
    public XMLLoginDeploymentXMLTestCases(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmllogin.XMLLoginDeploymentXMLTestCases" };
        junit.textui.TestRunner.main(arguments);
    }

    public void setUp() {
        m_project = new Project();
        m_login = new XMLLogin();
        m_project.setLogin(m_login);
    }
    
    // Bug 242452 - test 'DatasourcePlatform' in project.xml
    public void testDataSourcePlatform() {
        Project roundTrippedProject;
        XMLLogin roundTrippedLogin;

        // DatasourcePlatform = SAXPlatform
        // ================================        
        m_login.setDatasourcePlatform(new SAXPlatform());
        
        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.getDatasourcePlatform().getClass(), roundTrippedLogin.getDatasourcePlatform().getClass());

        // DatasourcePlatform = DOMPlatform
        // ================================
        m_login.setDatasourcePlatform(new DOMPlatform());
        
        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.getDatasourcePlatform().getClass(), roundTrippedLogin.getDatasourcePlatform().getClass());
    }

    // Bug - test 'EqualNamespaceResolvers' in project.xml
    public void testEqualNamespaceResolvers() {
        Project roundTrippedProject; 
        XMLLogin roundTrippedLogin; 
        
        // EqualNamespaceResolvers = true
        // ==============================
        m_login.setEqualNamespaceResolvers(true);

        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.hasEqualNamespaceResolvers(), roundTrippedLogin.hasEqualNamespaceResolvers());
        
        // EqualNamespaceResolvers = false
        // ===============================
        m_login.setEqualNamespaceResolvers(false);

        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.hasEqualNamespaceResolvers(), roundTrippedLogin.hasEqualNamespaceResolvers());
    }

    // Bug - test 'DocumentPreservationPolicy' in project.xml
    public void testDocumentPreservationPolicy() {
        Project roundTrippedProject; 
        XMLLogin roundTrippedLogin; 
        
        // DocumentPreservationPolicy = DescriptorLevelDocumentPreservationPolicy
        // NodeOrderingPolicy = AppendNewElementsOrderingPolicy        
        // ======================================================================
        m_login.setDocumentPreservationPolicy(new DescriptorLevelDocumentPreservationPolicy());
        m_login.getDocumentPreservationPolicy().setNodeOrderingPolicy(new AppendNewElementsOrderingPolicy());

        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.getDocumentPreservationPolicy().getClass(), roundTrippedLogin.getDocumentPreservationPolicy().getClass());
        assertEquals(m_login.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass(), 
                roundTrippedLogin.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass());
        

        // NodeOrderingPolicy = IgnoreNewElementsOrderingPolicy
        // DocumentPreservationPolicy = NoDocumentPreservationPolicy
        // =========================================================
        m_login.setDocumentPreservationPolicy(new NoDocumentPreservationPolicy());
        m_login.getDocumentPreservationPolicy().setNodeOrderingPolicy(new IgnoreNewElementsOrderingPolicy());

        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.getDocumentPreservationPolicy().getClass(), roundTrippedLogin.getDocumentPreservationPolicy().getClass());
        assertEquals(m_login.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass(), 
                roundTrippedLogin.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass());
        
        // DocumentPreservationPolicy = XMLBinderPolicy
        // NodeOrderingPolicy = RelativePositionOrderingPolicy        
        // ===================================================
        m_login.setDocumentPreservationPolicy(new XMLBinderPolicy());
        m_login.getDocumentPreservationPolicy().setNodeOrderingPolicy(new RelativePositionOrderingPolicy());
        
        roundTrippedProject = writeAndReadProject();
        roundTrippedLogin = (XMLLogin) roundTrippedProject.getDatasourceLogin(); 
        
        assertEquals(m_login.getDocumentPreservationPolicy().getClass(), roundTrippedLogin.getDocumentPreservationPolicy().getClass());
        assertEquals(m_login.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass(), 
                roundTrippedLogin.getDocumentPreservationPolicy().getNodeOrderingPolicy().getClass());
    }

    // Write the Project out to deployment XML, then read it back in and return
    // the round-tripped Project.
    public Project writeAndReadProject() {
        StringWriter buffer = new StringWriter();

        XMLProjectWriter.write(m_project, buffer);
        
        StringReader in = new StringReader(buffer.getBuffer().toString());
        
        XMLProjectReader.setShouldUseSchemaValidation(true);
        
        return XMLProjectReader.read(in);
    }
    
}