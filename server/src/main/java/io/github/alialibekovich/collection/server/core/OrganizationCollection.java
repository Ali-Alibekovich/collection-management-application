package io.github.alialibekovich.collection.server.core;

import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.server.util.CollectionUtils;
import io.github.alialibekovich.collection.server.util.OrganizationComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * In-memory view of the organization collection, loaded from the durable
 * store. All mutating and compound operations are safe to call from
 * concurrent request handlers.
 */
public class OrganizationCollection {

    private static final Logger log = LoggerFactory.getLogger(OrganizationCollection.class);

    private final OrganizationStore store;
    private final List<Organization> organizations = Collections.synchronizedList(new ArrayList<>());
    private final LocalDateTime initializationDate = LocalDateTime.now();

    public OrganizationCollection(OrganizationStore store) {
        this.store = store;
    }

    public void reload() throws SQLException {
        List<Organization> fresh = store.findAll();
        synchronized (organizations) {
            organizations.clear();
            organizations.addAll(fresh);
        }
    }

    public String information() {
        return "Тип коллекции: ArrayList" + ".\nДата инициализации: " + initializationDate
                + ".\nКоличество элементов: " + organizations.size() + ".";
    }

    public List<Organization> snapshot() {
        synchronized (organizations) {
            return new ArrayList<>(organizations);
        }
    }

    public boolean doesExist(int id) {
        synchronized (organizations) {
            for (Organization organization : organizations) {
                if (organization.getId() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateElement(Organization newOrganization, int id) {
        organizations.forEach(organization -> {
            if (organization.getId() == id) {
                organization.setName(newOrganization.getName());
                organization.setCoordinates(newOrganization.getCoordinates());
                organization.setAnnualTurnover(newOrganization.getAnnualTurnover());
                organization.setType(newOrganization.getType());
                organization.setOfficialAddress(newOrganization.getOfficialAddress());
            }
        });
    }

    public void removeElement(int id) {
        organizations.removeIf(organization -> organization.getId() == id);
    }

    /**
     * Describes the head element and, if it belongs to {@code login}, removes
     * it from the store and the collection. The whole compound runs under the
     * list lock so two concurrent calls cannot remove the same head.
     */
    public String removeHead(String login) {
        StringBuilder str = new StringBuilder();
        synchronized (organizations) {
            if (organizations.isEmpty()) {
                return "В этой коллекции нет элементов.";
            }
            Organization head = organizations.get(0);
            str.append(CollectionUtils.organizationInfo(head));
            if (store.isOwnedBy(head.getId(), login)) {
                try {
                    store.delete(head.getId());
                } catch (SQLException e) {
                    log.error("Failed to delete the head element from the database", e);
                }
                organizations.remove(0);
            }
        }
        return str.toString();
    }

    public String addIfMin(Organization candidate) throws SQLException {
        boolean isMin;
        synchronized (organizations) {
            isMin = organizations.stream().noneMatch(existing -> existing.compareTo(candidate) < 0);
        }
        if (!isMin) {
            return "Организация не добавлена.";
        }
        store.add(candidate, -1);
        reload();
        return "Организация добавлена.";
    }

    public String filterByAnnualTurnover(Double annualTurnover) {
        StringBuilder str = new StringBuilder();
        for (Organization organization : sortedSnapshot()) {
            if (organization.getAnnualTurnover().equals(annualTurnover)) {
                str.append(CollectionUtils.organizationInfo(organization));
            }
        }
        return str.toString();
    }

    public String filterStartsWithName(String name) {
        StringBuilder str = new StringBuilder();
        for (Organization organization : sortedSnapshot()) {
            if (organization.getName().startsWith(name)) {
                str.append(CollectionUtils.organizationInfo(organization));
            }
        }
        return str.toString();
    }

    public String printFieldDescendingAnnualTurnover() {
        List<Organization> snapshot = snapshot();
        Double[] turnovers = snapshot.stream().map(Organization::getAnnualTurnover).toArray(Double[]::new);
        Arrays.sort(turnovers);
        StringBuilder str = new StringBuilder("Годовые обороты компаний в порядке убывания: ");
        for (int k = 0; k < turnovers.length; k++) {
            Double value = turnovers[turnovers.length - (k + 1)];
            str.append(k == turnovers.length - 1 ? String.format("%.3f. \n", value) : String.format("%.3f, ", value));
        }
        return str.toString();
    }

    /** Direct view of the backing synchronized list — for same-package tests only. */
    List<Organization> rawList() {
        return organizations;
    }

    private Organization[] sortedSnapshot() {
        Organization[] snapshot = organizations.toArray(new Organization[0]);
        Arrays.sort(snapshot, new OrganizationComparator());
        return snapshot;
    }
}
