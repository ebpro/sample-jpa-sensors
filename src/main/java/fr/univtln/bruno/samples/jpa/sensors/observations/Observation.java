package fr.univtln.bruno.samples.jpa.sensors.observations;

import fr.univtln.bruno.samples.jpa.sensors.systems.Sensor;
import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.*;
import lombok.*;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.Units;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Table(name = "OBSERVATION")
public class Observation implements SimpleEntity<UUID> {
    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "RESULT_DATETIME")
    private LocalDateTime resultDateTime;

    @ManyToOne
    @JoinColumn(name = "SOURCE")
    private Sensor source;

    @Embedded
    private Result result;

    @ManyToOne
    @JoinColumn(name = "OBSERVABLE_PROPERTY_ID")
    private ObservableProperty observableProperty;
}

