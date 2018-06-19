package org.os890.bv.addon.label.test.uc.uc001;

public class TestPerson {
    @MySize(min = 1, message = "{nameLength}", propertyLabel = "{firstName}")
    private String firstName;

    @MySize(min = 2, message = "{nameLength}", propertyLabel = "{lastName}")
    private String lastName;

    public TestPerson(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}