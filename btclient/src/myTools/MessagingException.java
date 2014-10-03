package myTools;

public final class MessagingException extends Exception
{
    /**
     * Automatically generated and unused.
     */
    private static final long serialVersionUID = -4829433031030292728L;
    
    /**
     * The message to display regarding the exception.
     */
    private final String message;
    
    /**
     * Creates a new MessagingException with a blank message.
     */
    public MessagingException()
    {
        this.message = null;
    }
    
    /**
     * Creates a new MessagingException with the message provided.
     * @param message the message to display to the user.
     */
    public MessagingException(final String message)
    {
        this.message = message;
    }
    
    /**
     * Returns a string containing the exception message specified during
     * creation.
     */
    @Override
    public final String toString()
    {
        return "Messaging Exception:\n"+(this.message == null ? "" : this.message);
    }
}
