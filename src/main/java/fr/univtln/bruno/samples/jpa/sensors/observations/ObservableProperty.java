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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Builder
@Table(name = "OBSERVABLE_PROPERTY")
public class ObservableProperty {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "LABEL")
    private String label;

    @Column(name="COMMENT")
    private String Comment;
}
