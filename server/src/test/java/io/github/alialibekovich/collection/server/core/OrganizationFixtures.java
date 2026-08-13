package io.github.alialibekovich.collection.server.core;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;

import java.time.LocalDateTime;

final class OrganizationFixtures {

    private OrganizationFixtures() {
    }

    static Organization organization(int id, String name, double turnover) {
        return organization(id, name, turnover, "owner");
    }

    static Organization organization(int id, String name, double turnover, String owner) {
        return new Organization(
                id,
                name,
                new Coordinates(1.0, 2.0),
                LocalDateTime.of(2020, 7, 1, 20, 29),
                turnover,
                OrganizationType.COMMERCIAL,
                new Address("street", "zip", new Location(1f, 2f, "town")),
                owner,
                "0x990000ff");
    }
}
