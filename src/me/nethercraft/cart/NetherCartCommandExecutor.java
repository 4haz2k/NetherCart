package me.nethercraft.cart;

import me.vagdedes.mysql.database.MySQL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class NetherCartCommandExecutor implements CommandExecutor {
    ResultSet result;

    private NetherCraft_Cart mainplugin;

    public NetherCartCommandExecutor(NetherCraft_Cart plugin) {
        this.mainplugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length == 0){
                if(player.hasPermission("NetherCraft_Cart.cart")) {
                    MySQL.connect();
                    result = MySQL.query("SELECT * FROM `test` WHERE `player` = '" + player.getName().toLowerCase() + "'");
                    try {
                        if(!result.isBeforeFirst()){
                            player.sendMessage(ChatColor.DARK_RED+ "| " + ChatColor.GRAY + "Ваша корзина пустая.");
                            MySQL.disconnect();
                            return true;
                        }
                        StringBuilder items = new StringBuilder(ChatColor.DARK_GRAY + "| " + ChatColor.GREEN + "Ваша корзина: \n");
                        int count = 1;
                        while (result.next()) {
                            items.append(ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + count + ": " + ChatColor.GREEN).append(mainplugin.getConfig().getString("items." + result.getString(4), "def")).append(ChatColor.GRAY).append(" x ").append(ChatColor.GREEN).append(result.getString(5)).append("\n");
                            count++;
                        }
                        player.sendMessage(items.toString());
                    }
                    catch (SQLException e){
                        e.printStackTrace();
                    }
                    MySQL.disconnect();
                }
                else{
                    player.sendMessage(ChatColor.DARK_RED + "| " + ChatColor.GRAY + "Вы можете использовать эту команду только в игровом мире!");
                }
            }
            else if(args[0].equalsIgnoreCase("all")) {
                if(args.length > 1){
                    player.sendMessage(ChatColor.DARK_RED + "| " + ChatColor.GRAY + "Неправильная команда!");
                    return true;
                }
                if (player.hasPermission("NetherCraft_Cart.cart")) {
                    if (invFull(player)) {
                        player.sendMessage(ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Чтобы получить предметы, освободите инвентарь.");
                        return true;
                    }
                    MySQL.connect();
                    result = MySQL.query("SELECT * FROM `test` WHERE `player` = '" + player.getName().toLowerCase() + "'");
                    try {
                        if (!result.isBeforeFirst()) {
                            player.sendMessage(ChatColor.DARK_RED + "| " + ChatColor.GRAY + "Ваша корзина пустая..");
                            MySQL.disconnect();
                            return true;
                        }
                        StringBuilder items = new StringBuilder(ChatColor.DARK_GRAY + "| " + ChatColor.GREEN + "Выданы предметы: \n");
                        String info = null;
                        int count = 1;
                        ArrayList<Integer> arrayList = new ArrayList<>();
                        while (result.next()) {
                            if (invFull(player)) {
                                info = ChatColor.DARK_RED + "| " + ChatColor.GRAY + "Чтобы получить остальные предметы, освободите инвентарь.";
                                break;
                            }
                            items.append(ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + count + ": " + ChatColor.GREEN).append(mainplugin.getConfig().getString("items." + result.getString(4), "def")).append(ChatColor.GRAY).append(" x ").append(ChatColor.GREEN).append(result.getString(5)).append("\n");
                            ItemStack item = new ItemStack(Material.valueOf(result.getString(4)));
                            item.setAmount(result.getInt(5));
                            arrayList.add(result.getInt(1));
                            player.getInventory().addItem(item);
                            count++;
                         }
                        player.sendMessage(items.toString());
                        if(info != null){
                            player.sendMessage(info);
                        }
                        for (int id : arrayList) {
                            MySQL.update("DELETE FROM `test` WHERE `id` = " + id);
                        }
                        arrayList.clear();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    MySQL.disconnect();
                } else {
                    player.sendMessage(ChatColor.DARK_RED + "| " + ChatColor.GRAY + "Неизвестная команда! Допустимо: /cart all, /cart");
                }
            }
        }
        else{
            mainplugin.log.info("Эту команду может использовать только игрок!");
        }
        return true;
    }

    public boolean invFull(Player p) {
        return !Arrays.asList(p.getInventory().getStorageContents()).contains(null);
    }
}
