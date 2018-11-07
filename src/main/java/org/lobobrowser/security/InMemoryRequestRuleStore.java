package org.lobobrowser.security;

import org.cobraparser.ua.UserAgentContext.RequestKind;
import org.javatuples.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRequestRuleStore implements RequestRuleStore {
  private final Map<String, Map<String, PermissionSystem.Permission[]>> store = new HashMap<>();
  private static final PermissionSystem.Permission[] defaultPermissions = new PermissionSystem.Permission[RequestKind.numKinds() + 1];
  static {
    for (int i = 0; i < defaultPermissions.length; i++) {
      defaultPermissions[i] = PermissionSystem.Permission.Undecided;
    }
  }
  private static final Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> defaultPermissionPair = Pair.with(PermissionSystem.Permission.Undecided, defaultPermissions);

  static private InMemoryRequestRuleStore instance = new InMemoryRequestRuleStore();

  public static InMemoryRequestRuleStore getInstance() {
    instance.dump();
    return instance;
  }

  public InMemoryRequestRuleStore() {
    HelperPrivate.initStore(this);
  }

  public synchronized Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> getPermissions(final String frameHostPattern, final String requestHost) {
    final Map<String, PermissionSystem.Permission[]> reqHostMap = store.get(frameHostPattern);
    if (reqHostMap != null) {
      final PermissionSystem.Permission[] permissions = reqHostMap.get(requestHost);
      if (permissions != null) {
        return Pair.with(permissions[0], Arrays.copyOfRange(permissions, 1, permissions.length));
      } else {
        return defaultPermissionPair;
      }
    } else {
      return defaultPermissionPair;
    }
  }

  public synchronized void storePermissions(final String frameHostPattern, final String requestHost, final Optional<RequestKind> kindOpt,
      final PermissionSystem.Permission permission) {
    final int index = kindOpt.map(k -> k.ordinal() + 1).orElse(0);
    final Map<String, PermissionSystem.Permission[]> reqHostMap = store.get(frameHostPattern);
    if (reqHostMap != null) {
      final PermissionSystem.Permission[] permissions = reqHostMap.get(requestHost);
      if (permissions != null) {
        permissions[index] = permission;
      } else {
        addPermission(requestHost, index, permission, reqHostMap);
      }
    } else {
      final Map<String, PermissionSystem.Permission[]> newReqHostMap = new HashMap<>();
      addPermission(requestHost, index, permission, newReqHostMap);
      store.put(frameHostPattern, newReqHostMap);
    }
  }

  private static void addPermission(final String requestHost, final int index, final PermissionSystem.Permission permission,
      final Map<String, PermissionSystem.Permission[]> reqHostMap) {
    final PermissionSystem.Permission[] newPermissions = Arrays.copyOf(defaultPermissions, defaultPermissions.length);
    newPermissions[index] = permission;
    reqHostMap.put(requestHost, newPermissions);
  }

  private void dump() {
    System.out.println("Store: ");
    store.forEach((key, value) -> {
      System.out.println("{" + key + ": ");
      value.forEach((key2, value2) -> {
        System.out.println("  " + key2 + ": " + Arrays.toString(value2));
      });
      System.out.println("}");
    });
  }
}