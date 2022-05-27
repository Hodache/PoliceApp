package com.dataclasses;

public class Driver {
    private static String license = null;
    private static String firstName = null;
    private static String lastName = null;

    public static String getLicense() {
        return license;
    }

    public static void setLicense(String license) {
        Driver.license = license;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Driver.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Driver.lastName = lastName;
    }
}
