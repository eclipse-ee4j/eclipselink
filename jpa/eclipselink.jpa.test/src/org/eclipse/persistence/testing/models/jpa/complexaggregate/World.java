/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.complexaggregate;

import java.util.Vector;
import java.util.Collection;
import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_WORLD")
public class World implements Serializable {
    private int id;
    private Collection<CitySlicker> citySlickers;
    private Collection<CountryDweller> countryDwellers;
    
	public World () {
        citySlickers = new Vector<CitySlicker>();
        countryDwellers = new Vector<CountryDweller>();
    }

    public void addCitySlicker(CitySlicker citySlicker) { 
        citySlickers.add(citySlicker);
        citySlicker.setWorld(this);
    }
    
    public void addCountryDweller(CountryDweller countryDweller) { 
        countryDwellers.add(countryDweller); 
        countryDweller.setWorld(this);
    }
    
    @OneToMany(fetch=EAGER, mappedBy="world")
    @OrderBy // will default to the embedded id fields.
	public Collection<CitySlicker> getCitySlickers() { 
        return citySlickers; 
    }
    
    @OneToMany(fetch=EAGER, mappedBy="world")
    @OrderBy("age, getGender DESC")
	public Collection<CountryDweller> getCountryDwellers() { 
        return countryDwellers; 
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="WORLD_TABLE_GENERATOR")
	@TableGenerator(
        name="WORLD_TABLE_GENERATOR", 
        table="CMP3_WORLD_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="WORLD_SEQ"
    )
	public int getId() { 
        return id; 
    }

	public void setCitySlickers(Collection<CitySlicker> citySlickers) { 
        this.citySlickers = citySlickers; 
    }

	public void setCountryDwellers(Collection<CountryDweller> countryDwellers) { 
        this.countryDwellers = countryDwellers; 
    }
    
	public void setId(int id) { 
        this.id = id; 
    }
}
