/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41;

import de.bl4ckskull666.mu1ti1ingu41.commands.LangCommand;
import de.bl4ckskull666.mu1ti1ingu41.commands.LanguageCommand;
import de.bl4ckskull666.mu1ti1ingu41.commands.SpracheCommand;
import de.bl4ckskull666.mu1ti1ingu41.listener.PostLogin;
import de.bl4ckskull666.mu1ti1ingu41.utils.ResourceList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 *
 * @author PapaHarni
 */
public class Mu1ti1ingu41 extends Plugin {
    private Configuration _config = null;
    
    @Override
    public void onEnable() {
        _plugin = this;
        
        if(getConfig().getBoolean("save-user-language", true))
            UUIDLanguages.loadPlayerLanguages();

        UUIDLanguages._players.put(UUID.fromString("00000000-0000-0000-0000-000000000000"), getConfig().getString("default-language", "en"));
        Language.loadLanguage();
        
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LangCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LanguageCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SpracheCommand());
        
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PostLogin());
    }
    
    @Override
    public void onDisable() {
        if(getConfig().getBoolean("save-user-language", true))
            UUIDLanguages.savePlayerLanguages();
        
    }
    
    public Configuration getConfig() {
        if(_config == null)
            _config = Mu1ti1ingu41.loadConfig(this);
        return _config;
    }
    
    public static Configuration loadConfig(Plugin plugin) {
        try {
            boolean saveConfig = false;
            if(!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdirs();

            File file = new File(plugin.getDataFolder(), "config.yml");

            if(!file.exists()) {
                String srcFile = ResourceList.getResourceFile(plugin.getClass(), "config.yml");
                if(srcFile.isEmpty())
                    return null;

                InputStream in = plugin.getResourceAsStream(srcFile);
                int c = -1;
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                while((c = in.read()) != -1)
                    bw.write(String.valueOf((char)c));
                bw.close();
                saveConfig = true;
            }
            Configuration conf = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            if(saveConfig)
                Mu1ti1ingu41.saveConfig(conf, plugin);
            return conf;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Can't load the config file", ex);
        }
        return null;
    }
    
    public static void saveConfig(Configuration conf, Plugin plugin) {
        if(!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdirs();
        
        File file = new File(plugin.getDataFolder(), "config.yml");
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(conf, file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.WARNING, "Can't save the config file", ex);
        }
    }
    
    //Statics
    private static Mu1ti1ingu41 _plugin;
    public static Mu1ti1ingu41 getPlugin() {
        return _plugin;
    }
    
    public static String name() {
        return _plugin.getDescription().getName();
    }
    
    /**
     * 
     * @param plugin = YourPlugin
     * @param folder = folder in your plugin , like language in default, must be yml files in the folder
     */
    public static void loadExternalDefaultLanguage(Plugin plugin, String folder) {
        Language.loadDefaultLanguage(plugin, folder);
    }
    
    public static BaseComponent[] castMessage(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
    }
}
