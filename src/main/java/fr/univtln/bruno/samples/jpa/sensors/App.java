package fr.univtln.bruno.samples.jpa.sensors;

import fr.univtln.bruno.samples.jpa.sensors.communication.Group;
import fr.univtln.bruno.samples.jpa.sensors.devices.Platform;
import fr.univtln.bruno.samples.jpa.sensors.devices.Sensor;
import fr.univtln.bruno.samples.jpa.sensors.devices.Thermometer;
import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Demo for Sensors API
 */
public class App {

    private static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {

        Server h2Server;
        try {
            h2Server = Server.createTcpServer(new String[]{"-ifNotExists"}).start();
            if (h2Server.isRunning(true)) {
                System.out.println(h2Server.getStatus());
            } else {
                System.err.println("Could not start H2 server.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to start H2 server: " + e.getMessage());
        }

        entityManagerFactory = Persistence.createEntityManagerFactory("sensors_pu");

        Map<String, Location> locations = Stream.of(new String[][]{
                {"E00", "Cuisine"},
                {"E01", "Salon"},
                {"E02", "Salle à manger"},
                {"E03", "Chambre"}
        }).collect(Collectors.collectingAndThen(
                Collectors.toMap(data -> data[0], data -> Location.of(data[0], data[1])),
                Collections::<String, Location>unmodifiableMap));

        Map<String, FeatureOfInterest> featureOfInterestMap = locations.values().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(data -> data.getId(), data -> FeatureOfInterest.of(data.getId(), data)),
                        Collections::<String, FeatureOfInterest>unmodifiableMap));

        Map<String, ObservableProperty> observablePropertyMap = Stream.of(new String[]{"Temperature", "Humidity"})
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(data -> data, data -> ObservableProperty.of(data)),
                        Collections::<String, ObservableProperty>unmodifiableMap));

        {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            locations.values().stream().forEach(entityManager::persist);

            featureOfInterestMap.values().stream().forEach(f -> entityManager.persist(f));

            entityManager.getTransaction().commit();
            entityManager.close();
        }

        {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            Location location = entityManager.find(Location.class, "E00");
            System.out.println(location);
        }

        List<Platform> plateforms;
        List<Sensor> sensors;
        List<Group> groups;
        {
            EntityManager entityManager = entityManagerFactory.createEntityManager();

            plateforms = List.of(
                    Platform.of("P1", locations.get("E00")),
                    Platform.of("P2", locations.get("E01"))
            );

            persist(plateforms);

            sensors = List.of(Sensor.builder().host(plateforms.get(0)).build(),
                    Sensor.builder().host(plateforms.get(0)).build(),
                    Sensor.builder().host(plateforms.get(1)).build(),
                    Thermometer.of(0.5)
            );

            groups = List.of(Group.of("G1")
                            .addPublisher(sensors.get(0))
                            .addPublisher(sensors.get(1)),
                    Group.of("G2")
                            .addPublisher(sensors.get(2))
            );

            entityManager.getTransaction().begin();
            sensors.forEach(entityManager::persist);
            entityManager.getTransaction().commit();

            entityManager.getTransaction().begin();
            groups.forEach(entityManager::persist);
            entityManager.getTransaction().commit();
            entityManager.close();
        }

        {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();
            ObservableProperty temperature = ObservableProperty.of("Temperature");
            entityManager.merge(temperature);
            ObservableProperty humidity = ObservableProperty.of("Humidity");
            entityManager.merge(humidity);

            entityManager.persist(sensors.get(0).makeObservation("20 °C", featureOfInterestMap.get("E00"), temperature));
            entityManager.persist(sensors.get(1).makeObservation("70 %", featureOfInterestMap.get("E00"), humidity));
            entityManager.persist(sensors.get(2).makeObservation("25 °C", featureOfInterestMap.get("E01"), temperature));
            entityManager.getTransaction().commit();
            entityManager.close();
        }

        //h2Server.stop();
    }

    private static void persist(List entities) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entities.stream().forEach(entityManager::persist);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
