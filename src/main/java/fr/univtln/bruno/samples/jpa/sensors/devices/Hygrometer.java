package fr.univtln.bruno.samples.jpa.sensors.devices;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;

@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
public class Hygrometer extends Sensor {
    @Column(name = "MIN_HYGROMETRY")
    private int minHygrometry = 0;
    @Column(name = "MAX_HYGROMETRY")
    private int maxHygrometry = 100;

    public Hygrometer(Platform platform) {
        super(0, Status.ONLINE, platform, new HashSet<>());
    }
}
