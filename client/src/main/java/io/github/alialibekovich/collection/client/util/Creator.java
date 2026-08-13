package io.github.alialibekovich.collection.client.util;

import io.github.alialibekovich.collection.client.core.Receiver;
import io.github.alialibekovich.collection.model.*;

public class Creator {

    public static Organization create(String name, Double x, Double y, Double annualTurnover, OrganizationType type, String street, String zipcode, Float lx, Float ly, String town) {
        return new Organization(name, new Coordinates(x, y), annualTurnover, type, new Address(street, zipcode, new Location(lx, ly, town)), Receiver.myLogin, Receiver.myColor);
    }
}