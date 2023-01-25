package fr.univtln.bruno.samples.jpa.sensors.systems;

import fr.univtln.bruno.samples.jpa.sensors.communication.Group;
import fr.univtln.bruno.samples.jpa.sensors.deployment.Platform;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

/**
 * System is a unit of abstraction for pieces of infrastructure that implement Procedures.
 * A System may have components, its subsystems, which are other systems.
 *
 * @see <A href="https://www.w3.org/TR/vocab-ssn/#SSNSystem">System</A>
 *
 */
@ToString
@Getter
@NoArgsConstructor
@Entity
@Table(name = "SYSTEM")
@SuperBuilder
public abstract class System {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "HOST_ID")
    private Platform host;

    @ManyToMany(mappedBy = "publishers")
    private Set<Group> groups;

    @ManyToMany
    @JoinTable(name = "SUBSYSTEM")
    private Set<System> subsystem;

    @Setter
    @Column(name = "LABEL")
    private String label;

    @Setter
    @Column(name = "COMMENT")
    private String comment;

    @Setter
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)

    @Builder.Default
    private Sensor.Status status = Sensor.Status.UNKNOWN;

    protected System(String label) {
        this.label = label;
    }

    public enum Status {UNKNOWN, ONLINE, OFFLINE, ERROR}
}
