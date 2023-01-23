package fr.univtln.bruno.samples.jpa.sensors.devices;

import fr.univtln.bruno.samples.jpa.sensors.communication.Group;
import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.observations.Observation;
import fr.univtln.bruno.samples.jpa.sensors.observations.Result;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "SENSOR")
public class Sensor {

    @EqualsAndHashCode.Include
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private long id;

    @Setter
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status = Status.UNKNOWN;

    @ManyToOne
    @JoinColumn(name = "HOST_ID")
    private Platform host;

    @ManyToMany(mappedBy = "publishers")
    private Set<Group> groups;

    @Builder
    public Sensor(Status status, Platform host) {
        this.status = status;
        this.host = host;
    }

    public Observation makeObservation(String simpleResult, FeatureOfInterest featureOfInterest, ObservableProperty observableProperty) {
        return makeObservation(LocalDateTime.now(), simpleResult, featureOfInterest, observableProperty);
    }

    public Observation makeObservation(LocalDateTime localDateTime, String simpleResult, FeatureOfInterest featureOfInterest, ObservableProperty observableProperty) {
        return Observation.of(localDateTime, this, Result.of(simpleResult), featureOfInterest, observableProperty);
    }

    public enum Status {UNKNOWN, ONLINE, OFFLINE, ERROR}
}