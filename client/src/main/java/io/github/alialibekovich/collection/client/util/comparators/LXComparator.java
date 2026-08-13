package io.github.alialibekovich.collection.client.util.comparators;

import io.github.alialibekovich.collection.model.Organization;

import java.util.Comparator;

public class LXComparator implements Comparator<Organization> {
    @Override
    public int compare(Organization o1, Organization o2) {
        return Float.compare(o1.getOfficialAddress().getTown().getX(), o2.getOfficialAddress().getTown().getX());
    }
}
