package de.digitalcollections.commons.springdata.domain;

import de.digitalcollections.model.api.paging.Order;
import de.digitalcollections.model.api.paging.Sorting;
import de.digitalcollections.model.impl.paging.SortingImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.domain.Sort;

public class SortConverter {

  public static Sorting convert(Sort sort) {
    if (sort == null) {
      return null;
    }
    List<Order> orders = new ArrayList<>();
    Iterator<Sort.Order> iterator = sort.iterator();
    while (iterator.hasNext()) {
      Sort.Order order = iterator.next();
      orders.add(OrderConverter.convert(order));
    }
    if (orders.isEmpty()) {
      return null;
    }
    Sorting sorting = new SortingImpl(orders);
    return sorting;
  }

  public static Sort convert(Sorting sorting) {
    if (sorting == null) {
      return null;
    }
    List<Sort.Order> orders = new ArrayList<>();
    Iterator<Order> iterator = sorting.iterator();
    while (iterator.hasNext()) {
      Order order = iterator.next();
      orders.add(OrderConverter.convert(order));
    }
    if (orders.isEmpty()) {
      return null;
    }

    Sort sort = Sort.by(orders);
    return sort;
  }
}
