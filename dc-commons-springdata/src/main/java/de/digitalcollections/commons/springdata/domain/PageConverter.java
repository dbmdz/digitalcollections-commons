package de.digitalcollections.commons.springdata.domain;

import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PageConverter {

  public static Page convert(PageResponse pageResponse) {
    if (pageResponse == null) {
      return null;
    }
    return convert(pageResponse, (PageRequest) pageResponse.getRequest());
  }

  public static Page convert(PageResponse pageResponse, PageRequest pageRequest) {
    if (pageResponse == null) {
      return null;
    }
    Pageable pageable = PageableConverter.convert(pageRequest);
    @SuppressWarnings("unchecked")
    Page page = new PageImpl(pageResponse.getContent(), pageable, pageResponse.getTotalElements());
    return page;
  }
}
