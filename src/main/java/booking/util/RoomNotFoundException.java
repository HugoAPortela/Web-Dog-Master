package booking.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Room")
public class RoomNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
