package fr.univtln.bruno.samples.jpa.sensors;

import fr.univtln.bruno.samples.jpa.sensors.communication.Group;
import fr.univtln.bruno.samples.jpa.sensors.deployment.Deployment;
import fr.univtln.bruno.samples.jpa.sensors.deployment.Platform;
import fr.univtln.bruno.samples.jpa.sensors.systems.Sensor;
import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Demo for Sensors API
 */
public class App {

    private static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {

/*        Server h2Server;
        try {
            h2Server = Server.createTcpServer(new String[]{"-ifNotExists"}).start();
            if (h2Server.isRunning(true)) {
                System.out.println(h2Server.getStatus());
            } else {
                System.err.println("Could not start H2 server.");
            }
        } catch (SQLException e) {
            System.err.println("Failed to start H2 server: " + e.getMessage());
        }*/

        entityManagerFactory = Persistence.createEntityManagerFactory("sensors_pu");

        Map<String, Deployment> locations = Stream.of(new String[][]{
                {"E00", "Cuisine"},
                {"E01", "Salon"},
                {"E02", "Salle à manger"},
                {"E03", "Chambre"}
        }).collect(Collectors.collectingAndThen(
                Collectors.toMap(data -> data[0], data -> Deployment.builder().label(data[0]).comment(data[1]).build()),
                Collections::<String, Deployment>unmodifiableMap));

        /*Map<String, FeatureOfInterest> featureOfInterestMap = locations.values().stream()
                .collect(Collectors.collectingAndThen(Deployment::getLabel,
                                data -> FeatureOfInterest.builder().label(data.getLabel()).build()),
                        Collections::<String, FeatureOfInterest>unmodifiableMap);*/

        Map<String, FeatureOfInterest> featureOfInterestMap = new HashMap<>();
        locations.values().forEach(l->featureOfInterestMap.put(l.getLabel(), FeatureOfInterest.builder().label(l.getLabel()).comment(l.getComment()).build()));


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
            Deployment deployment = entityManager.find(Deployment.class, "E00");
            System.out.println(deployment);
        }

        List<Platform> plateforms;
        List<Sensor> sensors;
        List<Group> groups;
        {
            EntityManager entityManager = entityManagerFactory.createEntityManager();


            sensors = List.of(Sensor.builder().label("S1").build(),
                    Sensor.builder().label("S2").build(),
                    Sensor.builder().label("S3").build()
            );

            plateforms = List.of(
                    Platform.builder().label("P1").deployment(locations.get("E00")).build(),
                    Platform.builder().label("P2").deployment(locations.get("E01")).build()
            );

            persist(plateforms);

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
            ObservableProperty temperature = ObservableProperty.builder().label("Temperature").build();;
            entityManager.merge(temperature);
            ObservableProperty humidity = ObservableProperty.builder().label("Humidity").build();;
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
