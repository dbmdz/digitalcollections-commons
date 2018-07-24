package de.digitalcollections.commons.springdata.domain;

import de.digitalcollections.model.api.paging.Order;
import de.digitalcollections.model.api.paging.enums.Direction;
import de.digitalcollections.model.api.paging.enums.NullHandling;
import de.digitalcollections.model.impl.paging.OrderImpl;
import org.springframework.data.domain.Sort;

public class OrderConverter {

  public static Order convert(Sort.Order order) {
    if (order == null) {
      return null;
    }
    Sort.Direction direction = order.getDirection();
    Sort.NullHandling nullHandling = order.getNullHandling();

    Direction dcDirection = convert(direction);
    NullHandling dcNullHandling = convert(nullHandling);
    Order dcOrder = new OrderImpl(dcDirection, order.getProperty(), dcNullHandling);
    return dcOrder;
  }

  private static Direction convert(Sort.Direction direction) {
    return Direction.valueOf(direction.name());
  }

  private static NullHandling convert(Sort.NullHandling nullHandling) {
    return NullHandling.valueOf(nullHandling.name());
  }

  static Sort.Order convert(Order order) {
    if (order == null) {
      return null;
    }
    Direction direction = order.getDirection();
    NullHandling nullHandling = order.getNullHandling();

    Sort.Direction sdDirection = convert(direction);
    Sort.NullHandling sdNullHandling = convert(nullHandling);
    Sort.Order sdOrder = new Sort.Order(sdDirection, order.getProperty(), sdNullHandling);
    return sdOrder;
  }

  private static Sort.Direction convert(Direction direction) {
    return Sort.Direction.valueOf(direction.name());
  }

  private static Sort.NullHandling convert(NullHandling nullHandling) {
    return Sort.NullHandling.valueOf(nullHandling.name());
  }
}
