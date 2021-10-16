package booking.repository;

import booking.model.Category;
import org.springframework.data.repository.CrudRepository;


public interface CategoryRepository extends CrudRepository<Category, Long> {
	
	Category findByName(String name);
}

