package io.github.alialibekovich.collection.client.util.comparators;

import io.github.alialibekovich.collection.model.Organization;

import java.util.Comparator;

public class XComparator implements Comparator<Organization> {
    @Override
    public int compare(Organization o1, Organization o2) {
        return Double.compare(o1.getCoordinates().getX(), o2.getCoordinates().getX());
    }
}
