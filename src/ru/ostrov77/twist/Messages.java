package ru.ostrov77.twist;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.ChatColor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ru.ostrov77.twist.Main;






public class Messages {

    
    static File msgFile = new File(Main.GetInstance().getDataFolder() + "/messages.yml");
    static FileConfiguration msgConfig = YamlConfiguration.loadConfiguration(Messages.msgFile);
    
    private static HashMap<Integer,String> allMessages = new HashMap<>();
    


    
    
    
    
    
    
  
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
public static void Load() {
    
        Messages.msgConfig.addDefault("prefix", "&2[&a&bTwist&2] &f");
        Messages.msgConfig.addDefault("arena_exit", "§fYou leave arena!");
        Messages.msgConfig.addDefault("arena_ingame", "§4Arena in game!");
        Messages.msgConfig.addDefault("you_ingame", "§сYou allready in game!");
        Messages.msgConfig.addDefault("exit_item_name", "&2Exit");
        Messages.msgConfig.addDefault("prestart_title", "&aTwist start at");
        Messages.msgConfig.addDefault("prestart_subtitle", " %s sec.!");
        Messages.msgConfig.addDefault("players_need_for_start", "§6For start need §b%n §6players.!");
        Messages.msgConfig.addDefault("no_enough_players", "&d&lNot enough player, stop countdown.");
        Messages.msgConfig.addDefault("fast_start", "§2§lThe time until the start of the game reduced!");
        Messages.msgConfig.addDefault("too_few_cancel_start", "§d§lNot enough player, canceled.");
        Messages.msgConfig.addDefault("prepare_to_game", "§aTwist charging... b%s §asec. left !");
        Messages.msgConfig.addDefault("start_game", "§6TWIST! §aTWIST! §bTWIST!");
        Messages.msgConfig.addDefault("end_cause_timelinit_title", "Time is out!");
        Messages.msgConfig.addDefault("end_cause_timelinit_subtitle", "Game end!");
        Messages.msgConfig.addDefault("you_win_title", "§aYou win!");
        Messages.msgConfig.addDefault("you_win_subtitle", "§fYours regard - 300р !");
        Messages.msgConfig.addDefault("signs_line_3_prefix", "§1twisters: ");
        Messages.msgConfig.addDefault("signs_round_prefix", "§7Round: §b§l");
        Messages.msgConfig.addDefault("you_loose_title", "§4You lose!");
        Messages.msgConfig.addDefault("you_loose_subtitle", "&fAccept death with dignity..");
        Messages.msgConfig.addDefault("pig_zombie_name", "Judge");
        
        Messages.msgConfig.addDefault("WAITING", "§b§lWAITING");
        Messages.msgConfig.addDefault("STARTING", "§6§lSOON START");
        Messages.msgConfig.addDefault("STARTED", "§5§lCHARGING");
        Messages.msgConfig.addDefault("INGAME", "§4§lTWIST");
        Messages.msgConfig.addDefault("ENDING", "§9§lENDING");
        
        Messages.msgConfig.addDefault("score_ingame", "§7Round: §b§l%r §7/§b§l%m §f%t");
        Messages.msgConfig.addDefault("score_waiting", "§aNeed players: (§b%s§a)");
        Messages.msgConfig.addDefault("score_cooldown", "§7Time left: §b§l%s");
        Messages.msgConfig.addDefault("score_prestart", "§f§lCharging..");
        
        Messages.msgConfig.addDefault("arena_not_exist", "&cArena not exist");
        Messages.msgConfig.addDefault("arg_err", "&cArgument error!");
        Messages.msgConfig.addDefault("arenas_list_command_result", "§b§lFound arenas: ");
        Messages.msgConfig.addDefault("arenas_start_command_result", "§bTime to launch reduced");
        
        Messages.msgConfig.addDefault("wt", "White");
        Messages.msgConfig.addDefault("or", "Orange");
        Messages.msgConfig.addDefault("pu", "Purpur");
        Messages.msgConfig.addDefault("bl", "Blue");
        Messages.msgConfig.addDefault("ye", "Yellow");
        Messages.msgConfig.addDefault("la", "Lime");
        Messages.msgConfig.addDefault("ro", "Red");
        Messages.msgConfig.addDefault("dg", "Gray");
        Messages.msgConfig.addDefault("gr", "Silver");
        Messages.msgConfig.addDefault("aq", "Aqua");
        Messages.msgConfig.addDefault("dp", "Li");
        Messages.msgConfig.addDefault("db", "Deep Sea");
        Messages.msgConfig.addDefault("br", "Brown");
        Messages.msgConfig.addDefault("gn", "Green");
        Messages.msgConfig.addDefault("ba", "Bard");
        Messages.msgConfig.addDefault("bk", "Black");

        
        
        
        
        
        
        
        
        
        Messages.msgConfig.options().copyDefaults(true);
        saveCustomYml(Messages.msgConfig, Messages.msgFile);

        Messages.msgConfig.getKeys(false).stream().forEach((s) -> {
            allMessages.put( s.hashCode(),  Messages.msgConfig.getString(s) );
        });

                
//try {
           // for (String s : Messages.msgConfig.getKeys(false)) {
                
            //    allMessages.put(s.hashCode(), s);
                //Field field = Messages.class.getField(s);
                //field.setAccessible(true);
                //field.set((Object) null, Messages.msgConfig.getString(s));
           // }
       // } catch ( IllegalAccessException e) {
           // Main.log("§4Не удалось загрузить перевод из файла!! Попробуйте удалить его! ");
       // }

    }

    
    

public static String GetMsg (String msg) {
    
    if (allMessages.containsKey(msg.hashCode())) return ChatColor.translateAlternateColorCodes("&".charAt(0), allMessages.get( msg.hashCode() ) );
    else return "§4"+msg+" §cnot_found_in_messages.yml";
    
}





    
    
private static void saveCustomYml(FileConfiguration fileconfiguration, File file) {
        try {
            fileconfiguration.save(file);
        } catch (IOException e) {
            //Main.log("§4Не удалось сохранить перевод в файл!!");
            Main.log("§4Can not save lang file!!");
        }

    }


}


/*        Messages.msgConfig.addDefault("prefix", "&2[&a&bТвист&2] &f");
        Messages.msgConfig.addDefault("arena_exit", "§fВы вышли с арены!");
        Messages.msgConfig.addDefault("arena_ingame", "§4На арене идёт игра!");
        Messages.msgConfig.addDefault("you_ingame", "§сВы уже в игре!");
        Messages.msgConfig.addDefault("exit_item_name", "&2Выход");
        Messages.msgConfig.addDefault("prestart_title", "&aЗмейка стартует через");
        Messages.msgConfig.addDefault("prestart_subtitle", "&b %s сек.!");
        Messages.msgConfig.addDefault("players_need_for_start", "§6Для старта нужно еще §b%n §6чел.!");
        Messages.msgConfig.addDefault("no_enough_players", "&d&lНедостаточно участников, счётчик остановлен.");
        Messages.msgConfig.addDefault("fast_start", "§2§lВремя до старта игры уменьшено!");
        Messages.msgConfig.addDefault("too_few_cancel_start", "§d§lСлишкома мало игроков, отмена.");
        Messages.msgConfig.addDefault("prepare_to_game", "§aТвист заражается... Осталось §b%s §aсекунд!");
        Messages.msgConfig.addDefault("start_game", "§6ТВИСТ! §aТВИСТ! §bТВИСТ!");
        Messages.msgConfig.addDefault("end_cause_timelinit_title", "Время вышло!");
        Messages.msgConfig.addDefault("end_cause_timelinit_subtitle", "Игра окончена!");
        Messages.msgConfig.addDefault("you_win_title", "§aВы победили!");
        Messages.msgConfig.addDefault("you_win_subtitle", "§fВаша награда - 300р. на счёт !");
        Messages.msgConfig.addDefault("signs_line_3_prefix", "§1Твистеры: ");
        Messages.msgConfig.addDefault("signs_round_prefix", "§7Раунд: §b§l");
        Messages.msgConfig.addDefault("you_loose_title", "§4Вы проиграли!");
        Messages.msgConfig.addDefault("you_loose_subtitle", "&fПримите смерть достойно..");
        Messages.msgConfig.addDefault("pig_zombie_name", "Инквизитор");
        
        Messages.msgConfig.addDefault("WAITING", "§b§lОЖИДАНИЕ");
        Messages.msgConfig.addDefault("STARTING", "§6§lСКОРО СТАРТ!");
        Messages.msgConfig.addDefault("STARTED", "§5§lСТАРТУЕТ");
        Messages.msgConfig.addDefault("INGAME", "§4§lИГРА");
        Messages.msgConfig.addDefault("ENDING", "§9§lЗАВЕРШАЕТСЯ");
        
        Messages.msgConfig.addDefault("score_ingame", "§7Раунд: §b§l%r §7/§b§l%m §f%t");
        Messages.msgConfig.addDefault("score_waiting", "§aОжидаем игроков.. (§b%s§a)");
        Messages.msgConfig.addDefault("score_cooldown", "§7До старта: §b§l%s");
        Messages.msgConfig.addDefault("score_prestart", "§f§lЗарядка..");
        
        Messages.msgConfig.addDefault("arena_not_exist", "&cArena not exist");
        Messages.msgConfig.addDefault("arg_err", "&cArgument error!");
        Messages.msgConfig.addDefault("arenas_list_command_result", "§b§lАрен найдено: ");
        Messages.msgConfig.addDefault("arenas_start_command_result", "§bВремя до старта уменьшено");
        
        Messages.msgConfig.addDefault("wt", "Белый");
        Messages.msgConfig.addDefault("or", "Оранжевый");
        Messages.msgConfig.addDefault("pu", "Пурпурный");
        Messages.msgConfig.addDefault("bl", "Голубой");
        Messages.msgConfig.addDefault("ye", "Желтый");
        Messages.msgConfig.addDefault("la", "Лаймовый");
        Messages.msgConfig.addDefault("ro", "Розовый");
        Messages.msgConfig.addDefault("dg", "т.-Серый");
        Messages.msgConfig.addDefault("gr", "Серый");
        Messages.msgConfig.addDefault("aq", "Аквамарин");
        Messages.msgConfig.addDefault("dp", "т.-Фиолетовый");
        Messages.msgConfig.addDefault("db", "Синий");
        Messages.msgConfig.addDefault("br", "Коричневый");
        Messages.msgConfig.addDefault("gn", "Зелёный");
        Messages.msgConfig.addDefault("ba", "Бардовый");
        Messages.msgConfig.addDefault("bk", "Чёрный");

        
        
*/