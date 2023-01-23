package fr.univtln.bruno.samples.jpa.sensors.observations;

import jakarta.persistence.*;
import lombok.*;

/**
 * An observable quality (property, characteristic) of a FeatureOfInterest.
 *
 * @see FeatureOfInterest
 */
@Setter
@Getter
@ToString
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "OBSERVABLE_PROPERTY")
public class ObservableProperty {
    @Id
    @EqualsAndHashCode.Include
    private String name;
}
