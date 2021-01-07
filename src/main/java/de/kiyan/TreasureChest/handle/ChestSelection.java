package de.kiyan.TreasureChest.handle;

import de.kiyan.TreasureChest.Config;
import de.kiyan.TreasureChest.Messages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

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

    public HashMap< String, BlockData > getSelectedBlocks( Player player ) {
        if( getFirstLocation( player ) == null
                && getSecondLocation( player ) == null ) {
            player.sendMessage( Messages.TCHEST_SELECT_BEFORE.getMessage( true ) );
            return null;
        }

        World world = getFirstLocation( player ).getWorld();
        Location loc1 = getFirstLocation( player );
        Location loc2 = getSecondLocation( player );

        ArrayList< Block > blocks = new ArrayList<>();

        int topBlockX = Math.max( loc1.getBlockX(), loc2.getBlockX() );
        int topBlockY = Math.max( loc1.getBlockY(), loc2.getBlockY() );
        int topBlockZ = Math.max( loc1.getBlockZ(), loc2.getBlockZ() );

        int bottomBlockX = Math.min( loc1.getBlockX(), loc2.getBlockX() );
        int bottomBlockY = Math.min( loc1.getBlockY(), loc2.getBlockY() );
        int bottomBlockZ = Math.min( loc1.getBlockZ(), loc2.getBlockZ() );

        for( int x = bottomBlockX; x <= topBlockX; x++ )
            for( int z = bottomBlockZ; z <= topBlockZ; z++ )
                for( int y = bottomBlockY; y <= topBlockY; y++ )
                    blocks.add( world.getBlockAt( x, y, z ) );

        if( blocks.size() > 100 ) {
            setFirstLocation( player, null );
            setSecondLocation( player, null );

            player.sendMessage( Messages.TOO_BIG.getMessage( true ) );
            player.sendMessage( Messages.TOO_BIG.getMessage( true ) );
            player.sendMessage( Messages.TOO_BIG.getMessage( true ) );

            return null;
        }

        HashMap< String, BlockData > blockString = new HashMap<>();

        Location location3 = getCenter( player, loc2, loc1 );
        int j = location3.getBlockX();
        int k = location3.getBlockY();
        int m = location3.getBlockZ();
        for( Block bl : blocks ) {
            if( !bl.getType().equals( Material.AIR ) ) {
                Location location = bl.getLocation();
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

                blockString.put( stringX + "_" + stringY + "_" + stringZ, bl.getBlockData() );
            }
        }

        return blockString;
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
        PLAYER_SELECTION.get( player ).clear();
        PLAYER_SELECTION.put( player, replace );
    }

    public void setSecondLocation( Player player, Location loc ) {
        ArrayList< Location > replace = new ArrayList<>();
        replace.add( PLAYER_SELECTION.get( player ).get( 0 ) );
        replace.add( loc );
        PLAYER_SELECTION.get( player ).clear();
        PLAYER_SELECTION.put( player, replace );
    }

    public Location getCenter( Player player, Location loc1, Location loc2 ) {
        int minX = Math.min( loc1.getBlockX(), loc2.getBlockX() );
        int minY = Math.min( loc1.getBlockY(), loc2.getBlockY() );
        int minZ = Math.min( loc1.getBlockZ(), loc2.getBlockZ() );
        int x1 = Math.max( loc1.getBlockX(), loc2.getBlockX() ) + 1;
        //int y1 = Math.max(loc1.getBlockY(), loc2.getBlockY()) + 1;
        int z1 = Math.max( loc1.getBlockZ(), loc2.getBlockZ() ) + 1;

        return new Location( player.getWorld(), minX + ( x1 - minX ) / 2.0D, minY + 1, minZ + ( z1 - minZ ) / 2.0D );
    }

    public Location getFirstLocation( Player player ) {
        return PLAYER_SELECTION.get( player ) == null ? null : PLAYER_SELECTION.get( player ).get( 0 );
    }

    public Location getSecondLocation( Player player ) {
        return PLAYER_SELECTION.get( player ) == null ? null : PLAYER_SELECTION.get( player ).get( 1 );
    }

    public void endSelection( Player player ) {
        if( getFirstLocation( player ) != null && getSecondLocation( player ) != null ) {
            ChestSelection chest = new ChestSelection( player );
            Location loc1 = chest.getFirstLocation( player );
            Location loc2 = chest.getSecondLocation( player );

            ArrayList< Location > arrayLoc = new ArrayList<>();
            arrayLoc.add( loc1 );
            arrayLoc.add( loc2 );
            Config config = new Config();
            config.removeBlocks( EDIT_MODE.get( player ) );
            config.setBlocks( EDIT_MODE.get( player ), chest.getSelectedBlocks( player ) );
            new ChestMenu().setupNewMenu( player, EDIT_MODE.get( player ) ).openMenu( player );

            EDIT_MODE.remove( player );
            PLAYER_SELECTION.remove( player );
        }
    }
}
