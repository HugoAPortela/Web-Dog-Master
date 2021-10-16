package booking.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForApprovedComments.condition)
public @interface AllowedForApprovedComments {
	String condition = "@mySecurityService.canReplyToComment(#id, #comment_id, principal)";

}
