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

package org.logic2j.engine.solver;

/**
 * Created by tettoni on 2017-01-29.
 */
public class SolverContextHolder {

  private static final ThreadLocal<SolverContext> instance = new ThreadLocal<SolverContext>() {
    @Override
    protected SolverContext initialValue() {
      return new SolverContext();
    }
  };


  public static SolverContext register(Solver solver) {
    instance.remove();
    final SolverContext solverContext = instance.get();
    solverContext.solver = solver;
    return solverContext;
  }


  public static Solver getSolver() {
    return instance.get().solver;
  }
}