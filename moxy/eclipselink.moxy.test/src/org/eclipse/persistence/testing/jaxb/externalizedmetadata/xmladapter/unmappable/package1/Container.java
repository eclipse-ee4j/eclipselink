package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package1;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2.Unmappable;


public class Container {

    private Unmappable containerProperty;

    public Unmappable getContainerProperty() {
        return containerProperty;
    }

    public void setContainerProperty(Unmappable containerProperty) {
        this.containerProperty = containerProperty;
    }
    
    public boolean equals(Object obj) {
        return (obj instanceof Container) && ((Container)obj).getContainerProperty().equals(this.containerProperty); 
    }
}
