/*
 * logic2j - "Bring Logic to your Java" - Copyright (c) 2017 Laurent.Tettoni@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.logic2j.engine.predicates.impl.io.logging;

import org.logic2j.engine.predicates.impl.FOUniqueSolutionPredicate;
import org.logic2j.engine.unify.UnifyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Logging.
 */
public class Log extends FOUniqueSolutionPredicate {
  private static final Logger logger = LoggerFactory.getLogger(Log.class);
  private final Consumer<String> loggingMethod;

  /**
   * Log the arguments at the level specified.
   *
   * @param level
   * @param argList
   */
  public Log(String level, Object... argList) {
    super("log", argList);
    switch (level.toLowerCase()) {
      case "trace":
        loggingMethod = logger::trace;
        break;
      case "debug":
        loggingMethod = logger::debug;
        break;
      default:
        logger.error(this + " predicate sets level to " + level + " which is unknown - using info instead");
      case "info":
        loggingMethod = logger::info;
        break;
      case "warn":
        loggingMethod = logger::warn;
        break;
      case "error":
        loggingMethod = logger::error;
        break;
    }
  }

  @Override
  public void sideEffect(UnifyContext currentVars) {
    if (getArity() > 0) {
      final String str = Arrays.stream(getArgs()).map(currentVars::reify).map(String::valueOf).collect(Collectors.joining(" "));
      loggingMethod.accept(str);
    } else {
      // No argument to log
      loggingMethod.accept("");
    }
  }

}
