package com.worldofsoccer.location.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class VenueRepositoryIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @BeforeEach
    public void setupDb() {
        venueRepository.deleteAll();
    }

    @Test
    public void whenVenuesExist_thenReturnAllVenues() {
        Venue v1 = new Venue("Stadium A", 1000, "CityA", 1990, VenueState.UPCOMING);
        Venue v2 = new Venue("Stadium B", 2000, "CityB", 2000, VenueState.LIVE);
        venueRepository.save(v1);
        venueRepository.save(v2);

        long count = venueRepository.count();
        List<Venue> venues = venueRepository.findAll();

        assertNotNull(venues);
        assertEquals(count, venues.size());
        assertTrue(count >= 2);
    }

    @Test
    public void whenVenueExists_thenReturnById() {
        Venue v = new Venue( "My Venue", 5000, "MyCity", 1980, VenueState.PAST);
        venueRepository.save(v);
        String id = v.getVenueIdentifier().getVenueId();

        Venue found = venueRepository.findByVenueIdentifier_VenueId(id);
        assertNotNull(found);
        assertEquals(id,   found.getVenueIdentifier().getVenueId());
        assertEquals("My Venue", found.getName());
    }

    @Test
    public void whenVenueDoesNotExist_thenReturnNull() {
        Venue found = venueRepository.findByVenueIdentifier_VenueId("NON_EXISTENT");
        assertNull(found);
    }

    @Test
    public void testVenueIdentifierParameterizedConstructor() {
        String testId = "TEST-ID-1234";
        VenueIdentifier vid = new VenueIdentifier(testId);
        assertEquals(testId, vid.getVenueId());
    }

    @Test
    public void testVenueIdentifierDefaultConstructorGeneratesUUID() {
        VenueIdentifier vid = new VenueIdentifier();
        assertNotNull(vid.getVenueId());
        assertEquals(36, vid.getVenueId().length());
    }
}
