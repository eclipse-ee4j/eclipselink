package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.internal.oxm.QNameInheritancePolicy;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class MotorcycleProject extends Project {
    NamespaceResolver nsr;

    public MotorcycleProject() {
        nsr = new NamespaceResolver();
        nsr.put("ns2", "http://www.example.com");
        nsr.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        addDescriptor(getMotorcycleDescriptor());
        addDescriptor(getSportBikeDescriptor());
    }

    private XMLDescriptor getMotorcycleDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Motorcycle.class);
        descriptor.setDefaultRootElement("ns2:Motorcycle");

        XMLField classIndicatorField = new XMLField("@xsi:type");
        descriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);        
        descriptor.getInheritancePolicy().addClassIndicator(SportBike.class, "ns2:SportBike");        
        descriptor.getInheritancePolicy().addClassIndicator(Motorcycle.class, "ns2:Motorcycle");      
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        
        descriptor.setNamespaceResolver(nsr);
        
        XMLDirectMapping licenseMapping = new XMLDirectMapping();
        licenseMapping.setAttributeName("license");
        licenseMapping.setXPath("license-number/text()");
        descriptor.addMapping(licenseMapping);   
        
        return descriptor;
    }
    
    private XMLDescriptor getSportBikeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SportBike.class);
        descriptor.getInheritancePolicy().setParentClass(Motorcycle.class);
        descriptor.setDefaultRootElement("ns2:Motorcycle");
        descriptor.setNamespaceResolver(nsr);

        XMLDirectMapping displacementMapping = new XMLDirectMapping();
        displacementMapping.setAttributeName("displacement");
        displacementMapping.setXPath("engine-size/text()");
        descriptor.addMapping(displacementMapping);   
        return descriptor;
    }
}
