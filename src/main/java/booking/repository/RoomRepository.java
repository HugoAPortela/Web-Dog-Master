package booking.repository;

import booking.model.Room;
import org.springframework.data.repository.CrudRepository;


public interface RoomRepository extends CrudRepository<Room, Long> {
	
	
}

