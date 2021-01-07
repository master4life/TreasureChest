package de.kiyan.TreasureChest;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import de.kiyan.TreasureChest.Utils.Effects;
import de.kiyan.TreasureChest.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TChest {
    public static ArrayList< TChest > tchestList;
    public static HashMap< Player, Location > blockedPlayers;
    private Player playerWhoActivated;
    private HashMap< Block, BlockData > Backup;
    private ArrayList< Item > drops;
    private Location center;
    private TChest tchest;
    private HashMap< Block, BlockData > chests;
    private String selected;
    private TChestState state;
    private double dist;

    public enum TChestState {
        WAIT, BUILDING, DESTROYING, END
    }

    public TChest( Player player, String name ) {
        Config config = new Config();
        if( !config.exist( name ) )
            return;
        if( config.getBlocks( name ) == null ) {
            player.sendMessage( Messages.BLOCKS_HAVENT_SET.getMessage( true ) );
            return;
        }

        if( blockedPlayers == null )
            blockedPlayers = new HashMap<>();

        if( tchestList == null )
            tchestList = new ArrayList<>();

        tchestList.add( this );

        this.playerWhoActivated = player;
        this.tchest = this;
        this.selected = name;

        build();
    }

    public void build() {
        this.state = TChestState.BUILDING;
        Effects effect = new Effects();
        effect.createSecondCircle( playerWhoActivated, tchest, new Config().getParticle( "initiate" ) == null ? Particle.FLAME : new Config().getParticle( "initiate" ) );
        for( Player broadcast : Bukkit.getOnlinePlayers() ) {
            if( broadcast != playerWhoActivated )
                broadcast.sendMessage( Messages.BROADCAST.getMessage( false )
                        .replace( "{player}", playerWhoActivated.getName().toUpperCase( Locale.ROOT ) )
                        .replace( "{type}", selected.toUpperCase( Locale.ROOT ) ) );
        }

        new BukkitRunnable() {
            int i = 3;

            public void run() {
                if( i > 0 ) playerWhoActivated.sendMessage( Messages.OPENING_TCHEST.getMessage( false ) + " " + i );

                if( i == 0 ) {
                    playerWhoActivated.sendMessage( Messages.OPENING_TCHEST.getMessage( false ) + " §c§lNOW" );
                    // Event opening message
                    effect.FrostLordEffect( TChest.this.playerWhoActivated.getLocation(), Particle.WATER_SPLASH );
                    cancel();
                }
                i--;
            }
        }.runTaskTimer( Main.getInstance(), 0L, 20L );

        Location location = this.playerWhoActivated.getLocation();
        blockedPlayers.put( this.playerWhoActivated, location );

        playerWhoActivated.setInvulnerable( true );
        this.Backup = new HashMap<>();
        this.center = new Location( location.getWorld(), location.getBlockX() + 0.5D, location.getBlockY(), location.getBlockZ() + 0.5D );
        this.drops = new ArrayList<>();
        this.chests = new HashMap<>();
        HashMap< String, BlockData > hashBlocks = new Config().getBlocks( selected );
        String[] arrayBlocks = new String[ hashBlocks.size() ];
        int index = 0;
        this.dist = 1.0D;

        for( Map.Entry< String, BlockData > mapEntry : hashBlocks.entrySet() ) {
            arrayBlocks[ index ] = mapEntry.getKey();
            index++;
        }

        new BukkitRunnable() {
            public void run() {

                for( String arrayBlock : arrayBlocks ) {

                    String coord = ( String ) arrayBlock;
                    String[] split = coord.split( "_" );
                    Location location = new Location( TChest.this.center.getWorld(),
                            ( TChest.this.center.getBlockX() + Integer.parseInt( split[ 0 ] ) ) + 0.5D,
                            ( TChest.this.center.getBlockY() + Integer.parseInt( split[ 1 ] ) ),
                            ( TChest.this.center.getBlockZ() + Integer.parseInt( split[ 2 ] ) ) + 0.5D );
                    Block block = location.getBlock();
                    if( TChest.this.center.distance( location ) <= TChest.this.dist && !TChest.this.Backup.containsKey( block ) ) {
                        TChest.this.Backup.put( block, block.getBlockData() );

                        BlockData blockD = new Config().getBlocks( selected ).get( coord );

                        if( blockD.getMaterial().equals( Material.CHEST ) || blockD.getMaterial().equals( Material.TRAPPED_CHEST )) {
                            TChest.this.chests.put( block, blockD );
                        } else {
                            block.setBlockData( new Config().getBlocks( selected ).get( coord ) );
                            block.getWorld().playEffect( location, Effect.STEP_SOUND, 1, 20 );
                        }
                    }
                }

                TChest tChest = TChest.this;
                tChest.dist = tChest.dist + 0.5;
                if( TChest.this.dist > 6.5 ) {
                    cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 60L, 15L );
        new BukkitRunnable() {
            int i = 0;

            public void run() {
                if( !TChest.this.chests.isEmpty() && i < TChest.this.chests.size() ) {
                    Block block = ( Block ) TChest.this.chests.keySet().toArray()[i];
                    BlockData blockData = ( BlockData ) TChest.this.chests.values().toArray()[i];
                    Location bloc = block.getLocation();
                    //effect.createHelix( location, 3.0, Particle.FLAME, 8 );
                    effect.playSpiral( bloc );

                    new BukkitRunnable() {
                        public void run() {
                            //Directional directional = ( Directional ) block.getBlockData();
                            //directional.setFacing( Utils.yawToFace( Utils.getLookAtYaw( bloc.toVector().subtract( TChest.this.playerWhoActivated.getLocation().toVector() ) ), false ).getOppositeFace() );
                            block.getWorld().playSound( block.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f );
                            block.setBlockData( blockData );
                            block.setMetadata( "TChest", ( MetadataValue ) new FixedMetadataValue( Main.getInstance(), TChest.this.getP().getName() ) );
                        }
                    }.runTaskLater( Main.getInstance(), 40L );

                    ++i;
                    if( i == TChest.this.chests.size() + 1 )
                        cancel();
                } else {
                    cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 120L, 50L );
        this.state = TChestState.WAIT;
        process();
    }

    public void process() {
        new BukkitRunnable() {
            int i = 0;

            public void run() {
                if( !TChest.this.chests.isEmpty() ) {
                    TChest.this.openChest( (Block) TChest.this.chests.keySet().toArray()[ i ] );

                    if( i + 1 == TChest.this.chests.size() ) {
                        TChest.this.destroy();
                        cancel();
                    } else {
                        i++;
                    }
                } else {
                    TChest.this.destroy();
                    cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 460L, 50L );
    }

    private String getChanceType() {
        String[] tier = new Config().getTierChance( TChest.this.selected );
        double d = Double.parseDouble( String.valueOf( Utils.RandInt( 0, 100 ) ) );
        if( d <= Double.parseDouble( tier[ 2 ] ) ) {
            return "legendary";
        } else if( d <= Double.parseDouble( tier[ 1 ] ) ) {
            return "rare";
        }
        return "common";
    }


    public void openChest( Block block ) {
        Location location1 = Utils.getBlockCenter( block.getLocation() );
        Location clocUP = Utils.getBlockCenterUP( block.getLocation() );
        if( block.hasMetadata( "TChest" ) & ( ( ( MetadataValue ) block.getMetadata( "TChest" ).get( 0 ) ).asString().equals( "none" ) ? 0 : 1 ) != 0 ) {
            block.setMetadata( "TChest", ( MetadataValue ) new FixedMetadataValue( Main.getInstance(), "none" ) );
            new BukkitRunnable() {
                ArrayList< ItemStack > items = new Config().getItems( TChest.this.selected, true );
                String line = "";

                @Override
                public void run() {
                    Effects effect = new Effects();
                    Config config = new Config();
                    effect.spawn( clocUP, Particle.ASH, 0.1f, 0.1f, 0.1f, 0.05f, 30.0d );
                    if( this.items.size() >= 1 ) {
                        String str = getChanceType();

                        ArrayList< ItemStack > arrayList = new ArrayList<>();
                        ItemStack itemStack = null;

                        for( ItemStack itemStack1 : this.items ) {
                            ItemMeta itemMeta1 = itemStack1.getItemMeta();
                            List< String > list1 = itemMeta1.getLore();

                            for( String lore : list1 ) {
                                if( lore.startsWith( "§" ) && lore.contains( str ) ) {
                                    arrayList.add( itemStack1 );
                                }
                            }
                        }
                        if( arrayList.isEmpty() ) {
                            itemStack = ( ( ItemStack ) this.items.get( Utils.RandInt( 0, this.items.size() - 1 ) ) ).clone();
                        } else {
                            itemStack = ( ( ItemStack ) arrayList.get( Utils.RandInt( 0, arrayList.size() - 1 ) ) ).clone();
                        }
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        List< String > list = itemMeta.getLore();
                        if( list == null )
                            list = new ArrayList<>();

                        list.add( ChatColor.BLUE + "Item: " + Utils.RandInt( 0, 1000 ) );
                        itemMeta.setLore( list );
                        itemStack.setItemMeta( itemMeta );
                        location1.getWorld().playSound( location1, Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F );
                        effect.chestAnimation( location1 );
                        Item item = TChest.this.playerWhoActivated.getWorld().dropItem( clocUP, itemStack );
                        item.setVelocity( new Vector( 0.0D, 0.25D, 0.0D ) );
                        item.setPickupDelay( 4000 );
                        TChest.this.drops.add( item );

                        if( itemMeta.hasDisplayName() )
                            this.line = "§a§l" + itemStack.getAmount() + " " + itemMeta.getDisplayName();
                        else
                            this.line = ( "§a§l" + itemStack.getAmount() + " " + itemMeta.toString() ).replace( "_", " " );

                        for( String str1 : list ) {
                            if( str1.contains( "common" ) ) {
                                new BukkitRunnable() {

                                    public void run() {
                                        effect.createHologram( TChest.this.playerWhoActivated, clocUP, "§f§lCOMMON", item.getItemStack().getAmount() + "x " + "§f§l" + item.getName().replace( "§f", "§f§l" ) );
                                        cancel();
                                    }
                                }.runTaskTimer( Main.getInstance(), 0L, 15L );
                                continue;
                            }
                            if( str1.contains( "rare" ) ) {
                                new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        if( i == 0 )
                                            effect.createHologram( TChest.this.playerWhoActivated, clocUP, "§4§lRARE", item.getItemStack().getAmount() + "x " + "§4§l" + item.getName().replace( "§f", "§4§l" ) );

                                        effect.spawn( clocUP, config.getParticle( "rare" ) == null ? Particle.CRIT : config.getParticle( "rare" ), 0.3f, 0.3f, 0.3f, 0.3f, 30.0d );
                                        i++;

                                        if( i == 2 )
                                            cancel();
                                    }
                                }.runTaskTimer( Main.getInstance(), 0L, 15L );
                                effect.createCircle( clocUP, 2, config.getParticle( "firstRare" ) == null ? Particle.FLAME : config.getParticle( "firstRare" ) );
                                continue;
                            }
                            if( str1.contains( "legendary" ) )
                            {
                                new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        if( i == 0 )
                                            effect.createHologram( TChest.this.playerWhoActivated, clocUP, "§e§lLEGENDARY", item.getItemStack().getAmount() + "x " + "§4§l" + item.getName().replace( "§f", "§4§l" ) );
                                        effect.spawn( clocUP, config.getParticle( "legendary" ) == null ? Particle.FLAME : config.getParticle( "legendary" ), 0.3f, 0.3f, 0.3f, 0.3f, 30.0d );
                                        i++;
                                        if( i == 2 )
                                            cancel();
                                    }
                                }.runTaskTimer( Main.getInstance(), 0L, 15L );
                                effect.createCircle( clocUP, 2, config.getParticle( "firstLegendary" ) == null ? Particle.TOTEM : config.getParticle( "firstLegendary" ) );
                                new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        new Effects().playRandomFirework( clocUP );
                                        i++;
                                        if( this.i == 2 )
                                            cancel();
                                    }
                                }.runTaskTimer( Main.getInstance(), 0L, 20L );
                            }
                        }
                    }

                }
            }.runTaskLater( Main.getInstance(), 10L );
        }
    }

    public void destroy() {
        new BukkitRunnable() {
            int i = 0;

            public void run() {
                TChest.this.state = TChest.TChestState.DESTROYING;
                Object[] arrayOfObject = TChest.this.Backup.keySet().toArray();

                for( i = 0; i < arrayOfObject.length; i++ ) {
                    Block block = ( Block ) arrayOfObject[ i ];
                    Location location = Utils.getBlockCenter( block.getLocation() );
                    if( block.getType() == Material.WALL_TORCH
                            || block.getType().name().contains( "BANNER" )
                            || block.getType() == Material.REDSTONE_WALL_TORCH
                            || block.getType() == Material.REDSTONE_WIRE || block.getType() == Material.REDSTONE ) {
                        block.getWorld().playEffect( location, Effect.STEP_SOUND, 1 );
                        block.setBlockData( TChest.this.Backup.get( block ) );
                        TChest.this.Backup.remove( block );
                    }
                    if( location.distance( Utils.getBlockCenter( TChest.this.center.getBlock().getLocation() ) ) >= TChest.this.dist && TChest.this.Backup.containsKey( block ) ) {
                        BlockData blockData = TChest.this.Backup.get( block );
                        block.getWorld().playEffect( location, Effect.STEP_SOUND, 1 );
                        Block target = block.getLocation().getBlock();
                        if( target.getType() == Material.CHEST || target.getType() == Material.TRAPPED_CHEST )
                            target.setType( Material.AIR );

                        block.setBlockData( blockData );

                        TChest.this.Backup.remove( block );
                        for( Entity ent : Bukkit.getWorld( playerWhoActivated.getWorld().getName() ).getEntities() ) {
                            if( ent.hasMetadata( "TChest" ) && ent.getMetadata( "TChest" ).get( 0 ).asString().equalsIgnoreCase( playerWhoActivated.getName() ) ) {
                                LivingEntity entity = ( LivingEntity ) ent;
                                if( entity instanceof ArmorStand ) {
                                    entity.setHealth( 0.0D );
                                }
                            }
                        }
                    }
                }
                TChest.this.dist = TChest.this.dist - 0.5D;
                if( TChest.this.dist == 2.0D ) {
                    Iterator< Item > iterator = TChest.this.drops.iterator();
                    while( iterator.hasNext() ) {
                        Item item = iterator.next();
                        if( item.getItemStack().getType().equals( Material.getMaterial( "DOUBLE_PLANT" ) ) || item.getItemStack().getType().equals( Material.REDSTONE ) ) {
                            iterator.remove();
                            item.remove();
                            continue;
                        }

                        if( TChest.this.playerWhoActivated.getInventory().firstEmpty() != -1 ) {
                            ItemStack itemStack = item.getItemStack();
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            List< String > list = itemMeta.getLore();
                            Iterator< String > iterator1 = list.iterator();
                            while( iterator1.hasNext() ) {
                                String str = iterator1.next();
                                if( str.contains( "Item:" ) || str.contains( "common" ) || str.contains( "rare" ) || str.contains( "legendary" ) )
                                    iterator1.remove();
                            }
                            itemMeta.setLore( list );
                            itemStack.setItemMeta( itemMeta );
                            item.setItemStack( itemStack );
                            TChest.this.playerWhoActivated.getInventory().addItem( new ItemStack[]{ itemStack } );
                            iterator.remove();
                            item.remove();
                            continue;
                        }
                        item.setPickupDelay( 0 );
                        TChest.this.playerWhoActivated.sendMessage( Messages.FULL_INVENTORY.getMessage( true ) );
                    }
                    TChest.this.playerWhoActivated.setInvulnerable( false );
                    TChest.this.playerWhoActivated.updateInventory();
                    TChest.this.drops.clear();

                } else if( TChest.this.dist <= 0.0D ) {
                    if( TChest.blockedPlayers.containsKey( TChest.this.getP() ) ) {
                        TChest.blockedPlayers.remove( TChest.this.getP() );
                    }
                    if( TChest.tchestList.contains( TChest.this.getTChest() ) ) {
                        TChest.tchestList.remove( TChest.this.getTChest() );
                    }

                    TChest.this.tchest.setState( TChest.TChestState.END );
                    cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 20L, 15L );
    }
    /*
                < Getter and setter methods >
     */

    // Sets the current state of the opening process
    public TChestState getState() {
        return this.state;
    }

    public Location getCenter() {
        return this.center;
    }

    public void setState( TChestState chestState ) {
        this.state = chestState;
    }

    public Player getP() {
        return this.playerWhoActivated;
    }

    public void setP( Player player ) {
        this.playerWhoActivated = player;
    }

    public HashMap< Block, BlockData > getBackup() {
        return this.Backup;
    }

    public void setBackup( HashMap< Block, BlockData > paramHashMap ) {
        this.Backup = paramHashMap;
    }

    // TChest instance
    public TChest getTChest() {
        return this.tchest;
    }

    public void setTChest( TChest tchest ) {
        this.tchest = tchest;
    }

    //  Hashmap contains the actually item  and the drop chance
    public ArrayList< Item > getDrops() {
        return this.drops;
    }

    public void setDrops( ArrayList< Item > drop ) {
        this.drops = drop;
    }
}
