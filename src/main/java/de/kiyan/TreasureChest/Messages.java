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
    TCGEST_ITEM_LIST( "§aList of all items available" ),
    TCHEST_CHANGE_TIER( "§aChange Tier of Items" ),
    TCHEST_CHEST_TYPE( "§6Select a type:"),
    EXIST( "§cThis TChest already exists!"),
    NON_EXIST( "§cThis §f{chest}§c TChest does not exists!"),
    TCHEST_SELECT_BEFORE( "§cYou must select a zone before!"),
    TOO_BIG( "§cYour selection is too big!" ),
    ALREADY_SELECTED( "§cYou already selected an area!" ),
    SELECT_POS1( "§cYou selected {POS1} " ),
    SELECT_POS2( "§cYou selected {POS2} " ),
    FORGOT_OPEN_NAME( "§cYou forgot to give an chestname §f§l/tchest open <chestname>"),
    FORGOT_GIVE_NAME( "§cYou forgot to give an chestname §f§l/tchest give <chestname>"),
    OPENING_TCHEST( "§a§lOpening TChest .."),
    BREAK_BLOCK ( "§cYou cant break TChest items!"),
    OWN_CHEST( "§cThis TChest does not belong to you!"),
    FULL_INVENTORY( "§c§lYour inventory is full§c, item dropped on the ground!"),
    CANT_DO( "§cYou can't move this item!"),
    PLAYER_NOT_EXIST( "§cThe player: §e§l{player}§c does not exist!"),
    BROADCAST( "§aPLAYER §e{player}§a ARE OPENING A TREASURE CHEST TYPE:§5 {type} §a!" ),
    ALREADY_RUNNING( "§cTChest is already running." ),
    OWN_WRONG_WORLD( "§cYou can only open treasure chests in §e{world}!" ),
    PLAYER_WRONG_WORLD( "§a{player} §cis not on world§e {world}§c!"),
    NEARBY_PLAYER( "§cThere is an player nearby!"),
    BLOCKS_HAVENT_SET( "§4You haven't setup blocks yet!§c You cant open treasure chest" ),
    GIVE_AN_CHESTTYPE( "§cGive an tier type §f/tchest give <chestname> <player> <§4RED§f|§bBLUE§f|§eYELLOW§f|§5PURPLE§f|§aGREEN§f>");

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
