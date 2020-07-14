package model;

public class MessageObject
{
    protected int messageObjectId;
    protected String value;

    protected Message message = null;

    public int getMessageObjectId() { return this.messageObjectId; }
    public void setMessageObjectId(int messageObjectId) { this.messageObjectId = messageObjectId; }

    public String getValue() { return this.value; }
    public void setValue(String value) { this.value = value; }

    public Message getMessage() { return this.message; }
    public void setMessage(Message message) { this.message = message; }
}