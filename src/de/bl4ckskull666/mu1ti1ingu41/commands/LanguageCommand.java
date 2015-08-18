/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class LanguageCommand extends Command {
    
    public LanguageCommand() {
        super("language", "");
    }
    
    @Override
    public void execute(CommandSender s, String[] a) {
        MainField.useCommand(s, a);
    }
}