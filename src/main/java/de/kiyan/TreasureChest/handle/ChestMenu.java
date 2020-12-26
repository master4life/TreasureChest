package de.kiyan.TreasureChest.handle;

import de.kiyan.TreasureChest.Config;
import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.Utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ChestMenu {
    public Menu setupMainMenu( Player player ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_GUI_MENU.getMessage(), 2 );

        ArrayList< String > lore = new ArrayList<>();
        lore.add( "" );
        lore.add( "  §f§lLEFT:§r§a Create new TChest" );
        lore.add( "" );
        lore.add( "  §f§lRIGHT:§r§e Edit current TChest" );

        menu.addMenuItem( new MenuItem( "§r§aCreate TChest", new ItemBuilder( Material.ANVIL ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    AnvilGUI GUI = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick( AnvilGUI.AnvilClickEvent event ) {

                            if( event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT
                                    && event.hasText() ) {   // TODO: Add a check, if name is available or not.

                                Config config = new Config();
                                String text = event.getText();
                                if( config.getExist( text ) )
                                {
                                    setupMainMenu( player ).openMenu( player );
                                    player.sendMessage( Messages.EXIST.getMessage( true ) );
                                } else {

                                    setupNewMenu( player, text ).openMenu( player );
                                    config.saveFile( event.getText() );
                                }
                            }
                        }
                    } );

                    ItemStack i = new ItemStack( Material.CHEST );
                    GUI.setSlot( AnvilGUI.AnvilSlot.INPUT_LEFT, i );
                    GUI.setSlotName( AnvilGUI.AnvilSlot.INPUT_LEFT, "§r" );
                    GUI.setTitle( "Type a new name" );

                    GUI.open();

                }
                if( clickType.isRightClick() ) {

                }
            }
        }, 2 );

        ArrayList< String > lore2 = new ArrayList<>();
        lore2.add( "" );
        lore2.add( "§8Display all saved TChests" );
        lore2.add( "§8all chests in '§rTCHests§8'" );

        menu.addMenuItem( new MenuItem( "§r§aList all TChests", new ItemBuilder( Material.BOOK ).setLore( lore2 ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupListMenu( player ).openMenu( player );
                }
            }
        }, 4 );

        return menu;
    }

    public Menu setupListMenu( Player player ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_LIST_FOLDER.getMessage( false ), 6 );
        ArrayList< String > config = new Config().listFiles();

        for( int i = 1; i <= config.size(); i++ ) {
            int j = i - 1;
            menu.addMenuItem( new MenuItem( config.get( j ), new ItemStack( Material.PAPER ) ) {
                @Override
                public void onClick( Player player, InventoryClickType clickType ) {
                    setupNewMenu( player, config.get( j ) ).openMenu( player );
                }
            }, j );
        }

        menu.addMenuItem( new MenuItem( "§eBack", new ItemStack( Material.ARROW ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupMainMenu( player ).openMenu( player );
                }
            }
        }, 53 );

        return menu;
    }

    public Menu setupNewMenu( Player player, String message ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_GUI_NEW.getMessage() + " " + message, 2 );

        menu.addMenuItem( new MenuItem( "§r§lName: §a§l" + message, new ItemStack( Material.CHEST ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {

            }
        }, 0 );

        ChestSelection chest = new ChestSelection( player );

        Location first = chest.getFirstLocation( player );
        Location second = chest.getSecondLocation( player );

        ArrayList< String > lore = new ArrayList<>();
        lore.add( "§a§lFIRST LOCATION" );
        lore.add( first == null ? "n/a" : " X: " + first.getBlockX() );
        lore.add( first == null ? "n/a" : " Y: " + first.getBlockY() );
        lore.add( first == null ? "n/a" : " Z: " + first.getBlockZ() );
        lore.add( "§a§lSECOND LOCATION" );
        lore.add( second == null ? "n/a" : " X: " + second.getBlockX() );
        lore.add( second == null ? "n/a" : " Y: " + second.getBlockY() );
        lore.add( second == null ? "n/a" : " Z: " + second.getBlockZ() );

        menu.addMenuItem( new MenuItem( "§b§lSelect a ZONE", new ItemBuilder( Material.WOODEN_AXE ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    boolean bWoodAxe = false;
                    for( ItemStack is : player.getInventory().getContents() )
                        if( is != null )
                            if( is.getType().equals( Material.WOODEN_AXE ) )
                                bWoodAxe = true;

                    if( !bWoodAxe )
                        player.getInventory().addItem( new ItemStack( Material.WOODEN_AXE ) );

                    player.sendMessage( Messages.TCHEST_SELECT_AREA.getMessage( true ) );

                    ChestSelection.EDIT_MODE.put( player, message );

                    player.closeInventory();
                }
            }
        }, 2 );

        menu.addMenuItem( new MenuItem( "§9SAVE" ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    ChestSelection chest = new ChestSelection( player );
                    Location loc1 = chest.getFirstLocation( player );
                    Location loc2 = chest.getSecondLocation( player );

                    ArrayList< Location > arrayLoc = new ArrayList<>();
                    arrayLoc.add( loc1 );
                    arrayLoc.add( loc2 );
                    new Config().setBlocks( message, chest.getSelectedBlocks( player ) );
                }
            }
        }, 9 );

        menu.addMenuItem( new MenuItem( "§4Delete", new ItemStack( Material.BARRIER ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    new Config().delete( message );
                    player.closeInventory();

                    player.sendMessage( Messages.TCHEST_DELETED.getMessage( true ) );
                }
            }
        }, 16 );

        menu.addMenuItem( new MenuItem( "§eBack", new ItemStack( Material.ARROW ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupMainMenu( player ).openMenu( player );
                }
            }
        }, 17 );

        return menu;
    }
}
