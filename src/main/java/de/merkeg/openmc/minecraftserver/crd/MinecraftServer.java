package de.merkeg.openmc.minecraftserver.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group(MinecraftServer.GROUP)
@Version(MinecraftServer.VERSION)
@ShortNames("Minecraft")
public class MinecraftServer extends CustomResource<MinecraftServerSpec, MinecraftServerStatus> implements Namespaced {

  public static final String GROUP = "openmc.merkeg.de";
  public static final String VERSION = "v1";
  public static final String NAME = "minecraftservers."+GROUP;
  @Override
  protected MinecraftServerStatus initStatus() {
    return new MinecraftServerStatus();
  }

  @Override
  protected MinecraftServerSpec initSpec() {
    return new MinecraftServerSpec();
  }
}
