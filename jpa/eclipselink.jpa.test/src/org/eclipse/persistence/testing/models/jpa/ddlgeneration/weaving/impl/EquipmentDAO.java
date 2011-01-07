/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/12/2009-1.1 Daniel Lo
 *       - 247041: Null element inserted in the ArrayList 
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.impl;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Equipment;
import org.eclipse.persistence.testing.models.jpa.ddlgeneration.weaving.Port;

import org.eclipse.persistence.annotations.PrivateOwned;

@Entity
@Table(name="Equipment")
@NamedQueries({
    @NamedQuery(name="Equipment.findEquipmentById", query="select o from EquipmentDAO o where o.id = :id")
})
public class EquipmentDAO implements Equipment, java.io.Serializable {
    private static final long serialVersionUID = 1L;
 
    @Id
    @Column(name = "JDOID")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="PKGen")
    @TableGenerator(
            name="PKGen", 
            table="JDO_SEQUENCE", 
            pkColumnName="ID", 
            pkColumnValue="jdoid", 
            valueColumnName="SEQUENCE_VALUE",
            initialValue=0,
            allocationSize=50)
    protected long entityId;

    private java.lang.String id;
    
    @OneToMany(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE}, 
            fetch=FetchType.LAZY,
            targetEntity=PortDAO.class,
            mappedBy="equipment")
    @PrivateOwned
    @OrderBy("portOrder ASC")
    private java.util.List<Port> ports = new ArrayList<Port>();

    @Column(name="JDOVERSION")
    @Version
    int entityVersion;

    public EquipmentDAO() {}

    public long getEntityId() {
        return entityId;
    }

    public String getId() {
        return id;
    }
    
    public void setId( String id ) {
        this.id = id;
    }
    
    public java.util.List<Port> getPorts() {
            return ports;
    }

    public void setPorts(ArrayList<Port> ports ) {
        this.ports = ports;
    }
    
    public void addPort(Port p) {
		getPorts().add(p);
		p.setEquipment(this);
	}

	public Port removePort(int i) {
		Port port = getPorts().remove(i);
        
		if (port != null) {
            port.setEquipment(null);
        }
        
        return port;
	}
}
