package frontend;

public class EofToken
    extends Token
{
    public EofToken(Source source)
        throws Exception
    {
        super(source);
    }

    @Override
    protected void extract()
        throws Exception
    {
    }
}
