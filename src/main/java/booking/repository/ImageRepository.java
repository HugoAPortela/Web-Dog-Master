package booking.repository;

import booking.model.Image;
import org.springframework.data.repository.CrudRepository;


public interface ImageRepository extends CrudRepository<Image, Long> {
	
}

