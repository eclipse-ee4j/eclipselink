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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import java.util.*;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

public class BarPopulator {
    protected PopulationManager populationManager;

    public BarPopulator() {
        populationManager = PopulationManager.getDefaultManager();
    }

    public Award awardExample1() {
        Award award = new Award();
        award.setDescription("1997 - Most tips collected.");
        return award;
    }
    
    public Award awardExample2() {
        Award award = new Award();
        award.setDescription("1998 - Most tips collected.");
        return award;
    }
    
    public Award awardExample3() {
        Award award = new Award();
        award.setDescription("1999 - Most tips collected.");
        return award;
    }
    
    public Award awardExample4() {
        Award award = new Award();
        award.setDescription("2000 - Most tips collected.");
        return award;
    }
    
    public Award awardExample5() {
        Award award = new Award();
        award.setDescription("2001 - Most tips collected.");
        return award;
    }
    
    public Award awardExample6() {
        Award award = new Award();
        award.setDescription("2002 - Most tips collected.");
        return award;
    }
    
    public Award awardExample7() {
        Award award = new Award();
        award.setDescription("2003 - Most tips collected.");
        return award;
    }
    
    public Award awardExample8() {
        Award award = new Award();
        award.setDescription("2004 - Most tips collected.");
        return award;
    }
    
    public Award awardExample9() {
        Award award = new Award();
        award.setDescription("2005 - Most tips collected.");
        return award;
    }
    
    public Award awardExample10() {
        Award award = new Award();
        award.setDescription("2000 - Golden hand award.");
        return award;
    }
    
    public Award awardExample11() {
        Award award = new Award();
        award.setDescription("2001 - Golden hand award.");
        return award;
    }
    
    public Award awardExample12() {
        Award award = new Award();
        award.setDescription("2002 - Golden hand award.");
        return award;
    }
    
    public Award awardExample13() {
        Award award = new Award();
        award.setDescription("2003 - Golden hand award.");
        return award;
    }
    
    public Award awardExample14() {
        Award award = new Award();
        award.setDescription("2004 - Golden hand award.");
        return award;
    }
    
    public Award awardExample15() {
        Award award = new Award();
        award.setDescription("2005 - Golden hand award.");
        return award;
    }
    
    public Bar barExample1() {
        if (containsObject(Bar.class, "0001")) {
            return (Bar) getObject(Bar.class, "0001");
        }

        Bar bar = new Bar();

        try {
            bar.setName("The drinking wagon");
            bar.setLicense(licenseExample1());
            bar.addBartender(bartenderExample1());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        
        registerObject(Bar.class, bar, "0001");
        return bar;
    }

    public Bar barExample2() {
        if (containsObject(Bar.class, "0002")) {
            return (Bar) getObject(Bar.class, "0002");
        }

        Bar bar = new Bar();

        try {
            bar.setName("Cheers!");
            bar.setLicense(licenseExample2());
            bar.addBartender(bartenderExample2());
            bar.addBartender(bartenderExample3());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        
        registerObject(Bar.class, bar, "0002");
        return bar;
    }

    public Bar barExample3() {
        if (containsObject(Bar.class, "0003")) {
            return (Bar) getObject(Bar.class, "0003");
        }

        Bar bar = new Bar();

        try {
            bar.setName("House of sports");
            bar.setLicense(licenseExample3());
            bar.addBartender(bartenderExample4());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        
        registerObject(Bar.class, bar, "0003");
        return bar;
    }

    public Bar barExample4() {
        if (containsObject(Bar.class, "0004")) {
            return (Bar) getObject(Bar.class, "0004");
        }

        Bar bar = new Bar();

        try {
            bar.setName("Crazy crackas");
            bar.setLicense(licenseExample4());
            bar.addBartender(bartenderExample5());
            bar.addBartender(bartenderExample6());
            bar.addBartender(bartenderExample7());

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
        
        registerObject(Bar.class, bar, "0004");
        return bar;
    }
    
    public Bartender bartenderExample1() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("Jason");
        bartender.setLastName("Campeau");
        bartender.setQualification(qualificationExample1());
        return bartender;
    }

    public Bartender bartenderExample2() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("Guy");
        bartender.setLastName("Pelletier");
        bartender.setQualification(qualificationExample2());
        return bartender;
    }

    public Bartender bartenderExample3() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("Kirsten");
        bartender.setLastName("Pelletier");
        bartender.setQualification(qualificationExample3());
        return bartender;
    }

    public Bartender bartenderExample4() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("Sam");
        bartender.setLastName("Legg");
        bartender.setQualification(qualificationExample4());
        return bartender;
    }
    
    public Bartender bartenderExample5() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("Dale");
        bartender.setLastName("LaRocque");
        bartender.setQualification(qualificationExample5());
        return bartender;
    }
    
    public Bartender bartenderExample6() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("Cuong");
        bartender.setLastName("Dang");
        bartender.setQualification(qualificationExample6());
        return bartender;
    }
    
    public Bartender bartenderExample7() {
        Bartender bartender = new Bartender();
        bartender.setFirstName("David");
        bartender.setLastName("Whittaker");
        bartender.setQualification(qualificationExample7());
        return bartender;
    }
    
    public void buildExamples() {
        PopulationManager.getDefaultManager().getRegisteredObjects().remove(Bar.class);

        barExample1();
        barExample2();
        barExample3();
        barExample4();
    }

    protected boolean containsObject(Class domainClass, String identifier) {
        return populationManager.containsObject(domainClass, identifier);
    }

    protected Vector getAllObjects() {
        return populationManager.getAllObjects();
    }

    public Vector getAllObjectsForClass(Class domainClass) {
        return populationManager.getAllObjectsForClass(domainClass);
    }

    protected Object getObject(Class domainClass, String identifier) {
        return populationManager.getObject(domainClass, identifier);
    }

    public License licenseExample1() {
        License license = new License();
        license.setLicenseClass("A");
        return license;
    }
    
    public License licenseExample2() {
        License license = new License();
        license.setLicenseClass("B");
        return license;
    }
    
    public License licenseExample3() {
        License license = new License();
        license.setLicenseClass("C");
        return license;
    }
    
    public License licenseExample4() {
        License license = new License();
        license.setLicenseClass("D");
        return license;
    }
    
    public Qualification qualificationExample1() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(1);
        qualification.addAward(awardExample1());
        qualification.addAward(awardExample8());
        return qualification;
    }
    
    public Qualification qualificationExample2() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(2);
        qualification.addAward(awardExample2());
        qualification.addAward(awardExample9());
        return qualification;
    }
    
    public Qualification qualificationExample3() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(3);
        qualification.addAward(awardExample3());
        qualification.addAward(awardExample10());
        return qualification;
    }
    
    public Qualification qualificationExample4() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(4);
        qualification.addAward(awardExample4());
        qualification.addAward(awardExample11());
        return qualification;
    }
    
    public Qualification qualificationExample5() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(5);
        qualification.addAward(awardExample5());
        qualification.addAward(awardExample12());
        
        return qualification;
    }
    
    public Qualification qualificationExample6() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(6);
        qualification.addAward(awardExample6());
        qualification.addAward(awardExample13());
        return qualification;
    }
    
    public Qualification qualificationExample7() {
        Qualification qualification = new Qualification();
        qualification.setYearsOfExperience(7);
        qualification.addAward(awardExample7());
        qualification.addAward(awardExample14());
        qualification.addAward(awardExample15());
        return qualification;
    }
    
    protected void registerObject(Class domainClass, Object domainObject, String identifier) {
        populationManager.registerObject(domainClass, domainObject, identifier);
    }

    protected void registerObject(Object domainObject, String identifier) {
        populationManager.registerObject(domainObject, identifier);
    }
}
