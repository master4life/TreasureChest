package de.kiyan.TreasureChest;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Config {
    Plugin plugin = null;

    public Config() {
        this.plugin = Main.getInstance();
    }

    public void prepareConfig() {
        plugin.saveResource( "config.yml", true );

        File file = new File( getDir() );
        if( !file.exists() )
            file.mkdir();
    }


    public void delete( String name ) {
        try {
            new File( getDir() + "/" + name + ".yml" ).delete();
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setBlocks( String name, ArrayList< String > block ) {
        File file = new File( getDir(), name + ".yml" );

        if( block == null)
            System.out.println( "NULL ");
        System.out.println( file.getAbsoluteFile() );
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration( file );


        for( String bl : block ) {
            System.out.println( bl );
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

    public void saveFile( String name ) {
        try {
            plugin.getConfig().save( new File( getDir() + "/" + name + ".yml" ) );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public ArrayList< String > listFiles( ) {
        File folder = new File( getDir() );
        File[] listOfFiles = folder.listFiles();
        ArrayList< String > arrayList = new ArrayList<>();

        for( int i = 0; i < listOfFiles.length; i++ ) {
            if( listOfFiles[ i ].isFile() ) {
                arrayList.add( listOfFiles[ i ].getName().replace( ".yml", "" ) );
            }
        }

        return arrayList;
    }

    public String getDir() {
        return plugin.getDataFolder() + "/TChests";
    }
}
