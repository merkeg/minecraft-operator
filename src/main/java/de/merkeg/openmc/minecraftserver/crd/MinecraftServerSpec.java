package de.merkeg.openmc.minecraftserver.crd;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import de.merkeg.openmc.minecraftserver.data.ServerEngine;
import de.merkeg.openmc.minecraftserver.data.ServerProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MinecraftServerSpec {

  @JsonPropertyDescription("Minecraft version to be used")
  @NotNull
  private String minecraftVersion;

  @JsonPropertyDescription("Engine of the minecraft server")
  private ServerEngine serverEngine = ServerEngine.PAPER;

  @JsonPropertyDescription("Engine of the minecraft server")
  private String serverImage = "itzg/minecraft-server";

  @JsonPropertyDescription("Engine of the minecraft server")
  private String group = "freegame";

  @JsonPropertyDescription("Plugins to load")
  private List<String> plugins;

  @JsonPropertyDescription("Mods to load")
  private List<String> mods;

  @JsonPropertyDescription("Worlds to add")
  private List<String> worlds;

  @JsonPropertyDescription("Base server properties")
  private ServerProperties properties = new ServerProperties();

}
