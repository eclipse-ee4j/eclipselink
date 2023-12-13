module eclipselink.jpa.bugtest.test {
    requires jakarta.persistence;
    requires eclipselink.jpa.bugtest;
    requires eclipselink;
    requires junit;

    exports eclipselink.jpa.bugtest;
}
