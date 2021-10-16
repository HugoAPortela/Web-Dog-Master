package booking.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForManageComment.condition)
public @interface AllowedForManageComment {
	String condition = "@mySecurityService.canEditComment(#id_comment, principal) or " + AllowedForCommentModerator.condition;
}
