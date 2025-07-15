package de.merkeg.openmc.minecraftserver.deployment;

import de.merkeg.openmc.minecraftserver.crd.MinecraftServer;
import de.merkeg.openmc.minecraftserver.crd.MinecraftServerSpec;
import de.merkeg.openmc.minecraftserver.data.ServerProperties;
import de.merkeg.openmc.util.EnvKey;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;

import java.lang.reflect.Field;
import java.util.*;

public class MinecraftServerDeployment {

  public static Deployment createDeployment(MinecraftServer server) {
    MinecraftServerSpec spec = server.getSpec();

    Container container = new Container();
    container.setName("minecraft-server");
    container.setImage(spec.getServerImage());

    List<EnvVar> envVars = new ArrayList<>();
    envVars.add(env("VERSION", spec.getMinecraftVersion()));
    envVars.add(env("TYPE", spec.getServerEngine().toString()));
    envVars.add(env("EULA", "true"));
    envVars.add(env("SERVER_ID", server.getMetadata().getName()));

    if(spec.getProperties() != null) {
      envVars.addAll(propertiesEnv(spec.getProperties()));

      if(spec.getProperties().getWhitelist() != null && !spec.getProperties().getWhitelist().isEmpty()) {
        envVars.add(env("WHITELIST", String.join(",", spec.getProperties().getWhitelist())));
      }

      if(spec.getProperties().getOperators() != null && !spec.getProperties().getOperators().isEmpty()) {
        envVars.add(env("OPS", String.join(",", spec.getProperties().getOperators())));
      }
    }


    container.setEnv(envVars);

    ContainerPort port = new ContainerPort();
    port.setName("minecraft");
    port.setContainerPort(25565);
    container.setPorts(Collections.singletonList(port));

    PodSpec podSpec = new PodSpec();
    podSpec.setContainers(Collections.singletonList(container));

    PodTemplateSpec podTemplate = new PodTemplateSpec();
    podTemplate.setMetadata(new ObjectMeta());
    podTemplate.getMetadata().setLabels(Collections.singletonMap("serverId", server.getMetadata().getName()));
    podTemplate.setSpec(podSpec);

    LabelSelector selector = new LabelSelector();
    selector.setMatchLabels(Collections.singletonMap("serverId", server.getMetadata().getName()));

    DeploymentSpec deploymentSpec = new DeploymentSpec();
    deploymentSpec.setReplicas(1);
    deploymentSpec.setTemplate(podTemplate);
    deploymentSpec.setSelector(selector);

    ObjectMeta metadata = new ObjectMeta();
    metadata.setName(server.getMetadata().getName());
    metadata.setNamespace(server.getMetadata().getNamespace());

    Deployment deployment = new Deployment();
    deployment.setMetadata(metadata);
    deployment.setSpec(deploymentSpec);

    deployment.addOwnerReference(server);

    return deployment;
  }

  public static Service createService(MinecraftServer server) {
    ObjectMeta metadata = new ObjectMeta();
    metadata.setName(server.getMetadata().getName());
    metadata.setNamespace(server.getMetadata().getNamespace());

    ServiceSpec spec = new ServiceSpec();

    Map<String, String> selectors = new HashMap<>();
    selectors.put("serverId", server.getMetadata().getName());
    spec.setSelector(selectors);

    List<ServicePort> ports = new ArrayList<>();

    ServicePort gamePort = new ServicePort();
    gamePort.setName("minecraft");
    gamePort.setPort(25565);
    gamePort.setTargetPort(new IntOrString("minecraft"));
    gamePort.setProtocol("TCP");
    ports.add(gamePort);

    spec.setPorts(ports);

    Service service = new Service();
    service.setMetadata(metadata);
    service.setSpec(spec);

    service.addOwnerReference(server);

    return service;
  }

  public static List<EnvVar> propertiesEnv(ServerProperties properties) {
    List<EnvVar> envs = new ArrayList<>();
    Field[] fields = ServerProperties.class.getDeclaredFields();

    for (Field field : fields) {
      field.setAccessible(true);
      EnvKey annotation = field.getAnnotation(EnvKey.class);
      if (annotation == null) {
        continue; // Feld ohne EnvKey ignorieren
      }
      try {
        Object value = field.get(properties);
        if (value != null) {
          envs.add(env(annotation.value(), value.toString()));
        }
      } catch (IllegalAccessException e) {
      }
    }
    return envs;
  }

  public static EnvVar env(String name, String value) {
    EnvVar envVar = new EnvVar();
    envVar.setName(name);
    envVar.setValue(value);
    return envVar;
  }
}
