/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.bl4ckskull666.mu1ti1ingu41.UUIDLanguages;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author PapaHarni
 */
public final class MainField {
    public static void useCommand(CommandSender s, String[] a) {
        if(a.length > 0) {
            if(a[0].equalsIgnoreCase("reload")) {
                if(!s.hasPermission("mu1ti1ing41.admin")) {
                    Language.sendMessage(Mu1ti1ingu41.getPlugin(), s, "command.reload.no-permission", "You dont have permission to use this command.");
                    return;
                }
                
                Language.loadLanguage();
                
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Mu1ti1ingu41");
                out.writeUTF("reload");
                
                for(Map.Entry<String, ServerInfo> me: ProxyServer.getInstance().getServers().entrySet())
                    me.getValue().sendData("BungeeCord", out.toByteArray(), true);

                Language.sendMessage(Mu1ti1ingu41.getPlugin(), s, "command.reload.successful", "Language files has been reloaded.");
                return;
            } else if(a[0].equalsIgnoreCase("test")) {
                
            }
        }
        
        if(!(s instanceof ProxiedPlayer)) {
            s.sendMessage(Mu1ti1ingu41.castMessage("This command can only run by a player."));
            return;
        }
        
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(a.length < 1) {
            Language.sendMessage(Mu1ti1ingu41.getPlugin(), p, "command.need-language", "Please select one of the following language :");
            String ava = "";
            for(String str: Mu1ti1ingu41.getPlugin().getConfig().getConfigurationSection("short-language").getKeys(false))
                ava += (ava.isEmpty()?"§e":"§9, §e") + str;
            p.sendMessage(Mu1ti1ingu41.castMessage(ava));
            return;
        }
        
        if(Mu1ti1ingu41.getPlugin().getConfig().getString("short-language." + a[0].toLowerCase(), "").isEmpty()) {
            Language.sendMessage(Mu1ti1ingu41.getPlugin(), p, "command.unknown-language", "Can't find the wished language.");
            return;
        }
        
        UUIDLanguages._players.put(p.getUniqueId(), Mu1ti1ingu41.getPlugin().getConfig().getString("short-language." + a[0].toLowerCase()));
        
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Mu1ti1ingu41");
        out.writeUTF("player");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(Mu1ti1ingu41.getPlugin().getConfig().getString("short-language." + a[0].toLowerCase()));
        
        for(Map.Entry<String, ServerInfo> me: ProxyServer.getInstance().getServers().entrySet()) {
            me.getValue().sendData("BungeeCord", out.toByteArray(), true);
        }
        
        Language.sendMessage(Mu1ti1ingu41.getPlugin(), p, "command.changed-language", "The Wished Language has been changed.");
    }
}
