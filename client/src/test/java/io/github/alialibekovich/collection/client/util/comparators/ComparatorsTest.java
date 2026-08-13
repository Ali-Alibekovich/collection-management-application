package io.github.alialibekovich.collection.client.util.comparators;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The sort comparators drive the table sorting in the UI. Numeric fields must
 * be compared numerically — the old string-based comparison put 10 before 9.
 */
class ComparatorsTest {

    @Test
    void numericComparatorsCompareNumerically() {
        Organization nine = organization(9, 9.0, OrganizationType.COMMERCIAL, "A", "A");
        Organization ten = organization(10, 10.0, OrganizationType.COMMERCIAL, "A", "A");

        assertTrue(new IDComparator().compare(nine, ten) < 0);
        assertTrue(new TurnoverComparator().compare(nine, ten) < 0);
        assertTrue(new XComparator().compare(nine, ten) < 0);
        assertTrue(new YComparator().compare(nine, ten) < 0);
        assertTrue(new LXComparator().compare(nine, ten) < 0);
        assertTrue(new LYComparator().compare(nine, ten) < 0);
    }

    @Test
    void townAndStreetComparatorsCompareBothSides() {
        // regression: both used to compare an organization with itself (always 0)
        Organization inA = organization(1, 1.0, OrganizationType.COMMERCIAL, "Amsterdam", "Alpha");
        Organization inB = organization(2, 1.0, OrganizationType.COMMERCIAL, "Berlin", "Beta");

        assertTrue(new TownComparator().compare(inA, inB) < 0);
        assertTrue(new StreetComparator().compare(inA, inB) < 0);
        assertEquals(0, new TownComparator().compare(inA, inA));
    }

    @Test
    void typeComparatorComparesByTypeNotTurnover() {
        // regression: it used to fall through to compareTo (annual turnover)
        Organization commercialRich = organization(1, 999.0, OrganizationType.COMMERCIAL, "A", "A");
        Organization trustPoor = organization(2, 1.0, OrganizationType.TRUST, "A", "A");

        assertTrue(new TypeComparator().compare(commercialRich, trustPoor) < 0);
    }

    @Test
    void dateComparatorOrdersChronologically() {
        Organization older = organization(1, 1.0, OrganizationType.COMMERCIAL, "A", "A");
        Organization newer = new Organization(
                2, "org-2", new Coordinates(1.0, 1.0), LocalDateTime.of(2024, 1, 1, 0, 0),
                1.0, OrganizationType.COMMERCIAL,
                new Address("A", "zip", new Location(1f, 1f, "A")), "owner", "0x990000ff");

        assertTrue(new DateComparator().compare(older, newer) < 0);
    }

    private static Organization organization(int id, double turnover, OrganizationType type, String town, String street) {
        return new Organization(
                id,
                "org-" + id,
                new Coordinates((double) id, id),
                LocalDateTime.of(2020, 7, 1, 20, 29),
                turnover,
                type,
                new Address(street, "zip", new Location(id, id, town)),
                "owner",
                "0x990000ff");
    }
}
