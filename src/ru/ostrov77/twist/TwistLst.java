package ru.ostrov77.twist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.Listener;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.GameState;


public class TwistLst implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void PlayerQuitEvent (PlayerQuitEvent e) {
        final Arena a = AM.getArena(e.getPlayer());
        if (a!=null) {
            a.removePlayer(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            Player p = (Player) e.getEntity();
            final Arena arena = AM.getArena(p);
            if (arena != null) {
                e.setDamage(0);
                e.setCancelled(true);
            }
        }
    }
    
    
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemMerge (final ItemMergeEvent e) {
        Arena a = AM.getArenaByWorld(e.getEntity().getWorld().getName());
        if (a!=null && a.state != GameState.ОЖИДАНИЕ) {
             e.setCancelled(true);
        }
    }   
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {

        final Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);

        if (arena != null) {
            e.setDamage(0);
            p.setFallDistance(0);

            switch (arena.state) {
                
                case ОЖИДАНИЕ, СТАРТ ->
                    p.teleport(arena.arenaLobby);
                    
                case ЭКИПИРОВКА ->
                    p.teleport(arena.randomFielldLoc()); //
                    
                case ИГРА -> {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID || e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                        arena.fall(p);
                       if (arena.zero.getBlockY() - p.getLocation().getBlockY() > 5) {
                           p.teleport(arena.randomFielldLoc());
                       } 
                    }
                }
                case ФИНИШ ->
                    p.teleport(arena.randomFielldLoc());
                    
                default ->
                    p.teleport(p.getWorld().getSpawnLocation());
            }
        }

    }

    
    /*
    @EventHandler(priority = EventPriority.HIGH)
    public void onOpenInv (InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

            if ( AM.isInGame(player) ) {
                e.setCancelled(true);
                player.closeInventory();
                player.sendMessage("Инвентарь заблокирован!");
            }

    }*/


    @EventHandler(ignoreCancelled = true)
    public void monetPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player p = (Player) e.getEntity();
        final Arena arena = AM.getArena(p);
        if (arena==null) return;
        final Item i = e.getItem();
        e.setCancelled(true);
        
        switch (i.getItemStack().getType()) {

            case SUNFLOWER -> {
                if (i.isGlowing()) return;
                i.setVelocity(new Vector(0, 1.0, 0));
                Ostrov.sync( ()->i.remove(), 5);
                i.setGlowing(true);
                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                i.getWorld().playEffect(i.getLocation(), Effect.BOW_FIRE, 5);
                p.setLevel(p.getLevel() + 1);

            }

            default -> {
                i.remove();
                i.getWorld().playSound(i.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 9.9F);
            }

        }

    }



    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }


    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (ApiOstrov.isLocalBuilder(e.getPlayer()) || AM.getArenaByWorld(e.getPlayer().getWorld().getName()) == null) {
            return;
        }
        e.setCancelled(true);
    }

}
