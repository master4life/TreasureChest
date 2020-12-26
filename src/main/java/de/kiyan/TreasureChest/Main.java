package de.kiyan.TreasureChest;

import de.kiyan.TreasureChest.Listener.EventBlockInteract;
import de.kiyan.TreasureChest.Utils.MenuAPI;
import de.kiyan.TreasureChest.commands.TChestCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    /*
        TODO: Selection of an area, save the area in an file
     */

    private static Main instance;

    @Override
    public void onEnable()
    {
        instance = this;
        Bukkit.getServer().getConsoleSender().sendMessage( Messages.PLUGIN_LOAD.getMessage( true ) );
        this.getCommand( "TChest").setExecutor( new TChestCommand() );

        new Config().prepareConfig();

        PluginManager plg = Bukkit.getPluginManager( );

        plg.registerEvents( new MenuAPI( ), this );
        plg.registerEvents( new EventBlockInteract(), this );
    }

    public static Main getInstance()
    {
        return instance;
    }
}
