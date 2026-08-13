package io.github.alialibekovich.collection.client.i18n;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Every locale must translate exactly the same set of keys — a key missing in
 * one bundle shows up in the UI as a MissingResourceException at runtime.
 */
class BundleParityTest {

    private static final String[] LOCALES = {"en", "ru", "pl", "no"};

    @Test
    void allBundlesShareTheSameKeys() throws IOException {
        Set<String> reference = keysOf("en");
        assertFalse(reference.isEmpty());
        for (String locale : LOCALES) {
            assertEquals(reference, keysOf(locale), "bundle mismatch: " + locale);
        }
    }

    private static Set<String> keysOf(String locale) throws IOException {
        Properties properties = new Properties();
        String resource = "/i18n/resources_" + locale + ".properties";
        try (InputStream in = BundleParityTest.class.getResourceAsStream(resource)) {
            assertNotNull(in, "missing bundle " + resource);
            properties.load(in);
        }
        return properties.stringPropertyNames();
    }
}
