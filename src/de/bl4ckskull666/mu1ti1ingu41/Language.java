/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41;

import com.maxmind.geoip.LookupService;
import de.bl4ckskull666.mu1ti1ingu41.utils.ResourceList;
import de.bl4ckskull666.mu1ti1ingu41.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import yamlapi.file.FileConfiguration;
import yamlapi.file.YamlConfiguration;

/**
 *
 * @author PapaHarni
 */
public final class Language {
    private final static HashMap<String, HashMap<String, FileConfiguration>> _languages = new HashMap<>();
    private final static HashMap<String, HashMap<String, File>> _files = new HashMap<>();

    public static void loadLanguage() {
        checkGeoIP();
        if(!Mu1ti1ingu41.getPlugin().getDataFolder().exists())
            Mu1ti1ingu41.getPlugin().getDataFolder().mkdir();
        
        File lFold = new File(Mu1ti1ingu41.getPlugin().getDataFolder(), "languages");
        if(!lFold.exists())
            lFold.mkdir();
        
        for(File lang: lFold.listFiles()) {
            if(lang.isDirectory()) {
                for(File lang2: lang.listFiles()) {
                    if(!lang2.getName().endsWith(".yml"))
                        continue;
                    addFileToPlugin(lang2, lang.getName());
                }
            }
        }
        
        if(!_languages.containsKey(Mu1ti1ingu41.name().toLowerCase()))
            loadDefaultLanguage(Mu1ti1ingu41.getPlugin(), "languages");
        
        checkDefaultFiles();
        Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Languages has been loaded.");
    }
    
    private static void checkDefaultFiles() {
        for(Map.Entry<String, HashMap<String, FileConfiguration>> me: _languages.entrySet()) {
            if(me.getValue().containsKey(Mu1ti1ingu41.getPlugin().getConfig().getString("default-language"))) {
                Mu1ti1ingu41.getPlugin().getConfig().set("default-plugin-language." + me.getKey().toLowerCase(), Mu1ti1ingu41.getPlugin().getConfig().getString("default-language"));
                continue;
            }
            
            stopThisLoop:
            for(Map.Entry<String, FileConfiguration> me2: me.getValue().entrySet()) {
                Mu1ti1ingu41.getPlugin().getConfig().set("default-plugin-language." + me.getKey().toLowerCase(), me2.getKey().toLowerCase());
                Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Default Language in {0} not found. Set it to {1}", new Object[]{me.getKey(), me2.getKey()});
                break stopThisLoop;
            }
        }
    }
    private static void addFileToPlugin(File f, String plugin) {
        Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Check File {0} in folder {1}.", new Object[]{f.getName(), plugin});
        if(!_files.containsKey(plugin)) {
            _files.put(plugin, new HashMap<>());
            _languages.put(plugin, new HashMap<>());
        }
        
        String name = f.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0)
            name = name.substring(0, pos);
        
        FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
        if(fc == null)
            return;
        
        _files.get(plugin).put(name, f);
        _languages.get(plugin).put(name, fc);
        Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Language {0} for Plugin {1} has been loaded.", new Object[]{name, plugin});
    }
    
    public static ArrayList<String> getPluginLanguages(Plugin pl) {
        ArrayList<String> lang = new ArrayList<>();
        if(!_languages.containsKey(pl.getDescription().getName().toLowerCase()))
            return lang;
        
        for(Map.Entry<String, FileConfiguration> me: _languages.get(pl.getDescription().getName().toLowerCase()).entrySet())
            lang.add(me.getKey());
        return lang;
    }
    
    public static String getPluginDefaultLanguage(Plugin pl) {
        return Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + pl.getDescription().getName().toLowerCase(), "");
    }
    
    public static FileConfiguration getMessageFile(Plugin plugin, UUID uuid) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return null;
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return null;
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return null;
        
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(UUIDLanguages._players.get(uuid)))
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
        
        return fc;
    }
    
    public static String[] getMessages(Plugin plugin, UUID uuid, String path) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return new String[] {"Error on get Messages (11). Please Inform the Server Team."};
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return new String[] {"Error on get Messages (12). Please Inform the Server Team."};
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return new String[] {"Error on get Messages (13). Please Inform the Server Team."};
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(UUIDLanguages._players.get(uuid))) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
        }
        
        if(!fc.isList(path))
            return new String[] {"Error on get Messages (14). Please Inform the Server Team."};
        return (String[])fc.getStringList(path).toArray();
    }
    
    public static String getMsg(Plugin plugin, UUID uuid, String path, String defMsg) {
        return getMsg(plugin, uuid, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static String getMsg(Plugin plugin, UUID uuid, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return "Error on get Message (21). Please Inform the Server Team.";
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return "Error on get Message (22). Please Inform the Server Team.";
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return "Error on get Message (23). Please Inform the Server Team.";
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(UUIDLanguages._players.get(uuid))) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
        }
        
        if(fc.isConfigurationSection(path) && fc.isString(path + ".message"))
            return searchAndReplace(fc.getString(path + ".message"), search, replace);
            
        if(fc.getString(path, "").isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            return searchAndReplace(defMsg, search, replace);
        }
        
        return searchAndReplace(fc.getString(path), search, replace);
    }
    
    public static BaseComponent[] getMessage(Plugin plugin, UUID uuid, String path, String defMsg) {
        return getMessage(plugin, uuid, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static TextComponent getTextMessage(Plugin plugin, UUID uuid, String path, String defMsg) {
        return getTextMessage(plugin, uuid, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static TextComponent getTextMessage(Plugin plugin, UUID uuid, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return new TextComponent("Error on get Message (31). Please Inform the Server Team.");
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return new TextComponent("Error on get Message (32). Please Inform the Server Team.");
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return new TextComponent("Error on get Message (33). Please Inform the Server Team.");
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(UUIDLanguages._players.get(uuid))) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
        }
        
        if(fc.isConfigurationSection(path) && fc.isString(path + ".message")) {
            TextComponent msg = new TextComponent(searchAndReplace(fc.getString(path + ".message"), search, replace));
            if(fc.isString(path + ".hover-msg")) {
                msg.setHoverEvent(
                    new HoverEvent(
                        Utils.isHoverAction("show_" + fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(searchAndReplace(fc.getString(path + ".hover-msg"), search, replace)).create()
                    )
                );
            }
            if(fc.isString(path + ".click-msg")) {
                msg.setClickEvent(
                    new ClickEvent(
                        Utils.isClickAction(fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                        searchAndReplace(fc.getString(path + ".click-msg"), search, replace)
                    )
                );
            }
            return msg;
        }
            
        if(fc.getString(path, "").isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            return new TextComponent(searchAndReplace(defMsg, search, replace));
        }
        
        return new TextComponent(searchAndReplace(fc.getString(path), search, replace));
    }
    
    //NEW
    public static TextComponent getTextMessage(Plugin plugin, String countryCode, String path, String defMsg) {
        return getTextMessage(plugin, countryCode, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static TextComponent getTextMessage(Plugin plugin, String countryCode, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return new TextComponent("Error on get Message (41). Please Inform the Server Team.");
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return new TextComponent("Error on get Message (42). Please Inform the Server Team.");
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(countryCode)) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(countryCode);
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(countryCode);
        }
        
        if(fc.isConfigurationSection(path) && fc.isString(path + ".message")) {
            TextComponent msg = new TextComponent(searchAndReplace(fc.getString(path + ".message"), search, replace));
            if(fc.isString(path + ".hover-msg")) {
                msg.setHoverEvent(
                    new HoverEvent(
                        Utils.isHoverAction("show_" + fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(searchAndReplace(fc.getString(path + ".hover-msg"), search, replace)).create()
                    )
                );
            }
            if(fc.isString(path + ".click-msg")) {
                msg.setClickEvent(
                    new ClickEvent(
                        Utils.isClickAction(fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                        searchAndReplace(fc.getString(path + ".click-msg"), search, replace)
                    )
                );
            }
            return msg;
        }
            
        if(fc.getString(path, "").isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            return new TextComponent(searchAndReplace(defMsg, search, replace));
        }
        
        return new TextComponent(searchAndReplace(fc.getString(path), search, replace));
    }
    //NEW
    
    public static BaseComponent[] getMessage(Plugin plugin, UUID uuid, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return Mu1ti1ingu41.castMessage("Error on get Message (51). Please Inform the Server Team.");
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return Mu1ti1ingu41.castMessage("Error on get Message (52). Please Inform the Server Team.");
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return Mu1ti1ingu41.castMessage("Error on get Message (53). Please Inform the Server Team.");
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(UUIDLanguages._players.get(uuid))) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(UUIDLanguages._players.get(uuid));
        }
        
        if(fc.isConfigurationSection(path) && fc.isString(path + ".message")) {
            TextComponent msg = new TextComponent(searchAndReplace(fc.getString(path + ".message"), search, replace));
            if(fc.isString(path + ".hover-msg")) {
                msg.setHoverEvent(
                        new HoverEvent(
                        Utils.isHoverAction("show_" + fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(searchAndReplace(fc.getString(path + ".hover-msg"), search, replace)).create()
                    )
                );
            }
            if(fc.isString(path + ".click-msg")) {
                msg.setClickEvent(
                        new ClickEvent(
                        Utils.isClickAction(fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                        searchAndReplace(fc.getString(path + ".click-msg"), search, replace)
                    )
                );
            }
            return (new BaseComponent[] {msg});
        }
            
        if(fc.getString(path, "").isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            return Mu1ti1ingu41.castMessage(searchAndReplace(defMsg, search, replace));
        }
        
        return Mu1ti1ingu41.castMessage(searchAndReplace(fc.getString(path), search, replace));
    }
    
    public static String getMsg(Plugin plugin, String lang, String path, String defMsg) {
        return getMsg(plugin, lang, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static String getMsg(Plugin plugin, String lang, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return "Error on get Message (61). Please Inform the Server Team.";
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return "Error on get Message (62). Please Inform the Server Team.";
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(lang.toLowerCase())) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(lang.toLowerCase());
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(lang.toLowerCase());
        }
        
        if(fc.isConfigurationSection(path) && fc.isString(path + ".message"))
            return searchAndReplace(fc.getString(path + ".message"), search, replace);
        
        if(fc.getString(path, "").isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            return searchAndReplace(defMsg, search, replace);
        }
        
        return searchAndReplace(fc.getString(path), search, replace);
    }
    
    public static BaseComponent[] getMessage(Plugin plugin, String lang, String path, String defMsg) {
        return getMessage(plugin, lang, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static BaseComponent[] getMessage(Plugin plugin, String lang, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.getDescription().getName().toLowerCase()))
            return Mu1ti1ingu41.castMessage("Error on get Message (71). Please Inform the Server Team.");
        
        if(!Mu1ti1ingu41.getPlugin().getConfig().isString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()))
            return Mu1ti1ingu41.castMessage("Error on get Message (72). Please Inform the Server Team.");
        
        File f = _files.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        FileConfiguration fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.getDescription().getName().toLowerCase()));
        if(_languages.get(plugin.getDescription().getName().toLowerCase()).containsKey(lang.toLowerCase())) {
            f = _files.get(plugin.getDescription().getName().toLowerCase()).get(lang.toLowerCase());
            fc = _languages.get(plugin.getDescription().getName().toLowerCase()).get(lang.toLowerCase());
        }
        
        if(fc.isConfigurationSection(path) && fc.isString(path + ".message")) {
            TextComponent msg = new TextComponent(searchAndReplace(fc.getString(path + ",message"), search, replace));
            if(fc.isString(path + ".hover-msg")) {
                msg.setHoverEvent(
                        new HoverEvent(
                        Utils.isHoverAction("show_" + fc.getString(path + ".hover-type", "text"))?HoverEvent.Action.valueOf(("show_" + fc.getString(path + ".hover-type", "text")).toUpperCase()):HoverEvent.Action.SHOW_TEXT, 
                        new ComponentBuilder(searchAndReplace(fc.getString(path + ".hover-msg"), search, replace)).create()
                    )
                );
            }
            if(fc.isString(path + ".click-msg")) {
                msg.setClickEvent(
                        new ClickEvent(
                        Utils.isClickAction(fc.getString(path + ".click-type", "open_url"))?ClickEvent.Action.valueOf(fc.getString(path + ".click-type", "open_url").toUpperCase()):ClickEvent.Action.OPEN_URL, 
                        searchAndReplace(fc.getString(path + ".click-msg"), search, replace)
                    )
                );
            }
            return (new BaseComponent[] {msg});
        }
        
        if(fc.getString(path, "").isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            return Mu1ti1ingu41.castMessage(searchAndReplace(defMsg, search, replace));
        }
        
        return Mu1ti1ingu41.castMessage(searchAndReplace(fc.getString(path), search, replace));
    }
    
    private static void saveMissingPath(File f, FileConfiguration fc, String path, String defMsg) {
        fc.set(path, defMsg);
        saveConfigurationFile(fc, f);
    }

    private static void saveConfigurationFile(FileConfiguration conf, File f) {
        try {
            conf.save(f);
        } catch (IOException ex) {
            Mu1ti1ingu41.getPlugin().getLogger().log(Level.WARNING, "Can't save the file", ex);
        }
    }

    public static void setPlayerLanguage(UUID uuid, InetAddress ip) {
        UUIDLanguages._players.put(uuid, getLanguageByAddress(ip));
    }

    public static void setPlayerLanguage(UUID uuid) {
        String lang = "";
        if(ProxyServer.getInstance().getPlayer(uuid) == null)
            lang = Mu1ti1ingu41.getPlugin().getConfig().getString("default-language", "en");
        else
            lang = getLanguageByAddress(ProxyServer.getInstance().getPlayer(uuid).getPendingConnection().getAddress().getAddress());
        UUIDLanguages._players.put(uuid, lang);
    }
    
    public static String getLanguageByAddress(InetAddress ia) {
        File f = new File(Mu1ti1ingu41.getPlugin().getDataFolder(), "GeoIp.dat");
        if(!f.exists())
            checkGeoIP();
        
        try {
            LookupService cl = new LookupService(f, LookupService.GEOIP_MEMORY_CACHE);
            String code = cl.getCountry(ia).getCode();
            cl.close();
            if(!Mu1ti1ingu41.getPlugin().getConfig().getString("replace-languages." + code.toLowerCase(), "").isEmpty())
                return Mu1ti1ingu41.getPlugin().getConfig().getString("replace-languages." + code.toLowerCase());
            
            if(Mu1ti1ingu41.getPlugin().getConfig().getStringList("available-languages").contains(code.toLowerCase()))
                return code.toLowerCase();
        } catch(IOException e) {
            Mu1ti1ingu41.getPlugin().getLogger().log(Level.WARNING, "Cant find Country for IP " + ia.getHostAddress(), e);
        }
        return Mu1ti1ingu41.getPlugin().getConfig().getString("default-language", "en");
    }
    
    private static void checkGeoIP() {
        File f = new File(Mu1ti1ingu41.getPlugin().getDataFolder(), "GeoIp.dat");
        if(!f.exists() || Mu1ti1ingu41.getPlugin().getConfig().getLong("last-geo-update", 0) <= 0 || (System.currentTimeMillis()-1209600000) > Mu1ti1ingu41.getPlugin().getConfig().getLong("last-geo-update", 0)) {
            try {
                URL url = new URL("http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz");
                InputStream in = url.openConnection().getInputStream();
                File fgz = new File(Mu1ti1ingu41.getPlugin().getDataFolder(), "GeoIp.dat.gz");
                OutputStream out = new FileOutputStream(fgz);
                byte[] buffer = new byte[1024];
                int i;
                while ((i = in.read(buffer)) > 0) {
                    out.write(buffer, 0, i);
                }
                in.close();
                out.close();
                
                fgz = new File(Mu1ti1ingu41.getPlugin().getDataFolder(), "GeoIp.dat.gz");
                if(fgz.exists()) {
                    GZIPInputStream ingz = new GZIPInputStream(
                            new FileInputStream(fgz)
                    );

                    // Open the output file
                    OutputStream outgz = new FileOutputStream(f);

                    // Transfer bytes from the compressed file to the output file
                    byte[] buf = new byte[1024];
                    while ((i = ingz.read(buf)) > 0) {
                        outgz.write(buf, 0, i);
                    }
                    ingz.close();
                    outgz.close();
                }
                fgz.delete();
            } catch(IOException e) {
                Mu1ti1ingu41.getPlugin().getLogger().log(Level.WARNING, "Cant load GeoIp.dat file", e);
            }
        }
    }
    
    public static void loadDefaultLanguage(Plugin pl, String folder) {
        File lFold = new File(Mu1ti1ingu41.getPlugin().getDataFolder(), "languages/" + pl.getDescription().getName().toLowerCase());
        if(lFold.exists()) {
            Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Folder for {0} exist. Break the collect of default files. Remove it to load default files.", pl.getDescription().getName());
            return;
        }
         
        lFold.mkdirs();
        
        for(String srcFile: ResourceList.getResourceFiles(pl.getClass(), folder)) {
            String name = srcFile;
            int pos = name.lastIndexOf("/");
            if(pos > 0)
                name = name.substring(pos+1);
            
            try {
                InputStream in = pl.getResourceAsStream(srcFile);
                int c = -1;
                File spLang = new File(lFold, name);
                OutputStream os = new FileOutputStream(spLang);
                while((c = in.read()) != -1)
                    os.write(c);
                
                os.close();
                in.close();
                
//                if(name.lastIndexOf(".") > -1)
//                    name = name.substring(0, name.lastIndexOf("."));
                addFileToPlugin(spLang, pl.getDescription().getName().toLowerCase());
            } catch (IOException ex) {
                Mu1ti1ingu41.getPlugin().getLogger().log(Level.SEVERE, "Error on loading default language " + name, ex);
            }
        }
        checkDefaultFiles();
    }
    
    public static HashMap<String, HashMap<String, File>> getFileList() {
        return _files;
    }
    
    public static BaseComponent[] convertString(String msg) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg));
    }
    
    private static String searchAndReplace(String msg, String[] search, String[] replace) {
        if(search.length > 0 && search.length == replace.length) {
            for(int i = 0; i < search.length; i++)
                msg = msg.replaceAll(search[i], replace[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
