package ru.ostrov77.twist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.Listener;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;

public class TwistLst implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            final Arena arena = AM.getPlayersArena(p);
            if (arena != null) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        final Player p = (Player) e.getEntity();
        final Arena arena = AM.getPlayersArena(p);

        if (arena != null) {
            e.setDamage(0);
            p.setFallDistance(0);

            switch (arena.state) {
                case ИГРА -> {
                    //во время игры
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        arena.fall(p); //там в зрителя и тп над ареной
                    }
                }
                case СТАРТ, ФИНИШ ->
                    p.teleport(arena.zero.clone().add(0, 3, 0)); //
                default ->
                    p.teleport(p.getWorld().getSpawnLocation());
            }
        }

    }

    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onOpenInv (InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

            if ( AM.isInGame(player) ) {
                e.setCancelled(true);
                player.closeInventory();
                player.sendMessage("Инвентарь заблокирован!");
            }

    }


    @EventHandler(ignoreCancelled = true)
    public void monetPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player p = Bukkit.getPlayerExact(e.getEntity().getName());

        //if (e.getItem().getItemStack()!=null && e.getItem().getItemStack().hasItemMeta() && e.getItem().getItemStack().getItemMeta().)    
        switch (e.getItem().getItemStack().getType()) {

            case SUNFLOWER -> {
                //System.out.println("подобрал бонус level:"+e.getPlayer().getLevel());                    
                e.setCancelled(true);
                if (e.getItem().isGlowing()) {
                    return;
                }
                e.getItem().setVelocity(new Vector(0, 1.0, 0));

                Bukkit.getScheduler().scheduleSyncDelayedTask(Twist.instance, () -> {
                    e.getItem().remove();
                }, 5L);
                
                e.getItem().setGlowing(true);
                if (AM.isInGame(p)) {
                    //p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 20F, 1F);
                    //e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), "twist.bonus_pickup", 10, 1);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                    p.getWorld().playEffect(p.getLocation(), Effect.BOW_FIRE, 5);
                    p.setLevel(p.getLevel() + 1);

                }
            }

            default -> {
                e.setCancelled(true);
                e.getItem().remove();
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 9.9F);
            }

        }

    }



    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getPlayersArena(e.getPlayer()) == null) {
            return;
        }
        e.setCancelled(true);
       /* event.getItemDrop().remove();
        ItemStack droped = event.getPlayer().getItemInHand().clone();
        droped.setAmount(1);
        event.getPlayer().setItemInHand(droped);
        event.getPlayer().updateInventory();*/
    }


    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getPlayersArena(e.getPlayer()) == null) {
            return;
        }
        e.setCancelled(true);
    }




}
