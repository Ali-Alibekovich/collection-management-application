package io.github.alialibekovich.collection.client.util.comparators;

import io.github.alialibekovich.collection.model.Organization;

import java.util.Comparator;

public class TypeComparator implements Comparator<Organization> {
    @Override
    public int compare(Organization o1, Organization o2) {
        return o1.getType().name().compareTo(o2.getType().name());
    }
}
