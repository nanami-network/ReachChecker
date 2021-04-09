package xyz.n7mn.dev.reachchecker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

class EventListener implements Listener {

    private final Plugin plugin;

    public EventListener(Plugin plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void d1 (EntityDamageByEntityEvent e){

        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

        if (entity instanceof Player && damager instanceof Player){
            new Thread(()-> {
                Player targetPlayer = (Player) entity;
                Player fromPlayer = (Player) damager;
                if (fromPlayer.getGameMode() != GameMode.CREATIVE) {
                    if (fromPlayer.getLocation().getY() == targetPlayer.getLocation().getY()) {
                        double distance = targetPlayer.getLocation().distance(fromPlayer.getLocation());
                        plugin.getLogger().info(fromPlayer.getName() + " ---> " + targetPlayer.getName() + " : " + distance);
                        if (distance >= 3.5) {

                            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                                
                                if (player.isOp() || player.hasPermission("reachchecker.op")) {

                                    player.sendMessage("" +
                                            ChatColor.YELLOW + "[ReachChecker] " + ChatColor.RESET + fromPlayer.getName() + " : " + distance
                                    );
                                }
                            }
                        }
                    }
                }
            }).start();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void d2 (EntityDamageByEntityEvent e){
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

        if (entity instanceof Player && damager instanceof Player){
            new Thread(()->{

                Player targetPlayer = (Player) entity;
                Player fromPlayer = (Player) damager;

                double distance = targetPlayer.getLocation().distance(fromPlayer.getLocation());

                plugin.getLogger().info(fromPlayer.getName() + " ---> " + targetPlayer.getName() + " : " + distance);
                if (distance >= 4.6){ //Yが一致していない場合にlocationした場合4.5008805456048036の値が出たため...

                    for (Player player : Bukkit.getServer().getOnlinePlayers()){

                        if (player.isOp() || player.hasPermission("reachchecker.op")){

                            player.sendMessage("" +
                                    ChatColor.YELLOW + "[ReachChecker] "+ ChatColor.RESET + fromPlayer.getName()+" : " + distance
                            );
                        }
                    }
                }
            }).start();
        }
    }
}