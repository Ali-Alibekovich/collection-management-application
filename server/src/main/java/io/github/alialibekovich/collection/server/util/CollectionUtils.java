package io.github.alialibekovich.collection.server.util;

import io.github.alialibekovich.collection.model.Organization;

import java.time.format.DateTimeFormatter;

public class CollectionUtils {
    public static String organizationInfo(Organization organization) {
        return ("ID: " + organization.getId() + ".\n" +
                "Название организации: " + organization.getName() + "\n" +
                "Координаты: X=" + organization.getCoordinates().getX() + ", Y=" + organization.getCoordinates().getY() + "." + "\n" +
                "Время создания: " + organization.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "." + "\n" +
                "Годовой оборот организации: " + organization.getAnnualTurnover() + "." + "\n" +
                "Тип организации: " + organization.getType() + "." + "\n" +
                "Адрес организации: улица " + organization.getOfficialAddress().getStreet() + ", почтовый индекс " + organization.getOfficialAddress().getZipCode() + ", город " + organization.getOfficialAddress().getTown().getName() + " (X=" + organization.getOfficialAddress().getTown().getX() + ", Y=" + organization.getOfficialAddress().getTown().getY() + ")." + "\n");
    }
}
