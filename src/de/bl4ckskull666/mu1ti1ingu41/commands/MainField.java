/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41.commands;

import de.bl4ckskull666.mu1ti1ingu41.Language;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import de.bl4ckskull666.mu1ti1ingu41.UUIDLanguages;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 *
 * @author PapaHarni
 */
public final class MainField {
    public static void useCommand(CommandSender s, String[] a) {
        if(a.length > 0) {
            if(a[0].equalsIgnoreCase("reload")) {
                UUID uuid = s instanceof ProxiedPlayer?((ProxiedPlayer)s).getUniqueId():UUID.fromString("00000000-0000-0000-0000-000000000000");
                if(!s.hasPermission("mu1ti1ing41.admin")) {
                    s.sendMessage(Language.getMessage(Mu1ti1ingu41.name(), uuid, "command.reload.no-permission", "You dont have permission to use this command."));
                    return;
                }
                
                Language.loadLanguage();
                s.sendMessage(Language.getMessage(Mu1ti1ingu41.name(), uuid, "command.reload.successful", "Language files has been reloaded."));
                return;
            }
        }
        
        if(!(s instanceof ProxiedPlayer)) {
            s.sendMessage(Mu1ti1ingu41.castMessage("This command can only run by a player."));
            return;
        }
        
        ProxiedPlayer p = (ProxiedPlayer)s;
        if(a.length < 1) {
            p.sendMessage(Language.getMessage(Mu1ti1ingu41.name(), p.getUniqueId(), "command.need-language", "Please select one of the following language :"));
            String ava = "";
            for(String str: Mu1ti1ingu41.getPlugin().getConfig().getSection("short-language").getKeys())
                ava += ava.isEmpty()?"§e":"§9, §e" + str;
            p.sendMessage(Mu1ti1ingu41.castMessage(ava));
            return;
        }
        
        if(Mu1ti1ingu41.getPlugin().getConfig().getString("short-language." + a[0].toLowerCase(), "").isEmpty()) {
            p.sendMessage(Language.getMessage(Mu1ti1ingu41.name(), p.getUniqueId(), "command.unknown-language", "Can't find the wished language."));
            return;
        }
        
        UUIDLanguages._players.put(p.getUniqueId(), Mu1ti1ingu41.getPlugin().getConfig().getString("short-language." + a[0].toLowerCase()));
        p.sendMessage(Language.getMessage(Mu1ti1ingu41.name(), p.getUniqueId(), "command.changed-language", "The Wished Language has been changed."));
    }
}
