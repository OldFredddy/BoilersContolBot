package org.csbk;

import java.io.InputStream;
import java.util.Properties;

public class Tokens {
    private Properties properties = new Properties();

    public Tokens() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("keys.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find keys.properties");
                return;
            }
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getKey1() {
        return properties.getProperty("key1");
    }

    public String getKey2() {
        return properties.getProperty("key2");
    }
}
