package de.digitalcollections.prosemirror.model.api.content;

import de.digitalcollections.prosemirror.model.api.Content;
import java.util.List;

public interface Text extends Content {

  String getText();

  void setText(String text);

  List<Mark> getMarks();

  void setMarks(List<Mark> marks);

  void addMark(Mark mark);

}
