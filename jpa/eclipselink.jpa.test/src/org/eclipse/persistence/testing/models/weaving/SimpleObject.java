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
package org.eclipse.persistence.testing.models.weaving;

// J2SE imports
import java.io.Serializable;

// Persistence imports
import javax.persistence.*;

import static javax.persistence.GenerationType.*;

@Entity
@Table(name="SIMPLE")
public class SimpleObject implements Serializable {
	
	// ensure we have at least one of each type of primitive
	private int version;
	private boolean booleanAttribute;
	private char charAttribute;
	private byte byteAttribute;
	private short shortAttribute;
	private long longAttribute;
	private float floatAttribute;
	private double doubleAttribute;
	// have some objects, too
	private Integer id; // PK
	private String name;
	private SimpleAggregate simpleAggregate;
    
	public SimpleObject () {
	}

	@Id
    @GeneratedValue(strategy=TABLE, generator="SIMPLE_TABLE_GENERATOR")
	@TableGenerator(
        name="SIMPLE_TABLE_GENERATOR",
		table="SIMPLE_SEQ", 
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SIMPLE_SEQ"
    )
    @Column(name="ID")
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

	@Column(name="NAME", length=80)
	public String getName() { 
        return name; 
    }
    
	public void setName(String name) { 
        this.name = name; 
    }

	public boolean isBooleanAttribute() {
		return booleanAttribute;
	}

	public void setBooleanAttribute(boolean booleanAttribute) {
		this.booleanAttribute = booleanAttribute;
	}

	public byte getByteAttribute() {
		return byteAttribute;
	}

	public void setByteAttribute(byte byteAttribute) {
		this.byteAttribute = byteAttribute;
	}

	public char getCharAttribute() {
		return charAttribute;
	}

	public void setCharAttribute(char charAttribute) {
		this.charAttribute = charAttribute;
	}

	public double getDoubleAttribute() {
		return doubleAttribute;
	}

	public void setDoubleAttribute(double doubleAttribute) {
		this.doubleAttribute = doubleAttribute;
	}

	public float getFloatAttribute() {
		return floatAttribute;
	}

	public void setFloatAttribute(float floatAttribute) {
		this.floatAttribute = floatAttribute;
	}

	public long getLongAttribute() {
		return longAttribute;
	}

	public void setLongAttribute(long longAttribute) {
		this.longAttribute = longAttribute;
	}

	public short getShortAttribute() {
		return shortAttribute;
	}

	public void setShortAttribute(short shortAttribute) {
		this.shortAttribute = shortAttribute;
	}

	@Embedded()
	public SimpleAggregate getSimpleAggregate() {
		return simpleAggregate;
	}
	public void setSimpleAggregate(SimpleAggregate simpleAggregate) {
		this.simpleAggregate = simpleAggregate;
	}

}
