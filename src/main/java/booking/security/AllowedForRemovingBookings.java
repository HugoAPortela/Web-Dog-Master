package booking.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForRemovingBookings.condition)
public @interface AllowedForRemovingBookings {
	String condition = "@mySecurityService.canRemoveBooking(#booking_id, principal)";

}
