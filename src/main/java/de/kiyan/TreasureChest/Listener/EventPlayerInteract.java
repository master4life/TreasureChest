package de.kiyan.TreasureChest.Listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.kiyan.TreasureChest.Config;
import de.kiyan.TreasureChest.Main;
import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.Runnable.FlyingItems;
import de.kiyan.TreasureChest.TChest;
import de.kiyan.TreasureChest.Utils.Effects;
import de.kiyan.TreasureChest.handle.ChestSelection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventPlayerInteract implements Listener {
    @EventHandler
    public void onBlockInteract( PlayerInteractEvent event ) {
        Player player = event.getPlayer();
        ChestSelection tchest = new ChestSelection( player );
        Action action = event.getAction();

        if( player.getItemInHand().getItemMeta() != null ) {
            ItemStack is = player.getItemInHand();
            if( is.getItemMeta().getDisplayName().contains( "§6§lTreasure Chest: §5§l" )  ) {
                if( action == Action.RIGHT_CLICK_AIR)
                {
                    if( TChest.blockedPlayers == null || !TChest.blockedPlayers.containsKey( player ) ) {
                        for( Entity ent : player.getLocation().getWorld().getNearbyEntities( player.getLocation(), 5, 5, 5 ) ) {
                            if( ent instanceof Player ) {
                                if( ent != player ) {
                                    player.sendMessage( Messages.NEARBY_PLAYER.getMessage( true ) );

                                    return;
                                }
                            }
                        }

                        new FlyingItems( is, player, Integer.parseInt( new Config().get( "Flytime" ) == null ? "3" : new Config().get( "Flytime" )  ), Main.getInstance());

                        event.setCancelled( true );
                        new TChest( player, is.getItemMeta().getDisplayName().split( "§5§l" )[ 1 ] );

                        ItemStack hand = player.getInventory().getItemInHand();
                        if( hand.getAmount() > 1 ) {
                            hand.setAmount( hand.getAmount() - 1 );
                            player.getInventory().setItemInHand( hand );
                        } else
                            player.getInventory().remove( is );

                    } else {
                        player.sendMessage( Messages.ALREADY_RUNNING.getMessage() );
                        return;
                    }

                } else if( action == Action.RIGHT_CLICK_BLOCK )
                {
                    event.setCancelled( true );
                }

            }
        }

        if( tchest.getEditModer( player )
                && player.getItemInHand().getType().equals( Material.WOODEN_AXE ) ) {

            Block block = event.getClickedBlock();
            if( block.getType() == Material.AIR )
                return;

            if( action == Action.LEFT_CLICK_BLOCK ) {
                Location first = block.getLocation();
                player.sendMessage( Messages.SELECT_POS1.getMessage( true ).replace( "{POS1}", "in§f§l: " + first.getWorld().getName() + " §cx: §f§l" + first.getBlockX() + "§c y: §f§l" + first.getBlockY() + "§c z: §f§l" + first.getBlockZ() ) );
                tchest.setFirstLocation( player, first );
            }

            if( action == Action.RIGHT_CLICK_BLOCK ) {
                Location second = block.getLocation();
                player.sendMessage( Messages.SELECT_POS2.getMessage( true ).replace( "{POS2}", "in§f§l: " + second.getWorld().getName() + " §cx: §f§l" + second.getBlockX() + "§c y: §f§l" + second.getBlockY() + "§c z: §f§l" + second.getBlockZ() ) );
                tchest.setSecondLocation( player, second );
            }

            new ChestSelection( player ).endSelection( player );
        }

        if( action == Action.RIGHT_CLICK_BLOCK ) {
            Block block = event.getClickedBlock();
            if( block.hasMetadata( "TChest" ) ) {
                event.setCancelled( true );
                String str = block.getMetadata( "TChest" ).get( 0 ).asString();
                if( !str.equals( "none" ) ) {
                    Player player1 = Bukkit.getPlayer( str );
                    TChest tChest = getTChest( player1 );
                    if( tChest != null )
                        if( tChest.getP().equals( player ) ) {
                            tChest.openChest( block );
                        } else {
                            player.sendMessage( Messages.OWN_CHEST.getMessage( true ) );
                        }
                }
            }
        }
    }

    public TChest getTChest( Player player ) {
        for( TChest tChest : TChest.tchestList ) {
            if( tChest.getP().equals( player ) )
                return tChest;
        }
        return null;
    }
}
