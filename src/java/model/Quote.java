package model;

public class Quote extends MessageObject
{
    private User quoteAuthor = null;
    private Message quoteMessage = null;

    public User getQuoteAuthor() { return this.quoteAuthor; }
    public void setQuoteAuthor(User quoteAuthor) { this.quoteAuthor = quoteAuthor; }

    public Message getQuoteMessage() { return this.quoteMessage; }
    public void setQuoteMessage(Message quoteMessage) { this.quoteMessage = quoteMessage; }
}