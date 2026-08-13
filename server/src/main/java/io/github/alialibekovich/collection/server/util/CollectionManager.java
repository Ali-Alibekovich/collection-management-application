package io.github.alialibekovich.collection.server.util;

import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.server.db.DatabaseCommunicator;
import io.github.alialibekovich.collection.server.db.OrganizationsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CollectionManager {

    private static final Logger log = LoggerFactory.getLogger(CollectionManager.class);
    private static List<Organization> organizationCollection = Collections.synchronizedList(new ArrayList<Organization>());
    private static LocalDateTime initializationDate;


    public static void initializeCollection() {
        organizationCollection = Collections.synchronizedList(new ArrayList<>());
        initializationDate = LocalDateTime.now();
    }

    public static List<Organization> getCollection() {
        return organizationCollection;
    }

    public static String information() {
        return "Тип коллекции: ArrayList" + ".\nДата инициализации: " + initializationDate + ".\nКоличество элементов: " + organizationCollection.size() + ".";
    }

    public static String addOrganization() {
        try {
            OrganizationsRepository.loadCollection(getCollection());
        } catch (SQLException e) {
            log.error("Failed to reload the collection from the database", e);
        }
        return "Организация добавлена в коллекцию.";
    }

    public static void updateElement(Organization newOrganization, int ID) {
        organizationCollection.forEach(organization -> {
            if (organization.getId() == ID) {
                organization.setName(newOrganization.getName());
                organization.setCoordinates(newOrganization.getCoordinates());
                organization.setAnnualTurnover(newOrganization.getAnnualTurnover());
                organization.setType(newOrganization.getType());
                organization.setOfficialAddress(newOrganization.getOfficialAddress());
            }
        });
    }

    public static void removeElement(int ID) {
        organizationCollection.removeIf(organization -> (organization.getId() == ID));
    }

    public static void clearCollection() {
        organizationCollection.clear();
    }

    public static void clearCollectionOnDataBase(String owner) {
        OrganizationsRepository.clearCollectionOnDataBase(owner);
    }

    public static String removeHead(String login) {
        StringBuilder str = new StringBuilder();
        // get(0) + remove(0) is a compound action: hold the list lock so two
        // concurrent remove_head requests cannot observe and delete the same head
        synchronized (organizationCollection) {
            if (organizationCollection.size() != 0) {
                Organization head = organizationCollection.get(0);
                str.append(CollectionUtils.organizationInfo(head));
                if (OrganizationsRepository.isOwnedBy(head.getId(), login)) {
                    try {
                        DatabaseCommunicator.getOrganizations().deleteOrganizationFromDataBase(head.getId());
                    } catch (SQLException e) {
                        log.error("Failed to delete the head element from the database", e);
                    }
                    organizationCollection.remove(0);
                }
            } else {
                str.append("В этой коллекции нет элементов.");
            }
        }
        return String.valueOf(str);
    }

    public static String addIfMin(Organization organization) {
        if (organizationCollection.size() == 0) {
            addOrganization();
            return "Организация добавлена.";
        } else {
            AtomicBoolean flag = new AtomicBoolean(true);
            organizationCollection.forEach(organization1 -> {
                if (organization1.compareTo(organization) < 0) {
                    flag.set(false);
                }
            });
            if (flag.get()) {
                DatabaseCommunicator.getOrganizations().addOrganizationToTheBase(organization, -1);
                addOrganization();
                return "Организация добавлена.";
            } else {
                return "Организация не добавлена.";
            }
        }
    }

    public static String filterByAnnualTurnover(Double annualTurnover) {
        StringBuilder str = new StringBuilder();
        Organization[] snapshot = organizationCollection.toArray(new Organization[0]);
        Arrays.sort(snapshot, new OrganizationComparator());
        for (Organization organization : snapshot) {
            if (organization.getAnnualTurnover().equals(annualTurnover)) {
                str.append(CollectionUtils.organizationInfo(organization));
            }
        }
        return String.valueOf(str);
    }

    public static String filterStartsWithName(String name) {
        StringBuilder str = new StringBuilder();
        Organization[] snapshot = organizationCollection.toArray(new Organization[0]);
        Arrays.sort(snapshot, new OrganizationComparator());
        for (Organization organization : snapshot) {
            if (organization.getName().startsWith(name)) {
                str.append(CollectionUtils.organizationInfo(organization));
            }
        }
        return String.valueOf(str);
    }

    public static String printFieldDescendingAnnualTurnover() {
        StringBuilder str = new StringBuilder();
        Double[] arr = new Double[organizationCollection.size()];
        int i = 0;
        for (Organization organization : organizationCollection) {
            arr[i] = organization.getAnnualTurnover();
            i++;
        }
        Arrays.sort(arr);
        str.append("Годовые обороты компаний в порядке убывания: ");
        for (int k = 0; k < arr.length; k++) {
            if (k == arr.length - 1) {
                str.append(String.format("%.3f. \n", arr[arr.length - (k + 1)]));
            } else {
                str.append(String.format("%.3f, ", arr[arr.length - (k + 1)]));
            }
        }
        return String.valueOf(str);
    }
}