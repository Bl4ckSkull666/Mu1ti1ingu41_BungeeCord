/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41;

import com.maxmind.geoip.LookupService;
import de.bl4ckskull666.mu1ti1ingu41.utils.ResourceList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 *
 * @author PapaHarni
 */
public final class Language {
    private final static HashMap<String, HashMap<String, Configuration>> _languages = new HashMap<>();
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
        
        for(Map.Entry<String, HashMap<String, Configuration>> me: _languages.entrySet()) {
            if(me.getValue().containsKey(Mu1ti1ingu41.getPlugin().getConfig().getString("default-language"))) {
                Mu1ti1ingu41.getPlugin().getConfig().set("default-plugin-language." + me.getKey().toLowerCase(), Mu1ti1ingu41.getPlugin().getConfig().getString("default-language"));
                continue;
            }
            
            stopThisLoop:
            for(Map.Entry<String, Configuration> me2: me.getValue().entrySet()) {
                Mu1ti1ingu41.getPlugin().getConfig().set("default-plugin-language." + me.getKey().toLowerCase(), me2.getKey().toLowerCase());
                Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Default Language in {0} not found. Set it to {1}", new Object[]{me.getKey(), me2.getKey()});
                break stopThisLoop;
            }
        }
        Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Languages has been loaded.");
    }
    
    
    private static void addFileToPlugin(File f, String plugin) {
        if(!_files.containsKey(plugin)) {
            _files.put(plugin, new HashMap<>());
            _languages.put(plugin, new HashMap<>());
        }
        
        String name = f.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0)
            name = name.substring(0, pos);
        
        Configuration fc = CastFileToConfiguration(f);
        if(fc == null)
            return;
        
        _files.get(plugin).put(name, f);
        _languages.get(plugin).put(name, fc);
        Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Language {0} for Plugin {1} has been loaded.", new Object[]{name, plugin});
    }
    
    private static Configuration CastFileToConfiguration(File file) {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException ex) {
            Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Can''t cast {0} to Configuration.", file.getName());
            return null;
        }
    }
    
    public static Configuration getMessageFile(String plugin, UUID uuid) {
        if(!_languages.containsKey(plugin))
            return null;
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return null;
        
        if(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase(), "").isEmpty())
            return null;
        
        Configuration fc = _languages.get(plugin).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase()));
        if(_languages.get(plugin).containsKey(UUIDLanguages._players.get(uuid)))
            fc = _languages.get(plugin).get(UUIDLanguages._players.get(uuid));
        
        return fc;
    }
    
    public static String[] getMessages(String plugin, UUID uuid, String path) {
        if(!_languages.containsKey(plugin))
            return new String[] {"Error on get Messages (11). Please Inform the Server Team."};
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return new String[] {"Error on get Messages (12). Please Inform the Server Team."};
        
        if(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase(), "").isEmpty())
            return new String[] {"Error on get Messages (13). Please Inform the Server Team."};
        
        File f = _files.get(plugin).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase()));
        Configuration fc = _languages.get(plugin).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase()));
        if(_languages.get(plugin).containsKey(UUIDLanguages._players.get(uuid))) {
            f = _files.get(plugin).get(UUIDLanguages._players.get(uuid));
            fc = _languages.get(plugin).get(UUIDLanguages._players.get(uuid));
        }
        
        if(fc.getStringList(path).isEmpty())
            return new String[] {"Error on get Messages (14). Please Inform the Server Team."};
        return (String[])fc.getStringList(path).toArray();
    }
    
    public static BaseComponent[] getMessage(String plugin, UUID uuid, String path, String defMsg) {
        return getMessage(plugin, uuid, path, defMsg, new String[] {}, new String[] {});
    }
    
    public static BaseComponent[] getMessage(String plugin, UUID uuid, String path, String defMsg, String[] search, String[] replace) {
        if(!_languages.containsKey(plugin.toLowerCase()))
            return Mu1ti1ingu41.castMessage("Error on get Message (01). Please Inform the Server Team.");
        
        if(!UUIDLanguages._players.containsKey(uuid))
            setPlayerLanguage(uuid);
        
        if(!UUIDLanguages._players.containsKey(uuid))
            return Mu1ti1ingu41.castMessage("Error on get Message (02). Please Inform the Server Team.");
        
        if(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase(), "").isEmpty())
            return Mu1ti1ingu41.castMessage("Error on get Message (03). Please Inform the Server Team.");
        
        File f = _files.get(plugin.toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase()));
        Configuration fc = _languages.get(plugin.toLowerCase()).get(Mu1ti1ingu41.getPlugin().getConfig().getString("default-plugin-language." + plugin.toLowerCase()));
        if(_languages.get(plugin.toLowerCase()).containsKey(UUIDLanguages._players.get(uuid))) {
            f = _files.get(plugin.toLowerCase()).get(UUIDLanguages._players.get(uuid));
            fc = _languages.get(plugin.toLowerCase()).get(UUIDLanguages._players.get(uuid));
        }
            
        if(fc.getStringList(path).isEmpty()) {
            saveMissingPath(f, fc, path, defMsg);
            if(search.length > 0 && search.length == replace.length) {
                for(int i = 0; i < search.length; i++)
                    defMsg = defMsg.replaceAll(search[i], replace[i]);
            }
            return Mu1ti1ingu41.castMessage(defMsg);
        }
        
        String msg = fc.getString(path);
        if(search.length > 0 && search.length == replace.length) {
            for(int i = 0; i < search.length; i++)
                msg = msg.replaceAll(search[i], replace[i]);
        }
        return Mu1ti1ingu41.castMessage(msg);
    }
    
    private static void saveMissingPath(File f, Configuration fc, String path, String defMsg) {
        fc.set(path, defMsg);
        saveConfigurationFile(fc, f);
    }
    
    private static void saveConfigurationFile(Configuration conf, File f) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(conf, f);
        } catch (IOException ex) {
            Mu1ti1ingu41.getPlugin().getLogger().log(Level.WARNING, "Can't save the file", ex);
        }
    }
    
    public static void setPlayerLanguage(UUID uuid) {
        Mu1ti1ingu41 p = Mu1ti1ingu41.getPlugin();
        File f = new File(p.getDataFolder(), "GeoIp.dat");
        if(!f.exists())
            checkGeoIP();
        
        InetAddress ip = ProxyServer.getInstance().getPlayer(uuid).getPendingConnection().getAddress().getAddress();
        try {
            LookupService cl = new LookupService(f, LookupService.GEOIP_MEMORY_CACHE);
            String code = cl.getCountry(ip).getCode();
            cl.close();
            if(!p.getConfig().getString("replace-languages." + code.toLowerCase(), "").isEmpty()) {
                UUIDLanguages._players.put(uuid, p.getConfig().getString("replace-languages." + code.toLowerCase()));
                return;
            }
            
            if(p.getConfig().getStringList("available-languages").contains(code.toLowerCase())) {
                UUIDLanguages._players.put(uuid, code.toLowerCase());
                return;
            }
        } catch(IOException e) {
            p.getLogger().log(Level.WARNING, "Cant find Country for IP " + ip.getHostAddress(), e);
        }
        UUIDLanguages._players.put(uuid, p.getConfig().getString("default-language", "en"));
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
            Mu1ti1ingu41.getPlugin().getLogger().log(Level.INFO, "Folder for " + pl.getDescription().getName() + " exist. Break the collect of default files. Remove it to load default files.");
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
                FileWriter fw = new FileWriter(spLang);
                BufferedWriter bw = new BufferedWriter(fw);
                while((c = in.read()) != -1)
                    bw.write(String.valueOf((char)c));
                
                Configuration fcLang = CastFileToConfiguration(spLang);
                saveConfigurationFile(fcLang, spLang);
                
                if(name.lastIndexOf(".") > -1)
                    name = name.substring(0, name.lastIndexOf("."));
                addFileToPlugin(spLang, pl.getDescription().getName().toLowerCase().toLowerCase());
            } catch (IOException ex) {
                Mu1ti1ingu41.getPlugin().getLogger().log(Level.SEVERE, "Error on loading default language " + name, ex);
            }
        }
    }
    
    public static HashMap<String, HashMap<String, File>> getFileList() {
        return _files;
    }
}
