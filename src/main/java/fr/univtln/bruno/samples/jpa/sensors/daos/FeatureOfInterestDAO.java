package fr.univtln.bruno.samples.jpa.sensors.daos;

import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservationMinimal;
import fr.univtln.bruno.samples.utils.dao.AbstractDAO;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

public class FeatureOfInterestDAO extends AbstractDAO<FeatureOfInterest, UUID> {
    public FeatureOfInterestDAO(EntityManager entityManager) {
        super(entityManager);
    }

    public List<ObservationMinimal> getTemperaturesForLabel(String label) {
        return getEntityManager()
                .createNamedQuery("FeatureOfInterest.findTemperaturesByFeatureOfInterestLabel", ObservationMinimal.class)
                .setParameter("label", label)
                .getResultList();
    }
}
