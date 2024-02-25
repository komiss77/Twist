package ru.ostrov77.twist;

import ru.ostrov77.minigames.UniversalListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.block.data.CraftBlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import ru.komiss77.ApiOstrov;
import ru.komiss77.enums.Game;
import ru.komiss77.enums.GameState;
import ru.komiss77.enums.Stat;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.scoreboard.SideBar;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.ParticlePlay;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.minigames.IArena;
import ru.ostrov77.minigames.MG;

public class Arena implements IArena {

    private final String arenaName;
    public Location arenaLobby;
    public Location zero;
    private Material mat = Material.WHITE_WOOL;
    private final byte size_x;
    private final byte size_z;
    private final byte down;
    private byte show;
    private byte difficulty;
    private byte maxRound;

    private BukkitTask task, PreStart1, GameTimer1, EndGame1, DysplayColor,RemoveFloor;
    private int cdCounter, prestart, playtime, ending;

    private boolean canreset;

    private byte round;
    private DyeColor nextColor;

    private boolean displayColor, removeFloor;

    public Map<String, Integer> players = new HashMap<>();
    public Set<String> looser = new HashSet<>();
    private final Map<Integer, DyeColor> colormap = new HashMap<>();

    private static Random random;
    public GameState state; //ОЖИДАНИЕ СТАРТ ЭКИПИРОВКА ИГРА ФИНИШ

    private final net.minecraft.server.level.WorldServer nmsWorldServer;
    private final net.minecraft.world.level.block.state.IBlockData ibdDataAir;
    private static final BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition(0, 0, 0);

    private static final List <PotionEffect> pot = List.of(
            new PotionEffect(PotionEffectType.INVISIBILITY, 60, 10),
            new PotionEffect(PotionEffectType.CONFUSION, 60, 10),
            new PotionEffect(PotionEffectType.SLOW, 60, 10)
    );
    
    
    
    public Arena(String name,
            Location zero,
            Location arenaLobby,
            String material,
            byte size_x, byte size_z, byte down, byte show,
            byte difficulty, byte maxRound, byte minPlayers, byte playersForForcestart) {

        this.arenaName = name;
        //try {
        this.arenaLobby = arenaLobby;
        this.zero = zero;
        //} catch (NullPointerException ex) {}

        //switch (material) {
        //    case "clay" : mat = Material.WHITE_CONCRETE;  this.mode="clay"; break;
        //    case "glass": mat = Material.WHITE_STAINED_GLASS; this.mode="glass"; break;
        //    default     : mat = Material.WHITE_WOOL;          this.mode="wool"; break;
        // }
        if (size_x >= 2 && size_x <= 64) {
            this.size_x = size_x;
        } else {
            this.size_x = 16;
        }
        if (size_z >= 2 && size_z <= 64) {
            this.size_z = size_z;
        } else {
            this.size_z = 16;
        }
        if (down >= 1 && down <= 300) {
            this.down = down;
        } else {
            this.down = 89;
        }
        if (show >= 5 && show <= 100) {
            this.show = show;
        } else {
            this.show = 25;
        }
        if (difficulty >= 1 && difficulty <= 3) {
            this.difficulty = difficulty;
        } else {
            this.difficulty = 1;
        }
        if (maxRound >= 1 && maxRound <= 64) {
            this.maxRound = maxRound;
        } else {
            this.maxRound = 10;
        }
        //if (minPlayers >= 2 && minPlayers <= 64) {
        //    this.minPlayers = minPlayers;
       // } else {
       //     this.minPlayers = 2;
      //  }
       // if (playersForForcestart >= 2 && playersForForcestart < minPlayers) {
      //      this.playersForForcestart = playersForForcestart;
      //  } else {
      //      this.playersForForcestart = 12;
     //   }

        nmsWorldServer = ((CraftWorld) arenaLobby.getWorld()).getHandle();
        ibdDataAir = ((CraftBlockData) Material.AIR.createBlockData()).getState();//= net.minecraft.world.level.block.Block.a( 0 );
        //ibdDataDown = ((CraftBlockData)Material.GLOWSTONE.createBlockData()).getState();//= net.minecraft.world.level.block.Block.a( this.down );
//System.out.println("Создана арена "+name+"   размер "+this.size_x+"*"+this.size_z+
        //" diff "+this.difficulty+" раунды "+this.maxRound+" игроки/быстро "+this.minPlayers+"/"+this.playersForForcestart);
        //if (AM.ArenaExist(name)) return; //не создаём дубль!!

        cdCounter = 40; //ожид в лобби арены
        prestart = 7; //ожид на арене
        ending = 7; //салюты,награждения
        playtime = 0;

        canreset = true;

        round = 1;
        nextColor = TCUtils.randomDyeColor();//DyeColor.BLACK;
        displayColor = true;
        removeFloor = false;
        random = new Random();

        // no_mat = new ItemStack ( Material.STONE_BUTTON, 1);
        // ItemMeta m = no_mat.getItemMeta();
        //  m.setDisplayName(  "§8<<<" );
        //  no_mat.setItemMeta(m);
        GenerateNewFloor();

        state = GameState.ОЖИДАНИЕ;
        Twist.sendBsignMysql(name, state.displayColor + state.name(), "", GameState.ОЖИДАНИЕ);
    }

    public void resetGame() {

        canreset = false;
        if (task != null) {
            task.cancel();
        }
        if (DysplayColor != null) {
            DysplayColor.cancel();
        }
        if (RemoveFloor != null) {
            RemoveFloor.cancel();
        }

        arenaLobby.getWorld().getPlayers().stream().forEach( p -> {
            MG.lobbyJoin(p);
        });


        arenaLobby.getWorld().getEntities().stream().forEach( e -> {
            if (e.getType() != EntityType.PLAYER) {
                e.remove();
            }
        });

        players.clear();
        looser.clear();

        round = 1;

        cdCounter = 40;
        prestart = 7;
        ending = 10;
        playtime = 0;

        GenerateNewFloor();//BackFloor();

        nextColor = TCUtils.randomDyeColor();//DyeColor.BLACK;

        displayColor = true;
        removeFloor = false;

        state = GameState.ОЖИДАНИЕ;
        canreset = true;
        Twist.sendBsignMysql(arenaName, state.displayColor + state.name(), "", GameState.ОЖИДАНИЕ);
    }

    
    public void startCountdown() {                            //ожидание в лобби
        if (state != GameState.ОЖИДАНИЕ) {
            return;
        }
        state = GameState.СТАРТ;

        task = (new BukkitRunnable() {
            @Override
            public void run() {

                if (cdCounter == 0) {
                    cdCounter = 40;
                    this.cancel();
                    PrepareToStart();

                } else if (cdCounter > 0) {
                    --cdCounter;
                    Twist.sendBsignChanel(arenaName, players.size(),
                            "§1Твистеры: " + players.size(),
                            state.displayColor + state.name() + " §4" + cdCounter, GameState.СТАРТ);

                    Oplayer op;
                    for (Player p : getPlayers()) {
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle("§6До старта: §b"+(cdCounter+7));
                        if (cdCounter <= 5 && cdCounter > 0) {
                            p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5.0F, 5.0F);
                        }
                    }

                }

            }
        }).runTaskTimer(Twist.GetInstance(), 0L, 20L);
    }

    
    public void ForceStart(Player p) {
        if (state!=GameState.СТАРТ) {
            p.sendMessage("§cYou can't start an arena - arena must have state СТАРТ!");
            return;
        }
        if (task != null && cdCounter > 3) {
            cdCounter = 3;
        }
        p.sendMessage("§bВремя до старта уменьшено");
    }

    
    public void PrepareToStart() {
        if (state != GameState.СТАРТ) {
            return;
        }
        state = GameState.ЭКИПИРОВКА;
        if (task != null) {
            task.cancel();
        }

        getPlayers().stream().forEach( p -> {  //всех игроков в мире добавля в список и на арену  
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1));
            p.getInventory().clear();
            p.teleport(zero.clone().add((4 + random.nextInt(this.size_x - 2) * 4), 1, (4 + random.nextInt(this.size_z - 2) * 4)));
            p.playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2, 2);
            p.setWalkSpeed((float) 0.3);
        });

        task = (new BukkitRunnable() {         //тут уже таймер с игроками
            @Override
            public void run() {

                if (players.isEmpty() && canreset) {
                    resetGame();
                }

                if (prestart == 0) {
                    prestart = 7;
                    this.cancel();
                    GameProgress();

                } else {
                    Oplayer op;
                    for (Player p : getPlayers()) {
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle("§6До старта: §b"+prestart);
                        ApiOstrov.sendActionBarDirect(p, "§aТвист заражается... Осталось §b" + prestart + " §aсек.!");
                    }
                    --prestart;
                }

            }
        }).runTaskTimer(Twist.GetInstance(), 0L, 20L);
    }

    
    
    public void GameProgress() {
        if (state != GameState.ЭКИПИРОВКА) {
            return;
        }
        state = GameState.ИГРА;
        if (task != null) {
            task.cancel();
        }

        Oplayer op;
        for (Player p : getPlayers()) {
            op = PM.getOplayer(p);
            SideBar sb = op.score.getSideBar().setTitle("§6Раунд: §b"+round);
            for (String n : players.keySet()) {
                sb.add(n, TCUtils.toChat(nextColor)+n+" §e0 §f/ §c0");
            }
            sb.build();
            ApiOstrov.sendActionBarDirect(p, "§6ТВИСТ! §aТВИСТ! §bТВИСТ!");
            p.playSound(p.getLocation(), Sound.ENTITY_CAT_AMBIENT, 2, 2);
        }
        
        displayColor = true;
        removeFloor = false;
        
        task = (new BukkitRunnable() {
            @Override
            public void run() {
                final List<Player> list = getPlayers();

                if (displayColor) {
                    if (RemoveFloor != null) {
                        RemoveFloor.cancel();
                    }
                    showNextColor();                                             //показываем инфо
                    Bonus_spawn();
                    final String nextColorPref = TCUtils.toChat(nextColor);
                    Oplayer op;
                    for (Player p : list) {
                        if (looser.remove(p.getName())) {
                            p.teleport(zero.clone().add((4 + random.nextInt(size_x - 2) * 4), 1, (4 + random.nextInt(size_z - 2) * 4)));
                            p.getActivePotionEffects().stream().forEach((effect) -> {
                                p.removePotionEffect(effect.getType());
                            });
                            //p.setGameMode(GameMode.ADVENTURE);
                        }
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle("§6Раунд: §b"+round);
                        for (Player p1 : list) {
//Ostrov.log("p1="+p1.getName()+" players.get="+players.get(p1.getName()));
                            op.score.getSideBar().update(p1.getName(), nextColorPref+p1.getName()+" §e"+p1.getLevel()+" §f/ "+"§c"+players.get(p1.getName()));
                        }  
                    }


                } else if (removeFloor) {
                    if (DysplayColor != null) {
                        DysplayColor.cancel();
                    }
                    MustStayOne();                                              //удаляем все вроме текущего цвета
                    round++;                                                    //эта же функция возвращает новый пол через 3 сек
                    Twist.sendBsignChanel(arenaName, players.size(),
                            state.displayColor + state.name(),
                            "§7Раунд: §b§l" + round + "§7/§b§l" + maxRound, state);
                }

                //тут тикает только во время раундов!!
                for (Player p : list) {
                    if (zero.getBlockY() - p.getLocation().getBlockY() >= 3) { //ниже полотна на 3 и более блока - упал
                        fall(p);
                    }
                }

                //this.show - ( this.difficulty * this.round )
                if (players.isEmpty() || playtime > maxRound * show + 20 && canreset) {
                    resetGame();
                } else if (round >= maxRound) { //последний раунд и есть выжившие - победитель
                    this.cancel();
                    endGame();
                }

                playtime++;

            }
        }).runTaskTimer(Twist.GetInstance(), 0L, 20L);

    }


    
    public void fall(final Player p) {
        if (looser.add(p.getName())) {
            players.replace(p.getName(), players.get(p.getName())+1);
//Ostrov.log("fall players.get=");
            p.addPotionEffects(pot);
            ParticlePlay.deathEffect(p, false);
        }
    }

    private void Bonus_spawn() {

        int ammount = 0;
        for (Entity e : arenaLobby.getWorld().getEntities()) {
            if (e.getType() != EntityType.PLAYER && e.getTicksLived() > 300) {
                e.remove();
            }
            if (e.getType() == EntityType.DROPPED_ITEM) {
                ammount++;
            }
        }
        for (int i = ammount; i < (size_x * size_z / 8); i++) {
            final ItemStack is = new ItemStack(Material.SUNFLOWER, 1);// AM.bonus.clone();
            final ItemMeta im = is.getItemMeta();
            im.setDisplayName(String.valueOf(random.nextInt(999)));
            is.setItemMeta(im);
            Item item = arenaLobby.getWorld().dropItem(zero.clone().add((4 + random.nextInt(size_x - 2) * 4), 1, (4 + random.nextInt(size_z - 2) * 4)), is);
            item.setVelocity(new Vector(0, 0, 0));
            item.setPickupDelay(1);
            item.setGravity(false);
        }

    }

    
    
    public void endGame() {
        if (state != GameState.ИГРА) {
            return;
        }
        state = GameState.ФИНИШ;
        if (task != null) {
            task.cancel();
        }
        if (DysplayColor != null) {
            DysplayColor.cancel();
        }
        if (RemoveFloor != null) {
            RemoveFloor.cancel();
        }

        if (!players.isEmpty()) {
            SendMessage("");
            SendMessage("");
            SendMessage("§fПобедители: ");

            getPlayers().stream().forEach((win) -> {
                win.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 150, 0));
                win.getWorld().playSound(win.getLocation(), "twist.win", 10, 1);
                win.getWorld().playSound(win.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
                firework(win);
                ApiOstrov.addStat(win, Stat.TW_game);
                ApiOstrov.addStat(win, Stat.TW_win);
                //for (int m=0; m<win.getLevel();m++) {
                ApiOstrov.addStat(win, Stat.TW_gold, win.getLevel());
                //}
                win.setLevel(0);
            });
        }

        task = (new BukkitRunnable() {
            @Override
            public void run() {
                if (ending <= 0) {
                    this.cancel();
                    resetGame();
                } else {
                    for (Player p : getPlayers()) {
                        UniversalListener.spawnRandomFirework(p.getLocation());
                    }
                }
                --ending;
            }
        }).runTaskTimer(Twist.GetInstance(), 0L, 20L);

        Twist.sendBsignChanel(getName(), players.size(), "§1 - / -", state.displayColor + state.name(), state);
    }


    
    
    
    
    public void showNextColor() {                           //показать новый цвет, звуки, таймер ->> разрешение на стирание 

        nextColor = GenRandColor(nextColor);//new_col; //приготовить следующий цвет

        displayColor = false;
        removeFloor = false;
//Ostrov.log("showNextColor "+nextColor+" "+TCUtils.dyeDisplayName(nextColor));
        ItemStack item = new ItemStack(mat, 1);
        item = TCUtils.changeColor(item, nextColor);
        ItemMeta m = item.getItemMeta();
        m.displayName(Component.text(TCUtils.dyeDisplayName(nextColor)));
        item.setItemMeta(m);

        for (Player p : getPlayers()) {
            for (byte i = 0; i < 9; i++) {
                p.getInventory().setItem(i, item);
                p.updateInventory();
            }
        }

        int sw = this.show - (this.difficulty * this.round);
        if (sw < 5) {
            sw = 5;
        } else if (sw > 80) {
            sw = 80;
        }

        DysplayColor = (new BukkitRunnable() {

            byte i = 8;

            @Override
            public void run() {

                getPlayers().stream().forEach( p -> {

                    p.getInventory().setItem(i, ItemUtils.air);
                    p.updateInventory();

                    switch (i) {
                        case 8:
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                        case 5:
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                        case 2:
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                    }

                });

                i--;
                if (i < 0) {
                    displayColor = false;
                    removeFloor = true;
                    this.cancel();
                }

            }
        }).runTaskTimer(Twist.GetInstance(), 20L, sw);

    }

// ------------------------------------  удалить квадраты/ вернуть квадраты ------------------------------------------------
    private void MustStayOne() {                     //удал все цвета, кроме текущего
        displayColor = false;
        removeFloor = false;
        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {

                if (colormap.get(x * size_x + z) != nextColor) {
                    FillPlotAir(x, z);
                }

            }
        }

        RemoveFloor = (new BukkitRunnable() {
            @Override
            public void run() {
                //showNextColor();
                BackFloor();
                displayColor = true;
                removeFloor = false;
            }
        }).runTaskLater(Twist.GetInstance(), 60);

    }

    private void BackFloor() {                     //вернуть пол

        DyeColor previos;
        DyeColor newColor;
        int idx;

        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {
                idx = x * size_x + z;

                previos = colormap.get(idx);
                if (previos != nextColor) { //плот менялся на воздух
                    newColor = GenRandColor(previos); //рандомный цает, отличный от старого
                    colormap.put(idx, newColor);
                    FillPlotMat(x, z, newColor);
                }

            }
        }

        
    }

    private void FillPlotMat(byte plot_x, byte plot_z, DyeColor color) {
        int x = zero.getBlockX() + plot_x * 4;
        int y = zero.getBlockY();
        int z = zero.getBlockZ() + plot_z * 4;
        mat = TCUtils.changeColor(mat, color);//Material.valueOf(color.toString()+"_"+mat_base);
        final BlockData data = mat.createBlockData();
        final net.minecraft.world.level.block.state.IBlockData ibdColoredWool = ((CraftBlockData) data).getState(); //((CraftBlock)block).getNMS();
        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                mutableBlockPosition.d(x + x_, y, z + z_);
                CraftBlock.setTypeAndData(nmsWorldServer, mutableBlockPosition, ibdDataAir, ibdColoredWool, false);
            }
        }
    }

    private void FillPlotAir(byte plot_x, byte plot_z) {
        int x = zero.getBlockX() + plot_x * 4;
        int y = zero.getBlockY();
        int z = zero.getBlockZ() + plot_z * 4;
        final net.minecraft.world.level.block.state.IBlockData ibdDataPrevios = nmsWorldServer.a_(mutableBlockPosition);
        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                mutableBlockPosition.d(x + x_, y, z + z_);
                CraftBlock.setTypeAndData(nmsWorldServer, mutableBlockPosition, ibdDataPrevios, ibdDataAir, false);
            }
        }

    }

// ------------------------------------  Генерация ------------------------------------------------
    private void GenerateNewFloor() {
        colormap.clear();

        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {

                LoadChunk(x, z);          //!!! создание карты чанков для обновления и их загрузка !!!

                DyeColor col = GenRandColor(nextColor); //следущий цвет должен отличаться
                nextColor = col;
                colormap.put(x * size_x + z, col);
//Ostrov.log("generate idx="+(x * size_x + z)+" col="+col);
                FillPlotMat(x, z, col);  //заполняем плоты случ цветом
                FillDown(x, z);          //заполняем низ

            }
        }

    }

    private void LoadChunk(byte plot_x, byte plot_z) {

        int x = zero.getBlockX() + plot_x * 4;  //координата блока Х на углу плота
        int z = zero.getBlockZ() + plot_z * 4; //координата блока Z на углу плота

        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {

                final Chunk ch = zero.getWorld().getChunkAt((x + x_) >> 4, (z + z_) >> 4);      //берёт стандартный чанк на этой же координате
                //String t = String.valueOf(ch.getX()) + "x" + String.valueOf(ch.getZ());

                //if ( !this.chunksHash.contains(t) ) {
                //       this.chunksHash.add(t);
                //   this.chunks.add(ch.getX()+":"+ch.getZ());
                if (!ch.isLoaded()) {
                    ch.load();
                }
                // }

            }
        }

    }

    public void ResetFloor() {

        for (byte x = 0; x < this.size_x; x++) {
            for (byte z = 0; z < this.size_z; z++) {
                FillPlotAir(x, z);
                FillDownAir(x, z);
            }
        }

        //UpdateChunks();
        //this.chunks.clear();
        // this.chunksHash.clear();
        this.colormap.clear();

    }

    private void FillDown(byte plot_x, byte plot_z) {

        int x = zero.getBlockX() + plot_x * 4;  //координата блока Х на углу плота
        int y = zero.getBlockY() - 5;           //координата высоты (для пола -5)
        int z = zero.getBlockZ() + plot_z * 4; //координата блока Z на углу плота

        final net.minecraft.world.level.block.state.IBlockData ibdDataDown = ((CraftBlockData) Material.GLOWSTONE.createBlockData()).getState();
        //net.minecraft.server.v1_16_R3.World world = ( (CraftWorld) arenaLobby.getWorld() ).getHandle(); //берёт NMS мир
        //IBlockData ibd = net.minecraft.server.v1_16_R3.Block.getByCombinedId( this.down );     //создаёт ibd код для GLOWSTONE
        final net.minecraft.world.level.block.state.IBlockData ibdDataPrevios = nmsWorldServer.a_(mutableBlockPosition);

        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                //net.minecraft.server.v1_16_R3.Chunk c = c_world.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 );   //берёт NMS чанк
                //nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).a( new BlockPosition( (x+x_), y, (z+z_) ) , ibdDataDown );                                    //вносит в него ibd по blockposition
                //nmsWorldServer.d( (x+x_) >> 4, (z+z_) >> 4 ).setType(new net.minecraft.core.BlockPosition( (x+x_), y, (z+z_) ) , ibdDataDown , false, false );                                    //вносит в него ibd по blockposition
                //net.minecraft.world.level.chunk.Chunk chunk = nmsWorldServer.d( (x+x_) >> 4, (z+z_) >> 4 );
                //chunk.setBlockState(new net.minecraft.core.BlockPosition( (x+x_), y, (z+z_) ) , ibdDataDown , false, false );                                    //вносит в него ibd по blockposition
                mutableBlockPosition.d(x + x_, y, z + z_);
                CraftBlock.setTypeAndData(nmsWorldServer, mutableBlockPosition, ibdDataPrevios, ibdDataDown, false);
            }
        }

    }

    private void FillDownAir(byte plot_x, byte plot_z) {

        int x = this.zero.getBlockX() + plot_x * 4;
        int y = this.zero.getBlockY() - 5;
        int z = this.zero.getBlockZ() + plot_z * 4;

        //net.minecraft.server.v1_16_R3.World world = ( (CraftWorld) arenaLobby.getWorld() ).getHandle(); //берёт NMS мир
        //IBlockData ibd = net.minecraft.server.v1_16_R3.Block.getByCombinedId( Material.AIR.getId() );     //создаёт ibd код для GLOWSTONE
        for (byte x_ = 0; x_ < 4; x_++) {
            for (byte z_ = 0; z_ < 4; z_++) {
                //net.minecraft.server.v1_16_R3.Chunk c = c_world.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 );   //берёт NMS чанк
                //nmsWorldServer.getChunkAt( (x+x_) >> 4, (z+z_) >> 4 ).a( new BlockPosition( (x+x_), y, (z+z_) ) , ibdDataAir );                                    //вносит в него ibd по blockposition
                net.minecraft.world.level.chunk.Chunk chunk = nmsWorldServer.d((x + x_) >> 4, (z + z_) >> 4);
                chunk.setBlockState(new net.minecraft.core.BlockPosition((x + x_), y, (z + z_)), ibdDataAir, false, false);                                    //вносит в него ibd по blockposition
            }
        }

    }
// -----------------------------------------------------------------------------------------

    
    
    
    
    
    
    
    
// xxxxxxxxxxxxxxxxxxxxxxxxxxx  Обработчик игроков xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addPlayers(Player p) {
        if (state == GameState.ОЖИДАНИЕ || state == GameState.СТАРТ) {
            p.teleport(getLobby());
            players.put(p.getName(), 0);
            if (players.size()==1) {
                startCountdown();
            } else {
                int cd = cdCounter/players.size();
                if (cd < cdCounter) {
                   cdCounter = cd;
                   SendAB("§2§lВремя до старта игры уменьшено!");
                }
            }

            //p.getInventory().clear();
            p.getInventory().setItem(0, ItemUtils.air);
            p.getInventory().setItem(7, ItemUtils.air);
            MG.leaveArena.giveForce(p);//p.getInventory().setItem(8, UniversalListener.leaveArena.clone());
            //p.updateInventory();
            //Signs.SignsUpdate(name, Messages.GetMsg("signs_line_3_prefix")+ players.size(), getStateAsString(), "§1 - / -" );
            Twist.sendBsignChanel(arenaName, players.size(), "§1Твистеры: " + players.size(), state.displayColor + state.name(), GameState.ОЖИДАНИЕ);
            //if (players.size() < minPlayers) {
            //    SendAB("§6Для старта нужно еще §b" + (minPlayers - players.size()) + " §6чел.!");
           // }
            //if (players.size() >= minPlayers) {
           //     startCountdown();
            //}
            PM.getOplayer(p).tabSuffix(" §5"+arenaName, p);
        }
    }

    public void PlayerExit(Player p) {

        if (players.remove(p.getName()) != null) {
            if (players.isEmpty()) {
                if (task!=null) {
                    resetGame();
                } else {
                    Twist.sendBsignChanel(arenaName, players.size(), "§1Твистеры: " + players.size(), state.displayColor + state.name(), state);
                }
            } else {
                Oplayer op;
                for (Player pl : getPlayers()) {
                    op = PM.getOplayer(pl);
                    op.score.getSideBar().remove(p.getName());
                }
                Twist.sendBsignChanel(arenaName, players.size(), state.displayColor + state.name(), "§7Раунд: §b§l" + round + "§7/§b§l" + maxRound, state);
            }
        }
        
      /*  if (IsJonable()) {                //если ожидание или первый таёмер, т.е. не внесён в players
            players.remove(p.getName());         //перестраховка
            if (players.isEmpty() && CoolDown != null) {      //если был запущен таймер
                CoolDown.cancel();
                cdCounter = 40;
            }
            //new ActionbarTitleObject (Messages.GetMsg("arena_exit")).send(p);
            //ApiOstrov.sendActionBarDirect(p, Messages.GetMsg("arena_exit"));
            //p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
            //Signs.SignsUpdate( getName(),Messages.GetMsg("signs_line_3_prefix")+ players.size(), getStateAsString(), "§1 - / -" );
            Twist.sendBsignChanel(arenaName, players.size(), "§1Твистеры: " + players.size(), state.displayColor + state.name(), state);

        } else {  //выход во время игры-возможно только через отключение
            if (players.remove(p.getName()) != null) {
                Twist.sendBsignChanel(arenaName, players.size(), state.displayColor + state.name(), "§7Раунд: §b§l" + round + "§7/§b§l" + maxRound, state);
                Oplayer op;
                for (Player pl : getPlayers()) {
                    op = PM.getOplayer(pl);
                    op.score.getSideBar().update(arenaName, "§4§o✖");
                }
            }
        }*/

    }


// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void spectate(final Player p) {
        UniversalListener.spectatorPrepare(p);
        p.teleport(zero.clone().add(0, 5, 0));
    }

// **************************  GET / SET *******************************
    public boolean IsInGame(Player p) {
        return players.containsKey(p.getName());
    }

   // public boolean IsJonable() {
   //     return (state == GameState.ОЖИДАНИЕ || state == GameState.СТАРТ);
   // }

    public String getName() {
        return this.arenaName;
    }

    /*
    public String getScoreTimer() {
        return switch (state) {
            case ОЖИДАНИЕ -> Messages.GetMsg("score_waiting").replace("%s", String.valueOf(this.minPlayers-this.players.size()));
            case СТАРТ -> Messages.GetMsg("score_cooldown").replace("%s", String.valueOf(this.cdCounter));
            case ЭКИПИРОВКА -> Messages.GetMsg("score_prestart");
            case ИГРА -> Messages.GetMsg("score_ingame").replaceAll("%r", String.valueOf(this.round)).replaceAll("%m", String.valueOf(this.maxRound)).replace("%t", Twist.getTime(this.playtime) );
            default -> state.displayColor+state.name();
        };
   }*/
   /* public String GetScoreStatus(Player p) {
        if (state == GameState.ОЖИДАНИЕ || state == GameState.СТАРТ) {
            return "§f" + p.getName();
        } else if (players.contains(p.getName())) {
            //return "§2§o✔ "+ColorUtils.DyeToString(curr_color).substring(0,2)+p.getName();
            return "§2§o✔ " + TCUtils.toChat(nextColor) + p.getName();
        } else if (this.looser.contains(p.getName())) {
            return "§4§o✖ §7" + p.getName();
        } else {
            return "§7(зритель) " + p.getName();
        }
    }*/

    public Location getZero() {
        return this.zero;
    }

    public Location getLobby() {
        return this.arenaLobby;
    }

    public void setLobby(Location location) {
        this.arenaLobby = location;
    }

    //public String getMode() {
    //     return this.mode;
    // }
    public byte getSize_x() {
        return this.size_x;
    }

    public byte getSize_z() {
        return this.size_z;
    }

    public byte getDownId() {
        return this.down;
    }

    public byte getShow() {
        return this.show;
    }

    public void setShow(byte d) {
        this.show = d;
    }

    public byte getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(byte d) {
        this.difficulty = d;
    }

    public byte getMaxRound() {
        return this.maxRound;
    }

    public void setMaxRound(byte d) {
        this.maxRound = d;
    }


// *********************************************************************
// -------------------------------  Вспомогательные --------------------------
    public void SendAB(String text) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            //new ActionbarTitleObject (text).send(p);
            ApiOstrov.sendActionBarDirect(p, text);
        });
    }

    public void SendMessage(String text) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            p.sendMessage(text);
        });
    }

    public void SendSound(Sound s) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            p.playSound(p.getLocation(), s, 5.0F, 5.0F);
        });
    }

    public void SendSound(String s) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            p.playSound(p.getLocation(), s, 1, 1);
        });
    }

    public void SendTitle(String t, String st) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            ApiOstrov.sendTitle(p, t, st, 2, 10, 2);
            //(new TitleObject ((t), TitleObject.TitleType.TITLE)).setFadeIn(20).setStay(20).setFadeOut(5).send(p);
            //(new TitleObject ((st), TitleObject.TitleType.SUBTITLE)).setFadeIn(20).setStay(20).setFadeOut(5).send(p);
        });
    }

    private DyeColor GenRandColor(DyeColor old) {
        DyeColor dc = old;
        for (byte i = 0; i < 15; i++) {                                         //делаем 15 попыток подобрать уникальный цвет
            dc = Twist.allowedColors.get(random.nextInt(Twist.allowedColors.size() - 1));     //генерируем позицию цвета в списке разрешенных
            if (dc != old) {
                break;   //если старый цвет не совпадает с новым, отдаём
            }
        }
//Ostrov.log("GenRandColor old="+old+" new="+dc);
        return dc;//Main.allowedColors.get( pos );                               //если не подобрали, отдаём, что есть
    }

    ///салютики
    private static void firework(Player p) {
        for (int i = 0; i < 6; ++i) {                           //салютики
            new BukkitRunnable() {
                @Override
                public void run() {
                    Firework firework = (Firework) p.getWorld().spawn(p.getLocation().clone().add(0, 5, 0), Firework.class);
                    FireworkMeta fireworkmeta = firework.getFireworkMeta();
                    FireworkEffect fireworkeffect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).withFade(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256))).with(FireworkEffect.Type.STAR).trail(true).build();

                    fireworkmeta.addEffect(fireworkeffect);
                    firework.setFireworkMeta(fireworkmeta);
                }
            }.runTaskLater(Twist.GetInstance(), (long) (i * 5));
        }
    }

    public List<Player> getPlayers() {
        final List<Player> list = new ArrayList<>();
        for (String nik : players.keySet()) {
            if (Bukkit.getPlayerExact(nik) != null) {
                list.add(Bukkit.getPlayerExact(nik));
            }
        }
        return list;
    }

    @Override
    public Game game() {
        return Game.TW;
    }

    @Override
    public boolean hasPlayer(final Player p) {
        return players.containsKey(p.getName());
    }

    @Override
    public String joinCmd() {
        return "tw join ";
    }

    @Override
    public String leaveCmd() {
        return "tw leave";
    }



}
