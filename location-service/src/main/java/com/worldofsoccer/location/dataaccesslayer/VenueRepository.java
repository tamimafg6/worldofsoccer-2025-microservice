package com.worldofsoccer.location.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Integer> {
    Venue findByVenueIdentifier_VenueId (String id);

}
