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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection;

public class MailingAddress  {
  
  private String street;
  private String city;
  private String province;
  private String postalCode;

  public MailingAddress() {
    super();
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String newStreet) {
    street = newStreet;
  }
  
  public String getCity() {
    return city;
  }

  public void setCity(String newCity) {
    city = newCity;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String newProvince) {
    province = newProvince;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String newPostalCode) {
    postalCode = newPostalCode;
  }

  public String toString()
  {
    return " MailingAddress: " + getStreet() + " " + getCity() + " " + getProvince() + " " + getPostalCode();
  }
  
  public boolean equals(Object object)
  {
    if(!(object instanceof MailingAddress))
      return false;

    MailingAddress addressObject = (MailingAddress)object;
    if((this.getCity()==null && addressObject.getCity()==null) || (this.getCity().equals(addressObject.getCity())))
      if((this.getStreet()==null && addressObject.getStreet()==null) || (this.getStreet().equals(addressObject.getStreet())))
        if((this.getProvince()==null && addressObject.getProvince()==null) || (this.getProvince().equals(addressObject.getProvince())))
          if((this.getPostalCode()==null && addressObject.getPostalCode()==null) || (this.getPostalCode().equals(addressObject.getPostalCode())))
            return true;

    return false;
  }
}
