package de.kiyan.TreasureChest.handle;

import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.commands.TChestCommand;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ChestSelection {

    public static HashMap< Player, ArrayList< Location > > PLAYER_SELECTION = new HashMap<>();
    public static HashMap< Player, String > EDIT_MODE = new HashMap<>();

    public ChestSelection( Player player ) {
        if( !PLAYER_SELECTION.containsKey( player ) ) {
            ArrayList< Location > loc = new ArrayList<>();
            loc.add( null );
            loc.add( null );

            PLAYER_SELECTION.put( player, loc );
        }
    }

    public ArrayList< String > getSelectedBlocks( Player player ) {
        if( getFirstLocation( player ) == null && getSecondLocation( player ) == null ) {
            player.sendMessage( Messages.TCHEST_SELECT_BEFORE.getMessage( true ) );
            return null;
        }

        World world = getFirstLocation( player ).getWorld();
        Location location1 = getFirstLocation( player );
        Location location2 = getSecondLocation( player );

        ArrayList< Block > blocks = new ArrayList<>();

        for( int i = location1.getBlockX(); i <= location2.getBlockX(); i++ ) {
            for( int n = location1.getBlockY(); n <= location2.getBlockY(); n++ ) {
                for( int i1 = location1.getBlockZ(); i1 <= location2.getBlockZ(); i1++ )
                    blocks.add( world.getBlockAt( i, n, i1 ) );
            }
        }
        if( blocks.size() > 100 ) {
            player.sendMessage( Messages.TOO_BIG.getMessage( true ) );
            return null;
        }

        ArrayList< String > blockString = new ArrayList<>();

        Location location3 = player.getLocation();
        int j = location3.getBlockX();
        int k = location3.getBlockY();
        int m = location3.getBlockZ();
        for( Block block : blocks ) {
            if( !block.getState().getType().equals( Material.AIR ) ) {
                Location location = block.getLocation();
                int n = location.getBlockX();
                int i1 = location.getBlockY();
                int i2 = location.getBlockZ();
                String stringX = " ";
                String stringY = " ";
                String stringZ = " ";
                if( n > j ) {
                    stringX = "+" + ( n - j );
                } else {
                    stringX = "-" + ( j - n );
                }
                if( i1 > k ) {
                    stringY = "+" + ( i1 - k );
                } else {
                    stringY = "-" + ( k - i1 );
                }
                if( i2 > m ) {
                    stringZ = "+" + ( i2 - m );
                } else {
                    stringZ = "-" + ( m - i2 );
                }
                blockString.add(  stringX + "_" + stringY + "_" + stringZ );
            }
        }

        return blockString;
    }

    public ArrayList< String > getLocationRelatedToPlayer( Player player, ArrayList< Location > loc ) {
        ArrayList< String > arrayList = new ArrayList<>();
        ArrayList< Block > blockList = new ArrayList<>();

        if( arrayList.size() < 100 ) {
            World world = loc.get( 0 ).getWorld();

            Location location1 = loc.get( 0 );
            Location location2 = loc.get( 1 );
            for( int i = location1.getBlockX(); i <= location2.getBlockX(); i++ )
                for( int n = location1.getBlockY(); n <= location2.getBlockY(); n++ )
                    for( int i1 = location1.getBlockZ(); i1 <= location2.getBlockZ(); i1++ )
                        blockList.add( world.getBlockAt( i, n, i1 ) );

            Location location3 = player.getLocation();
            int j = location3.getBlockX();
            int k = location3.getBlockY();
            int m = location3.getBlockZ();

            for( Block block : blockList ) {
                if( !block.getState().getType().equals( Material.AIR ) ) {
                    Location location = block.getLocation();
                    int n = location.getBlockX();
                    int i1 = location.getBlockY();
                    int i2 = location.getBlockZ();
                    String stringX = " ";
                    String stringY = " ";
                    String stringZ = " ";
                    if( n > j ) {
                        stringX = "+" + ( n - j );
                    } else {
                        stringX = "-" + ( j - n );
                    }
                    if( i1 > k ) {
                        stringY = "+" + ( i1 - k );
                    } else {
                        stringY = "-" + ( k - i1 );
                    }
                    if( i2 > m ) {
                        stringZ = "+" + ( i2 - m );
                    } else {
                        stringZ = "-" + ( m - i2 );
                    }
                    arrayList.add( String.valueOf( stringX ) + "_" + stringZ + "_" + stringZ );
                }
            }

        } else {
            player.sendMessage( "[TreasureChest] You WorldEdit selection is too big!" );
        }

        return arrayList;
    }

    /*
    Getter and setter if the person is selecting or not.
     */
    public String getMenu( Player player ) {
        return EDIT_MODE.get( player );
    }

    public Boolean getEditModer( Player player ) {
        return EDIT_MODE.containsKey( player );
    }

    /*
    Getter and setter of the players locations
    */
    public void setFirstLocation( Player player, Location loc ) {
        ArrayList< Location > replace = new ArrayList<>();
        replace.add( loc );
        replace.add( PLAYER_SELECTION.get( player ).get( 1 ) );
        PLAYER_SELECTION.replace( player, replace );
    }

    public void setSecondLocation( Player player, Location loc ) {
        ArrayList< Location > replace = new ArrayList<>();
        replace.add( PLAYER_SELECTION.get( player ).get( 0 ) );
        replace.add( loc );
        PLAYER_SELECTION.replace( player, replace );
    }

    public Location getFirstLocation( Player player ) {
        return PLAYER_SELECTION.get( player ) == null ? null : PLAYER_SELECTION.get( player ).get( 0 );
    }

    public Location getSecondLocation( Player player ) {
        return PLAYER_SELECTION.get( player ) == null ? null : PLAYER_SELECTION.get( player ).get( 1 );
    }

    public void endSelection( Player player ) {
        if( getFirstLocation( player ) != null
                && getSecondLocation( player ) != null ) {
            new ChestMenu().setupNewMenu( player, EDIT_MODE.get( player ) ).openMenu( player );
            EDIT_MODE.remove( player );
        }
    }
}
