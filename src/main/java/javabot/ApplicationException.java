package javabot;

/**
 * Created Jun 27, 2005
 *
 * @author <a href="mailto:javabot@cheeseronline.org">cheeser</a>
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException() {
    }

    public ApplicationException(final Throwable cause) {
        super(cause);
    }

    public ApplicationException(final String message) {
        super(message);
    }

    public ApplicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}