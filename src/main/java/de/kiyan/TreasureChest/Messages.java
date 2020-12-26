package de.kiyan.TreasureChest;

public enum Messages
{
    PREFIX ( "§c[§bTChest§c]" ),
    PLUGIN_LOAD( "§gPlugin has been activated!" ),
    NO_ACCESS( "§cYou dont have access for this command!"),
    NON_PLAYER( "You needs to be a player"),
    TCHEST_GUI_MENU( "§cTChest Mainmenu"),
    TCHEST_GUI_NEW( "§aNew TChest"),
    TCHEST_SELECT_AREA( "§eMake a selection of §c§lpos1§e and §c§lpos2§e are required!"),
    TCHEST_DELETED( "§cThis TChest has been removed!"),
    TCHEST_LIST_FOLDER( "§aList of all TChests" ),
    EXIST( "§cThis TChest exist already!"),
    TCHEST_SELECT_BEFORE( "§cYou must select a zone before!"),
    TOO_BIG( "§cYour selection is too big!" );

    String message;
    Messages( String message )
    {
        this.message = message;
    }

    public String getMessage( boolean prefix )
    {
        return prefix ? PREFIX.getMessage() + " " + getMessage() : getMessage();
    }

    public String getMessage()
    {
        return this.message;
    }
}
