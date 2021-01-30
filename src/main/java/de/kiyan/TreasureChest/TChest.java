package de.kiyan.TreasureChest;

import de.kiyan.TreasureChest.Utils.Effects;
import de.kiyan.TreasureChest.Utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class TChest {
    public static ArrayList<TChest> tchestList;
    public static HashMap<Player, Location> blockedPlayers;
    private HashMap<Block, BlockData> chests;
    private HashMap<Location, FallingBlock> fallBlocks;
    private ArrayList<Item> drops;
    private Player playerWhoActivated;
    private Location center;
    private TChest tchest;
    private String selected;
    private TChestState state;
    private boolean chestPlaced;
    private double dist;

    public enum TChestState {
        WAIT, BUILDING_BLOCKS, BUILDING_CHEST, OPENING_DONE, DESTROYING, END
    }

    public TChest(Player player, String name) {
        Config config = new Config();
        if (!config.exist(name))
            return;
        if (config.getBlocks(name) == null) {
            player.sendMessage(Messages.BLOCKS_HAVENT_SET.getMessage(true));
            return;
        }

        if (blockedPlayers == null)
            blockedPlayers = new HashMap<>();

        if (tchestList == null)
            tchestList = new ArrayList<>();

        tchestList.add(this);

        this.playerWhoActivated = player;
        this.tchest = this;
        this.selected = name;
        this.chestPlaced = false;
        build();
    }

    public void build() {
        this.state = TChestState.BUILDING_BLOCKS;
        Effects effect = new Effects();
        effect.createSecondCircle(playerWhoActivated, tchest, new Config().getParticle("initiate") == null ? Particle.FLAME : new Config().getParticle("initiate"));
        for (Player broadcast : Bukkit.getOnlinePlayers()) {
            if (broadcast != playerWhoActivated)
                broadcast.sendMessage(Messages.BROADCAST.getMessage(false)
                        .replace("{player}", playerWhoActivated.getName().toUpperCase(Locale.ROOT))
                        .replace("{type}", selected.toUpperCase(Locale.ROOT)));
        }

        new BukkitRunnable() {
            int i = 3;

            public void run() {
                if (i > 0) playerWhoActivated.sendMessage(Messages.OPENING_TCHEST.getMessage(false) + " " + i);

                if (i == 0) {
                    playerWhoActivated.sendMessage(Messages.OPENING_TCHEST.getMessage(false) + " §c§lNOW");
                    // Event opening message
                    effect.FrostLordEffect(TChest.this.playerWhoActivated.getLocation(), Particle.WATER_SPLASH);
                    cancel();
                }
                i--;
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);

        Location location = this.playerWhoActivated.getLocation();
        blockedPlayers.put(this.playerWhoActivated, location);

        playerWhoActivated.setInvulnerable(true);
        this.center = new Location(location.getWorld(), location.getBlockX() + 0.5D, location.getBlockY(), location.getBlockZ() + 0.5D);
        this.drops = new ArrayList<>();
        this.fallBlocks = new HashMap<>();
        this.chests = new HashMap<>();
        HashMap<String, BlockData> hashBlocks = new Config().getBlocks(selected);
        String[] arrayBlocks = new String[hashBlocks.size()];
        int index = 0;
        this.dist = 1.0D;

        for (Map.Entry<String, BlockData> mapEntry : hashBlocks.entrySet()) {
            arrayBlocks[index] = mapEntry.getKey();
            index++;
        }

        new BukkitRunnable() {
            public void run() {
                for (String coord : arrayBlocks) {
                    String[] split = coord.split("_");
                    Location loc = new Location(TChest.this.center.getWorld(),
                            (TChest.this.center.getBlockX() + Integer.parseInt(split[0])) + 0.5D,
                            (TChest.this.center.getBlockY() + Integer.parseInt(split[1])),
                            (TChest.this.center.getBlockZ() + Integer.parseInt(split[2])) + 0.5D
                    );
                    Block block = loc.getBlock();

                    if (TChest.this.center.distance(loc) <= TChest.this.dist && !fallBlocks.containsKey(loc) && !chests.containsKey(block)) {
                        BlockData blockD = new Config().getBlocks(selected).get(coord);
                        if (blockD.getMaterial().equals(Material.CHEST) || blockD.getMaterial().equals(Material.TRAPPED_CHEST)) {
                            TChest.this.chests.put(block, blockD);
                        } else {
                            fallBlocks.put(loc, Utils.spawnFallingBlock(tchest, loc, blockD));
                            block.getWorld().playEffect(loc, Effect.STEP_SOUND, 1, 20);
                        }
                    }
                }

                TChest tChest = TChest.this;
                tChest.dist = tChest.dist + 0.5;
                if (TChest.this.dist > 6.5) {
                    TChest.this.state = TChestState.BUILDING_CHEST;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 3L * 20L, 15L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (fallBlocks.size() > 0)
                    TChest.this.fallBlocks.forEach((loc, fBlock) -> fBlock.setTicksLived(1));
                if (chestPlaced)
                    TChest.this.chests.forEach((block, blockD) -> Bukkit.getOnlinePlayers().forEach((all) -> all.sendBlockChange(block.getLocation(), blockD)));

                if (TChest.this.state == TChestState.END) {
                    TChest.this.chests.forEach((block, blockD) -> block.getState().update());
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 5L, 5L);

        new BukkitRunnable() {
            int i = 0;

            public void run() {
                if (!TChest.this.chests.isEmpty() && i < TChest.this.chests.size() && state.equals(TChestState.BUILDING_CHEST)) {
                    Block block = (Block) TChest.this.chests.keySet().toArray()[i];
                    BlockData blockData = (BlockData) TChest.this.chests.values().toArray()[i];
                    Location bloc = block.getLocation();
                    effect.playSpiral(bloc);

                    new BukkitRunnable() {
                        public void run() {
                            Utils.spawnChest(tchest, bloc, blockData);

                            block.getWorld().playSound(bloc, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                            block.setMetadata("TChest", new FixedMetadataValue(Main.getInstance(), TChest.this.getP().getName()));
                        }
                    }.runTaskLater(Main.getInstance(), 30L);

                    ++i;
                }
                if (i >= TChest.this.chests.size()) {
                    chestPlaced = true;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 8L * 20L, 50L);
        this.state = TChestState.WAIT;
        process();
    }

    public void process() {
        new BukkitRunnable() {
            int i = 0;

            public void run() {
                if (!TChest.this.chests.isEmpty() && TChest.this.chestPlaced) {
                    Block block = (Block) TChest.this.chests.keySet().toArray()[i];
                    TChest.this.openChest(block);

                    if (i + 1 == TChest.this.chests.size()) {
                        TChest.this.destroy();
                        cancel();
                    } else
                        i++;
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L * 20L, 50L);
    }

    private String getChanceType() {
        String[] tier = new Config().getTierChance(TChest.this.selected);
        double d = Double.parseDouble(String.valueOf(Utils.RandInt(0, 100)));
        if (d <= Double.parseDouble(tier[2])) {
            return "legendary";
        } else if (d <= Double.parseDouble(tier[1])) {
            return "rare";
        }
        return "common";
    }

    public void openChest(Block block) {
        Location location1 = Utils.getBlockCenter(block.getLocation());
        Location clocUP = Utils.getBlockCenterUP(block.getLocation());
        if (block.hasMetadata("TChest") & ((block.getMetadata("TChest").get(0)).asString().equals("none") ? 0 : 1) != 0) {
            block.setMetadata("TChest", new FixedMetadataValue(Main.getInstance(), "none"));
            new BukkitRunnable() {
                final ArrayList<ItemStack> items = new Config().getItems(TChest.this.selected, true);
                String line = "";

                @Override
                public void run() {
                    Effects effect = new Effects();
                    Config config = new Config();
                    effect.spawn(clocUP, config.getParticle("openchest") == null ? Particle.FLAME : config.getParticle("openchest"), 0.1f, 0.1f, 0.1f, 0.05f, 30.0d);
                    if (this.items.size() >= 1) {
                        String str = getChanceType();

                        ArrayList<ItemStack> arrayList = new ArrayList<>();
                        ItemStack itemStack;

                        for (ItemStack itemStack1 : this.items) {
                            ItemMeta itemMeta1 = itemStack1.getItemMeta();
                            if (itemMeta1 == null) break;
                            List<String> list1 = itemMeta1.getLore();
                            if (list1 == null) break;

                            for (String lore : list1) {
                                if (lore.startsWith("§") && lore.contains(str)) {
                                    arrayList.add(itemStack1);
                                }
                            }
                        }
                        if (arrayList.isEmpty()) {
                            itemStack = (this.items.get(Utils.RandInt(0, this.items.size() - 1))).clone();
                        } else {
                            itemStack = (arrayList.get(Utils.RandInt(0, arrayList.size() - 1))).clone();
                        }
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        List<String> list = itemMeta.getLore();
                        if (list == null)
                            list = new ArrayList<>();

                        list.add(ChatColor.BLUE + "Item: " + Utils.RandInt(0, 1000));
                        itemMeta.setLore(list);
                        itemStack.setItemMeta(itemMeta);
                        if (location1.getWorld() != null)
                            location1.getWorld().playSound(location1, Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);

                        effect.chestAnimation(location1);
                        Item item = TChest.this.playerWhoActivated.getWorld().dropItem(clocUP.subtract(0, 0.5, 0), itemStack);
                        item.setMetadata("TChest", new FixedMetadataValue(Main.getInstance(), "demo"));
                        item.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
                        item.setPickupDelay(4000);
                        item.setGravity(false);
                        TChest.this.drops.add(item);

                        if (itemMeta.hasDisplayName())
                            this.line = "§a§l" + itemStack.getAmount() + " " + itemMeta.getDisplayName();
                        else
                            this.line = ("§a§l" + itemStack.getAmount() + " " + itemMeta.toString()).replace("_", " ");

                        for (String str1 : list) {
                            if (str1.contains("common")) {
                                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                                    for (Entity enitity : TChest.this.playerWhoActivated.getNearbyEntities(10, 10, 10))
                                        if (enitity instanceof Player)
                                            enitity.sendMessage(Messages.ANNOUNCEMENT.getMessage(false)
                                                    .replace("{player}", TChest.this.playerWhoActivated.getName().toUpperCase(Locale.ROOT))
                                                    .replace("{tier}", "§f§lCOMMON")
                                                    .replace("{item}", "§f" + item.getName().toUpperCase(Locale.ROOT)));
                                    effect.createHologram(TChest.this.playerWhoActivated, clocUP, "§f§lCOMMON", item.getItemStack().getAmount() + "x " + "§f§l" + item.getName().replace("§f", "§f§l"));
                                }, 15L);
                                continue;
                            }
                            if (str1.contains("rare")) {
                                new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        if (i == 0) {
                                            effect.createHologram(TChest.this.playerWhoActivated, clocUP, "§4§lRARE", item.getItemStack().getAmount() + "x " + "§4§l" + item.getName().replace("§f", "§4§l"));
                                            Bukkit.broadcastMessage(Messages.ANNOUNCEMENT.getMessage(false)
                                                    .replace("{player}", TChest.this.playerWhoActivated.getName().toUpperCase(Locale.ROOT))
                                                    .replace("{tier}", "§4§lRARE")
                                                    .replace("{item}", "§4" + item.getName().toUpperCase(Locale.ROOT)));
                                        }

                                        effect.spawn(clocUP, config.getParticle("rare") == null ? Particle.CRIT : config.getParticle("rare"), 0.3f, 0.3f, 0.3f, 0.3f, 30.0d);
                                        i++;
                                        if (i == 2)
                                            cancel();
                                    }
                                }.runTaskTimer(Main.getInstance(), 0L, 15L);
                                effect.createCircle(clocUP, 2, config.getParticle("firstRare") == null ? Particle.FLAME : config.getParticle("firstRare"));
                                continue;
                            }
                            if (str1.contains("legendary")) {
                                new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        if (i == 0) {
                                            effect.createHologram(TChest.this.playerWhoActivated, clocUP, "§e§lLEGENDARY", item.getItemStack().getAmount() + "x " + "§4§l" + item.getName().replace("§f", "§4§l"));
                                            Bukkit.broadcastMessage(Messages.ANNOUNCEMENT.getMessage(false)
                                                    .replace("{player}", TChest.this.playerWhoActivated.getName().toUpperCase(Locale.ROOT))
                                                    .replace("{tier}", "§e§lLEGENDARY")
                                                    .replace("{item}", "§e" + item.getName().toUpperCase(Locale.ROOT)));
                                        }
                                        effect.spawn(clocUP, config.getParticle("legendary") == null ? Particle.FLAME : config.getParticle("legendary"), 0.3f, 0.3f, 0.3f, 0.3f, 30.0d);
                                        i++;
                                        if (i == 2)
                                            cancel();
                                    }
                                }.runTaskTimer(Main.getInstance(), 0L, 15L);
                                effect.createCircle(clocUP, 2, config.getParticle("firstLegendary") == null ? Particle.TOTEM : config.getParticle("firstLegendary"));
                                new BukkitRunnable() {
                                    int i = 0;

                                    public void run() {
                                        new Effects().playRandomFirework(clocUP);
                                        i++;
                                        if (this.i == 2)
                                            cancel();
                                    }
                                }.runTaskTimer(Main.getInstance(), 0L, 20L);
                            }
                        }
                    }

                }
            }.runTaskLater(Main.getInstance(), 10L);
        }
    }

    public void destroy() {
        new BukkitRunnable() {
            int i = 0;

            public void run() {
                TChest.this.state = TChest.TChestState.DESTROYING;

                Object[] objLocation = TChest.this.fallBlocks.keySet().toArray();
                Object[] arrayOfObject = TChest.this.fallBlocks.values().toArray();

                for (i = 0; i < arrayOfObject.length; i++) {
                    FallingBlock block = (FallingBlock) arrayOfObject[i];
                    Location location = (Location) objLocation[i];

                    if (location.distance(Utils.getBlockCenter(TChest.this.center.getBlock().getLocation())) >= TChest.this.dist && TChest.this.fallBlocks.containsKey(location)) {
                        block.getWorld().playEffect(location, Effect.STEP_SOUND, 1);
                        block.remove();
                        location.getBlock().getState().update();
                        fallBlocks.remove(location);
                        location.getWorld().getNearbyEntities(location, 0.1, 0.1, 0.1).forEach((ent) -> {
                            if (ent.hasMetadata("TChest") && ent.getMetadata("TChest").get(0).asString().equalsIgnoreCase(playerWhoActivated.getName())) {
                                ent.remove();
                            }
                        });

                        for (Entity ent : Bukkit.getWorld(playerWhoActivated.getWorld().getName()).getEntities()) {
                            if (ent.hasMetadata("TChest") && ent.getMetadata("TChest").get(0).asString().equalsIgnoreCase(playerWhoActivated.getName())) {
                                if (ent instanceof ArmorStand) {
                                    LivingEntity entity = (LivingEntity) ent;
                                    entity.setHealth(0.0D);
                                }
                                if (ent instanceof MagmaCube) {
                                    LivingEntity entity = (LivingEntity) ent;
                                    entity.remove();
                                }
                            }
                        }
                    }
                }
                TChest.this.dist = TChest.this.dist - 0.5D;
                if (TChest.this.dist == 2.0D) { // Prepare Items to drop to the player.
                    Iterator<Item> iterItem = TChest.this.drops.iterator();
                    while (iterItem.hasNext()) {
                        Item item = iterItem.next();
                        if (TChest.this.playerWhoActivated.getInventory().firstEmpty() != -1) {
                            ItemStack itemStack = item.getItemStack();
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            List<String> list = itemMeta.getLore();

                            Iterator<String> iterator1 = list.iterator();
                            while (iterator1.hasNext()) { // Removes lore content with Item, Tier type out of the lore
                                String line = iterator1.next();
                                if (line.contains("Item:") || line.contains("common") || line.contains("rare") || line.contains("legendary"))
                                    iterator1.remove();
                            }
                            itemMeta.setLore(list);
                            itemStack.setItemMeta(itemMeta);
                            item.setItemStack(itemStack);
                            TChest.this.playerWhoActivated.getInventory().addItem(itemStack);
                            iterItem.remove();
                            item.remove();
                            continue;
                        }
                        item.setPickupDelay(0);
                        item.teleport(TChest.this.playerWhoActivated.getLocation().add(0.0, 0.5, 0.0));
                        item.setMetadata("TChest", new FixedMetadataValue(Main.getInstance(), TChest.this.getP().getName()));
                        TChest.this.playerWhoActivated.sendMessage(Messages.FULL_INVENTORY.getMessage(true));
                    }
                    TChest.this.playerWhoActivated.setInvulnerable(false);
                    TChest.this.playerWhoActivated.updateInventory();
                    TChest.this.drops.clear();

                } else if (TChest.this.dist <= 0.0D) {
                    TChest.blockedPlayers.remove(TChest.this.getP());
                    TChest.tchestList.remove(TChest.this.getTChest());

                    TChest.this.fallBlocks.clear();

                    TChest.this.tchest.setState(TChest.TChestState.END);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 15L);
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

    public void setState(TChestState chestState) {
        this.state = chestState;
    }

    public Player getP() {
        return this.playerWhoActivated;
    }

    public HashMap<Location, FallingBlock> getFallBlocks() {
        return fallBlocks;
    }

    // TChest instance
    public TChest getTChest() {
        return this.tchest;
    }

    public void setTChest(TChest tchest) {
        this.tchest = tchest;
    }

    //  Hashmap contains the actually item  and the drop chance
    public ArrayList<Item> getDrops() {
        return this.drops;
    }
}
