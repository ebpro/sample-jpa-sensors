package fr.univtln.bruno.samples.jpa.sensors.observations;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

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
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "FEATURE_OF_INTEREST")
public class FeatureOfInterest {
    @Id
    @EqualsAndHashCode.Include
    private String name;

    @Lob
    private Serializable feature;
}