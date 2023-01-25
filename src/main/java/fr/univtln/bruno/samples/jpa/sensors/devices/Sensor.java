package fr.univtln.bruno.samples.jpa.sensors.devices;

import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.observations.Observation;
import fr.univtln.bruno.samples.jpa.sensors.observations.Result;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Sensor - Device, agent (including humans), or software (simulation) involved in, or implementing, a Procedure.
 * Sensors respond to a Stimulus, e.g., a change in the environment, or Input data composed of the Results of
 * prior Observations, and generate a Result. Sensors can be hosted by Platforms.
 * <p>
 * Example : Accelerometers, gyroscopes, barometers, magnetometers, and so forth are Sensors that are typically
 * mounted on a modern smartphone (which acts as Platform). Other examples of Sensors include the human eyes.
 *
 * @see <a href="https://www.w3.org/TR/vocab-ssn/#SOSASensor">SOSASensor</a>
 */
@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@Table(name = "SENSOR")
@SuperBuilder
public class Sensor extends System {

    @EqualsAndHashCode.Include
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private long id;

    private ObservableProperty observed;

    public Observation makeObservation(String simpleResult, FeatureOfInterest featureOfInterest, ObservableProperty observableProperty) {
        return makeObservation(LocalDateTime.now(), simpleResult, featureOfInterest, observableProperty);
    }

    public Observation makeObservation(LocalDateTime localDateTime, String simpleResult, FeatureOfInterest featureOfInterest, ObservableProperty observableProperty) {
        return Observation.of(localDateTime, this, Result.of(simpleResult), featureOfInterest, observableProperty);
    }

}