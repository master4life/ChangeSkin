package de.kiyan.ChangeSkin.Util;

public enum OverlayType
{
    PRISONER (0),
    GUARD (1),
    ADMIN (2);

    private int type;

    private OverlayType( int type)
    {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
