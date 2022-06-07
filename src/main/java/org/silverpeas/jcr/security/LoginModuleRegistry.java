/*
 * Copyright (c) 2000 - 2022 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "https://www.silverpeas.org/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.silverpeas.jcr.security;

import org.silverpeas.core.SilverpeasRuntimeException;

import javax.jcr.Credentials;
import javax.security.auth.spi.LoginModule;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Registry of all {@link javax.security.auth.spi.LoginModule} instances to use when authenticating
 * a user accessing the JCR. These {@link LoginModule} must be a bridge in the security system
 * between the JCR implementation and Silverpeas.
 * @author mmoquillon
 */
public class LoginModuleRegistry {

  private static final LoginModuleRegistry instance = new LoginModuleRegistry();

  private final Map<Class<? extends Credentials>, List<Supplier<LoginModule>>> registry =
      new HashMap<>();

  private LoginModuleRegistry() {

  }

  /**
   * Gets an instance of the registry.
   * @return a {@link LoginModuleRegistry} object.
   */
  public static LoginModuleRegistry getInstance() {
    return instance;
  }

  /**
   * Adds the specified {@link LoginModule} class as a processor of any instances of the given
   * credentials type.
   * @param credentialsType a concrete type of {@link Credentials}.
   * @param module a {@link LoginModule} class that will be instantiated on demand to perform an
   * authentication operation.
   */
  public void addLoginModule(final Class<? extends Credentials> credentialsType,
      final Class<? extends LoginModule> module) {
    registry.computeIfAbsent(credentialsType, k -> new ArrayList<>())
        .add(() -> spawn(module));
  }

  /**
   * Gets all the {@link LoginModule} objects that support the specified type of credentials.
   * @param credentialsType a concrete type of {@link Credentials}.
   * @return a list of {@link LoginModule} objects that can process the specified type of
   * credentials or an empty list if no one can take in charge this type of credentials.
   */
  public List<LoginModule> getLoginModule(final Class<? extends Credentials> credentialsType) {
    return registry.getOrDefault(credentialsType, Collections.emptyList())
        .stream()
        .map(Supplier::get)
        .collect(Collectors.toList());
  }

  private LoginModule spawn(final Class<? extends LoginModule> clazz) {
    PrivilegedExceptionAction<? extends LoginModule> newLoginModule = () -> {
      Constructor<? extends LoginModule> ctor = clazz.getConstructor();
      int modifier = ctor.getModifiers();
      if (!Modifier.isPublic(modifier)) {
        ctor.trySetAccessible();
      }

      return ctor.newInstance();
    };

    try {
      return AccessController.doPrivileged(newLoginModule);
    } catch (PrivilegedActionException e) {
      throw new SilverpeasRuntimeException(e);
    }
  }
}
