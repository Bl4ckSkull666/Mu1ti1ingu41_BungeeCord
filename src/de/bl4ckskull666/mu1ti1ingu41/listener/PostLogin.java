/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.bl4ckskull666.mu1ti1ingu41.UUIDLanguages;
import java.util.UUID;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class PostLogin implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPostLogin(PostLoginEvent e) {
        if(!UUIDLanguages._players.containsKey(e.getPlayer().getUniqueId())) {
            Language.setPlayerLanguage(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(Language.getMessage(Mu1ti1ingu41.getPlugin(),
                            e.getPlayer().getUniqueId(),
                            "auto-language",
                            "Set your language to %language%",
                            new String[] {"%language%"},
                            new String[] {getLanguageName(UUIDLanguages.getPlayerLanguage(e.getPlayer().getUniqueId()))}
                    )
            );
        }
        if(e.getPlayer().getServer() != null && e.getPlayer().getServer().getInfo() != null)
            sendPlayerLanguageToServer(e.getPlayer().getServer().getInfo(), e.getPlayer().getUniqueId(), UUIDLanguages._players.get(e.getPlayer().getUniqueId()));
    }
    
    public void onServerSwitch(ServerSwitchEvent e) {
        if(e.getPlayer().getReconnectServer() != null)
            sendPlayerLanguageToServer(e.getPlayer().getReconnectServer(), e.getPlayer().getUniqueId(), UUIDLanguages._players.get(e.getPlayer().getUniqueId()));
        if(e.getPlayer().getServer() != null && e.getPlayer().getServer().getInfo() != null)
            sendPlayerLanguageToServer(e.getPlayer().getServer().getInfo(), e.getPlayer().getUniqueId(), UUIDLanguages._players.get(e.getPlayer().getUniqueId()));
    }
    
    private void sendPlayerLanguageToServer(ServerInfo si, UUID uuid, String language) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MU1TI1INGU41");
        out.writeUTF(uuid.toString());
        out.writeUTF(language);
        si.sendData("BungeeCord", out.toByteArray());
    }
    
    private String getLanguageName(String shortLang) {
        for(String key: Mu1ti1ingu41.getPlugin().getConfig().getSection("short-language").getKeys()) {
            if(Mu1ti1ingu41.getPlugin().getConfig().getString("short-language." + key).equalsIgnoreCase(shortLang))
                return key;
        }
        return shortLang;
    }
}
