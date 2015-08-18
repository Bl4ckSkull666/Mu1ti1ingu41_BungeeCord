/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.mu1ti1ingu41.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.bl4ckskull666.mu1ti1ingu41.Mu1ti1ingu41;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public final class PingTasks { 
    private static class PingTask implements Runnable {
        private final ServerInfo _server;
        private boolean _online = false;
        private boolean _lastStatus = false;
        
        public PingTask(ServerInfo server) {
            _server = server;
        }

        public boolean isOnline() {
            return _online;
        }

        public ServerInfo getServer() {
            return _server;
        }

        @Override
        public void run() {
            _server.ping(new Callback<ServerPing>() {
                @Override
                public void done(ServerPing v, Throwable thrwbl) {
                    if (thrwbl != null || v == null) {
                        _online = false;
                        return;
                    }
                    _online = true;
                }

            });

            if(_lastStatus != _online && _online) {
                //Send Config to Server
                ProxyServer.getInstance().getScheduler().schedule(Mu1ti1ingu41.getPlugin(), new PingTaskSendConfig(_server), 60, TimeUnit.SECONDS);
            }
            _lastStatus = _online;
        }
    }
    
    private static class PingTaskSendConfig implements Runnable {
        private final ServerInfo _server;
        public PingTaskSendConfig(ServerInfo server) {
            _server = server;
        }

        @Override
        public void run() {
            for(String str: Mu1ti1ingu41.getPlugin().getConfig().getSection("bukkit-configs").getKeys()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("MU1TI1INGU41");
                out.writeUTF(str);
                out.writeUTF(Mu1ti1ingu41.getPlugin().getConfig().getString("bukkit-configs." + str));
                _server.sendData("BungeeCord", out.toByteArray());
            }
        }
    }
    
    //Statics
    private static final HashMap<PingTask, ScheduledTask> _serverTasks = new HashMap<>();
    public static void startTasks() {
        for(Map.Entry<String, ServerInfo> me: ProxyServer.getInstance().getServers().entrySet()) {
            PingTask pTask = new PingTask(me.getValue());
            ScheduledTask sTask = ProxyServer.getInstance().getScheduler().schedule(Mu1ti1ingu41.getPlugin(), pTask, 10, 10, TimeUnit.SECONDS);
            _serverTasks.put(pTask, sTask);
        }
    }
    
    public static void stopTasks() {
        for(Map.Entry<PingTask, ScheduledTask> me: _serverTasks.entrySet()) {
            if(me.getValue() != null)
                me.getValue().cancel();
        }
    }
    
    
}
