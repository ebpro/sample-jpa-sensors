package fr.univtln.bruno.samples.jpa.sensors;

import fr.univtln.bruno.samples.jpa.sensors.daos.FeatureOfInterestDAO;
import fr.univtln.bruno.samples.jpa.sensors.deployment.Deployment;
import fr.univtln.bruno.samples.jpa.sensors.deployment.Platform;
import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.observations.Observation;
import fr.univtln.bruno.samples.jpa.sensors.observations.Result;
import fr.univtln.bruno.samples.jpa.sensors.systems.SSNSystem;
import fr.univtln.bruno.samples.jpa.sensors.systems.Sensor;
import fr.univtln.bruno.samples.utils.AppConstants;
import fr.univtln.bruno.samples.utils.dao.AbstractDAO;
import fr.univtln.bruno.samples.utils.dao.DAO;
import fr.univtln.bruno.samples.utils.dao.exceptions.DAOException;
import fr.univtln.bruno.samples.utils.exceptions.BusinessException;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.java.Log;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.ImmutableMap;

import javax.measure.quantity.Temperature;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Log
public class House {


    public static void main(String[] args) {

        try (EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("sensors_pu");
             DAO<Platform, UUID> platformDAO = new AbstractDAO<>(entityManagerFactory.createEntityManager()) {
             };
             DAO<ObservableProperty, UUID> observablePropertyDAO = new AbstractDAO<>(entityManagerFactory.createEntityManager()) {
             };
             DAO<SSNSystem, UUID> ssnSystemDAO = new AbstractDAO<>(entityManagerFactory.createEntityManager()) {
             };
             DAO<Sensor, UUID> sensorDAO = new AbstractDAO<>(entityManagerFactory.createEntityManager()) {
             };
             DAO<Observation, UUID> observationDAO = new AbstractDAO<>(entityManagerFactory.createEntityManager()) {
             };
             FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO(entityManagerFactory.createEntityManager());
             DAO<Deployment, UUID> deploymentDAO = new AbstractDAO<>(entityManagerFactory.createEntityManager()) {
             };
        ) {
            ImmutableMap<String, String> locations = Maps.mutable.<String, String>empty()
                    .withKeyValue("E00", "house")
                    .withKeyValue("E01", "kitchen")
                    .withKeyValue("E02", "entrance")
                    .withKeyValue("E03", "laundry")
                    .withKeyValue("E04", "office")
                    .withKeyValue("E05", "dining room")
                    .withKeyValue("E06", "lounge")
                    .withKeyValue("E07", "bedroom 1")
                    .withKeyValue("E08", "bathroom 1")
                    .withKeyValue("E11", "bedroom 2")
                    .withKeyValue("E12", "bathroom 2")
                    .withKeyValue("E13", "boiler room")
                    .withKeyValue("E14", "triangle")
                    .withKeyValue("E15", "cellar")
                    .withKeyValue("E16", "corridor")
                    .withKeyValue("E17", "stairs")
                    .withKeyValue("E18", "bathroom 3")
                    .withKeyValue("E19", "bedroom 3")
                    .withKeyValue("E20", "hall")
                    .withKeyValue("E21", "bedroom 4")
                    .withKeyValue("E22", "bedroom 5")
                    .withKeyValue("E23", "storage room").toImmutable();

            //Each room is a feature of interest
            featureOfInterestDAO.begin();
            locations.forEachKeyValue((locationCode, locationDescription) ->
                    featureOfInterestDAO.persist(FeatureOfInterest.builder()
                            .label(locationCode)
                            .comment(locationDescription)
                            .build()));
            featureOfInterestDAO.commit();

            //we observe temperature and humidity in each room
            observablePropertyDAO.begin();
            locations.forEachKeyValue((locationCode, locationDescription) -> {
                try {
                    FeatureOfInterest featureOfInterest = featureOfInterestDAO
                            .findWithNamedQuery("FeatureOfInterest.findByLabel",
                                    Map.of("label", locationCode)).get(0);
                    observablePropertyDAO.persist(ObservableProperty.builder()
                            .label(String.format("%s/temperature", locationCode))
                            .comment(String.format("Temperature of %s", locationDescription))
                            .featureOfInterest(featureOfInterest)
                            .build());
                    observablePropertyDAO.persist(ObservableProperty.builder()
                            .label(String.format("%s/humidity", locationCode))
                            .comment(String.format("Humidity of %s", locationDescription))
                            .featureOfInterest(featureOfInterest)
                            .build());
                } catch (DAOException e) {
                    log.severe(e.getMessage());
                }
            });
            observablePropertyDAO.commit();

            //Each room is a platform which hosts sensors, actuators and samplers.
            platformDAO.begin();
            locations.forEachKeyValue((locationCode, locationDescription) ->
                    platformDAO.persist(Platform.builder()
                            .label(locationCode)
                            .comment(locationDescription)
                            .build()));
            platformDAO.commit();

            //Define a KNX switch with a temperature sensor in some rooms.
            final List<String> roomsWithKNXSwitchWithTSensor = List.of("E01", "E04", "E05", "E06", "E07", "E11", "E19", "E21", "E22");
            ssnSystemDAO.begin();
            locations.select((k, v) -> roomsWithKNXSwitchWithTSensor.contains(k))
                    .forEachKeyValue((locationCode, locationDescription) ->
                    {
                        try {
                            ssnSystemDAO.persist(SSNSystem
                                    .builder()
                                    .label(String.format("SW_%s_1", locationCode))
                                    .comment(String.format("BE-GTT4W.01/KNX Switch in %s.", locationCode))
                                    .subsystem(Sensor.builder()
                                            .label(String.format("CVCTH_%s", locationCode))
                                            .comment("Temperature sensor embedded on a BE-GTT4W.01/T")
                                            .observableProperty(observablePropertyDAO
                                                    .findWithNamedQuery("ObservableProperty.findByLabel", Map.of("label", locationCode + "/temperature")).get(0))
                                            .quantityClass(Temperature.class.getName())
                                            .build())
                                    .plateform(platformDAO
                                            .findWithNamedQuery("Platform.findByLabel",
                                                    Map.of("label", locationCode)).get(0))
                                    .build());
                        } catch (DAOException e) {
                            log.severe(e.getMessage());
                        }
                    });
            ssnSystemDAO.commit();

            //Deploy the KNX switch with a temperature sensor in some rooms.
            locations.select((k, v) -> roomsWithKNXSwitchWithTSensor.contains(k))
                    .forEachKeyValue((locationCode, locationName) ->
                    {
                        try {
                            platformDAO.begin();
                            Platform platform = platformDAO
                                    .findWithNamedQuery("Platform.findByLabel", Map.of("label", locationCode)).get(0);
                            Deployment deployment = Deployment.builder()
                                    .label(String.format("%sDeployment", locationCode))
                                    .comment(String.format("Deployment of a BE-GTT4W.01/T in %s (%s)", locationCode, locationName))
                                    .observableProperty(observablePropertyDAO
                                            .findWithNamedQuery("ObservableProperty.findByLabel", Map.of("label", locationCode + "/temperature")).get(0))
                                    .SSNSystem(ssnSystemDAO
                                            .findWithNamedQuery("SSNSystem.findByLabel", Map.of("label", String.format("SW_%s_1", locationCode))).get(0))
                                    .platform(platform)
                                    .build();
                            platformDAO.commit();

                            deploymentDAO.begin();
                            deploymentDAO.persist(deployment);
                            deploymentDAO.commit();

                            platformDAO.begin();
                            platform.setDeployment(deployment);
                            platformDAO.merge(platform);
                            platformDAO.commit();

                        } catch (DAOException e) {
                            log.severe(e.getMessage());
                        }
                    });

            Random random = new Random();

            observationDAO.begin();
            locations.select((k, v) -> roomsWithKNXSwitchWithTSensor.contains(k))
                    .forEachKeyValue((locationCode, locationDescription) -> {
                        try {
                            Sensor sensor = sensorDAO
                                    .findWithNamedQuery("Sensor.findByLabel", Map.of("label", String.format("CVCTH_%s", locationCode))).get(0);
                            observationDAO.persist(sensor.makeObservation()
                                    .result(Result.of(random.nextFloat(40) - 10 + " °C"))
                                    .observableProperty(sensor.getObservableProperties().stream().findFirst().get())
                                    .build());
                        } catch (DAOException e) {
                            log.severe(e.getMessage());
                        }
                    });
            observationDAO.commit();

            log.info(featureOfInterestDAO.getTemperaturesForLabel("E01").toString());

        }
    }
}
