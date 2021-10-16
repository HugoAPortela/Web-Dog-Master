package booking.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Hotel")
public class HotelNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
