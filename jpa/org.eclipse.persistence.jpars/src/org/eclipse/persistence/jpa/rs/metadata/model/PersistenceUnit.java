package org.eclipse.persistence.jpa.rs.metadata.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PersistenceUnit {

    protected String persistenceUnitName  = null;
    protected List<Link> types = new ArrayList<Link>();
    
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }
    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }
    public List<Link> getTypes() {
        return types;
    }
    public void setTypes(List<Link> types) {
        this.types = types;
    }
}
