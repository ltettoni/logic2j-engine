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

package org.logic2j.engine.model;

import org.junit.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.logic2j.engine.model.SimpleBinding.bind;
import static org.logic2j.engine.model.SimpleBinding.empty;

public class SimpleBindingTest {

  @Test
  public void emptyBinding() {
    final SimpleBinding<Integer> binding = empty(Integer.class);
    assertThat(binding.size(), is(0L));
  }

  @Test
  public void array() {
    final SimpleBinding<Integer> binding = bind(new Integer[] {1,2,3,4,5,4,3,2,1});
    assertThat(binding.size(), is(9L));
    // Can get several times as an array
    assertThat(binding.toArray().length, is(9));
    assertThat(binding.toArray().length, is(9));
    // But can get also several times as Stream
    assertThat(binding.toStream().count(), is(9L));
    assertThat(binding.toStream().count(), is(9L));
  }

  @Test
  public void setStream1() {
    final SimpleBinding<Long> binding = bind(LongStream.range(1, 1000).boxed());
    assertThat(binding.size(), is(999L));
    // Can get several times as an array
    assertThat(binding.toArray().length, is(999));
    assertThat(binding.toArray().length, is(999));
    // But can get also several times as Stream
    assertThat(binding.toStream().count(), is(999L));
    assertThat(binding.toStream().count(), is(999L));
  }

  @Test
  public void setIterator1() {
    final SimpleBinding<Long> binding = bind(LongStream.range(1, 1000).boxed().collect(Collectors.toList()).iterator());
    assertThat(binding.size(), is(999L));
  }

  @Test
  public void infiniteStream1() {
    final SimpleBinding<Integer> binding = bind(new Random().ints().limit(20000).boxed());
    assertThat(binding.size(), is(20000L));
  }

}