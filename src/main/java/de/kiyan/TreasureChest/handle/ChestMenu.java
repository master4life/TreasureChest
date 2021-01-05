package de.kiyan.TreasureChest.handle;

import de.kiyan.TreasureChest.Config;
import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.Utils.*;
import de.kiyan.TreasureChest.api.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ChestMenu {
    /*


        The Main menu


    */

    public Menu setupMainMenu( Player player ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_GUI_MENU.getMessage(), 1 );

        ArrayList< String > lore = new ArrayList<>();
        lore.add( "" );
        lore.add( "  §f§lLEFT:§r§a Create new TChest" );
        lore.add( "" );
        lore.add( "  §f§lRIGHT:§r§e Edit current TChest" );
        lore.add( "" );

        menu.addMenuItem( new MenuItem( "§r§aCreate TChest", new ItemBuilder( Material.ANVIL ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                AnvilGUI GUI = null;
                Config config = new Config();

                if( clickType.isLeftClick() ) {
                    GUI = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick( AnvilGUI.AnvilClickEvent event ) {

                            if( event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && event.hasText() ) {
                                String text = event.getText();

                                if( config.getExist( text ) ) {
                                    setupMainMenu( player ).openMenu( player );
                                    player.sendMessage( Messages.NON_EXIST.getMessage( true ) );
                                } else {

                                    setupNewMenu( player, text ).openMenu( player );
                                    config.saveFile( event.getText() );
                                    config.setTierList( text );
                                }
                            }
                        }
                    } );
                }
                if( clickType.isRightClick() ) {
                    GUI = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick( AnvilGUI.AnvilClickEvent event ) {

                            if( event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && event.hasText() ) {
                                String text = event.getText();
                                if( config.getExist( text ) ) {
                                    setupNewMenu( player, event.getText() ).openMenu( player );
                                } else {

                                    setupMainMenu( player ).openMenu( player );
                                    player.sendMessage( Messages.EXIST.getMessage( true ) );
                                }

                            }
                        }
                    } );
                }
                ItemStack i = new ItemStack( Material.CHEST );
                GUI.setSlot( AnvilGUI.AnvilSlot.INPUT_LEFT, i );
                GUI.setSlotName( AnvilGUI.AnvilSlot.INPUT_LEFT, "§r" );
                GUI.setTitle( "Type a new name" );

                GUI.open();
            }
        }, 2 );

        ArrayList< String > lore2 = new ArrayList<>();
        lore2.add( "" );
        lore2.add( "§8Display all saved TChests" );
        lore2.add( "§8all chests in '§rTCHests§8'" );

        menu.addMenuItem( new MenuItem( "§r§aList all TChests", new ItemBuilder( Material.BOOKSHELF ).setLore( lore2 ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupListMenu().openMenu( player );
                }
            }
        }, 6 );

        return menu;
    }

    /*

            Fetch the items and setup


     */
    public Menu setupTierList( String name ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_CHANGE_TIER.getMessage( false ), 6 );
        ArrayList< ItemStack > config = new Config().getItems( name, true );

        for( int i = 1; i <= config.size(); i++ ) {
            int j = i - 1;
            menu.addMenuItem( new MenuItem( config.get( j ).getItemMeta().getDisplayName(), config.get( j ) ) {
                @Override
                public void onClick( Player player, InventoryClickType clickType ) {
                    if( clickType.isLeftClick() ) {
                        new Config().setTier( name, String.valueOf( j + 1 ), "§f§lcommon" );
                        setupTierList( name ).openMenu( player );
                    }

                    if( clickType.isRightClick() ) {
                        new Config().setTier( name, String.valueOf( j + 1 ), "§4§lrare" );
                        setupTierList( name ).openMenu( player );
                    }
                    if( clickType.isShiftClick() ) {
                        new Config().setTier( name, String.valueOf( j + 1 ), "§e§llegendary" );
                        setupTierList( name ).openMenu( player );
                    }
                }
            }, j );
        }

        ArrayList< String > lore = new ArrayList<>();
        lore.add( "" );
        lore.add( "§8Left Click to turn to §fCOMMON" );
        lore.add( "§8Right Click to turn to §4RARE" );
        lore.add( "§8SHIFT Click to turn to §eLEGENDARY" );

        menu.addMenuItem( new MenuItem( "§eGuide:", new ItemBuilder( Material.PAPER ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) { }


        }, 52 );

        menu.addMenuItem( new MenuItem( "§eBack", new ItemStack( Material.ARROW ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupNewMenu( player, name ).openMenu( player );
                }

            }
        }, 53 );

        return menu;
    }

    /*


        set chance for each tier
    */
    public Menu setupTierchance( String previous ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_LIST_FOLDER.getMessage( false ), 1 );

        ArrayList< String > lore = new ArrayList<>();
        String[] build = new Config().getTierChance( previous );
        lore.add( "" );
        lore.add( "§8§lThere is an chance of:" );
        lore.add( "  §8§l- §f§l" + build[ 0 ] + "§f§l%" );

        menu.addMenuItem( new MenuItem( "§f§lCOMMON", new ItemBuilder( Material.IRON_INGOT ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    AnvilGUI GUI = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick( AnvilGUI.AnvilClickEvent event ) {
                            if( event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && event.hasText() ) {
                                new Config().setTierChance( previous, "common", Float.parseFloat( event.getText() ) );
                                setupTierchance( previous ).openMenu( player );
                            }
                        }
                    } );

                    GUI.setSlot( AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemStack( Material.IRON_INGOT ) );
                    GUI.setSlotName( AnvilGUI.AnvilSlot.INPUT_LEFT, "§r" );
                    GUI.setTitle( "Type a new chance value" );

                    GUI.open();
                }
            }
        }, 2 );

        lore.clear();
        lore.add( "" );
        lore.add( "§8§lThere is an chance of:" );
        lore.add( "  §8§l- §f§l" + build[ 1 ] + "§f§l%" );

        menu.addMenuItem( new MenuItem( "§4§lRARE", new ItemBuilder( Material.GOLD_INGOT ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    AnvilGUI GUI = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick( AnvilGUI.AnvilClickEvent event ) {
                            if( event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && event.hasText() ) {
                                new Config().setTierChance( previous, "rare", Float.parseFloat( event.getText() ) );
                                setupTierchance( previous ).openMenu( player );
                            }
                        }
                    } );

                    GUI.setSlot( AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemStack( Material.GOLD_INGOT ) );
                    GUI.setSlotName( AnvilGUI.AnvilSlot.INPUT_LEFT, "§r" );
                    GUI.setTitle( "Type a new chance value" );

                    GUI.open();
                }
            }
        }, 4 );

        lore.clear();
        lore.add( "" );
        lore.add( "§8§lThere is an chance of:" );
        lore.add( "  §8§l- §f§l" + build[ 2 ] + "§f§l%" );

        menu.addMenuItem( new MenuItem( "§e§lLEGENDARY", new ItemBuilder( Material.DIAMOND ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    AnvilGUI GUI = new AnvilGUI( player, new AnvilGUI.AnvilClickEventHandler() {

                        @Override
                        public void onAnvilClick( AnvilGUI.AnvilClickEvent event ) {
                            if( event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT && event.hasText() ) {
                                new Config().setTierChance( previous, "legendary", Float.parseFloat( event.getText() ) );
                                setupTierchance( previous ).openMenu( player );
                            }

                        }
                    } );

                    GUI.setSlot( AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemStack( Material.DIAMOND ) );
                    GUI.setSlotName( AnvilGUI.AnvilSlot.INPUT_LEFT, "§r" );
                    GUI.setTitle( "Type a new chance value" );

                    GUI.open();
                }
            }
        }, 6 );

        menu.addMenuItem( new MenuItem( "§eBack", new ItemStack( Material.ARROW ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupNewMenu( player, previous ).openMenu( player );
                }
            }
        }, 8 );

        return menu;
    }


    /*


            This menu lists all available treasurechests


     */
    public Menu setupListMenu() {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_LIST_FOLDER.getMessage( false ), 6 );
        ArrayList< String > config = new Config().listFiles();

        for( int i = 1; i <= config.size(); i++ ) {
            int j = i - 1;
            menu.addMenuItem( new MenuItem( "§a§l" + config.get( j ), new ItemStack( Material.PAPER ) ) {
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

    /*


            This menu collects all items and displays them from TreasureChest


     */
    public Menu setupItemMenu( String previous ) {

        Menu menu = new MenuAPI().createMenu( Messages.TCGEST_ITEM_LIST.getMessage( false ), 6 );
        menu.setallowMenuModify( true );
        ArrayList< ItemStack > config = new Config().getItems( previous );

        for( int i = 1; i <= config.size(); i++ ) {
            int j = i - 1;

            menu.addMenuItem( new MenuItem( config.get( j ).getItemMeta().getDisplayName(), config.get( j ) ) {
                public void onClick( Player player, InventoryClickType clickType ) {
                }
            }, j );
        }

        ArrayList< String > lore = new ArrayList<>();
        lore.add( "" );
        lore.add( "§8Just drop items inside the inventory shown" );
        lore.add( "§8Those following items will be dropped" );
        lore.add( "§8During the TChest event" );

        menu.addMenuItem( new MenuItem( "§eGuide:", new ItemBuilder( Material.PAPER ).setLore( lore ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() || clickType.isRightClick() || clickType.isShiftClick() ) {
                    player.sendMessage( Messages.CANT_DO.getMessage( true ) );
                }
            }
        }, 52 );

        menu.addMenuItem( new MenuItem( "§eSAVE", new ItemStack( Material.DIAMOND ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    ArrayList< ItemStack > Items = new ArrayList<>();
                    for( ItemStack is : menu.getInventory().getContents() )
                        if( is != null
                                && !is.getItemMeta().getDisplayName().equalsIgnoreCase( "§eBack" )
                                && !is.getItemMeta().getDisplayName().equalsIgnoreCase( "§eSAVE" )
                                && !is.getItemMeta().getDisplayName().equalsIgnoreCase( "§eGuide:" ) ) {
                            Items.add( is );
                        }

                    new Config().setItems( previous, Items );
                    setupNewMenu( player, previous ).openMenu( player );
                }
            }
        }, 53 );

        return menu;
    }

    /*
            This menu creates selection of TChest type
     */
    public Menu setupTChestType( String message) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_CHEST_TYPE.getMessage( false ), 1 );

        Config config = new Config();
        ArrayList< String > lore = new ArrayList<>();
        lore.add( "" );
        lore.add( "  §f<RIGHT CLICK TO OPEN>  " );
        lore.add( "  §7TYPE: §5§l" + message );
        lore.add( "  §7Contains: §f§l" + config.getItems( message ).size() + " §7Items.  " );
        lore.add( "  §7Drop chance: " );
        lore.add( "      §f§lCOMMON§7: §f" + config.getTierChance( message )[ 0 ] + "%  " );
        lore.add( "      §4§lRARE§7: §f" + config.getTierChance( message )[ 1 ] + "%  " );
        lore.add( "      §e§lLEGENDARY§7: §f" + config.getTierChance( message )[ 2 ] + "%  " );
        lore.add( "" );
        lore.add( "   §8[Be sure no player is nearby.]" );

        String uuid = "efbceecd-2043-4c17-b882-b20fc849ecdf";
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTU5NjM1NDkyOTk1NiwKICAicHJvZmlsZUlkIiA6ICJkNjBmMzQ3MzZhMTI0N2EyOWI4MmNjNzE1YjAwNDhkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCSl9EYW5pZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzYxYTcxN2NlNjcyNGU5NTY3NWI1NTQyODc2ZTg1MDNhOTgwOWFmZDY3ZWFlMTJkN2NmMTFkOTU5YzRjMTc2IgogICAgfQogIH0KfQ==";
        String signature = "ci59wy7DGGMnFTjQ5FUsHTdpWz3d7giIHig8e0q6EsgpYJr4bp+DfmsEmoipgK233HtGZjAbfg+T4O4Ty32y+YkNzfB0kw5RHDrFC0vnyyQC7ONKVZS9E5I1z4AH4LM0v3tqCQCRHPYCoRX9CCm3x0CYhVZB4WJAg6G4N4YL6I9Cm55OGwJulTkExxP+R+wp6nNqL73B/A3O++xKOwXJLYgCmzTdYoofgO64r5eCPMF+w/XRDkGdNSt5nBtDn2OObK4LkYVl2iobwbNYRwKMfxRYVZ7xMdxPSe0lmo38Wsi/8an25BWvFzVVu4uM8OQL1ZWPVe6KHeWwwbVpsfI73XEU9f+1eZMxCCLcBtdIaDX5YB0d0eszaZ8RDbl/KK0NmlrCx5bzrcNx/nId3PquWDnMPjm0q3l2/mQakaKvlW4WNCn4X1Fj4NH5tCquy45uNG1NgI4C1xQyGZ6tS+QZpfs8DROvFRWJ3fa38IAKP7Y8xTMrhJyo4EZhmv+dTcjrWeuvtTDW2v8Ta9xxfrOCZQgo31p2Xde7zx8sQcCQkSG+Q19qx2XGMRCnC3hMgF/POIyBkqgkNOc1++6VHozq1dh0fCR1BQ36OkzEBgIi/o7F7i7Zy7rO24CNkt5ey884DMesZjAHfaFJhtpIZHBX4i59gL616lqyaPhdNWKVlC8=";

        ItemStack RED = new ItemBuilder( Material.PLAYER_HEAD )
                .setDisplayName( "§6§lTreasure Chest: §5§l" + message )
                .setLore( lore )
                .setProfileHeader( uuid, texture, signature )
                .build();

        menu.addMenuItem( new MenuItem( "§4§lRED", RED ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType )
            {
                if( clickType.isLeftClick())
                {
                    player.getInventory().addItem( RED );

                    player.closeInventory();
                }

            }
        }, 1 );

        uuid = "e036376d-1bc8-4fa4-bc9a-846efb19965a";
        texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDYwMjgwNSwKICAicHJvZmlsZUlkIiA6ICI5MWYwNGZlOTBmMzY0M2I1OGYyMGUzMzc1Zjg2ZDM5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdG9ybVN0b3JteSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81M2VhOWI4NzM0MzlmMzFmNmI5ZmE1ZDhiYmZiMmY2NThkYWI2OThmNTEyYzU4YzE4YjliNzA0OGIyYjE2NmRmIgogICAgfQogIH0KfQ==";
        signature = "QWhvHH6GpcmOnyktViJNDYKNU1jmls8QlWMzw3uhJsVyoZBdrwcfnbQ0Y69Fd63XXHFOZVFMaj9nda3IIRWgiprE2ud7dISMACAazY+D46Gi0TZpQlnocYTd8yjzKXYOCqNhdKsZt5iwg+1G8J0g7zv3rhwJMyD+ah7g3+i255LOuSia+LxMeBZMi2mS24jg9SR8ji6ufzYmVy6fpLUNSAbxnfXfgEctma0A3fqmbhXTAM8lrj0cQQXlWlZdkeha58RVWHeYv/aCTdjT9nK/elNm5z4yKRedOWUgHpLTo2EEvoc3KgVCR5W27Snmr7Rl1RxT3RjVGqpB8FbkDaCqRsKQy4B7tYPVlZ7An3a9Z6Gj/z1YsvRXJ5R3UVvKgEed4Zh20g6xiKCy1nPlXKBNbigcmQqo2PX6YuFjYwt4Yb+1U+7EuaPrC8vB4sIq1HH34O046MjEbs9od+8GXiyGGBinrEbWYOt9jm9lir8/NbML3xWPVGyHY2rMcd7ABk0AGjWz+yvZbOFJT2qhhoG8hx/LJdoCLu0yaYdcWZMa43qXvz/nOLJnbHSHfbvg0myIu/dPR8GQWgLJYeX4nIfdEwPNzXhEceiu7T7/6wullYSzDm2Fm59m7x67kascNGTgfxC8ZF4ksoPn+0EaLHe5Yqx5PEtRfZvpBcccD5jhmGc=";

        ItemStack BLUE = new ItemBuilder( Material.PLAYER_HEAD )
                .setDisplayName( "§6§lTreasure Chest: §5§l" + message )
                .setLore( lore )
                .setProfileHeader( uuid, texture, signature )
                .build();

        menu.addMenuItem( new MenuItem( "§b§lBLUE", BLUE ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType )
            {
                if( clickType.isLeftClick())
                {
                    player.getInventory().addItem( BLUE );

                    player.closeInventory();
                }

            }
        }, 2 );

        uuid = "7585850e-88b8-4b4d-bcaa-1ac3d5e7ddf0";
        texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDgxMjQ0NywKICAicHJvZmlsZUlkIiA6ICI1NjY3NWIyMjMyZjA0ZWUwODkxNzllOWM5MjA2Y2ZlOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVJbmRyYSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MGY3NjZlZmQ2OTRmNDMwMjBkZGIzYjE1Yzk3MjQ5MjNkYTIwN2Y5ZjFjODUwYTE4MTQzMTVhOWJjODMyMDMzIgogICAgfQogIH0KfQ==";
        signature = "xvy31zLNHo81wxOzhenAD6TPP2fIVj4bAbuWvXEuSnVJzNLY8LxQSpW4V2U+dQbzMaQqcc053vfdwj3SaK1Z4JRfJBr35qo22BPhTf69htlg8vhK31nYQxyM+Oah2AT36wh4TOdp0kTRv/qDqgbQgturKlDRyhd4eN0nKS9oorF6LAn7dRNEpFhiXmPBhCxF9Re2ftR8WpYcNyKx4px5b7gevais5UJ+FT2cklZGDaDmDhT4KG/Ry3FVhyGYr1mJOfPER3I4x0vLYRVpvCixL5gHr1+JC6jghfwyENQgE0S6RuYGRDJcFO4POJjdSK5LT4wRdnpRB/oaf74EJCMnX3zSaf+oUAw3jHuzRf1ILTuIjwu9T1peM2i9BsD4dCKcklDRCZoB1MahcVjsdrHdcLsWMxLhpX8dj8VssJCOxn85olrb7KqHwhcKcL6wt5Q1vqPSKPnZO52A+z4cEXNjMr1FNl/TjcsEHwAlcqR4sPEQeYov3PbUVXmDxYN1reVjCbJe5fRuH2DJzOrQZjvfIq+gNEgc2JDZKwO38OSzhC0OYd8a4azEpcZaohb/CaOiNF89QFYqpA3Cq4TNYW+95awbFoWbPqgx3MIK4GYMUldkEUt0l+F1TJWBYPm2m+zIY7BSEP5tykzuArkD5L3Ct4ip6gGuiEj/QSbt/tguH/8=";

        ItemStack YELLOW = new ItemBuilder( Material.PLAYER_HEAD )
                .setDisplayName( "§6§lTreasure Chest: §5§l" + message )
                .setLore( lore )
                .setProfileHeader( uuid, texture, signature )
                .build();

        menu.addMenuItem( new MenuItem( "§6§lYellow", YELLOW ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType )
            {
                if( clickType.isLeftClick())
                {
                    player.getInventory().addItem( YELLOW );

                    player.closeInventory();
                }

            }
        }, 3 );

        uuid = "ed394ff4-7878-4c15-8bd9-3cb080d601f8";
        texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDg5NDYzMSwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JhZjYyM2I0YzMwZTdkYzE0M2ZmNjkwZDhmZWVhOWUxNjVjMGU1OWJhM2FmYjI1ZDZkYzJlZjBiMTcwOGZmMSIKICAgIH0KICB9Cn0=";
        signature = "b/vGZqxiuHxXyP0y/4ovqY/nS4A7iz21B0flu9PKORprChvDGf30021f4rFqtHRx1QB0THd8wQn4C5at6zFjNkgwtnpB39XnaDXF3PWLRrvnUhJLca4LCOCAWQqvvXXvqRpxp3Fzq+sdzhf2yj+fOemqe2mi1MKnPN5XX4u0GZFACCrN57rXaJb3Yqr2Egeh3VmA7yUABObJEoA+zXOac7GTNZNOq5P8HXFkoQoWRmGbKTiwaF6wRFd3YfGtErCaK3ejjJbvyz5mTn/DM663Rm0+l/Orfzvrj6g+aCXhNYXUHhJfMafkS9NiVgQE1rC3CNe3yQCc4kd2c8kxyrb0UyVAOSpSF2jmcR+NcVXVeHJO0sKIkSB37A1BtgqbMPeLZ8l6u3DTGKGH0bmH/dzEl7sDpYYpgU44cWSr7pDxZL/czYmYzgNIf4oMcWb8wL8LEFVR/i3FArP+Niq6mEsppzH8TJ7mFFlRI7b0c0tY89gOVESx3cEktfWydIaPZC2c7GosdU7fNUfEbU5zkYsVStYRtfOHvaekFQuKU0T2vvTJ0xcLtWwSDQtdH5RQtCTQTESHtg/DAecj4X7f7P+02JCYVJXswVVX+qTocjPLiRBYRf1bGDWJWt1gF0t0hDhCicaYfqw68+pbPNphgzWq8cQXPES62/XLuWr8dLXORp0=";

        ItemStack PURPLE = new ItemBuilder( Material.PLAYER_HEAD )
                .setDisplayName( "§6§lTreasure Chest: §5§l" + message )
                .setLore( lore )
                .setProfileHeader( uuid, texture, signature )
                .build();

        menu.addMenuItem( new MenuItem( "§5§lPurple", PURPLE ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType )
            {
                if( clickType.isLeftClick())
                {
                    player.getInventory().addItem( PURPLE );

                    player.closeInventory();
                }

            }
        }, 4 );

        uuid = "61f55db0-1a0f-4bdf-b2f0-2e85c78ebd29";
        texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDUwOTM4MCwKICAicHJvZmlsZUlkIiA6ICI5MzZmMTA3MTEzOGM0YjMyYTg0OGY2NmE5Nzc2NDJhMiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwMDAwMDAwMDAwMDAwMDB4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0MDQwMjAyODkyODk1MGE1ZjMyNjI4OGUwNTY1OWEzNGY3OTkxN2UxZjY3ZjA1ZjMzNjk2MzQ2NDY0NjFhNjYiCiAgICB9CiAgfQp9";
        signature = "mxnhquFcn93VVkKsHHl/TfbxvjtPoQGQhWsJXYN5dHrGroJ0xgeD75t3198+mR3WHiwjT4N8xhXS2rW7cAErLhsKsBsBeCCbX3oGanQ3Z4TnbRYxQpuxs/l3psqUwT81i8bilw0AorujPvpJnAFEq0SaRhJibNjXD3LtM/uajrTDwr6eSzg6ryeKFFBKwrulRutaJQ1OyZc6Di+bTVhhrBUpEbPZBDiDT1uwjCWJZqVluDIH+TVL/3MqzmCQfT00n5kO9VSJrwE81d9BeNv9m797EhfL19STFHPYSFch37Eg7kg0a3Mj/55AZyM1ksZtDBxwAL68DNaZQpa9ai+guNsOUQ1Yg7iEBSwjZ2OLaQIUnaNXrNruKWmoMLHbhC+/O198re6caTHQk8Lt0ERlhQw1iTfc4VMsSfYZ7W+sNHNR2NVw8PtB/3u9GOwcENYXIFEQIDZ7MbML+K1RV2r715R0VagoZMllQ1NZwOUSCjVs+MIcuQMfy3znsaOpFRuybT1akcWdNHwQYYnhwEsETN84c0ka3mCKuqdCDS5sLLGP/Yxkrlrxc09FXfk3MH8iMb7xRHEo48GsILjdqsWTS5CyJq8eo6qohkFSoXlnXN6TM1CQyTMvrEm+rsQTQUn0cTHdNsU+f1i0JuEAU3qOyjmv4/sKs2KZfnAG8LJUJSA=";

        ItemStack GREEN = new ItemBuilder( Material.PLAYER_HEAD )
                .setDisplayName( "§6§lTreasure Chest: §5§l" + message )
                .setLore( lore )
                .setProfileHeader( uuid, texture, signature )
                .build();

        menu.addMenuItem( new MenuItem( "§a§lGreen", GREEN ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType )
            {
                if( clickType.isLeftClick())
                {
                    player.getInventory().addItem( GREEN );

                    player.closeInventory();
                }

            }
        }, 5 );

        uuid = "5ff05f85-6a5f-404d-b79f-89568de788b7";
        texture = "ewogICJ0aW1lc3RhbXAiIDogMTU5NTMwMzE1NzgyMSwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4YzRmMjljM2RlMjdkM2ExODhlZTRjYzU3ZjNmY2EzYmRkMWFmZWE3NjhlZGExMmU3ZDdkOWI3ZDcyNzliYSIKICAgIH0KICB9Cn0=";
        signature = "R1atfblbQrreZXgo2yvf4zBzgxrG0Oaodo07uGHrmAWkD12MKFotMFIg9YUIyE4fEwB//k5aWUqcB+9FhUW+/Z7iBCo+gCBDKbnkFR9xcHCZJZ+FJMx1ZL6v2JGNtgNW993eXHTipgsbLqFQwgYXevfFutEX0WdfXGTqD4ShqWp8WfDXnIBGq/T5/uFJP4yRmXRf0+eN683NSGdcWIb3QfgGQKAiDMphefGqaKo/nH2XP2/c53pTxHkwGi00t6lZ0CvGoME0RqnO1fAnSEi64tSe+HJjDKF19z2wFfHCnEM6VzA8ICJ8/NCBk9kN18iysN8U237g+9TOUwh23q/vbELSfnGYdjabDMyWrevVDHU2xhMsvxMpoCPRCQ1IQoAffI0urzM3+5IiZUex8kWN92b/zDH+hFNyUK7SNcmTv0d2oq0hqrvnWRsuE+I88zxl9o+0TCMH32vuT1pS9dWZOiXS6LHgt1WSxwm5jRS40rwfnk8I5duqP+ubpwsq7D01X0+P5s5l2cKrdF1oH1HiVJ2KT1rPnilT++vIpW432ShWDwLhRtsiq0wXzRDbgkejx07ZW3EXRKJOqJFR6nplFBeTOeuwXMfasNjT/qHOYiLdgM6X0ryIDmh9L4xx+7d0wNRZ3c5AuQraoeX9ksZXk23jjYegDEF2Ax3YSMq/c3Y=";

        ItemStack DEFAULT = new ItemBuilder( Material.PLAYER_HEAD )
                .setDisplayName( "§6§lTreasure Chest: §5§l" + message )
                .setLore( lore )
                .setProfileHeader( uuid, texture, signature )
                .build();

        menu.addMenuItem( new MenuItem( "§6§lDefault", DEFAULT ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType )
            {
                if( clickType.isLeftClick())
                {
                    player.getInventory().addItem( DEFAULT );
                }

            }
        }, 6 );

        menu.addMenuItem( new MenuItem( "§eBack", new ItemStack( Material.ARROW ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupNewMenu( player, message ).openMenu( player );
                }
            }
        }, 8 );

        return menu;
    }

     /*


            This creates a new menu


     */

    public Menu setupNewMenu( Player player, String message ) {
        Menu menu = new MenuAPI().createMenu( Messages.TCHEST_GUI_NEW.getMessage() + " §f§l" + message, 2 );

        menu.addMenuItem( new MenuItem( "§r§lName: §c§l" + message, new ItemStack( Material.CHEST ) ) {
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
        lore.add( "§f§lRIGHT CLICK FOR RESET" );

        MenuItem mItem = null;
        mItem = new MenuItem( "§bSelect a ZONE", new ItemStack( Material.WOODEN_AXE ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    if( ChestSelection.PLAYER_SELECTION.get( player ).get( 0 ) != null ) {
                        player.sendMessage( Messages.ALREADY_SELECTED.getMessage( true ) );

                        return;
                    }

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

                if( clickType.isRightClick() ) {
                    new Config().removeBlocks( message );
                    if( ChestSelection.EDIT_MODE.containsKey( player ) )
                        ChestSelection.EDIT_MODE.remove( player );

                    if( ChestSelection.PLAYER_SELECTION.containsKey( player ) ) {
                        ArrayList< Location > loc = new ArrayList<>();
                        loc.add( null );
                        loc.add( null );
                        ChestSelection.PLAYER_SELECTION.remove( player );
                        ChestSelection.PLAYER_SELECTION.put( player, loc );
                    }

                    setupNewMenu( player, message ).openMenu( player );
                }
            }
        };
        mItem.setDescriptions( lore );

        menu.addMenuItem( mItem, 2 );

        menu.addMenuItem( new MenuItem( "§9Dropping Items", new ItemStack( Material.BUCKET ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupItemMenu( message ).openMenu( player );
                }
            }
        }, 3 );

        menu.addMenuItem( new MenuItem( "§eChange Tier", new ItemStack( Material.GOLD_INGOT ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupTierList( message ).openMenu( player );
                }
            }
        }, 4 );

        menu.addMenuItem( new MenuItem( "§eT§9i§ee§9r §9c§eh§9a§en§9c§ee", new ItemStack( Material.DIAMOND ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    setupTierchance( message ).openMenu( player );
                }
            }
        }, 5 );

        menu.addMenuItem( new MenuItem( "§6Get TChest", new ItemBuilder( Material.PLAYER_HEAD ).setProfileHeader( "5ff05f85-6a5f-404d-b79f-89568de788b7", "ewogICJ0aW1lc3RhbXAiIDogMTU5NTMwMzE1NzgyMSwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4YzRmMjljM2RlMjdkM2ExODhlZTRjYzU3ZjNmY2EzYmRkMWFmZWE3NjhlZGExMmU3ZDdkOWI3ZDcyNzliYSIKICAgIH0KICB9Cn0=", "R1atfblbQrreZXgo2yvf4zBzgxrG0Oaodo07uGHrmAWkD12MKFotMFIg9YUIyE4fEwB//k5aWUqcB+9FhUW+/Z7iBCo+gCBDKbnkFR9xcHCZJZ+FJMx1ZL6v2JGNtgNW993eXHTipgsbLqFQwgYXevfFutEX0WdfXGTqD4ShqWp8WfDXnIBGq/T5/uFJP4yRmXRf0+eN683NSGdcWIb3QfgGQKAiDMphefGqaKo/nH2XP2/c53pTxHkwGi00t6lZ0CvGoME0RqnO1fAnSEi64tSe+HJjDKF19z2wFfHCnEM6VzA8ICJ8/NCBk9kN18iysN8U237g+9TOUwh23q/vbELSfnGYdjabDMyWrevVDHU2xhMsvxMpoCPRCQ1IQoAffI0urzM3+5IiZUex8kWN92b/zDH+hFNyUK7SNcmTv0d2oq0hqrvnWRsuE+I88zxl9o+0TCMH32vuT1pS9dWZOiXS6LHgt1WSxwm5jRS40rwfnk8I5duqP+ubpwsq7D01X0+P5s5l2cKrdF1oH1HiVJ2KT1rPnilT++vIpW432ShWDwLhRtsiq0wXzRDbgkejx07ZW3EXRKJOqJFR6nplFBeTOeuwXMfasNjT/qHOYiLdgM6X0ryIDmh9L4xx+7d0wNRZ3c5AuQraoeX9ksZXk23jjYegDEF2Ax3YSMq/c3Y=" ).build() ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() )
                {
                    setupTChestType( message).openMenu( player );
                }
            }
        }, 6 );

        menu.addMenuItem( new MenuItem( "§4Delete", new ItemStack( Material.BARRIER ) ) {
            @Override
            public void onClick( Player player, InventoryClickType clickType ) {
                if( clickType.isLeftClick() ) {
                    new Config().delete( message );

                    player.sendMessage( Messages.TCHEST_DELETED.getMessage( true ) );

                    setupMainMenu( player ).openMenu( player );
                }
            }
        }, 8 );

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
