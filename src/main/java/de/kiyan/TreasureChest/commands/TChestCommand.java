package de.kiyan.TreasureChest.commands;

import de.kiyan.TreasureChest.Config;
import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.TChest;
import de.kiyan.TreasureChest.Utils.ItemBuilder;
import de.kiyan.TreasureChest.handle.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if( !( sender instanceof Player ) ) {
            sender.sendMessage( Messages.NON_PLAYER.getMessage() );
            return false;
        }

        Player player = ( Player ) sender;

        // Being sure no other commands is being used.
        if( !label.equalsIgnoreCase( "tchest" ) ) {
            return true;
        }

        // placeholder for geppi's rank systemapi
        if( player.getGameMode() != GameMode.CREATIVE ) {
            player.sendMessage( Messages.NO_ACCESS.getMessage( true ) );

            return true;
        }
        if( args.length == 0 ) {
            new ChestMenu().setupMainMenu( player ).openMenu( player );
            return true;
        } else {
            if( args[ 0 ].equalsIgnoreCase( "open" ) ) {
                if( args.length > 1 ) {
                    for( Entity ent : player.getLocation().getWorld().getNearbyEntities( player.getLocation(), 5, 5, 5 ) ) {
                        if( ent instanceof Player ) {
                            if( ent != player ) {
                                player.sendMessage( Messages.NEARBY_PLAYER.getMessage( true ) );

                                return false;
                            }
                        }
                    }

                    String filename = "";
                    for( String list : new Config().listFiles( true ) )
                        if( args[ 1 ].equalsIgnoreCase( list ) )
                            filename = list;

                    World world = new Config().getDesignatedWorld();
                    if( args[ 1 ].equalsIgnoreCase( filename ) ) {
                        if( args.length > 2 ) {
                            Player target = Bukkit.getPlayer( args[ 2 ] );

                            if( target == null ) {
                                player.sendMessage( Messages.PLAYER_NOT_EXIST.getMessage( true ).replace( "{player}", args[ 2 ] ) );

                                return false;
                            }

                            if( world == null && target.getWorld().getName().equalsIgnoreCase( world.getName() ) ) {
                                player.sendMessage( Messages.PLAYER_WRONG_WORLD.getMessage( true )
                                        .replace( "{player}", target.getName() )
                                        .replace( "{world}", world.getName() ) );
                                return false;
                            }

                            if( TChest.blockedPlayers == null || !TChest.blockedPlayers.containsKey( target ) ) {
                                new TChest( target, args[ 1 ] );
                                return true;
                            } else {
                                player.sendMessage( Messages.ALREADY_RUNNING.getMessage() );
                                return false;
                            }
                        }

                        if( world == null && player.getWorld().getName().equalsIgnoreCase( world.getName() ) ) {
                            player.sendMessage( Messages.OWN_WRONG_WORLD.getMessage( true )
                                    .replace( "{world}", world.getName() ) );
                            return false;
                        }
                        if( TChest.blockedPlayers == null || !TChest.blockedPlayers.containsKey( player ) ) {

                            new TChest( player, args[ 1 ] );
                            return true;
                        } else {
                            player.sendMessage( Messages.ALREADY_RUNNING.getMessage() );
                            return false;
                        }
                    } else {
                        player.sendMessage( Messages.NON_EXIST.getMessage( true ).replace( "{chest}", args[ 1 ] ) );

                        return false;
                    }
                }
                player.sendMessage( Messages.FORGOT_OPEN_NAME.getMessage( true ) );

                return false;
            } else if( args[ 0 ].equalsIgnoreCase( "give" ) ) {
                if( args.length > 1 ) {
                    String filename = "";
                    for( String list : new Config().listFiles( true ) )
                        if( args[ 1 ].equalsIgnoreCase( list ) )
                            filename = list;

                    if( args[ 1 ].equalsIgnoreCase( filename ) ) {
                        if( args.length > 2 ) {
                            Player target = Bukkit.getPlayer( args[ 2 ] );

                            if( target == null ) {
                                player.sendMessage( Messages.PLAYER_NOT_EXIST.getMessage( true ).replace( "{player}", args[ 2 ] ) );

                                return false;
                            }

                            Config config = new Config();

                            ArrayList< String > lore = new ArrayList<>();
                            lore.add( "" );
                            lore.add( "  §f<RIGHT CLICK TO OPEN>  " );
                            lore.add( "  §7TYPE: §5§l" + filename );
                            lore.add( "  §7Contains: §f§l" + config.getItems( filename ).size() + " §7Items.  " );
                            lore.add( "  §7Drop chance: " );
                            lore.add( "      §f§lCOMMON§7: §f" + config.getTierChance( filename )[ 0 ] + "%  " );
                            lore.add( "      §4§lRARE§7: §f" + config.getTierChance( filename )[ 1 ] + "%  " );
                            lore.add( "      §e§lLEGENDARY§7: §f" + config.getTierChance( filename )[ 2 ] + "%  " );
                            lore.add( "" );
                            lore.add( "   §8[Be sure no player is near you.]" );

                            String uuid, texture, signature;

                            if( args.length > 3 ) {
                                if( args[ 3 ].equalsIgnoreCase( "red" ) ) {
                                    uuid = "efbceecd-2043-4c17-b882-b20fc849ecdf";
                                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTU5NjM1NDkyOTk1NiwKICAicHJvZmlsZUlkIiA6ICJkNjBmMzQ3MzZhMTI0N2EyOWI4MmNjNzE1YjAwNDhkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCSl9EYW5pZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzYxYTcxN2NlNjcyNGU5NTY3NWI1NTQyODc2ZTg1MDNhOTgwOWFmZDY3ZWFlMTJkN2NmMTFkOTU5YzRjMTc2IgogICAgfQogIH0KfQ==";
                                    signature = "ci59wy7DGGMnFTjQ5FUsHTdpWz3d7giIHig8e0q6EsgpYJr4bp+DfmsEmoipgK233HtGZjAbfg+T4O4Ty32y+YkNzfB0kw5RHDrFC0vnyyQC7ONKVZS9E5I1z4AH4LM0v3tqCQCRHPYCoRX9CCm3x0CYhVZB4WJAg6G4N4YL6I9Cm55OGwJulTkExxP+R+wp6nNqL73B/A3O++xKOwXJLYgCmzTdYoofgO64r5eCPMF+w/XRDkGdNSt5nBtDn2OObK4LkYVl2iobwbNYRwKMfxRYVZ7xMdxPSe0lmo38Wsi/8an25BWvFzVVu4uM8OQL1ZWPVe6KHeWwwbVpsfI73XEU9f+1eZMxCCLcBtdIaDX5YB0d0eszaZ8RDbl/KK0NmlrCx5bzrcNx/nId3PquWDnMPjm0q3l2/mQakaKvlW4WNCn4X1Fj4NH5tCquy45uNG1NgI4C1xQyGZ6tS+QZpfs8DROvFRWJ3fa38IAKP7Y8xTMrhJyo4EZhmv+dTcjrWeuvtTDW2v8Ta9xxfrOCZQgo31p2Xde7zx8sQcCQkSG+Q19qx2XGMRCnC3hMgF/POIyBkqgkNOc1++6VHozq1dh0fCR1BQ36OkzEBgIi/o7F7i7Zy7rO24CNkt5ey884DMesZjAHfaFJhtpIZHBX4i59gL616lqyaPhdNWKVlC8=";
                                } else if( args[ 3 ].equalsIgnoreCase( "blue" ) ) {
                                    uuid = "e036376d-1bc8-4fa4-bc9a-846efb19965a";
                                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDYwMjgwNSwKICAicHJvZmlsZUlkIiA6ICI5MWYwNGZlOTBmMzY0M2I1OGYyMGUzMzc1Zjg2ZDM5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdG9ybVN0b3JteSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81M2VhOWI4NzM0MzlmMzFmNmI5ZmE1ZDhiYmZiMmY2NThkYWI2OThmNTEyYzU4YzE4YjliNzA0OGIyYjE2NmRmIgogICAgfQogIH0KfQ==";
                                    signature = "QWhvHH6GpcmOnyktViJNDYKNU1jmls8QlWMzw3uhJsVyoZBdrwcfnbQ0Y69Fd63XXHFOZVFMaj9nda3IIRWgiprE2ud7dISMACAazY+D46Gi0TZpQlnocYTd8yjzKXYOCqNhdKsZt5iwg+1G8J0g7zv3rhwJMyD+ah7g3+i255LOuSia+LxMeBZMi2mS24jg9SR8ji6ufzYmVy6fpLUNSAbxnfXfgEctma0A3fqmbhXTAM8lrj0cQQXlWlZdkeha58RVWHeYv/aCTdjT9nK/elNm5z4yKRedOWUgHpLTo2EEvoc3KgVCR5W27Snmr7Rl1RxT3RjVGqpB8FbkDaCqRsKQy4B7tYPVlZ7An3a9Z6Gj/z1YsvRXJ5R3UVvKgEed4Zh20g6xiKCy1nPlXKBNbigcmQqo2PX6YuFjYwt4Yb+1U+7EuaPrC8vB4sIq1HH34O046MjEbs9od+8GXiyGGBinrEbWYOt9jm9lir8/NbML3xWPVGyHY2rMcd7ABk0AGjWz+yvZbOFJT2qhhoG8hx/LJdoCLu0yaYdcWZMa43qXvz/nOLJnbHSHfbvg0myIu/dPR8GQWgLJYeX4nIfdEwPNzXhEceiu7T7/6wullYSzDm2Fm59m7x67kascNGTgfxC8ZF4ksoPn+0EaLHe5Yqx5PEtRfZvpBcccD5jhmGc=";
                                } else if( args[ 3 ].equalsIgnoreCase( "yellow" ) ) {
                                    uuid = "7585850e-88b8-4b4d-bcaa-1ac3d5e7ddf0";
                                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDgxMjQ0NywKICAicHJvZmlsZUlkIiA6ICI1NjY3NWIyMjMyZjA0ZWUwODkxNzllOWM5MjA2Y2ZlOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVJbmRyYSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MGY3NjZlZmQ2OTRmNDMwMjBkZGIzYjE1Yzk3MjQ5MjNkYTIwN2Y5ZjFjODUwYTE4MTQzMTVhOWJjODMyMDMzIgogICAgfQogIH0KfQ==";
                                    signature = "xvy31zLNHo81wxOzhenAD6TPP2fIVj4bAbuWvXEuSnVJzNLY8LxQSpW4V2U+dQbzMaQqcc053vfdwj3SaK1Z4JRfJBr35qo22BPhTf69htlg8vhK31nYQxyM+Oah2AT36wh4TOdp0kTRv/qDqgbQgturKlDRyhd4eN0nKS9oorF6LAn7dRNEpFhiXmPBhCxF9Re2ftR8WpYcNyKx4px5b7gevais5UJ+FT2cklZGDaDmDhT4KG/Ry3FVhyGYr1mJOfPER3I4x0vLYRVpvCixL5gHr1+JC6jghfwyENQgE0S6RuYGRDJcFO4POJjdSK5LT4wRdnpRB/oaf74EJCMnX3zSaf+oUAw3jHuzRf1ILTuIjwu9T1peM2i9BsD4dCKcklDRCZoB1MahcVjsdrHdcLsWMxLhpX8dj8VssJCOxn85olrb7KqHwhcKcL6wt5Q1vqPSKPnZO52A+z4cEXNjMr1FNl/TjcsEHwAlcqR4sPEQeYov3PbUVXmDxYN1reVjCbJe5fRuH2DJzOrQZjvfIq+gNEgc2JDZKwO38OSzhC0OYd8a4azEpcZaohb/CaOiNF89QFYqpA3Cq4TNYW+95awbFoWbPqgx3MIK4GYMUldkEUt0l+F1TJWBYPm2m+zIY7BSEP5tykzuArkD5L3Ct4ip6gGuiEj/QSbt/tguH/8=";
                                } else if( args[ 3 ].equalsIgnoreCase( "purple" ) ) {
                                    uuid = "ed394ff4-7878-4c15-8bd9-3cb080d601f8";
                                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDg5NDYzMSwKICAicHJvZmlsZUlkIiA6ICJiMGQ0YjI4YmMxZDc0ODg5YWYwZTg2NjFjZWU5NmFhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5lU2tpbl9vcmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JhZjYyM2I0YzMwZTdkYzE0M2ZmNjkwZDhmZWVhOWUxNjVjMGU1OWJhM2FmYjI1ZDZkYzJlZjBiMTcwOGZmMSIKICAgIH0KICB9Cn0=";
                                    signature = "b/vGZqxiuHxXyP0y/4ovqY/nS4A7iz21B0flu9PKORprChvDGf30021f4rFqtHRx1QB0THd8wQn4C5at6zFjNkgwtnpB39XnaDXF3PWLRrvnUhJLca4LCOCAWQqvvXXvqRpxp3Fzq+sdzhf2yj+fOemqe2mi1MKnPN5XX4u0GZFACCrN57rXaJb3Yqr2Egeh3VmA7yUABObJEoA+zXOac7GTNZNOq5P8HXFkoQoWRmGbKTiwaF6wRFd3YfGtErCaK3ejjJbvyz5mTn/DM663Rm0+l/Orfzvrj6g+aCXhNYXUHhJfMafkS9NiVgQE1rC3CNe3yQCc4kd2c8kxyrb0UyVAOSpSF2jmcR+NcVXVeHJO0sKIkSB37A1BtgqbMPeLZ8l6u3DTGKGH0bmH/dzEl7sDpYYpgU44cWSr7pDxZL/czYmYzgNIf4oMcWb8wL8LEFVR/i3FArP+Niq6mEsppzH8TJ7mFFlRI7b0c0tY89gOVESx3cEktfWydIaPZC2c7GosdU7fNUfEbU5zkYsVStYRtfOHvaekFQuKU0T2vvTJ0xcLtWwSDQtdH5RQtCTQTESHtg/DAecj4X7f7P+02JCYVJXswVVX+qTocjPLiRBYRf1bGDWJWt1gF0t0hDhCicaYfqw68+pbPNphgzWq8cQXPES62/XLuWr8dLXORp0=";
                                } else if( args[ 3 ].equalsIgnoreCase( "green" ) ) {
                                    uuid = "61f55db0-1a0f-4bdf-b2f0-2e85c78ebd29";
                                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTYzNDUwOTM4MCwKICAicHJvZmlsZUlkIiA6ICI5MzZmMTA3MTEzOGM0YjMyYTg0OGY2NmE5Nzc2NDJhMiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwMDAwMDAwMDAwMDAwMDB4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0MDQwMjAyODkyODk1MGE1ZjMyNjI4OGUwNTY1OWEzNGY3OTkxN2UxZjY3ZjA1ZjMzNjk2MzQ2NDY0NjFhNjYiCiAgICB9CiAgfQp9";
                                    signature = "mxnhquFcn93VVkKsHHl/TfbxvjtPoQGQhWsJXYN5dHrGroJ0xgeD75t3198+mR3WHiwjT4N8xhXS2rW7cAErLhsKsBsBeCCbX3oGanQ3Z4TnbRYxQpuxs/l3psqUwT81i8bilw0AorujPvpJnAFEq0SaRhJibNjXD3LtM/uajrTDwr6eSzg6ryeKFFBKwrulRutaJQ1OyZc6Di+bTVhhrBUpEbPZBDiDT1uwjCWJZqVluDIH+TVL/3MqzmCQfT00n5kO9VSJrwE81d9BeNv9m797EhfL19STFHPYSFch37Eg7kg0a3Mj/55AZyM1ksZtDBxwAL68DNaZQpa9ai+guNsOUQ1Yg7iEBSwjZ2OLaQIUnaNXrNruKWmoMLHbhC+/O198re6caTHQk8Lt0ERlhQw1iTfc4VMsSfYZ7W+sNHNR2NVw8PtB/3u9GOwcENYXIFEQIDZ7MbML+K1RV2r715R0VagoZMllQ1NZwOUSCjVs+MIcuQMfy3znsaOpFRuybT1akcWdNHwQYYnhwEsETN84c0ka3mCKuqdCDS5sLLGP/Yxkrlrxc09FXfk3MH8iMb7xRHEo48GsILjdqsWTS5CyJq8eo6qohkFSoXlnXN6TM1CQyTMvrEm+rsQTQUn0cTHdNsU+f1i0JuEAU3qOyjmv4/sKs2KZfnAG8LJUJSA=";
                                } else {
                                    uuid = "5ff05f85-6a5f-404d-b79f-89568de788b7";
                                    texture = "ewogICJ0aW1lc3RhbXAiIDogMTU5NTMwMzE1NzgyMSwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4YzRmMjljM2RlMjdkM2ExODhlZTRjYzU3ZjNmY2EzYmRkMWFmZWE3NjhlZGExMmU3ZDdkOWI3ZDcyNzliYSIKICAgIH0KICB9Cn0=";
                                    signature = "R1atfblbQrreZXgo2yvf4zBzgxrG0Oaodo07uGHrmAWkD12MKFotMFIg9YUIyE4fEwB//k5aWUqcB+9FhUW+/Z7iBCo+gCBDKbnkFR9xcHCZJZ+FJMx1ZL6v2JGNtgNW993eXHTipgsbLqFQwgYXevfFutEX0WdfXGTqD4ShqWp8WfDXnIBGq/T5/uFJP4yRmXRf0+eN683NSGdcWIb3QfgGQKAiDMphefGqaKo/nH2XP2/c53pTxHkwGi00t6lZ0CvGoME0RqnO1fAnSEi64tSe+HJjDKF19z2wFfHCnEM6VzA8ICJ8/NCBk9kN18iysN8U237g+9TOUwh23q/vbELSfnGYdjabDMyWrevVDHU2xhMsvxMpoCPRCQ1IQoAffI0urzM3+5IiZUex8kWN92b/zDH+hFNyUK7SNcmTv0d2oq0hqrvnWRsuE+I88zxl9o+0TCMH32vuT1pS9dWZOiXS6LHgt1WSxwm5jRS40rwfnk8I5duqP+ubpwsq7D01X0+P5s5l2cKrdF1oH1HiVJ2KT1rPnilT++vIpW432ShWDwLhRtsiq0wXzRDbgkejx07ZW3EXRKJOqJFR6nplFBeTOeuwXMfasNjT/qHOYiLdgM6X0ryIDmh9L4xx+7d0wNRZ3c5AuQraoeX9ksZXk23jjYegDEF2Ax3YSMq/c3Y=";
                                }
                                ItemStack item = new ItemBuilder( Material.PLAYER_HEAD )
                                        .setDisplayName( "§6§lTreasure Chest: §5§l" + filename )
                                        .setLore( lore )
                                        .setProfileHeader( uuid, texture, signature )
                                        .build();

                                target.getInventory().addItem( item );

                                player.sendMessage( Messages.GAVE_AN_CHESTTYPE.getMessage( true ).replace( "{target}", target.getDisplayName() ).replace( "{tier}", args[ 3 ] ) );
                                target.sendMessage( Messages.PLAYER_GAVE_AN_CHESTTYPE.getMessage( true ).replace( "{player}", player.getDisplayName() ).replace( "{tier}", args[ 3 ] ) );

                                return true;
                            }

                        player.sendMessage( Messages.GIVE_AN_CHESTTYPE.getMessage( true ) );

                        return true;
                    }
                } else {
                    player.sendMessage( Messages.NON_EXIST.getMessage( true ).replace( "{chest}", args[ 1 ] ) );

                    return false;
                }
            }

            player.sendMessage( Messages.FORGOT_GIVE_NAME.getMessage( true ) );

            return false;
        }
    }

        return false;
}
}
