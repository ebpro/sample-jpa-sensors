package fr.univtln.bruno.samples.jpa.sensors.deployment;

import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.systems.SSNSystem;
import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity @Table( name="DEPLOYEMENT" )
@Log
/**
 * Deployment - Describes the Deployment of one or more Systems for a particular purpose. Deployment may be done on a Platform.
 *
 * For example, a temperature Sensor deployed on a wall, or a whole network of Sensors deployed for an Observation campaign.
 *
 * See <a href="https://www.w3.org/TR/vocab-ssn/#SSNDeployment">SSNDeployment</a>
 */
@NamedQuery(name = "Deployment.findByLabel", query = "select f from Deployment f where f.label = :label")
public class Deployment implements Serializable, SimpleEntity<UUID> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column( name="LABEL" )
    private String label;

    @Column( name="COMMENT" )
    private String comment;

    @OneToMany
    @Singular
    private Set<SSNSystem> SSNSystems;

    @OneToMany(mappedBy = "deployment")
    @Singular
    private Set<Platform> platforms;

    @OneToMany
    @Singular
    private Set<ObservableProperty> observableProperties;

    @PrePersist
    public void logNewLocationAttempt() {
        log.info("Attempting to add new deployment: " + this);
    }

    @PostPersist
    public void logNewLocationAdded() {
        log.info("Added new deployment: " + this);
    }
}