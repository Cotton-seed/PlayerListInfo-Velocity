package com.example.playerlistinfo;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import net.kyori.adventure.text.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerListInfo {
    private final ProxyServer server;

    @Inject
    public PlayerListInfo(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        int totalCount = server.getAllPlayers().size();
        int lobbyCount = getPlayerCount("lobby");

        // 创建自定义悬停信息
        List<ServerPing.SamplePlayer> samplePlayers = new ArrayList<>();
        samplePlayers.add(new ServerPing.SamplePlayer(
                "§e当前玩家总数：§f" + totalCount + "人",
                UUID.randomUUID()
        ));
        samplePlayers.add(new ServerPing.SamplePlayer(
                "§b排队玩家人数：§f" + lobbyCount + "人",
                UUID.randomUUID()
        ));

        // 获取原始ping信息并创建构建器
        ServerPing originalPing = event.getPing();
        ServerPing.Builder builder = ServerPing.builder()
                .version(originalPing.getVersion())
                .description(originalPing.getDescriptionComponent())
                .onlinePlayers(totalCount)
                .maximumPlayers(2025)
                .samplePlayers(samplePlayers);

        // 如果有Favicon则添加
        originalPing.getFavicon().ifPresent(builder::favicon);

        // 设置修改后的ping信息
        event.setPing(builder.build());
    }

    private int getPlayerCount(String serverName) {
        Optional<RegisteredServer> targetServer = server.getServer(serverName);
        return targetServer.map(server -> server.getPlayersConnected().size()).orElse(0);
    }
}