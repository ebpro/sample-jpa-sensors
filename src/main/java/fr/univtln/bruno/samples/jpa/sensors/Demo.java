package fr.univtln.bruno.samples.jpa.sensors;

import fr.univtln.bruno.samples.jpa.sensors.devices.Platform;
import fr.univtln.bruno.samples.jpa.sensors.devices.Sensor;
import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.observations.Sample;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.java.Log;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

@Log
public class Demo {

    private static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {

        entityManagerFactory = Persistence.createEntityManagerFactory("sensors_pu");

        Platform platform1 = Platform.builder().label("IPhone 7 - IMEI 35-207306-844818-0")
                .comment("IPhone 7 - IMEI 35-207306-844818-0 - John Doe")
                .system(Sensor.builder()
                        .label("35-207306-844818-0/BMP282")
                        .build())
                .build();
        FeatureOfInterest featureOfInterest1 = FeatureOfInterest.builder()
                .label("Room #145")
                .sample(Sample.builder()
                        .label("Room145/east")
                        .comment("This wall hosts PCB Board 1 with DHT22 temperature and humidity sensor #4578.")
                        .build())
                .sample(Sample.builder()
                        .label("Room145/south")
                        .comment("This wall hosts PCB Board 2 with DHT22 temperature and humidity sensor #4579.")
                        .build())
                .build();
        log.info(featureOfInterest1.toString());


        MutableMap<String, Platform> platforms = Maps.mutable.of(
                "PCB Board 1",
                Platform
                        .builder()
                        .label("PCB Board 1")
                        .comment("PCB Board 1 hosts DHT22 temperature and humidity sensor #4578 permanently, one can say it has it as one of its subsystems.")
                        .system(Sensor.builder().label("DHT22 sensor #4578").build())
                        .build(),
                "PCB Board 2",
                Platform
                        .builder()
                        .label("PCB Board 2")
                        .comment("PCB Board 2 hosts DHT22 temperature and humidity sensor #4579 permanently, one can say it has it as one of its subsystems.")
                        .system(Sensor.builder().label("DHT22 sensor #4579").build())
                        .build(),
                "PCB Board 3",
                Platform
                        .builder()
                        .label("PCB Board 3")
                        .comment("PCB Board 3 hosts DHT22 temperature and humidity sensor #4580 permanently, one can say it has it as one of its subsystems.")
                        .system(Sensor.builder().label("DHT22 sensor #4580").build())
                        .build());

        MutableMap<String, Deployment> deployments = Maps.mutable.of(
                "Room245Deployment",
        Deployment.builder()
                .label("Room245Deployment")
                .comment("Deployment of PCB Board 3 on the south wall of room #245 for the purpose of observing the temperature and humidity of room #245.")
                .observableProperty(ObservableProperty.builder().label("Room245#temperature").build())
                .observableProperty(ObservableProperty.builder().label("Room245#humidity").build())
                .platform(platforms.get("PCB Board 1"))
                .build(),
                "Room145Deployment",
        Deployment.builder()
                .label("Room145Deployment")
                .comment("Deployment of PCB Board 1 and 2 on the east and south wall of room #145, respectively, for the purpose of observing the temperature and humidity of room #145.")
                .observableProperty(ObservableProperty.builder().label("Room145#temperature").build())
                .observableProperty(ObservableProperty.builder().label("Room145#humidity").build())
                .platform(platforms.get("PCB Board 1"))
                .build());


        log.info(platforms.toString());

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.persist(platform1);

        entityManager.persist(featureOfInterest1);

        deployments.forEach(entityManager::persist);

        platforms.forEach(entityManager::persist);

        entityManager.getTransaction().commit();
        entityManager.close();

    }
}
