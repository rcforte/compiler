package message;

public class Message
{
    private final MessageType type;
    private final Object body;

    public Message(MessageType type, Object body)
    {
        this.type = type;
        this.body = body;
    }

    public MessageType messageType()
    {
        return type;
    }

    public Object getBody()
    {
        return body;
    }
}
