package fr.univtln.bruno.samples.jpa.sensors.observations;

import fr.univtln.bruno.samples.jpa.sensors.deployment.Platform;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * Samples are typically subsets or extracts from the feature of interest of an observation. They are used in situations
 * where observations cannot be made directly on the ultimate feature of interest, either because the entire feature
 * cannot be observed, or because it is more convenient to use a proxy. Samples are thus artifacts of an observational
 * strategy, and usually have no significant function outside of their role in the observation process.
 * The characteristics of the samples themselves are generally of little interest, except to the manager of a sampling
 * campaign, or sample curator.
 * A Sample is intended to sample some FeatureOfInterest, so there is an expectation of at least one isSampleOf property.
 * However, in some cases the identity, and even the exact type, of the sampled feature may not be known when observations
 * are made using the sampling features.
 * Physical samples are sometimes known as 'specimens'.
 *
 * A 'station' is essentially an identifiable locality where a Sensor system or procedure may be deployed and
 * an observation made. In the context of the observation model, it connotes the 'world in the vicinity of the station',
 * so the observed properties relate to the physical medium at the station, and not to any physical artifact such
 * as a mooring, buoy, benchmark, monument, well, etc.
 * A statistical sample is often designed to be characteristic of an entire population, so that Observations can be
 * made regarding the sample that provide a good estimate of the properties of the population.
 *
 * @see <a href="https://www.w3.org/TR/vocab-ssn/#SOSASample">SOSASample</a>
 */
@Setter
@Getter
@ToString
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Builder
@Table(name = "SAMPLE")
public class Sample {
    @Id
    @EqualsAndHashCode.Include
    private String label;

    @Column(name = "COMMENT")
    private String comment;

    @ManyToMany
    private Set<Platform> platform;
}