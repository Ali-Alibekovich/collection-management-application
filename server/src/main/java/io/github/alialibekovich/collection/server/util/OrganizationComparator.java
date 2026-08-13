package io.github.alialibekovich.collection.server.util;

import io.github.alialibekovich.collection.model.Organization;

import java.util.Comparator;

public class OrganizationComparator implements Comparator<Organization> {
    @Override
    public int compare(Organization organization, Organization t1) {
        return organization.getOfficialAddress().getTown().getName().compareTo(t1.getOfficialAddress().getTown().getName());
    }
}
