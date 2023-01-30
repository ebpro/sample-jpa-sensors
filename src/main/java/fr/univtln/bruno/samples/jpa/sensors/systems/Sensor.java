package fr.univtln.bruno.samples.jpa.sensors.systems;

import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.observations.Observation;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
@ToString(callSuper = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@SuperBuilder
@NamedQuery(name = "Sensor.findByLabel", query = "select f from Sensor f where f.label = :label")
public class Sensor extends SSNSystem {

    @ManyToMany
    @ToString.Exclude
    @Singular
    private Set<ObservableProperty> observableProperties;
    /**
     * The measured quantity : Temperature, ...
     * see  https://www.javadoc.io/doc/javax.measure/unit-api/latest/javax/measure/quantity/package-summary.html
     */
    private String quantityClass;

    public Observation.ObservationBuilder makeObservation() {
        Observation.ObservationBuilder builder = Observation.builder()
                .resultDateTime(LocalDateTime.now())
                .source(this);

        if (observableProperties.size() == 1)
            builder = builder.observableProperty(observableProperties.stream().findFirst().get());

        return builder;
    }
}