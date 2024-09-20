package ru.ostrov77.twist;

import ru.ostrov77.minigames.MiniGamesLst;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
import ru.komiss77.modules.games.GM;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.world.WXYZ;
import ru.komiss77.scoreboard.SideBar;
import ru.komiss77.utils.*;
import ru.komiss77.utils.ParticleUtil;
import ru.komiss77.utils.TCUtil;
import ru.komiss77.version.GameApi;
import ru.komiss77.version.Nms;
import ru.ostrov77.minigames.IArena;
import ru.ostrov77.minigames.MG;

public class Arena implements IArena {

    public final String arenaName;
    public Location arenaLobby;
    public Location zero;
    private Material mat = Material.WHITE_WOOL;
    public final int size_x, size_z;
    public int show, difficulty, maxRound;

    private BukkitTask task, DysplayColor,RemoveFloor;
    private int cdCounter, prestart, playtime, ending;
    private int round;
    private DyeColor nextColor;
    private boolean displayColor, removeFloor;

    public Map<String, Integer> players = new HashMap<>();
    public Set<String> looser = new HashSet<>();
    private final Map<Integer, DyeColor> colormap = new HashMap<>();

    private static Random random;
    public GameState state = GameState.ВЫКЛЮЧЕНА; //ОЖИДАНИЕ СТАРТ ЭКИПИРОВКА ИГРА ФИНИШ

    private final WXYZ wxyz;
    //private final WorldServer nmsWorldServer;
    //private final net.minecraft.world.level.block.state.IBlockData ibdDataAir;
    //private static final BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition(0, 0, 0);

    
    private static final List <PotionEffect> pot = List.of(
            new PotionEffect(PotionEffectType.INVISIBILITY, 60, 10),
            new PotionEffect(PotionEffectType.OOZING, 60, 10),
            new PotionEffect(PotionEffectType.SLOWNESS, 60, 10)
    );
    
    
    
    public Arena(String name,
            Location zero,
            Location arenaLobby,
            int size_x, int size_z, int show,
            int difficulty, int maxRound) {

        this.arenaName = name;
        this.arenaLobby = arenaLobby;
        this.zero = zero;
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

        //nmsWorldServer = ((CraftWorld) arenaLobby.getWorld()).getHandle();
        //ibdDataAir = ((CraftBlockData) Material.AIR.createBlockData()).getState();//= net.minecraft.world.level.block.Block.a( 0 );
        
        cdCounter = 40; //ожид в лобби арены
        prestart = 7; //ожид на арене
        ending = 7; //салюты,награждения
        playtime = 0;

        round = 1;
        nextColor = TCUtil.randomDyeColor();//DyeColor.BLACK;
        displayColor = true;
        removeFloor = false;
        random = new Random();

        wxyz = new WXYZ(zero);
        //wxyz.x = zero.getBlockX() + plot_x * 4;  //координата блока Х на углу плота
        wxyz.y = zero.getBlockY() - 5;           //координата высоты (для пола -5)
        //wxyz.z = zero.getBlockZ() + plot_z * 4; //координата блока Z на углу плота
        GameApi.setFastMat(wxyz, size_x, 1, size_z, Material.GLOWSTONE);
        wxyz.y = zero.getBlockY(); //вернуть коорд.Y - в игре она не меняется!
        
        GenerateNewFloor();

        state = GameState.ОЖИДАНИЕ;
        Arena.this.sendArenaData();
    }

    
    public void resetGame() {
        if (state==GameState.ОЖИДАНИЕ) return;
        if (task != null) {
            task.cancel();
        }
        if (DysplayColor != null) {
            DysplayColor.cancel();
        }
        if (RemoveFloor != null) {
            RemoveFloor.cancel();
        }
        arenaLobby.getWorld().getEntities().stream().forEach( e -> {
            if (e.getType() == EntityType.PLAYER) {
                MG.lobbyJoin((Player) e);
            } else {
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
        nextColor = TCUtil.randomDyeColor();//DyeColor.BLACK;
        displayColor = true;
        removeFloor = false;
        state = GameState.ОЖИДАНИЕ;
        sendArenaData();
    }

    
    public void startCountdown() {                            //ожидание в лобби
        if (state != GameState.ОЖИДАНИЕ) return;
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
                    sendArenaData();

                    Oplayer op;
                    for (Player p : getPlayers()) {
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle("§6До старта: §b"+(cdCounter+7));
                        if (cdCounter <= 5 ) {
                            p.playSound(p.getEyeLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5.0F, 5.0F);
                        }
                    }

                }

            }
        }).runTaskTimer(TW.plugin, 0L, 20L);
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
        if (state != GameState.СТАРТ) return;
        state = GameState.ЭКИПИРОВКА;
        if (task != null)  task.cancel();

        getPlayers().stream().forEach( p -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5, 1));
            p.getInventory().clear();
            p.teleport(randomFielldLoc());
            p.playSound(p.getEyeLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2, 2);
            p.setWalkSpeed((float) 0.3);
        });

        task = (new BukkitRunnable() {         //тут уже таймер с игроками
            @Override
            public void run() {

                if (players.isEmpty()) {
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
                        ScreenUtil.sendActionBarDirect(p, "§aТвист заражается... Осталось §b" + prestart + " §aсек.!");
                        p.playSound(p.getEyeLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 5.0F, 5.0F);
                    }
                    --prestart;
                }

            }
        }).runTaskTimer(TW.plugin, 0L, 20L);
    }

    
    
    public void GameProgress() {
        if (state != GameState.ЭКИПИРОВКА)  return;
        state = GameState.ИГРА;
        if (task != null)   task.cancel();

        Oplayer op;
        final String r = "§7Раунд: §b§l" + round + "§7/§b§l" + maxRound;
        final String nextColorPref = TCUtil.toChat(nextColor);
        for (Player p : getPlayers()) {
            op = PM.getOplayer(p);
            SideBar sb = op.score.getSideBar().setTitle(r);
            for (String name : players.keySet()) {
                sb.add(name, nextColorPref+name+" §e0 §f/ §c0");
            }
            sb.build();
            ScreenUtil.sendActionBarDirect(p, "§6ТВИСТ! §aТВИСТ! §bТВИСТ!");
            p.playSound(p.getEyeLocation(), Sound.ENTITY_CAT_AMBIENT, 2, 2);
        }
        
        displayColor = true;
        removeFloor = false;
        showNextColor(); //первая раскачка
        
        task = (new BukkitRunnable() {
            @Override
            public void run() {
                final List<Player> list = getPlayers();

                if (displayColor) {
                    if (RemoveFloor != null) {
                        RemoveFloor.cancel();
                    }
                    //showNextColor();                                             //показываем инфо
                    spawnCoin();
                    final String nextColorPref = TCUtil.toChat(nextColor);
                    Oplayer op;
                    final String r = "§7Раунд: §b§l" + round + "§7/§b§l" + maxRound;
                    for (Player p : list) {
                        if (looser.remove(p.getName())) {
                            p.teleport(randomFielldLoc());
                            p.getActivePotionEffects().stream().forEach(effect -> {
                                p.removePotionEffect(effect.getType());
                            });
                        }
                        op = PM.getOplayer(p);
                        op.score.getSideBar().setTitle(r);
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
                    sendArenaData();
                }

                //тут тикает только во время раундов!!
                for (Player p : list) {
                    if (zero.getBlockY() - p.getLocation().getBlockY() >= 3) { //ниже полотна на 3 и более блока - упал
                        fall(p);
                    }
                }

                //this.show - ( this.difficulty * this.round )
                if (players.isEmpty() || playtime > maxRound * show + 20) {
                    resetGame();
                } else if (round >= maxRound) { //последний раунд и есть выжившие - победитель
                    this.cancel();
                    endGame();
                }

                playtime++;

            }
        }).runTaskTimer(TW.plugin, 0L, 20L);

    }


    //если обнаружен на 3 блока ниже шерсти или 
    public void fall(final Player p) {
        if (looser.add(p.getName())) {
            players.replace(p.getName(), players.get(p.getName())+1);
//Ostrov.log("fall players.get=");
            p.addPotionEffects(pot);
            ParticleUtil.deathEffect(p, false);
        }
    }

    private void spawnCoin() {
        int ammount = 0;
        for (Entity e : arenaLobby.getWorld().getEntities()) {
            if (e.getType() != EntityType.PLAYER && e.getTicksLived() > 300) {
                e.remove();
            }
            if (e.getType() == EntityType.ITEM) {
                ammount++;
            }
        }
        for (int i = ammount; i < (size_x * size_z / 10); i++) {
            final ItemStack is = new ItemStack(Material.SUNFLOWER, 1);// AM.bonus.clone();
            //final ItemMeta im = is.getItemMeta();
            //im.displayName(Component.text(String.valueOf(random.nextInt(999))));
            //is.setItemMeta(im);
            Item item = arenaLobby.getWorld().dropItem(randomFielldLoc(), is);
            item.setVelocity(new Vector(0, 0, 0));
            item.setPickupDelay(1);
            item.setGravity(false);
        }
    }

    
    //сюдя приходит если прожили все раунды
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
        BackFloor();

        int coin, fall;
        final List<String> winer = new ArrayList<>();
        for (Player p : getPlayers()) {
            coin = p.getLevel();
            fall = players.get(p.getName());
            p.setLevel(0);
            p.sendMessage(""); 
            p.sendMessage("§bРаунды §7: §b§l"+round+" §eМонеты §7: §e§l"+coin+" §cПадения §7: §c§l"+fall);
            if (fall>coin) {
                p.sendMessage("§5§lПадений больше, чем монет, это провал..");
                p.playSound(p.getEyeLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 2, 2);
            } else if (coin>fall) { //coin будет не менее 1!! (изначально fall=0 => 1>0)
                p.sendMessage("§a§lЗачтена победа!");
                ApiOstrov.addStat(p, Stat.TW_win);
                ApiOstrov.addStat(p, Stat.TW_gold, coin);
                p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 150, 0));
                p.getWorld().playSound(p.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
                winer.add(p.getName());
            } else {
                p.sendMessage("§b§lДля победы нужно собирать монеты.");
                p.playSound(p.getEyeLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 2, 2);
            }
            p.sendMessage("");
            ApiOstrov.addStat(p, Stat.TW_game);
        }


        task = (new BukkitRunnable() {
            @Override
            public void run() {
                if (ending <= 0 || players.isEmpty()) {
                    this.cancel();
                    resetGame();
                } else {
                    Player p;
                    for (String name : winer) {
                        p = Bukkit.getPlayerExact(name);
                        if (p!=null) {
                            ParticleUtil.spawnRandomFirework(p.getEyeLocation());
                        }
                    }
                }
                --ending;
            }
        }).runTaskTimer(TW.plugin, 0L, 20L);

        sendArenaData();
    }


    
    
    
    
    public void showNextColor() {                           //показать новый цвет, звуки, таймер ->> разрешение на стирание 

        nextColor = GenRandColor(nextColor);//new_col; //приготовить следующий цвет

        displayColor = false;
        removeFloor = false;
//Ostrov.log("showNextColor "+nextColor+" "+TCUtil.dyeDisplayName(nextColor));
        ItemStack item = new ItemStack(mat, 1);
        item = TCUtil.changeColor(item, nextColor);
        ItemMeta m = item.getItemMeta();
        m.displayName(Component.text(TCUtil.dyeDisplayName(nextColor)));
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

                    p.getInventory().setItem(i, ItemUtil.air);
                    p.updateInventory();

                    switch (i) {
                        case 8:
                            p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                        case 5:
                            p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                        case 2:
                            p.playSound(p.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
                    }

                });

                i--;
                if (i < 0) {
                    displayColor = false;
                    removeFloor = true;
                    this.cancel();
                }

            }
        }).runTaskTimer(TW.plugin, 20L, sw);

    }

// ------------------------------------  удалить квадраты/ вернуть квадраты ------------------------------------------------
    private void MustStayOne() {                     //удал все цвета, кроме текущего
        displayColor = false;
        removeFloor = false;
        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {

                if (colormap.get(x * size_z + z) != nextColor) {
                    FillPlotAir(x, z);
                }

            }
        }

        RemoveFloor = (new BukkitRunnable() {
            @Override
            public void run() {
                BackFloor(); //1!!
                showNextColor(); //2!!
                displayColor = true;
                removeFloor = false;
            }
        }).runTaskLater(TW.plugin, 60);

    }

    private void BackFloor() {                     //вернуть пол

        DyeColor previos;
        DyeColor newColor;
        int idx;

        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {
                idx = x * size_z + z;

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
        wxyz.x = zero.getBlockX() + plot_x * 4;
        //wxyz.y = zero.getBlockY();
        wxyz.z = zero.getBlockZ() + plot_z * 4;
        mat = TCUtil.changeColor(mat, color);//Material.valueOf(color.toString()+"_"+mat_base);
        GameApi.setFastMat(wxyz, 4, 1, 4, mat);
        //final BlockData data = mat.createBlockData();
        //final net.minecraft.world.level.block.state.IBlockData ibdColoredWool = ((CraftBlockData) data).getState(); //((CraftBlock)block).getNMS();
        //for (byte x_ = 0; x_ < 4; x_++) {
        //    for (byte z_ = 0; z_ < 4; z_++) {
        //        mutableBlockPosition.d(x + x_, y, z + z_);
        //        CraftBlock.setTypeAndData(nmsWorldServer, mutableBlockPosition, ibdDataAir, ibdColoredWool, false);
        //    }
        //}
    }

    private void FillPlotAir(byte plot_x, byte plot_z) {
        wxyz.x = zero.getBlockX() + plot_x * 4;
        //wxyz.y = zero.getBlockY();
        wxyz.z = zero.getBlockZ() + plot_z * 4;
        GameApi.setFastMat(wxyz, 4, 1, 4, Material.AIR);
        //final net.minecraft.world.level.block.state.IBlockData ibdDataPrevios = nmsWorldServer.a_(mutableBlockPosition);
        //for (byte x_ = 0; x_ < 4; x_++) {
        //    for (byte z_ = 0; z_ < 4; z_++) {
        //        mutableBlockPosition.d(x + x_, y, z + z_);
        //        CraftBlock.setTypeAndData(nmsWorldServer, mutableBlockPosition, ibdDataPrevios, ibdDataAir, false);
        //    }
        //}

    }

// ------------------------------------  Генерация ------------------------------------------------
    private void GenerateNewFloor() {
        colormap.clear();

        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {

                LoadChunk(x, z);          //!!! создание карты чанков для обновления и их загрузка !!!

                DyeColor col = GenRandColor(nextColor); //следущий цвет должен отличаться
                nextColor = col;
                colormap.put(x * size_z + z, col);
//Ostrov.log("generate idx="+(x * size_x + z)+" col="+col);
                FillPlotMat(x, z, col);  //заполняем плоты случ цветом
                //FillDown(x, z);          //заполняем низ

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

        for (byte x = 0; x < size_x; x++) {
            for (byte z = 0; z < size_z; z++) {
                FillPlotAir(x, z);
                //FillDownAir(x, z);
            }
        }
        wxyz.y = zero.getBlockY() - 5;           //координата высоты (для пола -5)
        GameApi.setFastMat(wxyz, size_x, 1, size_z, Material.AIR);
        wxyz.y = zero.getBlockY(); //вроде арена удаляется и не надо, но на всякий: вернуть коорд.Y - в игре она не меняется!
        colormap.clear();

    }

    //private void FillDown(byte plot_x, byte plot_z) {

        //wxyz.x = zero.getBlockX() + plot_x * 4;  //координата блока Х на углу плота
        //wxyz.y = zero.getBlockY() - 5;           //координата высоты (для пола -5)
        //wxyz.z = zero.getBlockZ() + plot_z * 4; //координата блока Z на углу плота
        
        //Nms.setFastMat(wxyz, 4, 0, 4, Material.GLOWSTONE);
        
        //final IBlockData ibdDataDown = ((CraftBlockData) Material.GLOWSTONE.createBlockData()).getState();
        //final net.minecraft.world.level.block.state.IBlockData ibdDataPrevios = nmsWorldServer.a_(mutableBlockPosition);

        //for (byte x_ = 0; x_ < 4; x_++) {
        //    for (byte z_ = 0; z_ < 4; z_++) {
        //        mutableBlockPosition.d(x + x_, y, z + z_);
        //        CraftBlock.setTypeAndData(nmsWorldServer, mutableBlockPosition, ibdDataPrevios, ibdDataDown, false);
        //    }
        //}

    //}

    //private void FillDownAir(byte plot_x, byte plot_z) {

    //    int x = zero.getBlockX() + plot_x * 4;
    //    int y = zero.getBlockY() - 5;
    //    int z = zero.getBlockZ() + plot_z * 4;

    //    for (byte x_ = 0; x_ < 4; x_++) {
    //        for (byte z_ = 0; z_ < 4; z_++) {
    //            net.minecraft.world.level.chunk.Chunk chunk = nmsWorldServer.d((x + x_) >> 4, (z + z_) >> 4);
    //            chunk.setBlockState(new net.minecraft.core.BlockPosition((x + x_), y, (z + z_)), ibdDataAir, false, false);                                    //вносит в него ibd по blockposition
    //        }
    //    }

    //}
// -----------------------------------------------------------------------------------------

    
    
    
    
    
    public Location randomFielldLoc() {
        return zero.clone().add( (4 + random.nextInt(size_x - 2) * 4), 1, (4 + random.nextInt(size_z - 2) * 4));
    }
    
    
// xxxxxxxxxxxxxxxxxxxxxxxxxxx  Обработчик игроков xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void addPlayer(Player p) {
        p.teleport(arenaLobby);
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

        p.getInventory().setItem(0, ItemUtil.air);
        p.getInventory().setItem(7, ItemUtil.air);
        MG.leaveArena.giveForce(p);//p.getInventory().setItem(8, UniversalListener.leaveArena.clone());
        if (p.hasPermission("forcestart")) {
            MG.forceStart.giveForce(p);
        }
        sendArenaData();
        PM.getOplayer(p).tabSuffix(" §5"+arenaName, p);
    }

    public void removePlayer(Player p) {

        if (players.remove(p.getName()) != null) {
            if (players.isEmpty()) {
                if (task!=null) {
                    resetGame();
                } else {
                    sendArenaData();
                }
            } else {
                Oplayer op;
                for (Player pl : getPlayers()) {
                    op = PM.getOplayer(pl);
                    op.score.getSideBar().reset();
                }
                sendArenaData();
            }
        }

    }


// xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    public void spectate(final Player p) {
        MiniGamesLst.spectatorPrepare(p);
        p.teleport(randomFielldLoc().add(0, 3, 0));
    }


// *********************************************************************
// -------------------------------  Вспомогательные --------------------------
    public void SendAB(String text) {
        arenaLobby.getWorld().getPlayers().stream().forEach((p) -> {
            ScreenUtil.sendActionBarDirect(p, text);
        });
    }


    private DyeColor GenRandColor(DyeColor old) {
        DyeColor dc = old;
        for (byte i = 0; i < 15; i++) {                                         //делаем 15 попыток подобрать уникальный цвет
            dc = TW.allowedColors.get(random.nextInt(TW.allowedColors.size() - 1));     //генерируем позицию цвета в списке разрешенных
            if (dc != old) {
                break;   //если старый цвет не совпадает с новым, отдаём
            }
        }
        return dc;                              //если не подобрали, отдаём, что есть
    }


    public List<Player> getPlayers() {
        final List<Player> list = new ArrayList<>();
        for (String nik : players.keySet()) {
            final Player p = Bukkit.getPlayerExact(nik);
            if (p!=null) {
                list.add(p);
            }
        }
        return list;
    }

    public void sendArenaData() {
        GM.sendArenaData(
                Game.TW, 
                arenaName, 
                state, 
                players.size(), 
                    "§2§l§oTWIST",
                "§5"+arenaName, 
                state.displayColor+state.name(),
                "§6Игроки: §2"+players.size()
        );
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
        return "tw join "+arenaName;
    }

    @Override
    public String leaveCmd() {
        return "tw leave";
    }

    @Override
    public String forceStartCmd() {
        return "tw start "+arenaName;
    }

    @Override
    public String name() {
        return arenaName;
    }

    @Override
    public GameState state() {
        return state;
    }



}
