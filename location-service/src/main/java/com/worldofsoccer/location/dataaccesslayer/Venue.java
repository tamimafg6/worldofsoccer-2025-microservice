package com.worldofsoccer.location.dataaccesslayer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venues")
@Data
@NoArgsConstructor
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private VenueIdentifier venueIdentifier;

    @Enumerated(EnumType.STRING)
    private VenueState venueState;

    @Column(nullable = false)
    private String name;
    private Integer capacity;
    private String city;
    private Integer yearBuilt;

    public Venue( String name, Integer capacity, String city, Integer yearBuilt,VenueState venueState) {
        this.venueIdentifier = new VenueIdentifier();
        this.name = name;
        this.capacity = capacity;
        this.city = city;
        this.yearBuilt = yearBuilt;
        this.venueState = venueState;
    }


}
