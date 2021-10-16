package booking.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForSystemUsers.condition)
public @interface AllowedForSystemUsers {
	String condition = "hasAnyRole({'ROLE_ADMIN', 'ROLE_USER','ROLE_COMMENT_MODERATOR','ROLE_HOTEL_MANAGER'})";
}
