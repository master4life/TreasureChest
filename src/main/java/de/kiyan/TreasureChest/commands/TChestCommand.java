package de.kiyan.TreasureChest.commands;

import de.kiyan.TreasureChest.Messages;
import de.kiyan.TreasureChest.handle.ChestMenu;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if( !( sender instanceof Player ) ) {
            sender.sendMessage( Messages.NON_PLAYER.getMessage() );
            return false;
        }

        Player player = ( Player ) sender;

        // placeholder for geppi's rank systemapi
        if( player.getGameMode() != GameMode.CREATIVE ) {
            player.sendMessage( Messages.NO_ACCESS.getMessage( true ) );

            return false;
        }

        new ChestMenu().setupMainMenu( player ).openMenu( player );

        return false;
    }
}
