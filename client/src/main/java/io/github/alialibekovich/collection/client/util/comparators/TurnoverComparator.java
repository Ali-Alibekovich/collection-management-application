package io.github.alialibekovich.collection.client.util.comparators;

import io.github.alialibekovich.collection.model.Organization;

import java.util.Comparator;

public class TurnoverComparator implements Comparator<Organization> {
    @Override
    public int compare(Organization o1, Organization o2) {
        String q = String.valueOf(o1.getAnnualTurnover());
        String w = String.valueOf(o2.getAnnualTurnover());
        return q.compareTo(w);
    }
}
