package org.lobobrowser.security;

import org.cobraparser.ua.UserAgentContext.RequestKind;
import org.javatuples.Pair;

import java.util.Optional;

interface RequestRuleStore {
  public Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> getPermissions(final String frameHostPattern, final String requestHost);

  public void storePermissions(final String frameHost, final String requestHost, Optional<RequestKind> kindOpt, PermissionSystem.Permission permission);

  public static RequestRuleStore getStore() {
    // return InMemoryStore.getInstance();
    return DBRequestRuleStore.getInstance();
  }

  static class HelperPrivate {
    static void initStore(final RequestRuleStore store) {
      final Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> permissions = store.getPermissions("*", "");
      assert (!permissions.getValue0().isDecided());
      store.storePermissions("*", "", Optional.empty(), PermissionSystem.Permission.Deny);
      store.storePermissions("*", "", Optional.of(RequestKind.Image), PermissionSystem.Permission.Allow);
      store.storePermissions("*", "", Optional.of(RequestKind.CSS), PermissionSystem.Permission.Allow);
    }
  }
}
