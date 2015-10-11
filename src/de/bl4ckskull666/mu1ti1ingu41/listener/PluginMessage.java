/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.mu1ti1ingu41.UUIDLanguages;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 *
 * @author PapaHarni
 */
public class PluginMessage implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("BungeeCord"))
            return;
        
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String sub = in.readUTF();
        if(!sub.equalsIgnoreCase("Mu1ti1ingu41"))
            return;
        
        UUID uuid = UUID.fromString(in.readUTF());
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Mu1ti1ingu41");
        out.writeUTF("player");
        out.writeUTF(uuid.toString());
        out.writeUTF(UUIDLanguages.getPlayerLanguage(uuid));
        
        for(Map.Entry<String, ServerInfo> me: ProxyServer.getInstance().getServers().entrySet()) {
            me.getValue().sendData("BungeeCord", out.toByteArray(), true);
        }
    }
    
    private ProxiedPlayer getPlayer(InetSocketAddress con) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().equals(con))
                return pp;
        }
        return null;
    }
}
