package com.example;

public class OwnerValidator {
    public static final int MAX_NAME_LENGTH = 50;

    private OwnerValidator() {}

    public static boolean isValidName(String name)
    {
        if (name == null || name.isBlank()) {
            return false;
        }

        String stripped = name.strip();
        return stripped.length() <= MAX_NAME_LENGTH;
    }

    public final static int MIN_PHONE_LENGTH = 8;
    public final static int MAX_PHONE_LENGTH = 15;


    public static boolean isValidPhone(String phone)
    {
        if(phone == null || phone.isBlank())
        {
            return false;
        }

        if(phone.length() < MIN_PHONE_LENGTH || phone.length() > MAX_PHONE_LENGTH)
        {
            return false;
        }

        return phone.chars().allMatch(ch -> ch >= '0' && ch <= '9');
    }
}
