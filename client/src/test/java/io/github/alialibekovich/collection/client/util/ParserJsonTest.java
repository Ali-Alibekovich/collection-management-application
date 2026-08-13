package io.github.alialibekovich.collection.client.util;

import io.github.alialibekovich.collection.model.Organization;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParserJsonTest {

    private static final String SAMPLE = """
            [{
              "id": 17,
              "name": "Acme",
              "coordinates": {"x": 2.0, "y": 3.0},
              "creationDate": "2020-07-01T20:29",
              "annualTurnover": 1000.0,
              "type": "TRUST",
              "officialAddress": {
                "street": "Main", "zipCode": "191187",
                "town": {"x": 1.0, "y": 2.0, "name": "SPb"}
              },
              "owner": "user1",
              "color": "0x990000ff"
            }]""";

    @Test
    void serverJsonIsParsedIntoTheCollection() {
        ParserJson.loadCollection(SAMPLE);

        assertEquals(1, CollectionManager.getCollection().size());
        Organization organization = CollectionManager.getCollection().get(0);
        assertEquals(17, organization.getId());
        assertEquals("Acme", organization.getName());
        assertEquals("SPb", organization.getOfficialAddress().getTown().getName());
    }

    @Test
    void loadReplacesThePreviousCollection() {
        ParserJson.loadCollection(SAMPLE);
        ParserJson.loadCollection("[]");

        assertTrue(CollectionManager.getCollection().isEmpty());
    }

    @Test
    void malformedJsonLeavesAnEmptyCollectionInsteadOfCrashing() {
        ParserJson.loadCollection("this is not json");

        assertTrue(CollectionManager.getCollection().isEmpty());
    }
}
