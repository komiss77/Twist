package ru.ostrov77.twist;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.util.Vector;

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
    public void sugarPickupEventSpeed(EntityPickupItemEvent e) {
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
                Pickup_efect(e.getItem());
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

    private static void Pickup_efect(Item item) {
        item.setVelocity(new Vector(0, 1.0, 0));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Twist.instance, () -> {
            item.remove();
        }, 5L);

    }

    
    @EventHandler
    public void onHostileSpawn(CreatureSpawnEvent e) {

        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }

    }


    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().isOp() || AM.getPlayersArena(event.getPlayer()) == null) {
            return;
        }
        event.setCancelled(true);
        event.getItemDrop().remove();
        ItemStack droped = event.getPlayer().getItemInHand().clone();
        droped.setAmount(1);
        event.getPlayer().setItemInHand(droped);
        event.getPlayer().updateInventory();
    }


    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent event) {
        if (event.getOffHandItem() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void strucGrow(StructureGrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();
        if (rain) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        boolean storm = event.toThunderState();
        if (storm) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE || event.getBlock().getType() == Material.PACKED_ICE || event.getBlock().getType() == Material.SNOW || event.getBlock().getType() == Material.SNOW_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void PlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }
    }

}
