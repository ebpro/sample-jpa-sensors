package fr.univtln.bruno.samples.jpa.sensors.devices;

import fr.univtln.bruno.samples.jpa.sensors.Location;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
public class Platform {
    public static Platform of(String name, Location location) {
        return new Platform(name, location, null);
    }

    @Builder
    private Platform(String name, Location location, Set<Sensor> sensors) {
        this.name = name;
        this.location = location;
        this.sensors = sensors;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne
    @JoinColumn(name = "LOCATION")
    @Setter
    private Location location;

    @Singular
    @Setter
    @OneToMany(mappedBy = "host")
    Set<Sensor> sensors;

}
