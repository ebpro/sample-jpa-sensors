package fr.univtln.bruno.samples.jpa.sensors.observations;

import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * The thing whose property is being estimated or calculated in the course of an Observation
 * to arrive at a Result, or whose property is being manipulated by an Actuator, or which is
 * being sampled or transformed in an act of Sampling.
 *
 * Example 	When measuring the height of a tree, the height is the observed ObservableProperty, 20m may be the Result
 * of the Observation, and the tree is the FeatureOfInterest. A window is a FeatureOfInterest for an automatic window
 * control Actuator.
 *
 * @see <a href="https://www.w3.org/TR/vocab-ssn/#SOSAFeatureOfInterest">SOSAFeatureOfInterest</a>
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "FEATURE_OF_INTEREST")
@NamedQuery(name = "FeatureOfInterest.findByLabel",
        query = "select f from FeatureOfInterest f where f.label = :label")
@NamedQuery(name= "FeatureOfInterest.findTemperaturesByFeatureOfInterestLabel",
        query= """
                select 
                    new fr.univtln.bruno.samples.jpa.sensors.observations.ObservationMinimal(o.resultDateTime, o.result.value,o.result.unit) 
                from 
                    FeatureOfInterest f 
                        join Observation o 
                where 
                    f.label=:label and o.source.quantityClass = 'javax.measure.quantity.Temperature'""" )
public class FeatureOfInterest implements SimpleEntity<UUID>, Serializable {
    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();


    @EqualsAndHashCode.Include
    @Column(name = "LABEL")
    private String label;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @Singular
    @ToString.Exclude
    /**
     * Relation between a FeatureOfInterest and the Sample used to represent it.
     */
    private Set<Sample> samples;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "featureOfInterest")
    @Singular
    @ToString.Exclude
    private Set<ObservableProperty> observableProperties;
}