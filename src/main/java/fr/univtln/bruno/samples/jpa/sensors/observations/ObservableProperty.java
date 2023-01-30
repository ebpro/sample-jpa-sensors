package fr.univtln.bruno.samples.jpa.sensors.observations;

import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

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
@NamedQuery(name = "ObservableProperty.findByLabel", query = "select f from ObservableProperty f where f.label = :label")
public class ObservableProperty implements SimpleEntity<UUID>, Serializable {
    @Id
    @GeneratedValue
    @Column(name = "ID", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "LABEL")
    private String label;

    @Column(name="COMMENT")
    private String comment;

    @JoinColumn(name = "FEATURE_OF_INTEREST_ID")
    private FeatureOfInterest featureOfInterest;
}
