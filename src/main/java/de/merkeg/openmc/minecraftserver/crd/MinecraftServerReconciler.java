package de.merkeg.openmc.minecraftserver.crd;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.merkeg.openmc.minecraftserver.deployment.MinecraftServerDeployment;
import de.merkeg.openmc.util.KubernetesUtil;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.PatchContext;
import io.fabric8.kubernetes.client.dsl.base.PatchType;
import io.javaoperatorsdk.operator.api.config.informer.Informer;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.quarkiverse.operatorsdk.annotations.CSVMetadata;
import io.quarkiverse.operatorsdk.annotations.RBACRule;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

@CSVMetadata(
        bundleName = "minecraft-operator",
        requiredCRDs = @CSVMetadata.RequiredCRD(
                kind = "MinecraftServer",
                name = MinecraftServer.NAME,
                version = MinecraftServer.VERSION
        )
)
@ControllerConfiguration(
        informer = @Informer(namespaces = Constants.WATCH_ALL_NAMESPACES)
)
@RBACRule(
        apiGroups = MinecraftServer.GROUP,
        resources = "minecraftservers",
        verbs = RBACRule.ALL
)
@ApplicationScoped
@Slf4j
public class MinecraftServerReconciler implements Reconciler<MinecraftServer> {
  @Override
  public UpdateControl<MinecraftServer> reconcile(MinecraftServer minecraftServer, Context<MinecraftServer> context) throws Exception {
    MinecraftServerStatus status = minecraftServer.getStatus();
    KubernetesClient client = context.getClient();

    ObjectMapper mapper = new ObjectMapper();
    String currentSpecJson = mapper.writeValueAsString(minecraftServer.getSpec());
    String currentSpecHash = DigestUtils.md5Hex(currentSpecJson);

    if(!currentSpecHash.equals(status.getLastAppliedHash())) {
      Deployment desiredDeployment = MinecraftServerDeployment.createDeployment(minecraftServer);
      KubernetesUtil.setDesiredResource(client, desiredDeployment);

      Service desiredService = MinecraftServerDeployment.createService(minecraftServer);
      KubernetesUtil.setDesiredResource(client, desiredService);

      status.setLastAppliedHash(currentSpecHash);
      minecraftServer.setStatus(status);
      return UpdateControl.patchStatus(minecraftServer);
    }

    return UpdateControl.noUpdate();
  }

  private UpdateControl<MinecraftServer> returnUpdate(MinecraftServer minecraftServer, boolean isReady, String message) {
    MinecraftServerStatus status = minecraftServer.getStatus();
    status.setReady(isReady);
    status.setMessage(message);
    return UpdateControl.patchStatus(minecraftServer);
  }

  private boolean checkDeploymentReady(Deployment deployment) {
    if (deployment.getStatus() == null) {
      return false;
    }
    Integer desiredReplicas = deployment.getSpec().getReplicas();
    Integer availableReplicas = deployment.getStatus().getAvailableReplicas();
    return availableReplicas != null && desiredReplicas != null && availableReplicas >= desiredReplicas;
  }
}
