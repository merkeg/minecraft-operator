package de.merkeg.openmc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.fabric8.kubernetes.client.dsl.ServiceResource;
import io.fabric8.kubernetes.client.dsl.base.PatchContext;
import io.fabric8.kubernetes.client.dsl.base.PatchType;
import jakarta.json.Json;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

@Slf4j
public class KubernetesUtil {

  @SneakyThrows
  public static <T extends HasMetadata> T setDesiredResource(KubernetesClient client, T desired) {
    ObjectMeta metadata = desired.getMetadata();

    Resource<T> resource = client.resource(desired);
    T current = resource.get();

    if(current == null) {
      return client.resource(desired).create();
    }

    log.info("Spec changed for resource {}/{}, updating information", getType(desired), metadata.getName());
    return resource.patch(PatchContext.of(PatchType.STRATEGIC_MERGE), desired);
  }

  public static String getType(Object o) {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode obj = mapper.valueToTree(o);
    return obj.get("apiVersion").asText() + "/" + obj.get("kind").asText();
  }

}
