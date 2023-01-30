package fr.univtln.bruno.samples.jpa.sensors.systems;

import fr.univtln.bruno.samples.jpa.sensors.deployment.Platform;
import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * System is a unit of abstraction for pieces of infrastructure that implement Procedures.
 * A System may have components, its subsystems, which are other systems.
 *
 * @see <A href="https://www.w3.org/TR/vocab-ssn/#SSNSystem">System</A>
 */
@ToString
@Getter
@NoArgsConstructor
@Entity
@Table(name = "SSNSYSTEM")
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NamedQuery(name = "SSNSystem.findByLabel", query = "select f from SSNSystem f where f.label = :label")
public class SSNSystem implements SimpleEntity<UUID>, Serializable {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "HOST_ID")
    @ToString.Exclude
    private Platform plateform;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "SSNSUBSYSTEM")
    @Singular
    @ToString.Exclude
    private Set<SSNSystem> subsystems;

    @Setter
    @Column(name = "LABEL")
    private String label;

    @Setter
    @Column(name = "COMMENT")
    private String comment;

    @Setter
    @Column(name = "STATUS")
    @Builder.Default
    private Sensor.Status status = Sensor.Status.UNKNOWN;

    public enum Status {UNKNOWN, ONLINE, OFFLINE, ERROR}
}
