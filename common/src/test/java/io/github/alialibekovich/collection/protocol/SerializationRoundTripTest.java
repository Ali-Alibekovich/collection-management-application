package io.github.alialibekovich.collection.protocol;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The protocol relies on plain Java serialization over UDP, so every envelope
 * and the whole domain model must survive a serialize/deserialize round trip.
 */
class SerializationRoundTripTest {

    private static class TestCommand implements Command {
        private static final long serialVersionUID = 32L;
    }

    @Test
    void argumentEnvelopeSurvivesRoundTrip() throws Exception {
        SerializedArgumentCommand envelope =
                new SerializedArgumentCommand(new TestCommand(), "user secret 42");

        SerializedArgumentCommand restored = roundTrip(envelope);

        assertEquals("user secret 42", restored.getArg());
    }

    @Test
    void combinedEnvelopeCarriesOrganizationPayload() throws Exception {
        Organization organization = sampleOrganization();
        SerializedCombinedCommand envelope =
                new SerializedCombinedCommand(new TestCommand(), organization, "user secret 7");

        SerializedCombinedCommand restored = roundTrip(envelope);
        Organization payload = (Organization) restored.getObject();

        assertEquals(organization.getName(), payload.getName());
        assertEquals(organization.getAnnualTurnover(), payload.getAnnualTurnover());
        assertEquals(organization.getType(), payload.getType());
        assertEquals(organization.getCoordinates().getX(), payload.getCoordinates().getX());
        assertEquals(organization.getOfficialAddress().getTown().getName(),
                payload.getOfficialAddress().getTown().getName());
        assertEquals("user secret 7", restored.getArg());
    }

    @Test
    void objectEnvelopeAllowsNullPayload() throws Exception {
        SerializedObjectCommand envelope = new SerializedObjectCommand(new TestCommand(), null);

        SerializedObjectCommand restored = roundTrip(envelope);

        assertNull(restored.getObject());
    }

    private static Organization sampleOrganization() {
        return new Organization(
                1,
                "Acme",
                new Coordinates(10.5, 20.5),
                java.time.LocalDateTime.of(2020, 7, 1, 20, 29),
                1000.0,
                OrganizationType.COMMERCIAL,
                new Address("Main street", "191187", new Location(1f, 2f, "Saint Petersburg")),
                "user",
                "0x990000ff");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T roundTrip(T value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) in.readObject();
        }
    }
}
