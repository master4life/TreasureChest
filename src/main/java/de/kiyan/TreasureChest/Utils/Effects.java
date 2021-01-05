package de.kiyan.TreasureChest.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.kiyan.TreasureChest.Main;
import de.kiyan.TreasureChest.TChest;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class Effects {
    public void spawn( Location location, Particle effect, Double speed, Double data ) {
        Bukkit.getWorld( location.getWorld().getName() ).spawnParticle( effect, location, 1, 0.0d, 0.0d, 0.0d );
    }

    public void spawn( Location location, Particle effect, Float x, Float y, Float z, Float speed, Double data ) {
        Bukkit.getWorld( location.getWorld().getName() ).spawnParticle( effect, location, 1, x, y, z );
    }

    public void dragonAnimation( Player player ) {
        PacketContainer packet = new PacketContainer( PacketType.Play.Server.SPAWN_ENTITY_LIVING );

        // Entity ID
        packet.getIntegers().write( 0, 323123 );
        packet.getModifier().writeDefaults();
        // Entity UUID
        packet.getUUIDs().write( 0, UUID.randomUUID() );
        // Entity Type
        packet.getIntegers().write(1, (int) 19);
        // Set location
        packet.getDoubles().write( 0, player.getLocation().getX() );
        packet.getDoubles().write( 1, player.getLocation().getY() );
        packet.getDoubles().write( 2, player.getLocation().getZ() );

        packet.getBytes().write(0, (byte) 0);
        packet.getBytes().write(1, (byte) 0);
        packet.getBytes().write(2, (byte) 0);
        packet.getIntegers().write(2, 0);
        packet.getIntegers().write(3, 0);
        packet.getIntegers().write(4, 0);
        try {
            ProtocolLibrary.getProtocolManager().broadcastServerPacket( packet );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public WrappedDataWatcher getDefaultWatcher( World world, EntityType type ) {
        Entity entity = world.spawnEntity( new Location( world, 0, 256, 0 ), type );
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher( entity ).deepClone();

        entity.remove();
        return watcher;
    }

    public void chestAnimation( Location loc ) {
        PacketContainer chest = new PacketContainer( PacketType.Play.Server.BLOCK_ACTION );
        chest.getBlocks().write( 0, Material.CHEST );
        chest.getBlockPositionModifier().write( 0, new BlockPosition( loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() ) );
        chest.getIntegers().write( 0, 1 );
        chest.getIntegers().write( 1, 1 ); //1 for open, 0 for close

        ProtocolLibrary.getProtocolManager().broadcastServerPacket( chest );
    }

    public void coneEffect( Location loc, Particle particleEffect ) {
        new BukkitRunnable() {
            double phi = 0.0;

            public void run() {
                this.phi += 0.39269908169872414;
                for( double n = 0.0; n <= 6.283185307179586; n += 0.19634954084936207 ) {
                    for( double n2 = 0.0; n2 <= 1.0; ++n2 ) {
                        final double n3 = 0.4 * ( 6.283185307179586 - n ) * 0.5 * Math.cos( n + this.phi + n2 * 3.141592653589793 );
                        final double n4 = 0.5 * n;
                        final double n5 = 0.4 * ( 6.283185307179586 - n ) * 0.5 * Math.sin( n + this.phi + n2 * 3.141592653589793 );
                        loc.add( n3, n4, n5 );
                        spawn( loc, particleEffect, 0.0d, 1.0d );
                        loc.subtract( n3, n4, n5 );
                    }
                }
                if( this.phi > 6.283185307179586 ) {
                    this.cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 0L, 3L );
    }

    public void createHologram( Player player, Location loc, String tier, String description ) {
        ArmorStand itemTier = ( ArmorStand ) loc.getWorld().spawnEntity( loc.add( new Vector( 0.0f, 0.1f, 0.0f ) ), EntityType.ARMOR_STAND );
        itemTier.setGravity( false );
        itemTier.setVisible( false );
        itemTier.setMarker( true );
        itemTier.setCustomNameVisible( true );
        itemTier.setCanPickupItems( false );
        itemTier.setCustomName( tier );
        itemTier.setMetadata( "TChest", ( MetadataValue ) new FixedMetadataValue( Main.getInstance(), player.getName() ) );

        ArmorStand itemDescription = ( ArmorStand ) loc.getWorld().spawnEntity( loc.add( new Vector( 0.0f, 0.3f, 0.0f ) ), EntityType.ARMOR_STAND );
        itemDescription.setGravity( false );
        itemDescription.setVisible( false );
        itemDescription.setCustomNameVisible( true );
        itemDescription.setCanPickupItems( false );
        itemDescription.setMarker( true );
        itemDescription.setCustomName( description );
        itemDescription.setMetadata( "TChest", ( MetadataValue ) new FixedMetadataValue( Main.getInstance(), player.getName() ) );
    }

    public void createTotemCircle( Player player, TChest tchest ) {
        new BukkitRunnable() {
            double i = 0.0;
            Location loc, first, second;

            public void run() {
                i += Math.PI / 16;

                loc = player.getLocation();
                first = loc.clone().add( Math.cos( i ), Math.sin( i ) + 1, Math.sin( i ) );
                second = loc.clone().add( Math.cos( i + Math.PI ), Math.sin( i ) + 1, Math.sin( i + Math.PI ) );

                player.getWorld().spawnParticle( Particle.FLAME, first, 0 );
                player.getWorld().spawnParticle( Particle.FLAME, second, 0 );
                if( tchest.getState().equals( TChest.TChestState.DESTROYING ) )
                    this.cancel();
            }
        }.runTaskTimer( Main.getInstance(), 0L, 1L );
    }

    public void createCircle( Location location, int n, Particle particleEffect ) {
        new BukkitRunnable() {
            double i = 0.0;

            public void run() {
                for( int i = 0; i <= 360; i += 15 ) {
                    double n = this.i * Math.cos( i );
                    double n2 = this.i * Math.sin( i );
                    location.add( n, 0.0, n2 );
                    spawn( location, particleEffect, 0.0d, 1.0d );
                    location.subtract( n, 0.0, n2 );
                }
                this.i += 0.1;
                if( this.i >= n ) {
                    this.cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 0L, 2L );
    }

    public void FrostLordEffect( Location location, Particle particleEffect ) {
        new BukkitRunnable() {
            double t = 0.0;

            public void run() {
                this.t += 0.39269908169872414;
                for( double n = 0.0; n <= 6.283185307179586; n += 1.5707963267948966 ) {
                    double n2 = 0.3 * ( 9.42477796076938 - this.t ) * Math.cos( this.t + n );
                    double n3 = 0.2 * this.t;
                    double n4 = 0.3 * ( 9.42477796076938 - this.t ) * Math.sin( this.t + n );

                    location.add( n2, n3, n4 );
                    spawn( location, particleEffect, 0.0d, 3.0d );
                    spawn( location.add( 0.0, 0.2, 0.0 ), particleEffect, 0.0d, 3.0d );
                    location.subtract( n2, n3 + 0.2, n4 );
                    if( this.t >= 12.566370614359172 ) {
                        location.add( n2, n3, n4 );
                        spawn( location, particleEffect, 1.0d, 50.0d );
                        this.cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer( Main.getInstance(), 0L, 1L );
    }

    public void createHelix( Location location, double n, Particle particleEffect, int n2 ) {
        new BukkitRunnable() {
            double y = n2;
            double raggio = n;

            public void run() {
                double n = this.raggio * Math.cos( this.y );
                double n2 = this.raggio * Math.sin( this.y );
                double n3 = this.raggio * Math.sin( this.y );
                double n4 = this.raggio * Math.cos( this.y );

                spawn( new Location( location.getWorld(), location.getX() + n, location.getY() + this.y, location.getZ() + n2 ), particleEffect, 0.0d, 1.0d );
                spawn( new Location( location.getWorld(), location.getX() + n, location.getY() + this.y - 0.03, location.getZ() + n2 ), particleEffect, 0.0d, 1.0d );
                spawn( new Location( location.getWorld(), location.getX() + n3, location.getY() + this.y, location.getZ() + n4 ), particleEffect, 0.0d, 1.0d );
                spawn( new Location( location.getWorld(), location.getX() + n3, location.getY() + this.y - 0.03, location.getZ() + n4 ), particleEffect, 0.0d, 1.0d );

                this.y -= 0.2;
                if( this.raggio > 1.0 ) {
                    this.raggio -= 0.1;
                }
                if( this.y < 0.0 ) {
                    this.cancel();
                }
            }
        }.runTaskTimer( Main.getInstance(), 0L, 2L );
    }

    public void playRandomFirework( Location location ) {
        Firework firework = ( Firework ) location.getWorld().spawnEntity( location, EntityType.FIREWORK );
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        Random random = new Random();
        int n = random.nextInt( 4 ) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if( n == 1 ) {
            type = FireworkEffect.Type.BALL;
        }
        if( n == 2 ) {
            type = FireworkEffect.Type.BALL_LARGE;
        }
        if( n == 3 ) {
            type = FireworkEffect.Type.BURST;
        }
        if( n == 4 ) {
            type = FireworkEffect.Type.CREEPER;
        }
        if( n == 5 ) {
            type = FireworkEffect.Type.STAR;
        }
        fireworkMeta.addEffect( FireworkEffect.builder().flicker( random.nextBoolean() ).withColor( Color.fromBGR( Utils.RandInt( 0, 255 ), Utils.RandInt( 0, 255 ), Utils.RandInt( 0, 255 ) ) ).withFade( Color.fromBGR( Utils.RandInt( 0, 255 ), Utils.RandInt( 0, 255 ), Utils.RandInt( 0, 255 ) ) ).with( type ).trail( random.nextBoolean() ).build() );
        fireworkMeta.setPower( random.nextInt( 2 ) + 1 );
        firework.setFireworkMeta( fireworkMeta );
        new BukkitRunnable() {
            public void run() {
                firework.setTicksLived( 1 );
            }
        }.runTaskLater( Main.getInstance(), 20L );
    }
}
