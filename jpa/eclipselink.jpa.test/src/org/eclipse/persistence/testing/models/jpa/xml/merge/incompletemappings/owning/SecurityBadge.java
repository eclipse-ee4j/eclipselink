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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.incompletemappings.owning;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;

/**
 *
 */
@Entity(name="XMLIncompleteMergeSecurityBadge")
@Table(name="CMP3_XML_MERGE_SECURITYBADGE")
public class SecurityBadge {
	private Integer id;
	private int badgeNumber;
	private int version;
    private Employee owner;

	public SecurityBadge () {}

	public SecurityBadge (int badgeNumber) {
		setBadgeNumber(badgeNumber);
	}

	@Id
    @GeneratedValue(strategy=TABLE, generator="XML_MERGE_SECURITYBADGE_TABLE_GENERATOR")
	@TableGenerator(
        name="XML_MERGE_SECURITYBADGE_TABLE_GENERATOR", 
        table="CMP3_XML_MERGE_BADGE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="XML_MERGE_BADGE_SEQ"
    )
    @Column(name="BADGE_ID")
	public Integer getId() { 
        return id; 
    }
    
	public void setId(Integer id) { 
        this.id = id; 
    }

	@Version
	@Column(name="VERSION")
	public int getVersion() { 
        return version; 
    }
    
	protected void setVersion(int version) {
		this.version = version;
	}
    
    @OneToOne(mappedBy="securityBadge")
    public Employee getOwner() {
    	return owner;
    }
    
    public void setOwner(Employee newOwner) {
    	owner = newOwner;
    }
    
    public int getBadgeNumber() {
    	return badgeNumber;
    }

    public void setBadgeNumber(int badgeNumber) {
    	this.badgeNumber = badgeNumber;
    }
}
