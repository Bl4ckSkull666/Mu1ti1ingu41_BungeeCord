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
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
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
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("Mu1ti1ingu41"))
            return;
        
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String sub = in.readUTF();
        if(!sub.equalsIgnoreCase("request"))
            return;
        
        UUID uuid = UUID.fromString(in.readUTF());
        ProxiedPlayer pp = getPlayer(e.getReceiver().getAddress());
        if(pp == null)
            pp = getPlayer(e.getSender().getAddress());
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("player");
        out.writeUTF(uuid.toString());
        out.writeUTF(UUIDLanguages.getPlayerLanguage(uuid));
        pp.getServer().sendData("Mu1ti1ingu41", out.toByteArray());
        
    }
    
    private ProxiedPlayer getPlayer(InetSocketAddress con) {
        for(ProxiedPlayer pp: ProxyServer.getInstance().getPlayers()) {
            if(pp.getPendingConnection().getAddress().equals(con))
                return pp;
        }
        return null;
    }
}
