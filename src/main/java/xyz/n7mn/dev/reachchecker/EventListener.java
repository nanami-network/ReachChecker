package xyz.n7mn.dev.reachchecker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class EventListener implements Listener {
    private final Plugin plugin;
    Map<UUID, Integer> VL = new HashMap<>();

    public EventListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {

        if (e.isCancelled()) {
            return;
        }

        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

        if (entity instanceof Player && damager instanceof Player) {
            new Thread(() -> {
                Player targetPlayer = (Player) entity;
                Player fromPlayer = (Player) damager;
                if (fromPlayer.getGameMode() != GameMode.CREATIVE) {
                    if (fromPlayer.getLocation().getY() != targetPlayer.getLocation().getY()) {
                        double x = targetPlayer.getLocation().getX() - fromPlayer.getLocation().getX();
                        x = x * x;
                        double z = targetPlayer.getLocation().getZ() - fromPlayer.getLocation().getZ();
                        z = z * z;
                        double y = Math.abs(targetPlayer.getLocation().getY() - fromPlayer.getLocation().getY());
                        double distance = Math.sqrt(x + z) - (y / 7.5); //1.8: 2.5//1.12.2: 7.5
                        plugin.getLogger().info(fromPlayer.getName() + " ---> " + targetPlayer.getName() + " : " + distance);
                        if (distance >= 4.0 && 12.0 >= distance) {
                            VL.put(fromPlayer.getUniqueId(), VL.get(fromPlayer.getUniqueId()) + 1);
                            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                if (ReachChecker.map.containsKey(player.getUniqueId()) && ReachChecker.map.get(player.getUniqueId()).equals("alert.true")) {
                                    player.sendMessage("" + ChatColor.YELLOW + "[ReachChecker(A)] " + ChatColor.RESET + fromPlayer.getName() + " : " + distance + " §6§l(" + VL.get(fromPlayer.getUniqueId()) + ")");
                                }
                            }
                        }
                    }else {
                        double distance = fromPlayer.getLocation().distance(targetPlayer.getLocation());
                        plugin.getLogger().info(fromPlayer.getName() + " ---> " + targetPlayer.getName() + " : " + distance);
                        if (distance >= 3.52 && 12.0 >= distance) { //1.8: 2.5//1.12.2: 7.5
                            VL.put(fromPlayer.getUniqueId(), VL.get(fromPlayer.getUniqueId()) + 1);
                            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                if (ReachChecker.map.containsKey(player.getUniqueId()) && ReachChecker.map.get(player.getUniqueId()).equals("alert.true")) {
                                    player.sendMessage("" + ChatColor.GOLD + "[ReachChecker(B)] " + ChatColor.RESET + fromPlayer.getName() + " : " + distance + " §6§l(" + VL.get(fromPlayer.getUniqueId()) + ")");
                                }
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        VLData(e.getPlayer());
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("reachchecker.op")) {
            ReachChecker.map.put(e.getPlayer().getUniqueId(), "alert.true");
            e.getPlayer().sendMessage("§e[ReachChecker] §rアラートが§aON§rになりました (/alerts で通知を切り替え可能です)");
        }
    }

    public void VLData(Player player) {
        if (!VL.containsKey(player.getUniqueId())) {
            VL.put(player.getUniqueId(), 0);
        }
    }
}