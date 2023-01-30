package fr.univtln.bruno.samples.jpa.sensors.deployment;

import fr.univtln.bruno.samples.jpa.sensors.systems.SSNSystem;
import fr.univtln.bruno.samples.utils.dao.entities.SimpleEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * A Platform is an entity that hosts other entities, particularly Sensors, Actuators, Samplers, and other Platforms.
 * <p>
 * Examples: A post, buoy, vehicle, ship, aircraft, satellite, cell-phone, human or animal may act as Platforms
 * for (technical or biological) Sensors or Actuators.
 *
 * @see <A href="https://www.w3.org/TR/vocab-ssn/#SOSAPlatform">SOSAPlatform</A>
 **/
@ToString
@Getter
@NoArgsConstructor
@Entity
@Table(name = "PLATFORM")
@Builder
@AllArgsConstructor

@NamedQuery(name = "Platform.findByLabel", query = "select f from Platform f where f.label = :label")
public class Platform implements SimpleEntity<UUID>, Serializable {

    @Singular
    @Setter
    @OneToMany(mappedBy = "plateform", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private Set<SSNSystem> SSNSystems;
    @Id
    @GeneratedValue
    @Column(name = "ID", updatable = false, nullable = false)
    @Builder.Default
    @EqualsAndHashCode.Include
    private UUID id = UUID.randomUUID();
    @Column(name = "LABEL", unique = true)
    private String label;
    @Column(name = "COMMENT")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "DEPLOYMENT")
    @Setter
    private Deployment deployment;

}
