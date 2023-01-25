package fr.univtln.bruno.samples.jpa.sensors.communication;

import fr.univtln.bruno.samples.jpa.sensors.devices.Sensor;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "GROUP_OF_DEVICES")
/**
 *
 */
public class Group {
    public static Group of(String name) {
        return new Group(UUID.randomUUID(), name, new HashSet<>());
    }

    @Id
    @Column(name = "UUID")
    private UUID uuid;

    @Column(name = "NAME")
    private String name;

    @ManyToMany
    @ToString.Exclude
    @JoinTable(name = "PUBLISHERS")
    private Set<Sensor> publishers;

    public Group addPublisher(Sensor sensor) {
        publishers.add(sensor);
        return this;
    }

    public Group removePublisher(Sensor sensor) {
        publishers.remove(sensor);
        return this;
    }

}
