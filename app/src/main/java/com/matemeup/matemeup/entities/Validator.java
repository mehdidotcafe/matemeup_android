package com.matemeup.matemeup.entities;

public class Validator
{
    public static Boolean validatePassword(String password)
    {
        int minLength = 6;
        int maxLength = 255;

        return password.length() >= minLength && password.length() <= maxLength;
    }

    public static Boolean validateConfPassword(String conf, String password)
    {
        return conf.equals(password);
    }

    public static Boolean validateEmail(String email)
    {
        return true;
    }

    public static Boolean validateString(String str)
    {
        return str.length() > 0;
    }
}
