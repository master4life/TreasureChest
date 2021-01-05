package de.kiyan.TreasureChest;

import de.kiyan.TreasureChest.Listener.*;
import de.kiyan.TreasureChest.api.MenuAPI;
import de.kiyan.TreasureChest.commands.TChestCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    /*
        KNOWN ISSUES:
            -You can't store entchantment books. or potions (but enchanted items works)
            -You can get the GUIDE item out of the 'Dropping Items' menu
            -Adding way too many chests could possibly cause a problematic on opening scene
            -Saving new items, resets all tier setup
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
        plg.registerEvents( new EventPlayerInteract(), this );
        plg.registerEvents( new EventPlayerMove(), this );
        plg.registerEvents( new EventPickupItem(), this );
        plg.registerEvents( new EventBlockBreak(), this );
    }

    public static Main getInstance()
    {
        return instance;
    }
}
