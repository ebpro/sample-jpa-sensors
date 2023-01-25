package fr.univtln.bruno.samples.jpa.sensors.deployment;

import fr.univtln.bruno.samples.jpa.sensors.systems.System;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * A Platform is an entity that hosts other entities, particularly Sensors, Actuators, Samplers, and other Platforms.
 *
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
public class Platform {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "LABEL")
    private String label;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "DEPLOYMENT")
    @Setter
    private Deployment deployment;

    @Singular
    @Setter
    @OneToMany(mappedBy = "host", cascade = CascadeType.PERSIST)
    Set<System> systems;

}
