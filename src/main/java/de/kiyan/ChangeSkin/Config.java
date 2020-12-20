package de.kiyan.ChangeSkin;

public class Config
{
    Main instance;

    private String prisoner;
    private String guard;

    public Config()
    {
        instance = Main.getInstance();
    }

    public void AssignVar( )
    {
        prisoner = instance.getConfig().getString( "prisonerOverlay");
        guard = instance.getConfig().getString( "guardOverlay");
    }

    public String getPrisoner( )
    {
        return prisoner;
    }
    public String getGuard( )
    {
        return guard;
    }

}
