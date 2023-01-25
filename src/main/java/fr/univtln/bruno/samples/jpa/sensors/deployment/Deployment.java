package fr.univtln.bruno.samples.jpa.sensors.deployment;

import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;

import java.io.Serializable;
import java.util.Set;

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
public class Deployment implements Serializable {
    @EqualsAndHashCode.Include
    @Column( name="LABEL" )
    @Id
    private String label;

    @Column( name="COMMENT" )
    private String comment;

    @OneToOne
    private fr.univtln.bruno.samples.jpa.sensors.systems.System system;

    @OneToOne
    private Platform platform;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn( name="OBSERVABLE_PROPERTY_ID" )
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