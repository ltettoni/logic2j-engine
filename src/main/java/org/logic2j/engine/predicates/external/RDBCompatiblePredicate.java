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

package org.logic2j.engine.predicates.external;

/**
 * Used to mark predicates that are supported to build SQL queries.
 * Predicates marked with this interface are subject to be directly converted into queries,
 * making inference much more efficient rather than retrieving more data and filtering later on via
 * regular inference.
 */
public interface RDBCompatiblePredicate {

  /**
   * @return Arguments, because most {@link RDBCompatiblePredicate} will actually
   * be {@link org.logic2j.engine.model.Struct}.
   */
  Object[] getArgs();

}
