package de.kiyan.ChangeSkin;

public class Config
{
    Main instance =  null;

    private String overlay;

    public Config()
    {
        instance = Main.getInstance();
    }

    public void AssignVar( )
    {
        overlay = instance.getConfig().getString( "OverlayURL");
    }

    public String getOverlay()
    {
        return overlay;
    }

}
