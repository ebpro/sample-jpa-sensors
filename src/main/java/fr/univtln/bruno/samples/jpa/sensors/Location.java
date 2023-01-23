package fr.univtln.bruno.samples.jpa.sensors;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@ToString
@AllArgsConstructor(staticName="of")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity @Table( name="LOCATION" )
public class Location implements Serializable {

    @EqualsAndHashCode.Include
    @Column( name="ID" )
    @Id
    private String id;

    @Column( name="DESCRIPTION" )
    private String description;

    @PrePersist
    public void logNewLocationAttempt() {
        System.out.println("Attempting to add new location: " + this);
    }

    @PostPersist
    public void logNewLocationAdded() {
        System.out.println("Added new location: " + this);
    }
}