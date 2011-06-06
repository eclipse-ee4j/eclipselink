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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.advanced;

import java.io.*;
import javax.persistence.*;

/**
 * This class is used to test XML and annotation merging. This class is mapped
 * in: eclipselink-xml-merge-model/orm-annotation-merge-advanced-entity-mappings.xml
 * 
 * This class is currently marked as metadata-complete=true meaning all the
 * annotations defined here should be ignored (somewhat defeating the purpose
 * of XML and Annotation merging testing)
 * 
 * Also there are no automated tests that go along with these models, see the
 * test suite: EntityMappingsMergeAdvancedJUnitTestCase. It tests through
 * inspecting descriptor settings only and by no means does extensive
 * validation of all the metadata and defaults.
 */
@IdClass(org.eclipse.persistence.testing.models.jpa.xml.merge.advanced.PhoneNumberPK.class)
@Entity(name="AnnMergePhoneNumber")
@Table(name="CMP3_ANN_MERGE_PHONENUMBER")
public class PhoneNumber implements Serializable {
	private String number;
	private String type;
	private Employee owner;
    private Integer id;
    private String areaCode;
	
    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = null;
    }

    @Id
	@Column(name="ANN_MERGE_OWNER_ID", insertable=false, updatable=false)
	public Integer getId() { 
        return id; 
    }
    
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="ANN_MERGE_NUMB")
	public String getNumber() { 
        return number; 
    }
    
	public void setNumber(String number) { 
        this.number = number; 
    }

    @Id
    @Column(name="ANN_MERGE_TYPE")
	public String getType() { 
        return type; 
    }
    
	public void setType(String type) {
		this.type = type;
	}

	@Column(name="ANN_MERGE_AREA_CODE")
	public String getAreaCode() { 
        return areaCode; 
    }
    
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	@ManyToOne
	@JoinColumn(name="ANN_MERGE_OWNER_ID", referencedColumnName="ANN_MERGE_EMP_ID")
	public Employee getOwner() { 
        return owner; 
    }
    
	public void setOwner(Employee owner) {
		this.owner = owner;
	}

    /**
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber[");
        writer.write(getType());
        writer.write("]: (");
        writer.write(getAreaCode());
        writer.write(") ");

        int numberLength = this.getNumber().length();
        writer.write(getNumber().substring(0, Math.min(3, numberLength)));
        if (numberLength > 3) {
            writer.write("-");
            writer.write(getNumber().substring(3, Math.min(7, numberLength)));
        }

        return writer.toString();
    }
    
    /**
     * Builds the PhoneNumberPK for this class
     */
    public PhoneNumberPK buildPK(){
        PhoneNumberPK pk = new PhoneNumberPK();
        pk.setId(this.getOwner().getId());
        pk.setType(this.getType());
        return pk;
    }
}
