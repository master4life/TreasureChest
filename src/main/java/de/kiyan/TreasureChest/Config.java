package de.kiyan.TreasureChest;

import de.kiyan.TreasureChest.Utils.ItemStackUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Config {
    Plugin plugin;

    public Config() {
        this.plugin = Main.getInstance();
    }

    public void prepareConfig() {
        plugin.saveResource( "config.yml", false );

        File file = new File( getDir() );
        if( !file.exists() )
            file.mkdir();

        File file2 = new File( getCrashedDir() );
        if( !file2.exists() )
            file2.mkdir();

        if( getCrashedFiles() != null ) {
            restorePlace( getCrashedFiles() );
        }
    }

    public void restorePlace( HashMap< Location, BlockData > map ) {
        for( Map.Entry< Location, BlockData > entry : map.entrySet() ) {
            Location loc = entry.getKey();
            BlockData blockData = entry.getValue();

            loc.getBlock().setBlockData( blockData );
        }

        for( String files : listFiles( false ) )
        {
            File file = new File( getCrashedDir(), files + ".yml" );
            if( file.exists() )
                file.delete();
        }
        Bukkit.getConsoleSender().sendMessage( Messages.TCHEST_RESTORED.getMessage( true ) );
    }

    public HashMap< Location, BlockData > getCrashedFiles() {
        ArrayList< String > list = listFiles( false ); // False, will fetch all crashed file names without (.yml)
        if( list != null && list.size() > 0 ) {
            for( String stringFile : list ) {
                File file = new File( getCrashedDir(), stringFile + ".yml" );
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

                HashMap< Location, BlockData > blockMap = new HashMap<>();
                for( String coord : yaml.getKeys( false ) ) {
                    String[] split = coord.split( ";" );
                    Location loc = new Location( Bukkit.getWorld( split[ 0 ] ), Double.parseDouble( split[ 1 ] ), Double.parseDouble( split[ 2 ] ), Double.parseDouble( split[ 3 ] ) );
                    BlockData blockData = Bukkit.createBlockData( yaml.getString( coord ) );

                    blockMap.put( loc, blockData );
                }
                return blockMap;
            }
        }

        return null;
    }

    public void removeCashFile( Player player ) {
        File file = new File( getCrashedDir(), player.getUniqueId() + ".yml" );
        if( file.exists() ) file.delete();
    }

    public void setCrashFile( Player player, Block block, BlockState blockState ) {
        File file = new File( getCrashedDir(), player.getUniqueId() + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );
        Location loc = block.getLocation();

        yaml.set( loc.getWorld().getName() + ";"
                        + loc.getBlockX() + ";"
                        + loc.getBlockY() + ";"
                        + loc.getBlockZ(),
                blockState.getBlockData().getAsString()
        );
        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public Particle getParticle( String which ) {
        String particle = plugin.getConfig().getString( "Effect." + which );
        return Particle.valueOf( particle );
    }

    public String get( String name ) {
        return plugin.getConfig().getString( name );
    }

    public World getDesignatedWorld() {
        String sWorld = plugin.getConfig().getString( "World" );
        if( sWorld == null ) {
            sWorld = "world";
        }
        World world = Bukkit.getWorld( sWorld );
        if( world == null ) {
            world = Bukkit.getWorlds().get( 0 );
        }
        return world;
    }


    public void setTier( String name, String id, String tier ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        String item = yaml.getString( "Items." + id );
        ItemStack is = ItemStackUtils.deserialize( item );

        yaml.set( "Items." + id, ItemStackUtils.serialize( is, tier ) );

        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setItems( String name, ArrayList< ItemStack > newItems ) {
        // Reads the file
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        // Set up items
        HashMap< Integer, String > savingItems = new HashMap<>();
        ArrayList< ItemStack > oldItems = getItems( name, true );
        ArrayList< String > tierList = new ArrayList<>();

        int j = 0;
        for( ItemStack old : oldItems ) {
            try {
                tierList.add( old.getItemMeta().getLore().get( old.getItemMeta().getLore().size() - 1 ) );
            } catch( Exception e ) {
                tierList.add( "§f§lcommon" );
            }
            j++;
        }

        int i = 0;
        for( ItemStack is : newItems ) {
            i++;
            try {
                savingItems.put( i, ItemStackUtils.serialize( is, tierList.get( i - 1 ) ) ); // Generates a String of all each item "blabal;amount;blabla etc.
            } catch( IndexOutOfBoundsException e ) {
                savingItems.put( i, ItemStackUtils.serialize( is ) ); // Generates a String of all each item "blabal;amount;blabla etc.
            }
        }

        yaml.set( "Items", "" ); // Empties the Items to avoid duplicates
        for( Map.Entry< Integer, String > entry : savingItems.entrySet() ) {
            int key = entry.getKey();
            String value = entry.getValue();

            yaml.set( "Items." + String.valueOf( key ), value );
        }

        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public ArrayList< ItemStack > getItems( String name ) {
        return getItems( name, false );
    }

    public ArrayList< ItemStack > getItems( String name, boolean withTier ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        ArrayList< ItemStack > arrayList = new ArrayList<>();

        if( yaml.getConfigurationSection( "Items" ) != null ) {
            for( String index : yaml.getConfigurationSection( "Items" ).getKeys( false ) ) {
                String string = yaml.getString( "Items." + index );

                arrayList.add( ItemStackUtils.deserialize( string, withTier ) );
            }
        }
        return arrayList;
    }

    public void removeBlocks( String name ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        yaml.set( "Blocks", "" );

        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public HashMap< String, BlockData > getBlocks( String name ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        HashMap< String, BlockData > hashMap1 = new HashMap<>();
        if( yaml.getConfigurationSection( "Blocks" ) == null )
            return null;

        for( String coord : yaml.getConfigurationSection( "Blocks" ).getKeys( false ) ) {
            String block = yaml.getString( "Blocks." + coord );

            BlockData data = Bukkit.createBlockData( block );
            hashMap1.put( coord, data );
        }

        return hashMap1;
    }

    public void setBlocks( String name, HashMap< String, BlockData > block ) {
        File file = new File( getDir(), name + ".yml" );

        if( block == null )
            System.out.println( "NULL " );

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        for( Map.Entry< String, BlockData > entry : block.entrySet() ) {
            String key = entry.getKey();
            BlockData value = entry.getValue();

            yaml.set( "Blocks." + key, value.getAsString() );
        }

        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public boolean getExist( String name ) {
        return new File( getDir() + "/" + name + ".yml" ).exists();
    }

    public ArrayList< String > listFiles( Boolean TChestOrCrashed ) {
        File folder = new File( TChestOrCrashed ? getDir() : getCrashedDir() );
        File[] listOfFiles = folder.listFiles();
        ArrayList< String > arrayList = new ArrayList<>();

        for( int i = 0; i < listOfFiles.length; i++ ) {
            if( listOfFiles[ i ].isFile() ) {
                arrayList.add( listOfFiles[ i ].getName().replace( ".yml", "" ) );
            }
        }
        return arrayList;
    }

    public String[] getTierChance( String name ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );
        String[] build = new String[ 3 ];

        build[ 0 ] = yaml.getString( "Tier.common" );
        build[ 1 ] = yaml.getString( "Tier.rare" );
        build[ 2 ] = yaml.getString( "Tier.legendary" );

        return build;
    }

    public void setTierChance( String name, String type, Float chance ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );

        yaml.set( "Tier." + type, String.valueOf( chance ) );

        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setTierList( String name ) {
        File file = new File( getDir(), name + ".yml" );
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set( "Tier.common", "80.0" );
        yaml.set( "Tier.rare", "40.0" );
        yaml.set( "Tier.legendary", "20.0" );

        try {
            yaml.save( file );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String getCrashedDir() {
        return plugin.getDataFolder() + "/Backup";
    }

    public String getDir() {
        return plugin.getDataFolder() + "/TChests";
    }

    public boolean exist( String name ) {
        File folder = new File( getDir() );
        File[] listOfFiles = folder.listFiles();

        for( int i = 0; i < listOfFiles.length; i++ ) {
            if( listOfFiles[ i ].isFile() && listOfFiles[ i ].getName().replace( ".yml", "" ).equals( name ) ) {
                return true;
            }
        }
        return false;
    }

    public void saveFile( String name ) {
        try {
            plugin.getConfig().save( new File( getDir() + "/" + name + ".yml" ) );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void delete( String name ) {
        try {
            new File( getDir() + "/" + name + ".yml" ).delete();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }
}
