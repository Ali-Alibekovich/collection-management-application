package io.github.alialibekovich.collection.server.util;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionManagerTest {

    @BeforeEach
    void resetCollection() {
        CollectionManager.initializeCollection();
    }

    @Test
    void informationReportsElementCount() {
        CollectionManager.getCollection().add(organization(1, "Acme", 100.0));
        CollectionManager.getCollection().add(organization(2, "Umbrella", 200.0));

        assertTrue(CollectionManager.information().contains("Количество элементов: 2"));
    }

    @Test
    void updateElementRewritesMatchingOrganization() {
        CollectionManager.getCollection().add(organization(1, "Acme", 100.0));

        CollectionManager.updateElement(organization(999, "Renamed", 300.0), 1);

        Organization updated = CollectionManager.getCollection().get(0);
        assertEquals(1, updated.getId());
        assertEquals("Renamed", updated.getName());
        assertEquals(300.0, updated.getAnnualTurnover());
    }

    @Test
    void removeElementDeletesOnlyMatchingId() {
        CollectionManager.getCollection().add(organization(1, "Acme", 100.0));
        CollectionManager.getCollection().add(organization(2, "Umbrella", 200.0));

        CollectionManager.removeElement(1);

        assertEquals(List.of(2), CollectionManager.getCollection().stream().map(Organization::getId).toList());
    }

    @Test
    void filterStartsWithNameMatchesPrefixOnly() {
        CollectionManager.getCollection().add(organization(1, "Acme", 100.0));
        CollectionManager.getCollection().add(organization(2, "Umbrella", 200.0));

        String result = CollectionManager.filterStartsWithName("Ac");

        assertTrue(result.contains("Acme"));
        assertFalse(result.contains("Umbrella"));
    }

    @Test
    void filtersDoNotReorderTheSharedCollection() {
        // regression: filters used to sort the shared list in place via clear/addAll
        CollectionManager.getCollection().add(organization(2, "Umbrella", 200.0));
        CollectionManager.getCollection().add(organization(1, "Acme", 100.0));

        CollectionManager.filterByAnnualTurnover(100.0);
        CollectionManager.filterStartsWithName("A");

        assertEquals(List.of(2, 1), CollectionManager.getCollection().stream().map(Organization::getId).toList());
    }

    static Organization organization(int id, String name, double turnover) {
        return new Organization(
                id,
                name,
                new Coordinates(1.0, 2.0),
                LocalDateTime.of(2020, 7, 1, 20, 29),
                turnover,
                OrganizationType.COMMERCIAL,
                new Address("street", "zip", new Location(1f, 2f, "town")),
                "owner",
                "0x990000ff");
    }
}
