package org.lobobrowser.security;

import org.lobobrowser.db.tables.Globals;
import org.lobobrowser.db.tables.Permissions;
import org.lobobrowser.db.tables.records.PermissionsRecord;
import org.lobobrowser.store.StorageManager;
import org.cobraparser.ua.UserAgentContext.RequestKind;
import org.javatuples.Pair;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Result;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Optional;

public class DBRequestRuleStore implements RequestRuleStore {
  private final DSLContext userDB;
  private static final PermissionSystem.Permission[] defaultPermissions = new PermissionSystem.Permission[RequestKind.numKinds()];
  static {
    for (int i = 0; i < defaultPermissions.length; i++) {
      defaultPermissions[i] = PermissionSystem.Permission.Undecided;
    }
  }
  private static final Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> defaultPermissionPair = Pair.with(PermissionSystem.Permission.Undecided, defaultPermissions);

  static private DBRequestRuleStore instance = new DBRequestRuleStore();

  public static DBRequestRuleStore getInstance() {
    return instance;
  }

  public DBRequestRuleStore() {
    final StorageManager storageManager = StorageManager.getInstance();
    userDB = storageManager.getDB();
    if (!userDB.fetchOne(Globals.GLOBALS).getPermissionsinitialized()) {
      HelperPrivate.initStore(this);
      userDB.fetchOne(Globals.GLOBALS).setPermissionsinitialized(true);
    }
  }

  private static Condition matchHostsCondition(final String frameHost, final String requestHost) {
    return Permissions.PERMISSIONS.FRAMEHOST.equal(frameHost).and(Permissions.PERMISSIONS.REQUESTHOST.equal(requestHost));
  }

  public Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> getPermissions(final String frameHostPattern, final String requestHost) {
    final Result<PermissionsRecord> permissionRecords = AccessController.doPrivileged((PrivilegedAction<Result<PermissionsRecord>>) () -> {
      return userDB.fetch(Permissions.PERMISSIONS, matchHostsCondition(frameHostPattern, requestHost));
    });

    if (permissionRecords.isEmpty()) {
      return defaultPermissionPair;
    } else {
      final PermissionsRecord existingRecord = permissionRecords.get(0);
      final Integer existingPermissions = existingRecord.getPermissions();
      final Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> permissions = decodeBitMask(existingPermissions);
      return permissions;
    }
  }

  private static Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> decodeBitMask(final Integer existingPermissions) {
    final PermissionSystem.Permission[] resultPermissions = new PermissionSystem.Permission[RequestKind.numKinds()];
    for (int i = 0; i < resultPermissions.length; i++) {
      resultPermissions[i] = decodeBits(existingPermissions, i + 1);
    }
    final Pair<PermissionSystem.Permission, PermissionSystem.Permission[]> resultPair = Pair.with(decodeBits(existingPermissions, 0), resultPermissions);
    return resultPair;
  }

  private static final int BITS_PER_KIND = 2;

  private static PermissionSystem.Permission decodeBits(final Integer existingPermissions, final int i) {
    final int permissionBits = (existingPermissions >> (i * BITS_PER_KIND)) & 0x3;
    if (permissionBits < 2) {
      return PermissionSystem.Permission.Undecided;
    } else {
      return permissionBits == 0x3 ? PermissionSystem.Permission.Allow : PermissionSystem.Permission.Deny;
    }
  }

  public void storePermissions(final String frameHost, final String requestHost, final Optional<RequestKind> kindOpt,
      final PermissionSystem.Permission permission) {
    final Result<PermissionsRecord> permissionRecords = AccessController.doPrivileged((PrivilegedAction<Result<PermissionsRecord>>) () -> {
      return userDB.fetch(Permissions.PERMISSIONS, matchHostsCondition(frameHost, requestHost));
    });

    final Integer permissionMask = makeBitSetMask(kindOpt, permission);

    if (permissionRecords.isEmpty()) {
      final PermissionsRecord newPermissionRecord = new PermissionsRecord(frameHost, requestHost, permissionMask);
      newPermissionRecord.attach(userDB.configuration());
      newPermissionRecord.store();
    } else {
      final PermissionsRecord existingRecord = permissionRecords.get(0);
      final Integer existingPermissions = existingRecord.getPermissions();
      final int newPermissions = (existingPermissions & makeBitBlockMask(kindOpt)) | permissionMask;
      existingRecord.setPermissions(newPermissions);
      existingRecord.store();
    }
  }

  private static Integer makeBitSetMask(final Optional<RequestKind> kindOpt, final PermissionSystem.Permission permission) {
    if (permission.isDecided()) {
      final Integer bitPos = kindOpt.map(k -> k.ordinal() + 1).orElse(0) * BITS_PER_KIND;
      final int bitset = permission == PermissionSystem.Permission.Allow ? 0x3 : 0x2;
      return bitset << bitPos;
    } else {
      return 0;
    }
  }

  private static Integer makeBitBlockMask(final Optional<RequestKind> kindOpt) {
    final Integer bitPos = kindOpt.map(k -> k.ordinal() + 1).orElse(0) * BITS_PER_KIND;
    return ~(0x3 << bitPos);
  }
}