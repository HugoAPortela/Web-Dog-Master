package booking.repository;

import booking.model.Hotel;
import org.springframework.data.repository.CrudRepository;


public interface HotelRepository extends CrudRepository<Hotel, Long> {
	
	Hotel findByName(String name);
}

