package de.digitalcollections.prosemirror.model.api.contentblocks;

import de.digitalcollections.prosemirror.model.api.Mark;
import de.digitalcollections.prosemirror.model.api.ContentBlock;
import java.util.List;

public interface Text extends ContentBlock {

  String getText();

  void setText(String text);

  List<Mark> getMarks();

  void setMarks(List<Mark> marks);

  void addMark(Mark mark);

}
