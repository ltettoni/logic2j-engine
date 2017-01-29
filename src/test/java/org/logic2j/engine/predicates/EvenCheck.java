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

package org.logic2j.engine.predicates;

import org.logic2j.engine.predicates.impl.FOPredicateJavaPredicate;

/**
 * Check an Integer is even.
 */
public class EvenCheck extends FOPredicateJavaPredicate<Number> {
  public EvenCheck(Number t1) {
    super("evenCheck", t1, nbr -> nbr.longValue() % 2 == 0);
  }
}