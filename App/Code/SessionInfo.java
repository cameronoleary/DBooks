public class SessionInfo
{
    private static String Id;
    private static boolean IsOwner;

    public static String GetId()
    {
        return Id;
    }

    public static void SetId(String id)
    {
        Id = id;
    }

    public static boolean IsOwner()
    {
        return IsOwner;
    }

    public static void SetOwner(boolean isOwner)
    {
        IsOwner = isOwner;
    }
}
