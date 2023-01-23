package fr.univtln.bruno.samples.jpa.sensors.devices;

import fr.univtln.bruno.samples.jpa.sensors.observations.FeatureOfInterest;
import fr.univtln.bruno.samples.jpa.sensors.observations.ObservableProperty;
import fr.univtln.bruno.samples.jpa.sensors.observations.Observation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;

@Getter
@ToString
@Entity
public class Thermometer extends Sensor {
    @Setter
    @Column(name = "TEMPERATURE_PRECISION")
    public double precision;

    protected Thermometer() {
        this(0, Status.ONLINE, null);
    }

    protected Thermometer(double precision, Status status, Platform platform) {
        super(0, status, platform, new HashSet<>());
        this.precision = precision;
    }

    private Thermometer(Platform platform) {
        super(0, Status.ONLINE, platform, new HashSet<>());
    }

    public static Thermometer of(double precision, Platform platform) {
        return new Thermometer(precision, Status.ONLINE, platform);
    }

    public static Thermometer of(Platform platform) {
        return new Thermometer(platform);
    }

    public static Thermometer of(double precision) {
        return new Thermometer(precision, Status.ONLINE, null);
    }

    @Override
    public Observation makeObservation(String simpleResult, FeatureOfInterest featureOfInterest, ObservableProperty observableProperty) {
        return makeObservation(LocalDateTime.now(), simpleResult, featureOfInterest, observableProperty);
    }

}
