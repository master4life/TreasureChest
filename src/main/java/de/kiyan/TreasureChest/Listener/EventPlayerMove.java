package de.kiyan.TreasureChest.Listener;

import de.kiyan.TreasureChest.TChest;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class EventPlayerMove implements Listener {
    @EventHandler
    public void onMove( PlayerMoveEvent event ) {
        Player player = event.getPlayer();
        if( TChest.blockedPlayers == null )
            return;
        if( TChest.blockedPlayers.containsKey( player ) ) {
            if( ( int ) event.getFrom().getX() != ( int ) ( ( Location ) TChest.blockedPlayers.get( player ) ).getX() || ( int ) event.getFrom().getZ() != ( int ) ( ( Location ) TChest.blockedPlayers.get( player ) ).getZ() ) {
                Location location = TChest.blockedPlayers.get( player );
                location.setPitch( event.getFrom().getPitch() );
                location.setYaw( event.getFrom().getYaw() );
                player.teleport( location );
            }
        } else {
            if( TChest.tchestList == null )
                return;

            for( TChest tChest : TChest.tchestList ) {
                if( tChest.getState() != TChest.TChestState.END )
                    for( Block block : tChest.getBackup().keySet() ) {
                        if( event.getTo().distance( block.getLocation() ) < 1.3D ) {
                            bounceBack( player, tChest.getCenter() );
                            return;
                        }
                    }
            }
        }
    }

    public void bounceBack( Player player, Location loc )
    {
        double dX = loc.getX() - player.getLocation().getX();
        double dY = loc.getY() - player.getLocation().getY();
        double dZ = loc.getZ() - player.getLocation().getZ();
        double yaw = Math.atan2( dZ, dX );
        double pitch = Math.atan2( Math.sqrt( dZ * dZ + dX * dX ), dY ) + Math.PI;
        double X = Math.sin( pitch ) * Math.cos( yaw );
        double Y = Math.sin( pitch ) * Math.sin( yaw );
        double Z = Math.cos( pitch );

        Vector vector = new Vector( X, Z, Y );
        player.setVelocity( vector.setY( 0.8 ) );
    }

}
