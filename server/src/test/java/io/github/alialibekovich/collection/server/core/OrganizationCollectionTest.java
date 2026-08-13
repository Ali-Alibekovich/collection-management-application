package io.github.alialibekovich.collection.server.core;

import io.github.alialibekovich.collection.model.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static io.github.alialibekovich.collection.server.core.OrganizationFixtures.organization;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganizationCollectionTest {

    private InMemoryOrganizationStore store;
    private OrganizationCollection collection;

    @BeforeEach
    void setUp() throws SQLException {
        store = new InMemoryOrganizationStore();
        collection = new OrganizationCollection(store);
    }

    @Test
    void informationReportsElementCount() throws SQLException {
        store.add(organization(1, "Acme", 100.0), -1);
        store.add(organization(2, "Umbrella", 200.0), -1);
        collection.reload();

        assertTrue(collection.information().contains("Количество элементов: 2"));
    }

    @Test
    void updateElementRewritesMatchingOrganization() throws SQLException {
        store.add(organization(1, "Acme", 100.0), -1);
        collection.reload();

        collection.updateElement(organization(999, "Renamed", 300.0), 1);

        Organization updated = collection.snapshot().get(0);
        assertEquals(1, updated.getId());
        assertEquals("Renamed", updated.getName());
        assertEquals(300.0, updated.getAnnualTurnover());
    }

    @Test
    void removeElementDeletesOnlyMatchingId() throws SQLException {
        store.add(organization(1, "Acme", 100.0), -1);
        store.add(organization(2, "Umbrella", 200.0), -1);
        collection.reload();

        collection.removeElement(1);

        assertEquals(List.of(2), collection.snapshot().stream().map(Organization::getId).toList());
    }

    @Test
    void doesExistFindsPresentAndAbsentIds() throws SQLException {
        store.add(organization(7, "Acme", 100.0), -1);
        collection.reload();

        assertTrue(collection.doesExist(7));
        assertFalse(collection.doesExist(8));
    }

    @Test
    void filterStartsWithNameMatchesPrefixOnly() throws SQLException {
        store.add(organization(1, "Acme", 100.0), -1);
        store.add(organization(2, "Umbrella", 200.0), -1);
        collection.reload();

        String result = collection.filterStartsWithName("Ac");

        assertTrue(result.contains("Acme"));
        assertFalse(result.contains("Umbrella"));
    }

    @Test
    void filtersDoNotReorderTheCollection() throws SQLException {
        store.add(organization(2, "Umbrella", 200.0), -1);
        store.add(organization(1, "Acme", 100.0), -1);
        collection.reload();

        collection.filterByAnnualTurnover(100.0);
        collection.filterStartsWithName("A");

        assertEquals(List.of(2, 1), collection.snapshot().stream().map(Organization::getId).toList());
    }

    @Test
    void removeHeadDeletesOnlyWhenCallerOwnsIt() throws SQLException {
        store.add(organization(1, "Acme", 100.0, "alice"), -1);
        collection.reload();

        String described = collection.removeHead("mallory");
        assertTrue(described.contains("Acme"));
        assertEquals(1, collection.snapshot().size());

        collection.removeHead("alice");
        assertTrue(collection.snapshot().isEmpty());
        assertTrue(store.findAll().isEmpty());
    }

    @Test
    void addIfMinAddsOnlyWhenStrictlyMinimal() throws SQLException {
        store.add(organization(1, "Acme", 100.0), -1);
        collection.reload();

        assertEquals("Организация не добавлена.", collection.addIfMin(organization(2, "Big", 500.0)));
        assertEquals("Организация добавлена.", collection.addIfMin(organization(3, "Small", 50.0)));
        assertEquals(2, collection.snapshot().size());
    }
}
