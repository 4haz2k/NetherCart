package me.nethercraft.cart;

import javafx.scene.transform.MatrixType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;
import me.vagdedes.mysql.database.MySQL;
import org.json.simple.JSONObject;

public class NetherCraft_Cart extends JavaPlugin{

//    protected FileConfiguration config;
    public Logger log = getLogger();
    private NetherCartCommandExecutor myExecutor;

    @Override
    public void onEnable(){
        log.info("NetherCraft Cart Enabled!");
        myExecutor = new NetherCartCommandExecutor(this);
        getCommand("cart").setExecutor(myExecutor);
    }

    @Override
    public void onDisable(){
        log.info("NetherCraft Cart Disabled!");
    }

}
